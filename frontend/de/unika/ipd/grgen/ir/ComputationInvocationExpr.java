/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */

package de.unika.ipd.grgen.ir;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A computation invocation is an expression.
 */
public class ComputationInvocationExpr extends Expression {
	/** The arguments of the computation invocation expression. */
	protected List<Expression> arguments = new ArrayList<Expression>();

	/** The computation of the computation invocation expression. */
	protected Computation computation;


	public ComputationInvocationExpr(Type type, Computation computation) {
		super("computation invocation expr", type);

		this.computation = computation;
	}

	/** @return The number of arguments. */
	public int arity() {
		return arguments.size();
	}

	public Computation getComputation() {
		return computation;
	}

	/**
	 * Get the ith argument.
	 * @param index The index of the argument
	 * @return The argument, if <code>index</code> was valid, <code>null</code> if not.
	 */
	public Expression getArgument(int index) {
		return index >= 0 || index < arguments.size() ? arguments.get(index) : null;
	}

	/** Adds an argument e to the expression. */
	public void addArgument(Expression e) {
		arguments.add(e);
	}

	public Collection<Expression> getWalkableChildren() {
		return arguments;
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNeededEntities() */
	public void collectNeededEntities(NeededEntities needs) {
		for(Expression child : getWalkableChildren())
			child.collectNeededEntities(needs);
	}
}
