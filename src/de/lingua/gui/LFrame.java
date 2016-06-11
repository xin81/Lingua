package de.lingua.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.lingua.gui.listener.LActionListener;
import de.lingua.gui.listener.LSelectionListener;
import de.lingua.gui.listener.LWindowListener;
import de.lingua.xml.XmlChineseReader;
import de.lingua.xml.XmlDimensionReader;
import de.lingua.xml.XmlFontReader;
import de.lingua.xml.XmlLocationReader;
import de.lingua.xml.XmlReader;
import de.lingua.xml.XmlStringReader;
/**
 * Constructs the main GUI window.
 * This window contains two GUI lists, two text areas, five buttons, one HTML editor pane, and one combo box.
 * This window basically contains 4000 Chinese characters. The student looks up each character by typing each word in PinYin.
 * Each tone is marked by a number.
 * The first high tone is marked by the number 1. The second rising tone is marked by the number 2.
 * The third falling-rising tone is marked by 3, and the fourth falling tone is marked by 4.
 * The fifth neutral tone is not marked by any number.
 * All words are typed in without white spaces.<br />
 * For example: Type in ni3hao3 to see 你好.
 * <ol>
 * <li>ma1 for 妈</li>
 * <li>ma2 for 麻</li>
 * <li>ma3 for 马</li>
 * <li>ma4 for 骂</li>
 * <li>ma for 吗</li>
 * </ol>
 * 
 * This GUI window mainly displays simplified characters in Mandarin. Occassionly,
 * Chinese names of famous people are displayed in both Mandarin, and Cantonese.
 * The Mandarin pronunciation is written down in PinYin. The Cantonese pronunciation is written in JyutPin.
 * Sometimes, the student will also encounter traditional characters.
 * This applies to Cantonese names, and some traditional characters that differ from simplified characters, entirely.
 * In the English version of this software application, all Chinese words are translated in English.
 * Other languages will be supported in the future.
 * To help the student learn to write each character, the HTML editor pane displays each character in GIF animation.
 * To learn the pronunciation, click the "Play" button to hear each word pronounced by a male speaker.
 */
public class LFrame extends JFrame {

	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static LFrame instance=null;	// one single instance of LFrame (singleton)
	private final static String FILENAME="resources/en/strings.xml";
	private JList<String>left_jlist;	// contains vocabularies that the user might be interested in
	private JList<String>right_jlist;	// contains vocabularies that the user might NOT be interested in anymore
	private DefaultListModel<String>left_listmodel; // attached to left_jlist
	private DefaultListModel<String>right_listmodel;// attached to right_jlist
	private JTextArea left_jtextarea;	// displays results in the target language
	private JTextArea right_jtextarea;	// displays results in the source language
	private JEditorPane img_jeditpane;	// displays GIF images
	private JButton jbPlay;				// plays an audio file
	private JButton jbStop;				// stops an audio file
	private JButton left_jbutton;		// moves one word to the left
	private JButton right_jbutton;		// moves one word to the right
	private JButton jbDelete;			// deletes one item from right_jList
	private JComboBox<String>jcombo;
	private JMenuBar jmenubar;
	
