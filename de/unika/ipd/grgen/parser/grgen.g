
header {
    package de.unika.ipd.grgen.parser;

		import java.util.Iterator;
		import java.util.Map;
		import java.util.HashMap;
		
    import de.unika.ipd.grgen.ast.*;
    import de.unika.ipd.grgen.util.report.*;
    import de.unika.ipd.grgen.Main;
}

/**
 * GRGen grammar
 * @version 0.1
 * @author Sebastian Hack
 */

class GRParser extends Parser;
options {
    k=3;
//    codeGenMakeSwitchThreshold = 2;
//	codeGenBitsetTestThreshold = 3;
	defaultErrorHandler = true;   
	buildAST = false;
//  ASTLabelType = "de.unika.ipd.grgen.ast.BaseNode";
}

tokens {
    DECL_GROUP;
    DECL_TEST;
    DECL_TYPE;
    DECL_NODE;
    DECL_EDGE;
    DECL_BASIC;
    TYPE_NODE;
    TYPE_EDGE;
    SIMPLE_CONN;
    GROUP_CONN;
    TEST_BODY;
    PATTERN_BODY;
    SUBGRAPH_SPEC;
    CONN_DECL;
    CONN_CONT;
    MAIN;
}

{
		/// did we encounter errors during parsing
		private boolean hadError;
	
		/// the current scope
    private Scope currScope;
    
    /// The root scope
    private Scope rootScope;
    
    /// The symbol table
    private SymbolTable symbolTable;

		/// the error reporter 
    private ErrorReporter reporter;
   
    /// unique ids for anonymous identifiers
    private static int uniqueId = 1;
    
    /// a dummy identifier
    private IdentNode dummyIdent;

		private IdentNode edgeRoot, nodeRoot;

		private CollectNode mainChilds;

		private static Map opIds = new HashMap();

		private static final void putOpId(int tokenId, int opId) {
			opIds.put(new Integer(tokenId), new Integer(opId));
		}

		static {
			putOpId(QUESTION, Operator.COND);
			putOpId(DOT, Operator.QUAL);
			putOpId(EQUAL, Operator.EQ);
			putOpId(NOT_EQUAL, Operator.NE);
			putOpId(NOT, Operator.LOG_NOT);
			putOpId(TILDE, Operator.BIT_NOT);
			putOpId(SL, Operator.SHL);
			putOpId(SR, Operator.SHR);
			putOpId(BSR, Operator.BIT_SHR);
			putOpId(DIV, Operator.DIV);
			putOpId(STAR, Operator.MUL);
			putOpId(MOD, Operator.MOD);
			putOpId(PLUS, Operator.ADD);
			putOpId(MINUS, Operator.SUB);
			putOpId(GE, Operator.GE);
			putOpId(GT, Operator.GT);
			putOpId(LE, Operator.LE);
			putOpId(LT, Operator.LT);
			putOpId(BAND, Operator.BIT_AND);
			putOpId(BOR, Operator.BIT_OR);
			putOpId(BXOR, Operator.BIT_XOR);
			putOpId(BXOR, Operator.BIT_XOR);
			putOpId(LAND, Operator.LOG_AND);
			putOpId(LOR, Operator.LOG_OR);
		};
    
  
    private OpNode makeOp(antlr.Token t) {
    	Coords c = new Coords(t, this);
			Integer opId = (Integer) opIds.get(new Integer(t.getType()));
			assert opId != null : "Invalid operator ID";
    	return new OpNode(getCoords(t), opId.intValue());
    }
    
    private OpNode makeBinOp(antlr.Token t, BaseNode op0, BaseNode op1) {
    	OpNode res = makeOp(t);
    	res.addChild(op0);
    	res.addChild(op1);
    	return res;
    }
    
    private OpNode makeUnOp(antlr.Token t, BaseNode op) {
    	OpNode res = makeOp(t);
    	res.addChild(op);
    	return res;
    }    	
    
    private int makeId() {
    	return uniqueId++;
    }
    
    private BaseNode initNode() {
    	return BaseNode.getErrorNode();
    }

		private IdentNode getDummyIdent() {
			return IdentNode.getInvalid();
		}

		private Coords getCoords(antlr.Token tok) {
			return new Coords(tok, this);
		}
		
    private Symbol.Definition define(String name, Coords coords) {
      Symbol sym = symbolTable.get(name);
      return currScope.define(sym, coords);
    }
    
    private IdentNode defineAnonymous(String part, Coords coords) {
    	return new IdentNode(currScope.defineAnonymous(part, coords));
    }
    
    private IdentNode predefine(String name) {
      Symbol sym = symbolTable.get(name);
      return new IdentNode(rootScope.define(sym, BaseNode.BUILTIN));
    }
    
    private IdentNode predefineType(String str, TypeNode type) {
    	IdentNode id = predefine(str);
    	id.setDecl(new TypeDeclNode(id, type));
    	return id;
    }
    
    private void makeBuiltin() {
    	nodeRoot = predefineType("Node", new NodeTypeNode(new CollectNode(), new CollectNode()));
			edgeRoot = predefineType("Edge", new EdgeTypeNode(new CollectNode(), new CollectNode()));
			
			predefineType("int", BasicTypeNode.intType);
			predefineType("string", BasicTypeNode.stringType);
			predefineType("boolean", BasicTypeNode.booleanType);
    }
    
    public void init(ErrorReporter reporter) {
    	this.reporter = reporter;
    	hadError = false;
      symbolTable = new SymbolTable();
      
      symbolTable.enterKeyword("int");
      symbolTable.enterKeyword("string");
      symbolTable.enterKeyword("boolean");
      
      rootScope = new Scope(reporter);
      currScope = rootScope;
      
      makeBuiltin();
      mainChilds = new CollectNode();
			mainChilds.addChild(edgeRoot.getDecl());
			mainChilds.addChild(nodeRoot.getDecl());
    }
    
	  public void reportError(RecognitionException arg0) {
  	  hadError = true;
	  	reporter.error(new Coords(arg0), arg0.getErrorMessage());
    }

	  public void reportError(String arg0) {
	  	hadError = true;
			reporter.error(arg0);
  	}
  	
  	public void reportWarning(String arg0) {
  		reporter.warning(arg0);
  	}
  	
  	public boolean hadError() {
  		return hadError;
  	}


}


