package de.lingua.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlStringReader extends XmlReader {
	private String string;
	private void visit(Node node){
		if(node.hasChildNodes()){
			NodeList list= node.getChildNodes();
			for(int i=0; i < list.getLength(); i++){
				visit(list.item(i));
			}
		}
		
		string=node.getTextContent();
	}
	
	public XmlStringReader() {
		// TODO Auto-generated constructor stub
		string="Lingua";
	}
	
	/**
	 * @param doc must be well-formed XML file
	 */
	@Override
	public void find(Document doc) {
		// TODO Auto-generated method stub
		// evaluate the entire document from the very root to the last node of the XML tree 
		find(doc, "/");
		
	}
	
	/**
	 * @param doc must be a well-formed XML file
	 * @param expression must be a valid XPath expression
	 */
	public void find(Document doc, String expression){
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
	
	public String getString(){
		return string;
	}
}
