package de.lingua.gui.listener;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.lingua.gui.LAudioPlayer;
import de.lingua.gui.LFrame;
import de.lingua.gui.Main;
import de.lingua.web.HtmlVisitor;
import de.lingua.web.Visitor;
import de.lingua.xml.XmlChineseReader;
import de.lingua.xml.XmlReader;

public class LSelectionListener implements ListSelectionListener{
	private LFrame frame;
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
	
	private String html(java.util.List<String> list){
		String code="<html><body>";
		for(Iterator<String> iterator=list.iterator(); iterator.hasNext();){
			String uri=iterator.next();
			if(uri.startsWith("http://")==false){
				code+="<img src=\"file:"+uri+"\" /> ";
			}else{
				code+="<img src=\""+uri+"\" />";
			}
		}
		code+="</body></html>";
		return code;
	}
	
	// displays gifs in the JEditorPane jedit
	private void displayGifs(Document doc, String item, boolean permission){
		XmlChineseReader gifreader=new XmlChineseReader();
		String expression="//word[@id='"+item+"']";
		gifreader.find(doc, expression);
		List<String>gifs=gifreader.getGifs();
		String htmlcode=html(gifs);
		JEditorPane jeditpane=frame.getEditPane();
		jeditpane.setText(htmlcode);		
	}
	
	// displays chinese vocabularies in the left text area
	// both simplified and traditional characters are printed on the screen
	// in addition, mandarin is written in pinyin, and cantonese is written in jyutpin
	private void displayChinese(Document doc, String item){
		JTextArea jtextarea=frame.getTargetText();
		XmlChineseReader reader=new XmlChineseReader();
		String expression="//word[@id='"+item+"']";
		reader.find(doc, expression);
		
		// display mandarin		
		String mandarin=reader.getMandarin();
		String mcharacter=reader.getSimplifiedCharacters();		
		jtextarea.setText(mandarin);		
		jtextarea.append("\n"+mcharacter);
		
		// display Jyutping		
		String cantonese=reader.getCantonese();
		if((cantonese.isEmpty()==false)&&(cantonese.compareToIgnoreCase("cantonese")!=0)){
			jtextarea.append("\n"+cantonese);
			String trcharacter=reader.getTraditionalCharacters();
			if((trcharacter.isEmpty()==false)&&(trcharacter.compareToIgnoreCase("traditional")!=0)){
				jtextarea.append("\n"+trcharacter);
			}
		}
	}
	
	private void displaySourceLang(Document doc, String item){
		JTextArea jtextarea=frame.getSourceText();
		XmlChineseReader reader=new XmlChineseReader();
		String expression="//word[@id='"+item+"']";
		reader.find(doc, expression);		
		String english=reader.getSource();
		jtextarea.setText(english);
	}

