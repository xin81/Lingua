package de.lingua.xml;

import java.awt.Font;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlFontReader extends XmlReader {
	private String family;
	private int style;
	private int size;
	
	private void visit(Node node){
		if(node!=null){
			if(node.hasChildNodes()){
				NodeList list=node.getChildNodes();
				for(int i=0; i < list.getLength(); i++){
					Node child=list.item(i);
					if(child!=null){
						visit(child);
					}
				}
			}
			
			if(node.getNodeName().compareTo("family")==0){
				family=node.getTextContent();
			}
			
			if(node.getNodeName().compareTo("style")==0){
				String strStyle=node.getTextContent();
				if(strStyle.compareToIgnoreCase("PLAIN")==0){
					style=Font.PLAIN;					
				}else if(strStyle.compareToIgnoreCase("BOLD")==0){
					style=Font.BOLD;
				}else if(strStyle.compareToIgnoreCase("ITALIC")==0){
					style=Font.ITALIC;
				}else if(strStyle.compareToIgnoreCase("CENTER_BASELINE")==0){
					style=Font.CENTER_BASELINE;
				}else{
					style=Font.PLAIN;
				}
			}
			
			if(node.getNodeName().compareTo("size")==0){
				size=Integer.parseInt(node.getTextContent());
			}
			
		}
	}
	public XmlFontReader() {
		// TODO Auto-generated constructor stub
		family="Arial";
		style=Font.PLAIN;
		size=15;
	}

	@Override
	public void find(Document doc) {
		// TODO Auto-generated method stub
		find(doc, "/");
	}

	@Override
	public void find(Document doc, String string) {
		// TODO Auto-generated method stub
		XPathFactory factory=XPathFactory.newInstance();
		XPath xpath=factory.newXPath();
		try{
			Node node=(Node)xpath.evaluate(string, doc, XPathConstants.NODE);
			if(node!=null){
				visit(node);
			}else{
				System.err.println("XPath expresson \""+string+"\" failed");
			}
		}catch(XPathExpressionException e){
			System.err.println(e.getMessage());
		}
	}
	
	public Font getFont(){
		return new Font(family, style, size);
	}
}
