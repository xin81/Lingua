package de.lingua.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.lingua.gui.listener.LMenuActionListener;
import de.lingua.shell.Installer;
import de.lingua.xml.IndexShellWriter;
import de.lingua.xml.XmlAboutReader;
import de.lingua.xml.XmlChineseReader;
import de.lingua.xml.XmlReader;
import de.lingua.xml.XmlStringReader;
import de.lingua.xml.XmlWriter;

// import de.lingua.xml.XmlStringReader;

public class LMenuBar extends JMenuBar {
	private class MyActionListener implements ActionListener{
		private java.awt.Window window;
		private MyActionListener(java.awt.Window window){
			this.window=window;
		}
		public void actionPerformed(ActionEvent e){
			window.setVisible(false);
			window.dispose();
		}		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LFrame lframe;
	private JMenuItem jmiSave;
	private JMenuItem jmiReset;
	private JMenuItem jmiClose;
	private JMenuItem jmiAbout;
	private static boolean hasBeenReset=false;
	private String getString(String filename, String compName)throws IOException, ParserConfigurationException, SAXException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document doc=db.parse(filename);
		XmlReader reader=new XmlStringReader();
		String expression="//*[@name='"+this.getClass().getName()+"."+compName+"']";
		reader.find(doc, expression);
		XmlStringReader stringReader=(XmlStringReader)reader;
		return stringReader.getString();
	}

	private String info(){		
		StringBuilder builder=new StringBuilder();
		try{			
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			final String xml="resources/en/about.xml";
			Document doc=db.parse(new File(xml));
			XmlReader reader=new XmlAboutReader();			
			reader.find(doc, "/about");
			
			XmlAboutReader xmlreader=(XmlAboutReader)reader;			
			String software=xmlreader.getSoftware();
			builder.append("Software: "+software+"\n");
			
			List<String> creators=xmlreader.getCreators();
			builder.append("Created by:\n");
			for(Iterator<String> iterator=creators.iterator(); iterator.hasNext();){
				String creator=iterator.next();
				builder.append("\t"+creator+"\n");
			}
			
			List<String> documentators=xmlreader.getDocumentAuthors();
			builder.append("Documented by:\n");
			for(Iterator<String> iterator=documentators.iterator(); iterator.hasNext();){
				String documentAuthor=iterator.next();
				builder.append("\t"+documentAuthor+"\n");
			}
			
			List<String> contributors=xmlreader.getContributors();
			builder.append("Contributors:\n");
			for(Iterator<String>iterator=contributors.iterator(); iterator.hasNext();){
				String contributor=iterator.next();
				builder.append("\t"+contributor+"\n");
			}
			
			String copyright=xmlreader.getCopyright();
			builder.append("Copyright: "+copyright+"\n");
			
			String licence=xmlreader.getLicence();
			builder.append("Licence: "+licence+"\n");
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}
		return builder.toString();
	}
	
	public LMenuBar(LFrame lframe) {
		// TODO Auto-generated constructor stub
		this.lframe=lframe;
		String filename=new String("resources/en/strings.xml");
		try{
			JMenu jmFile=new JMenu(getString(filename, "jmFile"));
			jmiSave=new JMenuItem(getString(filename, "jmiSave"));
			jmiReset=new JMenuItem(getString(filename, "jmiReset"));
			jmiClose=new JMenuItem(getString(filename, "jmiClose"));
			
			ActionListener al=new LMenuActionListener(this);
			jmiSave.addActionListener(al);
			jmiReset.addActionListener(al);
			jmiClose.addActionListener(al);
			
			jmFile.add(jmiSave);
			jmFile.add(jmiReset);
			jmFile.add(jmiClose);
			add(jmFile);
		
			JMenu jmHelp=new JMenu(getString(filename, "jmHelp"));
			jmiAbout=new JMenuItem(getString(filename, "jmiAbout"));
			jmiAbout.addActionListener(al);
			jmHelp.add(jmiAbout);
			add(jmHelp);
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}
	}
	
