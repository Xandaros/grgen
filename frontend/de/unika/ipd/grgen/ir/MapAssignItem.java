/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.0
 * Copyright (C) 2008 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * @author Moritz Kroll, Edgar Jakumeit
 * @version $Id: MapInit.java 22945 2008-10-16 16:02:13Z moritz $
 */

package de.unika.ipd.grgen.ir;

public class MapAssignItem extends EvalStatement {
	Qualification target;
	Expression keyExpr;
    Expression valueExpr;
	
	public MapAssignItem(Qualification target, Expression keyExpr, Expression valueExpr) {
		super("map assign item");
		this.target = target;
		this.keyExpr = keyExpr;
		this.valueExpr = valueExpr;
	}
	
	public Qualification getTarget() {
		return target;
	}
	
	public Expression getKeyExpr() {
		return keyExpr;
	}
	
	public Expression getValueExpr() {
		return valueExpr;
	}
}