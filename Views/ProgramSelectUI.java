/*
 * This class is for choosing a program from existing/ add a new project/ modify a existing project
 */

import java.util.LinkedList;

import com.sun.javafx.scene.control.SelectedCellsMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ProgramSelectUI {

	Stage window;
	LineChartDisplay temperature;
	ComboBox comboBox;
	Button newButton, modifyButton, applyButton, cancelButton;
	ProgramBuildUI buildUI;
	ProcessXML xml;
	LinkedList<Cycle> list=new LinkedList<Cycle>();

	Cycle cycle=new Cycle();
	boolean selectedProgram=false;
	
	double s_X,s_Y,s_Width,s_Height;
	
	public void start(Stage primaryStage) throws Exception {
				window=primaryStage;
				window.setTitle("Program Selector");
				
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
				
				temperature=new LineChartDisplay();
				comboBox=new ComboBox();
				
//				LinkedList<Cycle> list=new LinkedList<Cycle>();
				
				list=getList(list);
				
				comboBox.setStyle("-fx-font-size:20px");
				StackPane combo=new StackPane();
				combo.getChildren().add(comboBox);
//				comboBox.getSelectionModel().select(0);
				comboBox.setOnAction(e -> {
					try{
				    String selected = comboBox.getSelectionModel().getSelectedItem().toString();
				    System.out.println(selected);
				    cycle=getCycleByName(selected,list);
				    temperature.onUpdate(cycle);
				    selectedProgram=true;
					}catch(Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				
				newButton=new Button(" New ");
				newButton.setStyle("-fx-width:100px;-fx-font-size:20px;");
				newButton.setOnAction(e -> {
					buildUI=new ProgramBuildUI();
					try {
						buildUI.start(window);
					} catch (Exception e1) {
						// do nothing, because no effect actually...
//						e1.printStackTrace();
					}
				});
				modifyButton=new Button(" Modify ");
				modifyButton.setStyle("-fx-width:100px;-fx-font-size:20px;");
				modifyButton.setOnAction(e ->{
					// to be defined
					if(selectedProgram){
						buildUI=new ProgramBuildUI();
						buildUI.linelist=(LinkedList<CycleLine>) cycle.getCycleNode().clone();
						buildUI.modifying=true;
						buildUI.changing=true;
						buildUI.showList();
						
						try {
							buildUI.name=cycle.getCycleName();
							buildUI.start(window);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else{
						Alert alert=new Alert(AlertType.WARNING);
						alert.setTitle("No program!");
						alert.setContentText("There is no program selected >.<");
						alert.show();
					}
				});
				applyButton=new Button(" Apply ");
				applyButton.setStyle("-fx-width:100px;-fx-font-size:25px;");
				applyButton.setOnAction(e -> {
					if(selectedProgram){
					OvenStateUI ovenStateUI=new OvenStateUI();
					selectedProgram=false;
					try {
						//give cycle to new window, and start before-cycle session
						ovenStateUI.oven.setOvenController(cycle);
						ovenStateUI.oven.before=true;
						ovenStateUI.start(window);
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					}else{ 
						Alert alert=new Alert(AlertType.WARNING);
						alert.setTitle("Apply Button Alert");
						alert.setContentText("You haven't choose a program.\n Please choose a program from the combobox first.");
						alert.show();
					}
					
				});
				cancelButton=new Button(" Cancel ");
				cancelButton.setStyle("-fx-width:100px;-fx-font-size:20px;");
				cancelButton.setOnAction(e -> {
					OvenStateUI ovenStateUI=new OvenStateUI();
					try {
						ovenStateUI.start(window);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
				
				Rectangle space=new Rectangle();
				space.setWidth(10);
				space.setHeight(50);
				space.setFill(Color.TRANSPARENT);			
				
				HBox buttons=new HBox(50);
				buttons.getChildren().addAll(newButton,modifyButton,applyButton,cancelButton);
				buttons.setAlignment(Pos.BOTTOM_CENTER);
				
				VBox layout=new VBox(10);//argument in VBox means spacing(px) between different components
				layout.getChildren().addAll(temperature.lineChart, combo, space, buttons);
				
				Scene scene=new Scene(layout);
//				scene.setFill(Color.DARKGRAY);
				
				window=primaryStage;
				window.setTitle("Controlling Oven Temperature");
				window.setScene(scene);
				
				window.setOnCloseRequest(e -> {
					Platform.exit();
					});

	}
	
	public Cycle getCycleByName(String name, LinkedList<Cycle> list){
		Cycle cycle=new Cycle();
		int count=list.size();
		for(int i=0;i<count;i++){
			if(name.equals(list.get(i).getCycleName())){
				cycle.clone(cycle,list.get(i));//not sure if this can work...
				return cycle;
			}
		}
		System.out.println("cannot find matched name");
		return null;
	}
	
	//get information from xml file
	public LinkedList<Cycle> getList(LinkedList<Cycle> list){
		xml=new ProcessXML();
		list=(LinkedList<Cycle>) xml.ReadXML().clone();
		getName(list);
		return list;
	}
	
	public void getName(LinkedList<Cycle> list){
		
		ObservableList<String> programName = FXCollections.observableArrayList();
		int count=list.size();
		
		for(int i=0;i<count;i++){
//			comboBox.getItems().add(list.get(i).getCycleName());
			programName.add(list.get(i).getCycleName());
		}
		comboBox.setItems(programName);		
	}
	
	public void setList(){
		
	}
	
	public void setName(){
		
	}

}