/**
 * Build a main node. 
 * It has a collect node with the decls as child
 */
text returns [ BaseNode main = initNode() ]
  : { 
  		CollectNode n;
  		IdentNode id;
  	} 
  	"unit" id=identDecl SEMI n=decls EOF { mainChilds.addChildren(n); } {
  		main = new UnitNode(id, getFilename());
  		main.addChild(mainChilds);
  		
  		// leave the root scope and finish all occurrences
  		currScope.leaveScope();
  	}
  	;       

/**
 * Decls make a collect node with all the decls as children
 */
decls returns [ CollectNode n = new CollectNode() ]
  : { BaseNode d; }
  
  (d=decl { n.addChild(d); } )* ;        

/**
 * A decl is a 
 * - group
 * - edge type
 * - node type
 */ 
decl returns [ BaseNode res = initNode() ]
  : res=actionDecl
  | res=edgeClassDecl 
  | res=nodeClassDecl 
  | res=enumDecl
  ;

/**
 * An edge class decl makes a new type decl node with the declaring id and 
 * a new edge type node as children
 */
edgeClassDecl returns [ BaseNode res = initNode() ] 
  : { 
  	  BaseNode body, ext;
  	  IdentNode id;
    } 
  
  "edge" "class"! id=identDecl ext=edgeExtends // COLON r:connectAssertion
        pushScope[id] LBRACE! body=edgeClassBody { 

		id.setDecl(new TypeDeclNode(id, new EdgeTypeNode(ext, body)));
		res = id;
  } RBRACE! popScope! 
  ;

