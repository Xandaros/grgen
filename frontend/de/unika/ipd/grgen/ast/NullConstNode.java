/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET v2 beta
 * Copyright (C) 2008 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
 */

/**
 * @author Rubino Geiss
 * @version $Id$
 */
package de.unika.ipd.grgen.ast;

import de.unika.ipd.grgen.ir.Constant;
import de.unika.ipd.grgen.ir.IR;
import de.unika.ipd.grgen.parser.Coords;

/**
 * The null constant.
 */
public class NullConstNode extends ConstNode
{
	private TypeNode type;

	public NullConstNode(Coords coords) {
		super(coords, "null", Value.NULL);
		type = BasicTypeNode.nullType;
	}

	/**
	 * Singleton class representing the only constant value 'null' that
	 * the basic type 'object' has.
	 */
	public static class Value {
		public static Value NULL = new Value() {
			public String toString() { return "Const null"; }
		};

		private Value() {}

		public boolean equals(Object val) {
			return (this == val);
		}
	}

	public TypeNode getType() {
		return type;
	}

	public String toString() {
		return "Const (" + type + ") null";
	}
	
	protected IR constructIR() {
		return new Constant(getType().getType(), null);
	}	

	/** @see de.unika.ipd.grgen.ast.ConstNode#doCastTo(de.unika.ipd.grgen.ast.TypeNode) */
	protected ConstNode doCastTo(TypeNode type) {
		NullConstNode castedNull = new NullConstNode(getCoords());
		castedNull.type = type;
		return castedNull;
	}
}
