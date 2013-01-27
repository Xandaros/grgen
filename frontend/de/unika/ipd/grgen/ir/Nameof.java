/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

package de.unika.ipd.grgen.ir;

public class Nameof extends Expression {
	/** The entity whose name we want to know. */
	private final Entity entity;

	public Nameof(Entity entity, Type type) {
		super("nameof", type);
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNeededEntities() */
	public void collectNeededEntities(NeededEntities needs) {
		needs.needsGraph();

		if(entity==null)
			return;
		else
			if(!isGlobalVariable(entity))
				needs.add((GraphEntity) entity);
	}
}

