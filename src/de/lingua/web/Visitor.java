package de.lingua.web;

import org.htmlparser.Node;

public abstract class Visitor {

	public Visitor() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Visits all nodes in a tree. In this package, the {@link de.lingua.web.HtmlVisitor} visits
	 * all nodes in an HTML tree
	 */
	public abstract void visit(Node node);
}
