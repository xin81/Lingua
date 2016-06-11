package de.lingua.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.lingua.gui.LMenuBar;

public class LMenuActionListener implements ActionListener {
	private LMenuBar lmenubar;
	public LMenuActionListener(LMenuBar lmenubar){
		this.lmenubar=lmenubar;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(lmenubar.getJmiClose())){
			lmenubar.close();
		}else if(e.getSource().equals(lmenubar.getJmiAbout())){
			lmenubar.welcome();
		}else if(e.getSource().equals(lmenubar.getJmiSave())){
			lmenubar.save();
		}else if(e.getSource().equals(lmenubar.getJmiReset())){
			lmenubar.reset();
		}else{
			System.out.println(e.getSource().toString());
		}
	}
}
