/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.2
 * Copyright (C) 2003-2014 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

package de.unika.ipd.grgen.ir.exprevals;

import java.util.Collection;

public class HighlightProc extends ProcedureInvocationBase {
	private Collection<Expression> toHighlightExpressions;

	public HighlightProc(Collection<Expression> toHighlightExpressions) {
		super("highlight procedure");
		this.toHighlightExpressions = toHighlightExpressions;
	}

	public Collection<Expression> getToHighlightExpressions() {
		return toHighlightExpressions;
	}

	public ProcedureBase getProcedureBase() {
		return null; // dummy needed for interface, not accessed because the type of the class already defines the procedure
	}

	public void collectNeededEntities(NeededEntities needs) {
		needs.needsGraph();
		for(Expression exprToHighlight : toHighlightExpressions) {
			exprToHighlight.collectNeededEntities(needs);
		}
	}
}
