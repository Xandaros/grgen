/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.0
 * Copyright (C) 2008 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * @author Moritz Kroll, Edgar Jakumeit
 * @version $Id$
 */
package de.unika.ipd.grgen.ast;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ir.Expression;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.ir.StringLength;
import de.unika.ipd.grgen.parser.Coords;

public class StringLengthNode extends ExprNode {
	static {
		setName(StringLengthNode.class, "string length");
	}
	
	ExprNode stringExpr;
	

	public StringLengthNode(Coords coords, ExprNode stringExpr) {
		super(coords);
		
		this.stringExpr = becomeParent(stringExpr);
	}

	public Collection<? extends BaseNode> getChildren() {
		Vector<BaseNode> children = new Vector<BaseNode>();
		children.add(stringExpr);
		return children;
	}

	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("string");
		return childrenNames;
	}

	protected boolean checkLocal() {
		if(!stringExpr.getType().isEqual(BasicTypeNode.stringType)) {
			stringExpr.reportError("Argument to string length expression must be of type string");
			return false;
		}
		return true;
	}
	
	protected IR constructIR() {
		return new StringLength(stringExpr.checkIR(Expression.class));
	}
	
	public TypeNode getType() {
		return BasicTypeNode.intType;
	}
}