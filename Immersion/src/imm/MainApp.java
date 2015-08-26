package imm;
import processing.core.*;
import oscP5.*;
import netP5.*;
import java.io.*;

import org.omg.CORBA._PolicyStub;

import imm.Skeleton;
import ddf.minim.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;
import codeanticode.syphon.*;

@SuppressWarnings("serial")
public class MainApp extends PApplet{
	// Configuration
	// Size of image sent to Madmapper
	int setupWidth = 1000;
	int setupHeight = 1000;
	// Size of java preview image
	boolean renderView = true;
	int viewWidth = 500;
	int viewHeight = 500;
	int beats = 16;
	int maxSoundsPerBeat = 4;
	int worldSoundObjectsSize = 40;
	int defaultInterval = 12;
	int beatsLifespan = 12;
	int beatsPerBar = 1;
	int frameRateSet = 30;
	
	// Visual options
	float screenCentreX = setupWidth * 0.5f;
	float screenCentreY = setupHeight * 0.5f;
	float orbSize = 30;
	float sequencerSize = setupHeight * 0.20f;
	float sequencerSizeInner = sequencerSize * 0.6f;
	float sequencerCenterOffsetY = setupHeight * 0.32f;
	
	// Kinect reach options
	float offsetOrbCenter = setupHeight*0.02f; // from top and side of dome
	float offsetOrbRadius = setupHeight*0.02f; // from bottom of dome

	File dir;
	File [] files;

	OscP5 oscP5;
	NetAddress myRemoteLocation;
	
	Minim minim;
	AudioOutput out;
		
	Sequence sequence;
	SequencePlayer seqPlayer;
	SequencerBackground seqBG;
	
	HeartBeatPlayer hbPlayer;
	
	Timer timer;

	BackgroundAudio bgAudio;
	SoundLibrary musicSounds;
	SoundLibrary heartbeatSounds;
	ImageLibrary orbs;

	Skeleton skeleton;
	World world;
	
	SyphonSend sender;
	SyphonServer server;
	
	PGraphics pg;

	public void setup(){
		size(viewWidth,viewHeight, P2D);
		frameRate(frameRateSet);
//		smooth();
		
//		OSC
		oscP5 = new OscP5(this,3001);
		myRemoteLocation = new NetAddress("192.168.0.102",3000);

//		SOUND
		minim = new Minim(this);
		out   = minim.getLineOut();		
		musicSounds = new SoundLibrary(this,"/audio",0f);
		heartbeatSounds = new SoundLibrary(this,"/heartbeats",-20f);
		bgAudio = new BackgroundAudio(this);
		
//		IMAGES
		orbs = new ImageLibrary(this, "/orbs");
		
//		SKELETON
		skeleton = new Skeleton(this);
		
//		SEQUENCE
		sequence = new Sequence(this, beats, maxSoundsPerBeat);
		sequence.setSoundLibrary(musicSounds);
		seqBG = new SequencerBackground(this);
		seqPlayer = new SequencePlayer(this);
		seqPlayer.setSequence(sequence);
		
//		TIMER
		timer = new Timer(this,defaultInterval);
		
//		HEARTBEAT
		hbPlayer = new HeartBeatPlayer(this);
		hbPlayer.setHeartBeats(heartbeatSounds);
		hbPlayer.setTimer(timer);
				
//		WORLD CONTROLLER
		world = new World(this);
		world.setSoundLibrary(musicSounds);
		world.setOrbs(orbs);
		world.setSkeleton(skeleton);
		world.setTimer(timer);
		world.setSequence(sequence);
		
//		SYPHON COMMUNICATION
		server = new SyphonServer(this, "Processing Syphon");		
		sender = new SyphonSend(this);
	}


	public void draw(){
//		UPDATE EVERYTHING
		timer.update();
		world.update();
		if(timer.beatZero){
			seqPlayer.update();
			seqPlayer.play();
		}
		if(timer.barZero){
			hbPlayer.play();			
		}

//		START DRAWING
		pg.beginDraw();
		pg.background(0);
//		DRAW ALL OBJECTS
		seqBG.draw();
		seqPlayer.draw();
		hbPlayer.draw();
		world.draw();
		sequence.draw();
		skeleton.draw();
//		CIRCLE MASK
		drawMask();
//		CLOSE
		pg.endDraw();
		
//		DRAW ON SCREEN FOR DEBUGGING
		imageMode(CORNER);
		if(renderView) image(pg, 0, 0, width, height);
		text(frameRate, 20, 20);
		
//		SEND TO SYPHON
		sender.sendImage();
	}

	
	public void stop(){
		minim.stop();
		super.stop();
	}
	
