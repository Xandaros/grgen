/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.5
 * Copyright (C) 2003-2012 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @version $Id: Typeof.java 19827 2008-05-29 21:55:01Z moritz $
 */
package de.unika.ipd.grgen.ir;

public class IncidentEdgeExpr extends Expression {
	private final Node node;
	private final EdgeType incidentEdgeType;
	private final boolean outgoing;
	private final NodeType adjacentNodeType;

	public IncidentEdgeExpr(Node node,
			EdgeType incidentEdgeType, boolean outgoing,
			NodeType adjacentNodeType, Type type) {
		super("incident edge expression", type);
		this.node = node;
		this.incidentEdgeType = incidentEdgeType;
		this.outgoing = outgoing;
		this.adjacentNodeType = adjacentNodeType;
	}

	public Node getNode() {
		return node;
	}

	public EdgeType getIncidentEdgeType() {
		return incidentEdgeType;
	}

	public boolean isOutgoing() {
		return outgoing;
	}

	public NodeType getAdjacentNodeType() {
		return adjacentNodeType;
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNeededEntities() */
	public void collectNeededEntities(NeededEntities needs) {
		needs.needsGraph();
		needs.add(node);
	}
}

