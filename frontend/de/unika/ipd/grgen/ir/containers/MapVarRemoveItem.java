/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.0
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */

package de.unika.ipd.grgen.ir.containers;

import de.unika.ipd.grgen.ir.*;
import de.unika.ipd.grgen.ir.exprevals.*;

public class MapVarRemoveItem extends EvalStatement {
	Variable target;
	Expression keyExpr;

	public MapVarRemoveItem(Variable target, Expression keyExpr) {
		super("map var remove item");
		this.target = target;
		this.keyExpr = keyExpr;
	}

	public Variable getTarget() {
		return target;
	}

	public Expression getKeyExpr() {
		return keyExpr;
	}

	public void collectNeededEntities(NeededEntities needs)
	{
		if(!isGlobalVariable(target))
			needs.add(target);

		getKeyExpr().collectNeededEntities(needs);

		if(getNext()!=null) {
			getNext().collectNeededEntities(needs);
		}
	}
}