	public void reset(){
		String message="Do you really want to reset this application?";
		String title="Reset all settings";
		int optionType=JOptionPane.OK_CANCEL_OPTION;// optiontype
		int messageType=JOptionPane.QUESTION_MESSAGE;
		Icon icon=null;
		Object[] options={"Cancel", "Reset"};
		Object initialValue=options[0];
		
		int value=JOptionPane.showOptionDialog(lframe, message, title,
				optionType, messageType, icon, options, initialValue);
		if(value==1){
			Installer installer=Installer.getInstance();
			installer.reset();
			message="All settting changes will be applied a restart ";
			JOptionPane.showMessageDialog(lframe, message);
			jmiSave.setEnabled(false);
			hasBeenReset=true;
		}else{
			hasBeenReset=false;
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
	
	public void save(){
		DefaultListModel<String>names1=lframe.getLeftListModel();
		String[][]array1=new String[names1.size()][2];
		for(int i=0; i < names1.size(); i++){
			System.out.println("preparing data for left.xml: "+(((i*100)/(names1.size()))/2)+"%");
			array1[i][0]=getID(names1.get(i));
		}
		for(int i=0; i < names1.size(); i++){
			System.out.println("preparing data for left.xml: "+((i*100)/names1.size())+"%");
			array1[i][1]=names1.get(i);
		}
		
		DefaultListModel<String>names2=lframe.getRightListModel();
		String[][]array2=new String[names2.size()][2];
		for(int i=0; i < names2.size(); i++){
			System.out.println("preparing data for right.xml: "+(((i*100)/(names2.size()))/2)+"%");
			array2[i][0]=getID(names2.get(i));
		}
		for(int i=0; i < names2.size(); i++){
			System.out.println("preparing data for right.xml: "+((i*100)/names2.size())+"%");
			array2[i][1]=names2.get(i);
		}
		
		String subpath="";
		boolean permission=Main.getPermission_XMLS();
		if(permission==true){
			subpath="permission";
		}else{
			subpath="nopermission";
		}
		
		String filename="dictionary/"+subpath+"/left.xml";				
		String filename1="dictionary/"+subpath+"/right.xml";		
		boolean successful=false;
		try{
			XmlWriter writer=new IndexShellWriter();// XmlIndexWriter.getInstance();
			System.out.println("saving "+filename+" ...");
			writer.save(array1, filename);
			System.out.println("saving "+filename1+" ...");
			writer.save(array2, filename1);
			successful=true;
		}catch(IOException e){
			System.err.println(e.getMessage());
			successful=false;
		}
		
		String message="This session ";
		if(successful==true){
			message+=" has been saved";
			JOptionPane.showMessageDialog(lframe, message);
		}else{
			message+=" has not been saved";
			String title="Failed on saving this Session";
			int type=JOptionPane.ERROR_MESSAGE;
			JOptionPane.showMessageDialog(lframe, message, title,type);
		}		
	}
	
	public void welcome(){
		final int WIDTH=315;
		final int HEIGHT=280;
		final int X=775;
		final int Y=450;
		String filename="resources/en/strings.xml";
		JDialog jdialog=new JDialog(lframe);
		try{
			jdialog.setTitle(getString(filename, "jmiAbout"));
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}
		jdialog.setModal(true);
		jdialog.setSize(WIDTH, HEIGHT);
		jdialog.setLocation(X, Y);
		jdialog.setResizable(false);		
		
		jdialog.setLayout(new BorderLayout());
		int rows=20;
		int cols=20;		
		
		JTextArea jtextarea=new JTextArea(info(), rows, cols);
		jtextarea.setFont(new Font("Arial", Font.PLAIN, 16));
		jtextarea.setWrapStyleWord(true);
		jtextarea.setEditable(false);
		
		JScrollPane jscrollpane=new JScrollPane(jtextarea);
		jscrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jdialog.add(jscrollpane, BorderLayout.CENTER);		
		
		JButton jbOk=new JButton("OK");
		jbOk.addActionListener(new MyActionListener(jdialog));
		
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new FlowLayout());
		jpanel.add(jbOk);
		
		jdialog.add(jpanel, BorderLayout.SOUTH);
		jdialog.setVisible(true);
	}
	
	public void close(){
		if(hasBeenReset==false){
			lframe.close();
		}else{
			lframe.setVisible(false);
			lframe.dispose();
			System.exit(LFrame.EXIT_ON_CLOSE);
		}
	}	
	public JMenuItem getJmiSave(){
		return jmiSave;
	}
	public JMenuItem getJmiClose(){
		return jmiClose;
	}
	public JMenuItem getJmiReset(){
		return jmiReset;
	}
	public JMenuItem getJmiAbout(){
		return jmiAbout;
	}
	public static boolean hasBeenReset(){
		return hasBeenReset;
	}
}