connectAssertion returns [ BaseNode res = initNode() ]
	{ BaseNode src, tgt, srcRange, tgtRange; }
	: src=identUse srcRange=rangeSpec RARROW tgt=identUse tgtRange=rangeSpec
	;

nodeClassDecl! returns [ BaseNode res = initNode() ] 
  : {
  		BaseNode body, ext;
  		IdentNode id;
  	} 
  	
  	"node" "class"! id=identDecl ext=nodeExtends
        pushScope[id] LBRACE! body=nodeClassBody {

      id.setDecl(new TypeDeclNode(id, new NodeTypeNode(ext, body)));
		  res = id;
    } RBRACE! popScope! 
  ;

edgeExtends returns [ CollectNode c = new CollectNode() ] 
  : "extends" edgeExtendsCont[c]   	
  | { c.addChild(edgeRoot); }
  ;

edgeExtendsCont[ CollectNode c ] 
  : { BaseNode e; } 
    e=identUse { c.addChild(e); } 
    (COMMA! e=identUse { c.addChild(e); } )*
	;

nodeExtends returns [ CollectNode c = new CollectNode() ]
  : "extends" nodeExtendsCont[c]
  | { c.addChild(nodeRoot); }
  ;

nodeExtendsCont[ CollectNode c ] 
  : { BaseNode n; } n=identUse { c.addChild(n); }
    (COMMA! n=identUse { c.addChild(n); } )* ;

nodeClassBody returns [ CollectNode c = new CollectNode() ]
  : { BaseNode d; }
  (d=basicDecl { c.addChild(d); } SEMI!)*
  ;

edgeClassBody returns [ CollectNode c = new CollectNode() ] 
  : { BaseNode d; } 
    (d=basicDecl { c.addChild(d); } SEMI!)*
	; 
	
rangeSpec returns [ BaseNode res = initNode() ]
	{
		int lower = 1, upper = 1;
	}
	: (l:LBRACK lower=integerConst ( COLON { upper = RangeSpecNode.UNBOUND; } 
			( upper=integerConst )? )? RBRACK )? 
	{
			res = new RangeSpecNode(getCoords(l), lower, upper);
	}
	;
	
integerConst returns [ int value = 0 ] 
	: i:INTEGER {
		value = Integer.parseInt(i.getText());
	}
	; 
	
enumDecl returns [ BaseNode res = initNode() ]
	{
		IdentNode id;
		BaseNode c;
	}
	: "enum" id=identDecl pushScope[id] LBRACE c=enumList {
		BaseNode enumType = new EnumTypeNode();
		enumType.addChild(c);
		res = new TypeDeclNode(id, enumType);
		res.addChild(c);
	} RBRACE popScope;

enumList returns [ BaseNode res = initNode() ]
	{
		IdentNode id;
		res = new CollectNode(); 
	}
	:	id=identDecl { res.addChild(id); } 
		(COMMA id=identDecl { res.addChild(id); })*
	;

basicDecl returns [ BaseNode n = initNode() ] 
  : { 
  	  IdentNode id;
  	  BaseNode type;
    }
  
  id=identDecl COLON! type=identUse {
	
	id.setDecl(new MemberDeclNode(id, type));
	n = id;
};

/// groups have names and contain graph declarations
groupDecl returns [ BaseNode res = initNode() ]
  : {
  		IdentNode id;
  		CollectNode decls;
  	} 
  	"group" id=identDecl pushScope[id] LBRACE! decls=actionDecls {
	  	id.setDecl(new GroupDeclNode(id, decls));
	  	
	  	res = id;
  } RBRACE! popScope!;           

actionDecls returns [ CollectNode c = new CollectNode() ] 
  : { BaseNode d; } ( d=actionDecl { c.addChild(d); } )+;

/**
 * graph declarations contain
 * - rules
 * - tests
 */
