/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET v2 beta
 * Copyright (C) 2008 Universit�t Karlsruhe, Institut f�r Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * @author Moritz Kroll
 * @version $Id$
 */
package de.unika.ipd.grgen.ir;

import java.util.Set;

/**
 * A variable expression node.
 */
public class VariableExpression extends Expression {
	private Variable var;

	public VariableExpression(Variable var) {
		super("variable", var.getType());
		this.var = var;
	}

	/** Returns the variable of this variable expression. */
	public Variable getVariable() {
		return var;
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNodesnEdges() */
	public void collectElementsAndVars(Set<Node> nodes, Set<Edge> edges, Set<Variable> vars) {
		if(vars != null)
			vars.add(var);
	}
}
