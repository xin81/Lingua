package de.lingua.xml;//.install;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class IndexShellReader extends XmlReader{
	private List<String>ids;
	private List<String>names;
	private void visit(Node node){
		if(node.hasChildNodes()){
			NodeList list=node.getChildNodes();
			for(int i=0; i < list.getLength(); i++){
				Node child=list.item(i);
				visit(child);
			}
		}
		if(node.getNodeName().compareTo("word")==0){
			if(node.hasAttributes()){
				NamedNodeMap map=node.getAttributes();
				for(int i=0; i < map.getLength(); i++){
					Node item=map.item(i);
					String attribute=item.getNodeName();
					if(attribute.compareTo("id")==0){
						String value=item.getNodeValue();
						ids.add(value);
					}
					if(attribute.compareTo("name")==0){
						String value=item.getNodeValue();
						names.add(value);
					}
				}
			}
		}
	}
	
	
	@Override
	public void find(Document doc) {
		// TODO Auto-generated method stub
		find(doc, "/");
	}
	
	@Override
	public void find(Document doc, String expression) {
		// TODO Auto-generated method stub
		XPath xpath=XPathFactory.newInstance().newXPath();
		// String expression="//*";
		try{
			Node node=(Node)xpath.evaluate(expression, doc, XPathConstants.NODE);
			if(node!=null){
				visit(node);
			}else{
				System.err.println("XPath expression \""+expression+"\" failed");
			}
		}catch(Exception e){
			
		}
	}

	public IndexShellReader(){		
		ids=new ArrayList<String>(4000);
		names=new ArrayList<String>(4000);
	}
	/*
	public String[][] get2dArray(){
		return array;
	}*/
	
	/*public IndexShellReader(List<String> list){
		this.list=list;
	}*/
	public List<String>getNames(){return names;}
	public List<String>getIDs(){return ids;}
}