actionDecl returns [ BaseNode res = initNode() ]
  : res=testDecl
  | res=ruleDecl
  ;

testDecl returns [ BaseNode res = initNode() ]
  : {
  		IdentNode id;
  		BaseNode tb, pattern;
  	}
  	"test" id=identDecl pushScope[id] LBRACE! pattern=patternPart {
      id.setDecl(new TestDeclNode(id, pattern));
      res = id;
  	} RBRACE! popScope!;           

ruleDecl returns [ BaseNode res = initNode() ]
  : {
  		IdentNode id;
  		BaseNode rb, left, right;
  		CollectNode redir = new CollectNode();
  		CollectNode eval = new CollectNode();
  		CollectNode cond = new CollectNode();
  }
  "rule" id=identDecl pushScope[id] LBRACE! 
  	left=patternPart 
		( condPart[cond] )?
  	right=replacePart 
  	( redirectPart[redir] )? {
  	id.setDecl(new RuleDeclNode(id, left, right, redir, cond, eval));
  	res = id;
  } RBRACE! popScope!;

parameters 
	: LPAREN paramList RPAREN
	| LPAREN RPAREN
	| 
	;

paramList
	: param (COMMA param)*
	;
	
param 
	{
		IdentNode id;
	}
	: inOutSpec paramType id=identDecl
	;
	
inOutSpec
	: "in"
	| "out"
	;
	
paramType
	: identUse
	;

patternPart returns [ BaseNode res = initNode() ]
  : p:"pattern" LBRACE! res=patternBody[getCoords(p)] RBRACE! 
  ;

replacePart returns [ BaseNode res = initNode() ]
	: r:"replace" LBRACE! res=patternBody[getCoords(r)] RBRACE!
	;

redirectPart [ CollectNode collect ]
	: r:"redirect" LBRACE! redirectBody[collect, getCoords(r)] RBRACE!
	;

evalPart [ BaseNode n ]
	: e:"eval" LBRACE RBRACE
	;
	
condPart [ BaseNode n ]
	: c:"cond" LBRACE condBody[n] RBRACE 
	;
	
condBody [ BaseNode n ]
	{ BaseNode e; }
	: (e=expr { n.addChild(e); } SEMI)*
	;
	
redirectBody [ CollectNode c, Coords coords ] 
	:	( redirectStmt[c] SEMI )*
	;
	
redirEdgeOcc returns [ Object[] res ]
	{
			BaseNode id;
			res = new Object[2];
			res[1] = new Boolean(false);
	}
	
  : MINUS id=identUse RARROW { res[0] = id; }
	| RARROW { res[0] = edgeRoot; 	}
	| LARROW id=identUse MINUS { 
		res[0] = id;
		res[1] = new Boolean(true);
	}
	| LARROW { 
		res[0] = edgeRoot;
		res[1] = new Boolean(true); 
	}
	;
	
redirectStmt [ BaseNode c ] 
	: {
		BaseNode src, tgt, to;
		Object[] redirEdge;
	}

	to=identUse COLON src=identUse redirEdge=redirEdgeOcc tgt=identUse {
		BaseNode edgeTypeId = (BaseNode) redirEdge[0];
		Boolean incoming = (Boolean) redirEdge[1];
		c.addChild(new RedirectionNode(src, edgeTypeId, tgt, to, incoming.booleanValue()));
	}
	;

/**
 * Pattern bodies consist of connection nodes
 * The connection nodes in the collect node from subgraphSpec are integrated
 * In the collect node of the pattern node.
 */
patternBody [ Coords coords ] returns [ BaseNode res = initNode() ]
  : { 
  		BaseNode s;
  		CollectNode connections = new CollectNode();
  		CollectNode singleNodes = new CollectNode();
  		
  	  res = new PatternNode(coords, connections, singleNodes);
    }
    ( patternStmt[connections, singleNodes] SEMI )*
  ;

