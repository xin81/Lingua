package de.lingua.xml;

import java.io.IOException;

import javax.swing.DefaultListModel;

public abstract class XmlWriter {
	public abstract void save(DefaultListModel<String>list, String filename)throws IOException;
	public abstract void save(java.util.List<String> list, String filename)throws IOException;
	public abstract void save(String[][]array, String filename)throws IOException;
}
