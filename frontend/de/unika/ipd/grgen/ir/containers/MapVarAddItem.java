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

public class MapVarAddItem extends EvalStatement {
	Variable target;
	Expression keyExpr;
    Expression valueExpr;

	public MapVarAddItem(Variable target, Expression keyExpr, Expression valueExpr) {
		super("map var add item");
		this.target = target;
		this.keyExpr = keyExpr;
		this.valueExpr = valueExpr;
	}

	public Variable getTarget() {
		return target;
	}

	public Expression getKeyExpr() {
		return keyExpr;
	}

	public Expression getValueExpr() {
		return valueExpr;
	}

	public void collectNeededEntities(NeededEntities needs)
	{
		if(!isGlobalVariable(target))
			needs.add(target);

		getKeyExpr().collectNeededEntities(needs);
		getValueExpr().collectNeededEntities(needs);

		if(getNext()!=null) {
			getNext().collectNeededEntities(needs);
		}
	}
}