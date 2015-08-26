package imm;

import processing.core.*;

import java.io.File;
import java.io.FilenameFilter;

import ddf.minim.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

public class SoundLibrary {
	MainApp _p;
	
	String[][] paths;
	Sampler[] samples;
	Gain[] vol;	
	TickRate[] rateControl;
	String folder;
	SoundLibrary(MainApp parent, String f, float volume){
        _p = parent;
		
		folder = f;
		_p.dir= new File(_p.dataPath("") + f);
		_p.files= _p.dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		paths = new String[_p.files.length][2];
		samples = new Sampler[_p.files.length];
		rateControl = new TickRate[_p.files.length];
		vol = new Gain[_p.files.length];

//		PApplet.println(_p.dataPath(""));
		for(int i = 0; i<paths.length;i++){
			paths[i][0] = _p.files[i].getAbsolutePath();
			String name = _p.files[i].getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0) {
				name = name.substring(0, pos);
			}
			paths[i][1] = name;
			PApplet.println(i + ": "+paths[i][0]);
		}
		
		for(int i = 0;i<paths.length;i++){
			rateControl[i] = new TickRate(1.f);
			rateControl[i].setInterpolation( true );
			samples[i] = new Sampler( paths[i][0], 4, _p.minim);
			vol[i] = new Gain(volume);
			samples[i].patch(vol[i]).patch(rateControl[i]).patch(_p.out);
		}
	}
	
	void play(int index){
		samples[index].trigger();
	}
	
	void stop(int index){
		samples[index].stop();
	}
	
	void updatePitch(float rate){
		for(int i = 0;i<paths.length;i++){
		  rateControl[i].value.setLastValue(rate);
		}
	}

}