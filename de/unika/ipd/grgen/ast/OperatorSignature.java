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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.unika.ipd.grgen.parser.Coords;
import java.util.HashSet;

/**
 * Operator Description class.
 */
public class OperatorSignature extends FunctionSignature
{
	
	public static final int ERROR = 0;
	public static final int LOG_OR = 1;
	public static final int LOG_AND = 2;
	public static final int BIT_OR = 3;
	public static final int BIT_XOR = 4;
	public static final int BIT_AND = 5;
	public static final int EQ = 6;
	public static final int NE = 7;
	public static final int LT = 8;
	public static final int LE = 9;
	public static final int GT = 10;
	public static final int GE = 11;
	public static final int SHL = 12;
	public static final int SHR = 13;
	public static final int BIT_SHR = 14;
	public static final int ADD = 15;
	public static final int SUB = 16;
	public static final int MUL = 17;
	public static final int DIV = 18;
	public static final int MOD = 19;
	public static final int LOG_NOT = 20;
	public static final int BIT_NOT = 21;
	public static final int NEG = 22;
	public static final int CONST = 23;
	public static final int COND = 24;
	
	private static final int OPERATORS = COND + 1;
	
	/** Arity map of the operators. */
	private static final Map<Integer, Integer> arities = new HashMap<Integer, Integer>();
	
	/** Name map of the operators. */
	private static final Map<Integer, String> names = new HashMap<Integer, String>();
	
	static
	{
		Integer two = new Integer(2);
		Integer one = new Integer(1);
		Integer zero = new Integer(0);
		
		for(int i = 0; i < OPERATORS; i++)
			arities.put(new Integer(i), two);
		
		arities.put(new Integer(COND), new Integer(3));
		arities.put(new Integer(LOG_NOT), one);
		arities.put(new Integer(BIT_NOT), one);
		arities.put(new Integer(NEG), one);
		arities.put(new Integer(CONST), zero);
		arities.put(new Integer(ERROR), zero);
	}
	
	static
	{
		names.put(new Integer(COND), "Cond");
		names.put(new Integer(LOG_OR), "LogOr");
		names.put(new Integer(LOG_AND), "LogAnd");
		names.put(new Integer(BIT_XOR), "BitXor");
		names.put(new Integer(BIT_OR), "BitOr");
		names.put(new Integer(BIT_AND), "BitAnd");
		names.put(new Integer(EQ), "Eq");
		names.put(new Integer(NE), "Ne");
		names.put(new Integer(LT), "Lt");
		names.put(new Integer(LE), "Le");
		names.put(new Integer(GT), "Gt");
		names.put(new Integer(GE), "Ge");
		names.put(new Integer(SHL), "Shl");
		names.put(new Integer(SHR), "Shr");
		names.put(new Integer(BIT_SHR), "BitShr");
		names.put(new Integer(ADD), "Add");
		names.put(new Integer(SUB), "Sub");
		names.put(new Integer(MUL), "Mul");
		names.put(new Integer(DIV), "Div");
		names.put(new Integer(MOD), "Mod");
		names.put(new Integer(LOG_NOT), "LogNot");
		names.put(new Integer(BIT_NOT), "BitNot");
		names.put(new Integer(NEG), "Neg");
		names.put(new Integer(CONST), "Const");
		names.put(new Integer(ERROR), "Error");
	}
	
	/** Just a short form for the string type. */
	static final TypeNode STRING = BasicTypeNode.stringType;
	
	/** Just a short form for the boolean type. */
	static final TypeNode BOOLEAN = BasicTypeNode.booleanType;
	
	/** Just a short form for the int type. */
	static final TypeNode INT = BasicTypeNode.intType;
	
	/** Just a short form for the float type. */
	static final TypeNode FLOAT = BasicTypeNode.floatType;

	/** Just a short form for the float type. */
	static final TypeNode DOUBLE = BasicTypeNode.doubleType;

	/** Just a short form for the type type. */
	static final TypeNode TYPE = BasicTypeNode.typeType;
	
	/**
	 * Each operator is mapped by its ID to a Map, which maps
	 * each result type of the specific operator to a its
	 * signature.
	 */
	final static private Map<Integer, HashSet> operators = new HashMap<Integer, HashSet>();
	
