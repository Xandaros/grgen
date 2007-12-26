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
 * @author Sebastian Hack
 * @version $Id$
 */
package de.unika.ipd.grgen.ast;

import java.util.Collection;

import de.unika.ipd.grgen.parser.Coords;

/**
 * A boolean constant.
 */
public class BoolConstNode extends ConstNode
{
	/**
	 * @param coords The source code coordinates
	 * @param value The value.
	 */
	public BoolConstNode(Coords coords, boolean value) {
		super(coords, "boolean", new Boolean(value));
	}
	
	/** implementation of Walkable @see de.unika.ipd.grgen.util.Walkable#getWalkableChildren() */
	public Collection<? extends BaseNode> getWalkableChildren() {
		return children;
	}
	
	public TypeNode getType() {
		return BasicTypeNode.booleanType;
	}

	/**
	 * @see de.unika.ipd.grgen.ast.ConstNode#doCastTo(de.unika.ipd.grgen.ast.TypeNode)
	 */
	protected ConstNode doCastTo(TypeNode type) {
		boolean value = ((Boolean) getValue()).booleanValue();
		ConstNode res = ConstNode.getInvalid();

		if (type.isEqual(BasicTypeNode.intType))
			res = new IntConstNode(getCoords(), value ? 1 : 0);
		else if (type.isEqual(BasicTypeNode.stringType))
			res = new StringConstNode(getCoords(), "" + value);

		return res;
	}
}
