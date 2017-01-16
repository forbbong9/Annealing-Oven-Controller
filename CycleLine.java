/*
 * This class is for storing each line of cycle.
 */

public class CycleLine {
	private int startT, endT, duration;
	
	public CycleLine(String s, String e, String d){
		this.setStartT(Integer.parseInt(s));
		this.setEndT(Integer.parseInt(e));
		this.setDuration(Integer.parseInt(d));
	}
	
	public CycleLine(int s, int e, int d){
		this.setStartT(s);
		this.setEndT(e);
		this.setDuration(d);
	}

	public CycleLine() {
		// TODO Auto-generated constructor stub
	}

	//get and set startT
	public int getStartT() {
		return startT;
	}

	public void setStartT(String string) {
		this.startT = Integer.parseInt(string);
	}
	
	public void setStartT(int s){
		this.startT=s;
	}

	//get and set endT
	public int getEndT() {
		return endT;
	}

	public void setEndT(int endT) {
		this.endT = endT;
	}
	
	public void setEndT(String string) {
		this.endT = Integer.parseInt(string);
	}
	
	//get and set duration
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public void setDuration(String string) {
		this.duration = Integer.parseInt(string);
	}
}