patternStmt [ BaseNode connCollect, BaseNode snCollect ]
	{ BaseNode n; }
	: connections[connCollect, snCollect]
  | "node" nodeDecl ( COMMA nodeDecl )* 
  | "edge" edgeDecl ( COMMA edgeDecl )*
	;

connections [ BaseNode connColl, BaseNode snColl ]
  { BaseNode n; }
  : n=nodeOcc (continuation[n,connColl] | { 
  	snColl.addChild(new SingleNodeConnNode(n));
  })
  ;

/**
 * Acontinuation is a list of edge node pairs or a list of these pair lists, comma
 * seperated and delimited by parantheses.
 * all produced connection nodes are appended to the collect node
 */
continuation [ BaseNode left, BaseNode collect ]
  { BaseNode n; }
  : n=pair[left, collect] (continuation[n, collect])?
  | LPAREN continuation[left, collect] ( COMMA continuation[left, collect] )* RPAREN
  ;

/**
 * An edge node pair.
 * This rule builds a connection node with the parameter left, the edge and the nodeOcc
 * and appends this connection node to the children of coll.
 * The rule returns the right node (the one from the nodeOcc rule)
 */
pair [ BaseNode left, BaseNode coll ] returns [ BaseNode res = initNode() ]
	{ 
		BaseNode e;
		boolean negated = false;
  }
	:	(e=edge | e=negatedEdge { negated = true; }) res=nodeOcc {
			coll.addChild(new ConnectionNode(left, e, res, negated));
  	};

nodeOcc returns [ BaseNode res = initNode() ]
	{ CollectNode coll = new CollectNode(); }
  : (res=identUse | res=nodeDecl) 
  ;

nodeDecl returns [ BaseNode res = initNode() ]
  { 
    IdentNode id;
  	BaseNode type; 
  }
  : id=identDecl COLON! type=identUse {
  	
  	res = new NodeDeclNode(id, type);
    }            
  ;

negatedEdge returns [ BaseNode res = null ]
	: NOT MINUS res=identUse RARROW
	| NOT res=anonymousEdge[true]
	;

edge returns [ BaseNode res = null ] 
	: MINUS res=edgeDecl RARROW
	| MINUS res=identUse RARROW 
	| res=anonymousEdge[false]
	;
	
anonymousEdge [ boolean negated ] returns [ BaseNode res = null ]
	: m:RARROW {
		IdentNode id = defineAnonymous("edge", getCoords(m));
		res = new AnonymousEdgeDeclNode(id, edgeRoot, negated);
	}
	;		
	
edgeDecl returns [ BaseNode res = null ]
	{
		IdentNode id, type;
	}
	: id=identDecl COLON type=identUse {
		res = new EdgeDeclNode(id, type, false);
	}
	;


identList [ BaseNode c ]
	{ BaseNode id; }
	: id=identUse { c.addChild(id); } (COMMA id=identUse { c.addChild(id); })*
	; 

/**
 * declaration of an identifier
 */
identDecl returns [ IdentNode res = getDummyIdent() ]
  : i:IDENT {
  	
  		Symbol sym = symbolTable.get(i.getText());
  		Symbol.Definition def = currScope.define(sym, getCoords(i));
      res = new IdentNode(def);
    }
  ;        

/**
 * Represents the usage of an identifier. 
 * It is checked, whether the identifier is declared. The IdentNode
 * created by the definition is returned.
 */
identUse returns [ IdentNode res = getDummyIdent() ]
  : i:IDENT {
  	Symbol sym = symbolTable.get(i.getText());
  	Symbol.Occurrence occ = currScope.occurs(sym, getCoords(i));

	  res = new IdentNode(occ);
  }
  ;        

pushScope! [IdentNode name] options { defaultErrorHandler = false; } {
  currScope = currScope.newScope(name.toString());
  BaseNode.setCurrScope(currScope);    
} : ;

popScope! options { defaultErrorHandler = false; }  {
  if(currScope != rootScope)
      currScope = currScope.leaveScope();
  BaseNode.setCurrScope(currScope);    
    
} : ;

