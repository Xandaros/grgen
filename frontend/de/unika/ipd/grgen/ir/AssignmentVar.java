/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.5
 * Copyright (C) 2003-2012 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */
package de.unika.ipd.grgen.ir;


/**
 * Represents an assignment statement in the IR.
 */
public class AssignmentVar extends EvalStatement {

	/** The lhs of the assignment. */
	private Variable target;

	/** The rhs of the assignment. */
	private Expression expr;

	public AssignmentVar(Variable target, Expression expr) {
		super("assignment var");
		this.target = target;
		this.expr = expr;
	}

	protected AssignmentVar(String name, Variable target, Expression expr) {
		super(name);
		this.target = target;
		this.expr = expr;
	}

	public Variable getTarget() {
		return target;
	}

	public Expression getExpression() {
		return expr;
	}

	public String toString() {
		return getTarget() + " = " + getExpression();
	}

	public void collectNeededEntities(NeededEntities needs)
	{
		if(!isGlobalVariable(target))
			needs.add(target);

		getExpression().collectNeededEntities(needs);
	}
}
