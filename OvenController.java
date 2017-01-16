import java.awt.Window;
import java.text.DecimalFormat;
import java.util.Calendar;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class OvenController {

	//if the oven is heating or not
	boolean heating=false;
	//if in the before-start period--to heatup/cooldown untill the aim temperature
	boolean before=false;
	//if stop the oven or not
	boolean after=false;
	//if start cycle or not
	boolean cycling=false;
	//if pause cycle or not
	boolean pause=false;
	//if stop program or not
	boolean stop=false;
	//if get start temperature or not
	boolean done=false;
	
	final static int HSpeed=8;
	final static int CSpeed=4;
	
	//to record oven temperature at every second
	static int ovenF=0;
//	int startF,endF;
	static Cycle cycle=new Cycle();
	
	public OvenController() {
		
	}
	
	public void setOvenController(Cycle cycle) {
		cycle.clone(this.cycle, cycle);
		if(this.cycle!=null){
			System.out.println("Cloned!");
		}
	}
	
	//return format of show temperature
	public String getTempText(){
		
		DecimalFormat df = new DecimalFormat("000");
		String temp;
		
		if(ovenF<1000){
			temp=df.format(ovenF);
			temp+="°";
		}
		else{	
			temp=Integer.toString(ovenF)+"°";
		}
		return temp;
	}
	
	public int getTemp(){
		return ovenF;
	}
	
	//set Color based on different temperature
	public Color getColor(int nowTemp){
		if(nowTemp>900)	
			return Color.rgb(255, 0, 0);
		else if(nowTemp>600)
			return Color.rgb(255, 165, 0);
		else if (nowTemp>300) 
			return Color.rgb(255, 255, 0);
		else
			return Color.rgb(0, 255, 0);
	}
	
	//set Border when heating
	public void setBorder(StackPane lowleftPane, int nowTemp) {
		if(heating){
		if(nowTemp>900)	
			lowleftPane.setStyle("-fx-background-color: #000000;-fx-border-color: red;-fx-border-width: 10;");
		else if(nowTemp>600)
			lowleftPane.setStyle("-fx-background-color: #000000;-fx-border-color: orange;-fx-border-width: 10;");
		else if (nowTemp>300) 
			lowleftPane.setStyle("-fx-background-color: #000000;-fx-border-color: yellow;-fx-border-width: 10;");
		else
			lowleftPane.setStyle("-fx-background-color: #000000;-fx-border-color: lime;-fx-border-width: 10;");
		}
		else lowleftPane.setStyle("-fx-background-color: #000000;");
	}
	
	public void Heating(){
		heating=true;
		ovenF+=HSpeed;
	}
	
	public void Cooling(){
		heating=false;
		if(ovenF>0)
			ovenF-=CSpeed;
		if(ovenF<0)
			ovenF=0;
	}
	//when before==true, do
	public void onBefore(int startT){
		if(before){
		if(startT-10>ovenF) Heating();
		else if(startT+10<ovenF) Cooling();
		else{
//			before=false;
			done=true;
			pause=true;
		}
		}
	}
	
	public int getStartT(){
		return cycle.getCycleNode().getFirst().getStartT();
	}
	
	//when cycling==true, do
	boolean get=false;
	int[] indexTable;
	
	public void onCycling(int minute, int hour){
		if(cycling){
		int count=cycle.getSize();
//		int duration;
		
		if(!get){
		getIndexTable();
		}
		int index=indexTable[hour];
		int lineStartT=cycle.getCycleNode().get(index).getStartT();
		int lineEndT=cycle.getCycleNode().get(index).getEndT();
		int during=cycle.getCycleNode().get(index).getDuration();
		//calculate aim temperature of each second
		int aim=getAimTemp(minute, lineStartT, lineEndT,during);
		keepTemperature(aim);
		}
	}
	
	//build a table of hour corresponding to step in cycle 
	public void getIndexTable(){
		
		int length=cycle.getDurationTime();
		indexTable=new int[length];
		int i=0;
		int step=0;
		int temp;
		while(i<length){
			temp=cycle.getCycleNode().get(step).getDuration();
			while(temp>0){
				indexTable[i]=step;
				temp--;
				i++;
			}step++;
		}
		get=true;
	}
	
	//get currently what temperate should be
	public int getAimTemp(int minute, int lineStartT, int lineEndT,int during){
		int aim;
		double percentage=minute/(60.0*during);
		int changeT=lineEndT-lineStartT;
		aim=(int) (percentage * Math.abs(changeT)+lineStartT);
		System.out.println("Aim terperature: "+aim);
		return aim;
	}
	
	//when after==true, do
	public void onAfter(int endT){
		heating=false;
		Cooling();
	}
	
	//when pause==true, do
	public void onPause(int pauseT){
		keepTemperature(pauseT);
	}
	
	public String calculateEndTime(){
		
		DecimalFormat df = new DecimalFormat("00");
		
		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		int duration=cycle.getDurationTime();
		minutes+=duration;
		if(minutes>59){
			hours+=minutes/60;
			if(hours>23)
				hours%=24;
			minutes%=60;
		}
		String hour=df.format(hours);
		String minute=df.format(minutes);
		String endTime=(" "+hour+":"+minute+" ");
		
		return endTime;
	}
	
	public void keepTemperature(int keepF){

		if(Math.abs(ovenF-keepF)<5){
			heating=false;
			Cooling();
		}else{
			if(ovenF<keepF) 
				heating=true;
				Heating();
		}
	}
	

}
