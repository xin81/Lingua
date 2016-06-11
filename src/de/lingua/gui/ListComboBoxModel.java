package de.lingua.gui;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListDataListener;

public class ListComboBoxModel implements ComboBoxModel<String> {
	private DefaultListModel<String> list;
	private JList<String>jlist;
	public ListComboBoxModel(JList<String> jlist, DefaultListModel<String> list) {
		// TODO Auto-generated constructor stub
		this.jlist=jlist;
		this.list=list;
	}
	
	@Override
	public void addListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
		list.addListDataListener(arg0);
	}
	
	@Override
	public String getElementAt(int index) {
		// TODO Auto-generated method stub
		if(index < 0){
			return list.lastElement();
		}
		
		String string="no item selected";
		if(list.get(index)!=null){
			if(list.get(index).toString().isEmpty()==false){
				string=list.get(index).toString();
			}
		}		
		return string;
	}
	@Override
	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
		list.removeListDataListener(arg0);
	}
	
	@Override
	public Object getSelectedItem(){
		Object object=jlist.getSelectedValue();
		if(object!=null){
			return object;
		}
		return "\0";
	}
	
	@Override
	public void setSelectedItem(Object arg0) {
		if(arg0!=null){			
			jlist.setSelectedValue(arg0, true);
		}
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return list.getSize();
	}
}
