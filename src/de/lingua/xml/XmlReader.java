package de.lingua.xml;

import org.w3c.dom.Document;

public abstract class XmlReader {

	public XmlReader() {
		// TODO Auto-generated constructor stub
	}
	public abstract void find(Document doc);
	public abstract void find(Document doc, String string);
}
