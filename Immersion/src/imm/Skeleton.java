package imm;

import processing.core.*;



public class Skeleton {
	MainApp _p;

	PVector[] hands;
	boolean[] handsClosed;
	boolean[] handsChangeState;
	PVector head;
	PVector shoulderCenter;
	public Skeleton(MainApp parent) {
		_p = parent;
		
		hands = new PVector[2];
		hands[0] = new PVector(0,0);
		hands[1] = new PVector(0,0);
		
		handsClosed = new boolean[2];
		handsClosed[0] = false;
		handsClosed[1] = false;
		
		handsChangeState = new boolean[2];
		handsChangeState[0] = false;
		handsChangeState[1] = false;
		
		head = new PVector(0,0);
		shoulderCenter = new PVector(0,0);
	}
	void update(PVector lh,PVector rh, PVector h, PVector sc, boolean lhc, boolean rhc){
		boolean[] handCloseTemp = new boolean[2];
		handCloseTemp[0] = lhc;
		handCloseTemp[1] = rhc;
		
		hands[0] = lh;
		hands[1] = rh;
		head = h;
		shoulderCenter = sc;
		
		for(int hand = 0;hand<hands.length;hand++){
			if(handsClosed[hand] != handCloseTemp[hand]){
				handsChangeState[hand] = true;
			}else{
				handsChangeState[hand] = false;
			}
			handsClosed[hand] = handCloseTemp[hand];
		}   
	}
	void draw(){
		_p.pg.pushMatrix();
		_p.pg.translate(_p.setupWidth/2, _p.setupHeight/2);
		_p.pg.noFill();
//		_p.pg.fill(255);
		_p.pg.stroke(255);
		_p.pg.ellipse(hands[0].x, hands[0].y, 55, 55);
		_p.pg.ellipse(hands[1].x, hands[1].y, 55, 55);
		_p.pg.popMatrix();			
	}
}