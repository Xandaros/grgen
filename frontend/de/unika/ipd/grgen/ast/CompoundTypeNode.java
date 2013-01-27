/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Sebastian Hack
 */

package de.unika.ipd.grgen.ast;

import de.unika.ipd.grgen.parser.Scope;
import de.unika.ipd.grgen.parser.Symbol;


/**
 * Base class for all AST nodes representing compound types.
 * Note: The scope stored in the node
 * (accessible via {@link BaseNode#getScope()}) is the scope,
 * this compound type owns, not the scope it is declared in.
 */
public abstract class CompoundTypeNode extends DeclaredTypeNode
	implements ScopeOwner
{
	public boolean fixupDefinition(IdentNode id) {
		return fixupDefinition(id, true);
	}

	protected boolean fixupDefinition(IdentNode id, boolean reportErr)
	{
		Scope scope = getScope();

		debug.report(NOTE, "Fixup " + id + " in scope " + scope);

		// Get the definition of the ident's symbol local to the owned scope.
		Symbol.Definition def = scope.getLocalDef(id.getSymbol());
		debug.report(NOTE, "definition is: " + def);

		// The result is true, if the definition's valid.
		boolean res = def.isValid();

		// If this definition is valid, i.e. it exists,
		// the definition of the ident is rewritten to this definition,
		// else, an error is emitted,
		// since this ident was supposed to be defined in this scope.
		if(res)
			id.setSymDef(def);
		else if(reportErr)
			id.reportError("Identifier \"" + id + "\" not declared in this scope: " + scope);

		return res;
	}
}