// Expressions


assignment : qualIdent ASSIGN expr
;

expr returns [ BaseNode res = initNode() ]
	: res=condExpr ;

condExpr returns [ BaseNode res = initNode() ] 
	{ BaseNode op0, op1, op2; }
	: op0=logOrExpr { res=op0; } (t:QUESTION op1=expr COLON op2=condExpr { 
		res=makeOp(t);
		res.addChild(op0);
		res.addChild(op1);
		res.addChild(op2);
	})?
;

logOrExpr returns [ BaseNode res = initNode() ]
	{ BaseNode op; }
	: res=logAndExpr (t:LOR op=logAndExpr {
		res=makeBinOp(t, res, op);
	})*
	;

logAndExpr returns [ BaseNode res = initNode() ]
	{ BaseNode op; }
	: res=bitOrExpr (t:LAND op=bitOrExpr {
		res = makeBinOp(t, res, op);
	})*
	;

bitOrExpr returns [ BaseNode res = initNode() ]
	{ BaseNode op; }
	: res=bitXOrExpr (t:BOR op=bitXOrExpr {
		res = makeBinOp(t, res, op);
	})*
	;

bitXOrExpr returns [ BaseNode res = initNode() ]
	{ BaseNode op; }
	: res=bitAndExpr (t:BXOR op=bitAndExpr {
		res = makeBinOp(t, res, op);
	})*
	;

bitAndExpr returns [ BaseNode res = initNode() ]
	{ BaseNode op; }
	: res=eqExpr (t:BAND op=eqExpr {
		res = makeBinOp(t, res, op);
	})*
	;

eqOp returns [ Token t = null ]
	: e:EQUAL { t=e; }
	| n:NOT_EQUAL { t=n; }
	;

eqExpr returns [ BaseNode res = initNode() ]
	{ 
		BaseNode op;
		Token t;
	}
	: res=relExpr (t=eqOp op=relExpr {
		res = makeBinOp(t, res, op);
	})*
	;

relOp returns [ Token t = null ]
	: lt:LT { t=lt; }
	| le:LE { t=le; }
	| gt:GT { t=gt; }		
	| ge:GE { t=ge; }
	;
	
relExpr returns [ BaseNode res  = initNode() ]
	{ 
		BaseNode op; 
		Token t;
	}
	: res=shiftExpr (t=relOp op=shiftExpr {
		res = makeBinOp(t, res, op);
	})*
	;

shiftOp returns [ Token res = null ]
	: l:SL { res=l; }
	| r:SR { res=r; }
	| b:BSR { res=b; }
	;

shiftExpr returns [ BaseNode res = initNode() ]
	{ 
		BaseNode op;
		Token t;
	}
	: res=addExpr (t=shiftOp op=addExpr {
		res = makeBinOp(t, res, op);
	})*
	;
	
addOp returns [ Token t = null ]	
	: p:PLUS { t=p; }
	| m:MINUS { t=m; }
	;
	
addExpr returns [ BaseNode res = initNode() ]
	{ 
		BaseNode op;
		Token t;
	}
	: res=mulExpr (t=addOp op=mulExpr {
		res = makeBinOp(t, res, op);
	})*
	;
	
mulOp returns [ Token t = null ]
	: s:STAR { t=s; }
	| m:MOD { t=m; }
	| d:DIV { t=d; }
	;

	
mulExpr returns [ BaseNode res = initNode() ]
	{ 
		BaseNode op;
		Token t;
	}
	: res=unaryExpr (t=mulOp op=unaryExpr {
		res = makeBinOp(t, res, op);
	})*
	;
	
