package de.lingua.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlChineseReader extends XmlReader {
	private String source;
	private String mandarin_pronunciation;
	private String cantonese_pronunciation;
	private String simplified_characters;
	private String traditional_characters;
	private String audio;
	private List<String>giflist;
	private List<String>idlist;
	private List<String>idNames;
	private void visit(Node node){
		if(node.hasChildNodes()){
			NodeList list= node.getChildNodes();
			for(int i=0; i < list.getLength(); i++){
				visit(list.item(i));
			}
		}
		if(node.getNodeName().compareTo("word")==0){
			if(node.hasAttributes()){
				NamedNodeMap map=node.getAttributes();
				
				Node id=map.getNamedItem("id");
				String idValue=id.getNodeValue();
				idlist.add(idValue);
				
				Node name=map.getNamedItem("name");
				String nameValue=name.getNodeValue();
				idNames.add(nameValue);
			}
		}
		// word found in the source language (e. g. english)
		if(node.getNodeName().compareTo("source")==0){
			source=node.getTextContent();
		}
		
		// word found ...
		if(node.getNodeName().compareTo("pronunciation")==0){
			Node parent=node.getParentNode();
			if(parent.getNodeName().compareTo("dialect")==0){
				if(parent.hasAttributes()){
					NamedNodeMap map=parent.getAttributes();
					Node attribute=map.getNamedItem("name");
					String value=attribute.getNodeValue();
					if(value.compareTo("mandarin")==0){// in mandarin
						mandarin_pronunciation=node.getTextContent();
					}else{// in cantonese
						cantonese_pronunciation=node.getTextContent();
					}
				}
			}
		}
		
		// chacters found
		if(node.getNodeName().compareTo("characters")==0){
			if(node.hasAttributes()){
				NamedNodeMap map=node.getAttributes();
				Node attribute=map.getNamedItem("style");
				String value=attribute.getNodeValue();
				if(value.compareTo("simplified")==0){
					simplified_characters=node.getTextContent();
				}else{
					traditional_characters=node.getTextContent();
				}
			}
		}
		
		// gifs found
		if(node.getNodeName().compareTo("gif")==0){
			if(node.hasAttributes()){
				NamedNodeMap map=node.getAttributes();
				Node attribute = map.getNamedItem("src");
				String value=attribute.getNodeValue();
				giflist.add(value);
			}
		}
		
		// audios found
		if(node.getNodeName().compareTo("audio")==0){
			if(node.hasAttributes()){
				NamedNodeMap map=node.getAttributes();
				Node attribute = map.getNamedItem("src");
				String value=attribute.getNodeValue();
				audio=value;
			}
		}
	}
	
	public XmlChineseReader() {
		// TODO Auto-generated constructor stub
		source="english";
		mandarin_pronunciation="mandarin";
		cantonese_pronunciation="cantonese";
		simplified_characters="simplified";
		traditional_characters="traditional";
		audio="audio/file.wav";
		giflist=new ArrayList<String>(10);
		idlist=new ArrayList<String>(4000);
		idNames=new ArrayList<String>(4000);
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
	
	public String getSource(){
		return source;
	}
	public String getMandarin(){
		return mandarin_pronunciation;
	}
	public String getCantonese(){
		return cantonese_pronunciation;
	}
	public String getSimplifiedCharacters(){
		return simplified_characters;
	}
	public String getTraditionalCharacters(){
		return traditional_characters;
	}
	public String getAudio(){
		return audio;
	}
	public List<String>getIdList(){
		return idlist;
	}
	public List<String>getIdNames(){
		return idNames;
	}
	public List<String> getGifs(){
		return giflist;
	}
}
