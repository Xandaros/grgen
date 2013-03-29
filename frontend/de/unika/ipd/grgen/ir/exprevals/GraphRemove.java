/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.0
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

package de.unika.ipd.grgen.ir.exprevals;

public class GraphRemove extends EvalStatement {
	private Expression entity;

	public GraphRemove(Expression entity) {
		super("graph remove");
		this.entity = entity;
	}

	public Expression getEntity() {
		return entity;
	}

	public void collectNeededEntities(NeededEntities needs) {
		needs.needsGraph();
		entity.collectNeededEntities(needs);
	}
}
