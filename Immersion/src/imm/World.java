package imm;

import processing.core.*;
import imm.SoundObject;
import imm.Skeleton;


public class World {
	MainApp _p;	
	
	// watches the skeleton and moves soundobjects positions based on hand grabbed
	SoundObject[] worldSoundObjects;
	private Sequence seq;
	private SoundLibrary sl;
	private ImageLibrary orbs;
	private Skeleton sk;
	Timer t;
	int[] triggerHandEventsTimer;
	int triggerBuffer;
	private int[] worldSoundObjectDragged;
	private int[][] sequenceSoundObjectDragged;

	World(MainApp parent){
		_p = parent;
		
		worldSoundObjects = new SoundObject[_p.worldSoundObjectsSize];
		
		worldSoundObjectDragged = new int[2];
		worldSoundObjectDragged[0] = -1;
		worldSoundObjectDragged[1] = -1;
		
		sequenceSoundObjectDragged = new int[2][2];
		sequenceSoundObjectDragged[0][0] = -1;
		sequenceSoundObjectDragged[0][1] = -1;
		sequenceSoundObjectDragged[1][0] = -1;
		sequenceSoundObjectDragged[1][1] = -1;

		triggerHandEventsTimer = new int[2];
		triggerHandEventsTimer[0] = 0;
		triggerHandEventsTimer[1] = 0;
		triggerBuffer = PApplet.round(_p.frameRateSet/1.0f);
		
		this.sl = null;
		this.seq = null;
		this.sk = null;
		this.t = null;
		this.orbs = null;
	}

	void update(){
		// check if something is grabbed or released
		triggerHandEvents();
		
		// reduce life span of all objects
		for(int i = 0;i<worldSoundObjects.length;i++){
			// check if an object exists
			if(worldSoundObjects[i] != null){
				// check if soundobject should be aging
				if(worldSoundObjects[i].aging){
					// if it's still allowed to live update. if it's dead (true) than kill this object
					if(worldSoundObjects[i].updateLifespan()){
						destroySoundObject(i);
//						worldSoundObjects[i] = null;
					}					
				}
									
			}
		}
		
		// creation of items within intervals
		if(t.barZero){
			int i = 0;
			while(worldSoundObjects[i] != null && i<worldSoundObjects.length){
				i++;
			}
			if(i==worldSoundObjects.length-1){
				// start at first index again when array is full
				i = 0;
				PApplet.println(_p.frameCount+": START AGAIN worldSoundObjects Array to 0");
			}
			createSoundObject(i);
		}
	}
	
	void draw(){
		for(int i = 0;i<worldSoundObjects.length;i++){
			if(worldSoundObjects[i] != null){
				worldSoundObjects[i].draw();
			}
		}
	}

