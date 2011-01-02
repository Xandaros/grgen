/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.7
 * Copyright (C) 2003-2011 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Moritz Kroll, Edgar Jakumeit
 * @version $Id: MapInit.java 22945 2008-10-16 16:02:13Z moritz $
 */

package de.unika.ipd.grgen.ir;

import java.util.HashSet;

public class MapRemoveItem extends EvalStatement {
	Qualification target;
	Expression keyExpr;

	public MapRemoveItem(Qualification target, Expression keyExpr) {
		super("map remove item");
		this.target = target;
		this.keyExpr = keyExpr;
	}

	public Qualification getTarget() {
		return target;
	}

	public Expression getKeyExpr() {
		return keyExpr;
	}
	
	public void collectNeededEntities(NeededEntities needs)
	{
		Entity entity = target.getOwner();
		needs.add((GraphEntity) entity);

		// Temporarily do not collect variables for target
		HashSet<Variable> varSet = needs.variables;
		needs.variables = null;
		target.collectNeededEntities(needs);
		needs.variables = varSet;

		getKeyExpr().collectNeededEntities(needs);

		if(getNext()!=null) {
			getNext().collectNeededEntities(needs);
		}
	}
}
