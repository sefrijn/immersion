package imm;

import processing.core.*;
//import ddf.minim.*;

public class SoundObject {
	MainApp _p;
	
	boolean isDragged;
	int soundFile;
	int lifespan;
	String orb;
	PImage img;
	
	int radius;
	boolean aging;
	PVector location;
	public SoundObject(MainApp parent, int s, String orb){
        _p = parent;
        boolean valid = false;
		while(!valid){
			float r = _p.random(_p.screenCentreY - _p.offsetOrbRadius);
			float angle = _p.random( PApplet.PI );
			float x = PApplet.cos(angle)*r;
			float y = PApplet.sin(angle)*r;
			location = new PVector(x,y);
			PVector center = new PVector(0,_p.sequencerCenterOffsetY);
			if(PVector.sub(location,center).mag() > _p.sequencerSize / 2.0f 
					&& location.y > _p.offsetOrbCenter){
				valid = true;
			}
		}
//        float r = _p.random(_p.setupHeight/4f)+_p.setupHeight/4f;
//        float angle = _p.random( 2 * PApplet.PI );
//        float x = PApplet.cos(angle)*r;
//        float y = PApplet.sin(angle)*r;
//		location = new PVector(x,y);
		isDragged = false;
		soundFile = s;
		this.orb = orb;
		img = _p.loadImage(this.orb);
		aging = true;
		radius = 25;
		lifespan = _p.defaultInterval*_p.beatsLifespan;
	}
	void draw(){
//		START
		_p.pg.pushMatrix();
		_p.pg.translate(_p.screenCentreX, _p.screenCentreY);

//		DRAW IMAGE CENTERED
		_p.pg.fill(0);
		_p.pg.ellipse(location.x, location.y, radius*1.8f, radius*1.8f);
		_p.pg.blendMode(PApplet.LIGHTEST);		
		_p.pg.imageMode(PApplet.CENTER);
		_p.pg.image(img,location.x,location.y, radius*2, radius*2);
		_p.pg.blendMode(PApplet.BLEND); // reset blendmode to default		
//		CLOSE
		_p.pg.popMatrix();
	}
	boolean updateLifespan(){
		if(lifespan>0){
			lifespan--;				
		}else if(lifespan==0){
			return true;
		}
		return false;
	}
}