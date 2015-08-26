package imm;


public class Timer{
	MainApp _p;

	int interval;
	int beatCounter;
	int barCounter;
	boolean beatZero;
	boolean barZero;
	Timer(MainApp parent, int iv){
        _p = parent;
		interval = iv;
		beatCounter = interval;
		barCounter = 0;
	}
	void update(){
		if(beatCounter <= 0){
			beatCounter = interval;
			barCounter--;
			beatZero = true;
		}else{
			beatZero = false;
		}
		if(barCounter <= 0){
			barCounter = _p.beatsPerBar;
			barZero = true;
		}else{
			barZero = false;
		}
		beatCounter --;
//		return beatZero;
	}
}