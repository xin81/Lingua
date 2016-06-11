package de.lingua.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

//import settings.Settings;


public class IndexShellWriter extends XmlWriter {

	@Override
	public void save(List<String> list, String filename)
			throws IOException {
		// TODO Auto-generated method stub
		// save(list, filename);
		// TODO Auto-generated method stub
		final String XSD="words.xsd";
		File file=new File(filename);
		FileWriter fw=new FileWriter(file);
		BufferedWriter bw=new BufferedWriter(fw);
		bw.write("<?xml version=\""+1.0+"\" encoding=\"UTF-8\"?>\n");
		bw.append("<words xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		bw.append("xsi:noNamespaceSchemaLocation=\"../"+XSD+"\">\n");
		
		for(Iterator<String> iterator=list.iterator(); iterator.hasNext();){
			String id = iterator.next();
			bw.append("<word id=\""+id+"\" />\n");
		}
		
		bw.append("</words>");
		bw.close();
		fw.close();		
	}
	
	/*
	 <?xml version="1.0" encoding="UTF-8"?>
	 <words xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 xsi:noNamespaceSchemaLocation="words.xsd">
	 <word id="4efd" name="zhong1"/>
	 </words>
	*/
	@Override
	public void save(DefaultListModel<String> list, String filename) throws IOException {		
		System.out.println("save "+filename);
		// TODO Auto-generated method stub
		final String XSD="words.xsd";
		File file=new File(filename);
		FileWriter fw=new FileWriter(file);
		BufferedWriter bw=new BufferedWriter(fw);
		bw.write("<?xml version=\""+1.0+"\" encoding=\"UTF-8\"?>\n");
		bw.append("<words xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		bw.append("xsi:noNamespaceSchemaLocation=\"../"+XSD+"\">\n");
		
		for(int i=0; i < list.getSize(); i++){
			String id = list.get(i);
			bw.append("<word id=\""+id+"\" />\n");
		}
		
		bw.append("</words>");
		bw.close();
		fw.close();
	}

	@Override
	public void save(String[][]array, String filename) throws IOException {		
		System.out.println("save "+filename);
		// TODO Auto-generated method stub
		final String XSD="words.xsd";
		File file=new File(filename);
		FileWriter fw=new FileWriter(file);
		BufferedWriter bw=new BufferedWriter(fw);
		bw.write("<?xml version=\""+1.0+"\" encoding=\"UTF-8\"?>\n");
		bw.append("<words xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		bw.append("xsi:noNamespaceSchemaLocation=\"../"+XSD+"\">\n");
		
		for(int i=0; i < array.length; i++){
			System.out.println(((i*100)/array.length)+"% saved");
			bw.append("<word ");
			for(int j=0; j < array[i].length; j++){
				if(j==0){
					bw.append("id=\""+array[i][j]+"\" ");
				}else{
					bw.append("name=\""+array[i][j]+"\"");
				}
			}
			bw.append(" />\n");
		}
		
		bw.append("</words>");
		bw.close();
		fw.close();
	}	
}
