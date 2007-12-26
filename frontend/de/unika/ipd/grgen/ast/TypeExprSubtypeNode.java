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
 * TypeConstSubtypeNode.java
 *
 * @author Sebastian Hack
 */

package de.unika.ipd.grgen.ast;

import java.util.Collection;

import de.unika.ipd.grgen.ast.util.DeclTypeResolver;
import de.unika.ipd.grgen.ast.util.Resolver;
import de.unika.ipd.grgen.ast.util.SimpleChecker;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.ir.InheritanceType;
import de.unika.ipd.grgen.ir.TypeExprSubtypes;
import de.unika.ipd.grgen.parser.Coords;

public class TypeExprSubtypeNode extends TypeExprNode
{
	static {
		setName(TypeExprSubtypeNode.class, "type expr subtype");
	}
	
	private static final int OPERAND = 0;
	
	public TypeExprSubtypeNode(Coords coords, BaseNode type) {
		super(coords, SUBTYPES);
		addChild(type);
	}
	
	/** implementation of Walkable @see de.unika.ipd.grgen.util.Walkable#getWalkableChildren() */
	public Collection<? extends BaseNode> getWalkableChildren() {
		return children;
	}
	
	/** @see de.unika.ipd.grgen.ast.BaseNode#resolve() */
	protected boolean resolve() {
		if(isResolved()) {
			return resolutionResult();
		}
		
		debug.report(NOTE, "resolve in: " + getId() + "(" + getClass() + ")");
		boolean successfullyResolved = true;
		Resolver typeResolver = new DeclTypeResolver(InheritanceTypeNode.class);
		successfullyResolved = typeResolver.resolve(this, OPERAND) && successfullyResolved;
		nodeResolvedSetResult(successfullyResolved); // local result
		if(!successfullyResolved) {
			debug.report(NOTE, "resolve error");
		}

		for(int i=0; i<children(); ++i) {
			successfullyResolved = getChild(i).resolve() && successfullyResolved;
		}
		return successfullyResolved;
	}

	/** @see de.unika.ipd.grgen.ast.BaseNode#check() */
	protected boolean check() {
		if(!resolutionResult()) {
			return false;
		}
		if(isChecked()) {
			return getChecked();
		}
		
		boolean childrenChecked = true;
		if(!visitedDuringCheck()) {
			setCheckVisited();
			
			childrenChecked = getChild(OPERAND).check() && childrenChecked;
		}
		
		boolean locallyChecked = checkLocal();
		nodeCheckedSetResult(locallyChecked);
		
		return childrenChecked && locallyChecked;
	}
	
	protected boolean checkLocal() {
		int arity = children();
		boolean arityOk = true;

		if(arity != 1) {
			reportError("Illegal arity: " + arity + " (1 expected)");
			arityOk = false;
		}

		return arityOk && (new SimpleChecker(InheritanceTypeNode.class)).check(getChild(OPERAND), error);
	}

	protected IR constructIR() {
		InheritanceType inh =
			(InheritanceType) getChild(OPERAND).checkIR(InheritanceType.class);
		return new TypeExprSubtypes(inh);
	}
}

