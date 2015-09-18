package imm;

import processing.core.*;

import java.io.File;
import java.io.FilenameFilter;

public class ImageLibrary {
	MainApp _p;

	PImage[] images;
	
	String[][] paths;
	String folder;
	ImageLibrary(MainApp parent, String f){
        _p = parent;
		
		folder = f;
//		_p.dir= new File(_p.dataPath("") + f);
		_p.dir= new File(_p.dataFolder.concat(f));		
		_p.files= _p.dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		paths = new String[_p.files.length][2];
		images = new PImage[paths.length];
		PApplet.println(_p.dataPath(""));
		for(int i = 0; i<paths.length;i++){
			paths[i][0] = _p.files[i].getAbsolutePath();
			String name = _p.files[i].getName();
			int pos = name.lastIndexOf(".");
			if (pos > 0) {
				name = name.substring(0, pos);
			}
			paths[i][1] = name;
			images[i] = _p.loadImage(paths[i][0]);
			PApplet.println(paths[i][0]);
		}
	}
}
