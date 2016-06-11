package de.lingua.junit;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.junit.Assert;
import org.junit.Test;

public class HtmlVisitorCase {
	void fetchTableData(TableTag table){
		// System.out.println("fetching table data!");
		StringBuffer buffer=new StringBuffer("");
		TableRow[] rows=table.getRows();
		// if(rows.length > 1){
		for(int i=1; i < rows.length; i++){
			if(rows[i]!=null){
				TableColumn[] cols=rows[i].getColumns();
				if(cols.length > 2){
					for(int j=0; j < cols.length; j++){
						NodeList children=cols[j].getChildren();
						for(SimpleNodeIterator iterator=children.elements();iterator.hasMoreNodes();){
							Node child=iterator.nextNode();
							if(child.getClass().getName().compareTo(TextNode.class.getName())==0){
								switch(j){
									case 0: System.out.println("simplified: "+child.getText()); break;
									case 1: System.out.println("traditional: "+child.getText()); break;
									case 2: System.out.println("pronunciation: "+child.getText());break;
									case 3: buffer.append(child.getText());break;
									default: System.out.println("");
								}
							}
						}
					}
				}
			//}
		}
		}
		System.out.println("definition: "+buffer.toString());
	}
	
	/* Tests the class Visitor, and HtmlVisitor from the package de.lingua.web */
	void visit(Node node){
		if(node!=null){
			String message="no class name found!";
			String className=node.getClass().getName();
			boolean condition=(className.isEmpty()==false);
			Assert.assertTrue(message, condition);
			// System.out.println("class: "+className);
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
		}// end if(node!=null)
	}
	
	@Test
	public void test(){
		// fail("Not yet implemented");
		String id="7684";
		String url="http://www.learnchineseez.com/read-write/simplified/view.php?code="+id;
		try{
			Parser p=new Parser(url);
			NodeList list=p.parse(null);
			for(SimpleNodeIterator iterator=list.elements(); iterator.hasMoreNodes();){
				Node next=iterator.nextNode();
				visit(next);
			}
		}catch(ParserException e){
			System.err.println(e.getMessage());
		}
	}
}
