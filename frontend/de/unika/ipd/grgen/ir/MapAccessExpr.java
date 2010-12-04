/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.6
 * Copyright (C) 2003-2010 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Moritz Kroll, Edgar Jakumeit
 * @version $Id: MapInit.java 22945 2008-10-16 16:02:13Z moritz $
 */

package de.unika.ipd.grgen.ir;

public class MapAccessExpr extends Expression {
	Expression targetExpr;
	Expression keyExpr;

	public MapAccessExpr(Expression targetExpr, Expression keyExpr) {
		super("map access expression", ((MapType) targetExpr.getType()).getValueType());
		this.targetExpr = targetExpr;
		this.keyExpr = keyExpr;
	}

	public void collectNeededEntities(NeededEntities needs) {
		needs.add(this);
		keyExpr.collectNeededEntities(needs);
		targetExpr.collectNeededEntities(needs);
	}

	public Expression getTargetExpr() {
		return targetExpr;
	}

	public Expression getKeyExpr() {
		return keyExpr;
	}
}
