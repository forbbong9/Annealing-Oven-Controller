import java.util.LinkedList;

import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LineChartDisplay extends Parent{
	
	final LineChart<Number,Number> lineChart;
	
	//default example display
	public LineChartDisplay() { 
		
		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Hours");
        yAxis.setLabel("Temperature");
        //creating the chart
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("Oven Program");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("Example program");
        
        //populating the series with data
        series.getData().add(new XYChart.Data(0, 900));
        series.getData().add(new XYChart.Data(1, 800));
        series.getData().add(new XYChart.Data(2, 650));
        series.getData().add(new XYChart.Data(5, 1000));
        series.getData().add(new XYChart.Data(7, 700));
        series.getData().add(new XYChart.Data(8, 600));
        series.getData().add(new XYChart.Data(9, 450));
        series.getData().add(new XYChart.Data(10, 300));
        series.getData().add(new XYChart.Data(12, 0));
//        
        lineChart.getData().add(series);
    
	}
	
	//Update cycle
	public void onUpdate(Cycle cycle){
        XYChart.Series series = new XYChart.Series();
        series.setName(cycle.getCycleName());
        
        int count = cycle.getSize();
        if(count!=0){
        int x,y;
        x=0;
        y=cycle.getCycleNode().getFirst().getStartT();
        series.getData().add(new XYChart.Data(x,y));
        for(int i=0;i<count;i++){
        	
        	x+=cycle.getCycleNode().get(i).getDuration();
        	y=cycle.getCycleNode().get(i).getEndT();
        	series.getData().add(new XYChart.Data(x,y));
        }
        lineChart.getData().add(series);
        }else System.out.println("Cycle is empty!");
	}
}
