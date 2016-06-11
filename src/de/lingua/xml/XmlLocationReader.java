package de.lingua.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlLocationReader extends XmlReader {
	private int x;
	private int y;
	private void visit(Node node){
		if(node.hasChildNodes()){
			NodeList list=node.getChildNodes();
			for(int i=0; i < list.getLength(); i++){
				visit(list.item(i));
			}
		}
		
		if(node.getNodeName().compareTo("px")==0){
			x=Integer.parseInt(node.getTextContent());

		}
		
		if(node.getNodeName().compareTo("py")==0){
			y=Integer.parseInt(node.getTextContent());			
		}
	}
	
	public XmlLocationReader() {
		// TODO Auto-generated constructor stub
		x=0;
		y=0;
	}

	@Override
	public void find(Document doc) {
		// TODO Auto-generated method stub
		find(doc, "/");
	}

	@Override
	public void find(Document doc, String expression) {
		// TODO Auto-generated method stub
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		try{
			Node node=(Node)xpath.evaluate(expression, doc, XPathConstants.NODE);
			if(node!=null){
				visit(node);
			}else{
				System.err.println("XPath expression \""+expression+"\" failed!");
			}
		}catch(XPathExpressionException e){
			System.err.println(e.getMessage());
		}
	}
	
	public int getX(){
		return x;	
	}
	public int getY(){
		return y;
	}
}
