/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.5
 * Copyright (C) 2009 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 */

package de.unika.ipd.grgen.ast;

import de.unika.ipd.grgen.ir.Node;


/**
 * Dummy node needed for dangling edges
 */
public class DummyNodeDeclNode extends NodeDeclNode
{
	static {
		setName(DummyNodeDeclNode.class, "dummy node");
	}

	public DummyNodeDeclNode(IdentNode id, BaseNode type, int context, PatternGraphNode directlyNestingLHSGraph) {
		super(id, type, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
	}

	public Node getNode() {
		return null;
	}

	public boolean isDummy() {
		return true;
	}

	public String toString() {
		return "a dummy node";
	}
};