	void oscEvent(OscMessage theOscMessage){
		float kinectX = 480;
		float kinectY = 360;
		float[] kinectOffset = new float[4];
		kinectOffset[0] = 140;
		kinectOffset[1] = 140;
		kinectOffset[2] = 140;
		kinectOffset[3] = 80;
		float kinectActiveX = kinectX-kinectOffset[0]-kinectOffset[1];
		float kinectActiveY = kinectY-kinectOffset[2]-kinectOffset[3];

		
		if(theOscMessage.checkAddrPattern("/skeleton")==true) {
			// skeleton tracking updated by windows with kinect
			if(theOscMessage.checkTypetag("ffffffffss")) {
				PVector lh = new PVector(theOscMessage.get(0).floatValue(),theOscMessage.get(1).floatValue());
				PVector rh = new PVector(theOscMessage.get(2).floatValue(),theOscMessage.get(3).floatValue());
				PVector h = new PVector(theOscMessage.get(4).floatValue(),theOscMessage.get(5).floatValue());
				PVector sc = new PVector(theOscMessage.get(6).floatValue(),theOscMessage.get(7).floatValue());
				lh = clamp(lh,kinectOffset[0],kinectX-kinectOffset[1],kinectOffset[2],kinectY-kinectOffset[3]);
				rh = clamp(rh,kinectOffset[0],kinectX-kinectOffset[1],kinectOffset[2],kinectY-kinectOffset[3]);
//				PApplet.println("Links X: "+lh.x +", Y: "+lh.y);
//				PApplet.println(kinectActiveX);
				lh = warpSkeleton(lh,kinectActiveX,kinectActiveY);
				rh = warpSkeleton(rh,kinectActiveX,kinectActiveY);
				lh = new PVector(
						map(lh.x, 0, kinectActiveX, - screenCentreX, screenCentreX), 
						map(lh.y, 0, kinectActiveY, 0, screenCentreY)
						);
				rh = new PVector(
						map(rh.x, 0, kinectActiveX, - screenCentreX, screenCentreX), 
						map(rh.y, 0, kinectActiveY, 0, screenCentreY)
						);
//						map(theOscMessage.get(2).floatValue(), 0+skeletonOffset, kinectX-skeletonOffset, - screenCentreX, screenCentreX), 
//						map(theOscMessage.get(3).floatValue(), 0+skeletonOffset, kinectY-skeletonOffset, 0, screenCentreY)
//						);
//				PVector h = new PVector(
//						map(theOscMessage.get(4).floatValue(), 0+skeletonOffset, kinectX-skeletonOffset, - screenCentreX, screenCentreX), 
//						map(theOscMessage.get(5).floatValue(), 0+skeletonOffset, kinectY-skeletonOffset, 0, screenCentreY)
//						);
//				PVector sc = new PVector(
//						map(theOscMessage.get(6).floatValue(), 0+skeletonOffset, kinectX-skeletonOffset, - screenCentreX, screenCentreX), 
//						map(theOscMessage.get(7).floatValue(), 0+skeletonOffset, kinectY-skeletonOffset, 0, screenCentreY)
//						);						
				boolean lhc =  Boolean.parseBoolean(theOscMessage.get(8).stringValue());
				boolean rhc =  Boolean.parseBoolean(theOscMessage.get(9).stringValue());
				skeleton.update(lh,rh,h,sc,lhc,rhc);
			}  
		}  
		if(theOscMessage.checkAddrPattern("/heartbeat")==true) {
			// update BPM value, change the speed of the sequencer
			PApplet.println("BPM: "+theOscMessage.typetag());
			if(theOscMessage.checkTypetag("ss")) {
				// Convert the BPM value to Interval timing
				int bpm = Integer.parseInt(theOscMessage.get(1).stringValue()); 
				updatePitch(bpm);
				timer.interval = intervalCalculator(bpm);
				PApplet.println("Hartslag interval: "+timer.interval);
			}
		}
//		if(theOscMessage.checkAddrPattern("/heartbeat")==true) {
			// an exact heartbeat signal is sent 
//		}
	}
		
	PVector clamp(PVector hand, float xmin, float xmax, float ymin, float ymax){
		if(hand.x < xmin) hand.x = xmin;
		if(hand.x > xmax) hand.x = xmax;
		if(hand.y < ymin) hand.y = ymin;
		if(hand.y > ymax) hand.y = ymax;
		hand.x = hand.x - xmin;
		hand.y = hand.y - ymin;
		return hand;
	}
	
	PVector warpSkeleton(PVector hand, float kx, float ky){
		float angle = PI + PI * hand.x / kx;
		float x = cos(angle) * hand.y * (kx/ky) * 0.5f + kx * 0.5f;
		float y = sin(angle-PI)*hand.y;
		hand.x = x;
		hand.y = y;
		return hand;
	}
	
	int intervalCalculator(int bpm){
		int interv = round( 60f * (float)frameRateSet / ((float)bpm * (float)beatsPerBar));
		return interv;
	}
	
	int heartBeatCalculator(int interv){
		int bpm = round( 60f * (float)frameRateSet / ((float)beatsPerBar * (float)interv));
		return bpm;
	}
	
	void updatePitch(int bpm){
		int sampleBPM = 97;
		int min = sampleBPM-40;
		int max = sampleBPM+40;
		float rate = map(bpm, min, max, min/(float)sampleBPM, max/(float)sampleBPM);	
		musicSounds.updatePitch(rate);
		bgAudio.updatePitch(rate);
	}	
	
	void drawMask(){
		  pg.fill(0);
		  pg.noStroke();
		  pg.beginShape();
		  pg.vertex(0, 0);
		  pg.vertex(setupWidth,0);
		  pg.vertex(setupWidth,setupHeight);
		  pg.vertex(0,setupHeight);
		  pg.bezierVertex(0,setupHeight,0,0,0,setupHeight*0.5f);
		  pg.bezierVertex(0,setupHeight*0.25f,setupWidth*0.25f,0,setupWidth*0.5f, 0);
		  pg.bezierVertex(setupWidth*0.75f,0,setupWidth,setupHeight*0.25f,setupWidth, setupHeight*0.5f);
		  pg.bezierVertex(setupWidth,setupHeight*0.75f,setupWidth*0.75f,setupHeight,setupWidth*0.5f, setupHeight);
		  pg.bezierVertex(setupWidth*0.25f,setupHeight,0,setupHeight*0.75f,0, setupHeight*0.5f);
		  pg.endShape();
		
	}

}