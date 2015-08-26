package imm;

import imm.SoundObject;


public class Sequence {
	MainApp _p;
	int sl;
	int ss;
	SoundLibrary soundlibrary;
	SoundObject[][] sequenceSoundObjects;
	
	public Sequence(MainApp parent, int sequenceLength, int sequenceSize){
		this.soundlibrary = null;
		sl = sequenceLength;
		ss = sequenceSize;
		_p = parent;
		sequenceSoundObjects = new SoundObject[sl][ss];
	}
	
	void draw(){
		_p.pg.pushMatrix();
		_p.pg.translate(_p.setupWidth/2f, _p.setupHeight/2f);
		_p.pg.noStroke();
		_p.pg.fill(255,50);;			
		for(int i = 0;i<sl;i++){
			for(int j = 0;j<ss;j++){
				if(sequenceSoundObjects[i][j] != null){
					float x = sequenceSoundObjects[i][j].location.x;
					float y = sequenceSoundObjects[i][j].location.y;
					_p.pg.ellipse(x, y, sequenceSoundObjects[i][j].radius*2f, sequenceSoundObjects[i][j].radius*2f);
				}
			}
		}
		_p.pg.popMatrix();				
	}
	
	void clean(){
		// Clean Sequence based on whether it is being dragged
		for(int i = 0;i<sl;i++){
			for(int j = 0;j<ss;j++){
				if(sequenceSoundObjects[i][j] != null){
					if(sequenceSoundObjects[i][j].isDragged){
						sequenceSoundObjects[i][j] = null;						
					}
				}
			}
		}		
	}
	
	void setSoundLibrary(SoundLibrary soundlib){
		soundlibrary = soundlib;
	}
}