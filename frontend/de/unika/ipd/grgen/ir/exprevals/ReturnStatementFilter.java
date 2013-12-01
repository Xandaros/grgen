/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.1
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */

package de.unika.ipd.grgen.ir.exprevals;

/**
 * Represents a return statement of a filter function in the IR.
 */
public class ReturnStatementFilter extends EvalStatement {

	public ReturnStatementFilter() {
		super("return statement filter");
	}

	public void collectNeededEntities(NeededEntities needs)
	{
	}
}
