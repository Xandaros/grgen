/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.6
 * Copyright (C) 2003-2012 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */
 
/*
 * GrGen model and rule specification language grammar for ANTLR 3
 * @author Sebastian Hack, Daniel Grund, Rubino Geiss, Adam Szalkowski, Veit Batz, Edgar Jakumeit, Sebastian Buchwald, Moritz Kroll
 * @version $Id: base.g 20237 2008-06-24 15:59:24Z eja $
*/

grammar GrGen;

options {
	k = 2;
}

tokens {
	NUM_BYTE;
	NUM_SHORT;
	NUM_INTEGER;
	NUM_LONG;
	NUM_FLOAT;
	NUM_DOUBLE;
}

// todo: use scopes for the variables passed through numerous parsing rules as e.g. context
// should simplify grammar a good deal / eliminate a lot of explicit parameter passing
//scope Context {
//	int context;
//	PatternGraphNode directlyNestingLHSGraph;
//}
// todo: maybe user other features of antlr 3?

@lexer::header {
	package de.unika.ipd.grgen.parser.antlr;

	import java.io.File;
}

@lexer::members {
	GRParserEnvironment env;

	void setEnv(GRParserEnvironment env) {
		this.env = env;
	}
  
	// overriden for handling EOF of included file
	public Token nextToken() {
		Token token = super.nextToken();

		if(token==Token.EOF_TOKEN) {
			if(env.popFile(this)) {
				token = super.nextToken();
			}
		}

		// Skip first token after switching to another input.
		if(((CommonToken)token).getStartIndex() < 0) {
			token = this.nextToken();
		}
			
		return token;
	}
}

@header {
	package de.unika.ipd.grgen.parser.antlr;
	
	import java.util.Iterator;
	import java.util.LinkedList;
	import java.util.Collection;
	
	import java.io.File;

	import de.unika.ipd.grgen.parser.*;
	import de.unika.ipd.grgen.ast.*;
	import de.unika.ipd.grgen.ast.containers.*;
	import de.unika.ipd.grgen.util.*;
}

@members {
	boolean hadError = false;

	private static Map<Integer, Integer> opIds = new HashMap<Integer, Integer>();

	private static void putOpId(int tokenId, int opId) {
		opIds.put(new Integer(tokenId), new Integer(opId));
	}

	static {
		putOpId(QUESTION, OperatorSignature.COND);
		putOpId(EQUAL, OperatorSignature.EQ);
		putOpId(NOT_EQUAL, OperatorSignature.NE);
		putOpId(NOT, OperatorSignature.LOG_NOT);
		putOpId(TILDE, OperatorSignature.BIT_NOT);
		putOpId(SL, OperatorSignature.SHL);
		putOpId(SR, OperatorSignature.SHR);
		putOpId(BSR, OperatorSignature.BIT_SHR);
		putOpId(DIV, OperatorSignature.DIV);
		putOpId(STAR, OperatorSignature.MUL);
		putOpId(MOD, OperatorSignature.MOD);
		putOpId(PLUS, OperatorSignature.ADD);
		putOpId(MINUS, OperatorSignature.SUB);
		putOpId(GE, OperatorSignature.GE);
		putOpId(GT, OperatorSignature.GT);
		putOpId(LE, OperatorSignature.LE);
		putOpId(LT, OperatorSignature.LT);
		putOpId(BAND, OperatorSignature.BIT_AND);
		putOpId(BOR, OperatorSignature.BIT_OR);
		putOpId(BXOR, OperatorSignature.BIT_XOR);
		putOpId(BXOR, OperatorSignature.BIT_XOR);
		putOpId(LAND, OperatorSignature.LOG_AND);
		putOpId(LOR, OperatorSignature.LOG_OR);
		putOpId(IN, OperatorSignature.IN);
		putOpId(BACKSLASH, OperatorSignature.EXCEPT);
	};

	private OpNode makeOp(org.antlr.runtime.Token t) {
		Integer opId = opIds.get(new Integer(t.getType()));
		assert opId != null : "Invalid operator ID";
		return new ArithmeticOpNode(getCoords(t), opId.intValue());
	}

	private OpNode makeBinOp(org.antlr.runtime.Token t, ExprNode op0, ExprNode op1) {
		OpNode res = makeOp(t);
		res.addChild(op0);
		res.addChild(op1);
		return res;
	}

	private OpNode makeUnOp(org.antlr.runtime.Token t, ExprNode op) {
		OpNode res = makeOp(t);
		res.addChild(op);
		return res;
	}

	protected ParserEnvironment env;

	public void setEnv(ParserEnvironment env) {
		this.env = env;
	}

	protected Coords getCoords(org.antlr.runtime.Token tok) {
		return new Coords(tok);
	}

	protected final void reportError(de.unika.ipd.grgen.parser.Coords c, String s) {
		hadError = true;
		env.getSystem().getErrorReporter().error(c, s);
	}

	public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        reportError(new Coords(e), msg);
    }

	public void reportWarning(de.unika.ipd.grgen.parser.Coords c, String s) {
		env.getSystem().getErrorReporter().warning(c, s);
	}

	public boolean hadError() {
		return hadError;
	}

	public String getFilename() {
		return env.getFilename();
	}
}



////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Actions and Patterns
////////////////////////////////////////////////////////////////////////////////////////////////////////////////



/**
 * Build a main node.
 * It has a collect node with the decls as child
 */
textActions returns [ UnitNode main = null ]
	@init{
		CollectNode<ModelNode> modelChilds = new CollectNode<ModelNode>();
		CollectNode<IdentNode> patternChilds = new CollectNode<IdentNode>();
		CollectNode<IdentNode> actionChilds = new CollectNode<IdentNode>();
		CollectNode<IdentNode> sequenceChilds = new CollectNode<IdentNode>();
		String actionsName = Util.getActionsNameFromFilename(getFilename());
		if(!Util.isFilenameValidActionName(getFilename())) {
			reportError(new de.unika.ipd.grgen.parser.Coords(), "the filename "+getFilename()+" can't be used as action name, must be similar to an identifier");
		}
	}

	: (
		( a=ACTIONS i=IDENT
			{
				reportWarning(getCoords(a), "keyword \"actions\" is deprecated");
				reportWarning(getCoords(i),
					"the name of this actions component is not set by the identifier " +
					"after the \"actions\" keyword anymore but derived from the filename");
			}
			( usingDecl[modelChilds]
			| SEMI
			)
		)
	| usingDecl[modelChilds]
	)?

	( patternOrActionOrSequenceDecls[patternChilds, actionChilds, sequenceChilds] )? EOF
		{
			if(modelChilds.getChildren().size() == 0)
				modelChilds.addChild(env.getStdModel());
			else if(modelChilds.getChildren().size() > 1) {
				//
				// If more than one model is specified, generate a new graph model
				// using the name of the grg-file containing all given models.
				//
				IdentNode id = new IdentNode(env.define(ParserEnvironment.ENTITIES, actionsName,
					modelChilds.getCoords()));
				ModelNode model = new ModelNode(id, new CollectNode<IdentNode>(), new CollectNode<IdentNode>(), modelChilds);
				modelChilds = new CollectNode<ModelNode>();
				modelChilds.addChild(model);
			}
			main = new UnitNode(actionsName, getFilename(), env.getStdModel(), 
								modelChilds, patternChilds, actionChilds, sequenceChilds);
		}
	;

usingDecl [ CollectNode<ModelNode> modelChilds ]
	@init{ Collection<String> modelNames = new LinkedList<String>(); }

	: u=USING identList[modelNames] SEMI
		{
			modelChilds.setCoords(getCoords(u));
			for(Iterator<String> it = modelNames.iterator(); it.hasNext();)
			{
				String modelName = it.next();
				File modelFile = env.findModel(modelName);
				if ( modelFile == null ) {
					reportError(getCoords(u), "model \"" + modelName + "\" could not be found");
				} else {
					ModelNode model;
					model = env.parseModel(modelFile);
					modelChilds.addChild(model);
				}
			}
		}
	;

patternOrActionOrSequenceDecls[ CollectNode<IdentNode> patternChilds, CollectNode<IdentNode> actionChilds, CollectNode<IdentNode> sequenceChilds ]
	@init{ mod = 0; }

	: ( mod=patternModifiers patternOrActionOrSequenceDecl[patternChilds, actionChilds, sequenceChilds, mod] )+
	;

patternModifiers returns [ int res = 0 ]
	: ( m=patternModifier[ res ]  { res = m; } )*
	;

patternModifier [ int mod ] returns [ int res = 0 ]
	: modifier=INDUCED
		{
			if((mod & PatternGraphNode.MOD_INDUCED)!=0) {
				reportError(getCoords(modifier), "\"induced\" modifier already declared");
			}
			res = mod | PatternGraphNode.MOD_INDUCED;
		}
	| modifier=EXACT
		{		
			if((mod & PatternGraphNode.MOD_EXACT)!=0) {
				reportError(getCoords(modifier), "\"exact\" modifier already declared");
			}
			res = mod | PatternGraphNode.MOD_EXACT;
		}
	| modifier=IDENT 
		{
			if(modifier.getText().equals("dpo")) {
				if((mod & PatternGraphNode.MOD_DANGLING)!=0 || (mod & PatternGraphNode.MOD_IDENTIFICATION)!=0) {
					reportError(getCoords(modifier), "\"dpo\" or \"dangling\" or \"identification\" modifier dangling already declared");
				}
				res = mod | PatternGraphNode.MOD_DANGLING | PatternGraphNode.MOD_IDENTIFICATION;
			} else if(modifier.getText().equals("dangling")) {
				if((mod & PatternGraphNode.MOD_DANGLING)!=0) {
					reportError(getCoords(modifier), "\"dangling\" modifier already declared");
				}
				res = mod | PatternGraphNode.MOD_DANGLING;
			} else if(modifier.getText().equals("identification")) {
				if((mod & PatternGraphNode.MOD_IDENTIFICATION)!=0) {
					reportError(getCoords(modifier), "\"identification\" modifier already declared");
				}
				res = mod | PatternGraphNode.MOD_IDENTIFICATION;
			} else {
				reportError(getCoords(modifier), "unknown modifier "+modifier.getText());
			}
		}
	;

patternOrActionOrSequenceDecl [ CollectNode<IdentNode> patternChilds, CollectNode<IdentNode> actionChilds, CollectNode<IdentNode> sequenceChilds, int mod ]
	@init{
		CollectNode<IdentNode> dels = new CollectNode<IdentNode>();
		CollectNode<RhsDeclNode> rightHandSides = new CollectNode<RhsDeclNode>();
		CollectNode<BaseNode> modifyParams = new CollectNode<BaseNode>();
		ExecNode exec = null;
		AnonymousPatternNamer namer = new AnonymousPatternNamer(env);
		TestDeclNode actionDecl = null;
	}

	: t=TEST id=actionIdentDecl pushScope[id] params=parameters[BaseNode.CONTEXT_TEST|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_LHS|BaseNode.CONTEXT_PARAMETER, null] 
		ret=returnTypes LBRACE
		left=patternPart[getCoords(t), params, namer, mod, BaseNode.CONTEXT_TEST|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_LHS, id.toString()]
			{
				actionDecl = new TestDeclNode(id, left, ret);
				id.setDecl(actionDecl);
				actionChilds.addChild(id);
			}
		RBRACE popScope
		{
			if((mod & PatternGraphNode.MOD_DANGLING)!=0 || (mod & PatternGraphNode.MOD_IDENTIFICATION)!=0) {
				reportError(getCoords(t), "no \"dpo\" or \"dangling\" or \"identification\" modifier allowed for test");
			}
		}
		externalFilters[id, actionDecl]
	| r=RULE id=actionIdentDecl pushScope[id] params=parameters[BaseNode.CONTEXT_RULE|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_LHS|BaseNode.CONTEXT_PARAMETER, null]
		ret=returnTypes LBRACE
		left=patternPart[getCoords(r), params, namer, mod, BaseNode.CONTEXT_RULE|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_LHS, id.toString()]
		( rightReplace=replacePart[new CollectNode<BaseNode>(), BaseNode.CONTEXT_RULE|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_RHS, id, left]
			{
				actionDecl = new RuleDeclNode(id, left, rightReplace, ret);
				id.setDecl(actionDecl);
				actionChilds.addChild(id);
			}
		| rightModify=modifyPart[dels, new CollectNode<BaseNode>(), BaseNode.CONTEXT_RULE|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_RHS, id, left]
			{
				actionDecl = new RuleDeclNode(id, left, rightModify, ret);
				id.setDecl(actionDecl);
				actionChilds.addChild(id);
			}
		| emptyRightModify=emptyModifyPart[getCoords(r), dels, new CollectNode<BaseNode>(), BaseNode.CONTEXT_RULE|BaseNode.CONTEXT_ACTION|BaseNode.CONTEXT_RHS, id, left]
			{
				actionDecl = new RuleDeclNode(id, left, emptyRightModify, ret);
				id.setDecl(actionDecl);
				actionChilds.addChild(id);
			}
		)
		RBRACE popScope
		externalFilters[id, actionDecl]
	| p=PATTERN id=patIdentDecl pushScope[id] params=patternParameters[BaseNode.CONTEXT_PATTERN|BaseNode.CONTEXT_LHS|BaseNode.CONTEXT_PARAMETER, null] 
		((MODIFY|REPLACE) mp=patternParameters[BaseNode.CONTEXT_PATTERN|BaseNode.CONTEXT_RHS|BaseNode.CONTEXT_PARAMETER, null] { modifyParams = mp; })?
		LBRACE
		left=patternPart[getCoords(p), params, namer, mod, BaseNode.CONTEXT_PATTERN|BaseNode.CONTEXT_LHS, id.toString()]
		( rightReplace=replacePart[modifyParams, BaseNode.CONTEXT_PATTERN|BaseNode.CONTEXT_RHS, id, left]
			{
				rightHandSides.addChild(rightReplace);
			}
		| rightModify=modifyPart[dels, modifyParams, BaseNode.CONTEXT_PATTERN|BaseNode.CONTEXT_RHS, id, left]
			{
				rightHandSides.addChild(rightModify);
			}
		)*
			{
				id.setDecl(new SubpatternDeclNode(id, left, rightHandSides));
				patternChilds.addChild(id);
			}
		RBRACE popScope
	| s=SEQUENCE id=actionIdentDecl pushScope[id] { exec = new ExecNode(getCoords(s)); }
		inParams=execInParameters[exec] outParams=execOutParameters[exec]
		(LBRACE 
			xgrs[exec]
		RBRACE
		| SEMI // no body? -> external sequence (externally implemented)
		) popScope
		{
			id.setDecl(new SequenceDeclNode(id, exec, inParams, outParams));
			sequenceChilds.addChild(id);
		}
	;

parameters [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ CollectNode<BaseNode> res = new CollectNode<BaseNode>() ]
	: LPAREN (paramList[res, context, directlyNestingLHSGraph])? RPAREN
	|
	;

patternParameters [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ CollectNode<BaseNode> res = new CollectNode<BaseNode>() ]
	: LPAREN (patternParamList[res, context, directlyNestingLHSGraph])? RPAREN
	|
	;

paramList [ CollectNode<BaseNode> params, int context, PatternGraphNode directlyNestingLHSGraph ]
	: p=param[context, directlyNestingLHSGraph] { params.addChild(p); }
		( COMMA p=param[context, directlyNestingLHSGraph] { params.addChild(p); } )*
	;

patternParamList [ CollectNode<BaseNode> params, int context, PatternGraphNode directlyNestingLHSGraph ]
	: (p=param[context, directlyNestingLHSGraph] { params.addChild(p); } | dp=defEntityToBeYieldedTo[null, null, context, directlyNestingLHSGraph] { params.addChild(dp); })
		( COMMA (p=param[context, directlyNestingLHSGraph] { params.addChild(p); } | dp=defEntityToBeYieldedTo[null, null, context, directlyNestingLHSGraph] { params.addChild(dp); }) )*
	;

