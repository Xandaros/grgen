/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */

package de.unika.ipd.grgen.ir.containers;

import de.unika.ipd.grgen.ir.*;
import java.util.HashSet;

public class SetAddItem extends EvalStatement {
	Qualification target;
    Expression valueExpr;

	public SetAddItem(Qualification target, Expression valueExpr) {
		super("set add item");
		this.target = target;
		this.valueExpr = valueExpr;
	}

	public Qualification getTarget() {
		return target;
	}

	public Expression getValueExpr() {
		return valueExpr;
	}

	public void collectNeededEntities(NeededEntities needs)
	{
		Entity entity = target.getOwner();
		if(!isGlobalVariable(entity))
			needs.add((GraphEntity) entity);

		// Temporarily do not collect variables for target
		HashSet<Variable> varSet = needs.variables;
		needs.variables = null;
		target.collectNeededEntities(needs);
		needs.variables = varSet;

		getValueExpr().collectNeededEntities(needs);

		if(getNext()!=null) {
			getNext().collectNeededEntities(needs);
		}
	}
}