	/**
	 * Makes an entry in the {@link #operators} map.
	 * @param id The ID of the operator.
	 * @param resType The result type of the operator.
	 * @param opTypes The operand types of the operator.
	 */
	final static void makeOp(int id, TypeNode resType,
							 TypeNode[] opTypes, Evaluator eval)
	{
		
		Integer oid = new Integer(id);
		
		if(operators.get(oid) == null)
			operators.put(oid, new HashSet());
		
		HashSet<OperatorSignature> typeMap = operators.get(oid);
		typeMap.add(new OperatorSignature(id, resType, opTypes, eval));
	}
	
	/**
	 * Enter a binary operator.
	 * This is just a convenience function for
	 * {@link #makeOp(int, TypeNode, TypeNode[])}.
	 */
	final static void makeBinOp(int id, TypeNode res,
								TypeNode op0, TypeNode op1, Evaluator eval)
	{
		
		makeOp(id, res, new TypeNode[] { op0, op1 }, eval);
	}
	
	/**
	 * Enter an unary operator.
	 * This is just a convenience function for
	 * {@link #makeOp(int, TypeNode, TypeNode[])}.
	 */
	final static void makeUnOp(int id, TypeNode res, TypeNode op0,
							   Evaluator eval)
	{
		makeOp(id, res, new TypeNode[] { op0 }, eval);
	}
	
	
	/**
	 * A class that represents an evaluator for constant expressions.
	 */
	static class Evaluator
	{
		
		public ExprNode evaluate(ExprNode expr, OperatorSignature op,
								 ExprNode[] args)
		{
			debug.report(NOTE, "id: " + op.id + ", name: " + names.get(new Integer(op.id)));
			
			ExprNode res = expr;
			TypeNode[] paramTypes = op.getOperandTypes();
			
			// Check, if the arity matches.
			if(args.length == paramTypes.length)
			{
				
				// Check the types of the arguments.
				for(int i = 0; i < args.length; i++)
				{
					debug.report(NOTE, "parameter type: " + paramTypes[i]
									 + " argument type: " + args[i].getType());
					if(!paramTypes[i].isEqual(args[i].getType()))
						return res;
				}
				
				// If we're here, all checks succeeded.
				try
				{
					res = eval(expr.getCoords(), op, args);
				}
				catch (NotEvaluatableException e)
				{
					debug.report(NOTE, e.toString());
				}
			}
			
			ConstNode c = (res instanceof ConstNode) ? (ConstNode)res : ConstNode.getInvalid();
			
			debug.report(NOTE, "result: " + res.getClass() + ", value: "
							 + c.getValue());
			
			return res;
		}
		
		class NotEvaluatableException extends Exception
		{
			
			private Coords coords;
			
			public NotEvaluatableException(Coords coords)
			{
				super();
				this.coords = coords;
			}
			
			@Override
				public String getMessage()
			{
				return "Expression not evaluatable at " + coords.toString();
			}
		}
		
		class ValueException extends Exception
		{
			
			private Coords coords;
			
			public ValueException(Coords coords)
			{
				super();
				this.coords = coords;
			}
			
			@Override
				public String getMessage()
			{
				return "Expression not constant or value has wrong type at " + coords.toString();
			}
		}
		
		
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e)
			throws NotEvaluatableException
		{
			return null;
		}
		
		private Object checkValue(ExprNode e, Class type) throws ValueException
		{
			if(e.isConst())
			{
				ConstNode c = (ConstNode)e.getConst();
				Object v = c.getValue();
				
				if(!type.isInstance(v)) throw new ValueException(e.getCoords());
				
				return v;
			}
			else throw new ValueException(e.getCoords());
		}
		
