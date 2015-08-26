package imm;
import processing.core.*;

public class HeartBeatFractal {
	MainApp _p;
	PGraphics pg;

	int n = 50;
	float r = 0.1f;
	float w, h;

	public HeartBeatFractal(MainApp parent) {
		_p = parent;

		w = _p.width/2f; 
		h = _p.height/2f;
	}

	void draw() {
		_p.pushMatrix();
		_p.translate(w,h);
		_p.scale(w);
		_p.strokeWeight(1f/ w);
		_p.stroke(255, 30);
		for(int y=1; y<n; y++) for(int x=1; x<n; x++) {
			line(f(x, y), f(x-1, y));
			line(f(x, y), f(x, y-1));
		}
		_p.popMatrix();
	}

	void line(float[] p1, float[] p2) {
		_p.line(p1[0], p1[1], p2[0], p2[1]);
	}

	float[] f(int ix, int iy) {
		float x = PApplet.map(ix, 0, n-1, -1, 1) * _p.random(1-r, 1+r);
		float y = PApplet.map(iy, 0, n-1, -1, 1) * _p.random(1-r, 1+r);
		float a = (_p.mouseX*4f-w)/(w+h);
		float b = (_p.mouseY*4f-h)/(w+h);
		float d = x*x + y*y; 
		return new float[] { (x*a + y*b)/d, (x*b - y*a)/d }; 
	} 




}
