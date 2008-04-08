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
 * @author Rubino Geiss
 * @version $Id$
 */
package de.unika.ipd.grgen.ir;

import java.util.Set;

/**
 * A member expression node.
 */
public class MemberExpression extends Expression {
	private Entity member;

	public MemberExpression(Entity member) {
		super("member", member.getType());
		this.member = member;
	}

	/** Returns the member entity of this member expression. */
	public Entity getMember() {
		return member;
	}

	/** @see de.unika.ipd.grgen.ir.Expression#collectNodesnEdges() */
	public void collectElementsAndVars(Set<Node> nodes, Set<Edge> edges, Set<Variable> vars) {
	}
}
