package de.lingua.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.lingua.gui.LAudioPlayer;
import de.lingua.gui.LFrame;
import de.lingua.gui.Main;
import de.lingua.xml.XmlChineseReader;
import de.lingua.xml.XmlReader;

public class LActionListener implements ActionListener {
	private LFrame frame;
	
	private void showErrorMessage(String title, String message){
		int type=JOptionPane.ERROR_MESSAGE;
		JOptionPane.showMessageDialog(frame, message, title, type);
	}
	
	public LActionListener(LFrame lframe) {
		// TODO Auto-generated constructor stub
		frame=lframe;
	}
	
	/* OK */
	private void move_to_the_left(){
		// System.out.println("clicked: move to the left");
		// click the right list
		JList<String> right=frame.getRightJList();
		int index=right.getSelectedIndex();
		int last=right.getLastVisibleIndex();
		if((index >=0)&&(index <= last)){
			DefaultListModel<String>rightlist=frame.getRightListModel();
			String item=rightlist.get(index);		
			DefaultListModel<String>leftlist=frame.getLeftListModel();
			leftlist.addElement(item);			
			rightlist.remove(index);
		}
	}
	
	/* OK */
	private void move_to_the_right(){
		JList<String> left=frame.getLeftJList();
		int index=left.getSelectedIndex();
		// String value=left.getSelectedValue();
		// System.out.println("["+index+"]: "+value);
		DefaultListModel<String>rightlist=frame.getRightListModel();
		DefaultListModel<String>leftlist=frame.getLeftListModel();
		String item=leftlist.get(index);
		rightlist.addElement(item);
		
		if(index < left.getLastVisibleIndex()){
			leftlist.remove(index);
		}else{
			leftlist.setElementAt("", index);
		}
	}
	private void delete(){
		// System.out.println("clicked: delete");
		JList<String> right=frame.getRightJList();
		DefaultListModel<String>rightlist=frame.getRightListModel();
		int index=right.getSelectedIndex();
		if(index > -1){
			rightlist.remove(index);
		}else{
			showErrorMessage("Deletion failed", "Nothing has been selected");
		}		
	}
	
	private void stop(){
		LAudioPlayer player=LAudioPlayer.getInstance(frame);
		try{
			@SuppressWarnings("restriction")
			sun.audio.AudioStream audioStream=LAudioPlayer.getAudioStream();
			player.stop(audioStream);
		}catch(IOException ioe){
			System.err.println(ioe.getMessage());
		}		
	}
	
	private String getID(String name){
		boolean permission=Main.getPermission_XMLS();
		String subpath="";
		if(permission==true){
			subpath="permission";
		}else{
			subpath="nopermission";
		}
		String filename="dictionary/"+subpath+"/original.xml";
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		String id="";
		try{
			DocumentBuilder db=dbf.newDocumentBuilder();
			Document doc=db.parse(new File(filename));
			XmlReader reader=new XmlChineseReader();
			String expression="//word[@name='"+name+"']";
			reader.find(doc, expression);
			List<String>ids=((XmlChineseReader)reader).getIdList();
			for(Iterator<String> iterator=ids.iterator(); iterator.hasNext();){
				id=iterator.next();
			}
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		return id;
	}
	
	private void play()throws ParserConfigurationException, SAXException, IOException{
		JList<String> left=frame.getLeftJList();
		int index=left.getSelectedIndex();
		if(index > -1){
			DefaultListModel<String> leftlist=frame.getLeftListModel();
			String item=leftlist.get(index);
			String id=getID(item);
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			
			boolean permission=Main.getPermission_XMLS();
			String audiofile="";			
			if(permission==true){
				String subpath="permission";
				Document doc=db.parse(new File("dictionary/"+subpath+"/"+id+".xml"));
				XmlChineseReader reader=new XmlChineseReader();
				String expression="//word[@id='"+id+"']";
				reader.find(doc, expression);			
				audiofile=reader.getAudio();
			}else{
				audiofile="resources/audio/"+id+".wav";
			}
						
			if(audiofile.isEmpty()==false){
				LAudioPlayer player=LAudioPlayer.getInstance(frame);
				player.play(audiofile);
			}else{
				showErrorMessage("Play Audio Failed", "No audio file has been detected");
			}
		}else{
			showErrorMessage("Play Audio Failed", "No item has been selected");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
		if(e.getSource().equals(frame.getJbRight())==true){
			move_to_the_right();
		}else if(e.getSource().equals(frame.getJbLeft())==true){
			move_to_the_left();
		}else if(e.getSource().equals(frame.getJbDelete())==true){
			delete();
		}else if(e.getSource().equals(frame.getJbPlay())==true){
			try{
				play();
			}catch(ParserConfigurationException pe){
				// System.err.println("pe: "+pe.getMessage());
				showErrorMessage("ParserConf. Exception", pe.getMessage());
			}catch(SAXException se){
				// System.err.println("sax: "+se.getMessage());
				showErrorMessage("SAXException", se.getMessage());
			}catch(IOException ioe){
				// System.err.println("io: "+ioe.getMessage());
				showErrorMessage("IOException", ioe.getMessage());
			}
		}else if(e.getSource().equals(frame.getJbStop())==true){
			stop();
		}else{
			System.err.println("no function for "+e.getSource().toString()+" has been implemented yet");
		}
	}
}