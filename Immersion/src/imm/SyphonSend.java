package imm;

//import codeanticode.syphon.*;
import processing.core.*;


public class SyphonSend {
	MainApp _p;

	
	public SyphonSend(MainApp parent) {
		_p = parent;
		_p.pg = _p.createGraphics(_p.setupWidth, _p.setupHeight,PApplet.P2D);
//		System.loadLibrary("jsyphon");
	}
	
	void sendImage(){
//		_p.loadPixels();
////		pg.beginDraw();
////		pg.background(0);
////		pg.endDraw();
//		pg.loadPixels();
//		PApplet.arrayCopy(_p.pixels, pg.pixels);
//		for(int i=0;i<_p.pixels.length;i++){
//		      pg.pixels[i] = _p.pixels[i];
//		    }
//		pg.updatePixels();
		_p.server.sendImage(_p.pg);    
	}

}
