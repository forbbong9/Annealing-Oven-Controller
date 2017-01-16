/*
 * This class is for processing XML file, read and write information into index.xml
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.application.Application;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ProcessXML {
	
	public LinkedList<Cycle> programs=new LinkedList<Cycle>();

	public LinkedList<Cycle> ReadXML(){
		try {

			File fXmlFile = new File(OvenStateUI.parameter);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList cycleList = doc.getElementsByTagName("cycle");
			//cycle-loop
			for (int i = 0; i < cycleList.getLength(); i++) {
				Node mNode = cycleList.item(i);
				Element mElement=(Element) mNode;
				
				Cycle Cycle=new Cycle();
				Cycle.setCycleName(mElement.getElementsByTagName("name").item(0).getTextContent());
				NodeList lineList =mElement.getElementsByTagName("line");
				
				//line-loop
				for(int j=0;j<lineList.getLength();j++){
					
					CycleLine cLine=new CycleLine();
					Node nNode = lineList.item(j);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						
						Element eElement = (Element) nNode;
						cLine.setStartT(eElement.getElementsByTagName("start").item(0).getTextContent());
						cLine.setEndT(eElement.getElementsByTagName("end").item(0).getTextContent());
						cLine.setDuration(eElement.getElementsByTagName("duration").item(0).getTextContent());
						Cycle.setCycleNode(cLine);
					}
				}
//				Cycle.setCycleName(cList.getAttribute("name"));
				programs.add(Cycle);
				
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }

		return programs;
	}
	
	
	public void WriteXML(LinkedList<Cycle> programs){
		try {
			File fXmlFile = new File(OvenStateUI.parameter);
			fXmlFile.delete();

		    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		    Document doc = docBuilder.newDocument();
		    
		    Element rootElement = doc.createElement("Programs");
		    doc.appendChild(rootElement);
		    
		    LinkedList<CycleLine> pcycle=new LinkedList<CycleLine>();
		    int pCount=programs.size();
		    int cCount;
		    
		    //cycle-loop
		    for(int i=0;i<pCount;i++){
			    Element cycle = doc.createElement("cycle");
			    rootElement.appendChild(cycle);
			    
			    pcycle=(LinkedList<CycleLine>) programs.get(i).getCycleNode().clone();
			    cCount=pcycle.size();
			    
			  //print out demo
				System.out.println("Now the linelist has:");
				int cc=pcycle.size();
				for(int ti=0;ti<cc;ti++){
					int lstart=pcycle.get(ti).getStartT();
					int lend=pcycle.get(ti).getEndT();
					int lduration=pcycle.get(ti).getDuration();
					System.out.println("No "+ti+" Start: "+lstart+" End: "+lend+" Duration: "+lduration);
				}
				System.out.println("---------------------------");
		
			    
			    Element name=doc.createElement("name");
			    name.appendChild(doc.createTextNode(programs.get(i).getCycleName()));
			    cycle.appendChild(name);
			    
			    //line-loop
			    for(int j=0;j<cCount;j++){
			    	
			    	Element line = doc.createElement("line");
				    cycle.appendChild(line);
				    
				    Element start = doc.createElement("start");
				    start.appendChild(doc.createTextNode(Integer.toString(pcycle.get(j).getStartT())));
				    line.appendChild(start);
				    
				    Element end = doc.createElement("end");
				    end.appendChild(doc.createTextNode(Integer.toString(pcycle.get(j).getEndT())));
				    line.appendChild(end);
				    
				    Element duration = doc.createElement("duration");
				    duration.appendChild(doc.createTextNode(Integer.toString(pcycle.get(j).getDuration())));
				    line.appendChild(duration);
			    }
		    }
		    
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    DOMSource source = new DOMSource(doc);
		    
		    StreamResult result = new StreamResult(new File(OvenStateUI.parameter));
		    transformer.transform(source, result);
		    System.out.println("File saved!");
		    
		  } catch (ParserConfigurationException pce) {
		    pce.printStackTrace();
		  } catch (TransformerException tfe) {
		    tfe.printStackTrace();
		  }
	}
	
}