	private void display(String item){
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db=dbf.newDocumentBuilder();
			String subpath="";
			String xml="";
			String xsd="";
			
			boolean permission_xml=de.lingua.gui.Main.getPermission_XMLS();
			if(permission_xml==true){
				subpath="permission";
				xml="dictionary/"+subpath+"/"+item+".xml";
				xsd="dictionary/"+subpath+"/english_chinese.xsd";
			}

			if(isValid(xml, xsd)==true){
				File file=new File(xml);				
				Document doc=db.parse(file);			
				displayChinese(doc, item);
				displaySourceLang(doc, item);
				boolean permission_gif=de.lingua.gui.Main.getPermission_XMLS();
				displayGifs(doc, item, permission_gif);
			}else{
				String message=new String(xml+" is not valid!");
				String title="Invalid XML file";
				int type=JOptionPane.ERROR_MESSAGE;
				JOptionPane.showMessageDialog(frame, message, title, type);
			}
		}catch(ParserConfigurationException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}catch(SAXException e){
			System.err.println(e.getMessage());
		}
	}
	
	/*
	 * Converts a decimal HTML char code (e. g. &#299;) into a readable Unicode character (e.g. ī)
	 * @param string might contain an HTML char code (&#299;)
	 * @return the new string replacing the HTML char code with the corresponding Unicode character
	 */
	private String convert(String string){
		int start=string.indexOf('&');
		int end=string.indexOf(';');
		String substr=string.substring(start, (end+1));
		String code=string.substring((start+2), end);
		int decimal=Integer.parseInt(code);
		int codePoint=decimal;
		char[]ch=Character.toChars(codePoint);
		String newSubStr="";
		for(int i=0; i < ch.length; i++){
			newSubStr+=ch[i];
		}
		return string.replace(substr, newSubStr);
	}
	
	/*
	 * Parses one web page from http://www.learnChineseEZ.com
	 * WARNING: Only English translation is supported this class because the given URL contains
	 * only Chinese and English contents. Any other languages are NOT supported.
	 * @param url http://www.learnchineseEZ.com
	 */
	private void displayFromWeb(String url)throws ParserException{
		try{
			Parser p=new Parser(url);
			p.setEncoding("utf-8");
			NodeList list=p.parse(null);
			Visitor visitor=new HtmlVisitor();
			for(SimpleNodeIterator iterator=list.elements(); iterator.hasMoreNodes();){
				Node next=iterator.nextNode();
				visitor.visit(next);
			}
			
			String definition=((HtmlVisitor)visitor).getDefinition();
			String pronunciation=((HtmlVisitor)visitor).getPronunciation();
			String newPronunciation=convert(pronunciation);
			String simplified=((HtmlVisitor)visitor).getSimplified();
			String traditional=((HtmlVisitor)visitor).getTraditional();
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			try{
				DocumentBuilder db=factory.newDocumentBuilder();			
				Document doc=db.newDocument();
				doc.setXmlVersion("1.0");
				doc.setXmlStandalone(true);
				int start=url.indexOf('=');
				String id=url.substring((start+1));
				
				Element dictionary=doc.createElement("dictionary");
				
				/////////// source /////////////
				Element source=doc.createElement("source");
				source.setAttribute("name", "english");
				source.setTextContent(definition);				
				
				/////////// dialect: mandarin ////////////
				Element mandarin=doc.createElement("dialect");
				mandarin.setAttribute("name", "mandarin");
				Element mpronunciation=doc.createElement("pronunciation");
				mpronunciation.setTextContent(newPronunciation);
				mandarin.appendChild(mpronunciation);
				
				// simplified
				Element mcharacters=doc.createElement("characters");
				mcharacters.setAttribute("style", "simplified");
				mcharacters.setTextContent(simplified);
				mandarin.appendChild(mcharacters);
				
				///////////// dialect: cantonese //////////////
				Element cantonese=doc.createElement("dialect");
				cantonese.setAttribute("name", "cantonese");
				
				Element cpronunciation=doc.createElement("pronunciation");
				cpronunciation.setTextContent("\0");				
				cantonese.appendChild(cpronunciation);	
				
				// traditional
				Element trcharacters=doc.createElement("characters");
				trcharacters.setAttribute("style", "traditional");
				trcharacters.setTextContent(traditional);				
				cantonese.appendChild(trcharacters);
				
				////////// target /////////////
				Element target=doc.createElement("target");
				target.setAttribute("name", "chinese");
				target.appendChild(mandarin);
				target.appendChild(cantonese);
				////////// URL ///////////////////
				
				Element uri=doc.createElement("url");
				Element gif=doc.createElement("gif");
				String strUrl="http://www.learnchineseez.com/read-write/images/"+id+".gif";
				gif.setAttribute("src", strUrl);
				uri.appendChild(gif);
				
				Element audio=doc.createElement("audio");
				String src="resources/audio/"+id+".wav";
				audio.setAttribute("src", src);
				uri.appendChild(audio);
				////////// word ////////////////
				Element word=doc.createElement("word");
				word.setAttribute("id", id);
				word.appendChild(source);
				word.appendChild(target);
				word.appendChild(uri);
				
				////////////////////////////////				
				dictionary.appendChild(word);
				doc.appendChild(dictionary);
				displayGifs(doc, id, de.lingua.gui.Main.getPermission_XMLS());
				displayChinese(doc, id);
				displaySourceLang(doc, id);
			}catch(ParserConfigurationException e){
				System.err.println(e.getMessage());
			}
		}catch(ParserException e){
			System.err.println(e.getMessage());
		}
	}
	
	// stops the current audio file,
	// and enables the play button
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
	
	public LSelectionListener(LFrame frame){
		this.frame=frame;
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
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(frame.getLeftJList())==true){
			JList<String> left=frame.getLeftJList();
			int index=left.getSelectedIndex();			
			JButton right_button=frame.getJbRight();
			if(index > -1){
				// display results
				DefaultListModel<String>list=frame.getLeftListModel();
				String item=list.get(index);
				String id=getID(item);
				
				if(de.lingua.gui.Main.getPermission_XMLS()==true){
					display(id);
				}else{
					String url=new String("http://www.learnchineseez.com/read-write/simplified/view.php?code="+id+"");
					try{
						// contains bugs to be fixed!!
						displayFromWeb(url);
					}catch(ParserException p){
						System.err.println(p.getMessage());
					}
				}
				// enable button "move to the right"
				right_button.setEnabled(true);
				stop();
			}else{
				// disable button "move to the right"
				right_button.setEnabled(false);				
			}			
		}else if(e.getSource().equals(frame.getRightJList())==true){
			JList<String> right=frame.getRightJList();
			int index=right.getSelectedIndex();
			JButton left_button=frame.getJbLeft();
			if(index > -1){
				// enable button "move to the left"
				left_button.setEnabled(true);				
			}else{
				// disable button "move to the left"
				left_button.setEnabled(false);				
			}
		}
	}	
}
