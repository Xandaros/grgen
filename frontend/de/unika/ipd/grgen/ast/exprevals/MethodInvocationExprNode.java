/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.0
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * @author Moritz Kroll
 */

package de.unika.ipd.grgen.ast.exprevals;

import java.util.Collection;
import java.util.Vector;

import de.unika.ipd.grgen.ast.*;
import de.unika.ipd.grgen.ast.containers.*;
import de.unika.ipd.grgen.ir.IR;

public class MethodInvocationExprNode extends ExprNode
{
	static {
		setName(MethodInvocationExprNode.class, "method invocation expression");
	}

	static TypeNode methodTypeNode = new TypeNode() {
		public Collection<BaseNode> getChildren() {
			Vector<BaseNode> children = new Vector<BaseNode>();
			// no children
			return children;
		}

		public Collection<String> getChildrenNames() {
			Vector<String> childrenNames = new Vector<String>();
			// no children
			return childrenNames;
		}
	};

	private ExprNode targetExpr;
	private IdentNode methodIdent;
	private CollectNode<ExprNode> params;
	private ExprNode result;

	public MethodInvocationExprNode(ExprNode targetExpr, IdentNode methodIdent, CollectNode<ExprNode> params)
	{
		super(methodIdent.getCoords());
		this.targetExpr  = becomeParent(targetExpr);
		this.methodIdent = becomeParent(methodIdent);
		this.params      = becomeParent(params);
	}

	@Override
	public Collection<? extends BaseNode> getChildren() {
		Vector<BaseNode> children = new Vector<BaseNode>();
		children.add(targetExpr);
		//children.add(methodIdent);	// HACK: We don't have a declaration, so avoid failure during check phase
		children.add(params);
		if(isResolved())
			children.add(result);
		return children;
	}

	@Override
	public Collection<String> getChildrenNames() {
		Vector<String> childrenNames = new Vector<String>();
		childrenNames.add("targetExpr");
		//childrenNames.add("methodIdent");
		childrenNames.add("params");
		if(isResolved())
			childrenNames.add("result");
		return childrenNames;
	}