param [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	: MINUS edge=edgeDeclParam[context, directlyNestingLHSGraph] direction = forwardOrUndirectedEdgeParam
		{
			BaseNode dummy = env.getDummyNodeDecl(context, directlyNestingLHSGraph);
			res = new ConnectionNode(dummy, edge, dummy, direction, ConnectionNode.NO_REDIRECTION);
		}
	| LARROW edge=edgeDeclParam[context, directlyNestingLHSGraph] RARROW
		{
			BaseNode dummy = env.getDummyNodeDecl(context, directlyNestingLHSGraph);
			res = new ConnectionNode(dummy, edge, dummy, ConnectionNode.ARBITRARY_DIRECTED, ConnectionNode.NO_REDIRECTION);
		}
	| QUESTIONMINUS edge=edgeDeclParam[context, directlyNestingLHSGraph] MINUSQUESTION
		{
			BaseNode dummy = env.getDummyNodeDecl(context, directlyNestingLHSGraph);
			res = new ConnectionNode(dummy, edge, dummy, ConnectionNode.ARBITRARY, ConnectionNode.NO_REDIRECTION);
		}
	| v=varDecl[context, directlyNestingLHSGraph] 
		{
			res = v;
		}
	| node=nodeDeclParam[context, directlyNestingLHSGraph]
		{
			res = new SingleNodeConnNode(node);
		}
	;

forwardOrUndirectedEdgeParam returns [ int res = ConnectionNode.ARBITRARY ]
	: RARROW { res = ConnectionNode.DIRECTED; }
	| MINUS  { res = ConnectionNode.UNDIRECTED; }
	;

returnTypes returns [ CollectNode<BaseNode> res = new CollectNode<BaseNode>() ]
	: COLON LPAREN (returnTypeList[res])? RPAREN
	|
	;

returnTypeList [ CollectNode<BaseNode> returnTypes ]
	: t=returnType { returnTypes.addChild(t); } ( COMMA t=returnType { returnTypes.addChild(t); } )*
	;

returnType returns [ BaseNode res = env.initNode() ]
	:	type=typeIdentUse
		{
			res = type;
		}
	|
		MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse GT
		{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
			res = MapTypeNode.getMapType(keyType, valueType);
		}
	|
		SET LT keyType=typeIdentUse GT
		{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
			res = SetTypeNode.getSetType(keyType);
		}
	|
		ARRAY LT keyType=typeIdentUse GT
		{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
			res = ArrayTypeNode.getArrayType(keyType);
		}
	|
		QUEUE LT keyType=typeIdentUse GT
		{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
			res = QueueTypeNode.getQueueType(keyType);
		}
	;

externalFilters [ IdentNode actionIdent, TestDeclNode actionDecl ]
	@init {
		ArrayList<String> extFilters = new ArrayList<String>();
		actionDecl.addFilters(extFilters);
	}
	: BACKSLASH externalFilterList[actionIdent, extFilters]
	|
	;

externalFilterList [ IdentNode actionIdent, ArrayList<String> extFilters ]
	: (id=actionIdentDecl { extFilters.add(id.getSymbol().getText()); id.setDecl(new FilterDeclNode(id, actionIdent)); } 
		| AUTO { extFilters.add("auto"); }
		)
		( COMMA id=actionIdentDecl { extFilters.add(id.getSymbol().getText()); id.setDecl(new FilterDeclNode(id, actionIdent)); } 
		| COMMA AUTO { extFilters.add("auto"); }
		)*
	;

patternPart [ Coords pattern_coords, CollectNode<BaseNode> params, AnonymousPatternNamer namer, int mod,
			int context, String nameOfGraph ]
			returns [ PatternGraphNode res = null ]
	: p=PATTERN LBRACE
		n=patternBody[getCoords(p), params, namer, mod, context, nameOfGraph] { res = n; }
		RBRACE
			{ reportWarning(getCoords(p), "separate pattern part deprecated, just merge content directly into rule/test-body"); }
	| n=patternBody[pattern_coords, params, namer, mod, context, nameOfGraph] { res = n; }
	;

replacePart [ CollectNode<BaseNode> params, 
			int context, IdentNode nameOfRHS, PatternGraphNode directlyNestingLHSGraph ]
			returns [ ReplaceDeclNode res = null ]
	: r=REPLACE ( id=rhsIdentDecl { nameOfRHS = id; } )?
		LBRACE
		b=replaceBody[getCoords(r), params, context, nameOfRHS, directlyNestingLHSGraph] { res = b; }
		RBRACE
	| LBRACEMINUS 
		{ params = new CollectNode<BaseNode>(); }
		b=replaceBody[getCoords(r), params, context, nameOfRHS, directlyNestingLHSGraph] { res = b; }
	  RBRACE
	;

modifyPart [ CollectNode<IdentNode> dels, CollectNode<BaseNode> params,
			int context, IdentNode nameOfRHS, PatternGraphNode directlyNestingLHSGraph ]
			returns [ ModifyDeclNode res = null ]
	: m=MODIFY ( id=rhsIdentDecl { nameOfRHS = id; } )?
		LBRACE
		b=modifyBody[getCoords(m), dels, params, context, nameOfRHS, directlyNestingLHSGraph] { res = b; }
		RBRACE
	| LBRACEPLUS 
		{ params = new CollectNode<BaseNode>(); }
		b=modifyBody[getCoords(m), dels, params, context, nameOfRHS, directlyNestingLHSGraph] { res = b; }
	  RBRACE
	;

emptyModifyPart [ Coords coords, CollectNode<IdentNode> dels, CollectNode<BaseNode> params,
				int context, IdentNode nameOfRHS, PatternGraphNode directlyNestingLHSGraph ]
				returns [ ModifyDeclNode res = null ]
	@init{
		CollectNode<BaseNode> connections = new CollectNode<BaseNode>();
		CollectNode<VarDeclNode> defVariablesToBeYieldedTo = new CollectNode<VarDeclNode>();
		CollectNode<SubpatternUsageNode> subpatterns = new CollectNode<SubpatternUsageNode>();
		CollectNode<OrderedReplacementNode> orderedReplacements = new CollectNode<OrderedReplacementNode>();
		CollectNode<EvalStatementNode> evals = new CollectNode<EvalStatementNode>();
		CollectNode<ExprNode> returnz = new CollectNode<ExprNode>();
		CollectNode<BaseNode> imperativeStmts = new CollectNode<BaseNode>();
		GraphNode graph = new GraphNode(nameOfRHS.toString(), coords, 
			connections, params, defVariablesToBeYieldedTo, subpatterns,
			orderedReplacements, evals, returnz, imperativeStmts,
			context, directlyNestingLHSGraph);
		res = new ModifyDeclNode(nameOfRHS, graph, dels);
	}
	: 
	;

patternBody [ Coords coords, CollectNode<BaseNode> params, AnonymousPatternNamer namer, int mod, int context, String nameOfGraph ] returns [ PatternGraphNode res = null ]
	@init{
		CollectNode<BaseNode> connections = new CollectNode<BaseNode>();
		CollectNode<VarDeclNode> defVariablesToBeYieldedTo = new CollectNode<VarDeclNode>();
		CollectNode<SubpatternUsageNode> subpatterns = new CollectNode<SubpatternUsageNode>();
		CollectNode<OrderedReplacementNode> orderedReplacements = new CollectNode<OrderedReplacementNode>();
		CollectNode<AlternativeNode> alts = new CollectNode<AlternativeNode>();
		CollectNode<IteratedNode> iters = new CollectNode<IteratedNode>();
		CollectNode<PatternGraphNode> negs = new CollectNode<PatternGraphNode>();
		CollectNode<PatternGraphNode> idpts = new CollectNode<PatternGraphNode>();
		CollectNode<ExprNode> conds = new CollectNode<ExprNode>();
		CollectNode<EvalStatementNode> evals = new CollectNode<EvalStatementNode>();
		CollectNode<ExprNode> returnz = new CollectNode<ExprNode>();
		CollectNode<HomNode> homs = new CollectNode<HomNode>();
		CollectNode<TotallyHomNode> totallyhoms = new CollectNode<TotallyHomNode>();
		CollectNode<ExactNode> exact = new CollectNode<ExactNode>();
		CollectNode<InducedNode> induced = new CollectNode<InducedNode>();
		res = new PatternGraphNode(nameOfGraph, coords, 
				connections, params, defVariablesToBeYieldedTo, subpatterns, orderedReplacements, 
				alts, iters, negs, idpts, conds, evals,
				returnz, homs, totallyhoms, exact, induced, mod, context);
	}

	: ( patternStmt[connections, defVariablesToBeYieldedTo, subpatterns, orderedReplacements,
			alts, iters, negs, idpts, namer, conds, evals,
			returnz, homs, totallyhoms, exact, induced, context, res] )*
	;

patternStmt [ CollectNode<BaseNode> conn, CollectNode<VarDeclNode> defVariablesToBeYieldedTo,
			CollectNode<SubpatternUsageNode> subpatterns, CollectNode<OrderedReplacementNode> orderedReplacements,
			CollectNode<AlternativeNode> alts, CollectNode<IteratedNode> iters, CollectNode<PatternGraphNode> negs,
			CollectNode<PatternGraphNode> idpts, AnonymousPatternNamer namer, CollectNode<ExprNode> conds, CollectNode<EvalStatementNode> evals,
			CollectNode<ExprNode> returnz, CollectNode<HomNode> homs, CollectNode<TotallyHomNode> totallyhoms,
			CollectNode<ExactNode> exact, CollectNode<InducedNode> induced,
			int context, PatternGraphNode directlyNestingLHSGraph]
	: connectionsOrSubpattern[conn, defVariablesToBeYieldedTo, subpatterns, orderedReplacements, context, directlyNestingLHSGraph] SEMI
	| (iterated[AnonymousPatternNamer.getDummyNamer(), 0]) => iter=iterated[namer, context] { iters.addChild(iter); } // must scan ahead to end of () to see if *,+,?,[ is following in order to distinguish from one-case alternative ()
	| alt=alternative[namer, context] { alts.addChild(alt); }
	| neg=negative[namer, context] { negs.addChild(neg); }
	| idpt=independent[namer, context] { idpts.addChild(idpt); }
	| condition[conds]
	| yielding[evals, context, directlyNestingLHSGraph]
	| rets[returnz, context] SEMI
	| hom=homStatement { homs.addChild(hom); } SEMI
	| totallyhom=totallyHomStatement { totallyhoms.addChild(totallyhom); } SEMI
	| exa=exactStatement { exact.addChild(exa); } SEMI
	| ind=inducedStatement { induced.addChild(ind); } SEMI
	;

connectionsOrSubpattern [ CollectNode<BaseNode> conn, CollectNode<VarDeclNode> defVariablesToBeYieldedTo, 
						CollectNode<SubpatternUsageNode> subpatterns, CollectNode<OrderedReplacementNode> orderedReplacements,
						int context, PatternGraphNode directlyNestingLHSGraph ]
	: firstEdge[conn, context, directlyNestingLHSGraph] // connection starts with an edge which dangles on the left
	| firstNodeOrSubpattern[conn, subpatterns, orderedReplacements, context, directlyNestingLHSGraph] // there's a subpattern or a connection that starts with a node
	| defEntityToBeYieldedTo[conn, defVariablesToBeYieldedTo, context, directlyNestingLHSGraph] // single entity definitions to be filled by later yield assignments
	;

firstEdge [ CollectNode<BaseNode> conn, int context, PatternGraphNode directlyNestingLHSGraph ]
	@init{
		boolean forward = true;
		MutableInteger direction = new MutableInteger(ConnectionNode.ARBITRARY);
		MutableInteger redirection = new MutableInteger(ConnectionNode.NO_REDIRECTION);
	}

	:   ( e=forwardOrUndirectedEdgeOcc[context, direction, redirection, directlyNestingLHSGraph] { forward=true; } // get first edge
		| e=backwardOrArbitraryDirectedEdgeOcc[context, direction, redirection, directlyNestingLHSGraph] { forward=false; }
		| e=arbitraryEdgeOcc[context, directlyNestingLHSGraph] { forward=false; direction.setValue(ConnectionNode.ARBITRARY);}
		)
		nodeContinuation[e, env.getDummyNodeDecl(context, directlyNestingLHSGraph), forward, direction, redirection, conn, context, directlyNestingLHSGraph] // and continue looking for node
	;

firstNodeOrSubpattern [ CollectNode<BaseNode> conn, CollectNode<SubpatternUsageNode> subpatterns, CollectNode<OrderedReplacementNode> orderedReplacements, int context, PatternGraphNode directlyNestingLHSGraph ]
	@init{
		id = env.getDummyIdent();
		type = env.getNodeRoot();
		constr = TypeExprNode.getEmpty();
		annots = env.getEmptyAnnotations();
		boolean hasAnnots = false;
		CollectNode<ExprNode> subpatternConn = new CollectNode<ExprNode>();
		CollectNode<ExprNode> subpatternReplConn = new CollectNode<ExprNode>();
		curId = env.getDummyIdent();
		CollectNode<IdentNode> mergees = new CollectNode<IdentNode>();
		BaseNode n = null;
	}

	: id=entIdentUse firstEdgeContinuation[id, conn, context, directlyNestingLHSGraph] // use of already declared node, continue looking for first edge
	| id=entIdentUse l=LPAREN arguments[subpatternReplConn] RPAREN // use of already declared subpattern
		{ orderedReplacements.addChild(new SubpatternReplNode(id, subpatternReplConn)); }
	| id=entIdentDecl cc=COLON // node or subpattern declaration
		( // node declaration
			type=typeIdentUse
			( constr=typeConstraint )?
			( 
				{
					n = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
				}
			| LT oldid=entIdentUse (COMMA curId=entIdentUse { mergees.addChild(curId); })* GT
				{
					n = new NodeTypeChangeNode(id, type, context, oldid, mergees, directlyNestingLHSGraph);
				}
			| LBRACE oldid=entIdentUse (d=DOT attr=entIdentUse)? (LBRACK mapAccess=entIdentUse RBRACK)? RBRACE
				{
					if(mapAccess==null)
						n = new MatchNodeFromStorageNode(id, type, context, 
							attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), directlyNestingLHSGraph);
					else
						n = new MatchNodeByStorageAccessNode(id, type, context, 
							attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), new IdentExprNode(mapAccess), directlyNestingLHSGraph);
				}
			)
			firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
		| // node typeof declaration
			TYPEOF LPAREN type=entIdentUse RPAREN
			( constr=typeConstraint )?
			( LT oldid=entIdentUse (COMMA curId=entIdentUse { mergees.addChild(curId); })* GT )?
			{
				if(oldid==null) {
					n = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
				} else {
					n = new NodeTypeChangeNode(id, type, context, oldid, mergees, directlyNestingLHSGraph);
				}
			}
			firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
		| // node copy declaration
			COPY LT type=entIdentUse GT 
			{
				n = new NodeDeclNode(id, type, true, context, constr, directlyNestingLHSGraph);
			}
			firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
		| // subpattern declaration
			type=patIdentUse LPAREN arguments[subpatternConn] RPAREN
			{ subpatterns.addChild(new SubpatternUsageNode(id, type, context, subpatternConn)); }
		)
	| ( annots=annotations { hasAnnots = true; } )?
		c=COLON // anonymous node or subpattern declaration
			( // node declaration
				{ id = env.defineAnonymousEntity("node", getCoords(c)); }
				type=typeIdentUse
				( constr=typeConstraint )?
				(
					{
						n = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
					}
				| LT oldid=entIdentUse (COMMA curId=entIdentUse { mergees.addChild(curId); })* GT
					{
						n = new NodeTypeChangeNode(id, type, context, oldid, mergees, directlyNestingLHSGraph);
					}
				| LBRACE oldid=entIdentUse (d=DOT attr=entIdentUse)? (LBRACK mapAccess=entIdentUse RBRACK)? RBRACE
					{
						if(mapAccess==null)
							n = new MatchNodeFromStorageNode(id, type, context, 
								attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), directlyNestingLHSGraph);
						else
							n = new MatchNodeByStorageAccessNode(id, type, context,
								attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), new IdentExprNode(mapAccess), directlyNestingLHSGraph);
					}
				)
				firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
			| // node typeof declaration
				{ id = env.defineAnonymousEntity("node", getCoords(c)); }
				TYPEOF LPAREN type=entIdentUse RPAREN
				( constr=typeConstraint )?
				( LT oldid=entIdentUse (COMMA curId=entIdentUse { mergees.addChild(curId); })* GT )?
				{
					if(oldid==null) {
						n = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
					} else {
						n = new NodeTypeChangeNode(id, type, context, oldid, mergees, directlyNestingLHSGraph);
					}
				}
				firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
			| // node copy declaration
				COPY LT type=entIdentUse GT 
				{
					n = new NodeDeclNode(id, type, true, context, constr, directlyNestingLHSGraph);
				}
				firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
			| // subpattern declaration
				{ id = env.defineAnonymousEntity("sub", getCoords(c)); }
				type=patIdentUse LPAREN arguments[subpatternConn] RPAREN
				{ subpatterns.addChild(new SubpatternUsageNode(id, type, context, subpatternConn)); }
			)
			{ if (hasAnnots) { id.setAnnotations(annots); } }
	| d=DOT // anonymous node declaration of type node
		{ id = env.defineAnonymousEntity("node", getCoords(d)); }
		( annots=annotations { id.setAnnotations(annots); } )?
		{ n = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph); }
		firstEdgeContinuation[n, conn, context, directlyNestingLHSGraph] // and continue looking for first edge
	;

defEntityToBeYieldedTo [ CollectNode<BaseNode> connections, CollectNode<VarDeclNode> defVariablesToBeYieldedTo,
						int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	: DEF (
		MINUS edge=defEdgeToBeYieldedTo[context, directlyNestingLHSGraph] direction = forwardOrUndirectedEdgeParam
			{
				BaseNode dummy = env.getDummyNodeDecl(context, directlyNestingLHSGraph);
				res = new ConnectionNode(dummy, edge, dummy, direction, ConnectionNode.NO_REDIRECTION);
				if(connections!=null) connections.addChild(res);
			}
		| LARROW edge=defEdgeToBeYieldedTo[context, directlyNestingLHSGraph] RARROW
			{
				BaseNode dummy = env.getDummyNodeDecl(context, directlyNestingLHSGraph);
				res = new ConnectionNode(dummy, edge, dummy, ConnectionNode.ARBITRARY_DIRECTED, ConnectionNode.NO_REDIRECTION);
				if(connections!=null) connections.addChild(res);
			}
		| QUESTIONMINUS edge=defEdgeToBeYieldedTo[context, directlyNestingLHSGraph] MINUSQUESTION
			{
				BaseNode dummy = env.getDummyNodeDecl(context, directlyNestingLHSGraph);
				res = new ConnectionNode(dummy, edge, dummy, ConnectionNode.ARBITRARY, ConnectionNode.NO_REDIRECTION);
				if(connections!=null) connections.addChild(res);
			}
		| v=defVarDeclToBeYieldedTo[context, directlyNestingLHSGraph]
			{
				res = v;
				if(defVariablesToBeYieldedTo!=null) defVariablesToBeYieldedTo.addChild(v);
			}
		| node=defNodeToBeYieldedTo[context, directlyNestingLHSGraph]
			{
				res = new SingleNodeConnNode(node);
				if(connections!=null) connections.addChild(res);
			}
		)
	;

defNodeToBeYieldedTo[ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ NodeDeclNode res = null ]
	: id=entIdentDecl COLON type=typeIdentUse
		{ res = new NodeDeclNode(id, type, false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph, false, true); }
	;

defEdgeToBeYieldedTo [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ EdgeDeclNode res = null ]
	: id=entIdentDecl COLON type=typeIdentUse
		{ res = new EdgeDeclNode(id, type, false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph, false, true); }
	;
	
defVarDeclToBeYieldedTo [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ VarDeclNode res = env.initVarNode(directlyNestingLHSGraph, context) ]
	@init{ VarDeclNode var = null; }
	: paramModifier=IDENT id=entIdentDecl COLON
		(
			type=typeIdentUse
			{
				var = new VarDeclNode(id, type, directlyNestingLHSGraph, context, true);
				if(!paramModifier.getText().equals("var")) 
					{ reportError(getCoords(paramModifier), "var keyword needed before non graph element and non container parameter"); }
			}
		|
			MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				var = new VarDeclNode(id, MapTypeNode.getMapType(keyType, valueType), directlyNestingLHSGraph, context, true);
				if(!paramModifier.getText().equals("ref"))
					{ reportError(getCoords(paramModifier), "ref keyword needed before map typed parameter"); }
			}
		|
			SET LT keyType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				var = new VarDeclNode(id, SetTypeNode.getSetType(keyType), directlyNestingLHSGraph, context, true);
				if(!paramModifier.getText().equals("ref")) 
					{ reportError(getCoords(paramModifier), "ref keyword needed before set typed parameter"); }
			}
		|
			ARRAY LT keyType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				var = new VarDeclNode(id, ArrayTypeNode.getArrayType(keyType), directlyNestingLHSGraph, context, true);
				if(!paramModifier.getText().equals("ref")) 
					{ reportError(getCoords(paramModifier), "ref keyword needed before array typed parameter"); }
			}
		|
			QUEUE LT keyType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				var = new VarDeclNode(id, QueueTypeNode.getQueueType(keyType), directlyNestingLHSGraph, context, true);
				if(!paramModifier.getText().equals("ref")) 
					{ reportError(getCoords(paramModifier), "ref keyword needed before queue typed parameter"); }
			}
		)
		{ if(var!=null) res = var; }
		(a=ASSIGN e=expr[false] { if(var!=null) var.setInitialization(e); } )? 
	;

