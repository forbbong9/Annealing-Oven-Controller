import java.nio.channels.NonWritableChannelException;
import java.util.LinkedList;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import com.sun.glass.ui.Size;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProgramBuildUI {

	Stage window;
	TextField fieldName;
	Button saveButton,cancelButton;
	
	double s_X,s_Y,s_Width,s_Height;
	VBox layout;
	LinkedList<HBox> lineBox=new LinkedList<HBox>();
	LinkedList<LineComponent> lineCombo=new LinkedList<LineComponent>();
	LinkedList<CycleLine> linelist=new LinkedList<CycleLine>();
	LinkedList<Cycle> cycleList=new LinkedList<Cycle>();
	
	String name;
	
	//check textfield validation
	boolean validate=false;
	//save textfield to linelist
	boolean saved=false;
	//has program name
	boolean hasName=false;
	//successfully save to xml
	boolean success=false;
	//for when do add LineComponent
	boolean modifying=false;
	//for deleting a cycle node or not
	boolean changing=false;
	
	public void start(Stage primaryStage) throws Exception{
		
		window=primaryStage;
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		
		s_X=primaryScreenBounds.getMinX();
		s_Y=primaryScreenBounds.getMinY();
		s_Width=primaryScreenBounds.getHeight();
		s_Height=primaryScreenBounds.getHeight();
		window.setX(s_X);
		window.setY(s_Y);
		window.setWidth(s_Height*16/9);
		window.setHeight(s_Height);
		
		Label labelName=new Label("Defining Name");
		labelName.setStyle("-fx-font-size:20px");
		fieldName=new javafx.scene.control.TextField();
		fieldName.setStyle("-fx-font-size:20px;-fx-width:100px");
		fieldName.setMaxWidth(200);
		
		saveButton=new Button(" Save ");
		saveButton.setStyle("-fx-font-size:20px");
		saveButton.setOnAction(e ->{
			
			if(lineCombo==null){
				Alert alert=new Alert(AlertType.WARNING);
				alert.setTitle("Nothing in line!");
				alert.setContentText("There is no line for this cycle >0<");
				alert.show();
			}else{
				
			//save to linelist
			saveList();
			//save to xml, to be defined
			if(saved){
				
				saveToXML();
				if(success){
					ProgramSelectUI programUI=new ProgramSelectUI();
					try {
						
						programUI.start(window);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			}
		});
		
		cancelButton=new Button(" Cancel ");
		cancelButton.setStyle("-fx-font-size:20px");
		cancelButton.setOnAction(e ->{
			//to be defined
			ProgramSelectUI programUI=new ProgramSelectUI();
			try {
				programUI.start(window);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		HBox buttons=new HBox(50);
		buttons.getChildren().addAll(saveButton,cancelButton);
		buttons.setAlignment(Pos.CENTER);
		
		if(!modifying){
		LineComponent lineComponent=new LineComponent();
		lineCombo.addFirst(lineComponent);
		lineBox.addFirst(lineComponent.hbox);
		}
		
		layout=new VBox(10);
		layout.getChildren().addAll(labelName,fieldName,buttons);
		
		layout.getChildren().addAll(lineBox);
		
		if(modifying){
		fieldName.setText(name);
		fieldName.setDisable(true);
		}
		layout.setAlignment(Pos.TOP_CENTER);
		Scene scene=new Scene(layout);
		
		window.setTitle("Controlling Oven Temperature");
		window.setScene(scene);
		
		modifying=false;
		
		window.setOnCloseRequest(e -> {
			Platform.exit();
			});
	}
	
	private int DeleteByName(String name){
		int count=cycleList.size();
		for(int i=0;i<count;i++){
			if(cycleList.get(i).getCycleName().equals(name)){
				cycleList.remove(i);
				return 0;
			}
		}
		
		return 0;
	}
	private int saveToXML() {
		// TODO Auto-generated method stub
		hasName=false;
		success=false;
		//Name cannot be none
		String name=fieldName.getText();
		if(name.equals("")){
			Alert alert=new Alert(AlertType.WARNING);
			alert.setTitle("Losing name!");
			alert.setContentText("There is no name for this cycle >0<");
			alert.show();
			return 0;
		}else	
			hasName=true;
		
		//when has name, and we already at savelist, then we start storing it into xml file
		if(hasName){
			//create a Cycle for this linelist+name-->cycle
			Cycle cycle=new Cycle();
			cycle.setCycleName(name);
			cycle.setCycleNode(linelist);
//			int count=linelist.size();
//			for(int i=0;i<count;i++){
//				cycle.setCycleNode(linelist.get(i));
//			}
			//Read old xml
			ProcessXML xml=new ProcessXML();
			
			cycleList=(LinkedList<Cycle>) xml.ReadXML().clone();

			if(changing){
				DeleteByName(name);
				changing=false;
				}

			cycleList.add(cycle);
			//Write new xml
			xml.WriteXML(cycleList);
			success=true;
		}
		return 0;
	}

	private int saveList() {
		// TODO Auto-generated method stub
		validate=false;
		saved=false;
		int count=lineCombo.size();
		String s;
		String e;
		String d;
		linelist.clear();
		//first check if all the 
		for(int i=0;i<count;i++){
			s=lineCombo.get(i).startTemp.getText();
			e=lineCombo.get(i).endTemp.getText();
			d=lineCombo.get(i).duration.getText();
			checkValidation(s, e, d);
		}
		//then check if startT=endT
		if(validate){
		validate=false;
		checkESequals();
		}
		//if validate, start save textfiled value in linelist
		if(validate){
			for(int i=0;i<count;i++){
				CycleLine line=new CycleLine();
				s=lineCombo.get(i).startTemp.getText();
				e=lineCombo.get(i).endTemp.getText();
				d=lineCombo.get(i).duration.getText();
				//correct
				
				line.setStartT(s);
				line.setEndT(e);
				line.setDuration(d);
				//correct
//				System.out.println("start "+line.getStartT()+" end "+line.getEndT()+" duration "+line.getDuration());
				
				linelist.add(line);
			}
		
		//print out demo
				System.out.println("Now the linelist has:");
				int cc=linelist.size();
				for(int i=0;i<cc;i++){
					int lstart=linelist.get(i).getStartT();
					int lend=linelist.get(i).getEndT();
					int lduration=linelist.get(i).getDuration();
					System.out.println("No "+i+" Start: "+lstart+" End: "+lend+" Duration: "+lduration);
				}
				System.out.println("---------------------------");
		
		saved=true;
		}
		return 0;
	}
	
	public int checkESequals(){
		int count=lineBox.size();
		for(int i=1;i<count;i++){
			String start=lineCombo.get(i).startTemp.getText();
			String end=lineCombo.get(i-1).endTemp.getText();
			if(!start.equals(end)){
				Alert alert=new Alert(AlertType.INFORMATION);
				alert.setTitle("Unequal Information");
				alert.setContentText("The start temperature of each line should equals to it's previous end temperature if has :)");
				alert.show();
				return 0;
			}
		}
				
		validate=true;		
		return 0;
	}
	
	public int checkValidation(String s, String e, String d){
		
		if(s.equals("")||e.equals("")||d.equals("")){
			Alert alert=new Alert(AlertType.INFORMATION);
			alert.setTitle("Null Information");
			alert.setContentText("Please fill all the blanks :)");
			alert.show();
			return 0;
		}else if(!s.matches("[0-9]+")||!e.matches("[0-9]+")||!d.matches("[0-9]+")){
			Alert alert=new Alert(AlertType.INFORMATION);
			alert.setTitle("Uncorrect format");
			alert.setContentText("Please fill with only digits :)");
			alert.show();
			return 0;
		}else if(Integer.parseInt(s)>1500||Integer.parseInt(e)>1500||Integer.parseInt(d)>20){
			Alert alert=new Alert(AlertType.INFORMATION);
			alert.setTitle("Uncorrect format");
			alert.setContentText("Too large number for me :(");
			alert.show();
			return 0;
		}else if(Integer.parseInt(s)<0||Integer.parseInt(e)<0||Integer.parseInt(d)<0){
			Alert alert=new Alert(AlertType.INFORMATION);
			alert.setTitle("Uncorrect format");
			alert.setContentText("I cannot have negative numbers :(");
			alert.show();
			return 0;
		}
		validate=true;
		return 0;
	}
	
	//create LineComponent based on linelist
	public void showList() {

		int count=linelist.size();
		
		for(int i=0;i<count;i++){
			LineComponent line=new LineComponent();
			line.startTemp.setText(Integer.toString(linelist.get(i).getStartT()));
			line.endTemp.setText(Integer.toString(linelist.get(i).getEndT()));
			line.duration.setText(Integer.toString(linelist.get(i).getDuration()));
			lineBox.add(line.hbox);
			lineCombo.add(line);
		}
	}

	//inner class
	public class LineComponent extends Parent{
		
		TextField startTemp, endTemp, duration;
		Button insertButton, deleteButton;
		HBox hbox;
		
		CycleLine line;
		
//		boolean validate;
		//First line, no start temp
		public LineComponent() {
			
			startTemp=new TextField();
			startTemp.setStyle("-fx-font-size:20px");
			startTemp.setPromptText("Your starting temperature?");
			endTemp=new TextField();
			endTemp.setStyle("-fx-font-size:20px");
			endTemp.setPromptText("Your end temperature");
			duration=new TextField();
			duration.setStyle("-fx-font-size:20px");
			duration.setPromptText("How long time?");
			
			insertButton=new Button("+");
			insertButton.setStyle("-fx-font-size:20px");
			insertButton.setOnAction(e ->{
				
				validate=false;
				
				String sString=startTemp.getText();
				String eString=endTemp.getText();
				String dString=duration.getText();
				checkValidation(sString, eString, dString);
				
				if(validate){
				layout.getChildren().removeAll(lineBox);
					
				int index=lineBox.indexOf(this.hbox);
				System.out.println("My index is : "+index);
					
//				line=new CycleLine(sString, eString, dString);
//				linelist.add(index,line);

				LineComponent lineComponent=new LineComponent();
				lineComponent.startTemp.setText(eString);
				lineComponent.startTemp.setDisable(true);
				
				lineBox.add(index+1, lineComponent.hbox);
				lineCombo.add(index+1,lineComponent);
				
				System.out.println("My size is : "+lineCombo.size());
				
				if(lineCombo.size()-index>2){
					//how to get startTemp textfiled through linebox
					lineCombo.get(index+2).startTemp.setText("");
					lineCombo.get(index+2).startTemp.setDisable(false);
				}
				if(!modifying){
				layout.getChildren().addAll(lineBox);
				}
				}
			} );
			
			deleteButton=new Button("-");
			deleteButton.setStyle("-fx-font-size:20px");
			deleteButton.setOnAction(e ->{
				int index=lineBox.indexOf(this.hbox);
				//use object or use index?
				layout.getChildren().remove(this.hbox);
				//also remove it from linelist and linebox
				lineBox.remove(index);
				lineCombo.remove(index);
//				linelist.remove(index);
				if(index!=0){
//				linelist.get(index).setStartT(linelist.get(index-1).getEndT());
					lineCombo.get(index).startTemp.setText(lineCombo.get(index-1).endTemp.getText());
					lineCombo.get(index).startTemp.setDisable(true);
				}
			});
			
			hbox=new HBox(10);
			hbox.getChildren().addAll(startTemp,endTemp,duration,insertButton,deleteButton);
			hbox.setAlignment(Pos.CENTER);
		}		
	}

}