	protected boolean resolveLocal() {
		if(!targetExpr.resolve()) return false;

		String methodName = methodIdent.toString();
		TypeNode targetType = targetExpr.getType();

		if(targetType == BasicTypeNode.stringType) {
			if(methodName.equals("length")) {
				if(params.size() != 0) {
					reportError("string.length() does not take any parameters.");
					return false;
				}
				else
					result = new StringLengthNode(getCoords(), targetExpr);
			}
			else if(methodName.equals("substring")) {
  				if(params.size() != 2) {
  					reportError("string.substring(startIndex, length) takes two parameters.");
					return false;
				}
  				else
  					result = new StringSubstringNode(getCoords(), targetExpr, params.get(0), params.get(1));
  			}
  			else if(methodName.equals("indexOf")) {
  				if(params.size() != 1) {
  					reportError("string.indexOf(strToSearchFor) takes one parameter.");
					return false;
				}
  				else
  					result = new StringIndexOfNode(getCoords(), targetExpr, params.get(0));
  			}
  			else if(methodName.equals("lastIndexOf")) {
  				if(params.size() != 1) {
  					reportError("string.lastIndexOf(strToSearchFor) takes one parameter.");
					return false;
				}
  				else
  					result = new StringLastIndexOfNode(getCoords(), targetExpr, params.get(0));
  			}
  			else if(methodName.equals("replace")) {
  				if(params.size() != 3) {
  					reportError("string.replace(startIndex, length, replaceStr) takes three parameters.");
					return false;
				}
  				else
  					result = new StringReplaceNode(getCoords(), targetExpr, params.get(0), params.get(1), params.get(2));
  			}
  			else {
  				reportError("string does not have a method named \"" + methodName + "\"");
  				return false;
  			}
		}
		else if(targetType instanceof MapTypeNode) {
			if(methodName.equals("size")) {
  				if(params.size() != 0) {
  					reportError("map<S,T>.size() does not take any parameters.");
					return false;
				}
  				else
  					result = new MapSizeNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("empty")) {
  				if(params.size() != 0) {
  					reportError("map<S,T>.empty() does not take any parameters.");
					return false;
				}
  				else
  					result = new MapEmptyNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("domain")) {
  				if(params.size() != 0) {
  					reportError("map<S,T>.domain() does not take any parameters.");
					return false;
				}
  				else
  					result = new MapDomainNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("range")) {
  				if(params.size() != 0) {
  					reportError("map<S,T>.range() does not take any parameters.");
					return false;
				}
  				else
  					result = new MapRangeNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("peek")) {
				if(params.size() != 1) {
  					reportError("map<S,T>.peek(number in iteration sequence) takes one parameter.");
					return false;
				}
  				else
  					result = new MapPeekNode(getCoords(), targetExpr, params.get(0));
			}
  			else {
  				reportError("map<S,T> does not have a method named \"" + methodName + "\"");
  				return false;
  			}
		}
		else if(targetType instanceof SetTypeNode) {
			if(methodName.equals("size")) {
  				if(params.size() != 0) {
  					reportError("set<T>.size() does not take any parameters.");
					return false;
				}
  				else
  					result = new SetSizeNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("empty")) {
  				if(params.size() != 0) {
  					reportError("set<T>.empty() does not take any parameters.");
					return false;
				}
  				else
  					result = new SetEmptyNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("peek")) {
				if(params.size() != 1) {
  					reportError("set<T>.peek(number in iteration sequence) takes one parameter.");
					return false;
				}
  				else
  					result = new SetPeekNode(getCoords(), targetExpr, params.get(0));
			}
  			else {
  				reportError("set<T> does not have a method named \"" + methodName + "\"");
  				return false;
  			}
		}
		else if(targetType instanceof ArrayTypeNode) {
			if(methodName.equals("size")) {
  				if(params.size() != 0) {
  					reportError("array<T>.size() does not take any parameters.");
					return false;
				}
  				else
  					result = new ArraySizeNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("empty")) {
  				if(params.size() != 0) {
  					reportError("array<T>.empty() does not take any parameters.");
					return false;
				}
  				else
  					result = new ArrayEmptyNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("peek")) {
				if(params.size() != 0 && params.size() != 1) {
  					reportError("array<T>.peek(index) takes one parameter; or none parameter returning the value from the end.");
					return false;
				}
  				else {
  					if(params.size() == 0 )
  						result = new ArrayPeekNode(getCoords(), targetExpr);
  					else
  						result = new ArrayPeekNode(getCoords(), targetExpr, params.get(0));
  				}
			}
  			else if(methodName.equals("indexOf")) {
  				if(params.size() != 1) {
  					reportError("array<T>.indexOf(valueToSearchFor) takes one parameter.");
					return false;
				}
  				else
  					result = new ArrayIndexOfNode(getCoords(), targetExpr, params.get(0));
  			}
  			else if(methodName.equals("lastIndexOf")) {
  				if(params.size() != 1) {
  					reportError("array<T>.lastIndexOf(valueToSearchFor) takes one parameter.");
					return false;
				}
  				else
  					result = new ArrayLastIndexOfNode(getCoords(), targetExpr, params.get(0));
  			}
			else if(methodName.equals("subarray")) {
  				if(params.size() != 2) {
  					reportError("array<T>.subarray(startIndex, length) takes two parameters.");
					return false;
				}
  				else
  					result = new ArraySubarrayNode(getCoords(), targetExpr, params.get(0), params.get(1));
  			}
  			else {
  				reportError("array<T> does not have a method named \"" + methodName + "\"");
  				return false;
  			}
		}
		else if(targetType instanceof DequeTypeNode) {
			if(methodName.equals("size")) {
  				if(params.size() != 0) {
  					reportError("deque<T>.size() does not take any parameters.");
					return false;
				}
  				else
  					result = new DequeSizeNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("empty")) {
  				if(params.size() != 0) {
  					reportError("deque<T>.empty() does not take any parameters.");
					return false;
				}
  				else
  					result = new DequeEmptyNode(getCoords(), targetExpr);
  			}
			else if(methodName.equals("peek")) {
				if(params.size() != 0 && params.size() != 1) {
  					reportError("deque<T>.peek(index) takes one parameter; or none parameter returning the value from the begin.");
					return false;
				}
  				else {
  					if(params.size() == 0 )
  						result = new DequePeekNode(getCoords(), targetExpr);
  					else
  						result = new DequePeekNode(getCoords(), targetExpr, params.get(0));
  				}
			}
  			else if(methodName.equals("indexOf")) {
  				if(params.size() != 1) {
  					reportError("deque<T>.indexOf(valueToSearchFor) takes one parameter.");
					return false;
				}
  				else
  					result = new DequeIndexOfNode(getCoords(), targetExpr, params.get(0));
  			}
  			else if(methodName.equals("lastIndexOf")) {
  				if(params.size() != 1) {
  					reportError("deque<T>.lastIndexOf(valueToSearchFor) takes one parameter.");
					return false;
				}
  				else
  					result = new DequeLastIndexOfNode(getCoords(), targetExpr, params.get(0));
  			}
			else if(methodName.equals("subdeque")) {
  				if(params.size() != 2) {
  					reportError("deque<T>.subdeque(startIndex, length) takes two parameters.");
					return false;
				}
  				else
  					result = new DequeSubdequeNode(getCoords(), targetExpr, params.get(0), params.get(1));
  			}
  			else {
  				reportError("deque<T> does not have a method named \"" + methodName + "\"");
  				return false;
  			}
		}
		else if(targetType instanceof InheritanceTypeNode) {
			// METHOD-TODO
			result = new FunctionMethodOrExternalFunctionMethodInvocationExprNode(((IdentExprNode)targetExpr).getIdent(), methodIdent, params);
		}
		else {
			reportError(targetType.toString() + " does not have any methods");
			return false;
		}
		return true;
	}

	@Override
	protected boolean checkLocal() {
		return true;
	}

	@Override
	public TypeNode getType() {
		return result.getType();
	}

	protected ExprNode getResult() {
		return result;
	}

	@Override
	protected IR constructIR() {
		return result.getIR();
	}
}