unaryExpr returns [ BaseNode res = initNode() ]
	{ BaseNode op, id; }
	: t:TILDE op=unaryExpr {
		res = makeUnOp(t, op);
	}
	| n:NOT op=unaryExpr {
		res = makeUnOp(n, op);
	}
	| m:MINUS op=unaryExpr {
		res = new OpNode(getCoords(m), Operator.NEG);
		res.addChild(op);
	}
	| PLUS res=unaryExpr
	| p:LPAREN id=identUse RPAREN op=unaryExpr {
		res = new CastNode(getCoords(p));
		res.addChild(id);
		res.addChild(op);
	}
	| res=primaryExpr
	;
	
primaryExpr returns [ BaseNode res = initNode() ]
	: res=qualIdent
	| res=identExpr
	| res=constant
	| LPAREN res=expr RPAREN
	;
	
constant returns [ BaseNode res = initNode() ]
	: i:NUM_DEC {
		res = new IntConstNode(getCoords(i), Integer.parseInt(i.getText()));
	}
	| h:NUM_HEX {
		res = new IntConstNode(getCoords(h), Integer.parseInt(h.getText(), 16));
	}
	| s:STRING_LITERAL {
		res = new StringConstNode(getCoords(s), s.getText());
	}
	| t:"true" {
		res = new BoolConstNode(getCoords(t), true);
	}
	| f:"false" {
		res = new BoolConstNode(getCoords(f), false);
	}
	;	

identExpr returns [ BaseNode res = initNode() ]
	{ IdentNode id; }
	: id=identUse { 
		res = new IdentExprNode(id);
	}
	;	

qualIdent returns [ BaseNode res = initNode() ]
	{ BaseNode id; }
	: res=identExpr (d:DOT id=identExpr {
		res = new QualIdentNode(getCoords(d), res, id);
	})+
	;






// Lexer stuff

class GRLexer extends Lexer;

options {
	testLiterals=false;    // don't automatically test for literals
	k=4;                   // four characters of lookahead
	codeGenBitsetTestThreshold=20;
}

QUESTION		:	'?'		;
LPAREN			:	'('		;
RPAREN			:	')'		;
LBRACK			:	'['		;
RBRACK			:	']'		;
LBRACE			:	'{'		;
RBRACE			:	'}'		;
COLON			:	':'		;
COMMA			:	','		;
DOT 			:	'.'		;
ASSIGN			:	'='		;
EQUAL			:	"=="	;
NOT         	:	'!'		;
TILDE			:	'~'		;
NOT_EQUAL		:	"!="	;
SL			  : "<<" ;
SR 			  : ">>" ;
BSR       : ">>>" ;
DIV				:	'/'		;
PLUS			:	'+'		;
MINUS			:	'-'		;
STAR			:	'*'		;
MOD				:	'%'		;
GE				:	">="	;
GT				:	">"		;
LE				:	"<="	;
LT				:	'<'		;
RARROW    : "->"  ;
LARROW    : "<-"  ;
BXOR			:	'^'		;
BOR				:	'|'		;
LOR				:	"||"	;
BAND			:	'&'		;
LAND			:	"&&"	;
SEMI			:	';'		;

// Whitespace -- ignored
WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
			{ newline(); }
		)+
		{ $setType(Token.SKIP); }
	;

SL_COMMENT
  :	"//" (~('\n'|'\r'))* ('\n'|'\r'('\n')?)
        {
			$setType(Token.SKIP);
			newline();
		}
	;

// multiple-line comments
ML_COMMENT
  :	"/*"
		(	/*	'\r' '\n' can be matched in one alternative or by matching
				'\r' in one iteration and '\n' in another.  I am trying to
				handle any flavor of newline that comes in, but the language
				that allows both "\r\n" and "\r" and "\n" to all be valid
				newline is ambiguous.  Consequently, the resulting grammar
				must be ambiguous.  I'm shutting this warning off.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{ $setType(Token.SKIP); }
  ;
  
NUM_DEC
	: ('0'..'9')+
	;
	
NUM_HEX
	: '0' 'x' ('0'..'9' | 'a' .. 'f' | 'A' .. 'F')+
	;
	
protected	
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

IDENT
	options {testLiterals=true;}
	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
	;
