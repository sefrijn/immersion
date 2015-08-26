package imm;
import processing.core.*;
import imm.Sequence;

// plays the sequence
public class SequencePlayer {
	MainApp _p;
	int currentBeat;
	Sequence seq;

	SequencePlayer(	MainApp parent) {
		_p = parent;
		seq = null;
		currentBeat = 0;
	}
	
	void update(){
		if(currentBeat < _p.beats-1){
			currentBeat++;
		}else{
			currentBeat = 0;
		}
	}
	
	void play(){
		for(int i = 0; i<seq.sequenceSoundObjects[currentBeat].length;i++){
			if(seq.sequenceSoundObjects[currentBeat][i] != null){
				PApplet.println(seq.sequenceSoundObjects[currentBeat][i].soundFile);
				seq.soundlibrary.play(seq.sequenceSoundObjects[currentBeat][i].soundFile);
			}
		}		
	}
	
	void draw(){
		_p.pg.pushMatrix();
		_p.pg.translate(_p.screenCentreX, _p.screenCentreY + _p.sequencerCenterOffsetY);
		_p.pg.fill(255,180);
		_p.pg.noStroke();
		_p.pg.rotate(currentBeat * 2*PApplet.PI / (float)_p.beats - (0.5f *PApplet.PI));
		_p.pg.arc(0f,0f,_p.sequencerSize,_p.sequencerSize, 0f, 2f*PApplet.PI / (float)_p.beats,PApplet.PIE);
		_p.pg.popMatrix();
	}
	
	void setSequence(Sequence seq){
		this.seq = seq;
	}
	
}