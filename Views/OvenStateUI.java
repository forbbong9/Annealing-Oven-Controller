/*
 * HCI assignment 3, Annealing Oven Controller
 * by @HL.com
 */


//import java.awt.Event;
import java.nio.channels.SelectableChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Stack;

import javax.print.attribute.standard.DateTimeAtCompleted;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.geometry.*;

public class OvenStateUI extends Application{

	Stage window;
	Text nowTime, nowTemp, duringTime, finishTime;
	StackPane highPane, lowleftPane, lowmiddlePane, lowrightPane;
	VBox lowmiddle, lowright, layout;
	HBox lowPane;
	Button selectP, startC, pauseC, stopP;

	double s_X,s_Y,s_Width,s_Height;
	OvenController oven=new OvenController();

	boolean running = false;
	//if endtime is calculated or not
	boolean calculated=false;
	//if assigned pausedTemperature or not
	boolean assigned=false;
	//if firstTime, read paramters
	static boolean firstTime = true;

	private String mdigits_during, hdigits_during, mdigits_now, hdigits_now;

	static ProgramSelectUI programUI;

	int pauseTemperature;

	public static String parameter;

	public static void main(String[] args){
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {

		if(firstTime) {
			parameter = getParameters().getRaw().get(0).toString();
			firstTime = false;
		}

		window=primaryStage;
		window.setTitle("Controlling Oven Temperature");
		
		//Get Screen Size from Monitor, return double; Assign window size base on screen size
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		
		s_X=primaryScreenBounds.getMinX();
		s_Y=primaryScreenBounds.getMinY();
		s_Width=primaryScreenBounds.getHeight();
		s_Height=primaryScreenBounds.getHeight();
		window.setX(s_X);
		window.setY(s_Y);
		window.setWidth(s_Height*16/9);
		window.setHeight(s_Height);
		
		nowTime=new Text();
		nowTime.setFill(Color.LIGHTGRAY);
		nowTime.setFont(Font.font("Verdana", s_Height*0.35));

		int temp=oven.getTemp();
		
		nowTemp=new Text("       ");
		nowTemp.setFill(oven.getColor(temp));
		nowTemp.setFont(Font.font("Verdana", s_Height*0.3));
		
		//Text for Low Middle
		duringTime=new Text("           ");
		duringTime.setFill(oven.getColor(temp));
		duringTime.setFont(Font.font("Verdana", s_Height*0.2));
		
		finishTime=new Text("           ");
		finishTime.setFill(oven.getColor(temp));
		finishTime.setFont(Font.font("Verdana", s_Height*0.2));
		
		//Buttons for Low Right
		Rectangle spaceLable=new Rectangle();
		spaceLable.setStyle("-fx-background-color:black;");
		selectP=new Button("Select Program");
		selectP.setStyle("-fx-width:100px;-fx-background-color:grey;-fx-font-size:20px;-fx-text-fill:white");
		selectP.setOnAction(e -> {
			oven.stop=false;
			oven.done=false;
			programUI=new ProgramSelectUI();
			try {
				programUI.start(window);
				running=false;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		startC=new Button("Start Cycle");
		startC.setStyle("-fx-width:100px;-fx-background-color:grey;-fx-font-size:20px;-fx-text-fill:white");
		startC.setOnAction(e -> {
			if(oven.before&&oven.done){
				assigned=false;
				oven.before=false;
				oven.pause=false;
				oven.cycling=true;
			}
		});
		//toggle button!
		pauseC=new Button("Pause Cycle");
		pauseC.setStyle("-fx-width:100px;-fx-background-color:grey;-fx-font-size:20px;-fx-text-fill:white");
		pauseC.setOnAction(e -> {
			oven.pause=!oven.pause;
			pauseTemperature=oven.ovenF;
			if(oven.pause){
				pauseC.setText(" Resume Cycle ");
			}else pauseC.setText(" Pause Cycle ");
		});
		stopP=new Button("Stop Program");
		stopP.setStyle("-fx-width:100px;-fx-background-color:grey;-fx-font-size:20px;-fx-text-fill:white");
		stopP.setOnAction(e -> {
			oven.pause=false;
			oven.before=false;
			oven.cycling=false;
			oven.after=false;
			oven.stop=true;
			
			nowTemp.setText("       ");
			duringTime.setText("           ");
			finishTime.setText("           ");
		});
		
		//High
		highPane=new StackPane();
		highPane.getChildren().add(nowTime);
		highPane.setStyle("-fx-background-color: #000000;");
		
		//Low Left
		lowleftPane=new StackPane();
		lowleftPane.getChildren().add(nowTemp);
		lowleftPane.setStyle("-fx-background-color: #000000;");
		oven.setBorder(lowleftPane,temp);
		
		//Low Middle Layout
		lowmiddle=new VBox();
		lowmiddle.getChildren().addAll(duringTime, finishTime);
		
		lowmiddlePane=new StackPane();
		lowmiddlePane.getChildren().addAll(lowmiddle);
		lowmiddlePane.setStyle("-fx-background-color: #000000;");
		
		//Low Right Layout selectP,startC,stopC
		lowright=new VBox(40);
		lowright.getChildren().addAll(spaceLable,selectP,startC,pauseC,stopP);
		
		lowrightPane=new StackPane();
		lowrightPane.getChildren().addAll(lowright);
		lowrightPane.setStyle("-fx-background-color: #000000;");

		//VBox=Vertical Box (HBox=Horizontal Box)
		lowPane=new HBox();
		lowPane.getChildren().addAll(lowleftPane,lowmiddlePane,lowrightPane);
		
		layout=new VBox(10);//what the argument in vbox mean?
		layout.getChildren().addAll(highPane,lowPane);
		Scene scene=new Scene(layout);
		scene.setFill(Color.RED);
		
		window.setScene(scene);
		window.show();
		
		runClock();
		
		window.setOnCloseRequest(e -> {
			running=false;
			Platform.exit();
			});
	}
	
	private void runClock(){
		running=true;
		new Thread(){
			public void run(){
				long last=System.nanoTime();
				double delta=0;
				double ns=1000000000.0;
				int minute=0;
				int hour=0;
				
				while(running){
					long now=System.nanoTime();
					delta += (now-last)/ns;
					last=now;
					
						while(delta>=1){
							if(oven.pause){
								if(!assigned){
									pauseTemperature=oven.ovenF;
									assigned=true;
								}
								System.out.println("Pause Temperature:" + pauseTemperature);
								oven.onPause(pauseTemperature);
								
							}else{
							if(oven.before){
								oven.onBefore(oven.getStartT());
								
							}
							if(oven.cycling){
								
								if(minute==59){
									hour=(hour+1) % 24;
								}
								minute=(minute+1) % 60;
								//calculate how long time have past in this cycle
								RefreshDigits(minute,hour);
								//calculate when end
								if(!calculated){
								finishTime.setText(oven.calculateEndTime());
								calculated=true;
								}
								oven.onCycling(minute, hour);
							}if(oven.after){
								oven.onAfter(pauseTemperature);
								//oven.onPause(pauseTemperature);
							}if(oven.stop){
								oven.onAfter(0);
							}
							
							}
							delta--;
							//do every second
							oven.setBorder(lowleftPane,oven.ovenF);
							nowTemp.setText(oven.getTempText());
							nowTemp.setFill(oven.getColor(oven.ovenF));
							duringTime.setFill(oven.getColor(oven.ovenF));
							finishTime.setFill(oven.getColor(oven.ovenF));
							RefreshClock();
						}
					}
				}	
		}.start();
	}
	
	public void RefreshDigits(int minute, int hour){

		DecimalFormat df = new DecimalFormat("00");
		
		hdigits_during=df.format(hour);
		mdigits_during=df.format(minute);
		
		String time=" "+hdigits_during+":"+mdigits_during+" ";
		duringTime.setText(time);
	}
	
	private void RefreshClock(){
		
		DecimalFormat df = new DecimalFormat("00");
		
		Calendar calendar = Calendar.getInstance();
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		hdigits_now=df.format(hours);
		mdigits_now=df.format(minutes);
		
		String times=" "+hdigits_now+":"+mdigits_now+" ";
		nowTime.setText(times);
	}

}
