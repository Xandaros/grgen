/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.0
 * Copyright (C) 2008 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * @author Moritz Kroll, Edgar Jakumeit
 * @version $Id$
 */

package de.unika.ipd.grgen.ast;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ir.Entity;
import de.unika.ipd.grgen.ir.Expression;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.ir.MapRemoveItem;
import de.unika.ipd.grgen.ir.Qualification;
import de.unika.ipd.grgen.parser.Coords;

public class MapRemoveItemNode extends EvalStatementNode
{
	static {
		setName(MapRemoveItemNode.class, "map remove item");
	}

	QualIdentNode target;
	ExprNode keyExpr;

	public MapRemoveItemNode(Coords coords, QualIdentNode target, ExprNode keyExpr)
	{
		super(coords);
		this.target = becomeParent(target);
		this.keyExpr = becomeParent(keyExpr);
	}

	public Collection<? extends BaseNode> getChildren() {
		Vector<BaseNode> children = new Vector<BaseNode>();
		children.add(target);
		children.add(keyExpr);
		return children;
	}

	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("target");
		childrenNames.add("keyExpr");
		return childrenNames;
	}

	protected boolean resolveLocal() {
		return true;
	}

	protected boolean checkLocal() {
		boolean success = true;
		TypeNode targetType = target.getDecl().getDeclType();
		assert targetType instanceof MapTypeNode: target + " should have a map type";
		MapTypeNode targetMapType = (MapTypeNode) targetType;
		TypeNode keyType = targetMapType.keyType;
		TypeNode keyExprType = keyExpr.getType();

		if (!keyExprType.isEqual(keyType)) {
			keyExpr = becomeParent(keyExpr.adjustType(keyType, getCoords()));

			if (keyExpr == ConstNode.getInvalid()) {
				success = false;
			}
		}

		return success;
	}

	protected IR constructIR() {
		Entity ownerIR = target.getOwner().checkIR(Entity.class);
		Entity memberIR = target.getDecl().checkIR(Entity.class);

		return new MapRemoveItem(new Qualification(ownerIR, memberIR),
				keyExpr.checkIR(Expression.class));
	}
}
