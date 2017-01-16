import java.util.LinkedList;

public class Cycle {
	
	private String CycleName;
	private LinkedList<CycleLine> CycleNode=new LinkedList<CycleLine>();

	public LinkedList<CycleLine> getCycleNode() {
		return this.CycleNode;
	}

	public void setCycleNode(LinkedList<CycleLine> linelist) {
		CycleNode=(LinkedList<CycleLine>) linelist.clone();
	}
	public void setCycleNode(CycleLine cLine) {
		CycleNode.add(cLine);
	}

	public String getCycleName() {
		return this.CycleName;
	}

	public void setCycleName(String cycleName) {
		CycleName = cycleName;
	}
	
	public int getSize(){
		return CycleNode.size();
	}
	
	public void clone(Cycle newC,Cycle oldC){
		newC.CycleName=oldC.CycleName;
		newC.CycleNode=(LinkedList<CycleLine>) oldC.CycleNode.clone();
	}

	public int getDurationTime() {
		int count=this.getSize();
		int duration=0;
		
		for(int i=0;i<count;i++){
			int temp=this.getCycleNode().get(i).getDuration();
			duration+=temp;
		}
		
		return duration;
	}


}
