/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.5
 * Copyright (C) 2003-2012 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 * @version $Id: MapItem.java 22945 2008-10-16 16:02:13Z moritz $
 */

package de.unika.ipd.grgen.ir;

public class SetItem extends IR {
	Expression valueExpr;

	public SetItem(Expression valueExpr) {
		super("set item");
		this.valueExpr = valueExpr;
	}

	public Expression getValueExpr() {
		return valueExpr;
	}

	public void collectNeededEntities(NeededEntities needs) {
		valueExpr.collectNeededEntities(needs);
	}
}