nodeContinuation [ BaseNode e, BaseNode n1, boolean forward, MutableInteger direction, MutableInteger redirection, CollectNode<BaseNode> conn, int context, PatternGraphNode directlyNestingLHSGraph ]
	@init{ n2 = env.getDummyNodeDecl(context, directlyNestingLHSGraph); }

	: n2=nodeOcc[context, directlyNestingLHSGraph] // node following - get it and build connection with it, then continue with looking for follwing edge
		{
			if (direction.getValue() == ConnectionNode.DIRECTED && !forward) {
				conn.addChild(new ConnectionNode(n2, e, n1, direction.getValue(), redirection.getValue()));
			} else {
				conn.addChild(new ConnectionNode(n1, e, n2, direction.getValue(), redirection.getValue()));
			}
		}
		edgeContinuation[n2, conn, context, directlyNestingLHSGraph]
	|   // nothing following - build connection with edge dangeling on the right (see n2 initialization)
		{
			if (direction.getValue() == ConnectionNode.DIRECTED && !forward) {
				conn.addChild(new ConnectionNode(n2, e, n1, direction.getValue(), redirection.getValue()));
			} else {
				conn.addChild(new ConnectionNode(n1, e, n2, direction.getValue(), redirection.getValue()));
			}
		}
	;

firstEdgeContinuation [ BaseNode n, CollectNode<BaseNode> conn, int context, PatternGraphNode directlyNestingLHSGraph ]
	@init{
		boolean forward = true;
		MutableInteger direction = new MutableInteger(ConnectionNode.ARBITRARY);
		MutableInteger redirection = new MutableInteger(ConnectionNode.NO_REDIRECTION);
	}

	: // nothing following? -> one single node
	{
		if (n instanceof IdentNode) {
			conn.addChild(new SingleGraphEntityNode((IdentNode)n));
		}
		else {
			conn.addChild(new SingleNodeConnNode(n));
		}
	}
	|   ( e=forwardOrUndirectedEdgeOcc[context, direction, redirection, directlyNestingLHSGraph] { forward=true; }
		| e=backwardOrArbitraryDirectedEdgeOcc[context, direction, redirection, directlyNestingLHSGraph] { forward=false; }
		| e=arbitraryEdgeOcc[context, directlyNestingLHSGraph] { forward=false; direction.setValue(ConnectionNode.ARBITRARY);}
		)
			nodeContinuation[e, n, forward, direction, redirection, conn, context, directlyNestingLHSGraph] // continue looking for node
	;

edgeContinuation [ BaseNode left, CollectNode<BaseNode> conn, int context, PatternGraphNode directlyNestingLHSGraph ]
	@init{
		boolean forward = true;
		MutableInteger direction = new MutableInteger(ConnectionNode.ARBITRARY);
		MutableInteger redirection = new MutableInteger(ConnectionNode.NO_REDIRECTION);
	}

	:   // nothing following? -> connection end reached
	|   ( e=forwardOrUndirectedEdgeOcc[context, direction, redirection, directlyNestingLHSGraph] { forward=true; }
		| e=backwardOrArbitraryDirectedEdgeOcc[context, direction, redirection, directlyNestingLHSGraph] { forward=false; }
		| e=arbitraryEdgeOcc[context, directlyNestingLHSGraph] { forward=false; direction.setValue(ConnectionNode.ARBITRARY);}
		)
			nodeContinuation[e, left, forward, direction, redirection, conn, context, directlyNestingLHSGraph] // continue looking for node
	;

nodeOcc [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	@init{
		id = env.getDummyIdent();
		annots = env.getEmptyAnnotations();
		boolean hasAnnots = false;
	}

	: e=entIdentUse { res = e; } // use of already declared node
	| id=entIdentDecl COLON co=nodeTypeContinuation[id, context, directlyNestingLHSGraph] { res = co; } // node declaration
	| ( annots=annotations { hasAnnots = true; } )?
		c=COLON // anonymous node declaration
			{ id = env.defineAnonymousEntity("node", getCoords(c)); }
			{ if (hasAnnots) { id.setAnnotations(annots); } }
			co=nodeTypeContinuation[id, context, directlyNestingLHSGraph] { res = co; }
	| d=DOT // anonymous node declaration of type node
		{ id = env.defineAnonymousEntity("node", getCoords(d)); }
		( annots=annotations { id.setAnnotations(annots); } )?
		{ res = new NodeDeclNode(id, env.getNodeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph); }
	;

nodeTypeContinuation [ IdentNode id, int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	@init{
		type = env.getNodeRoot();
		constr = TypeExprNode.getEmpty();
		curId = env.getDummyIdent();
		CollectNode<IdentNode> mergees = new CollectNode<IdentNode>();
	}

	:	( type=typeIdentUse
		| TYPEOF LPAREN type=entIdentUse RPAREN
		)
		( constr=typeConstraint )?
		( 
			{
				res = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
			}
		| LT oldid=entIdentUse (COMMA curId=entIdentUse { mergees.addChild(curId); })* GT
			{
				res = new NodeTypeChangeNode(id, type, context, oldid, mergees, directlyNestingLHSGraph);
			}
		| LBRACE oldid=entIdentUse (d=DOT attr=entIdentUse)? (LBRACK mapAccess=entIdentUse RBRACK)? RBRACE
			{
				if(mapAccess==null)
					res = new MatchNodeFromStorageNode(id, type, context, 
						attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), directlyNestingLHSGraph);
				else
					res = new MatchNodeByStorageAccessNode(id, type, context,
						attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), new IdentExprNode(mapAccess), directlyNestingLHSGraph);
			}
		)
	| COPY LT type=entIdentUse GT
		{
			res = new NodeDeclNode(id, type, true, context, constr, directlyNestingLHSGraph);
		}
	;

nodeDecl [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	@init{
		constr = TypeExprNode.getEmpty();
		curId = env.getDummyIdent();
		CollectNode<IdentNode> mergees = new CollectNode<IdentNode>();
	}

	: id=entIdentDecl COLON
		( type=typeIdentUse
		| TYPEOF LPAREN type=entIdentUse RPAREN
		)
		( constr=typeConstraint )?
		( 
			{
				res = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
			}
		| LT oldid=entIdentUse (COMMA curId=entIdentUse { mergees.addChild(curId); })* GT 
			{
				res = new NodeTypeChangeNode(id, type, context, oldid, mergees, directlyNestingLHSGraph);
			}
		| LBRACE oldid=entIdentUse (d=DOT attr=entIdentUse)? (LBRACK mapAccess=entIdentUse RBRACK)? RBRACE
			{
				if(mapAccess==null)
					res = new MatchNodeFromStorageNode(id, type, context,
						attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), directlyNestingLHSGraph);
				else
					res = new MatchNodeByStorageAccessNode(id, type, context, 
						attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), new IdentExprNode(mapAccess), directlyNestingLHSGraph);
			}
		)
	| COPY LT type=entIdentUse GT
		{
			res = new NodeDeclNode(id, type, true, context, constr, directlyNestingLHSGraph);
		}
	;

nodeDeclParam [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	@init{
		constr = TypeExprNode.getEmpty();
	}

	: id=entIdentDecl COLON
		type=typeIdentUse
		( constr=typeConstraint )?
		( LT (interfaceType=typeIdentUse | maybe=NULL 
				| interfaceType=typeIdentUse PLUS maybe=NULL | maybe=NULL PLUS interfaceType=typeIdentUse) GT )?
			{
				if(interfaceType==null) {
					res = new NodeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph, maybe!=null, false);
				} else {
					res = new NodeInterfaceTypeChangeNode(id, type, context, interfaceType, directlyNestingLHSGraph, maybe!=null);
				}
			}
	;

varDecl [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	: paramModifier=IDENT id=entIdentDecl COLON
		(
			type=typeIdentUse
			{
				res = new VarDeclNode(id, type, directlyNestingLHSGraph, context);
				if(!paramModifier.getText().equals("var")) 
					{ reportError(getCoords(paramModifier), "var keyword needed before non graph element and non container parameter"); }
			}
		|
			MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				res = new VarDeclNode(id, MapTypeNode.getMapType(keyType, valueType), directlyNestingLHSGraph, context);
				if(!paramModifier.getText().equals("ref"))
					{ reportWarning(getCoords(paramModifier), "ref keyword needed before map typed parameter"); } // TODO: next version -> error
			}
		|
			SET LT keyType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				res = new VarDeclNode(id, SetTypeNode.getSetType(keyType), directlyNestingLHSGraph, context);
				if(!paramModifier.getText().equals("ref")) 
					{ reportWarning(getCoords(paramModifier), "ref keyword needed before set typed parameter"); } // TODO: next version -> error
			}
		|
			ARRAY LT keyType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				res = new VarDeclNode(id, ArrayTypeNode.getArrayType(keyType), directlyNestingLHSGraph, context);
				if(!paramModifier.getText().equals("ref")) 
					{ reportWarning(getCoords(paramModifier), "ref keyword needed before array typed parameter"); } // TODO: next version -> error
			}
		|
			QUEUE LT keyType=typeIdentUse GT
			{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
				res = new VarDeclNode(id, QueueTypeNode.getQueueType(keyType), directlyNestingLHSGraph, context);
				if(!paramModifier.getText().equals("ref")) 
					{ reportWarning(getCoords(paramModifier), "ref keyword needed before queue typed parameter"); } // TODO: next version -> error
			}
		)
	;