	/*
	 * Accepts an XPath expression, and queries a string from resources/{language}/strings.xml.
	 * Currently, only English is supported. So, replace {language} by <em>en</em>.
	 * The XPath expression must have a reference to the name of component.
	 * That particular name will be found in the given XML file.
	 * 
	 * Example for a valid XPath expression: "//*[@name='de.lingua.gui.LFrame.jbDelete']".
	 * This XPath expression looks for the attribute name which has been set to the value 'de.lingua.gui.LFrame.jbDelete'.
	 * 
	 * @filename resources/{language}/strings.xml
	 * @param compName name of GUI component
	 * @return a string from the given XML file
	 */
	private String getString(String filename, String compName)throws IOException, ParserConfigurationException, SAXException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document doc=db.parse(filename);
		XmlReader reader=new XmlStringReader();
		String expression="//*[@name='"+LFrame.class.getName()+"."+compName+"']";
		reader.find(doc, expression);
		XmlStringReader stringReader=(XmlStringReader)reader;
		return stringReader.getString();
	}
	
	/*
	 * Accepts an XPath expression, and queries a string from resources/fonts.xml
	 * @filename resources/fonts.xml
	 * @return the sizes for this frame
	 */
	private Dimension getDimension(String filename)throws IOException, ParserConfigurationException, SAXException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document doc=db.parse(filename);
		XmlReader reader=new XmlDimensionReader();

		String expression="//*[@name='"+LFrame.class.getName()+"']";
		reader.find(doc, expression);
		XmlDimensionReader anatomyReader=(XmlDimensionReader)reader;
		return new Dimension(anatomyReader.getWidth(), anatomyReader.getHeight());
	}
	
	/*
	 * Accepts an XPath expression, and queries location points (x, y) from resources/locations.xml
	 * @filename resources/locations.xml
	 * @return the location points (x, y) for this frame
	 */
	private Location getLocation(String filename)throws IOException, ParserConfigurationException, SAXException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document doc=db.parse(filename);
		XmlReader reader=new XmlLocationReader();
		
		String expression="//*[@name='"+LFrame.class.getName()+"']";
		reader.find(doc, expression);
		
		XmlLocationReader locationReader=(XmlLocationReader)reader;
		int x=locationReader.getX();
		int y=locationReader.getY();
		return new Location(x, y);
	}
	
	/*
	 * Adds a GUI list containing all word IDs on the left side of the frame.
	 * This panel contains a GUI list, and a combo box. A word ID refers to each ID value of a word
	 * in an XML file (e. g. dictionary/zhong1.xml). All these IDs are taken from the file dictionary/left.xml.
	 * Before the XML file is parsed, the file has to be validated by javax.xml.validation.Validator
	 * @return the left GUI panel
	 */
	private JPanel build_westComponents(){
		left_listmodel=new DefaultListModel<String>(); // attached to left_jlist
		
		String subpath="";
		boolean permission=Main.getPermission_XMLS();
		if(permission==true){
			subpath="permission";
		}else{
			subpath="nopermission";
		}
		
		String xml="dictionary/"+subpath+"/left.xml";
		String xsd="dictionary/words.xsd";
		importIDs(xml, xsd, left_listmodel);		
		left_jlist=new JList<String>(left_listmodel);	// contains vocabularies that the user might be interested in
		ListSelectionListener lsl=new LSelectionListener(this);
		left_jlist.addListSelectionListener(lsl);
		JScrollPane jscrollpane=new JScrollPane(left_jlist);
		jscrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		jcombo=new JComboBox<String>();
		jcombo.setEditable(true);
		jcombo.setEnabled(true);
		ComboBoxModel<String> comboModel=new ListComboBoxModel(left_jlist, left_listmodel);
		jcombo.setModel(comboModel);
		org.jdesktop.swingx.autocomplete.Configurator.enableAutoCompletion(jcombo);		
		
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new BorderLayout());
		
		jpanel.add(jcombo, BorderLayout.NORTH);
		SwingUtilities.updateComponentTreeUI(jscrollpane);		
		jpanel.add(jscrollpane, BorderLayout.CENTER);
		return jpanel;
	}

	/*
	 * Adds a GUI list containing all word IDs on the right side of the frame.
	 * This panel contains a GUI list, and a "Delete" button.
	 * The GUI list contains vocabularies that are of no interest for the student.
	 * The student can delete them all, if he wants to.
	 * @return the right GUI panel
	 */
	private JPanel build_eastComponents()throws SAXException, ParserConfigurationException, IOException{
		right_listmodel=new DefaultListModel<String>();
		
		String subpath="";
		boolean permission=Main.getPermission_XMLS();
		if(permission==true){
			subpath="permission";
		}else{
			subpath="nopermission";
		}
		
		String xml="dictionary/"+subpath+"/right.xml";
		String xsd="dictionary/words.xsd";
		importIDs(xml, xsd, right_listmodel);
		right_jlist=new JList<String>(right_listmodel);
		ListSelectionListener listener=new LSelectionListener(this);
		right_jlist.addListSelectionListener(listener);
		JScrollPane jscrollpane=new JScrollPane(right_jlist);
		jscrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jscrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// jbDelete.setName("jbDelete");
		jbDelete=new JButton(getString(FILENAME, "jbDelete"));
		ActionListener l=new LActionListener(this);
		jbDelete.addActionListener(l);
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new BorderLayout());
		SwingUtilities.updateComponentTreeUI(jbDelete);
		jpanel.add(jbDelete, BorderLayout.NORTH);
		SwingUtilities.updateComponentTreeUI(jscrollpane);
		jpanel.add(jscrollpane, BorderLayout.CENTER);
		return jpanel;
	}
	
	/*
	 * Contains HTML code snippets for displaying two Chinese characters in GIF animations.
	 * @return the HTML code for the editor pane
	 **/
	private String getEditPaneContent()throws SAXException, ParserConfigurationException, IOException{
		String url1=getString(FILENAME, "JEditorPane.url1");
		String url2=getString(FILENAME, "JEditorPane.url2");
		return "<html><body><img src=\""+url1+"\" /><img src=\""+url2+"\" /></body></html>";
	}
	
	/*
	 * Accepts an XPath expression, and retrieves the appropriate font style for text component.
	 * @param filename resources/fonts.xml
	 * @param compName name of a GUI component.
	 * @see #getString(String, String)
	 */
	private Font getFont(String filename, String compName)throws ParserConfigurationException, IOException, SAXException{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=dbf.newDocumentBuilder();
		Document doc=db.parse(new File(filename));
		XmlReader reader=new XmlFontReader();
		String expression="//*[@name='"+this.getClass().getName()+"."+compName+"']";
		reader.find(doc, expression);
		Font font=((XmlFontReader)reader).getFont();
		return font;		
	}
	
	/*
	 * This panel is placed in the centre of this GUI window.
	 * It contains two text areas, one HTML editor pane for displaying query results,
	 * and two Buttons to stop and play an audio file.
	 * Everything about this panel has been already explained in top of this document.
	 */
	private JPanel build_centreComponents()throws ParserConfigurationException, IOException, SAXException{
		String font_xmlfilename="resources/fonts.xml";
		left_jtextarea=new JTextArea(getString(FILENAME, "left_jtextarea"));
		left_jtextarea.setEditable(false);
		left_jtextarea.setName("left_jtextarea");
		left_jtextarea.setFont(getFont(font_xmlfilename, left_jtextarea.getName()));
		
		right_jtextarea=new JTextArea(getString(FILENAME, "right_jtextarea"));
		right_jtextarea.setEditable(false);
		right_jtextarea.setName("right_jtextarea");
		right_jtextarea.setFont(getFont(font_xmlfilename, right_jtextarea.getName()));
		
		img_jeditpane=new JEditorPane();
		img_jeditpane.setContentType("text/html");		
		img_jeditpane.setText(getEditPaneContent());
		img_jeditpane.setEditable(false);
		
		final int MAX=3;
		JScrollPane[] jscrollpane=new JScrollPane[MAX];
		jscrollpane[0]=new JScrollPane(left_jtextarea);
		jscrollpane[1]=new JScrollPane(right_jtextarea);
		jscrollpane[2]=new JScrollPane(img_jeditpane);
		
		for(int i=0; i < jscrollpane.length; i++){
			jscrollpane[i].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jscrollpane[i].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		
		jbPlay=new JButton(getString(FILENAME, "jbPlay"));
		jbStop=new JButton(getString(FILENAME, "jbStop"));
		ActionListener l=new LActionListener(this);
		jbPlay.addActionListener(l);
		// jbPlay.setEnabled(false);
		jbStop.addActionListener(l);
		JPanel jp=new JPanel();
		jp.setLayout(new FlowLayout());
		jp.add(jbPlay);
		jp.add(jbStop);
		
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new GridLayout(2, 2));
		SwingUtilities.updateComponentTreeUI(jscrollpane[0]);
		SwingUtilities.updateComponentTreeUI(jscrollpane[1]);
		SwingUtilities.updateComponentTreeUI(jscrollpane[2]);
		jpanel.add(jscrollpane[0]);
		jpanel.add(jscrollpane[1]);
		jpanel.add(jscrollpane[2]);
		jpanel.add(jp);
		return jpanel;
	}
	/*
	 * This panel is placed in the bottom of this GUI window.
	 * It contains two buttons to move one item from a list to another list.
	 * The user can remove words he is not interested in to the right GUI list.
	 * If he changes his mind, he can move a word back into the left list.
	 */
	private JPanel build_southComponents()throws ParserConfigurationException, SAXException, IOException{
		left_jbutton=new JButton(getString(FILENAME, "left_jbutton"));
		right_jbutton=new JButton(getString(FILENAME, "right_jbutton"));
		ActionListener l=new LActionListener(this);
		left_jbutton.addActionListener(l);
		right_jbutton.addActionListener(l);
		JPanel jpanel=new JPanel();
		jpanel.setLayout(new FlowLayout());
		SwingUtilities.updateComponentTreeUI(left_jbutton);
		SwingUtilities.updateComponentTreeUI(right_jbutton);
		jpanel.add(left_jbutton);
		jpanel.add(right_jbutton);
		return jpanel;
	}
	
	/*
	 * Builds and creates all required GUI components
	 */
	private void build(){
		try{
			JPanel jwestpanel=build_westComponents();
			JPanel jeastpanel=build_eastComponents();
			JPanel jcentrepanel=build_centreComponents();
			JPanel jsouthpanel=build_southComponents();
			
			Container container=getContentPane();					
			container.setLayout(new BorderLayout());
			container.add(jwestpanel, BorderLayout.WEST);
			container.add(jeastpanel, BorderLayout.EAST);
			container.add(jcentrepanel, BorderLayout.CENTER);
			container.add(jsouthpanel, BorderLayout.SOUTH);
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
	
	/*
	 * Validates an existing XML file with an existing XSD schema file.
	 * A valid XML file is both well-formed and valid.
	 * @return true if the XML is valid
	 */
	private boolean isValid(String xml, String xsd){		
		boolean valid=true;
		try{
			String schemaLanguage="http://www.w3.org/2001/XMLSchema";
			SchemaFactory sf=SchemaFactory.newInstance(schemaLanguage);		
			Schema schema=sf.newSchema(new StreamSource(xsd));
			Validator validator=schema.newValidator();
			validator.validate(new StreamSource(xml));
			valid=true;
		}catch(SAXException e){
			System.err.println(e.getMessage());
			valid=false;
		}catch(IOException e){
			System.err.println(e.getMessage());
			valid=false;
		}
		return valid;
	}
	
	/*
	 * Parses an dictionary/left.xml, and saves all ID values in the given the list model (<code>DefaultListModel</code>).
	 * All items from the given <code>DefaultListModel</code> are displayed in the left GUI list.
	 * @param xml dictionary/left.xml
	 * @param xsd dictionary/words.xsd
	 * @list <code>DefaultListModel<E></code> used by the left GUI list.
	 */
	private void importIDs(String xml, String xsd, DefaultListModel<String> list){
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db=dbf.newDocumentBuilder();
			if(isValid(xml, xsd)==true){
				File file=new File(xml);
				Document doc=db.parse(file);
				doc.getDocumentElement().normalize();
				XmlReader reader=new XmlChineseReader();
				String expression="//*";
				reader.find(doc, expression);
				
				XmlChineseReader xmlreader=(XmlChineseReader)reader;
				List<String> names=xmlreader.getIdNames();
				
				for(Iterator<String> iterator=names.iterator(); iterator.hasNext();){
					list.addElement(iterator.next());
				}
			}else{
				System.err.println(xml+" is not valid!");
			}			
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}		
	}	
	
	/* Only one instance of this window frame is needed */
	private LFrame() throws HeadlessException {
		// TODO Auto-generated constructor stub
		try{
			String filename="resources/en/strings.xml";
			String title=getString(filename, "title");
			setTitle(title);
			Dimension dimension=getDimension("resources/dimensions.xml");
			setSize(dimension);
			Location location=getLocation("resources/locations.xml");
			setLocation(location.getX(), location.getY());
			
			WindowListener wl=new LWindowListener(this);
			addWindowListener(wl);
			jmenubar=new LMenuBar(this);
			setJMenuBar(jmenubar);			
			build();
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}
	}
	
	/*
	 * Opens the dialogue window which asks the user if he wants to save the current session.
	 * If the user confirms the save procedure, the XML files
	 * dictionary/left.xml and dictionary/right.xml will be rewritten.
	 */
	private boolean openOptionDialog(){
		boolean hasOpened=false;
		String title="Save this session?";
		String message="Do you want to save this session?";
		int optionType=JOptionPane.OK_CANCEL_OPTION;
		int messageType=JOptionPane.QUESTION_MESSAGE;
		Icon icon=null;
		Object[] options={"Don't save", "Save"};
		Object initialValue=options[0];
		int value=JOptionPane.showOptionDialog(this, message, title,
				optionType, messageType, icon, options, initialValue);		
		final int POSITIVE=1;
		if(value==POSITIVE){
			if(jmenubar!=null){
				((LMenuBar)jmenubar).save();
			}
		}		
		hasOpened=true;		
		return hasOpened;
	}
	
	/**
	 * Before the window is closed, the user will be asked, if he wants to save the current session.
	 * If the user confirms the save procedure, the XML files
	 * dictionary/left.xml and dictionary/right.xml will be rewritten.
	 */	
	public void close(){
		if(LMenuBar.hasBeenReset()==false){
			if(openOptionDialog()==true){
				setVisible(false);
				dispose();
			}
		}else{
			setVisible(false);
			dispose();
			System.exit(LFrame.EXIT_ON_CLOSE);
		}
	}
	
	/**
	 * Invokes this GUI window
	 * @return the single instance of this GUI window
	 */
	public static LFrame getInstance(){
		if(instance==null){
			instance=new LFrame();
		}
		return instance;
	}
	
	/** @return the HTML editor pane */
	public JEditorPane getEditPane(){
		return this.img_jeditpane;
	}
	/** @return the left text area for displaying Chinese characters and pronunciations */
	public JTextArea getTargetText(){
		return left_jtextarea;
	}
	
	/** @return the right text area displaying translations (current language: English) */
	public JTextArea getSourceText(){
		return right_jtextarea;
	}
	/**
	 * @return the left GUI list containing words of interest
	 */
	public JList<String> getLeftJList(){
		return left_jlist;
	}
	
	/** @return the right GUI list containing words of no interest.
	 */
	public JList<String> getRightJList(){
		return right_jlist;
	}
	
	/** @return the list model for the left GUI list */
	public DefaultListModel<String> getLeftListModel(){
		return left_listmodel;
	}
	/** @return the list model for the right GUI list */
	public DefaultListModel<String> getRightListModel(){
		return right_listmodel;
	}
	/** @return the "Play" button */
	public JButton getJbPlay(){
		return jbPlay;
	}
	/** @return the "Stop" button */
	public JButton getJbStop(){
		return jbStop;
	}
	/** @return the "Delete" button */
	public JButton getJbDelete(){
		return jbDelete;
	}
	
	/** @return the button which moves list items to the right GUI list */
	public JButton getJbRight(){
		return right_jbutton;
	}
	
	/** @return the button which moves list items to the left GUI list */
	public JButton getJbLeft(){
		return left_jbutton;
	}
	
	/**
	 * This combo box completes the user's query, automatically
	 * @return the combo box */
	public JComboBox<String>getJCombo(){
		return jcombo;
	}	
}