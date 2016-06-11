package de.lingua.shell;//.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.lingua.gui.Main;
import de.lingua.xml.IndexShellReader;
import de.lingua.xml.IndexShellWriter;
import de.lingua.xml.XmlReader;
import de.lingua.xml.XmlWriter;

public class Installer {
	private static Installer instance=null;
	private Installer(){		
	}
	
	public static Installer getInstance(){
		if(instance==null){
			instance=new Installer();
		}
		return instance;
	}

	public void reset(){
		boolean permission=Main.getPermission_XMLS();
		String subpath="";
		String main_xml="";
		
		if(permission==true){
			subpath="permission";
		}else{
			subpath="nopermission";
		}
		
		main_xml="dictionary/"+subpath+"/original.xml";
		String right_xml="dictionary/"+subpath+"/right.xml";//
		File file=new File(right_xml);
		try{
			FileWriter fw=new FileWriter(file);
			BufferedWriter bw=new BufferedWriter(fw);
			String xsd="dictionary/words.xsd"; //;
			bw.write("<?xml version=\""+1.0+"\" encoding=\"UTF-8\"?>\n");
			bw.append("<words xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"../"+xsd+"\">\n</words>");
			bw.close();
			fw.close();
		}catch(IOException e){
			System.err.println(e.getMessage());
		}		
		install(main_xml);
	}
	
	public void install(String filename){
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder bf=dbf.newDocumentBuilder();
			File file = new File(filename);
			Document doc=bf.parse(file);
			doc.normalize();
			
			// List<String>list=new ArrayList<String>(4000);
			XmlReader reader=new IndexShellReader();
			String expression="//*";
			reader.find(doc, expression);
			
			XmlWriter writer=new IndexShellWriter();
			boolean permission=Main.getPermission_XMLS();
			String subpath="nopermission";
			if(permission==true){
				subpath="permission";
			}else{
				subpath="nopermission";
			}
			
			List<String>ids=((IndexShellReader)reader).getIDs();
			List<String>names=((IndexShellReader)reader).getNames();
			String[][]array=new String[ids.size()][2];
			
			int i=0;
			for(Iterator<String> it=ids.iterator(); it.hasNext();){
				String id=it.next();
				array[i][0]=id;
				i++;
			}
			
			i=0;
			for(Iterator<String>it=names.iterator(); it.hasNext();){
				String name=it.next();
				array[i][1]=name;
				i++;
			}
			writer.save(array, "dictionary/"+subpath+"/left.xml");
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println();
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}
	}
}
