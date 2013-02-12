/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

package de.unika.ipd.grgen.ir;

import de.unika.ipd.grgen.ast.BaseNode;

public class IncidentEdgeExpr extends Expression {
	private final Node node;
	private final EdgeType incidentEdgeType;
	private final int direction;
	private final NodeType adjacentNodeType;

	public static final int INCIDENT = 0;
	public static final int INCOMING = 1;
	public static final int OUTGOING = 2;

	public IncidentEdgeExpr(Node node,
			EdgeType incidentEdgeType, int direction,
			NodeType adjacentNodeType, Type type) {
		super("incident edge expression", type);
		this.node = node;
		this.incidentEdgeType = incidentEdgeType;
		this.direction = direction;
		this.adjacentNodeType = adjacentNodeType;
	}

	public Node getNode() {
		return node;
	}

	public EdgeType getIncidentEdgeType() {
		return incidentEdgeType;
	}

	public int Direction() {
		return direction;
	}

	public NodeType getAdjacentNodeType() {
		return adjacentNodeType;
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNeededEntities() */
	public void collectNeededEntities(NeededEntities needs) {
		needs.needsGraph();
		if(!isGlobalVariable(node) && (node.getContext()&BaseNode.CONTEXT_COMPUTATION)!=BaseNode.CONTEXT_COMPUTATION)
			needs.add(node);
	}
}

