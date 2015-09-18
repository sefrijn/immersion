package imm;

import ddf.minim.spi.*;
import ddf.minim.ugens.*;

public class BackgroundAudio {
	MainApp _p;
//	AudioPlayer player;
	FilePlayer filePlayer;
	TickRate rateControl;
	Gain vol;
	
	public BackgroundAudio(MainApp parent) {
		_p = parent;
		AudioRecordingStream myFile = _p.minim.loadFileStream( _p.dataFolder.concat("/Lee_Rosevere_-_03_-_Illuminations.mp3"), // the file to load
                1024,     // the size of the buffer. 1024 is a typical buffer size
                false      // whether to load it totally into memory or not
                          // we say true because the file is short 
              );
		rateControl = new TickRate(1.f);
		rateControl.setInterpolation( true );

		filePlayer = new FilePlayer( myFile );
		filePlayer.loop();
		
		vol = new Gain(-15.0f);

		filePlayer.patch(vol).patch(rateControl).patch(_p.out);
		
//		player.setVolume(0.5f);
	}
	
	void updatePitch(float rate){
		  rateControl.value.setLastValue(rate);
	}
}
