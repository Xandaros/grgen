/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.2
 * Copyright (C) 2003-2014 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */
package de.unika.ipd.grgen.ast;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ast.exprevals.*;
import de.unika.ipd.grgen.ast.util.DeclarationResolver;
import de.unika.ipd.grgen.ir.AttributeIndex;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.ir.IndexAccessEquality;
import de.unika.ipd.grgen.ir.Node;
import de.unika.ipd.grgen.ir.exprevals.Expression;


public class MatchNodeByIndexAccessEqualityNode extends NodeDeclNode implements NodeCharacter  {
	static {
		setName(MatchNodeByIndexAccessEqualityNode.class, "match node by index access equality decl");
	}

	private IdentNode indexUnresolved;
	private AttributeIndexDeclNode index;
	private ExprNode expr;

	public MatchNodeByIndexAccessEqualityNode(IdentNode id, BaseNode type, int context,
			IdentNode index, ExprNode expr, PatternGraphNode directlyNestingLHSGraph) {
		super(id, type, false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
		this.indexUnresolved = index;
		becomeParent(this.indexUnresolved);
		this.expr = expr;
		becomeParent(this.expr);
	}

	/** returns children of this node */
	@Override
	public Collection<BaseNode> getChildren() {
		Vector<BaseNode> children = new Vector<BaseNode>();
		children.add(ident);
		children.add(getValidVersion(typeUnresolved, typeNodeDecl, typeTypeDecl));
		children.add(constraints);
		children.add(getValidVersion(indexUnresolved, index));
		children.add(expr);
		return children;
	}

	/** returns names of the children, same order as in getChildren */
	@Override
	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("ident");
		childrenNames.add("type");
		childrenNames.add("constraints");
		childrenNames.add("index");
		childrenNames.add("expression");
		return childrenNames;
	}

	private static DeclarationResolver<AttributeIndexDeclNode> indexResolver =
		new DeclarationResolver<AttributeIndexDeclNode>(AttributeIndexDeclNode.class);

	/** @see de.unika.ipd.grgen.ast.BaseNode#resolveLocal() */
	@Override
	protected boolean resolveLocal() {
		boolean successfullyResolved = super.resolveLocal();
		index = indexResolver.resolve(indexUnresolved, this);
		successfullyResolved &= index!=null;
		successfullyResolved &= expr.resolve();
		return successfullyResolved;
	}

	/** @see de.unika.ipd.grgen.ast.BaseNode#checkLocal() */
	@Override
	protected boolean checkLocal() {
		boolean res = super.checkLocal();
		if((context&CONTEXT_LHS_OR_RHS)==CONTEXT_RHS) {
			reportError("Can't employ match node by index on RHS");
			return false;
		}
		TypeNode expectedIndexAccessType = index.member.getDeclType();
		TypeNode indexAccessType = expr.getType();
		if(!indexAccessType.isCompatibleTo(expectedIndexAccessType)) {
			String expTypeName = expectedIndexAccessType instanceof DeclaredTypeNode ? ((DeclaredTypeNode)expectedIndexAccessType).getIdentNode().toString() : expectedIndexAccessType.toString();
			String typeName = indexAccessType instanceof DeclaredTypeNode ? ((DeclaredTypeNode)indexAccessType).getIdentNode().toString() : indexAccessType.toString();
			ident.reportError("Cannot convert type used in accessing index from \""
					+ typeName + "\" to \"" + expTypeName + "\" in match node by index access");
			return false;
		}
		TypeNode expectedEntityType = getDeclType();
		TypeNode entityType = index.type;
		if(!entityType.isCompatibleTo(expectedEntityType)) {
			String expTypeName = expectedEntityType instanceof DeclaredTypeNode ? ((DeclaredTypeNode)expectedEntityType).getIdentNode().toString() : expectedEntityType.toString();
			String typeName = entityType instanceof DeclaredTypeNode ? ((DeclaredTypeNode)entityType).getIdentNode().toString() : entityType.toString();
			ident.reportError("Cannot convert index type from \""
					+ typeName + "\" to pattern element type \"" + expTypeName + "\" in match node by index access");
			return false;
		}
		return res;
	}

	/** @see de.unika.ipd.grgen.ast.BaseNode#constructIR() */
	@Override
	protected IR constructIR() {
		Node node = (Node)super.constructIR();
		if (isIRAlreadySet()) { // break endless recursion in case of cycle in usage
			return getIR();
		} else{
			setIR(node);
		}
		node.setIndex(new IndexAccessEquality(index.checkIR(AttributeIndex.class), 
				expr.checkIR(Expression.class)));
		return node;
	}
}