forwardOrUndirectedEdgeOcc [int context, MutableInteger direction, MutableInteger redirection, PatternGraphNode directlyNestingLHSGraph] returns [ BaseNode res = env.initNode() ]
	: (NOT { redirection.setValue(ConnectionNode.REDIRECT_SOURCE); })? MINUS 
		( e1=edgeDecl[context, directlyNestingLHSGraph] { res = e1; } 
		| e2=entIdentUse { res = e2; } ) 
		forwardOrUndirectedEdgeOccContinuation[direction, redirection]
	| da=DOUBLE_RARROW
		{
			IdentNode id = env.defineAnonymousEntity("edge", getCoords(da));
			res = new EdgeDeclNode(id, env.getDirectedEdgeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
			direction.setValue(ConnectionNode.DIRECTED);
		}
	| mm=MINUSMINUS
		{
			IdentNode id = env.defineAnonymousEntity("edge", getCoords(mm));
			res = new EdgeDeclNode(id, env.getUndirectedEdgeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
			direction.setValue(ConnectionNode.UNDIRECTED);
		}
	;

forwardOrUndirectedEdgeOccContinuation [MutableInteger direction, MutableInteger redirection]
	: MINUS { direction.setValue(ConnectionNode.UNDIRECTED); } (NOT { redirection.setValue(ConnectionNode.REDIRECT_TARGET | redirection.getValue()); })? // redirection not allowd but semantic error is better
	| RARROW { direction.setValue(ConnectionNode.DIRECTED); } (NOT { redirection.setValue(ConnectionNode.REDIRECT_TARGET | redirection.getValue()); })?
	;

backwardOrArbitraryDirectedEdgeOcc [ int context, MutableInteger direction, MutableInteger redirection, PatternGraphNode directlyNestingLHSGraph ] returns [ BaseNode res = env.initNode() ]
	: (NOT { redirection.setValue(ConnectionNode.REDIRECT_TARGET); })? LARROW 
		( e1=edgeDecl[context, directlyNestingLHSGraph] { res = e1; }
		| e2=entIdentUse { res = e2; } )
		backwardOrArbitraryDirectedEdgeOccContinuation[ direction, redirection ]
	| da=DOUBLE_LARROW
		{
			IdentNode id = env.defineAnonymousEntity("edge", getCoords(da));
			res = new EdgeDeclNode(id, env.getDirectedEdgeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
			direction.setValue(ConnectionNode.DIRECTED);
		}
	| lr=LRARROW
		{
			IdentNode id = env.defineAnonymousEntity("edge", getCoords(lr));
			res = new EdgeDeclNode(id, env.getDirectedEdgeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
			direction.setValue(ConnectionNode.ARBITRARY_DIRECTED);
		}
	;

backwardOrArbitraryDirectedEdgeOccContinuation [MutableInteger direction, MutableInteger redirection]
	: MINUS { direction.setValue(ConnectionNode.DIRECTED); } (NOT { redirection.setValue(ConnectionNode.REDIRECT_SOURCE | redirection.getValue()); })?
	| RARROW { direction.setValue(ConnectionNode.ARBITRARY_DIRECTED); } (NOT { redirection.setValue(ConnectionNode.REDIRECT_SOURCE | redirection.getValue()); })? // redirection not allowd but semantic error is better
	;

arbitraryEdgeOcc [int context, PatternGraphNode directlyNestingLHSGraph] returns [ BaseNode res = env.initNode() ]
	: QUESTIONMINUS
		( e1=edgeDecl[context, directlyNestingLHSGraph] { res = e1; }
		| e2=entIdentUse { res = e2; } )
		MINUSQUESTION
	| q=QMMQ
		{
			IdentNode id = env.defineAnonymousEntity("edge", getCoords(q));
			res = new EdgeDeclNode(id, env.getArbitraryEdgeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph);
		}
	;

edgeDecl [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ EdgeDeclNode res = null ]
	@init{
		id = env.getDummyIdent();
	}

	:   ( id=entIdentDecl COLON
			co=edgeTypeContinuation[id, context, directlyNestingLHSGraph] { res = co; } 
		| atCo=annotationsWithCoords
			( c=COLON
				{ id = env.defineAnonymousEntity("edge", getCoords(c)); }
				co=edgeTypeContinuation[id, context, directlyNestingLHSGraph] { res = co; } 
			|   { id = env.defineAnonymousEntity("edge", atCo.second); }
				{ res = new EdgeDeclNode(id, env.getDirectedEdgeRoot(), false, context, TypeExprNode.getEmpty(), directlyNestingLHSGraph); }
			)
				{ id.setAnnotations(atCo.first); }
		| cc=COLON
			{ id = env.defineAnonymousEntity("edge", getCoords(cc)); }
			co=edgeTypeContinuation[id, context, directlyNestingLHSGraph] { res = co; } 
		)
	;

edgeDeclParam [ int context, PatternGraphNode directlyNestingLHSGraph ] returns [ EdgeDeclNode res = null ]
	@init{
		id = env.getDummyIdent();
		type = env.getNodeRoot();
		constr = TypeExprNode.getEmpty();
	}

	: id=entIdentDecl COLON type=typeIdentUse
		( constr=typeConstraint )?
		( LT (interfaceType=typeIdentUse | maybe=NULL 
				| interfaceType=typeIdentUse PLUS maybe=NULL | maybe=NULL PLUS interfaceType=typeIdentUse) GT )?
			{
				if( interfaceType == null ) {
					res = new EdgeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph, maybe!=null, false);
				} else {
					res = new EdgeInterfaceTypeChangeNode(id, type, context, interfaceType, directlyNestingLHSGraph, maybe!=null);
				}
			}
	;

edgeTypeContinuation [ IdentNode id, int context, PatternGraphNode directlyNestingLHSGraph ] returns [ EdgeDeclNode res = null ]
	@init{
		type = env.getNodeRoot();
		constr = TypeExprNode.getEmpty();
	}

	:	( type=typeIdentUse
		| TYPEOF LPAREN type=entIdentUse RPAREN
		)
		( constr=typeConstraint )?
		(
			{
				res = new EdgeDeclNode(id, type, false, context, constr, directlyNestingLHSGraph);
			}
		| LT oldid=entIdentUse GT
			{
				res = new EdgeTypeChangeNode(id, type, context, oldid, directlyNestingLHSGraph);
			}
		| LBRACE oldid=entIdentUse (d=DOT attr=entIdentUse)? (LBRACK mapAccess=entIdentUse RBRACK)? RBRACE
			{
				if(mapAccess==null)
					res = new MatchEdgeFromStorageNode(id, type, context, 
						attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), directlyNestingLHSGraph);
				else
					res = new MatchEdgeByStorageAccessNode(id, type, context, 
						attr==null ? new IdentExprNode(oldid) : new QualIdentNode(getCoords(d), oldid, attr), new IdentExprNode(mapAccess), directlyNestingLHSGraph);
			}
		)
	| COPY LT type=entIdentUse GT
		{
			res = new EdgeDeclNode(id, type, true, context, constr, directlyNestingLHSGraph);
		}
	;

arguments[CollectNode<ExprNode> args]
	: ( arg=argument[args] ( COMMA argument[args] )* )?
	;
	
argument[CollectNode<ExprNode> args] // argument for a subpattern usage or subpattern dependent rewrite usage
	@init{ boolean yielded=false; }
	: (y=YIELD { yielded = true; })? arg=expr[false] { args.addChild(arg); if(yielded) { if(arg instanceof IdentExprNode) ((IdentExprNode)arg).setYieldedTo(); else reportError(getCoords(y), "Can only yield to an element/variable, defined to by yielded to (def)"); } }
 	;

homStatement returns [ HomNode res = null ]
	: h=HOM {res = new HomNode(getCoords(h)); }
		LPAREN id=entIdentUse { res.addChild(id); }
			(COMMA id=entIdentUse { res.addChild(id); } )*
		RPAREN
	;

totallyHomStatement returns [ TotallyHomNode res = null ]
	: i=INDEPENDENT {res = new TotallyHomNode(getCoords(i)); }
		LPAREN id=entIdentUse { res.setTotallyHom(id); } 
			(BACKSLASH entityUnaryExpr[res])?
		RPAREN
	;

entityUnaryExpr[ TotallyHomNode thn ]
	: ent=entIdentUse { thn.addChild(ent); }
	| LPAREN te=entityAddExpr[thn] RPAREN 
	;

entityAddExpr[ TotallyHomNode thn ]
	: ent=entIdentUse { thn.addChild(ent); }
		(p=PLUS op=entIdentUse { thn.addChild(ent); })*
	;
	
exactStatement returns [ ExactNode res = null ]
	: e=EXACT {res = new ExactNode(getCoords(e)); }
		LPAREN id=entIdentUse { res.addChild(id); }
			(COMMA id=entIdentUse { res.addChild(id); } )*
		RPAREN
	;

inducedStatement returns [ InducedNode res = null ]
	: i=INDUCED {res = new InducedNode(getCoords(i)); }
		LPAREN id=entIdentUse { res.addChild(id); }
			(COMMA id=entIdentUse { res.addChild(id); } )*
		RPAREN
	;

replaceBody [ Coords coords, CollectNode<BaseNode> params, int context, IdentNode nameOfRHS, PatternGraphNode directlyNestingLHSGraph ] returns [ ReplaceDeclNode res = null ]
	@init{
		CollectNode<BaseNode> connections = new CollectNode<BaseNode>();
		CollectNode<VarDeclNode> defVariablesToBeYieldedTo = new CollectNode<VarDeclNode>();
		CollectNode<SubpatternUsageNode> subpatterns = new CollectNode<SubpatternUsageNode>();
		CollectNode<OrderedReplacementNode> orderedReplacements = new CollectNode<OrderedReplacementNode>();
		CollectNode<EvalStatementNode> evals = new CollectNode<EvalStatementNode>();
		CollectNode<ExprNode> returnz = new CollectNode<ExprNode>();
		CollectNode<BaseNode> imperativeStmts = new CollectNode<BaseNode>();
		GraphNode graph = new GraphNode(nameOfRHS.toString(), coords, 
			connections, params, defVariablesToBeYieldedTo, subpatterns, 
			orderedReplacements, evals, returnz, imperativeStmts,
			context, directlyNestingLHSGraph);
		res = new ReplaceDeclNode(nameOfRHS, graph);
	}

	: ( replaceStmt[coords, connections, defVariablesToBeYieldedTo, subpatterns, orderedReplacements, evals, context, directlyNestingLHSGraph] 
		| rets[returnz, context] SEMI
		| execStmt[imperativeStmts, context, directlyNestingLHSGraph] SEMI
		| emitStmt[imperativeStmts, orderedReplacements] SEMI
		)*
	;

replaceStmt [ Coords coords, CollectNode<BaseNode> connections, CollectNode<VarDeclNode> defVariablesToBeYieldedTo,
		CollectNode<SubpatternUsageNode> subpatterns, CollectNode<OrderedReplacementNode> orderedReplacements,
		CollectNode<EvalStatementNode> evals,
		int context, PatternGraphNode directlyNestingLHSGraph ]
	: connectionsOrSubpattern[connections, defVariablesToBeYieldedTo, subpatterns, orderedReplacements, context, directlyNestingLHSGraph] SEMI
	| evaluation[evals, orderedReplacements, context, directlyNestingLHSGraph]
	| alternativeOrIteratedRewriteUsage[orderedReplacements]
	;

modifyBody [ Coords coords, CollectNode<IdentNode> dels, CollectNode<BaseNode> params, int context, IdentNode nameOfRHS, PatternGraphNode directlyNestingLHSGraph ] returns [ ModifyDeclNode res = null ]
	@init{
		CollectNode<BaseNode> connections = new CollectNode<BaseNode>();
		CollectNode<VarDeclNode> defVariablesToBeYieldedTo = new CollectNode<VarDeclNode>();
		CollectNode<SubpatternUsageNode> subpatterns = new CollectNode<SubpatternUsageNode>();
		CollectNode<OrderedReplacementNode> orderedReplacements = new CollectNode<OrderedReplacementNode>();
		CollectNode<EvalStatementNode> evals = new CollectNode<EvalStatementNode>();
		CollectNode<ExprNode> returnz = new CollectNode<ExprNode>();
		CollectNode<BaseNode> imperativeStmts = new CollectNode<BaseNode>();
		GraphNode graph = new GraphNode(nameOfRHS.toString(), coords, 
			connections, params, defVariablesToBeYieldedTo, subpatterns,
			orderedReplacements, evals, returnz, imperativeStmts,
			context, directlyNestingLHSGraph);
		res = new ModifyDeclNode(nameOfRHS, graph, dels);
	}

	: ( modifyStmt[coords, connections, defVariablesToBeYieldedTo, subpatterns, orderedReplacements, evals, dels, context, directlyNestingLHSGraph] 
		| rets[returnz, context] SEMI
		| execStmt[imperativeStmts, context, directlyNestingLHSGraph] SEMI
		| emitStmt[imperativeStmts, orderedReplacements] SEMI
		)*
	;

modifyStmt [ Coords coords, CollectNode<BaseNode> connections, CollectNode<VarDeclNode> defVariablesToBeYieldedTo,
		CollectNode<SubpatternUsageNode> subpatterns, CollectNode<OrderedReplacementNode> orderedReplacements,
		CollectNode<EvalStatementNode> evals, CollectNode<IdentNode> dels,
		int context, PatternGraphNode directlyNestingLHSGraph ]
	: connectionsOrSubpattern[connections, defVariablesToBeYieldedTo, subpatterns, orderedReplacements, context, directlyNestingLHSGraph] SEMI
	| deleteStmt[dels] SEMI
	| evaluation[evals, orderedReplacements, context, directlyNestingLHSGraph ]
	| alternativeOrIteratedRewriteUsage[orderedReplacements]
	;

alternative [ AnonymousPatternNamer namer, int context ] returns [ AlternativeNode alt = null ]
	: a=ALTERNATIVE (name=altIdentDecl)? { namer.defAlt(name, getCoords(a)); alt = new AlternativeNode(namer.alt()); } LBRACE
		( alternativeCase[alt, namer, context] ) +
		RBRACE
	| a=LPAREN { namer.defAlt(null, getCoords(a)); alt = new AlternativeNode(namer.alt()); }
		( alternativeCasePure[alt, a, namer, context] )
			( BOR alternativeCasePure[alt, a, namer, context] ) *
		RPAREN
	;	
	
alternativeCase [ AlternativeNode alt, AnonymousPatternNamer namer, int context ]
	@init{
		int mod = 0;
		CollectNode<IdentNode> dels = new CollectNode<IdentNode>();
		CollectNode<RhsDeclNode> rightHandSides = new CollectNode<RhsDeclNode>();
	}
	
	: (name=altIdentDecl)? l=LBRACE { namer.defAltCase(name, getCoords(l)); } pushScope[namer.altCase()]
		left=patternBody[getCoords(l), new CollectNode<BaseNode>(), namer, mod, context, namer.altCase().toString()]
		(
			rightReplace=replacePart[new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.altCase(), left]
				{
					rightHandSides.addChild(rightReplace);
				}
			| rightModify=modifyPart[dels, new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.altCase(), left]
				{
					rightHandSides.addChild(rightModify);
				}
		) ?
		RBRACE popScope	{ alt.addChild(new AlternativeCaseNode(namer.altCase(), left, rightHandSides)); }
	;

alternativeCasePure [ AlternativeNode alt, Token a, AnonymousPatternNamer namer, int context ]
	@init{
		int mod = 0;
		CollectNode<IdentNode> dels = new CollectNode<IdentNode>();
		CollectNode<RhsDeclNode> rightHandSides = new CollectNode<RhsDeclNode>();
		IdentNode altCaseName = IdentNode.getInvalid();
	}
	
	: { namer.defAltCase(null, getCoords(a)); } pushScope[namer.altCase()]
		left=patternBody[getCoords(a), new CollectNode<BaseNode>(), namer, mod, context, namer.altCase().toString()]
		(
			rightReplace=replacePart[new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.altCase(), left]
				{
					rightHandSides.addChild(rightReplace);
				}
			| rightModify=modifyPart[dels, new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.altCase(), left]
				{
					rightHandSides.addChild(rightModify);
				}
		) ?
		popScope { alt.addChild(new AlternativeCaseNode(namer.altCase(), left, rightHandSides)); }
	;

iterated [ AnonymousPatternNamer namer, int context ] returns [ IteratedNode res = null ]
	@init{
		CollectNode<IdentNode> dels = new CollectNode<IdentNode>();
		CollectNode<RhsDeclNode> rightHandSides = new CollectNode<RhsDeclNode>();
		IdentNode iterName = IdentNode.getInvalid();
		int minMatches = -1;
		int maxMatches = -1;
	}

	: ( i=ITERATED { minMatches = 0; maxMatches = 0; } 
	  | i=OPTIONAL { minMatches = 0; maxMatches = 1; }
	  | i=MULTIPLE { minMatches = 1; maxMatches = 0; }
	  )
	    ( name=iterIdentDecl { namer.defIter(name, null); } 
		  | { namer.defIter(null, getCoords(i)); } )
		LBRACE pushScope[namer.iter()]
		left=patternBody[getCoords(i), new CollectNode<BaseNode>(), namer, 0, context, namer.iter().toString()]
		(
			rightReplace=replacePart[new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.iter(), left]
				{
					rightHandSides.addChild(rightReplace);
				}
			| rightModify=modifyPart[dels, new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.iter(), left]
				{
					rightHandSides.addChild(rightModify);
				}
		) ?				
		RBRACE popScope { res = new IteratedNode(namer.iter(), left, rightHandSides, minMatches, maxMatches); }
	| 
		l=LPAREN { namer.defIter(null, getCoords(l)); } pushScope[namer.iter()]
		left=patternBody[getCoords(i), new CollectNode<BaseNode>(), namer, 0, context, namer.iter().toString()]
		(
			rightReplace=replacePart[new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.iter(), left]
				{
					rightHandSides.addChild(rightReplace);
				}
			| rightModify=modifyPart[dels, new CollectNode<BaseNode>(), context|BaseNode.CONTEXT_RHS, namer.iter(), left]
				{
					rightHandSides.addChild(rightModify);
				}
		) ?	
		RPAREN popScope 
	  ( 
	    STAR { minMatches = 0; maxMatches = 0; } 
	  | QUESTION { minMatches = 0; maxMatches = 1; }
	  | PLUS { minMatches = 1; maxMatches = 0; }
	  | LBRACK i=NUM_INTEGER { minMatches = Integer.parseInt(i.getText()); }
	  	   ( COLON ( STAR { maxMatches=0; } | i=NUM_INTEGER { maxMatches = Integer.parseInt(i.getText()); } ) | { maxMatches = minMatches; } )
		  RBRACK
	  )
		{ res = new IteratedNode(namer.iter(), left, rightHandSides, minMatches, maxMatches); }
	;

negative [ AnonymousPatternNamer namer, int context ] returns [ PatternGraphNode res = null ]
	@init{
		int mod = 0;
		boolean brk = false;
	}
	
	: (BREAK { brk = true; })? n=NEGATIVE (name=negIdentDecl)? { namer.defNeg(name, getCoords(n)); } 
		LBRACE pushScope[namer.neg()]
			( ( PATTERNPATH { mod = PatternGraphNode.MOD_PATTERNPATH_LOCKED; }
			| PATTERN { mod = PatternGraphNode.MOD_PATTERN_LOCKED; } ) SEMI )*
			b=patternBody[getCoords(n), new CollectNode<BaseNode>(), namer, mod, 
				context|BaseNode.CONTEXT_NEGATIVE, namer.neg().toString()] { res = b; b.iterationBreaking = brk; } 
		RBRACE popScope
	| n=TILDE { namer.defNeg(null, getCoords(n)); }
		LPAREN pushScope[namer.neg()]
			( ( PATTERNPATH { mod = PatternGraphNode.MOD_PATTERNPATH_LOCKED; }
			| PATTERN { mod = PatternGraphNode.MOD_PATTERN_LOCKED; } ) SEMI )*
			b=patternBody[getCoords(n), new CollectNode<BaseNode>(), namer, mod, 
				context|BaseNode.CONTEXT_NEGATIVE, namer.neg().toString()] { res = b; } 
		RPAREN popScope
	;

independent [ AnonymousPatternNamer namer, int context ] returns [ PatternGraphNode res = null ]
	@init{
		int mod = 0;
		boolean brk = false;
	}
	
	: (BREAK { brk = true; })? i=INDEPENDENT (name=idptIdentDecl)? { namer.defIdpt(name, getCoords(i)); }
		LBRACE pushScope[namer.idpt()]
			( ( PATTERNPATH { mod = PatternGraphNode.MOD_PATTERNPATH_LOCKED; }
			| PATTERN { mod = PatternGraphNode.MOD_PATTERN_LOCKED; } ) SEMI )*
			b=patternBody[getCoords(i), new CollectNode<BaseNode>(), namer, mod,
				context|BaseNode.CONTEXT_INDEPENDENT, namer.idpt().toString()] { res = b; b.iterationBreaking = brk; } 
		RBRACE popScope
	| i=BAND { namer.defIdpt(null, getCoords(i)); }
		LPAREN pushScope[namer.idpt()]
			( ( PATTERNPATH { mod = PatternGraphNode.MOD_PATTERNPATH_LOCKED; }
			| PATTERN { mod = PatternGraphNode.MOD_PATTERN_LOCKED; } ) SEMI )*
			b=patternBody[getCoords(i), new CollectNode<BaseNode>(), namer, mod,
				context|BaseNode.CONTEXT_INDEPENDENT, namer.idpt().toString()] { res = b; } 
		RPAREN popScope
	;

condition [ CollectNode<ExprNode> conds ]
	: IF LBRACE
			( e=expr[false] { conds.addChild(e); } SEMI )* 
		RBRACE
	;

evaluation [ CollectNode<EvalStatementNode> evals, CollectNode<OrderedReplacementNode> orderedReplacements, int context, PatternGraphNode directlyNestingLHSGraph ]
	: EVAL LBRACE
		( a=assignmentOrMethodCall[false, context, directlyNestingLHSGraph] { evals.addChild(a); } SEMI )*
		RBRACE
	| EVALHERE LBRACE
		( a=assignmentOrMethodCall[false, context, directlyNestingLHSGraph] { orderedReplacements.addChild(a); } SEMI )*
		RBRACE
	;

yielding [ CollectNode<EvalStatementNode> evals, int context, PatternGraphNode directlyNestingLHSGraph]
	: YIELD LBRACE
		( a=assignmentOrMethodCall[true, context, directlyNestingLHSGraph] { evals.addChild(a); } SEMI )*
		RBRACE
	;
	
rets[CollectNode<ExprNode> res, int context]
	@init{
		boolean multipleReturns = ! res.getChildren().isEmpty();
	}

	: r=RETURN
		{
			if ( multipleReturns ) {
				reportError(getCoords(r), "multiple occurrence of return statement in one rule");
			}
			if ( (context & BaseNode.CONTEXT_ACTION_OR_PATTERN) == BaseNode.CONTEXT_PATTERN) {
				reportError(getCoords(r), "return statement only allowed in actions, not in pattern type declarations");
			}
			res.setCoords(getCoords(r));
		}
		LPAREN exp=expr[false] { if ( !multipleReturns ) res.addChild(exp); }
		( COMMA exp=expr[false] { if ( !multipleReturns ) res.addChild(exp); } )*
		RPAREN
	;

deleteStmt[CollectNode<IdentNode> res]
	: DELETE LPAREN paramListOfEntIdentUse[res] RPAREN
	;

paramListOfEntIdentUse[CollectNode<IdentNode> res]
	: id=entIdentUse { res.addChild(id); }	( COMMA id=entIdentUse { res.addChild(id); } )*
	;

alternativeOrIteratedRewriteUsage[CollectNode<OrderedReplacementNode> orderedReplacements]
	: a=ALTERNATIVE id=altIdentUse { orderedReplacements.addChild(new AlternativeReplNode(id)); } SEMI
	| i=ITERATED id=iterIdentUse { orderedReplacements.addChild(new IteratedReplNode(id)); } SEMI
	;
	
execStmt[CollectNode<BaseNode> imperativeStmts, int context, PatternGraphNode directlyNestingLHSGraph]
    @init{ ExecNode exec = null; }
    
	: e=EXEC pushScopeStr["exec_", getCoords(e)] { exec = new ExecNode(getCoords(e)); } LPAREN xgrs[exec] RPAREN
		{ imperativeStmts.addChild(exec); } popScope
	;

emitStmt[CollectNode<BaseNode> imperativeStmts, CollectNode<OrderedReplacementNode> orderedReplacements]
	@init{ EmitNode emit = null; boolean isHere = false;}
	
	: (e=EMIT | e=EMITHERE { isHere = true; })
		{ emit = new EmitNode(getCoords(e)); }
		LPAREN
			exp=expr[false] { emit.addChild(exp); }
			( COMMA exp=expr[false] { emit.addChild(exp); } )*
		RPAREN
		{ 
			if(isHere) orderedReplacements.addChild(emit);
			else imperativeStmts.addChild(emit);
		}
	;

typeConstraint returns [ TypeExprNode constr = null ]
	: BACKSLASH te=typeUnaryExpr { constr = te; } 
	;

typeAddExpr returns [ TypeExprNode res = null ]
	: typeUse=typeIdentUse { res = new TypeConstraintNode(typeUse); }
		(t=PLUS op=typeUnaryExpr
			{ res = new TypeBinaryExprNode(getCoords(t), TypeExprNode.UNION, res, op); }
		)*
	;

typeUnaryExpr returns [ TypeExprNode res = null ]
	: typeUse=typeIdentUse { res = new TypeConstraintNode(typeUse); }
	| LPAREN te=typeAddExpr RPAREN { res = te; } 
	;


//////////////////////////////////////////
// Embedded XGRS
//////////////////////////////////////////

// todo: add more user friendly explicit error messages for % used after $ instead of implicit syntax error
// (a user choice $% override for the random flag $ is only available in the shell/debugger)

// note: sequences and expressions are right associative here, that's wrong but doesn't matter cause this is only a syntax checking pre pass
// in the backend, the operators are parsed with correct associativity (and with correct left-to-right, def-before-use order of variables)

execInParameters [ ExecNode xg ] returns [ CollectNode<ExecVarDeclNode> res = new CollectNode<ExecVarDeclNode>() ]
	: LPAREN (execParamList[res, xg])? RPAREN
	|
	;

execOutParameters [ ExecNode xg ] returns [ CollectNode<ExecVarDeclNode> res = new CollectNode<ExecVarDeclNode>() ]
	: COLON LPAREN (execParamList[res, xg])? RPAREN
	|
	;

execParamList [ CollectNode<ExecVarDeclNode> params, ExecNode xg ]
	: p=xgrsEntityDecl[xg, false] { params.addChild(p); } ( COMMA p=xgrsEntityDecl[xg, false] { params.addChild(p); } )*
	;

xgrs[ExecNode xg]
	: xgrsLazyOr[xg] ( DOLLAR THENLEFT {xg.append(" $<; ");} xgrs[xg] | THENLEFT {xg.append(" <; ");} xgrs[xg]
						| DOLLAR THENRIGHT {xg.append(" $;> ");} xgrs[xg] | THENRIGHT {xg.append(" ;> ");} xgrs[xg] )?
	;

xgrsLazyOr[ExecNode xg]
	: xgrsLazyAnd[xg] ( DOLLAR LOR {xg.append(" $|| ");} xgrsLazyOr[xg] | LOR {xg.append(" || ");} xgrsLazyOr[xg] )?
	;

xgrsLazyAnd[ExecNode xg]
	: xgrsStrictOr[xg] ( DOLLAR LAND {xg.append(" $&& ");} xgrsLazyAnd[xg] | LAND {xg.append(" && ");} xgrsLazyAnd[xg] )?
	;

xgrsStrictOr[ExecNode xg]
	: xgrsStrictXor[xg] ( DOLLAR BOR {xg.append(" $| ");} xgrsStrictOr[xg] | BOR {xg.append(" | ");} xgrsStrictOr[xg] )?
	;

xgrsStrictXor[ExecNode xg]
	: xgrsStrictAnd[xg] ( DOLLAR BXOR {xg.append(" $^ ");} xgrsStrictXor[xg] | BXOR {xg.append(" ^ ");} xgrsStrictXor[xg] )?
	;

xgrsStrictAnd[ExecNode xg]
	: xgrsNegOrIteration[xg] ( DOLLAR BAND {xg.append(" $& ");} xgrsStrictAnd[xg] | BAND {xg.append(" & ");} xgrsStrictAnd[xg] )?
	;

xgrsNegOrIteration[ExecNode xg]
	: NOT {xg.append("!");} iterSequence[xg] (ASSIGN_TO {xg.append("=>");} xgrsEntity[xg] | BOR_TO {xg.append("|>");} xgrsEntity[xg] | BAND_TO {xg.append("&>");} xgrsEntity[xg])?
	| iterSequence[xg] (ASSIGN_TO {xg.append("=>");} xgrsEntity[xg] | BOR_TO {xg.append("|>");} xgrsEntity[xg] | BAND_TO {xg.append("&>");} xgrsEntity[xg])?
	;

iterSequence[ExecNode xg]
	: simpleSequence[xg]
		(
			rsn=rangeSpecXgrsLoop { xg.append(rsn); }
		|
			STAR { xg.append("*"); }
		|
			PLUS { xg.append("+"); }
		)
	;

simpleSequence[ExecNode xg]
	@init{
		CollectNode<BaseNode> returns = new CollectNode<BaseNode>();
	}
	
	// attention/todo: names are are only partly resolved!
	// -> using not existing types, not declared names outside of the return assignment of an action call 
	// will not be detected in the frontend; xgrs in the frontend are to a certain degree syntax only
	: (xgrsEntity[null] (ASSIGN | GE )) => lhs=xgrsEntity[xg] (ASSIGN { xg.append("="); } | GE { xg.append(">="); })
		(
			id=entIdentUse LPAREN // deliver understandable error message for case of missing parenthesis at rule result assignment
				{ reportError(id.getCoords(), "the destination variable(s) of a rule result assignment must be enclosed in parenthesis"); }
		|
			xgrsConstant[xg]
		|
			xgrsVarUse[xg]
		|
			d=DOLLAR MOD LPAREN typeIdentUse RPAREN
			{ reportError(getCoords(d), "user input is only requestable in the GrShell, not at lgsp(libgr search plan backend)-level"); }
		|
			d=DOLLAR LPAREN 
			(
				n=NUM_INTEGER RPAREN { xg.append("$("); xg.append(n.getText()); xg.append(")"); }
				| f=NUM_DOUBLE RPAREN { xg.append("$("); xg.append(f.getText()); xg.append(")"); }
			)
		|
			LPAREN { xg.append('('); } xgrs[xg] RPAREN { xg.append(')'); }
		)
	| xgrsVarDecl=xgrsEntityDecl[xg, true]
	| YIELD { xg.append("yield "); } lhsent=entIdentUse { xg.append(lhsent); xg.addUsage(lhsent); } ASSIGN { xg.append('='); } 
	    ( xgrsConstant[xg]
		| xgrsVarUse[xg]
		)
	| TRUE { xg.append("true"); }
	| FALSE { xg.append("false"); }
	| (parallelCallRule[null, null]) => parallelCallRule[xg, returns]
	| DOUBLECOLON id=entIdentUse { xg.append("::" + id); xg.addUsage(id); }
	| (( DOLLAR ( MOD )? )? LBRACE LPAREN) => ( DOLLAR { xg.append("$"); } ( MOD { xg.append("\%"); } )? )?
		LBRACE LPAREN { xg.append("{("); } parallelCallRule[xg, returns] (COMMA { xg.append(","); returns = new CollectNode<BaseNode>(); } parallelCallRule[xg, returns])* RPAREN RBRACE { xg.append(")}"); }
	| DOLLAR { xg.append("$"); } ( MOD { xg.append("\%"); } )? 
		(LOR { xg.append("||"); } | LAND { xg.append("&&"); } | BOR { xg.append("|"); } | BAND { xg.append("&"); }) 
		LPAREN { xg.append("("); } xgrs[xg] (COMMA { xg.append(","); } xgrs[xg])* RPAREN { xg.append(")"); }
	| LPAREN { xg.append("("); } xgrs[xg] RPAREN { xg.append(")"); }
	| LT { xg.append(" <"); } xgrs[xg] GT { xg.append("> "); }
	| SL { xg.append(" <<"); } parallelCallRule[xg, returns] (DOUBLE_SEMI|SEMI) { xg.append(";;"); } xgrs[xg] SR { xg.append(">> "); }
	| DIV { xg.append(" /"); } xgrs[xg] DIV { xg.append("/ "); }
	| IF l=LBRACE pushScopeStr["if/exec", getCoords(l)] { xg.append("if{"); } xgrs[xg] s=SEMI 
		pushScopeStr["if/then-part", getCoords(s)] { xg.append("; "); } xgrs[xg] popScope
		(SEMI { xg.append("; "); } xgrs[xg])? popScope RBRACE { xg.append("}"); }
	| FOR l=LBRACE pushScopeStr["for", getCoords(l)] { xg.append("for{"); } xgrsEntity[xg] forSeqRemainder[xg, returns]
	| LBRACE { xg.append("{"); } seqCompoundComputation[xg] (SEMI)? RBRACE { xg.append("}"); } 
	;

forSeqRemainder[ExecNode xg, CollectNode<BaseNode> returns]
options { k = 3; }
	: (RARROW { xg.append(" -> "); } xgrsEntity[xg])? IN { xg.append(" in "); } xgrsEntity[xg]
			SEMI { xg.append("; "); } xgrs[xg] popScope RBRACE { xg.append("}"); }
	| IN { xg.append(" in "); } { input.LT(1).getText().equals("adjacent") || input.LT(1).getText().equals("adjacentIncoming") || input.LT(1).getText().equals("adjacentOutgoing")
			|| input.LT(1).getText().equals("incident") || input.LT(1).getText().equals("incoming") || input.LT(1).getText().equals("outgoing") }?
			i=IDENT LPAREN { xg.append(i.getText()); xg.append("("); }
			expr1=seqExpression[xg] (COMMA { xg.append(","); } expr2=seqExpression[xg] (COMMA { xg.append(","); } expr3=seqExpression[xg])? )?
			RPAREN { xg.append(")"); }
			SEMI { xg.append("; "); } xgrs[xg] popScope RBRACE { xg.append("}"); }
	| SEMI { xg.append("; "); } xgrs[xg] popScope RBRACE { xg.append("}"); }
	| IN LBRACK QUESTION { xg.append(" in [?"); } callRule[xg, returns] RBRACK { xg.append("]"); }
			SEMI { xg.append("; "); } xgrs[xg] popScope RBRACE { xg.append("}"); }
	;

seqCompoundComputation[ExecNode xg]
	: seqComputation[xg] (SEMI { xg.append(";"); } seqCompoundComputation[xg])?
	;

seqComputation[ExecNode xg]
	: (seqAssignTarget[null] (ASSIGN|GE)) => seqAssignTarget[xg] (ASSIGN { xg.append("="); } | GE { xg.append(">="); }) seqExpressionOrAssign[xg]
	| (xgrsEntityDecl[null,true]) => xgrsVarDecl=xgrsEntityDecl[xg, true]
	| (methodCall[null]) => methodCallRepeated[xg]
	| (procedureCall[null]) => procedureCall[xg]
	| seqExpression[xg]
	;

seqExpressionOrAssign[ExecNode xg]
	: (seqAssignTarget[null] (ASSIGN|GE)) => seqAssignTarget[xg] (ASSIGN { xg.append("="); } | GE { xg.append(">="); }) seqExpressionOrAssign[xg]
	| seqExpression[xg] 
	| VALLOC LPAREN RPAREN { xg.append("valloc()"); }
	;

seqAssignTarget[ExecNode xg]
	: YIELD { xg.append("yield "); } xgrsVarUse[xg] 
	| (xgrsVarUse[null] DOT VISITED) => xgrsVarUse[xg] DOT VISITED LBRACK { xg.append(".visited["); } seqExpression[xg] RBRACK { xg.append("]"); } 
	| (xgrsVarUse[null] DOT IDENT ) => xgrsVarUse[xg] d=DOT attr=IDENT { xg.append("."+attr.getText()); }
	| (xgrsVarUse[null] LBRACK) => xgrsVarUse[xg] LBRACK { xg.append("["); } seqExpression[xg] RBRACK { xg.append("]"); }
	| xgrsEntity[xg]
	;

// todo: add expression value returns to remaining sequence expressions,
// as of now only some sequence expressions return an expression
// the expressions are needed for the argument expressions of rule/sequence calls,
// in all other places of the sequences we only need a textual emit of the constructs just parsed
seqExpression[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprLazyOr[xg] { res = exp; }
		( 
			q=QUESTION { xg.append("?"); } op1=seqExpression[xg] COLON { xg.append(":"); } op2=seqExpression[xg]
			{
				OpNode cond=makeOp(q);
				cond.addChild(exp);
				cond.addChild(op1);
				cond.addChild(op2);
				res=cond;
			}
		)?
	;

seqExprLazyOr[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprLazyAnd[xg] { res=exp; } ( t=LOR {xg.append(" || ");} exp2=seqExprLazyOr[xg] { res = makeBinOp(t, exp, exp2); })?
	;

seqExprLazyAnd[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprStrictOr[xg] { res=exp; } ( t=LAND {xg.append(" && ");} exp2=seqExprLazyAnd[xg] { res = makeBinOp(t, exp, exp2); })?
	;

seqExprStrictOr[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprStrictXor[xg] { res=exp; } ( t=BOR {xg.append(" | ");} exp2=seqExprStrictOr[xg] { res = makeBinOp(t, exp, exp2); })?
	;

seqExprStrictXor[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprStrictAnd[xg] { res=exp; } ( t=BXOR {xg.append(" ^ ");} exp2=seqExprStrictXor[xg] { res = makeBinOp(t, exp, exp2); })?
	;

seqExprStrictAnd[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprEquality[xg] { res=exp; } ( t=BAND {xg.append(" & ");} exp2=seqExprStrictAnd[xg] { res = makeBinOp(t, exp, exp2); })?
	;
	
seqEqOp[ExecNode xg] returns [ Token t = null ]
	: e=EQUAL {xg.append(" == "); t = e; }
	| n=NOT_EQUAL {xg.append(" != "); t = n; }
	| s=STRUCTURAL_EQUAL {xg.append(" ~~ "); t = s; }
	;

seqExprEquality[ExecNode xg] returns [ExprNode res = env.initExprNode()]
	: exp=seqExprRelation[xg] { res=exp; } ( t=seqEqOp[xg] exp2=seqExprEquality[xg] { res = makeBinOp(t, exp, exp2); })?
	;

seqRelOp[ExecNode xg] returns [ Token t = null ]
	: lt=LT {xg.append(" < "); t = lt; }
	| le=LE {xg.append(" <= "); t = le; }
	| gt=GT {xg.append(" > "); t = gt; }
	| ge=GE {xg.append(" >= "); t = ge; }
	| in=IN {xg.append(" in "); t = in; }
	;

seqExprRelation[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprAdd[xg] { res=exp; } ( t=seqRelOp[xg] exp2=seqExprRelation[xg] { res = makeBinOp(t, exp, exp2); })?
	;

seqExprAdd[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: exp=seqExprUnary[xg] { res=exp; } ( t=PLUS {xg.append(" + ");} exp2=seqExprAdd[xg]  { res = makeBinOp(t, exp, exp2); })?
	;

seqExprUnary[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	@init{ Token t = null; }
	: (LPAREN typeIdentUse RPAREN) =>
		p=LPAREN {xg.append("(");} id=typeIdentUse {xg.append(id);} RPAREN {xg.append(")");} op=seqExprBasic[xg]
		{
			res = new CastNode(getCoords(p), id, op);
		}
	| (n=NOT {t=n; xg.append("!");})? exp=seqExprBasic[xg] { if(t!=null) res = makeUnOp(t, exp); else res = exp; }
	| m=MINUS {xg.append("-");} exp=seqExprBasic[xg]
		{
			OpNode neg = new ArithmeticOpNode(getCoords(m), OperatorSignature.NEG);
			neg.addChild(exp);
			res = neg;
		}
	;

// todo: the xgrsVarUse[xg] casted to IdenNodes might be not simple variable identifiers, but global variables with :: prefix,
//  probably a distinction is needed
seqExprBasic[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	@init{
		CollectNode<BaseNode> returns = new CollectNode<BaseNode>();
	}
	
	: (methodCall[null]) => methodCall[xg]
	| (xgrsVarUse[null] DOT VISITED) => xgrsVarUse[xg] DOT VISITED LBRACK 
		{ xg.append(".visited["); } seqExpression[xg] RBRACK { xg.append("]"); }
	| (xgrsVarUse[null] DOT IDENT) => target=xgrsVarUse[xg] d=DOT attr=memberIdentUse 
		{ xg.append("."+attr.getSymbol().getText()); res = new MemberAccessExprNode(getCoords(d), new IdentExprNode((IdentNode)target), attr); }
	| (xgrsVarUse[null] LBRACK) => target=xgrsVarUse[xg] l=LBRACK { xg.append("["); } key=seqExpression[xg] RBRACK 
		{ xg.append("]"); res = new IndexedAccessExprNode(getCoords(l), new IdentExprNode((IdentNode)target), key); }
	| (xgrsConstant[null]) => exp=xgrsConstant[xg] { res = (ExprNode)exp; }
	| (functionCall[null]) => functionCall[xg]
	| RANDOM LPAREN { xg.append("random"); xg.append("("); } ( fromExpr=seqExpression[xg] )? RPAREN { xg.append(")"); }
	| DEF LPAREN { xg.append("def("); } xgrsVariableList[xg, returns] RPAREN { xg.append(")"); } 
	| (xgrsVarUse[null])=> exp=xgrsVarUse[xg] { res = new IdentExprNode((IdentNode)exp); }
	| a=AT LPAREN { xg.append("@("); } (i=IDENT { xg.append(i.getText()); } | s=STRING_LITERAL { xg.append(s.getText()); }) RPAREN { xg.append(")"); }
	| LPAREN { xg.append("("); } seqExpression[xg] RPAREN { xg.append(")"); } 
	;

procedureCall[ExecNode xg]
	: { input.LT(1).getText().equals("vfree") || input.LT(1).getText().equals("vfreenonreset") || input.LT(1).getText().equals("vreset") 
		|| input.LT(1).getText().equals("record") || input.LT(1).getText().equals("emit") 
		|| input.LT(1).getText().equals("rem") || input.LT(1).getText().equals("clear")}?
		(i=IDENT | i=EMIT) LPAREN { xg.append(i.getText()); xg.append("("); } ( seqExpression[xg] )? RPAREN { xg.append(")"); }
	;

functionCall[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: { input.LT(1).getText().equals("valloc") || input.LT(1).getText().equals("add") 
		|| input.LT(1).getText().equals("insertInduced") || input.LT(1).getText().equals("insertDefined")
		|| input.LT(1).getText().equals("adjacent") || input.LT(1).getText().equals("adjacentIncoming") || input.LT(1).getText().equals("adjacentOutgoing")
		|| input.LT(1).getText().equals("incident") || input.LT(1).getText().equals("incoming") || input.LT(1).getText().equals("outgoing") 
		|| input.LT(1).getText().equals("inducedSubgraph") || input.LT(1).getText().equals("definedSubgraph")
		|| input.LT(1).getText().equals("source") || input.LT(1).getText().equals("target") }?
		i=IDENT LPAREN { xg.append(i.getText()); xg.append("("); } ( fromExpr=seqExpression[xg] (COMMA { xg.append(","); } fromExpr2=seqExpression[xg] (COMMA { xg.append(","); } fromExpr3=seqExpression[xg])? )? )? RPAREN { xg.append(")"); }
	;
	
methodCall[ExecNode xg]
	: xgrsVarUse[xg] d=DOT method=IDENT LPAREN { xg.append("."+method.getText()+"("); } 
			 ( seqExpression[xg] (COMMA { xg.append(","); } seqExpression[xg])? )? RPAREN { xg.append(")"); }
		{ if(!method.getText().equals("add") && !method.getText().equals("rem") && !method.getText().equals("clear")
				&& !method.getText().equals("size") && !method.getText().equals("empty") && !method.getText().equals("peek"))
			reportError(getCoords(d), "Unknown method name \""+method.getText()+"\"! (available are add|rem|clear|size|empty|peek on set|map|array|queue)");
		}
	;

methodCallRepeated[ExecNode xg]
	: methodCall[xg] ( d=DOT method=IDENT LPAREN { xg.append("."+method.getText()+"("); } 
			 ( seqExpression[xg] (COMMA { xg.append(","); } seqExpression[xg])? )? RPAREN { xg.append(")"); }
		{ if(!method.getText().equals("add") && !method.getText().equals("rem") && !method.getText().equals("clear")
				&& !method.getText().equals("size") && !method.getText().equals("empty") && !method.getText().equals("peek"))
			reportError(getCoords(d), "Unknown method name \""+method.getText()+"\"! (available are add|rem|clear|size|empty|peek on set|map|array|queue)");
		}
	)*
	;

xgrsConstant[ExecNode xg] returns[ExprNode res = env.initExprNode()]
	: b=NUM_BYTE { xg.append(b.getText()); res = new ByteConstNode(getCoords(b), Byte.parseByte(ByteConstNode.removeSuffix(b.getText()), 10)); }
	| sh=NUM_SHORT { xg.append(sh.getText()); res = new ShortConstNode(getCoords(sh), Short.parseShort(ShortConstNode.removeSuffix(sh.getText()), 10)); }
	| i=NUM_INTEGER { xg.append(i.getText()); res = new IntConstNode(getCoords(i), Integer.parseInt(i.getText(), 10)); }
	| l=NUM_LONG { xg.append(l.getText()); res = new LongConstNode(getCoords(l), Long.parseLong(LongConstNode.removeSuffix(l.getText()), 10)); }
	| f=NUM_FLOAT { xg.append(f.getText()); res = new FloatConstNode(getCoords(f), Float.parseFloat(f.getText())); }
	| d=NUM_DOUBLE { xg.append(d.getText()); res = new DoubleConstNode(getCoords(d), Double.parseDouble(d.getText())); }
	| s=STRING_LITERAL { xg.append(s.getText()); String buff = s.getText();
			// Strip the " from the string
			buff = buff.substring(1, buff.length() - 1);
			res = new StringConstNode(getCoords(s), buff); }
	| tt=TRUE { xg.append(tt.getText()); res = new BoolConstNode(getCoords(tt), true); }
	| ff=FALSE { xg.append(ff.getText()); res = new BoolConstNode(getCoords(ff), false); }
	| n=NULL { xg.append(n.getText()); res = new NullConstNode(getCoords(n)); }
	| tid=typeIdentUse d=DOUBLECOLON id=entIdentUse { xg.append(tid + "::" + id); res = new DeclExprNode(new EnumExprNode(getCoords(d), tid, id)); }
	| MAP LT typeName=typeIdentUse COMMA toTypeName=typeIdentUse GT LBRACE RBRACE { xg.append("map<"+typeName+","+toTypeName+">{ }"); }
	| SET LT typeName=typeIdentUse GT LBRACE RBRACE { xg.append("set<"+typeName+">{ }"); }
	| ARRAY LT typeName=typeIdentUse GT LBRACK RBRACK { xg.append("array<"+typeName+">[ ]"); }
	| QUEUE LT typeName=typeIdentUse GT RBRACK LBRACK { xg.append("queue<"+typeName+">] ["); }
	;
	
parallelCallRule[ExecNode xg, CollectNode<BaseNode> returns]
	: ( LPAREN {xg.append("(");} xgrsVariableList[xg, returns] RPAREN ASSIGN {xg.append(")=");} )?
		(	( DOLLAR {xg.append("$");} ( xgrsVarUse[xg] 
						(COMMA {xg.append(",");} (xgrsVarUse[xg] | STAR {xg.append("*");}))? )? )?
				LBRACK {xg.append("[");} 
				callRule[xg, returns]
				RBRACK {xg.append("]");}
		| 
			callRule[xg, returns]
		)
	;
		
callRule[ExecNode xg, CollectNode<BaseNode> returns]
	@init{
		CollectNode<BaseNode> params = new CollectNode<BaseNode>();
	}
	
	: ( | MOD { xg.append("\%"); } | MOD QUESTION { xg.append("\%?"); } | QUESTION { xg.append("?"); } | QUESTION MOD { xg.append("?\%"); } )
		id=actionIdentUse {xg.append(id);}
		(LPAREN {xg.append("(");} ruleParams[xg, params] RPAREN {xg.append(")");})?
		(BACKSLASH filterId=actionIdentUse {xg.append("\\"); xg.append(filterId);} | BACKSLASH AUTO {xg.append("\\"); xg.append("auto");})?
		{
			xg.addCallAction(new CallActionNode(id.getCoords(), id, params, returns, filterId));
		}
	;

ruleParam[ExecNode xg, CollectNode<BaseNode> parameters]
	: exp=seqExpression[xg] { parameters.addChild(exp); }
	;

ruleParams[ExecNode xg, CollectNode<BaseNode> parameters]
	: ruleParam[xg, parameters]	( COMMA {xg.append(",");} ruleParam[xg, parameters] )*
	;

xgrsVariableList[ExecNode xg, CollectNode<BaseNode> res]
	: child=xgrsEntity[xg] { res.addChild(child); }
		( COMMA { xg.append(","); } child=xgrsEntity[xg] { res.addChild(child); } )*
	;

xgrsVarUse[ExecNode xg] returns [BaseNode res = null]
	:
		id=entIdentUse // var of node, edge, or basic type
		{ res = id; xg.append(id); xg.addUsage(id); } 
	|
		DOUBLECOLON id=entIdentUse // global var of node, edge, or basic type
		{ res = id; xg.append("::" + id); xg.addUsage(id); } 
	;

xgrsEntity[ExecNode xg] returns [BaseNode res = null]
	:
		varUse=xgrsVarUse[xg] 
		{ res = varUse; }
	|
		(IDENT COLON | MINUS IDENT COLON) => xgrsVarDecl=xgrsEntityDecl[xg, true]
		{ res = xgrsVarDecl; }
	;

xgrsEntityDecl[ExecNode xg, boolean emit] returns [ExecVarDeclNode res = null]
options { k = *; }
	:
		id=entIdentDecl COLON type=typeIdentUse // node decl
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, type);
			if(emit) xg.append(id.toString()+":"+type.toString());
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		id=entIdentDecl COLON MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse GT // map decl
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, MapTypeNode.getMapType(keyType, valueType));
			if(emit) xg.append(id.toString()+":map<"+keyType.toString()+","+valueType.toString()+">");
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		(entIdentDecl COLON MAP LT typeIdentUse COMMA typeIdentUse GE) =>
		id=entIdentDecl COLON MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse // map decl; special to save user from splitting map<S,T>=x to map<S,T> =x as >= is GE not GT ASSIGN
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, MapTypeNode.getMapType(keyType, valueType));
			if(emit) xg.append(id.toString()+":map<"+keyType.toString()+","+valueType.toString());
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		id=entIdentDecl COLON SET LT type=typeIdentUse GT // set decl
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, SetTypeNode.getSetType(type));
			if(emit) xg.append(id.toString()+":set<"+type.toString()+">");
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		(entIdentDecl COLON SET LT typeIdentUse GE) => 
		id=entIdentDecl COLON SET LT type=typeIdentUse // set decl; special to save user from splitting set<S>=x to set<S> =x as >= is GE not GT ASSIGN
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, SetTypeNode.getSetType(type));
			if(emit) xg.append(id.toString()+":set<"+type.toString());
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		id=entIdentDecl COLON ARRAY LT type=typeIdentUse GT // array decl
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, ArrayTypeNode.getArrayType(type));
			if(emit) xg.append(id.toString()+":array<"+type.toString()+">");
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		(entIdentDecl COLON ARRAY LT typeIdentUse GE) => 
		id=entIdentDecl COLON ARRAY LT type=typeIdentUse // array decl; special to save user from splitting array<S>=x to array<S> =x as >= is GE not GT ASSIGN
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, ArrayTypeNode.getArrayType(type));
			if(emit) xg.append(id.toString()+":array<"+type.toString());
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		id=entIdentDecl COLON QUEUE LT type=typeIdentUse GT // queue decl
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, QueueTypeNode.getQueueType(type));
			if(emit) xg.append(id.toString()+":queue<"+type.toString()+">");
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		(entIdentDecl COLON QUEUE LT typeIdentUse GE) => 
		id=entIdentDecl COLON QUEUE LT type=typeIdentUse // queue decl; special to save user from splitting queue<S>=x to queue<S> =x as >= is GE not GT ASSIGN
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, QueueTypeNode.getQueueType(type));
			if(emit) xg.append(id.toString()+":queue<"+type.toString());
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		id=entIdentDecl COLON MATCH LT type=actionIdentUse GT // match decl
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, MatchTypeNode.getMatchType(type));
			if(emit) xg.append(id.toString()+":match<"+type.toString()+">");
			xg.addVarDecl(decl);
			res = decl;
		}
	|
		MINUS id=entIdentDecl COLON type=typeIdentUse RARROW // edge decl, interpreted grs don't use -:-> form
		{
			ExecVarDeclNode decl = new ExecVarDeclNode(id, type);
			if(emit) xg.append(decl.getIdentNode().getIdent() + ":" + decl.typeUnresolved);
			xg.addVarDecl(decl);
			res = decl;
		}
	;


////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Types / Model
////////////////////////////////////////////////////////////////////////////////////////////////////////////////



textTypes returns [ ModelNode model = null ]
	@init{
		CollectNode<ModelNode> modelChilds = new CollectNode<ModelNode>();
		CollectNode<IdentNode> types = new CollectNode<IdentNode>();
		CollectNode<IdentNode> externalFuncs = new CollectNode<IdentNode>();
		IdentNode id = env.getDummyIdent();

		String modelName = Util.removeFileSuffix(Util.removePathPrefix(getFilename()), "gm");

		id = new IdentNode(
			env.define(ParserEnvironment.MODELS, modelName,
			new de.unika.ipd.grgen.parser.Coords(0, 0, getFilename())));
	}

	:   ( m=MODEL ignoredToken=IDENT SEMI
			{ reportWarning(getCoords(m), "keyword \"model\" is deprecated"); }
		)?
		( usingDecl[modelChilds] )?
		typeDecls[types, externalFuncs] EOF
		{
			if(modelChilds.getChildren().size() == 0)
				modelChilds.addChild(env.getStdModel());
			model = new ModelNode(id, types, externalFuncs, modelChilds);
		}
	;

typeDecls [ CollectNode<IdentNode> types,  CollectNode<IdentNode> externalFuncs ]
	: ( type=typeDecl { types.addChild(type); } |
		externalFunc=externalFunctionDecl { externalFuncs.addChild(externalFunc); })*
	;

externalFunctionDecl returns [ IdentNode res = env.getDummyIdent() ]
	: id=extFuncIdentDecl params=paramTypes COLON ret=returnType SEMI
		{
			id.setDecl(new ExternalFunctionDeclNode(id, params, ret));
			res = id;
		}
	;

paramTypes returns [ CollectNode<BaseNode> res = new CollectNode<BaseNode>() ]
	: LPAREN (returnTypeList[res])? RPAREN // we reuse the return type list cause it's of format we need
	;

typeDecl returns [ IdentNode res = env.getDummyIdent() ]
	: d=classDecl { res = d; } 
	| d=enumDecl { res = d; } 
	| d=extClassDecl { res = d; }
	;

classDecl returns [ IdentNode res = env.getDummyIdent() ]
	@init{ mods = 0; }

	: (mods=typeModifiers)? (d=edgeClassDecl[mods] { res = d; } | d=nodeClassDecl[mods] { res = d; } )
	;

typeModifiers returns [ int res = 0; ]
	@init{ mod = 0; }

	: (mod=typeModifier { res |= mod; })+
	;

typeModifier returns [ int res = 0; ]
	: ABSTRACT { res |= InheritanceTypeNode.MOD_ABSTRACT; }
	| CONST { res |= InheritanceTypeNode.MOD_CONST; }
	;

/**
 * An edge class decl makes a new type decl node with the declaring id and
 * a new edge type node as children
 */
edgeClassDecl[int modifiers] returns [ IdentNode res = env.getDummyIdent() ]
	@init{
		boolean arbitrary = false;
		boolean undirected = false;
	}

	:	(
			ARBITRARY
			{
				arbitrary = true;
				modifiers |= InheritanceTypeNode.MOD_ABSTRACT;
			}
		|	DIRECTED // do nothing, that's default
		|	UNDIRECTED { undirected = true; }
		)?
		EDGE CLASS id=typeIdentDecl (LT externalName=fullQualIdent GT)?
	  	ext=edgeExtends[id, arbitrary, undirected] cas=connectAssertions pushScope[id]
		(
			LBRACE body=classBody[id] RBRACE
		|	SEMI
			{ body = new CollectNode<BaseNode>(); }
		)
		{
			EdgeTypeNode et;
			if (arbitrary) {
				et = new ArbitraryEdgeTypeNode(ext, cas, body, modifiers, externalName);
			}
			else {
				if (undirected) {
					et = new UndirectedEdgeTypeNode(ext, cas, body, modifiers, externalName);
				} else {
					et = new DirectedEdgeTypeNode(ext, cas, body, modifiers, externalName);
				}
			}
			id.setDecl(new TypeDeclNode(id, et));
			res = id;
		}
		popScope
  ;

nodeClassDecl[int modifiers] returns [ IdentNode res = env.getDummyIdent() ]
	: 	NODE CLASS id=typeIdentDecl (LT externalName=fullQualIdent GT)?
	  	ext=nodeExtends[id] pushScope[id]
		(
			LBRACE body=classBody[id] RBRACE
		|	SEMI
			{ body = new CollectNode<BaseNode>(); }
		)
		{
			NodeTypeNode nt = new NodeTypeNode(ext, body, modifiers, externalName);
			id.setDecl(new TypeDeclNode(id, nt));
			res = id;
		}
		popScope
	;

validIdent returns [ String id = "" ]
	:	i=~GT
		{
			if(i.getType() != IDENT && !env.isLexerKeyword(i.getText()))
				reportError(getCoords(i), "\"" + i.getText() + "\" is not a valid identifier");
			id = i.getText();
		}
	;

fullQualIdent returns [ String id = "" ]
	:	i=validIdent { id = i; } 
	 	(DOT id2=validIdent { id += "." + id2; })*
	;

connectAssertions returns [ CollectNode<ConnAssertNode> c = new CollectNode<ConnAssertNode>() ]
	: CONNECT connectAssertion[c]
		( COMMA connectAssertion[c] )*
	|
	;

connectAssertion [ CollectNode<ConnAssertNode> c ]
options { k = *; }
	: src=typeIdentUse srcRange=rangeSpec r=RARROW tgt=typeIdentUse tgtRange=rangeSpec
		{ c.addChild(new ConnAssertNode(src, srcRange, tgt, tgtRange, false));
		  reportWarning(getCoords(r), "-> in connection assertion is deprecated, use --> (or <-- for reverse direction, or -- for undirected edges, or ?--? for arbitrary edges)");
		}
	| src=typeIdentUse srcRange=rangeSpec DOUBLE_RARROW tgt=typeIdentUse tgtRange=rangeSpec
		{ c.addChild(new ConnAssertNode(src, srcRange, tgt, tgtRange, false)); }
	| src=typeIdentUse srcRange=rangeSpec DOUBLE_LARROW tgt=typeIdentUse tgtRange=rangeSpec
		{ c.addChild(new ConnAssertNode(tgt, tgtRange, src, srcRange, false)); }
	| src=typeIdentUse srcRange=rangeSpec QMMQ tgt=typeIdentUse tgtRange=rangeSpec
		{ c.addChild(new ConnAssertNode(src, srcRange, tgt, tgtRange, true)); }
	| src=typeIdentUse srcRange=rangeSpec MINUSMINUS tgt=typeIdentUse tgtRange=rangeSpec
		{ c.addChild(new ConnAssertNode(src, srcRange, tgt, tgtRange, true)); }
	| co=COPY EXTENDS
		{ c.addChild(new ConnAssertNode(getCoords(co))); }
	;

edgeExtends [IdentNode clsId, boolean arbitrary, boolean undirected] returns [ CollectNode<IdentNode> c = new CollectNode<IdentNode>() ]
	: EXTENDS edgeExtendsCont[clsId, c, undirected]
	|	{
			if (arbitrary) {
				c.addChild(env.getArbitraryEdgeRoot());
			} else {
				if(undirected) {
					c.addChild(env.getUndirectedEdgeRoot());
				} else {
					c.addChild(env.getDirectedEdgeRoot());
				}
			}
		}
	;

edgeExtendsCont [ IdentNode clsId, CollectNode<IdentNode> c, boolean undirected ]
	: e=typeIdentUse
		{
			if ( ! ((IdentNode)e).toString().equals(clsId.toString()) )
				c.addChild(e);
			else
				reportError(e.getCoords(), "A class must not extend itself");
		}
	(COMMA e=typeIdentUse
		{
			if ( ! ((IdentNode)e).toString().equals(clsId.toString()) )
				c.addChild(e);
			else
				reportError(e.getCoords(), "A class must not extend itself");
		}
	)*
		{
			if (c.getChildren().size() == 0) {
				if (undirected) {
					c.addChild(env.getUndirectedEdgeRoot());
				} else {
					c.addChild(env.getDirectedEdgeRoot());
				}
			}
		}
	;

nodeExtends [ IdentNode clsId ] returns [ CollectNode<IdentNode> c = new CollectNode<IdentNode>() ]
	: EXTENDS nodeExtendsCont[clsId, c]
	|	{ c.addChild(env.getNodeRoot()); }
	;

nodeExtendsCont [IdentNode clsId, CollectNode<IdentNode> c ]
	: n=typeIdentUse
		{
			if ( ! ((IdentNode)n).toString().equals(clsId.toString()) )
				c.addChild(n);
			else
				reportError(n.getCoords(), "A class must not extend itself");
		}
	(COMMA n=typeIdentUse
		{
			if ( ! ((IdentNode)n).toString().equals(clsId.toString()) )
				c.addChild(n);
			else
				reportError(n.getCoords(), "A class must not extend itself");
		}
	)*
		{ if ( c.getChildren().size() == 0 ) c.addChild(env.getNodeRoot()); }
	;

classBody [IdentNode clsId] returns [ CollectNode<BaseNode> c = new CollectNode<BaseNode>() ]
	:	(
			(
				b1=basicAndContainerDecl[c]
			|
				b3=initExpr { c.addChild(b3); }
			|
				b4=constrDecl[clsId] { c.addChild(b4); }
			) SEMI
		)*
	;

enumDecl returns [ IdentNode res = env.getDummyIdent() ]
	@init{
		CollectNode<EnumItemNode> c = new CollectNode<EnumItemNode>();
	}

	: ENUM id=typeIdentDecl pushScope[id]
		LBRACE enumList[id, c]
		{
			TypeNode enumType = new EnumTypeNode(c);
			id.setDecl(new TypeDeclNode(id, enumType));
			res = id;
		}
		RBRACE popScope
	;

enumList[ IdentNode enumType, CollectNode<EnumItemNode> collect ]
	@init{
		int pos = 0;
	}

	: init=enumItemDecl[enumType, collect, env.getZero(), pos++]
		( COMMA init=enumItemDecl[enumType, collect, init, pos++] )*
	;

enumItemDecl [ IdentNode type, CollectNode<EnumItemNode> coll, ExprNode defInit, int pos ]
				returns [ ExprNode res = env.initExprNode() ]
	@init{
		ExprNode value;
	}

	: id=entIdentDecl	( ASSIGN init=expr[true] )? //'true' means that expr initializes an enum item
		{
			if(init != null) {
				value = init;
			} else {
				value = defInit;
			}
			EnumItemNode memberDecl = new EnumItemNode(id, type, value, pos);
			id.setDecl(memberDecl);
			coll.addChild(memberDecl);
			OpNode add = new ArithmeticOpNode(id.getCoords(), OperatorSignature.ADD);
			add.addChild(value);
			add.addChild(env.getOne());
			res = add;
		}
	;

extClassDecl returns [ IdentNode res = env.getDummyIdent() ]
	: CLASS id=typeIdentDecl ext=extExtends[id] SEMI
		{
			ExternalTypeNode et = new ExternalTypeNode(ext);
			id.setDecl(new TypeDeclNode(id, et));
			res = id;
		}
	;

extExtends [ IdentNode clsId ] returns [ CollectNode<IdentNode> c = new CollectNode<IdentNode>() ]
	: (EXTENDS extExtendsCont[clsId, c])?
	;

extExtendsCont [IdentNode clsId, CollectNode<IdentNode> c ]
	: n=typeIdentUse
		{
			if ( ! ((IdentNode)n).toString().equals(clsId.toString()) )
				c.addChild(n);
			else
				reportError(n.getCoords(), "A class must not extend itself");
		}
	(COMMA n=typeIdentUse
		{
			if ( ! ((IdentNode)n).toString().equals(clsId.toString()) )
				c.addChild(n);
			else
				reportError(n.getCoords(), "A class must not extend itself");
		}
	)*
	;
	
basicAndContainerDecl [ CollectNode<BaseNode> c ]
	@init{
		id = env.getDummyIdent();
		MemberDeclNode decl = null;
		boolean isConst = false;
	}

	:	(
			ABSTRACT ( CONST { isConst = true; } )? id=entIdentDecl
			{
				decl = new AbstractMemberDeclNode(id, isConst);
				c.addChild(decl);
			}
		|
			( CONST { isConst = true; } )? id=entIdentDecl COLON 
			(
				type=typeIdentUse
				{
					decl = new MemberDeclNode(id, type, isConst);
					id.setDecl(decl);
					c.addChild(decl);
				}
				(
					init=initExprDecl[decl.getIdentNode()]
					{
						c.addChild(init);
						if(isConst)
							decl.setConstInitializer(init);
					}
				)?
			|
				MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse GT
				{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
					decl = new MemberDeclNode(id, MapTypeNode.getMapType(keyType, valueType), isConst);
					id.setDecl(decl);
					c.addChild(decl);
				}
				(
					ASSIGN init2=initMapExpr[decl.getIdentNode(), null]
					{
						c.addChild(init2);
						if(isConst)
							decl.setConstInitializer(init2);
					}
				)?
			|
				SET LT valueType=typeIdentUse GT
				{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
					decl = new MemberDeclNode(id, SetTypeNode.getSetType(valueType), isConst);
					id.setDecl(decl);
					c.addChild(decl);
				}
				(
					ASSIGN init3=initSetExpr[decl.getIdentNode(), null]
					{
						c.addChild(init3);
						if(isConst)
							decl.setConstInitializer(init3);
					}
				)?
			|
				ARRAY LT valueType=typeIdentUse GT
				{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
					decl = new MemberDeclNode(id, ArrayTypeNode.getArrayType(valueType), isConst);
					id.setDecl(decl);
					c.addChild(decl);
				}
				(
					ASSIGN init4=initArrayExpr[decl.getIdentNode(), null]
					{
						c.addChild(init4);
						if(isConst)
							decl.setConstInitializer(init4);
					}
				)?
			|
				QUEUE LT valueType=typeIdentUse GT
				{ // MAP TODO: das sollte eigentlich kein Schluesselwort sein, sondern ein Typbezeichner
					decl = new MemberDeclNode(id, QueueTypeNode.getQueueType(valueType), isConst);
					id.setDecl(decl);
					c.addChild(decl);
				}
				(
					ASSIGN init5=initQueueExpr[decl.getIdentNode(), null]
					{
						c.addChild(init5);
						if(isConst)
							decl.setConstInitializer(init5);
					}
				)?
			)
		)
	;

initExpr returns [ MemberInitNode res = null ]
	: id=entIdentUse init=initExprDecl[id] { res = init; }
	;

initExprDecl [IdentNode id] returns [ MemberInitNode res = null ]
	: a=ASSIGN e=expr[false]
		{
			res = new MemberInitNode(getCoords(a), id, e);
		}
	;

initMapExpr [IdentNode id, MapTypeNode mapType] returns [ MapInitNode res = null ]
	: l=LBRACE { res = new MapInitNode(getCoords(l), id, mapType); }
	          item1=mapItem { res.addMapItem(item1); }
	  ( COMMA item2=mapItem { res.addMapItem(item2); } )*
	  RBRACE
	;

initSetExpr [IdentNode id, SetTypeNode setType] returns [ SetInitNode res = null ]
	: l=LBRACE { res = new SetInitNode(getCoords(l), id, setType); }	
	          item1=setItem { res.addSetItem(item1); }
	  ( COMMA item2=setItem { res.addSetItem(item2); } )*
	  RBRACE
	;

initArrayExpr [IdentNode id, ArrayTypeNode arrayType] returns [ ArrayInitNode res = null ]
	: l=LBRACK { res = new ArrayInitNode(getCoords(l), id, arrayType); }	
	          item1=arrayItem { res.addArrayItem(item1); }
	  ( COMMA item2=arrayItem { res.addArrayItem(item2); } )*
	  RBRACK
	;

initQueueExpr [IdentNode id, QueueTypeNode queueType] returns [ QueueInitNode res = null ]
	: l=RBRACK { res = new QueueInitNode(getCoords(l), id, queueType); }	
	          item1=queueItem { res.addQueueItem(item1); }
	  ( COMMA item2=queueItem { res.addQueueItem(item2); } )*
	  LBRACK
	;

mapItem returns [ MapItemNode res = null ]
	: key=expr[false] a=RARROW value=expr[false]
		{
			res = new MapItemNode(getCoords(a), key, value);
		}
	;

setItem returns [ SetItemNode res = null ]
	: value=expr[false]
		{
			res = new SetItemNode(value.getCoords(), value);
		}
	;

arrayItem returns [ ArrayItemNode res = null ]
	: value=expr[false]
		{
			res = new ArrayItemNode(value.getCoords(), value);
		}
	;

queueItem returns [ QueueItemNode res = null ]
	: value=expr[false]
		{
			res = new QueueItemNode(value.getCoords(), value);
		}
	;

constrDecl [IdentNode clsId] returns [ ConstructorDeclNode res = null ]
	@init {
		CollectNode<ConstructorParamNode> params = new CollectNode<ConstructorParamNode>();
	}
	
	: id=typeIdentUse LPAREN constrParamList[params] RPAREN
		{
			res = new ConstructorDeclNode(id, params);
			
			if(!id.toString().equals(clsId.toString()) )
				reportError(id.getCoords(), "A constructor must have the name of the containing class");
		}
	;

constrParamList [ CollectNode<ConstructorParamNode> params ]
	: p=constrParam { params.addChild(p); } ( COMMA p=constrParam { params.addChild(p); } )*
	;

constrParam returns [ ConstructorParamNode res = null ]
	: id=entIdentUse ( ASSIGN e=expr[false] )?
		{
			res = new ConstructorParamNode(id, e);
		}
	;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Base
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


memberIdent returns [ Token t = null ]
	: i=IDENT { t = i; }
	| r=REPLACE { r.setType(IDENT); t = r; }             // HACK: For string replace function... better choose another name?
	; 

pushScope [IdentNode name]
	@init{ env.pushScope(name); }
	:
	;

pushScopeStr [String str, Coords coords]
	@init{ env.pushScope(new IdentNode(new Symbol.Definition(env.getCurrScope(), coords, new Symbol(str, SymbolTable.getInvalid())))); }
	:
	;

popScope
	@init{ env.popScope(); }
	:
	;


typeIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.TYPES, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

rhsIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.REPLACES, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

entIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.ENTITIES, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

actionIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.ACTIONS, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

altIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.ALTERNATIVES, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

iterIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.ITERATEDS, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;
	
negIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.ITERATEDS, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

idptIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.INDEPENDENTS, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

patIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.PATTERNS, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

extFuncIdentDecl returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
		{ if(i!=null) res = new IdentNode(env.define(ParserEnvironment.EXTERNAL_FUNCTIONS, i.getText(), getCoords(i))); }
		( annots=annotations { res.setAnnotations(annots); } )?
	;

/////////////////////////////////////////////////////////
// Identifier usages, it is checked, whether the identifier is declared.
// The IdentNode created by the definition is returned.
// Don't factor the common stuff into "identUse", that pollutes the follow sets

typeIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.TYPES, i.getText(), getCoords(i))); }
	;

rhsIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.REPLACES, i.getText(), getCoords(i))); }
	;

entIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.ENTITIES, i.getText(), getCoords(i))); }
	;

actionIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.ACTIONS, i.getText(), getCoords(i))); }
	;

altIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.ALTERNATIVES, i.getText(), getCoords(i))); }
	;

iterIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.ITERATEDS, i.getText(), getCoords(i))); }
	;

negIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.NEGATIVES, i.getText(), getCoords(i))); }
	;

idptIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.INDEPENDENTS, i.getText(), getCoords(i))); }
	;

patIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.PATTERNS, i.getText(), getCoords(i))); }
	;

extFuncIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=IDENT 
	{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.EXTERNAL_FUNCTIONS, i.getText(), getCoords(i))); }
	;

	
annotations returns [ Annotations annots = new DefaultAnnotations() ]
	: LBRACK keyValuePairs[annots] RBRACK
	;

annotationsWithCoords
	returns [
		Pair<DefaultAnnotations, de.unika.ipd.grgen.parser.Coords> res =
			new Pair<DefaultAnnotations, de.unika.ipd.grgen.parser.Coords>(
				new DefaultAnnotations(), Coords.getInvalid()
			)
	]
	: l=LBRACK keyValuePairs[res.first] RBRACK
		{ res.second = getCoords(l); }
	;

