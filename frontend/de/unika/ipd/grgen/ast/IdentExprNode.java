/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.0
 * Copyright (C) 2003-2011 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Moritz Kroll
 * @version $Id$
 */
package de.unika.ipd.grgen.ast;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ir.Constant;
import de.unika.ipd.grgen.ir.IR;

/**
 * An identifier expression.
 */
public class IdentExprNode extends DeclExprNode {
	static {
		setName(IdentExprNode.class, "ident expression");
	}

	boolean yieldedTo = false;

	public IdentExprNode(IdentNode ident) {
		super(ident);
	}

	public void setYieldedTo() {
		yieldedTo = true;
	}

	@Override
	protected boolean resolveLocal() {
		decl = ((DeclaredCharacter) declUnresolved).getDecl();
		if(decl instanceof TypeDeclNode)
			return true;

		return super.resolveLocal();
	}

	protected IdentNode getIdent() {
		return (IdentNode) declUnresolved;
	}

	@Override
	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("ident");
		return childrenNames;
	}

	@Override
	protected IR constructIR() {
		BaseNode declNode = (BaseNode) decl;
		if(declNode instanceof TypeDeclNode)
			return new Constant(BasicTypeNode.typeType.getType(),
					((TypeDeclNode) decl).getDeclType().getIR());
		else
			return super.constructIR();
	}
}
