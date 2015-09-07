package imm;

import processing.core.*;

// draws Non-animated background of everything (galaxy + sequencer grid)
public class SequencerBackground {
	MainApp _p;
	PImage img;
	SequencerBackground(MainApp parent){
		_p = parent;
		img = _p.loadImage("bg.jpg");
	}
	void draw(){
//		Start drawing
		_p.pg.pushMatrix();
		_p.pg.translate(_p.screenCentreX, _p.screenCentreY);
//		Background image
		_p.pg.imageMode(PApplet.CENTER);
		_p.pg.image(img, 0, 0, _p.setupWidth, _p.setupHeight);
//		Sequencer chart
		_p.pg.translate(0, _p.sequencerCenterOffsetY);
		_p.pg.fill(30,0,50);
		_p.pg.stroke(0);
		for(int i = 0;i<_p.beats;i++){
			_p.pg.arc(0f,0f,_p.sequencerSize,_p.sequencerSize, 0f, 2f*PApplet.PI / (float)_p.beats,PApplet.PIE);
			_p.pg.rotate(2*PApplet.PI / (float)_p.beats);
		}
//		Close
		_p.pg.popMatrix();
	}
}