	void triggerHandEvents(){
		for(int hand = 0;hand < sk.hands.length; hand++){
			if(sk.handsChangeState[hand]){
				triggerHandEventsTimer[hand] = triggerBuffer;
				PApplet.println("timer reset");
			}
			if(sk.handsClosed[hand]){
				if(triggerHandEventsTimer[hand] == 1){
					handGrabbed(hand);
				}else if(triggerHandEventsTimer[hand] == 0){
					handDragged(hand);
				}
			}
			if(!sk.handsClosed[hand]){
				if(triggerHandEventsTimer[hand] == 1){
					PApplet.println("release called");
					handReleased(hand);					
				}else if(triggerHandEventsTimer[hand] > 1){
					handDragged(hand);
				}
			}

			if(triggerHandEventsTimer[hand] > 0){
				triggerHandEventsTimer[hand]--;
			}			
			
			sk.handsChangeState[hand] = false;
		} 
	}
	void handGrabbed(int hand){
		// find nearest soundobject
		for(int i = 0;i<worldSoundObjects.length;i++){
			if(worldSoundObjects[i] != null){
				float mag = PVector.sub(worldSoundObjects[i].location,sk.hands[hand]).mag(); 
				if(mag < 40){
					if(worldSoundObjectDragged[hand] != -1){
						if(mag < PVector.sub(worldSoundObjects[worldSoundObjectDragged[hand]].location,sk.hands[hand]).mag()){
							worldSoundObjectDragged[hand] = i;
						}
					}else{
						worldSoundObjectDragged[hand] = i;							
					}
					// Grabbed a orb!
					// Play it to let user know what kind of sound it is
					seq.soundlibrary.play(worldSoundObjects[worldSoundObjectDragged[hand]].soundFile);
				}					
			}
		}
		// Stop aging of grabbed object
		if(worldSoundObjectDragged[hand] != -1){
			worldSoundObjects[worldSoundObjectDragged[hand]].aging = false;
			worldSoundObjects[worldSoundObjectDragged[hand]].isDragged = true;
		}
		// Clean the sequence if orbs have been removed outside of the sequencer
		seq.clean();
	}
	void handDragged(int hand){
		if(worldSoundObjectDragged[hand] != -1){
			worldSoundObjects[worldSoundObjectDragged[hand]].location = sk.hands[hand];
		}
	}
	void handReleased(int hand){		
		if(worldSoundObjectDragged[hand] != -1){
			// Stop playing the file
			seq.soundlibrary.stop(worldSoundObjects[worldSoundObjectDragged[hand]].soundFile);
			
			PVector center = new PVector(0,_p.sequencerCenterOffsetY);
			// Check if the released object is inside the sequencer circle
			if(PVector.sub(worldSoundObjects[worldSoundObjectDragged[hand]].location,center).mag() < _p.sequencerSize / 2.0f){
				// Inside the sequencer
				// Check in which part of the sequencer it is				
				float angle = PApplet.atan2(0f, -0.5f*_p.sequencerSize) - PApplet.atan2(worldSoundObjects[worldSoundObjectDragged[hand]].location.x, worldSoundObjects[worldSoundObjectDragged[hand]].location.y - _p.sequencerCenterOffsetY); 
				int index =  PApplet.floor(angle* _p.beats / (PApplet.PI * 2.0f));
				for(int i = 0;i<seq.sequenceSoundObjects[index].length;i++){
					// Assign the SoundObject to the sequence
					if(seq.sequenceSoundObjects[index][i]== null){
						// The position is empty, so take this space
						seq.sequenceSoundObjects[index][i] = worldSoundObjects[worldSoundObjectDragged[hand]];
						seq.sequenceSoundObjects[index][i].isDragged = false;
						break;
					}else{
						// This beat is full with objects. Notify user to first move one of the spheres
						// Delete the SoundObject from the sequence and let it die immediately
						// Learn by doing and chance this happens is low
					}
				}
			}else{
				// Outside the sequencer. Continue aging
				worldSoundObjects[worldSoundObjectDragged[hand]].aging = true;
				seq.soundlibrary.stop(worldSoundObjects[worldSoundObjectDragged[hand]].soundFile);
			}				
			// Remove dragged object from dragging memory variable
			worldSoundObjectDragged[hand] = -1;
		}
	}		
	
	void createSoundObject(int index){
		int s = MainApp.round(_p.random(sl.paths.length-1));

		// Temporary random orb
		String orb;
//		orb = orbs.paths[MainApp.round(_p.random(orbs.paths.length-1))][0];
//		int s = MainApp.round(_p.random(sl.paths.length-1));
		// In the future this should be based on s. As many Sounds 's' as Orbs
		// Name Orbs exactly same as sounds
		if(s < orbs.paths.length-1){
			orb = orbs.paths[s][0];			
		}else{
			orb = orbs.paths[0][0];
			PApplet.println("Name each Orb file eaxctly as Sounds");
		}
		worldSoundObjects[index] = new SoundObject(_p,s,orb);
	}
	void destroySoundObject(int index){
		worldSoundObjects[index] = null;
	}	
	
	void setSkeleton(Skeleton sk){
		this.sk = sk;
	}

	void setTimer(Timer t){
		this.t = t;
	}
	
	void setOrbs(ImageLibrary orbs){
		this.orbs = orbs;
	}
	
	void setSequence(Sequence seq){
		this.seq = seq;
	}

	void setSoundLibrary(SoundLibrary sl){
		this.sl = sl;
	}
}