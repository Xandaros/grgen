/*
  GrGen: graph rewrite generator tool.
  Copyright (C) 2005  IPD Goos, Universit"at Karlsruhe, Germany

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * @author shack
 * @version $Id$
 */
package de.unika.ipd.grgen.ast.util;

import de.unika.ipd.grgen.ast.BaseNode;
import de.unika.ipd.grgen.ast.CollectNode;
import de.unika.ipd.grgen.util.report.ErrorReporter;

/**
 * A checker that checks if the node is a collection node
 * and if so applies a contained child checker to all the children
 */
public class CollectChecker implements Checker
{
	/** The checker to apply to the children of the collect node to be checked by this checker */
	private Checker childChecker;

	/** Create checker with the checker to apply to the children */
	public CollectChecker(Checker childChecker)
	{
		this.childChecker = childChecker;
	}

	/** Check if the node is a collect node and if so apply the child checker to all children.
	 *  @see de.unika.ipd.grgen.ast.check.Checker#check(de.unika.ipd.grgen.ast.BaseNode, de.unika.ipd.grgen.util.report.ErrorReporter) */
	public boolean check(BaseNode node, ErrorReporter reporter)
	{
		if(node instanceof CollectNode) {
			boolean result = true;
			for(BaseNode n : node.getChildren()) {
				result = childChecker.check(n, reporter) && result;
			}
			return result;
		} else {
			node.reportError("Not a collect node"); // TODO: WTF? why report to the node??
			return false;
		}
	}
}
