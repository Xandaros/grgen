/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.0
 * Copyright (C) 2008 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * Represents a "var" parameter of an action.
 *
 * @author Moritz Kroll
 * @version $Id$
 */

package de.unika.ipd.grgen.ir;

public class Variable extends Entity {
	public Variable(String name, Ident ident, Type type) {
		super(name, ident, type, false);
	}
}

