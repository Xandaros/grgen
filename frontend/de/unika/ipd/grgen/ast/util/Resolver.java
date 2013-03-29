/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.0
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Sebastian Hack
 */

package de.unika.ipd.grgen.ast.util;

import de.unika.ipd.grgen.ast.BaseNode;
import de.unika.ipd.grgen.util.Base;

/**
 * something, that resolves a node to another node.
 *
 * @param <T> the type of the resolution result.
 */
public abstract class Resolver<T> extends Base {
	/**
	 * Resolves a node to another node.
	 * (but doesn't replace the node in the AST)
	 *
	 * @param node The original node to resolve.
	 * @param parent The new parent of the resolved node.
	 * @return The node the original node was resolved to (which might be the
	 *         original node itself), or null if the resolving failed.
	 */
	public abstract T resolve(BaseNode node, BaseNode parent);
}
