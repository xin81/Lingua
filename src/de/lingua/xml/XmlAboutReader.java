package de.lingua.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlAboutReader extends XmlReader {
	private String software;
	private String copyright;
	private String licence;
	
	private List<String> creators;
	private List<String> documentators;
	private List<String> contributors;
	
	private void collectData(Node node){		
		String name=node.getNodeName();
		String content=node.getTextContent();
		
		switch(name){
			case "software": software = node.getTextContent();
			break;
			case "author":
				if(node.getParentNode().getNodeName().compareTo("createdBy")==0){
					String creator=content;
					creators.add(creator);
				}else if(node.getParentNode().getNodeName().compareTo("documentedBy")==0){
					String documentAuthor=content;
					documentators.add(documentAuthor);
				}else if(node.getParentNode().getNodeName().compareTo("contributions")==0){
					String contributor=content;
					contributors.add(contributor);
				}else{
					// System.out.println("do nothing");
				}
			break;
			case "copyright": copyright=content;
			break;
			case "licence": licence=content;
			default: // System.out.println("do nothing");
		}		
	}
	
	private void visitAllNodes(Node node){
		if(node.hasChildNodes()){
			NodeList children=node.getChildNodes();
			for(int i=0; i < children.getLength(); i++){
				Node child=children.item(i);
				visitAllNodes(child);
			}
		}
		collectData(node);
	}
	
	public XmlAboutReader(){
		software="";
		creators=new ArrayList<String>(5);
		documentators=new ArrayList<String>(10);
		contributors=new ArrayList<String>(100);
		copyright="";
		licence="";
	}
	
	public void find(Document doc){
		find(doc, "/");
	}
	
	@Override
	public void find(Document doc, String expression) {
		XPath xpath=XPathFactory.newInstance().newXPath();		
		try{			
			Node node=(Node)xpath.evaluate(expression, doc, XPathConstants.NODE);
			if(node!=null){
				visitAllNodes(node);
			}else{
				System.err.println("XPath expression \""+expression+"\" failed!");
			}			
		}catch(XPathExpressionException e){
			System.err.println(e.getMessage());
		}		
	}
	
	public String getSoftware(){
		return software;
	}
	
	public List<String> getCreators(){
		return creators;
	}
	public List<String> getDocumentAuthors(){
		return documentators;
	}
	public List<String> getContributors(){
		return contributors;
	}
	
	public String getCopyright(){
		return copyright;
	}
	public String getLicence(){
		return licence;
	}	
}
