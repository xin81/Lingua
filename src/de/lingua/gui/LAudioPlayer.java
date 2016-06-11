package de.lingua.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;

/** 
 * Stops and plays an audio file. This audio player currently supports only wav files.
 * @author Nguyễn Việt Tân
 */
public class LAudioPlayer {
	private static LAudioPlayer instance=null;
	private LFrame frame;
	
	/* If this is marked as an error, open the dialogue window "Preferences", navigate to --> Java/Compiler/Error/Warnings,
	 * and select "Warning" for "Discouraged reference (access rules) under "Deprecated and restricted API"
	 */
	@SuppressWarnings("restriction")
	private static sun.audio.AudioStream audioStream=null;
	
	/* Only one audio player is needed
	 * @param frame reference to the class {@link de.lingua.gui.LFrame}
	 */
	private LAudioPlayer(LFrame frame){
		this.frame=frame;
	}
	
	/* Creates one single instance of this class
	 * @param frame reference to the class {@link de.lingua.gui.LFrame}
	 */
	public static LAudioPlayer getInstance(LFrame frame){
		if(instance==null){
			instance=new LAudioPlayer(frame);
		}
		return instance;
	}
	
	/*
	 * Plays an audio file when the user clicks the play button.
	 * Only wav files are currently supported
	 * @param filename name of the existing audio file
	 */
	@SuppressWarnings("restriction")
	public void play(String filename)throws IOException{ 
		// System.out.println("audio file: "+filename);		
		InputStream inputStream = new FileInputStream(filename);
		if(inputStream!=null){
			audioStream=new sun.audio.AudioStream(inputStream);
			sun.audio.AudioPlayer player=sun.audio.AudioPlayer.player;
    	
			JButton jbPlay=frame.getJbPlay();
			jbPlay.setText(filename);								
			jbPlay.setEnabled(false);
			
			JButton jbRight=frame.getJbRight();
			jbRight.setEnabled(false);
			player.start(audioStream);
		}
    }
	
	/**
	 * Invokes the currently active audio stream to stop playing a song
	 * @param stream active stream 
	 */
	@SuppressWarnings("restriction")
	public void stop(sun.audio.AudioStream stream)throws IOException{
		sun.audio.AudioPlayer player=sun.audio.AudioPlayer.player; 
		player.stop(stream);
		
	   	JButton jbPlay=frame.getJbPlay();		
		jbPlay.setEnabled(true);
		jbPlay.setText("Play");
		JButton jbRight=frame.getJbRight();
		jbRight.setEnabled(true);
    }
	
	/**
	 * @return the active audio stream
	 */
	@SuppressWarnings("restriction")
	public static sun.audio.AudioStream getAudioStream(){
		return audioStream;
	}	
}
