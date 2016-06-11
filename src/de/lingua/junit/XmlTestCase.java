package de.lingua.junit;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.lingua.gui.LFrame;
import de.lingua.xml.XmlChineseReader;
import de.lingua.xml.XmlReader;
import de.lingua.xml.XmlStringReader;

public class XmlTestCase {

	public XmlTestCase() {
		// TODO Auto-generated constructor stub
	}
	/* tests the class XmlStringReader */
	@Test
	public void testXmlStringReader(){
		String filename="resources/en/strings.xml";
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.parse(filename);
			XmlReader reader=new XmlStringReader();
			String expression="//*[@name='"+LFrame.class.getName()+".title']";
			reader.find(doc, expression);
			String string=((XmlStringReader)reader).getString();
			Assert.assertTrue("This string is empty!", string.isEmpty()==false);
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}
	}
	
	/* Tests the class XmlChineseReader */
	@Test
	public void testXmlChineseReader(){
			testChinese("hello");
			testChinese("you");
	}
	
	private void testChinese(String id){
		String filename="dictionary/english_chinese.xml";
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db=dbf.newDocumentBuilder();		
			Document doc=db.parse(filename);
			XmlReader reader=new XmlChineseReader();
		
			String expression="//word[@id='"+id+"']";
			reader.find(doc, expression);
			XmlChineseReader zh=(XmlChineseReader)reader;
			
			String source=zh.getSource();
			Assert.assertTrue("source is empty!", source.isEmpty()==false);
			System.out.println("source language: "+source);
			
			String mandarin_pronunciation=zh.getMandarin();
			Assert.assertTrue("mandarin is empty!", mandarin_pronunciation.isEmpty()==false);
			System.out.println("mandarin: "+mandarin_pronunciation);
			
			String cantonese_pronunciation=zh.getCantonese();
			Assert.assertTrue("cantonese is empty!", cantonese_pronunciation.isEmpty()==false);
			System.out.println("cantonese: "+cantonese_pronunciation);
			
			String simple_characters=zh.getSimplifiedCharacters();
			Assert.assertTrue("there are no simplified characters!", simple_characters.isEmpty()==false);
			System.out.println("characters: "+simple_characters);
			
			String traditional_characters=zh.getTraditionalCharacters();
			Assert.assertTrue("there are no traditional characters!", traditional_characters.isEmpty()==false);
			System.out.println("characters: "+traditional_characters);
			
			List<String>giflist=zh.getGifs();
			for(Iterator<String> iterator=giflist.iterator(); iterator.hasNext();){
				String gif=iterator.next();
				Assert.assertTrue("there are gif images!", gif.isEmpty()==false);
				System.out.println("gif: "+gif);
			}
			
			String audio=zh.getAudio();
			Assert.assertTrue("There is no audio file!", audio.isEmpty()==false);
			System.out.println("audio: "+audio+"\n\n");
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}		
	}
}