keyValuePairs [ Annotations annots ]
	: keyValuePair[annots] (COMMA keyValuePair[annots])*
	;

keyValuePair [ Annotations annots ]
	: id=IDENT
		(
			ASSIGN c=constant
			{ annots.put(id.getText(), ((ConstNode) c).getValue()); }
		|
			{ annots.put(id.getText(), true); }
		)
	;

identList [ Collection<String> strings ]
	: fid=IDENT { strings.add(fid.getText()); }
		( COMMA sid=IDENT { strings.add(sid.getText()); } )*
	;

memberIdentUse returns [ IdentNode res = env.getDummyIdent() ]
	: i=memberIdent
		{ if(i!=null) res = new IdentNode(env.occurs(ParserEnvironment.ENTITIES, i.getText(), getCoords(i))); }
	;

//////////////////////////////////////////
// Expressions
//////////////////////////////////////////

	
assignmentOrMethodCall [ boolean onLHS, int context, PatternGraphNode directlyNestingLHSGraph ] returns [ EvalStatementNode res = null ]
options { k = 4; }
	@init{
		int cat = -1; // compound assign type
		int ccat = CompoundAssignNode.NONE; // changed compound assign type
		BaseNode tgtChanged = null;
		CollectNode<ExprNode> subpatternConn = new CollectNode<ExprNode>();
		boolean yielded = false;
	}

	: owner=entIdentUse	d=DOT member=entIdentUse a=ASSIGN e=expr[false] //'false' because this rule is not used for the assignments in enum item decls
		{ res = new AssignNode(getCoords(a), new QualIdentNode(getCoords(d), owner, member), e, context); }
		{ if(onLHS) reportError(getCoords(d), "Assignment to an attribute is forbidden in LHS eval, only yield assignment to a def variable allowed."); }
	|
	  (y=YIELD { yielded = true; })? variable=entIdentUse a=ASSIGN e=expr[false]
		{ res = new AssignNode(getCoords(a), new IdentExprNode(variable, yielded), e, context); }
	|
	  vis=visited a=ASSIGN e=expr[false]
		{ res = new AssignVisitedNode(getCoords(a), vis, e); }
		{ if(onLHS) reportError(getCoords(a), "Assignment to a visited flag is forbidden in LHS eval."); }
	| 
	  owner=entIdentUse	d=DOT member=entIdentUse LBRACK idx=expr[false] RBRACK a=ASSIGN e=expr[false] //'false' because this rule is not used for the assignments in enum item decls
		{ res = new AssignIndexedNode(getCoords(a), new QualIdentNode(getCoords(d), owner, member), e, idx); }
		{ if(onLHS) reportError(getCoords(d), "Indexed assignment to an attribute is forbidden in LHS eval, only yield indexed assignment to a def variable allowed."); }
	|
	  (y=YIELD { yielded = true; })? variable=entIdentUse LBRACK idx=expr[false] RBRACK a=ASSIGN e=expr[false]
		{ res = new AssignIndexedNode(getCoords(a), new IdentExprNode(variable, yielded), e, idx); }
	| 
	  owner=entIdentUse d=DOT member=entIdentUse DOT method=memberIdentUse params=paramExprs[false]
		{ res = new MethodCallNode(new QualIdentNode(getCoords(d), owner, member), method, params); }
		{ if(onLHS) reportError(getCoords(d), "Method call on an attribute is forbidden in LHS eval, only yield method call to a def variable allowed."); }
	|
	  (y=YIELD { yielded = true; })? variable=entIdentUse DOT method=memberIdentUse params=paramExprs[false]
		{ res = new MethodCallNode(new IdentExprNode(variable, yielded), method, params); }
	| 
	  owner=entIdentUse d=DOT member=entIdentUse 
		(BOR_ASSIGN { cat = CompoundAssignNode.UNION; } | BAND_ASSIGN { cat = CompoundAssignNode.INTERSECTION; }
			| BACKSLASH_ASSIGN { cat = CompoundAssignNode.WITHOUT; } | PLUS_ASSIGN { cat = CompoundAssignNode.CONCATENATE; })
		e=expr[false] ( at=assignTo { ccat = at.ccat; tgtChanged = at.tgtChanged; } )?
			{ res = new CompoundAssignNode(getCoords(a), new QualIdentNode(getCoords(d), owner, member), cat, e, ccat, tgtChanged); }
			{ if(onLHS) reportError(getCoords(d), "Assignment to an attribute is forbidden in LHS eval, only yield assignment to a def variable allowed."); }
			{ if(cat==CompoundAssignNode.CONCATENATE && ccat!=CompoundAssignNode.NONE) reportError(getCoords(d), "No change assignment allowed for array|queue concatenation."); }
	|
	  (y=YIELD { yielded = true; })? variable=entIdentUse 
		(BOR_ASSIGN { cat = CompoundAssignNode.UNION; } | BAND_ASSIGN { cat = CompoundAssignNode.INTERSECTION; } 
			| BACKSLASH_ASSIGN { cat = CompoundAssignNode.WITHOUT; } | PLUS_ASSIGN { cat = CompoundAssignNode.CONCATENATE; })
		e=expr[false] ( at=assignTo { ccat = at.ccat; tgtChanged = at.tgtChanged; } )?
			{ res = new CompoundAssignNode(getCoords(a), new IdentExprNode(variable, yielded), cat, e, ccat, tgtChanged); }
			{ if(cat==CompoundAssignNode.CONCATENATE && ccat!=CompoundAssignNode.NONE) reportError(getCoords(d), "No change assignment allowed for array|queue concatenation."); }
	|
	  f=FOR LBRACE pushScopeStr["for iterated", getCoords(f)] variable=entIdentDecl IN i=iterIdentUse SEMI aomc=assignmentOrMethodCall[onLHS, context, directlyNestingLHSGraph] RBRACE popScope
		{ res = new IteratedAccumulationYieldNode(getCoords(f), new VarDeclNode(variable, IdentNode.getInvalid(), directlyNestingLHSGraph, context), i, aomc); }
	;
	
