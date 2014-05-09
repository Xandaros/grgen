/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.3
 * Copyright (C) 2003-2014 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */
package de.unika.ipd.grgen.ir.containers;

import de.unika.ipd.grgen.ir.exprevals.*;

public class DequePeekExpr extends Expression {
	private Expression targetExpr, numberExpr;

	public DequePeekExpr(Expression targetExpr, Expression numberExpr) {
		super("deque peek expr", ((DequeType)(targetExpr.getType())).valueType);
		this.targetExpr = targetExpr;
		this.numberExpr = numberExpr;
	}

	public Expression getTargetExpr() {
		return targetExpr;
	}

	public Expression getNumberExpr() {
		return numberExpr;
	}

	public void collectNeededEntities(NeededEntities needs) {
		needs.add(this);
		targetExpr.collectNeededEntities(needs);
		if(numberExpr!=null) numberExpr.collectNeededEntities(needs);
	}
}
