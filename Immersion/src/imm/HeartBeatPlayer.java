package imm;
import processing.core.*;
import imm.SoundLibrary;
import ddf.minim.*;


public class HeartBeatPlayer {
	MainApp _p;
	SoundLibrary heartbeatSounds;
	Timer timer;
	
	AudioSample sample;
	int animationCounter;
	int animationLength; // make this fixed? because the audio will also be fixed. But the sample won't be sped up. But maybe it looks more realistic to speed it up.

	PImage img;
	
	HeartBeatPlayer(MainApp parent) {
		_p = parent;
		this.heartbeatSounds = null;
		animationCounter = 0;
		img = _p.loadImage("human.jpg");
		
	}
	
	void play(){
		// play random heart beat
		int index = PApplet.round(_p.random(this.heartbeatSounds.paths.length-1));
		heartbeatSounds.play(index);
	}
	
	void draw(){
//		Start
		_p.pg.pushMatrix();
		_p.pg.translate(_p.screenCentreX, _p.screenCentreY + _p.sequencerCenterOffsetY);
		
//		Circle mask over Sequencer
		_p.pg.fill(0);
		_p.pg.noStroke();		
		_p.pg.ellipse(0, 0, _p.sequencerSizeInner, _p.sequencerSizeInner);
		
//		Human Body Image
		_p.pg.blendMode(PApplet.LIGHTEST);
		_p.pg.image(img, 0, 0, _p.sequencerSizeInner ,_p.sequencerSizeInner);
		_p.pg.blendMode(PApplet.BLEND);
		
//		Heart beating animation
		if(timer.barZero){
			animationCounter = 0;
		}else{
			animationCounter++;
			int interval = timer.interval * _p.beatsPerBar;
			if(animationCounter< (interval/2.0f)){
				_p.pg.fill(255,80,80,150);
				_p.pg.noStroke();
				_p.pg.ellipse(7, -10, PApplet.sin(animationCounter * 2.0f * PApplet.PI / (interval/2.0f)) * 40, PApplet.sin(animationCounter * 2.0f * PApplet.PI / (interval/2.0f)) * 40);
			}
		}
//		Close
		_p.pg.popMatrix();		
	}
	
	void drawNumeric(){
//		_p.pushMatrix();
//		_p.translate(_p.setupWidth/2f, _p.setupHeight/2f);
//		_p.fill(255,80,80);
//		_p.textSize(60);
//		_p.textAlign(PApplet.CENTER, PApplet.CENTER);
//		_p.text(PApplet.round(60f * _p.frameRate / timer.interval), 0, 0);
//		_p.popMatrix();		
	}
	
	void setHeartBeats(SoundLibrary heartbeatSounds){
		this.heartbeatSounds = heartbeatSounds;
	}
	
	void setTimer(Timer timer){
		this.timer = timer;
	}
	
}