package de.lingua.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
// import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * Sets the settings for this software application. By default, the English version is supported.
 * All XML and GIF resources are read from the hard disk.
 * Future versions will support other languages, and may requests data from a local database.
 * 
 * Implementation Details:<br />
 * <ul>
 * <li>JDK: 1.7</li>
 * <li>libs: swingx-0.8.0.jar</li>
 * <li>OS: Linux Mint 17.0 (but it should work on any other platforms as well)</li>
 * <li>
 *  <ul>Resources:
 *   <li>audio: resources/audio/*.wav (sound files)</li>
 *   <li>gif: resources/img/*.gif (GIF files)</li>
 *  </ul>
 * </li>
 * </ul> 
 * @author Nguễn Việt Tân (aka xin81)
 */
public final class Main {
	private static String language="en";
	private static boolean supports_database_requests=false;
	private static String operating_system="any";
	private static boolean permission_use_of_XMLS=false;
	
	/** @return the current language */
	public static String getLanguage(){
		return language;
	}
	/** @return the current operating system */
	public static String getOS(){
		return operating_system;
	}
	/** @return true, if database requests are supported */
	public static boolean supports_database_requests(){
		return supports_database_requests;
	}
	
	/*
	 * @param filename resources/help.txt
	 * @return help information
	 */	
	private static String help(String filename){
		String line="\0";
		StringBuffer buffer=new StringBuffer(line);
		File file=new File(filename);
		try{
			FileReader reader=new FileReader(file);
			BufferedReader br=new BufferedReader(reader);		
			while((line!=null)&&(line.length()>-1)){
				line=br.readLine();
				if(line!=null){
					buffer.append(line+"\n");
				}				
			}
			br.close();
			reader.close();
		}catch(FileNotFoundException e){
			System.err.println(e.getMessage());		
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		return buffer.toString();
	}
	
	// @return current settings
	private static String info(){
		String info="current settings:\n";
		info+="language="+language+"\n";
		info+="supports_database_requests="+supports_database_requests+"\n";
		info+="operating_system="+operating_system;
		return info;
	}
	
	// no instantiation allowed from the outside
	private Main(){
		language="en";
		supports_database_requests=false;
		operating_system="any";
	}
	
	/*
	 * @param args
	 */
	private static void changeSettings(String[] args) {
		// TODO Auto-generated method stub
		String filename="resources/help.txt";
		if(args.length < 1){
			language="en";
			supports_database_requests=false;
			operating_system="any";
		}else{			
			int i=0;
			do{
				if(args[i].startsWith("-")==true){
					if(args[0].compareToIgnoreCase("-help")==0){
						System.out.println(help(filename));
						break;
					}else if(args[i].compareToIgnoreCase("-language")==0){
						language=args[i+1];
					}else if(args[i].compareToIgnoreCase("-db")==0){
						if(Integer.parseInt(args[i+1])!=0){
							supports_database_requests=true;
						}else{
							supports_database_requests=false;
						}
					}else if(args[i].compareToIgnoreCase("-os")==0){
						operating_system=args[i+1];
					}else{
						operating_system="any";
						System.err.println(help(filename));
					}
				}
				i++;
			}while(i < (args.length-1));
		}
		System.out.println(info());
	}
	
	public static boolean getPermission_XMLS(){
		return permission_use_of_XMLS;
	}
	
	public static void main(String[] args){
		// changeSettings(args);		
		permission_use_of_XMLS=true;		
		try{
			String lookAndFeel=UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		}catch(Exception e){
			System.err.println(e.getMessage());
		}		
		
		JFrame jframe= LFrame.getInstance();
		jframe.setVisible(true);
	}
}