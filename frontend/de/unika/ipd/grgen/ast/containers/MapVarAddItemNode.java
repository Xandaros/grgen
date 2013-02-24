/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Edgar Jakumeit
 */

package de.unika.ipd.grgen.ast.containers;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ast.*;
import de.unika.ipd.grgen.ast.exprevals.*;
import de.unika.ipd.grgen.ir.exprevals.Expression;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.ir.containers.MapVarAddItem;
import de.unika.ipd.grgen.ir.Variable;
import de.unika.ipd.grgen.parser.Coords;

public class MapVarAddItemNode extends EvalStatementNode
{
	static {
		setName(MapVarAddItemNode.class, "map var add item statement");
	}

	private VarDeclNode target;
	private ExprNode keyExpr;
	private ExprNode valueExpr;

	public MapVarAddItemNode(Coords coords, VarDeclNode target, ExprNode keyExpr, ExprNode valueExpr)
	{
		super(coords);
		this.target = becomeParent(target);
		this.keyExpr = becomeParent(keyExpr);
		this.valueExpr = becomeParent(valueExpr);
	}

	@Override
	public Collection<? extends BaseNode> getChildren() {
		Vector<BaseNode> children = new Vector<BaseNode>();
		children.add(target);
		children.add(keyExpr);
		children.add(valueExpr);
		return children;
	}

	@Override
	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("target");
		childrenNames.add("keyExpr");
		childrenNames.add("valueExpr");
		return childrenNames;
	}

	@Override
	protected boolean resolveLocal() {
		return true;
	}

	@Override
	protected boolean checkLocal() {
		TypeNode targetType = target.getDeclType();
		TypeNode targetKeyType = ((MapTypeNode)targetType).keyType;
		TypeNode targetValueType = ((MapTypeNode)targetType).valueType;
		return checkType(keyExpr, targetKeyType, "map add item statement", "key")
				&& checkType(valueExpr, targetValueType, "map add item statement", "value");
	}

	@Override
	protected IR constructIR() {
		return new MapVarAddItem(target.checkIR(Variable.class),
				keyExpr.checkIR(Expression.class), valueExpr.checkIR(Expression.class));
	}
}
