/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.1
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Rubino Geiss
 */

package de.unika.ipd.grgen.ir.exprevals;

import de.unika.ipd.grgen.ir.*;

public class Qualification extends Expression {
	/** The owner of the expression. */
	private final Entity owner;

	/** The member of the qualification. */
	private final Entity member;

	public Qualification(Entity owner, Entity member) {
		super("qual", member.getType());
		this.owner = owner;
		this.member = member;
	}

	public Entity getOwner() {
		return owner;
	}

	public Entity getMember() {
		return member;
	}

	public String getNodeLabel() {
		return "<" + owner + ">.<" + member + ">";
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNeededEntities() */
	public void collectNeededEntities(NeededEntities needs) {
		if(!isGlobalVariable(owner))
			needs.addAttr((GraphEntity) owner, member);
	}
}

