/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.0
 * Copyright (C) 2008 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * @author Moritz Kroll
 * @version $Id$
 */

package de.unika.ipd.grgen.ast;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ast.util.DeclarationResolver;
import de.unika.ipd.grgen.ir.Entity;
import de.unika.ipd.grgen.ir.GraphEntityExpression;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.ir.Qualification;
import de.unika.ipd.grgen.parser.Coords;

public class MemberAccessExprNode extends ExprNode
{
	static {
		setName(MemberAccessExprNode.class, "member access expression");
	}
	
	ExprNode targetExpr;
	IdentNode memberIdent;
	MemberDeclNode member;
	
	public MemberAccessExprNode(Coords coords, ExprNode targetExpr, IdentNode memberIdent) {
		super(coords);
		this.targetExpr  = becomeParent(targetExpr);
		this.memberIdent = becomeParent(memberIdent);
	}
	
	public Collection<? extends BaseNode> getChildren() {
		Vector<BaseNode> children = new Vector<BaseNode>();
		children.add(targetExpr);
		children.add(memberIdent);
		return children;
	}

	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("targetExpr");
		childrenNames.add("memberIdent");
		return childrenNames;
	}
	
	private static final DeclarationResolver<MemberDeclNode> memberResolver
	        = new DeclarationResolver<MemberDeclNode>(MemberDeclNode.class);
	
	protected boolean resolveLocal() {
		if(!targetExpr.resolve()) return false;
		
		TypeNode ownerType = targetExpr.getType();
		if(!(ownerType instanceof ScopeOwner)) {
			reportError("Left hand side of '.' has no members.");
			return false;
		}
		
		if(!(ownerType instanceof InheritanceTypeNode)) {
			reportError("Only member access of nodes and edges supported.");
			return false;
		}
		
		ScopeOwner o = (ScopeOwner) ownerType;
		o.fixupDefinition(memberIdent);
		member = memberResolver.resolve(memberIdent, this);
		
		return member != null;
	}
	
	protected boolean checkLocal() {
		return true;
	}
	
	public MemberDeclNode getDecl() {
		assert isResolved();

		return member;
	}
	
	public TypeNode getType() {
		return member.getDecl().getDeclType();
	}
	
	protected IR constructIR() {
		return new Qualification(targetExpr.checkIR(GraphEntityExpression.class).getGraphEntity(), member.checkIR(Entity.class));
	}
	
	public static String getKindStr() {
		return "member";
	}

	public static String getUseStr() {
		return "member access";
	}
}