assignTo returns [ int ccat = CompoundAssignNode.NONE, BaseNode tgtChanged = null ]
	: (ASSIGN_TO { $ccat = CompoundAssignNode.ASSIGN; }
		| BOR_TO { $ccat = CompoundAssignNode.UNION; }
		| BAND_TO { $ccat = CompoundAssignNode.INTERSECTION; })
	  tgtc=assignToTgt { $tgtChanged = tgtc; }
	;

assignToTgt returns [ BaseNode tgtChanged = null ]
options { k = 4; }
@init{ boolean yielded=false; }
	: tgtOwner=entIdentUse d=DOT tgtMember=entIdentUse { tgtChanged = new QualIdentNode(getCoords(d), tgtOwner, tgtMember); }
	    | (y=YIELD { yielded = true; })? tgtVariable=entIdentUse { tgtChanged = new IdentExprNode(tgtVariable, yielded); }
	    | vis=visited { tgtChanged = vis; }
	;

expr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=condExpr[inEnumInit] { res = e; }
	;

condExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: op0=logOrExpr[inEnumInit] { res=op0; }
		( t=QUESTION op1=expr[inEnumInit] COLON op2=condExpr[inEnumInit]
			{
				OpNode cond=makeOp(t);
				cond.addChild(op0);
				cond.addChild(op1);
				cond.addChild(op2);
				res=cond;
			}
		)?
	;

logOrExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=logAndExpr[inEnumInit] { res = e; }
		( t=LOR op=logAndExpr[inEnumInit]
			{ res=makeBinOp(t, res, op); }
		)*
	;

logAndExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=bitOrExpr[inEnumInit] { res = e; }
		( t=LAND op=bitOrExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

bitOrExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=bitXOrExpr[inEnumInit] { res = e; }
		( t=BOR op=bitXOrExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

bitXOrExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=bitAndExpr[inEnumInit] { res = e; }
		( t=BXOR op=bitAndExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

bitAndExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=exceptExpr[inEnumInit] { res = e; }
		( t=BAND op=exceptExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

exceptExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=eqExpr[inEnumInit] { res = e; }
		( t=BACKSLASH op=eqExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

eqOp returns [ Token t = null ]
	: e=EQUAL { t = e; }
	| n=NOT_EQUAL { t = n; }
	;

eqExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=relExpr[inEnumInit] { res = e; }
		( t=eqOp op=relExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

relOp returns [ Token t = null ]
	: lt=LT { t = lt; }
	| le=LE { t = le; }
	| gt=GT { t = gt; }
	| ge=GE { t = ge; }
	| in=IN { t = in; }
	;

relExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=shiftExpr[inEnumInit] { res = e; }
		( t=relOp op=shiftExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

shiftOp returns [ Token res = null ]
	: l=SL { res = l; }
	| r=SR { res = r; }
	| b=BSR { res = b; }
	;

shiftExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=addExpr[inEnumInit] { res = e; }
		( t=shiftOp op=addExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

addOp returns [ Token t = null ]
	: p=PLUS { t = p; }
	| m=MINUS { t = m; }
	;

addExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=mulExpr[inEnumInit] { res = e; }
		( t=addOp op=mulExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

mulOp returns [ Token t = null ]
	: s=STAR { t = s; }
	| m=MOD { t = m; }
	| d=DIV { t = d; }
	;


mulExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: e=unaryExpr[inEnumInit] { res = e; }
		( t=mulOp op=unaryExpr[inEnumInit]
			{ res = makeBinOp(t, res, op); }
		)*
	;

unaryExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: t=TILDE op=unaryExpr[inEnumInit]
		{ res = makeUnOp(t, op); }
	| n=NOT op=unaryExpr[inEnumInit]
		 { res = makeUnOp(n, op); }
	| m=MINUS op=unaryExpr[inEnumInit]
		{
			OpNode neg = new ArithmeticOpNode(getCoords(m), OperatorSignature.NEG);
			neg.addChild(op);
			res = neg;
		}
	| PLUS e=unaryExpr[inEnumInit] { res = e; }
	| (LPAREN typeIdentUse RPAREN unaryExpr[false])
		=> p=LPAREN id=typeIdentUse RPAREN op=unaryExpr[inEnumInit]
		{
			res = new CastNode(getCoords(p), id, op);
		}
	| e=primaryExpr[inEnumInit] ((LBRACK|DOT) => e=selectorExpr[e, inEnumInit])* { res = e; }
	; 

primaryExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
options { k = 3; }
	: e=visited { res = e; }
	| e=randomExpr { res = e; }
	| e=nameOf { res = e; }
	| e=identExpr { res = e; }
	| e=constant { res = e; }
	| e=enumItemExpr { res = e; }
	| e=typeOf { res = e; }
	| e=initContainerExpr { res = e; }
	| e=externalFunctionInvocationExpr[inEnumInit] { res = e; }
	| LPAREN e=expr[inEnumInit] { res = e; } RPAREN
	| p=PLUSPLUS { reportError(getCoords(p), "increment operator \"++\" not supported"); }
	| q=MINUSMINUS { reportError(getCoords(q), "decrement operator \"--\" not supported"); }
	;

visited returns [ VisitedNode res ]
	: v=VISITED LPAREN elem=entIdentUse 
		( COMMA idExpr=expr[false] RPAREN
			{ res = new VisitedNode(getCoords(v), idExpr, elem); }
		| RPAREN
			{ res = new VisitedNode(getCoords(v), new IntConstNode(getCoords(v), 0), elem); }
		)
		{ reportWarning(getCoords(v), "visited in function notation deprecated, use element.visited[flag-id] instead"); }
	|
		elem=entIdentUse DOT v=VISITED  
		( (LBRACK) => LBRACK idExpr=expr[false] RBRACK // [ starts a visited flag expression, not a following map access selector expression
			{ res = new VisitedNode(getCoords(v), idExpr, elem); }
		| 
			{ res = new VisitedNode(getCoords(v), new IntConstNode(getCoords(v), 0), elem); }
		)
	;

randomExpr returns [ ExprNode res = env.initExprNode() ]
	: r=RANDOM LPAREN 
		( numExpr=expr[false] RPAREN
			{ res = new RandomNode(getCoords(r), numExpr); }
		| RPAREN
			{ res = new RandomNode(getCoords(r), null); }
		)
	;

nameOf returns [ ExprNode res = env.initExprNode() ]
	: n=NAMEOF LPAREN (id=entIdentUse)? RPAREN { res = new NameofNode(getCoords(n), id); }
	;

typeOf returns [ ExprNode res = env.initExprNode() ]
	: t=TYPEOF LPAREN id=entIdentUse RPAREN { res = new TypeofNode(getCoords(t), id); }
	;
	
initContainerExpr returns [ ExprNode res = env.initExprNode() ]
	: MAP LT keyType=typeIdentUse COMMA valueType=typeIdentUse GT e1=initMapExpr[null, MapTypeNode.getMapType(keyType, valueType)] { res = e1; }
	| SET LT valueType=typeIdentUse GT e2=initSetExpr[null, SetTypeNode.getSetType(valueType)] { res = e2; }
	| ARRAY LT valueType=typeIdentUse GT e3=initArrayExpr[null, ArrayTypeNode.getArrayType(valueType)] { res = e3; }
	| QUEUE LT valueType=typeIdentUse GT e4=initQueueExpr[null, QueueTypeNode.getQueueType(valueType)] { res = e4; }
	| (LBRACE expr[false] RARROW) => e1=initMapExpr[null, null] { res = e1; }
	| (LBRACE) => e2=initSetExpr[null, null] { res = e2; }
	| (LBRACK) => e3=initArrayExpr[null, null] { res = e3; }
	| (RBRACK) => e4=initQueueExpr[null, null] { res = e4; }
	;
	
constant returns [ ExprNode res = env.initExprNode() ]
	: b=NUM_BYTE
		{ res = new ByteConstNode(getCoords(b), Byte.parseByte(ByteConstNode.removeSuffix(b.getText()), 10)); }
	| sh=NUM_SHORT
		{ res = new ShortConstNode(getCoords(sh), Short.parseShort(ShortConstNode.removeSuffix(sh.getText()), 10)); }
	| i=NUM_INTEGER
		{ res = new IntConstNode(getCoords(i), Integer.parseInt(i.getText(), 10)); }
	| l=NUM_LONG
		{ res = new LongConstNode(getCoords(l), Long.parseLong(LongConstNode.removeSuffix(l.getText()), 10)); }
	| hb=NUM_HEX_BYTE
		{ res = new ByteConstNode(getCoords(hb), Byte.parseByte(ByteConstNode.removeSuffix(hb.getText().substring(2)), 16)); }
	| hsh=NUM_HEX_SHORT
		{ res = new ShortConstNode(getCoords(hsh), Short.parseShort(ShortConstNode.removeSuffix(hsh.getText().substring(2)), 16)); }
	| hi=NUM_HEX
		{ res = new IntConstNode(getCoords(hi), Integer.parseInt(hi.getText().substring(2), 16)); }
	| hl=NUM_HEX_LONG
		{ res = new LongConstNode(getCoords(hl), Long.parseLong(LongConstNode.removeSuffix(hl.getText().substring(2)), 16)); }
	| f=NUM_FLOAT
		{ res = new FloatConstNode(getCoords(f), Float.parseFloat(f.getText())); }
	| d=NUM_DOUBLE
		{ res = new DoubleConstNode(getCoords(d), Double.parseDouble(d.getText())); }
	| s=STRING_LITERAL
		{
			String buff = s.getText();
			// Strip the " from the string
			buff = buff.substring(1, buff.length() - 1);
			res = new StringConstNode(getCoords(s), buff);
		}
	| tt=TRUE
		{ res = new BoolConstNode(getCoords(tt), true); }
	| ff=FALSE
		{ res = new BoolConstNode(getCoords(ff), false); }
	| n=NULL
		{ res = new NullConstNode(getCoords(n)); }
	;

identExpr returns [ ExprNode res = env.initExprNode() ]
	@init{ IdentNode id; }

	: i=IDENT
		{
			// Entity names can overwrite type names
			if(env.test(ParserEnvironment.ENTITIES, i.getText()) || !env.test(ParserEnvironment.TYPES, i.getText()))
				id = new IdentNode(env.occurs(ParserEnvironment.ENTITIES, i.getText(), getCoords(i)));
			else
				id = new IdentNode(env.occurs(ParserEnvironment.TYPES, i.getText(), getCoords(i)));
			res = new IdentExprNode(id);
		}
	;

globalsAccessExpr returns [ ExprNode res = env.initExprNode() ]
	@init{ IdentNode id; }

	: DOUBLECOLON i=IDENT
		{
			id = new IdentNode(env.occurs(ParserEnvironment.ENTITIES, i.getText(), getCoords(i)));
			res = new GlobalsAccessExprNode(id);
		}
	;

enumItemAcc returns [ EnumExprNode res = null ]
	: tid=typeIdentUse d=DOUBLECOLON id=entIdentUse
	{ res = new EnumExprNode(getCoords(d), tid, id); }
	;

enumItemExpr returns [ ExprNode res = env.initExprNode() ]
	: n=enumItemAcc { res = new DeclExprNode(n); }
	;

externalFunctionInvocationExpr [ boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	: id=extFuncIdentUse params=paramExprs[inEnumInit]
		{
			if((id.toString()=="min" || id.toString()=="max" || id.toString()=="pow") && params.getChildren().size()==2
				|| (id.toString()=="incoming" || id.toString()=="outgoing") && params.getChildren().size()>=1 && params.getChildren().size()<=3) {
				res = new FunctionInvocationExprNode(id, params, env);
			} else {
				res = new ExternalFunctionInvocationExprNode(id, params);
			}
		}
	;
	
selectorExpr [ ExprNode target, boolean inEnumInit ] returns [ ExprNode res = env.initExprNode() ]
	:	l=LBRACK key=expr[inEnumInit] RBRACK { res = new IndexedAccessExprNode(getCoords(l), target, key); }
	|	d=DOT id=memberIdentUse
		(
			params=paramExprs[inEnumInit]
			{
				res = new MethodInvocationExprNode(target, id, params);
			}
		| 
			{
				res = new MemberAccessExprNode(getCoords(d), target, id);
			}
		)
	;
	
paramExprs [boolean inEnumInit] returns [ CollectNode<ExprNode> params = new CollectNode<ExprNode>(); ]
	:	LPAREN
		(
			e=expr[inEnumInit] { params.addChild(e); }
			( COMMA e=expr[inEnumInit] { params.addChild(e); } ) *
		)?
		RPAREN
	;

//////////////////////////////////////////
// Range Spec
//////////////////////////////////////////


rangeSpec returns [ RangeSpecNode res = null ]
	@init{
		lower = 0; upper = RangeSpecNode.UNBOUND;
		de.unika.ipd.grgen.parser.Coords coords = de.unika.ipd.grgen.parser.Coords.getInvalid();
		// range allows [*], [+], [?], [c:*], [c], [c:d]; no range equals [*]
	}

	:
		(
			l=LBRACK { coords = getCoords(l); }
			(
				STAR { lower=0; upper=RangeSpecNode.UNBOUND; }
			|
				PLUS { lower=1; upper=RangeSpecNode.UNBOUND; }
			|
				QUESTION { lower=0; upper=1; }
			|
				lower=integerConst
				(
					COLON ( STAR { upper=RangeSpecNode.UNBOUND; } | upper=integerConst )
				|
					{ upper = lower; }
				)
			)
			RBRACK
		)?
		{ res = new RangeSpecNode(coords, lower, upper); }
	;

rangeSpecXgrsLoop returns [ RangeSpecNode res = null ]
	@init{
		lower = 1; upper = 1;
		de.unika.ipd.grgen.parser.Coords coords = de.unika.ipd.grgen.parser.Coords.getInvalid();
		// range allows [*], [+], [c:*], [c], [c:d]; no range equals 1:1
	}

	:
		(
			l=LBRACK { coords = getCoords(l); }
			(
				STAR { lower=0; upper=RangeSpecNode.UNBOUND; }
			|
				PLUS { lower=1; upper=RangeSpecNode.UNBOUND; }
			|
				lower=integerConst
				(
					COLON ( STAR { upper=RangeSpecNode.UNBOUND; } | upper=integerConst )
				|
					{ upper = lower; }
				)
			)
			RBRACK
		)?
		{ res = new RangeSpecNode(coords, lower, upper); }
	;

integerConst returns [ long value = 0 ]
	: i=NUM_INTEGER
		{ value = Long.parseLong(i.getText()); }
	;



////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Symbols
////////////////////////////////////////////////////////////////////////////////////////////////////////////////



QUESTION		:	'?'		;
QUESTIONMINUS	:	'?-'	;
MINUSQUESTION	:	'-?'	;
QMMQ			:	'?--?'	;
LPAREN			:	'('		;
RPAREN			:	')'		;
LBRACK			:	'['		;
RBRACK			:	']'		;
LBRACE			:	'{'		;
LBRACEMINUS		:	'{-'	;
LBRACEPLUS		:	'{+'	;
RBRACE			:	'}'		;
COLON			:	':'		;
DOUBLECOLON     :   '::'    ;
COMMA			:	','		;
DOT 			:	'.'		;
ASSIGN			:	'='		;
BOR_ASSIGN		:	'|='	;
BAND_ASSIGN		:	'&='	;
BACKSLASH_ASSIGN:	'\\='	;
ASSIGN_TO		:	'=>'	;
BOR_TO			:	'|>'	;
BAND_TO			:	'&>'	;
EQUAL			:	'=='	;
NOT         	:	'!'		;
TILDE			:	'~'		;
STRUCTURAL_EQUAL:	'~~'	;
NOT_EQUAL		:	'!='	;
SL				:	'<<'	;
SR				:	'>>'	;
BSR				:	'>>>'	;
DIV				:	'/'		;
PLUS			:	'+'		;
PLUS_ASSIGN		:	'+='	;
MINUS			:	'-'		;
STAR			:	'*'		;
MOD				:	'%'		;
GE				:	'>='	;
GT				:	'>'		;
LE				:	'<='	;
LT				:	'<'		;
RARROW			:	'->'	;
LARROW			:	'<-'	;
LRARROW			:	'<-->'	;
DOUBLE_LARROW	:	'<--'	;
DOUBLE_RARROW	:	'-->'	;
BXOR			:	'^'		;
BOR				:	'|'		;
LOR				:	'||'	;
BAND			:	'&'		;
LAND			:	'&&'	;
SEMI			:	';'		;
DOUBLE_SEMI		:	';;'	;
BACKSLASH		:	'\\'	;
PLUSPLUS		:	'++'	;
MINUSMINUS		:	'--'	;
DOLLAR          :   '$'     ;
THENLEFT		:	'<;'	;
THENRIGHT		:	';>'	;
AT				:   '@'		;

// Whitespace -- ignored
WS	:	(	' '
		|	'\t'
		|	'\f'
		|	'\r'
		|	'\n'
		)+
		{ $channel=HIDDEN; }
	;

// single-line comment
SL_COMMENT
	: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;

// multiple-line comment
ML_COMMENT
	:   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
	;

fragment NUM_BYTE: ;
fragment NUM_SHORT: ;
fragment NUM_INTEGER: ;
fragment NUM_LONG: ;
fragment NUM_FLOAT: ;
fragment NUM_DOUBLE: ;
NUMBER
   : ('0'..'9')+
   ( ('.' '0'..'9') => '.' ('0'..'9')+
     (   ('f'|'F')    { $type = NUM_FLOAT; }
       | ('d'|'D')?   { $type = NUM_DOUBLE; }
     )
   | ('y'|'Y') { $type = NUM_BYTE; }
   | ('s'|'S') { $type = NUM_SHORT; }
   | { $type = NUM_INTEGER; }
   | ('l'|'L') { $type = NUM_LONG; }
   )
   ;


fragment NUM_HEX_BYTE: ;
fragment NUM_HEX_SHORT: ;
fragment NUM_HEX_LONG: ;
NUM_HEX
	: '0' 'x' ('0'..'9' | 'a' .. 'f' | 'A' .. 'F')+
	( ('y'|'Y') { $type = NUM_HEX_BYTE; }
	| ('s'|'S') { $type = NUM_HEX_SHORT; }
	| { $type = NUM_HEX; }
	| ('l'|'L') { $type = NUM_HEX_LONG; }
	)
	;

fragment
ESC
	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\')
		;

STRING_LITERAL
	:	'"' (ESC|~('"'|'\\'))* '"'
	;

INCLUDE
  : '#include' WS s=STRING_LITERAL {
  	$channel=HIDDEN;
	String file = s.getText();
	file = file.substring(1,file.length()-1);
	env.pushFile(this, new File(file));
  }
  ;

ABSTRACT : 'abstract';
ACTIONS : 'actions';
ALTERNATIVE : 'alternative';
ARBITRARY : 'arbitrary';
ARRAY : 'array';
AUTO : 'auto';
BREAK : 'break';
CLASS : 'class';
COPY : 'copy';
CONNECT : 'connect';
CONST : 'const';
DEF : 'def';
DELETE : 'delete';
DIRECTED : 'directed';
EDGE : 'edge';
EMIT : 'emit';
EMITHERE : 'emithere';
ENUM : 'enum';
EVAL : 'eval';
EVALHERE : 'evalhere';
EXACT : 'exact';
EXEC : 'exec';
EXTENDS : 'extends';
FALSE : 'false';
FOR : 'for';
HOM : 'hom';
IF : 'if';
IN : 'in';
INDEPENDENT : 'independent';
INDUCED : 'induced';
ITERATED : 'iterated';
MAP : 'map';
MATCH : 'match';
MODEL : 'model';
MODIFY : 'modify';
MULTIPLE : 'multiple';
NAMEOF : 'nameof';
NEGATIVE : 'negative';
NODE : 'node';
NULL : 'null';
OPTIONAL : 'optional';
PATTERN : 'pattern';
PATTERNPATH : 'patternpath';
QUEUE : 'queue';
RANDOM : 'random';
REPLACE : 'replace';
RETURN : 'return';
RULE : 'rule';
SEQUENCE : 'sequence';
SET : 'set';
TEST : 'test';
TRUE : 'true';
TYPEOF : 'typeof';
UNDIRECTED : 'undirected';
USING : 'using';
VALLOC : 'valloc';
VISITED : 'visited';
YIELD : 'yield';

IDENT : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')* ;