		protected Object getArgValue(ExprNode[] args, OperatorSignature op, int pos)  throws ValueException
		{
			TypeNode[] paramTypes = op.getOperandTypes();
			
			if(paramTypes[pos].isBasic())
			{
				BasicTypeNode paramType = (BasicTypeNode) paramTypes[pos];
				
				return checkValue(args[pos], paramType.getValueType());
			}
			else throw new ValueException(args[pos].getCoords());
		}
	}
	
	private static final Evaluator stringEvaluator = new Evaluator()
	{
		
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e) throws NotEvaluatableException
		{
			
			String a0, a1;
			
			try
			{
				a0 = (String) getArgValue(e, op, 0);
				a1 = (String) getArgValue(e, op, 1);
			}
			catch (ValueException x)
			{
				throw new NotEvaluatableException(coords);
			}
			
			if (op.id == ADD)
			{
				return new StringConstNode(coords, a0 + a1);
				
			}
			else
			{
				
				switch (op.id)
				{
					case EQ:
						return new BoolConstNode(coords, a0.equals(a1));
					case NE:
						return new BoolConstNode(coords, !a0.equals(a1));
					case GE:
						return new BoolConstNode(coords, a0.compareTo(a1) >= 0);
					case GT:
						return new BoolConstNode(coords, a0.compareTo(a1) > 0);
					case LE:
						return new BoolConstNode(coords, a0.compareTo(a1) <= 0);
					case LT:
						return new BoolConstNode(coords, a0.compareTo(a1) < 0);
					default:
						throw new NotEvaluatableException(coords);
				}
			}
		}
	};
	
	private static final Evaluator intEvaluator = new Evaluator()
	{
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e) throws NotEvaluatableException
		{
			
			int a0, a1;
			
			try
			{
				a0 = (Integer)getArgValue(e, op, 0);
				a1 = (Integer)getArgValue(e, op, 1);
			}
			catch (ValueException x)
			{
				throw new NotEvaluatableException(coords);
			}
			
			switch(op.id)
			{
				case EQ:
					return new BoolConstNode(coords, a0 == a1);
				case NE:
					return new BoolConstNode(coords, a0 != a1);
				case LT:
					return new BoolConstNode(coords, a0 < a1);
				case LE:
					return new BoolConstNode(coords, a0 <= a1);
				case GT:
					return new BoolConstNode(coords, a0 > a1);
				case GE:
					return new BoolConstNode(coords, a0 >= a1);
				case ADD:
					return new IntConstNode(coords, a0 + a1);
				case SUB:
					return new IntConstNode(coords, a0 - a1);
				case MUL:
					return new IntConstNode(coords, a0 * a1);
				case DIV:
					return new IntConstNode(coords, a0 / a1);
				case MOD:
					return new IntConstNode(coords, a0 % a1);
				case SHL:
					return new IntConstNode(coords, a0 << a1);
				case SHR:
					return new IntConstNode(coords, a0 >> a1);
				case BIT_SHR:
					return new IntConstNode(coords, a0 >>> a1);
				case BIT_OR:
					return new IntConstNode(coords, a0 | a1);
				case BIT_AND:
					return new IntConstNode(coords, a0 & a1);
				case BIT_XOR:
					return new IntConstNode(coords, a0 ^ a1);
				case NEG:
					return new IntConstNode(coords, -a0);
				case BIT_NOT:
					return new IntConstNode(coords, ~a0);
				default:
					throw new NotEvaluatableException(coords);
			}
		}
	};
	
	private static final Evaluator floatEvaluator = new Evaluator()
	{
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e) throws NotEvaluatableException
		{
			
			float a0, a1;
			
			try
			{
				a0 = (Float)getArgValue(e, op, 0);
				a1 = (Float)getArgValue(e, op, 1);
			}
			catch (ValueException x)
			{
				throw new NotEvaluatableException(coords);
			}
			
			switch(op.id)
			{
				case EQ:
					return new BoolConstNode(coords, a0 == a1);
				case NE:
					return new BoolConstNode(coords, a0 != a1);
				case LT:
					return new BoolConstNode(coords, a0 < a1);
				case LE:
					return new BoolConstNode(coords, a0 <= a1);
				case GT:
					return new BoolConstNode(coords, a0 > a1);
				case GE:
					return new BoolConstNode(coords, a0 >= a1);
				case ADD:
					return new FloatConstNode(coords, a0 + a1);
				case SUB:
					return new FloatConstNode(coords, a0 - a1);
				case MUL:
					return new FloatConstNode(coords, a0 * a1);
				case DIV:
					return new FloatConstNode(coords, a0 / a1);
				case MOD:
					return new FloatConstNode(coords, a0 % a1);
				default:
					throw new NotEvaluatableException(coords);
			}
		}
	};
	
	private static final Evaluator doubleEvaluator = new Evaluator()
	{
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e) throws NotEvaluatableException
		{
			
			double a0, a1;
			
			try
			{
				a0 = (Double)getArgValue(e, op, 0);
				a1 = (Double)getArgValue(e, op, 1);
			}
			catch (ValueException x)
			{
				throw new NotEvaluatableException(coords);
			}
			
			switch(op.id)
			{
				case EQ:
					return new BoolConstNode(coords, a0 == a1);
				case NE:
					return new BoolConstNode(coords, a0 != a1);
				case LT:
					return new BoolConstNode(coords, a0 < a1);
				case LE:
					return new BoolConstNode(coords, a0 <= a1);
				case GT:
					return new BoolConstNode(coords, a0 > a1);
				case GE:
					return new BoolConstNode(coords, a0 >= a1);
				case ADD:
					return new DoubleConstNode(coords, a0 + a1);
				case SUB:
					return new DoubleConstNode(coords, a0 - a1);
				case MUL:
					return new DoubleConstNode(coords, a0 * a1);
				case DIV:
					return new DoubleConstNode(coords, a0 / a1);
				case MOD:
					return new DoubleConstNode(coords, a0 % a1);
				default:
					throw new NotEvaluatableException(coords);
			}
		}
	};
	
	private static final Evaluator typeEvaluator = new Evaluator()
	{
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e) throws NotEvaluatableException
		{
			
			boolean is_node1, is_node2;
			
			if(e[0] instanceof TypeConstNode)
			{
				TypeNode type = (TypeNode)((TypeConstNode)e[0]).getValue();
				is_node1 = type instanceof NodeTypeNode;
			}
			else if (e[0] instanceof TypeofNode)
				{
					TypeNode type = (TypeNode)((TypeofNode)e[0]).getEntity().getDeclType();
					is_node1 = type instanceof NodeTypeNode;
				}
				else throw new NotEvaluatableException(coords);
			
			if(e[1] instanceof TypeConstNode)
			{
				TypeNode type = (TypeNode)((TypeConstNode)e[1]).getValue();
				is_node2 = type instanceof NodeTypeNode;
			}
			else if (e[0] instanceof TypeofNode)
				{
					TypeNode type = (TypeNode)((TypeofNode)e[1]).getEntity().getDeclType();
					is_node2 = type instanceof NodeTypeNode;
				}
				else throw new NotEvaluatableException(coords);
			
			if(is_node1 != is_node2)
			{
				error.warning(coords, "comparison between node and edge types will always fail");
				switch(op.id)
				{
					case EQ:
					case LT:
					case GT:
					case LE:
					case GE:
						return new BoolConstNode(coords, false);
					case NE:
						return new BoolConstNode(coords, true);
				}
			}
			throw new NotEvaluatableException(coords);
		}
	};
	
	private static final Evaluator booleanEvaluator = new Evaluator()
	{
		protected ExprNode eval(Coords coords, OperatorSignature op, ExprNode[] e) throws NotEvaluatableException
		{
			
			boolean a0, a1;
			
			try
			{
				a0 = (Boolean) getArgValue(e, op, 0);
				a1 = (Boolean) getArgValue(e, op, 1);
			}
			catch (ValueException x)
			{
				throw new NotEvaluatableException(coords);
			}
			
			switch(op.id)
			{
				case EQ:
					return new BoolConstNode(coords, a0 == a1);
				case NE:
					return new BoolConstNode(coords, a0 != a1);
				case LOG_AND:
					return new BoolConstNode(coords, a0 && a1);
				case LOG_OR:
					return new BoolConstNode(coords, a0 || a1);
				case LOG_NOT:
					return new BoolConstNode(coords, !a0);
				default:
					throw new NotEvaluatableException(coords);
			}
		}
	};
	
	private static final Evaluator condEvaluator = new Evaluator()
	{
		public ExprNode evaluate(ExprNode expr, OperatorSignature op, ExprNode[] args)
		{
			if(op.id == COND && args.length == op.getArity())
			{
				
				boolean v;
				
				try
				{
					v = (Boolean)getArgValue(args, op, 0);
				}
				catch (ValueException x)
				{
					return expr;
				}
				
				if(v)
				{
					return args[1];
				}
				else
				{
					return args[2];
				}
			}
			return expr;
		}
	};
	
	private static final Evaluator emptyEvaluator = new Evaluator();
	
	// Initialize the operators map.
	static
	{
		
		// String operators
		makeBinOp(EQ, BOOLEAN, STRING, STRING, stringEvaluator);
		makeBinOp(NE, BOOLEAN, STRING, STRING, stringEvaluator);
		makeBinOp(GE, BOOLEAN, STRING, STRING, stringEvaluator);
		makeBinOp(GT, BOOLEAN, STRING, STRING, stringEvaluator);
		makeBinOp(LE, BOOLEAN, STRING, STRING, stringEvaluator);
		makeBinOp(LT, BOOLEAN, STRING, STRING, stringEvaluator);
		
		// Integer comparison
		makeBinOp(EQ, BOOLEAN, INT, INT, intEvaluator);
		makeBinOp(NE, BOOLEAN, INT, INT, intEvaluator);
		makeBinOp(GE, BOOLEAN, INT, INT, intEvaluator);
		makeBinOp(GT, BOOLEAN, INT, INT, intEvaluator);
		makeBinOp(LE, BOOLEAN, INT, INT, intEvaluator);
		makeBinOp(LT, BOOLEAN, INT, INT, intEvaluator);
		
		// Float comparison
		makeBinOp(EQ, BOOLEAN, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(NE, BOOLEAN, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(GE, BOOLEAN, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(GT, BOOLEAN, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(LE, BOOLEAN, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(LT, BOOLEAN, FLOAT, FLOAT, floatEvaluator);
		
		// Double comparison
		makeBinOp(EQ, BOOLEAN, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(NE, BOOLEAN, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(GE, BOOLEAN, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(GT, BOOLEAN, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(LE, BOOLEAN, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(LT, BOOLEAN, DOUBLE, DOUBLE, doubleEvaluator);
		
		// Boolean operators
		makeBinOp(EQ, BOOLEAN, BOOLEAN, BOOLEAN, booleanEvaluator);
		makeBinOp(NE, BOOLEAN, BOOLEAN, BOOLEAN, booleanEvaluator);
		makeBinOp(LOG_AND, BOOLEAN, BOOLEAN, BOOLEAN, booleanEvaluator);
		makeBinOp(LOG_OR, BOOLEAN, BOOLEAN, BOOLEAN, booleanEvaluator);
		makeUnOp(LOG_NOT, BOOLEAN, BOOLEAN, booleanEvaluator);
		
		// Integer arithmetic
		makeBinOp(ADD, INT, INT, INT, intEvaluator);
		makeBinOp(SUB, INT, INT, INT, intEvaluator);
		makeBinOp(MUL, INT, INT, INT, intEvaluator);
		makeBinOp(DIV, INT, INT, INT, intEvaluator);
		makeBinOp(MOD, INT, INT, INT, intEvaluator);
		makeBinOp(SHL, INT, INT, INT, intEvaluator);
		makeBinOp(SHR, INT, INT, INT, intEvaluator);
		makeBinOp(BIT_SHR, INT, INT, INT, intEvaluator);
		makeBinOp(BIT_OR, INT, INT, INT, intEvaluator);
		makeBinOp(BIT_AND, INT, INT, INT, intEvaluator);
		makeBinOp(BIT_XOR, INT, INT, INT, intEvaluator);
		
		makeUnOp(NEG, INT, INT, intEvaluator);
		makeUnOp(BIT_NOT, INT, INT, intEvaluator);
		
		// Float arithmetic
		makeBinOp(ADD, FLOAT, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(SUB, FLOAT, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(MUL, FLOAT, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(DIV, FLOAT, FLOAT, FLOAT, floatEvaluator);
		makeBinOp(MOD, FLOAT, FLOAT, FLOAT, floatEvaluator);
		
		// Double arithmetic
		makeBinOp(ADD, DOUBLE, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(SUB, DOUBLE, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(MUL, DOUBLE, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(DIV, DOUBLE, DOUBLE, DOUBLE, doubleEvaluator);
		makeBinOp(MOD, DOUBLE, DOUBLE, DOUBLE, doubleEvaluator);

		// "String arithmetic"
		makeBinOp(ADD, STRING, STRING, STRING, stringEvaluator);
		makeBinOp(ADD, STRING, STRING, INT, stringEvaluator);
		makeBinOp(ADD, STRING, STRING, BOOLEAN, stringEvaluator);
		
		// Type comparison
		makeBinOp(EQ, BOOLEAN, TYPE, TYPE, typeEvaluator);
		makeBinOp(NE, BOOLEAN, TYPE, TYPE, typeEvaluator);
		makeBinOp(GE, BOOLEAN, TYPE, TYPE, typeEvaluator);
		makeBinOp(GT, BOOLEAN, TYPE, TYPE, typeEvaluator);
		makeBinOp(LE, BOOLEAN, TYPE, TYPE, typeEvaluator);
		makeBinOp(LT, BOOLEAN, TYPE, TYPE, typeEvaluator);
		
		// And of course the ternary COND operator
		makeOp(COND, INT, new TypeNode[] { BOOLEAN, INT, INT }, condEvaluator);
		makeOp(COND, STRING, new TypeNode[] { BOOLEAN, STRING, STRING }, condEvaluator);
		makeOp(COND, BOOLEAN, new TypeNode[] { BOOLEAN, BOOLEAN, BOOLEAN }, condEvaluator);
	}
	
	/**
	 * Get the arity of an operator.
	 * @param id The ID of the operator.
	 * @return The arity of the operator.
	 */
	protected static int getArity(int id)
	{
		return arities.get(new Integer(id)).intValue();
	}
	
	/**
	 * Get the name of an operator.
	 * @param id ID of the operator.
	 * @return The name of the operator.
	 */
	protected static String getName(int id)
	{
		return names.get(new Integer(id));
	}
	
	/**
	 * Check, if the given ID is a valid operator ID.
	 * @param id An operator ID.
	 * @return true, if the ID is a valid operator ID, false if not.
	 */
	private static boolean isValidId(int id)
	{
		return id >= 0 && id < OPERATORS;
	}
	
	/**
	 * Get the "nearest" operator for a given set of operand types.
	 * This method selects the operator that will provoke the least implicit
	 * type casts when used.
	 * @param id The operator id.
	 * @param opTypes The operands.
	 * @return The "nearest" operator.
	 */
	/**
	 * Method getNearest
	 *
	 * @param    id                  an int
	 * @param    opTypes             a  TypeNode[]
	 *
	 * @return   an OperatorSignature
	 *
	 */
	protected static OperatorSignature getNearest(int id, TypeNode[] opTypes)
	{
		Integer oid = new Integer(id);
		OperatorSignature res = INVALID;
		int nearest = Integer.MAX_VALUE;
		
		assert operators.get(oid) != null : "Operator \"" + getName(id)
			+ "\" must be registered";
		HashSet<OperatorSignature> opSet = operators.get(oid);
		
		for(Iterator<OperatorSignature> it = opSet.iterator(); it.hasNext();)
		{
			OperatorSignature op = it.next();
			int dist = op.getDistance(opTypes);
			
			debug.report(NOTE, "dist: " + dist + "\n signature: " + op);
			
			if(dist < nearest)
			{
				nearest = dist;
				res = op;
			}
		}
		
		debug.report(NOTE, "selected: " + res);
		
		return res;
	}
	
	/**
	 * An invalid operator signature.
	 */
	private static final OperatorSignature INVALID =
		new OperatorSignature(ERROR, BasicTypeNode.errorType,
							  new TypeNode[] { }, emptyEvaluator)
	{
		protected boolean isValid()
		{
			return false;
		}
	};
	
	/** id of the operator. */
	private int id;
	
	/** The evaluator for constant expressions for this operator. */
	private Evaluator evaluator;
	
	/**
	 * Make a new operator.
	 * This is used exclusively in this class, so it's private.
	 * @param id The operator id.
	 * @param resType The result type of the operator.
	 * @param opTypes The operand types.
	 * @param evaluator The evaluator for this operator signature.
	 */
	private OperatorSignature(int id, TypeNode resType,
							  TypeNode[] opTypes, Evaluator evaluator)
	{
		
		super(resType, opTypes);
		this.id = id;
		this.evaluator = evaluator;
		
		assert isValidId(id) : "need a valid operator id: " + id;
	}
	
	/**
	 * Evaluate an expression using this operator signature.
	 * @param expr The default result if the evaluation fails
	 * @param args The arguments for this operator.
	 * @return The computed value of the constant expression or a constant
	 * with {@link ConstNode.getInvalid()} that returns true.
	 */
	protected ExprNode evaluate(ArithmeticOpNode expr, ExprNode[] args)
	{
		return evaluator.evaluate(expr, this, args);
	}
	
	/**
	 * Check, if this signature is ok, not bad.
	 * @return true, if the signature is ok, false, if not.
	 */
	protected boolean isValid()
	{
		return true;
	}
	
	int getOpId()
	{
		return id;
	}
	
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String res = getResultType().toString() + " ";
		res += names.get(new Integer(id)) + "(";
		TypeNode[] opTypes = getOperandTypes();
		for(int i = 0; i < opTypes.length; i++)
		{
			res += (i == 0 ? "" : ",") + opTypes[i];
		}
		res += ")";
		return res;
	}
	
}
