package de.lingua.web;

import org.htmlparser.Node;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

public class HtmlVisitor extends Visitor {
	private StringBuffer definition;
	private String simplified;
	private String traditional;
	private String pronunciation;
	
	/*
	 * Finds
	 * <ul<li>simplified characters</li>
	 * <li>traditional characters</li>
	 * <li>the pronunciation of a Mandarin word</li>
	 * <li>the English definition of a Mandarin word</li>
	 * </ul>
	 * @param column <TD />
	 * @param index column index
	 */
	private void fetchColumnData(TableColumn column, int index){
		NodeList children=column.getChildren();
		for(SimpleNodeIterator iterator=children.elements(); iterator.hasMoreNodes();){
			Node child=iterator.nextNode();
			if(child.getClass().getName().compareTo(TextNode.class.getName())==0){
				switch(index){
					case 0: simplified =child.getText(); break;
					case 1: traditional=child.getText(); break;
					case 2: pronunciation=child.getText();break;
					case 3: if(child.getText().compareToIgnoreCase("Definition")!=0){
								definition.append(child.getText());
							}
					break;
					default: // System.out.println("");
				}
			}
		}
	}
	
	/* Fetches Row data
	 * @param table <TABLE />
	 */
	private void fetchTableData(TableTag table){
		TableRow[] rows=table.getRows();
		for(int i=0; i < rows.length; i++){
			if(rows[i]!=null){
				TableColumn[] cols=rows[i].getColumns();
				if(cols.length > 2){
					if(i < 2){
						for(int j=0; j < cols.length; j++){
							fetchColumnData(cols[j], j);
						}
					}
				}
			}
		}
	}
	
	public HtmlVisitor() {
		// TODO Auto-generated constructor stub
		definition=new StringBuffer("");
		simplified="";
		traditional="";		
	}
	
	/*
	 * Visits all HTML tags. An HTML page is perceived as a tree.
	 * Each node is represented by a tag. Empty text contents are also considered tree nodes.
	 * These nodes are called <code>TextNodes</code> 
	 * @param node current node, or tag
	 */	
	public void visit(Node node){
		if(node!=null){
			String className=node.getClass().getName();
			if(node.getChildren()!=null){
				NodeList children=node.getChildren();
				for(SimpleNodeIterator iterator=children.elements();iterator.hasMoreNodes();){
					Node child=iterator.nextNode();
					visit(child);
				}
			}
			if(className.compareTo(TableTag.class.getName())==0){
				fetchTableData((TableTag)node);
			}
		}
	}
	
	public String getSimplified(){
		return simplified;
	}
	public String getTraditional(){
		return traditional;
	}
	public String getPronunciation(){
		return pronunciation;
	}
	public String getDefinition(){
		return definition.toString();
	}
}
