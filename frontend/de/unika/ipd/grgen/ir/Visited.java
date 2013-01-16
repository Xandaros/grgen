/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.5
 * Copyright (C) 2003-2012 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @version $Id$
 */
package de.unika.ipd.grgen.ir;

public class Visited extends Expression {
	private Expression visitorID;
	private Entity entity;

	public Visited(Expression visitorID, Entity entity) {
		super("visited", BooleanType.getType());
		this.visitorID = visitorID;
		this.entity = entity;
	}

	public Expression getVisitorID() {
		return visitorID;
	}

	public Entity getEntity() {
		return entity;
	}

	public void collectNeededEntities(NeededEntities needs) {
		needs.needsGraph();
		if(!isGlobalVariable(entity))
			needs.add((GraphEntity) entity);
		visitorID.collectNeededEntities(needs);
	}
}
