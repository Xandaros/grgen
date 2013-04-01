/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.0
 * Copyright (C) 2003-2013 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

/**
 * Generates the actions file for the SearchPlanBackend2 backend.
 * @author Moritz Kroll, Edgar Jakumeit
 */

package de.unika.ipd.grgen.be.Csharp;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

import de.unika.ipd.grgen.ir.*;
import de.unika.ipd.grgen.ir.exprevals.*;
import de.unika.ipd.grgen.ir.containers.*;

public class ActionsGen extends CSharpBase {
	// constants encoding different types of match parts
	// must be consecutive, beginning with MATCH_PART_NODES, ending with terminating dummy-element MATCH_PART_END
	final int MATCH_PART_NODES = 0;
	final int MATCH_PART_EDGES = 1;
	final int MATCH_PART_VARIABLES = 2;
	final int MATCH_PART_EMBEDDED_GRAPHS = 3;
	final int MATCH_PART_ALTERNATIVES = 4;
	final int MATCH_PART_ITERATEDS = 5;
	final int MATCH_PART_INDEPENDENTS = 6;
	final int MATCH_PART_END = 7;

	public ActionsGen(SearchPlanBackend2 backend, String nodeTypePrefix, String edgeTypePrefix) {
		super(nodeTypePrefix, edgeTypePrefix);
		be = backend;
		model = be.unit.getActionsGraphModel();
		mg = new ModifyGen(backend, nodeTypePrefix, edgeTypePrefix);
		modifyGenForComputations = new ModifyGen(backend, nodeTypePrefix, edgeTypePrefix);
	}

	/**
	 * Generates the subpatterns, actions, sequences, computations sourcecode for this unit.
	 */
	public void genActionlike() {
		StringBuffer sb = new StringBuffer();
		String filename = be.unit.getUnitName() + "Actions_intermediate.cs";

		System.out.println("  generating the " + filename + " file...");

		sb.append("// This file has been generated automatically by GrGen (www.grgen.net)\n"
				+ "// Do not modify this file! Any changes will be lost!\n"
				+ "// Generated from \"" + be.unit.getFilename() + "\" on " + new Date() + "\n"
				+ "\n"
				+ "using System;\n"
				+ "using System.Collections.Generic;\n"
				+ "using System.Collections;\n"
				+ "using System.Text;\n"
                + "using GRGEN_LIBGR = de.unika.ipd.grGen.libGr;\n"
                + "using GRGEN_LGSP = de.unika.ipd.grGen.lgsp;\n"
                + "using GRGEN_EXPR = de.unika.ipd.grGen.expression;\n"
				+ "using GRGEN_MODEL = de.unika.ipd.grGen.Model_" + be.unit.getActionsGraphModelName() + ";\n"
				+ "\n"
				+ "namespace de.unika.ipd.grGen.Action_" + be.unit.getUnitName() + "\n"
				+ "{\n");

		/////////////////////////////////////////////////////////
		
		for(Rule subpatternRule : be.unit.getSubpatternRules()) {
			genSubpattern(sb, subpatternRule);
		}

		for(Rule actionRule : be.unit.getActionRules()) {
			genAction(sb, actionRule);
		}

		for(Sequence sequence : be.unit.getSequences()) {
			genSequence(sb, sequence);
		}
		
		genComputations(sb);
		
		/////////////////////////////////////////////////////////
		
		sb.append("\tpublic class " + be.unit.getUnitName() + "_RuleAndMatchingPatterns : GRGEN_LGSP.LGSPRuleAndMatchingPatterns\n");
		sb.append("\t{\n");
		sb.append("\t\tpublic " + be.unit.getUnitName() + "_RuleAndMatchingPatterns()\n");
		sb.append("\t\t{\n");
		sb.append("\t\t\tsubpatterns = new GRGEN_LGSP.LGSPMatchingPattern["+be.unit.getSubpatternRules().size()+"];\n");
		sb.append("\t\t\trules = new GRGEN_LGSP.LGSPRulePattern["+be.unit.getActionRules().size()+"];\n");
		sb.append("\t\t\trulesAndSubpatterns = new GRGEN_LGSP.LGSPMatchingPattern["+
				be.unit.getSubpatternRules().size()+"+"+be.unit.getActionRules().size()+"];\n");
		sb.append("\t\t\tdefinedSequences = new GRGEN_LIBGR.DefinedSequenceInfo["+be.unit.getSequences().size()+"];\n");
		sb.append("\t\t\tcomputations = new GRGEN_LIBGR.ComputationInfo["+be.unit.getComputations().size()+"];\n");	
		int i = 0;
		for(Rule subpatternRule : be.unit.getSubpatternRules()) {
			sb.append("\t\t\tsubpatterns["+i+"] = Pattern_"+formatIdentifiable(subpatternRule)+".Instance;\n");
			sb.append("\t\t\trulesAndSubpatterns["+i+"] = Pattern_"+formatIdentifiable(subpatternRule)+".Instance;\n");
			++i;
		}
		int j = 0;
		for(Rule actionRule : be.unit.getActionRules()) {
			sb.append("\t\t\trules["+j+"] = Rule_"+formatIdentifiable(actionRule)+".Instance;\n");
			sb.append("\t\t\trulesAndSubpatterns["+i+"+"+j+"] = Rule_"+formatIdentifiable(actionRule)+".Instance;\n");
			++j;
		}
		i = 0;
		for(Sequence sequence : be.unit.getSequences()) {
			sb.append("\t\t\tdefinedSequences["+i+"] = SequenceInfo_"+formatIdentifiable(sequence)+".Instance;\n");
			++i;
		}
		i = 0;
		for(Computation computation : be.unit.getComputations()) {
			sb.append("\t\t\tcomputations["+i+"] = ComputationInfo_"+formatIdentifiable(computation)+".Instance;\n");
			++i;
		}
		sb.append("\t\t}\n");
		sb.append("\t\tpublic override GRGEN_LGSP.LGSPRulePattern[] Rules { get { return rules; } }\n");
		sb.append("\t\tprivate GRGEN_LGSP.LGSPRulePattern[] rules;\n");
		sb.append("\t\tpublic override GRGEN_LGSP.LGSPMatchingPattern[] Subpatterns { get { return subpatterns; } }\n");
		sb.append("\t\tprivate GRGEN_LGSP.LGSPMatchingPattern[] subpatterns;\n");
		sb.append("\t\tpublic override GRGEN_LGSP.LGSPMatchingPattern[] RulesAndSubpatterns { get { return rulesAndSubpatterns; } }\n");
		sb.append("\t\tprivate GRGEN_LGSP.LGSPMatchingPattern[] rulesAndSubpatterns;\n");
		sb.append("\t\tpublic override GRGEN_LIBGR.DefinedSequenceInfo[] DefinedSequences { get { return definedSequences; } }\n");
		sb.append("\t\tprivate GRGEN_LIBGR.DefinedSequenceInfo[] definedSequences;\n");
		sb.append("\t\tpublic override GRGEN_LIBGR.ComputationInfo[] Computations { get { return computations; } }\n");
		sb.append("\t\tprivate GRGEN_LIBGR.ComputationInfo[] computations;\n");
		sb.append("\t}\n");
		sb.append("\n");

		sb.append("// GrGen insert Actions here\n");
		sb.append("}\n");

		writeFile(be.path, filename, sb);
	}

	/**
	 * Generates the subpattern action representation sourcecode for the given subpattern-matching-action
	 */
	private void genSubpattern(StringBuffer sb, Rule subpatternRule) {
		String actionName = formatIdentifiable(subpatternRule);
		String className = "Pattern_"+actionName;
		List<String> staticInitializers = new LinkedList<String>();

		sb.append("\tpublic class " + className + " : GRGEN_LGSP.LGSPMatchingPattern\n");
		sb.append("\t{\n");
		sb.append("\t\tprivate static " + className + " instance = null;\n");
		sb.append("\t\tpublic static " + className + " Instance { get { if (instance==null) { "
				+ "instance = new " + className + "(); instance.initialize(); } return instance; } }\n");
		sb.append("\n");

		String patGraphVarName = "pat_" + subpatternRule.getPattern().getNameOfGraph();
		genRuleOrSubpatternClassEntities(sb, subpatternRule, patGraphVarName, staticInitializers,
				subpatternRule.getPattern().getNameOfGraph()+"_", new HashMap<Entity, String>());
		sb.append("\n");
		genRuleOrSubpatternInit(sb, subpatternRule, className, true);
		sb.append("\n");

		mg.genModify(sb, subpatternRule, true);

		genImperativeStatements(sb, subpatternRule, formatIdentifiable(subpatternRule) + "_", true, true);
		genImperativeStatementClosures(sb, subpatternRule, formatIdentifiable(subpatternRule) + "_", false);

		genStaticConstructor(sb, className, staticInitializers);

		genMatch(sb, subpatternRule.getPattern(), className);

		sb.append("\t}\n");
		sb.append("\n");
	}

	/**
	 * Generates the action representation sourcecode for the given matching-action
	 */
	private void genAction(StringBuffer sb, Rule actionRule) {
		String actionName = formatIdentifiable(actionRule);
		String className = "Rule_"+actionName;
		List<String> staticInitializers = new LinkedList<String>();

		sb.append("\tpublic class " + className + " : GRGEN_LGSP.LGSPRulePattern\n");
		sb.append("\t{\n");
		sb.append("\t\tprivate static " + className + " instance = null;\n");
		sb.append("\t\tpublic static " + className + " Instance { get { if (instance==null) { "
				+ "instance = new " + className + "(); instance.initialize(); } return instance; } }\n");
		sb.append("\n");

		String patGraphVarName = "pat_" + actionRule.getPattern().getNameOfGraph();
		genRuleOrSubpatternClassEntities(sb, actionRule, patGraphVarName, staticInitializers,
				actionRule.getPattern().getNameOfGraph()+"_", new HashMap<Entity, String>());
		sb.append("\n");
		genRuleOrSubpatternInit(sb, actionRule, className, false);
		sb.append("\n");

		mg.genModify(sb, actionRule, false);

		genImperativeStatements(sb, actionRule, formatIdentifiable(actionRule) + "_", true, false);
		genImperativeStatementClosures(sb, actionRule, formatIdentifiable(actionRule) + "_", true);

		genStaticConstructor(sb, className, staticInitializers);

		genMatch(sb, actionRule.getPattern(), className);

		sb.append("\t}\n");
		sb.append("\n");
	}

	/**
	 * Generates the sequence representation sourcecode for the given sequence
	 */
	private void genSequence(StringBuffer sb, Sequence sequence) {
		String sequenceName = formatIdentifiable(sequence);
		String className = "SequenceInfo_"+sequenceName;
		boolean isExternalSequence = sequence.getExec().getXGRSString().length()==0;
		String baseClass = isExternalSequence ? "GRGEN_LIBGR.ExternalDefinedSequenceInfo" : "GRGEN_LIBGR.DefinedSequenceInfo";

		sb.append("\tpublic class " + className + " : " + baseClass + "\n");
		sb.append("\t{\n");
		sb.append("\t\tprivate static " + className + " instance = null;\n");
		sb.append("\t\tpublic static " + className + " Instance { get { if (instance==null) { "
				+ "instance = new " + className + "(); } return instance; } }\n");
		sb.append("\n");

		sb.append("\t\tprivate " + className + "()\n");
		sb.append("\t\t\t\t\t: base(\n");
		sb.append("\t\t\t\t\t\tnew String[] { ");
		for(ExecVariable inParam : sequence.getInParameters()) {
			sb.append("\"" + inParam.getIdent() + "\", ");
		}
		sb.append(" },\n");
		sb.append("\t\t\t\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
		for(ExecVariable inParam : sequence.getInParameters()) {
			if(inParam.getType() instanceof InheritanceType) {
				sb.append(formatTypeClassRef(inParam.getType()) + ".typeVar, ");
			} else {
				sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(inParam.getType()) + ")), ");
			}
		}
		sb.append(" },\n");
		sb.append("\t\t\t\t\t\tnew String[] { ");
		for(ExecVariable inParam : sequence.getOutParameters()) {
			sb.append("\"" + inParam.getIdent() + "\", ");
		}
		sb.append(" },\n");
		sb.append("\t\t\t\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
		for(ExecVariable outParam : sequence.getOutParameters()) {
			if(outParam.getType() instanceof InheritanceType) {
				sb.append(formatTypeClassRef(outParam.getType()) + ".typeVar, ");
			} else {
				sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(outParam.getType()) + ")), ");
			}
		}
		sb.append(" },\n");
		sb.append("\t\t\t\t\t\t\"" + sequenceName + "\",\n");
		if(!isExternalSequence)
			sb.append("\t\t\t\t\t\t\"" + sequence.getExec().getXGRSString().replace("\\", "\\\\").replace("\"", "\\\"") + "\",\n");
		sb.append("\t\t\t\t\t\t" + sequence.getExec().getLineNr() + "\n");
		sb.append("\t\t\t\t\t  )\n");
		sb.append("\t\t{\n");
		sb.append("\t\t}\n");

		sb.append("\t}\n");
		sb.append("\n");
	}
	
	private void genComputations(StringBuffer sb) {
		sb.append("\tpublic class Computations\n");
		sb.append("\t{\n");
		
		for(Computation computation : be.unit.getComputations()) {
			genComputation(sb, computation);
		}

		List<String> staticInitializers = new LinkedList<String>();
		String pathPrefixForElements = "";
		HashMap<Entity, String> alreadyDefinedEntityToName = new HashMap<Entity, String>();

		for(Computation computation : be.unit.getComputations()) {
			genLocalContainersEvals(sb, computation.getComputationStatements(), staticInitializers,
					pathPrefixForElements, alreadyDefinedEntityToName);
		}
		
		sb.append("#if INITIAL_WARMUP\t\t// GrGen computation exec section:\n");
		for(Computation computation : be.unit.getComputations()) {
			genImperativeStatements(sb, computation);
		}
		sb.append("#endif\n");

		sb.append("\t}\n");
		sb.append("\n");
		
		for(Computation computation : be.unit.getComputations()) {
			genComputationInfo(sb, computation);
		}
	}

	private void genComputation(StringBuffer sb, Computation computation) {
		sb.append("\t\tpublic static " + formatType(computation.getReturnType()) + " ");
		sb.append(computation.getIdent().toString() + "(GRGEN_LGSP.LGSPActionExecutionEnvironment actionEnv, GRGEN_LGSP.LGSPGraph graph");
		for(Entity inParam : computation.getParameters()) {
			sb.append(", ");
			sb.append(formatType(inParam.getType()));
			sb.append(" ");
			sb.append(formatEntity(inParam));
		}
		sb.append(")\n");
		sb.append("\t\t{\n");
		ModifyGen.ModifyGenerationState modifyGenState = modifyGenForComputations.new ModifyGenerationState();
		for(EvalStatement evalStmt : computation.getComputationStatements()) {
			modifyGenState.computationName = computation.getIdent().toString();
			modifyGenForComputations.genEvalStmt(sb, modifyGenState, evalStmt);
		}
		sb.append("\t\t}\n");
	}

	/**
	 * Generates the computation info for the given computation
	 */
	private void genComputationInfo(StringBuffer sb, Computation computation) {
		String computationName = formatIdentifiable(computation);
		String className = "ComputationInfo_"+computationName;

		sb.append("\tpublic class " + className + " : GRGEN_LIBGR.ComputationInfo\n");
		sb.append("\t{\n");
		sb.append("\t\tprivate static " + className + " instance = null;\n");
		sb.append("\t\tpublic static " + className + " Instance { get { if (instance==null) { "
				+ "instance = new " + className + "(); } return instance; } }\n");
		sb.append("\n");

		sb.append("\t\tprivate " + className + "()\n");
		sb.append("\t\t\t\t\t: base(\n");
		sb.append("\t\t\t\t\t\t\"" + computationName + "\",\n");
		sb.append("\t\t\t\t\t\tnew String[] { ");
		for(Entity inParam : computation.getParameters()) {
			sb.append("\"" + inParam.getIdent() + "\", ");
		}
		sb.append(" },\n");
		sb.append("\t\t\t\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
		for(Entity inParam : computation.getParameters()) {
			if(inParam.getType() instanceof InheritanceType) {
				sb.append(formatTypeClassRef(inParam.getType()) + ".typeVar, ");
			} else {
				sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(inParam.getType()) + ")), ");
			}
		}
		sb.append(" },\n");
		Type outParamType = computation.getReturnType();
		if(outParamType instanceof InheritanceType) {
			sb.append("\t\t\t\t\t\t" + formatTypeClassRef(outParamType) + ".typeVar\n");
		} else {
			sb.append("\t\t\t\t\t\tGRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(outParamType) + "))\n");
		}
		sb.append("\t\t\t\t\t  )\n");
		sb.append("\t\t{\n");
		sb.append("\t\t}\n");
		
		sb.append("\t\tpublic override object Apply(GRGEN_LIBGR.IActionExecutionEnvironment actionEnv, GRGEN_LIBGR.IGraph graph, GRGEN_LIBGR.ComputationInvocationParameterBindings paramBindings)\n");
		sb.append("\t\t{\n");
		sb.append("\t\t\treturn Computations." + computationName + "((GRGEN_LGSP.LGSPActionExecutionEnvironment)actionEnv, (GRGEN_LGSP.LGSPGraph)graph");
		int i = 0;
		for(Entity inParam : computation.getParameters()) {			
			sb.append(", (" + formatType(inParam.getType()) + ")paramBindings.Arguments[" + i + "]");
			++i;
		}
		sb.append(");\n");
		sb.append("\t\t}\n");

		sb.append("\t}\n");
		sb.append("\n");
	}

	/**
	 * Generates the match classes (of pattern and contained patterns)
	 */
	private void genMatch(StringBuffer sb, PatternGraph pattern, String className) {
		// generate getters to contained nodes, edges, variables, embedded graphs, alternatives
		genPatternMatchInterface(sb, pattern, pattern.getNameOfGraph(),
				"GRGEN_LIBGR.IMatch", pattern.getNameOfGraph()+"_",
				false, false);

		// generate contained nodes, edges, variables, embedded graphs, alternatives
		// and the implementation of the various getters from IMatch and the pattern specific match interface
		String patGraphVarName = "pat_" + pattern.getNameOfGraph();
		genPatternMatchImplementation(sb, pattern, pattern.getNameOfGraph(),
				patGraphVarName, className, pattern.getNameOfGraph()+"_", false, false);
	}

	//////////////////////////////////////////////////
	// rule or subpattern class entities generation //
	//////////////////////////////////////////////////

	private void genRuleOrSubpatternClassEntities(StringBuffer sb, Rule rule,
							String patGraphVarName, List<String> staticInitializers,
							String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		PatternGraph pattern = rule.getPattern();
		genAllowedTypeArrays(sb, pattern, pathPrefixForElements, alreadyDefinedEntityToName);
		genEnums(sb, pattern, pathPrefixForElements);
		genLocalContainers(sb, rule, staticInitializers, pathPrefixForElements,
				alreadyDefinedEntityToName);
		sb.append("\t\tpublic GRGEN_LGSP.PatternGraph " + patGraphVarName + ";\n");
		sb.append("\n");

		for(PatternGraph neg : pattern.getNegs()) {
			String negName = neg.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			genRuleOrSubpatternClassEntities(sb, neg, pathPrefixForElements+negName, staticInitializers,
					pathPrefixForElements + negName + "_",
					alreadyDefinedEntityToNameClone);
		}

		for(PatternGraph idpt : pattern.getIdpts()) {
			String idptName = idpt.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			genRuleOrSubpatternClassEntities(sb, idpt, pathPrefixForElements+idptName, staticInitializers,
					pathPrefixForElements + idptName + "_",
					alreadyDefinedEntityToNameClone);
		}

		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			genCaseEnum(sb, alt, pathPrefixForElements+altName+"_");
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				String altPatGraphVarName = pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph();
				HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
				genRuleOrSubpatternClassEntities(sb, altCase, altPatGraphVarName, staticInitializers,
						pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph() + "_",
						alreadyDefinedEntityToNameClone);
			}
		}

		for(Rule iter : pattern.getIters()) {
			String iterName = iter.getLeft().getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			genRuleOrSubpatternClassEntities(sb, iter, pathPrefixForElements+iterName, staticInitializers,
					pathPrefixForElements + iterName + "_",
					alreadyDefinedEntityToNameClone);
		}
	}

	private void genRuleOrSubpatternClassEntities(StringBuffer sb, PatternGraph pattern,
							String patGraphVarName, List<String> staticInitializers,
							String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		genAllowedTypeArrays(sb, pattern, pathPrefixForElements, alreadyDefinedEntityToName);
		genEnums(sb, pattern, pathPrefixForElements);
		genLocalContainers(sb, pattern, staticInitializers,
				pathPrefixForElements, alreadyDefinedEntityToName);
		sb.append("\t\tpublic GRGEN_LGSP.PatternGraph " + patGraphVarName + ";\n");
		sb.append("\n");

		for(PatternGraph neg : pattern.getNegs()) {
			String negName = neg.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			genRuleOrSubpatternClassEntities(sb, neg, pathPrefixForElements+negName, staticInitializers,
					pathPrefixForElements + negName + "_",
					alreadyDefinedEntityToNameClone);
		}

		for(PatternGraph idpt : pattern.getIdpts()) {
			String idptName = idpt.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			genRuleOrSubpatternClassEntities(sb, idpt, pathPrefixForElements+idptName, staticInitializers,
					pathPrefixForElements + idptName + "_",
					alreadyDefinedEntityToNameClone);
		}

		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			genCaseEnum(sb, alt, pathPrefixForElements+altName+"_");
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				String altPatGraphVarName = pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph();
				HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
				genRuleOrSubpatternClassEntities(sb, altCase, altPatGraphVarName, staticInitializers,
						pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph() + "_",
						alreadyDefinedEntityToNameClone);
				}
		}

		for(Rule iter : pattern.getIters()) {
			String iterName = iter.getLeft().getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			genRuleOrSubpatternClassEntities(sb, iter, pathPrefixForElements+iterName, staticInitializers,
					pathPrefixForElements + iterName + "_",
					alreadyDefinedEntityToNameClone);
		}
	}

	private void genAllowedTypeArrays(StringBuffer sb, PatternGraph pattern,
									  String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		genAllowedNodeTypeArrays(sb, pattern, pathPrefixForElements, alreadyDefinedEntityToName);
		genAllowedEdgeTypeArrays(sb, pattern, pathPrefixForElements, alreadyDefinedEntityToName);
	}

	private void genAllowedNodeTypeArrays(StringBuffer sb, PatternGraph pattern,
										  String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		StringBuilder aux = new StringBuilder();
		for(Node node : pattern.getNodes()) {
			if(alreadyDefinedEntityToName.get(node)!=null) {
				continue;
			}
			sb.append("\t\tpublic static GRGEN_LIBGR.NodeType[] "
					+ formatEntity(node, pathPrefixForElements) + "_AllowedTypes = ");
			aux.append("\t\tpublic static bool[] " + formatEntity(node, pathPrefixForElements) + "_IsAllowedType = ");
			if( !node.getConstraints().isEmpty() ) {
				// alle verbotenen Typen und deren Untertypen
				HashSet<Type> allForbiddenTypes = new HashSet<Type>();
				for(Type forbiddenType : node.getConstraints())
					for(Type type : model.getNodeTypes()) {
						if (type.isCastableTo(forbiddenType))
							allForbiddenTypes.add(type);
					}
				sb.append("{ ");
				aux.append("{ ");
				for(Type type : model.getNodeTypes()) {
					boolean isAllowed = type.isCastableTo(node.getNodeType()) && !allForbiddenTypes.contains(type);
					// all permitted nodes, aka nodes that are not forbidden
					if( isAllowed )
						sb.append(formatTypeClassRef(type) + ".typeVar, ");
					aux.append(isAllowed);
					aux.append(", ");
				}
				sb.append("}");
				aux.append("}");
			} else {
				sb.append("null");
				aux.append("null");
			}
			sb.append(";\n");
			aux.append(";\n");
			alreadyDefinedEntityToName.put(node, formatEntity(node, pathPrefixForElements));
		}
		sb.append(aux);
	}

	private void genAllowedEdgeTypeArrays(StringBuffer sb, PatternGraph pattern,
										  String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		StringBuilder aux = new StringBuilder();
		for(Edge edge : pattern.getEdges()) {
			if(alreadyDefinedEntityToName.get(edge)!=null) {
				continue;
			}
			sb.append("\t\tpublic static GRGEN_LIBGR.EdgeType[] "
					+ formatEntity(edge, pathPrefixForElements) + "_AllowedTypes = ");
			aux.append("\t\tpublic static bool[] " + formatEntity(edge, pathPrefixForElements) + "_IsAllowedType = ");
			if( !edge.getConstraints().isEmpty() ) {
				// alle verbotenen Typen und deren Untertypen
				HashSet<Type> allForbiddenTypes = new HashSet<Type>();
				for(Type forbiddenType : edge.getConstraints())
					for(Type type : model.getEdgeTypes()) {
						if (type.isCastableTo(forbiddenType))
							allForbiddenTypes.add(type);
					}
				sb.append("{ ");
				aux.append("{ ");
				for(Type type : model.getEdgeTypes()) {
					boolean isAllowed = type.isCastableTo(edge.getEdgeType()) && !allForbiddenTypes.contains(type);
					// all permitted nodes, aka node that are not forbidden
					if( isAllowed )
						sb.append(formatTypeClassRef(type) + ".typeVar, ");
					aux.append(isAllowed);
					aux.append(", ");
				}
				sb.append("}");
				aux.append("}");
			} else {
				sb.append("null");
				aux.append("null");
			}
			sb.append(";\n");
			aux.append(";\n");
			alreadyDefinedEntityToName.put(edge, formatEntity(edge, pathPrefixForElements));
		}
		sb.append(aux);
	}

	private void genEnums(StringBuffer sb, PatternGraph pattern, String pathPrefixForElements) {
		sb.append("\t\tpublic enum " + pathPrefixForElements + "NodeNums { ");
		for(Node node : pattern.getNodes()) {
			sb.append("@" + formatIdentifiable(node) + ", ");
		}
		sb.append("};\n");

		sb.append("\t\tpublic enum " + pathPrefixForElements + "EdgeNums { ");
		for(Edge edge : pattern.getEdges()) {
			sb.append("@" + formatIdentifiable(edge) + ", ");
		}
		sb.append("};\n");

		sb.append("\t\tpublic enum " + pathPrefixForElements + "VariableNums { ");
		for(Variable var : pattern.getVars()) {
			sb.append("@" + formatIdentifiable(var) + ", ");
		}
		sb.append("};\n");

		sb.append("\t\tpublic enum " + pathPrefixForElements + "SubNums { ");
		for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
			sb.append("@" + formatIdentifiable(sub) + ", ");
		}
		sb.append("};\n");

		sb.append("\t\tpublic enum " + pathPrefixForElements + "AltNums { ");
		for(Alternative alt : pattern.getAlts()) {
			sb.append("@" + alt.getNameOfGraph() + ", ");
		}
		sb.append("};\n");

		sb.append("\t\tpublic enum " + pathPrefixForElements + "IterNums { ");
		for(Rule iter : pattern.getIters()) {
			sb.append("@" + iter.getLeft().getNameOfGraph() + ", ");
		}
		sb.append("};\n");
	}

	private void genCaseEnum(StringBuffer sb, Alternative alt, String pathPrefixForElements) {
		sb.append("\t\tpublic enum " + pathPrefixForElements + "CaseNums { ");
		for(Rule altCase : alt.getAlternativeCases()) {
			PatternGraph altCasePattern = altCase.getLeft();
			sb.append("@" + altCasePattern.getNameOfGraph() + ", ");
		}
		sb.append("};\n");
	}

	private void genLocalContainers(StringBuffer sb, Rule rule,
			List<String> staticInitializers, String pathPrefixForElements,
			HashMap<Entity, String> alreadyDefinedEntityToName) {
		genLocalContainers(sb, rule.getLeft(), staticInitializers,
				pathPrefixForElements, alreadyDefinedEntityToName);
		
		for(EvalStatements evals : rule.getEvals()) {
			genLocalContainersEvals(sb, evals.evalStatements, staticInitializers,
					pathPrefixForElements, alreadyDefinedEntityToName);
		}
		genLocalContainersReturns(sb, rule.getReturns(), staticInitializers,
				pathPrefixForElements, alreadyDefinedEntityToName);
		if(rule.getRight()!=null) {
			genLocalContainersInitializations(sb, rule.getRight(), rule.getLeft(), staticInitializers,
					pathPrefixForElements, alreadyDefinedEntityToName);
			genLocalContainersImperativeStatements(sb, rule.getRight().getImperativeStmts(), staticInitializers,
					pathPrefixForElements, alreadyDefinedEntityToName);
		}
	}

	private void genLocalContainers(StringBuffer sb, PatternGraph pattern,
			List<String> staticInitializers, String pathPrefixForElements,
			HashMap<Entity, String> alreadyDefinedEntityToName) {
		genLocalContainersInitializations(sb, pattern, pattern, staticInitializers,
				pathPrefixForElements, alreadyDefinedEntityToName);
		genLocalContainersConditions(sb, pattern, staticInitializers,
				pathPrefixForElements, alreadyDefinedEntityToName);
		for(EvalStatements evals: pattern.getYields()) {
			genLocalContainersEvals(sb, evals.evalStatements, staticInitializers,
					pathPrefixForElements, alreadyDefinedEntityToName);
		}
	}

	private void genLocalContainersInitializations(StringBuffer sb, PatternGraph pattern, PatternGraph directlyNestingLHSPattern, List<String> staticInitializers,
			String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		NeededEntities needs = new NeededEntities(false, false, false, false, false, true, false);
		for(Variable var : pattern.getVars()) {
			if(var.initialization!=null) {
				if(var.directlyNestingLHSGraph==directlyNestingLHSPattern) {
					var.initialization.collectNeededEntities(needs);
				}
			}
		}
		genLocalContainers(sb, needs, staticInitializers);
	}

	private void genLocalContainersConditions(StringBuffer sb, PatternGraph pattern, List<String> staticInitializers,
			String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		NeededEntities needs = new NeededEntities(false, false, false, false, false, true, false);
		for(Expression expr : pattern.getConditions()) {
			expr.collectNeededEntities(needs);
		}
		genLocalContainers(sb, needs, staticInitializers);
	}

	private void genLocalContainersEvals(StringBuffer sb, Collection<EvalStatement> evals, List<String> staticInitializers,
			String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		NeededEntities needs = new NeededEntities(false, false, false, false, false, true, false);
		for(EvalStatement eval : evals) {
			if(eval instanceof AssignmentIndexed) { // must come before Assignment
				AssignmentIndexed assignment = (AssignmentIndexed)eval;
				assignment.getExpression().collectNeededEntities(needs);
				assignment.getIndex().collectNeededEntities(needs);
			}
			else if(eval instanceof Assignment) {
				Assignment assignment = (Assignment)eval;
				assignment.getExpression().collectNeededEntities(needs);
			}
			else if(eval instanceof CompoundAssignment) {
				CompoundAssignment assignment = (CompoundAssignment)eval;
				assignment.getExpression().collectNeededEntities(needs);
			}
			else if(eval instanceof AssignmentVarIndexed) { // must come before AssignmentVar
				AssignmentVarIndexed assignment = (AssignmentVarIndexed)eval;
				assignment.getExpression().collectNeededEntities(needs);
				assignment.getIndex().collectNeededEntities(needs);
			}
			else if(eval instanceof AssignmentVar) {
				AssignmentVar assignment = (AssignmentVar)eval;
				assignment.getExpression().collectNeededEntities(needs);
			}
			else if(eval instanceof AssignmentGraphEntity) {
				AssignmentGraphEntity assignment = (AssignmentGraphEntity)eval;
				assignment.getExpression().collectNeededEntities(needs);
			}
			else if(eval instanceof CompoundAssignmentVar) {
				CompoundAssignmentVar assignment = (CompoundAssignmentVar)eval;
				assignment.getExpression().collectNeededEntities(needs);
			}
			else if(eval instanceof DefDeclVarStatement) {
				DefDeclVarStatement assignment = (DefDeclVarStatement)eval;
				if(assignment.getTarget().initialization != null)
					assignment.getTarget().initialization.collectNeededEntities(needs);
			}
			else if(eval instanceof ReturnStatement) {
				ReturnStatement returnStmt = (ReturnStatement)eval;
				returnStmt.getReturnValueExpr().collectNeededEntities(needs);
			}
		}
		genLocalContainers(sb, needs, staticInitializers);
	}

	// type collision with the method below cause java can't distinguish List<Expression> from List<ImperativeStmt>
	private void genLocalContainersReturns(StringBuffer sb, List<Expression> returns, List<String> staticInitializers,
			String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName) {
		NeededEntities needs = new NeededEntities(false, false, false, false, false, true, false);
		for(Expression expr : returns) {
			expr.collectNeededEntities(needs);
		}
		genLocalContainers(sb, needs, staticInitializers);
	}
	
	private void genLocalContainersImperativeStatements(StringBuffer sb, List<ImperativeStmt> istmts, List<String> staticInitializers,
			String pathPrefixForElements, HashMap<Entity, String> alreadyDefinedEntityToName)
	{
		NeededEntities needs = new NeededEntities(false, false, false, false, false, true, false);
		for(ImperativeStmt istmt : istmts) {
			if(istmt instanceof Emit) {
				Emit emit = (Emit) istmt;
				for(Expression arg : emit.getArguments())
					arg.collectNeededEntities(needs);
			}
			else if (istmt instanceof Exec) {
				Exec exec = (Exec) istmt;
				for(Expression arg : exec.getArguments())
					arg.collectNeededEntities(needs);
			}
			else assert false : "unknown ImperativeStmt: " + istmt;
		}
		genLocalContainers(sb, needs, staticInitializers);
	}

	private void genLocalContainers(StringBuffer sb, NeededEntities needs, List<String> staticInitializers) {
		sb.append("\n");
		for(Expression containerExpr : needs.containerExprs) {
			if(containerExpr instanceof MapInit) {
				genLocalMap(sb, (MapInit)containerExpr, staticInitializers);
			} else if(containerExpr instanceof SetInit) {
				genLocalSet(sb, (SetInit)containerExpr, staticInitializers);
			} else if(containerExpr instanceof ArrayInit) {
				genLocalArray(sb, (ArrayInit)containerExpr, staticInitializers);
			} else if(containerExpr instanceof DequeInit) {
				genLocalDeque(sb, (DequeInit)containerExpr, staticInitializers);
			}
		}
	}

	private void genLocalMap(StringBuffer sb, MapInit mapInit, List<String> staticInitializers) {
		String mapName = mapInit.getAnonymousMapName();
		String attrType = formatAttributeType(mapInit.getType());
		if(mapInit.isConstant()) {
			sb.append("\t\tpublic static readonly " + attrType + " " + mapName + " = " +
					"new " + attrType + "();\n");
			staticInitializers.add("init_" + mapName);
			sb.append("\t\tstatic void init_" + mapName + "() {\n");
			for(MapItem item : mapInit.getMapItems()) {
				sb.append("\t\t\t");
				sb.append(mapName);
				sb.append("[");
				genExpression(sb, item.getKeyExpr(), null);
				sb.append("] = ");
				genExpression(sb, item.getValueExpr(), null);
				sb.append(";\n");
			}
			sb.append("\t\t}\n");
		} else {
			sb.append("\t\tpublic static " + attrType + " fill_" + mapName + "(");
			int itemCounter = 0;
			boolean first = true;
			for(MapItem item : mapInit.getMapItems()) {
				String itemKeyType = formatType(item.getKeyExpr().getType());
				String itemValueType = formatType(item.getValueExpr().getType());
				if(first) {
					sb.append(itemKeyType + " itemkey" + itemCounter + ",");
					sb.append(itemValueType + " itemvalue" + itemCounter);
					first = false;
				} else {
					sb.append(", " + itemKeyType + " itemkey" + itemCounter + ",");
					sb.append(itemValueType + " itemvalue" + itemCounter);
				}
				++itemCounter;
			}
			sb.append(") {\n");
			sb.append("\t\t\t" + attrType + " " + mapName + " = " +
					"new " + attrType + "();\n");

			int itemLength = mapInit.getMapItems().size();
			for(itemCounter = 0; itemCounter < itemLength; ++itemCounter) {
				sb.append("\t\t\t" + mapName);
				sb.append("[" + "itemkey" + itemCounter + "] = itemvalue" + itemCounter + ";\n");
			}
			sb.append("\t\t\treturn " + mapName + ";\n");
			sb.append("\t\t}\n");
		}
	}

	private void genLocalSet(StringBuffer sb, SetInit setInit, List<String> staticInitializers) {
		String setName = setInit.getAnonymousSetName();
		String attrType = formatAttributeType(setInit.getType());
		if(setInit.isConstant()) {
			sb.append("\t\tpublic static readonly " + attrType + " " + setName + " = " +
					"new " + attrType + "();\n");
			staticInitializers.add("init_" + setName);
			sb.append("\t\tstatic void init_" + setName + "() {\n");
			for(SetItem item : setInit.getSetItems()) {
				sb.append("\t\t\t");
				sb.append(setName);
				sb.append("[");
				genExpression(sb, item.getValueExpr(), null);
				sb.append("] = null;\n");
			}
			sb.append("\t\t}\n");
		} else {
			sb.append("\t\tpublic static " + attrType + " fill_" + setName + "(");
			int itemCounter = 0;
			boolean first = true;
			for(SetItem item : setInit.getSetItems()) {
				String itemType = formatType(item.getValueExpr().getType());
				if(first) {
					sb.append(itemType + " item" + itemCounter);
					first = false;
				} else {
					sb.append(", " + itemType + " item" + itemCounter);
				}
				++itemCounter;
			}
			sb.append(") {\n");
			sb.append("\t\t\t" + attrType + " " + setName + " = " +
					"new " + attrType + "();\n");

			int itemLength = setInit.getSetItems().size();
			for(itemCounter = 0; itemCounter < itemLength; ++itemCounter) {
				sb.append("\t\t\t" + setName);
				sb.append("[" + "item" + itemCounter + "] = null;\n");
			}
			sb.append("\t\t\treturn " + setName + ";\n");
			sb.append("\t\t}\n");
		}
	}

	private void genLocalArray(StringBuffer sb, ArrayInit arrayInit, List<String> staticInitializers) {
		String arrayName = arrayInit.getAnonymousArrayName();
		String attrType = formatAttributeType(arrayInit.getType());
		if(arrayInit.isConstant()) {
			sb.append("\t\tpublic static readonly " + attrType + " " + arrayName + " = " +
					"new " + attrType + "();\n");
			staticInitializers.add("init_" + arrayName);
			sb.append("\t\tstatic void init_" + arrayName + "() {\n");
			for(ArrayItem item : arrayInit.getArrayItems()) {
				sb.append("\t\t\t");
				sb.append(arrayName);
				sb.append(".Add(");
				genExpression(sb, item.getValueExpr(), null);
				sb.append(");\n");
			}
			sb.append("\t\t}\n");
		} else {
			sb.append("\t\tpublic static " + attrType + " fill_" + arrayName + "(");
			int itemCounter = 0;
			boolean first = true;
			for(ArrayItem item : arrayInit.getArrayItems()) {
				String itemType = formatType(item.getValueExpr().getType());
				if(first) {
					sb.append(itemType + " item" + itemCounter);
					first = false;
				} else {
					sb.append(", " + itemType + " item" + itemCounter);
				}
				++itemCounter;
			}
			sb.append(") {\n");
			sb.append("\t\t\t" + attrType + " " + arrayName + " = " +
					"new " + attrType + "();\n");

			int itemLength = arrayInit.getArrayItems().size();
			for(itemCounter = 0; itemCounter < itemLength; ++itemCounter) {
				sb.append("\t\t\t" + arrayName);
				sb.append(".Add(" + "item" + itemCounter + ");\n");
			}
			sb.append("\t\t\treturn " + arrayName + ";\n");
			sb.append("\t\t}\n");
		}
	}

	private void genLocalDeque(StringBuffer sb, DequeInit dequeInit, List<String> staticInitializers) {
		String dequeName = dequeInit.getAnonymousDequeName();
		String attrType = formatAttributeType(dequeInit.getType());
		if(dequeInit.isConstant()) {
			sb.append("\t\tpublic static readonly " + attrType + " " + dequeName + " = " +
					"new " + attrType + "();\n");
			staticInitializers.add("init_" + dequeName);
			sb.append("\t\tstatic void init_" + dequeName + "() {\n");
			for(DequeItem item : dequeInit.getDequeItems()) {
				sb.append("\t\t\t");
				sb.append(dequeName);
				sb.append(".Add(");
				genExpression(sb, item.getValueExpr(), null);
				sb.append(");\n");
			}
			sb.append("\t\t}\n");
		} else {
			sb.append("\t\tpublic static " + attrType + " fill_" + dequeName + "(");
			int itemCounter = 0;
			boolean first = true;
			for(DequeItem item : dequeInit.getDequeItems()) {
				String itemType = formatType(item.getValueExpr().getType());
				if(first) {
					sb.append(itemType + " item" + itemCounter);
					first = false;
				} else {
					sb.append(", " + itemType + " item" + itemCounter);
				}
				++itemCounter;
			}
			sb.append(") {\n");
			sb.append("\t\t\t" + attrType + " " + dequeName + " = " +
					"new " + attrType + "();\n");

			int itemLength = dequeInit.getDequeItems().size();
			for(itemCounter = 0; itemCounter < itemLength; ++itemCounter) {
				sb.append("\t\t\t" + dequeName);
				sb.append(".Enqueue(" + "item" + itemCounter + ");\n");
			}
			sb.append("\t\t\treturn " + dequeName + ";\n");
			sb.append("\t\t}\n");
		}
	}

	/////////////////////////////////////////
	// Rule/Subpattern metadata generation //
	/////////////////////////////////////////

	private void genRuleOrSubpatternInit(StringBuffer sb, MatchingAction action,
			String className, boolean isSubpattern) {
		PatternGraph pattern = action.getPattern();

		sb.append("\t\tprivate " + className + "()\n");
		sb.append("\t\t{\n");
		sb.append("\t\t\tname = \"" + formatIdentifiable(action) + "\";\n");
		sb.append("\n");
		genRuleParamResult(sb, action, isSubpattern);
		sb.append("\n");
		addAnnotations(sb, action, "annotations");
		sb.append("\t\t}\n");

		HashMap<Entity, String> alreadyDefinedEntityToName = new HashMap<Entity, String>();
		HashMap<Identifiable, String> alreadyDefinedIdentifiableToName = new HashMap<Identifiable, String>();

		double max = computePriosMax(-1, action.getPattern());

		StringBuilder aux = new StringBuilder();
		String patGraphVarName = "pat_" + pattern.getNameOfGraph();
		sb.append("\t\tprivate void initialize()\n");
		sb.append("\t\t{\n");

		genPatternGraph(sb, aux, pattern, "", pattern.getNameOfGraph(), patGraphVarName, className,
				alreadyDefinedEntityToName, alreadyDefinedIdentifiableToName, action.getParameters(), max);
		sb.append(aux);
		sb.append("\n");
		sb.append("\t\t\tpatternGraph = " + patGraphVarName + ";\n");

		sb.append("\t\t}\n");
	}

	private void genPatternGraph(StringBuffer sb, StringBuilder aux, PatternGraph pattern,
								String pathPrefix, String patternName, // negatives without name, have to compute it and hand it in
								String patGraphVarName, String className,
								HashMap<Entity, String> alreadyDefinedEntityToName,
								HashMap<Identifiable, String> alreadyDefinedIdentifiableToName,
								List<Entity> parameters, double max) {
		genElementsRequiredByPatternGraph(sb, aux, pattern, pathPrefix, patternName, patGraphVarName, className,
										  alreadyDefinedEntityToName, alreadyDefinedIdentifiableToName, parameters, max);

		sb.append("\t\t\t" + patGraphVarName + " = new GRGEN_LGSP.PatternGraph(\n");
		sb.append("\t\t\t\t\"" + patternName + "\",\n");
		sb.append("\t\t\t\t\"" + pathPrefix + "\",\n");
		sb.append("\t\t\t\t" + (pattern.isPatternpathLocked() ? "true" : "false") + ", " );
		sb.append((pattern.isIterationBreaking() ? "true" : "false") + ",\n" );

		String pathPrefixForElements = pathPrefix+patternName+"_";

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternNode[] ");
		genEntitySet(sb, pattern.getNodes(), "", "", true, pathPrefixForElements, alreadyDefinedEntityToName);
		sb.append(", \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternEdge[] ");
		genEntitySet(sb, pattern.getEdges(), "", "", true, pathPrefixForElements, alreadyDefinedEntityToName);
		sb.append(", \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternVariable[] ");
		genEntitySet(sb, pattern.getVars(), "", "", true, pathPrefixForElements, alreadyDefinedEntityToName);
		sb.append(", \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternGraphEmbedding[] ");
		genSubpatternUsageSet(sb, pattern.getSubpatternUsages(), "", "", true, pathPrefixForElements, alreadyDefinedIdentifiableToName);
		sb.append(", \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.Alternative[] { ");
		for(Alternative alt : pattern.getAlts()) {
			sb.append(pathPrefixForElements + alt.getNameOfGraph() + ", ");
		}
		sb.append(" }, \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.Iterated[] { ");
		for(Rule iter : pattern.getIters()) {
			sb.append(pathPrefixForElements + iter.getLeft().getNameOfGraph()+"_it" + ", ");
		}
		sb.append(" }, \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternGraph[] { ");
		for(PatternGraph neg : pattern.getNegs()) {
			sb.append(pathPrefixForElements + neg.getNameOfGraph() + ", ");
		}
		sb.append(" }, \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternGraph[] { ");
		for(PatternGraph idpt : pattern.getIdpts()) {
			sb.append(pathPrefixForElements + idpt.getNameOfGraph() + ", ");
		}
		sb.append(" }, \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternCondition[] { ");
		for(int i = 0; i < pattern.getConditions().size(); i++){
			sb.append(pathPrefixForElements+"cond_" + i + ", ");
		}
		sb.append(" }, \n");

		sb.append("\t\t\t\tnew GRGEN_LGSP.PatternYielding[] { ");
		for(EvalStatements evals : pattern.getYields()) {
			sb.append(pathPrefixForElements + evals.getName() + ", ");
		}
		sb.append(" }, \n");

		sb.append("\t\t\t\tnew bool[" + pattern.getNodes().size() + ", " + pattern.getNodes().size() + "] ");
		if(pattern.getNodes().size() > 0) {
			sb.append("{\n");
			for(Node node1 : pattern.getNodes()) {
				sb.append("\t\t\t\t\t{ ");
				for(Node node2 : pattern.getNodes()) {
					if(pattern.isHomomorphic(node1,node2))
						sb.append("true, ");
					else
						sb.append("false, ");
				}
				sb.append("},\n");
			}
			sb.append("\t\t\t\t}");
		}
		sb.append(",\n");

		sb.append("\t\t\t\tnew bool[" + pattern.getEdges().size() + ", " + pattern.getEdges().size() + "] ");
		if(pattern.getEdges().size() > 0) {
			sb.append("{\n");
			for(Edge edge1 : pattern.getEdges()) {
				sb.append("\t\t\t\t\t{ ");
				for(Edge edge2 : pattern.getEdges()) {
					if(pattern.isHomomorphic(edge1,edge2))
						sb.append("true, ");
					else
						sb.append("false, ");
				}
				sb.append("},\n");
			}
			sb.append("\t\t\t\t}");
		}
		sb.append(",\n");

		sb.append("\t\t\t\t" + pathPrefixForElements + "isNodeHomomorphicGlobal,\n");

		sb.append("\t\t\t\t" + pathPrefixForElements + "isEdgeHomomorphicGlobal,\n");

		sb.append("\t\t\t\t" + pathPrefixForElements + "isNodeTotallyHomomorphic,\n");

		sb.append("\t\t\t\t" + pathPrefixForElements + "isEdgeTotallyHomomorphic\n");
		
		sb.append("\t\t\t);\n");

		// link edges to nodes
		for(Edge edge : pattern.getEdges()) {
			String edgeName = alreadyDefinedEntityToName.get(edge)!=null ?
					alreadyDefinedEntityToName.get(edge) : formatEntity(edge, pathPrefixForElements);

			if(pattern.getSource(edge)!=null) {
				String sourceName = formatEntity(pattern.getSource(edge), pathPrefixForElements, alreadyDefinedEntityToName);
				sb.append("\t\t\t" + patGraphVarName + ".edgeToSourceNode.Add("+edgeName+", "+sourceName+");\n");
			}

			if(pattern.getTarget(edge)!=null) {
				String targetName = formatEntity(pattern.getTarget(edge), pathPrefixForElements, alreadyDefinedEntityToName);
				sb.append("\t\t\t" + patGraphVarName + ".edgeToTargetNode.Add("+edgeName+", "+targetName+");\n");
			}
		}

		// set embedding-member of contained graphs
		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				String altPatGraphVarName = pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph();
				sb.append("\t\t\t" + altPatGraphVarName + ".embeddingGraph = " + patGraphVarName + ";\n");
			}
		}

		for(Rule iter : pattern.getIters()) {
			String iterName = iter.getLeft().getNameOfGraph();
			sb.append("\t\t\t" + pathPrefixForElements+iterName + ".embeddingGraph = " + patGraphVarName + ";\n");
		}

		for(PatternGraph neg : pattern.getNegs()) {
			String negName = neg.getNameOfGraph();
			sb.append("\t\t\t" + pathPrefixForElements+negName + ".embeddingGraph = " + patGraphVarName + ";\n");
		}

		for(PatternGraph idpt : pattern.getIdpts()) {
			String idptName = idpt.getNameOfGraph();
			sb.append("\t\t\t" + pathPrefixForElements+idptName + ".embeddingGraph = " + patGraphVarName + ";\n");
		}

		sb.append("\n");
	}

	private void genElementsRequiredByPatternGraph(StringBuffer sb, StringBuilder aux, PatternGraph pattern,
												   String pathPrefix, String patternName,
												   String patGraphVarName, String className,
												   HashMap<Entity, String> alreadyDefinedEntityToName,
												   HashMap<Identifiable, String> alreadyDefinedIdentifiableToName,
												   List<Entity> parameters, double max) {
		String pathPrefixForElements = pathPrefix+patternName+"_";

		sb.append("\t\t\tbool[,] " + pathPrefixForElements + "isNodeHomomorphicGlobal = "
				+ "new bool[" + pattern.getNodes().size() + ", " + pattern.getNodes().size() + "] ");
		if(pattern.getNodes().size() > 0) {
			sb.append("{\n");
			for(Node node1 : pattern.getNodes()) {
				sb.append("\t\t\t\t{ ");
				for(Node node2 : pattern.getNodes()) {
					if(pattern.isHomomorphicGlobal(alreadyDefinedEntityToName, node1, node2))
						sb.append("true, ");
					else
						sb.append("false, ");
				}
				sb.append("},\n");
			}
			sb.append("\t\t\t}");
		}
		sb.append(";\n");

		sb.append("\t\t\tbool[,] " + pathPrefixForElements + "isEdgeHomomorphicGlobal = "
				+ "new bool[" + pattern.getEdges().size() + ", " + pattern.getEdges().size() + "] ");
		if(pattern.getEdges().size() > 0) {
			sb.append("{\n");
			for(Edge edge1 : pattern.getEdges()) {
				sb.append("\t\t\t\t{ ");
				for(Edge edge2 : pattern.getEdges()) {
					if(pattern.isHomomorphicGlobal(alreadyDefinedEntityToName, edge1, edge2))
						sb.append("true, ");
					else
						sb.append("false, ");
				}
				sb.append("},\n");
			}
			sb.append("\t\t\t}");
		}
		sb.append(";\n");

		sb.append("\t\t\tbool[] " + pathPrefixForElements + "isNodeTotallyHomomorphic = "
				+ "new bool[" + pattern.getNodes().size() + "] ");
		if(pattern.getNodes().size() > 0) {
			sb.append("{ ");
			for(Node node : pattern.getNodes()) {
				if(pattern.isTotallyHomomorphic(node))
					sb.append("true, ");
				else
					sb.append("false, ");
			}
			sb.append(" }");
		}
		sb.append(";\n");

		sb.append("\t\t\tbool[] " + pathPrefixForElements + "isEdgeTotallyHomomorphic = "
				+ "new bool[" + pattern.getEdges().size() + "] ");
		if(pattern.getEdges().size() > 0) {
			sb.append("{ ");
			for(Edge edge : pattern.getEdges()) {
				if(pattern.isTotallyHomomorphic(edge))
					sb.append("true, ");
				else
					sb.append("false, ");
			}
			sb.append(" }");
		}
		sb.append(";\n");

		for(Variable var : pattern.getVars()) {
			if(alreadyDefinedEntityToName.get(var)!=null) {
				continue;
			}

			String varName = formatEntity(var, pathPrefixForElements);
			sb.append("\t\t\tGRGEN_LGSP.PatternVariable " + varName
					+ " = new GRGEN_LGSP.PatternVariable(");
			sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(var)
					+ ")), \"" + varName + "\", \"" + formatIdentifiable(var) + "\", ");
			sb.append(parameters.indexOf(var)+", ");
			sb.append(var.isDefToBeYieldedTo() ? "true, " : "false, ");
			if(var.initialization!=null) {
				genExpressionTree(sb, var.initialization, className, pathPrefixForElements, alreadyDefinedEntityToName);
				sb.append(");\n");
			} else {
				sb.append("null);\n");				
			}
			alreadyDefinedEntityToName.put(var, varName);
			aux.append("\t\t\t" + varName + ".pointOfDefinition = " + (parameters.indexOf(var)==-1 ? patGraphVarName : "null") + ";\n");
			addAnnotations(aux, var, varName+".annotations");
		}

		// Dependencies because an element requires another element (e.g. match by storage access)
		int dependencyLevel = 0;
		boolean somethingSkipped;
		do {
			somethingSkipped = false;

			for(Node node : pattern.getNodes()) {
				if(alreadyDefinedEntityToName.get(node)!=null) {
					continue;
				}
				if(node.getDependencyLevel()>dependencyLevel) {
					somethingSkipped = true;
					continue;
				}
				
				String nodeName = formatEntity(node, pathPrefixForElements);
				sb.append("\t\t\tGRGEN_LGSP.PatternNode " + nodeName + " = new GRGEN_LGSP.PatternNode(");
				sb.append("(int) GRGEN_MODEL.NodeTypes.@" + formatIdentifiable(node.getType()) 
						+ ", " + formatTypeClassRef(node.getType()) + ".typeVar"
						+ ", \"" + formatElementInterfaceRef(node.getType()) + "\", ");
				sb.append("\"" + nodeName + "\", \"" + formatIdentifiable(node) + "\", ");
				sb.append(nodeName + "_AllowedTypes, ");
				sb.append(nodeName + "_IsAllowedType, ");
				appendPrio(sb, node, max);
				sb.append(parameters.indexOf(node)+", ");
				sb.append(node.getMaybeNull() ? "true, " : "false, ");
				genStorageAccess(sb, pathPrefix, alreadyDefinedEntityToName,
						pathPrefixForElements, node);
				sb.append((node instanceof RetypedNode ? formatEntity(((RetypedNode)node).getOldNode(), pathPrefixForElements, alreadyDefinedEntityToName) : "null")+", ");
				sb.append(node.isDefToBeYieldedTo() ? "true," : "false,");
				if(node.initialization!=null) {
					genExpressionTree(sb, node.initialization, className, pathPrefixForElements, alreadyDefinedEntityToName);
					sb.append(");\n");
				} else {
					sb.append("null);\n");				
				}
				alreadyDefinedEntityToName.put(node, nodeName);
				aux.append("\t\t\t" + nodeName + ".pointOfDefinition = " + (parameters.indexOf(node)==-1 ? patGraphVarName : "null") + ";\n");
				addAnnotations(aux, node, nodeName+".annotations");
	
				node.setPointOfDefinition(pattern);
			}
	
			for(Edge edge : pattern.getEdges()) {
				if(alreadyDefinedEntityToName.get(edge)!=null) {
					continue;
				}
				if(edge.getDependencyLevel()>dependencyLevel) {
					somethingSkipped = true;
					continue;
				}

				String edgeName = formatEntity(edge, pathPrefixForElements);
				sb.append("\t\t\tGRGEN_LGSP.PatternEdge " + edgeName + " = new GRGEN_LGSP.PatternEdge(");
				sb.append((edge.hasFixedDirection() ? "true" : "false") + ", ");
				sb.append("(int) GRGEN_MODEL.EdgeTypes.@" + formatIdentifiable(edge.getType()) 
						+ ", " + formatTypeClassRef(edge.getType()) + ".typeVar"
						+ ", \"" + formatElementInterfaceRef(edge.getType()) + "\", ");
				sb.append("\"" + edgeName + "\", \"" + formatIdentifiable(edge) + "\", ");
				sb.append(edgeName + "_AllowedTypes, ");
				sb.append(edgeName + "_IsAllowedType, ");
				appendPrio(sb, edge, max);
				sb.append(parameters.indexOf(edge)+", ");
				sb.append(edge.getMaybeNull()?"true, ":"false, ");
				genStorageAccess(sb, pathPrefix, alreadyDefinedEntityToName,
						pathPrefixForElements, edge);
				sb.append((edge instanceof RetypedEdge ? formatEntity(((RetypedEdge)edge).getOldEdge(), pathPrefixForElements, alreadyDefinedEntityToName) : "null")+", ");
				sb.append(edge.isDefToBeYieldedTo() ? "true," : "false,");
				if(edge.initialization!=null) {
					genExpressionTree(sb, edge.initialization, className, pathPrefixForElements, alreadyDefinedEntityToName);
					sb.append(");\n");
				} else {
					sb.append("null);\n");				
				}
				alreadyDefinedEntityToName.put(edge, edgeName);
				aux.append("\t\t\t" + edgeName + ".pointOfDefinition = " + (parameters.indexOf(edge)==-1 ? patGraphVarName : "null") + ";\n");
				addAnnotations(aux, edge, edgeName+".annotations");
	
				edge.setPointOfDefinition(pattern);
			}
			
			++dependencyLevel;
		} while(somethingSkipped);
		
		for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
			if(alreadyDefinedIdentifiableToName.get(sub)!=null) {
				continue;
			}
			String subName = formatIdentifiable(sub, pathPrefixForElements);
			sb.append("\t\t\tGRGEN_LGSP.PatternGraphEmbedding " + subName
					+ " = new GRGEN_LGSP.PatternGraphEmbedding(");
			sb.append("\"" + formatIdentifiable(sub) + "\", ");
			sb.append("Pattern_"+ sub.getSubpatternAction().getIdent().toString() + ".Instance, \n");
			sb.append("\t\t\t\tnew GRGEN_EXPR.Expression[] {\n");
			NeededEntities needs = new NeededEntities(true, true, true, false, false, true, false);
			for(Expression expr : sub.getSubpatternConnections()) {
				expr.collectNeededEntities(needs);
				sb.append("\t\t\t\t\t");
				genExpressionTree(sb, expr, className, pathPrefixForElements, alreadyDefinedEntityToName);
				sb.append(",\n");
			}
			sb.append("\t\t\t\t}, \n");
			sb.append("\t\t\t\tnew string[] { ");
			for(Expression expr : sub.getSubpatternYields()) {
				sb.append("\"");
				if(expr instanceof VariableExpression) {
					VariableExpression ve = (VariableExpression)expr;
					sb.append(formatEntity(ve.getVariable(), pathPrefixForElements, alreadyDefinedEntityToName));
				} else {
					GraphEntityExpression ge = (GraphEntityExpression)expr;
					sb.append(formatEntity(ge.getGraphEntity(), pathPrefixForElements, alreadyDefinedEntityToName));
				}
				sb.append("\", ");
			}
			sb.append("}, new string[] ");
			genEntitySet(sb, needs.nodes, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new string[] ");
			genEntitySet(sb, needs.edges, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new string[] ");
			genEntitySet(sb, needs.variables, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new GRGEN_LIBGR.VarType[] ");
			genVarTypeSet(sb, needs.variables, true);
			sb.append(");\n");
			alreadyDefinedIdentifiableToName.put(sub, subName);
			aux.append("\t\t\t" + subName + ".PointOfDefinition = " + patGraphVarName + ";\n");
			addAnnotations(aux, sub, subName+".annotations");
		}

		int i = 0;
		for(Expression expr : pattern.getConditions()) {
			NeededEntities needs = new NeededEntities(true, true, true, false, false, true, false);
			expr.collectNeededEntities(needs);
			sb.append("\t\t\tGRGEN_LGSP.PatternCondition " + pathPrefixForElements+"cond_"+i
					+ " = new GRGEN_LGSP.PatternCondition(\n"
					+ "\t\t\t\t");
			genExpressionTree(sb, expr, className, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(",\n");
			sb.append("\t\t\t\tnew string[] ");
			genEntitySet(sb, needs.nodes, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new string[] ");
			genEntitySet(sb, needs.edges, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new string[] ");
			genEntitySet(sb, needs.variables, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new GRGEN_LIBGR.VarType[] ");
			genVarTypeSet(sb, needs.variables, true);
			sb.append(");\n");
			++i;
		}

		for(EvalStatements yields : pattern.getYields()) {
			sb.append("\t\t\tGRGEN_LGSP.PatternYielding " + pathPrefixForElements+yields.getName()
					+ " = new GRGEN_LGSP.PatternYielding(");
			sb.append("\"" + yields.getName() + "\",\n ");
			sb.append("\t\t\tnew GRGEN_EXPR.Yielding[] {\n ");

			for(EvalStatement yield : yields.evalStatements) {
				genYield(sb, yield, className, pathPrefixForElements, alreadyDefinedEntityToName);
				sb.append(",\n");
			}
			
			sb.append("\t\t\t}, \n");

			NeededEntities needs = new NeededEntities(true, true, true, false, false, true, false);
			yields.collectNeededEntities(needs);
			sb.append("\t\t\t\tnew string[] ");
			genEntitySet(sb, needs.nodes, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new string[] ");
			genEntitySet(sb, needs.edges, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new string[] ");
			genEntitySet(sb, needs.variables, "\"", "\"", true, pathPrefixForElements, alreadyDefinedEntityToName);
			sb.append(", new GRGEN_LIBGR.VarType[] ");
			genVarTypeSet(sb, needs.variables, true);
			sb.append(");\n");
		}

		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				String altPatGraphVarName = pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph();
				HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
				HashMap<Identifiable, String> alreadyDefinedIdentifiableToNameClone = new HashMap<Identifiable, String>(alreadyDefinedIdentifiableToName);
				genPatternGraph(sb, aux, altCasePattern,
								  pathPrefixForElements+altName+"_", altCasePattern.getNameOfGraph(),
								  altPatGraphVarName, className,
								  alreadyDefinedEntityToNameClone,
								  alreadyDefinedIdentifiableToNameClone,
								  parameters, max);
			}
		}

		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			sb.append("\t\t\tGRGEN_LGSP.Alternative " + pathPrefixForElements+altName + " = new GRGEN_LGSP.Alternative( ");
			sb.append("\"" + altName + "\", ");
			sb.append("\"" + pathPrefixForElements + "\", ");
			sb.append("new GRGEN_LGSP.PatternGraph[] ");
			genAlternativesSet(sb, alt.getAlternativeCases(), pathPrefixForElements+altName+"_", "", true);
			sb.append(" );\n\n");
		}

		for(Rule iter : pattern.getIters()) {
			PatternGraph iterPattern = iter.getLeft();
			String iterName = iterPattern.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			HashMap<Identifiable, String> alreadyDefinedIdentifiableToNameClone = new HashMap<Identifiable, String>(alreadyDefinedIdentifiableToName);
			genPatternGraph(sb, aux, iterPattern,
							  pathPrefixForElements, iterName,
							  pathPrefixForElements+iterName, className,
							  alreadyDefinedEntityToNameClone,
							  alreadyDefinedIdentifiableToNameClone,
							  parameters, max);
		}

		for(Rule iter : pattern.getIters()) {
			PatternGraph iterPattern = iter.getLeft();
			String iterName = iterPattern.getNameOfGraph();
			sb.append("\t\t\tGRGEN_LGSP.Iterated " + pathPrefixForElements+iterName+"_it" + " = new GRGEN_LGSP.Iterated( ");
			sb.append(pathPrefixForElements + iterName + ", ");
			sb.append(iter.getMinMatches() + ", ");
			sb.append(iter.getMaxMatches() + ");\n");
		}
		
		for(PatternGraph neg : pattern.getNegs()) {
			String negName = neg.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			HashMap<Identifiable, String> alreadyDefinedIdentifiableToNameClone = new HashMap<Identifiable, String>(alreadyDefinedIdentifiableToName);
			genPatternGraph(sb, aux, neg,
							  pathPrefixForElements, negName,
							  pathPrefixForElements+negName, className,
							  alreadyDefinedEntityToNameClone,
							  alreadyDefinedIdentifiableToNameClone,
							  parameters, max);
		}

		for(PatternGraph idpt : pattern.getIdpts()) {
			String idptName = idpt.getNameOfGraph();
			HashMap<Entity, String> alreadyDefinedEntityToNameClone = new HashMap<Entity, String>(alreadyDefinedEntityToName);
			HashMap<Identifiable, String> alreadyDefinedIdentifiableToNameClone = new HashMap<Identifiable, String>(alreadyDefinedIdentifiableToName);
			genPatternGraph(sb, aux, idpt,
							  pathPrefixForElements, idptName,
							  pathPrefixForElements+idptName, className,
							  alreadyDefinedEntityToNameClone,
							  alreadyDefinedIdentifiableToNameClone,
							  parameters, max);
		}
	}

	private void genStorageAccess(StringBuffer sb, String pathPrefix,
			HashMap<Entity, String> alreadyDefinedEntityToName,
			String pathPrefixForElements, GraphEntity entity) {
		if(entity.storageAccess!=null) {
			if(entity.storageAccess.storageVariable!=null) {
				Variable storageVariable = entity.storageAccess.storageVariable;
				sb.append("new GRGEN_LGSP.StorageAccess(" + formatEntity(storageVariable, pathPrefixForElements, alreadyDefinedEntityToName) + "), ");
			} else if(entity.storageAccess.storageAttribute!=null) {
				Qualification storageAttribute = entity.storageAccess.storageAttribute;
				GraphEntity owner = (GraphEntity)storageAttribute.getOwner();
				Entity member = storageAttribute.getMember();
				sb.append("new GRGEN_LGSP.StorageAccess(new GRGEN_LGSP.QualificationAccess(" + formatEntity(owner, pathPrefix, alreadyDefinedEntityToName) + ", ");
				sb.append(formatTypeClassRef(owner.getParameterInterfaceType()!=null ? owner.getParameterInterfaceType() : owner.getType()) + ".typeVar" + ".GetAttributeType(\"" + formatIdentifiable(member) + "\")");
				sb.append(")), ");
			}
		} else {
			sb.append("null, ");
		}
		if(entity.storageAccessIndex!=null) {
			if(entity.storageAccessIndex.indexGraphEntity!=null) {
				GraphEntity indexGraphEntity = entity.storageAccessIndex.indexGraphEntity;
				sb.append("new GRGEN_LGSP.StorageAccessIndex(" + formatEntity(indexGraphEntity, pathPrefixForElements, alreadyDefinedEntityToName) + "), ");
			}
		} else {
			sb.append("null, ");
		}
	}

	private void genRuleParamResult(StringBuffer sb, MatchingAction action, boolean isSubpattern) {
		sb.append("\t\t\tinputs = new GRGEN_LIBGR.GrGenType[] { ");
		for(Entity ent : action.getParameters()) {
			if(ent instanceof Variable) {
				sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(ent) + ")), ");
			} else {
				GraphEntity gent = (GraphEntity)ent;
				sb.append(formatTypeClassRef(gent.getParameterInterfaceType()!=null ? gent.getParameterInterfaceType() : gent.getType()) + ".typeVar, ");
			}
		}
		sb.append("};\n");

		sb.append("\t\t\tinputNames = new string[] { ");
		for(Entity ent : action.getParameters())
			sb.append("\"" + formatEntity(ent, action.getPattern().getNameOfGraph()+"_") + "\", ");
		sb.append("};\n");

		sb.append("\t\t\tdefs = new GRGEN_LIBGR.GrGenType[] { ");
		for(Entity ent : action.getDefParameters()) {
			if(ent instanceof Variable) {
				sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(ent) + ")), ");
			} else {
				GraphEntity gent = (GraphEntity)ent;
				sb.append(formatTypeClassRef(gent.getParameterInterfaceType()!=null ? gent.getParameterInterfaceType() : gent.getType()) + ".typeVar, ");
			}
		}
		sb.append("};\n");

		sb.append("\t\t\tdefNames = new string[] { ");
		for(Entity ent : action.getDefParameters())
			sb.append("\"" + formatIdentifiable(ent) + "\", ");
		sb.append("};\n");

		if(!isSubpattern) {
			sb.append("\t\t\toutputs = new GRGEN_LIBGR.GrGenType[] { ");
			for(Expression expr : action.getReturns()) {
				if(expr instanceof GraphEntityExpression)
					sb.append(formatTypeClassRef(expr.getType()) + ".typeVar, ");
				else
					sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(expr.getType()) + ")), ");
			}
			sb.append("};\n");
			
			sb.append("\t\t\tfilters = new String[] { ");
			for(String name : action.getFilters()) {
				sb.append("\"" + name + "\", ");
			}
			sb.append("};\n");
		}
	}

	//////////////////////////////////////////
	// Imperative statement/exec generation //
	//////////////////////////////////////////

	private void genImperativeStatements(StringBuffer sb, Rule rule, String pathPrefix,
			boolean isTopLevel, boolean isSubpattern) {
		if(rule.getRight()==null) {
			return;
		}
		
		if(isTopLevel) {
			sb.append("#if INITIAL_WARMUP\t\t// GrGen imperative statement section: "  + (isSubpattern ? "Pattern_" : "Rule_") + formatIdentifiable(rule) + "\n");
		}
		
		genImperativeStatements(sb, rule, pathPrefix);
				
		PatternGraph pattern = rule.getPattern();
		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				genImperativeStatements(sb, altCase,
						pathPrefix + altName + "_" + altCasePattern.getNameOfGraph() + "_",
						false, isSubpattern);
			}
		}
		
		for(Rule iter : pattern.getIters()) {
			String iterName = iter.getLeft().getNameOfGraph();
			genImperativeStatements(sb, iter,
					pathPrefix + iterName + "_",
					false, isSubpattern);
		}
		
		if(isTopLevel) {
			sb.append("#endif\n");
		}
	}

	private void genImperativeStatements(StringBuffer sb, Rule rule, String pathPrefix) {
		int xgrsID = 0;
		for(ImperativeStmt istmt : rule.getRight().getImperativeStmts()) {
			if (istmt instanceof Exec) {
				Exec exec = (Exec) istmt;
				sb.append("\t\tpublic static GRGEN_LIBGR.EmbeddedSequenceInfo XGRSInfo_" + pathPrefix + xgrsID
						+ " = new GRGEN_LIBGR.EmbeddedSequenceInfo(\n");
				sb.append("\t\t\tnew string[] {");
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(!neededEntity.isDefToBeYieldedTo()) {
						sb.append("\"" + neededEntity.getIdent() + "\", ");
					}
				}
				sb.append("},\n");
				sb.append("\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(!neededEntity.isDefToBeYieldedTo()) {
						if(neededEntity instanceof Variable) {
							sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(neededEntity) + ")), ");
						} else {
							GraphEntity gent = (GraphEntity)neededEntity;
							sb.append(formatTypeClassRef(gent.getType()) + ".typeVar, ");
						}
					}
				}
				sb.append("},\n");
				sb.append("\t\t\tnew string[] {");
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(neededEntity.isDefToBeYieldedTo()) {
						sb.append("\"" + neededEntity.getIdent() + "\", ");
					}
				}
				sb.append("},\n");
				sb.append("\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(neededEntity.isDefToBeYieldedTo()) {
						if(neededEntity instanceof Variable) {
							sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(neededEntity) + ")), ");
						} else {
							GraphEntity gent = (GraphEntity)neededEntity;
							sb.append(formatTypeClassRef(gent.getType()) + ".typeVar, ");
						}
					}
				}
				sb.append("},\n");
				sb.append("\t\t\t\"" + exec.getXGRSString().replace("\\", "\\\\").replace("\"", "\\\"") + "\",\n");
				sb.append("\t\t\t" + exec.getLineNr() + "\n");
				sb.append("\t\t);\n");
				
				sb.append("\t\tprivate static bool ApplyXGRS_" + pathPrefix + xgrsID + "(GRGEN_LGSP.LGSPGraphProcessingEnvironment procEnv");
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(!neededEntity.isDefToBeYieldedTo()) {
						sb.append(", " + formatType(neededEntity.getType()) + " var_" + neededEntity.getIdent());
					}
				}
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(neededEntity.isDefToBeYieldedTo()) {
						sb.append(", ref " + formatType(neededEntity.getType()) + " " + formatEntity(neededEntity));
					}
				}
				sb.append(") {\n");
				for(Entity neededEntity : exec.getNeededEntities(false)) {
					if(neededEntity.isDefToBeYieldedTo()) {
						sb.append("\t\t\t" + formatEntity(neededEntity) + " = ");
						sb.append(getInitializationValue(neededEntity.getType()) + ";\n");
						sb.append(";\n");
					}
				}
				sb.append("\t\t\treturn true;\n");
				sb.append("\t\t}\n");
				
				++xgrsID;
			} else if(istmt instanceof Emit) {
				// nothing to do
			} else {
				assert false : "unknown ImperativeStmt: " + istmt + " in " + rule;
			}
		}
	}

	private void genImperativeStatementClosures(StringBuffer sb, Rule rule, String pathPrefix,
			boolean isTopLevelRule) {
		if(rule.getRight()==null) {
			return;
		}

		if(!isTopLevelRule) {
			genImperativeStatementClosures(sb, rule, pathPrefix);
		}
						
		PatternGraph pattern = rule.getPattern();
		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				genImperativeStatementClosures(sb, altCase,
						pathPrefix + altName + "_" + altCasePattern.getNameOfGraph() + "_",
						false);
			}
		}
		
		for(Rule iter : pattern.getIters()) {
			String iterName = iter.getLeft().getNameOfGraph();
			genImperativeStatementClosures(sb, iter,
					pathPrefix + iterName + "_",
					false);
		}
	}

	private void genImperativeStatementClosures(StringBuffer sb, Rule rule, String pathPrefix) {
		int xgrsID = 0;
		for(ImperativeStmt istmt : rule.getRight().getImperativeStmts()) {
			if (!(istmt instanceof Exec)) {
				continue;
			}
			
			Exec exec = (Exec) istmt;
			sb.append("\n"
					+ "\t\tpublic class XGRSClosure_" + pathPrefix + xgrsID + " : GRGEN_LGSP.LGSPEmbeddedSequenceClosure\n" 
					+ "\t\t{\n");
			sb.append("\t\t\tpublic XGRSClosure_" + pathPrefix + xgrsID + "(");
			boolean first = true;
			for(Entity neededEntity : exec.getNeededEntities(false)) {
				if(first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(formatType(neededEntity.getType()) + " " + formatEntity(neededEntity));
			}
			sb.append(") {\n");
			for(Entity neededEntity : exec.getNeededEntities(false)) {
				sb.append("\t\t\t\tthis." + formatEntity(neededEntity) + " = " + formatEntity(neededEntity) + ";\n");
			}
			sb.append("\t\t\t}\n");
			
			sb.append("\t\t\tpublic override bool exec(GRGEN_LGSP.LGSPGraphProcessingEnvironment procEnv) {\n");
			sb.append("\t\t\t\treturn ApplyXGRS_" + pathPrefix + xgrsID + "(procEnv");
			for(Entity neededEntity : exec.getNeededEntities(false)) {
				sb.append(", " + formatEntity(neededEntity));
			}
			
			sb.append(");\n"); 
			sb.append("\t\t\t}\n");
			
			for(Entity neededEntity : exec.getNeededEntities(false)) {
				sb.append("\t\t\t" + formatType(neededEntity.getType()) + " " + formatEntity(neededEntity) + ";\n");
			}

			//sb.append("\n");
			//sb.append("\t\t\tpublic static int numFreeClosures = 0;\n");
			//sb.append("\t\t\tpublic static LGSPEmbeddedSequenceClosure rootOfFreeClosures = null;\n");

			sb.append("\t\t}\n");
			
			++xgrsID;
		}
	}

	private void genImperativeStatements(StringBuffer sb, Computation computation) {
		int xgrsID = 0;
		for(EvalStatement evalStmt : computation.getComputationStatements()) {
			xgrsID = genImperativeStatements(sb, computation, evalStmt, xgrsID);
		}
	}

	private int genImperativeStatements(StringBuffer sb, Computation computation, EvalStatement evalStmt, int xgrsID) {
		if(evalStmt instanceof ExecStatement) {
			genImperativeStatement(sb, computation, (ExecStatement)evalStmt, xgrsID);
			++xgrsID;
		} else if(evalStmt instanceof ConditionStatement) {
			ConditionStatement condStmt = (ConditionStatement)evalStmt;
			for(EvalStatement childEvalStmt : condStmt.getTrueCaseStatements()) {
				xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
			}
			if(condStmt.getFalseCaseStatements()!=null) {
				for(EvalStatement childEvalStmt : condStmt.getFalseCaseStatements()) {
					xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
				}
			}
		} else if(evalStmt instanceof ContainerAccumulationYield) {
			for(EvalStatement childEvalStmt : ((ContainerAccumulationYield)evalStmt).getAccumulationStatements()) {
				xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
			}
		} else if(evalStmt instanceof DoWhileStatement) {
			for(EvalStatement childEvalStmt : ((DoWhileStatement)evalStmt).getLoopedStatements()) {
				xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
			}
		} else if(evalStmt instanceof ForFunction) {
			for(EvalStatement childEvalStmt : ((ForFunction)evalStmt).getLoopedStatements()) {
				xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
			}
		} else if(evalStmt instanceof ForLookup) {
			for(EvalStatement childEvalStmt : ((ForLookup)evalStmt).getLoopedStatements()) {
				xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
			}
		} else if(evalStmt instanceof WhileStatement) {
			for(EvalStatement childEvalStmt : ((WhileStatement)evalStmt).getLoopedStatements()) {
				xgrsID = genImperativeStatements(sb, computation, childEvalStmt, xgrsID);
			}
		}
		return xgrsID;
	}

	private void genImperativeStatement(StringBuffer sb, Computation computation, ExecStatement execStmt, int xgrsID) {
		Exec exec = execStmt.getExec();
		
		sb.append("\t\tpublic static GRGEN_LIBGR.EmbeddedSequenceInfo XGRSInfo_" + formatIdentifiable(computation) + "_" + xgrsID
				+ " = new GRGEN_LIBGR.EmbeddedSequenceInfo(\n");
		sb.append("\t\t\tnew string[] {");
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(!neededEntity.isDefToBeYieldedTo()) {
				sb.append("\"" + neededEntity.getIdent() + "\", ");
			}
		}
		sb.append("},\n");
		sb.append("\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(!neededEntity.isDefToBeYieldedTo()) {
				if(neededEntity instanceof Variable) {
					sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(neededEntity) + ")), ");
				} else {
					GraphEntity gent = (GraphEntity)neededEntity;
					sb.append(formatTypeClassRef(gent.getType()) + ".typeVar, ");
				}
			}
		}
		sb.append("},\n");
		sb.append("\t\t\tnew string[] {");
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(neededEntity.isDefToBeYieldedTo()) {
				sb.append("\"" + neededEntity.getIdent() + "\", ");
			}
		}
		sb.append("},\n");
		sb.append("\t\t\tnew GRGEN_LIBGR.GrGenType[] { ");
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(neededEntity.isDefToBeYieldedTo()) {
				if(neededEntity instanceof Variable) {
					sb.append("GRGEN_LIBGR.VarType.GetVarType(typeof(" + formatAttributeType(neededEntity) + ")), ");
				} else {
					GraphEntity gent = (GraphEntity)neededEntity;
					sb.append(formatTypeClassRef(gent.getType()) + ".typeVar, ");
				}
			}
		}
		sb.append("},\n");
		sb.append("\t\t\t\"" + exec.getXGRSString().replace("\\", "\\\\").replace("\"", "\\\"") + "\",\n");
		sb.append("\t\t\t" + exec.getLineNr() + "\n");
		sb.append("\t\t);\n");
		
		sb.append("\t\tprivate static bool ApplyXGRS_" + formatIdentifiable(computation) + "_" + xgrsID + "(GRGEN_LGSP.LGSPGraphProcessingEnvironment procEnv");
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(!neededEntity.isDefToBeYieldedTo()) {
				sb.append(", " + formatType(neededEntity.getType()) + " var_" + neededEntity.getIdent());
			}
		}
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(neededEntity.isDefToBeYieldedTo()) {
				sb.append(", ref " + formatType(neededEntity.getType()) + " " + formatEntity(neededEntity));
			}
		}
		sb.append(") {\n");
		for(Entity neededEntity : exec.getNeededEntities(true)) {
			if(neededEntity.isDefToBeYieldedTo()) {
				sb.append("\t\t\t" + formatEntity(neededEntity) + " = ");
				sb.append(getInitializationValue(neededEntity.getType()) + ";\n");
				sb.append(";\n");
			}
		}
		sb.append("\t\t\treturn true;\n");
		sb.append("\t\t}\n");
	}

	//////////////////////////////////////////
	// Condition expression tree generation //
	//////////////////////////////////////////

	private void genExpressionTree(StringBuffer sb, Expression expr, String className,
			String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName)
	{
		if(expr instanceof Operator) {
			Operator op = (Operator) expr;
			String opNamePrefix = "";
			if(op.getType() instanceof SetType || op.getType() instanceof MapType)
				opNamePrefix = "DICT_";
			if(op.getType() instanceof ArrayType)
				opNamePrefix = "LIST_";
			if(op.getType() instanceof DequeType)
				opNamePrefix = "DEQUE_";
			if(op.getOpCode()==Operator.EQ || op.getOpCode()==Operator.NE
				|| op.getOpCode()==Operator.GT || op.getOpCode()==Operator.GE
				|| op.getOpCode()==Operator.LT || op.getOpCode()==Operator.LE) {
				Expression opnd = op.getOperand(0); // or .getOperand(1), irrelevant
				if(opnd.getType() instanceof SetType || opnd.getType() instanceof MapType) {
					opNamePrefix = "DICT_";
				}
				if(opnd.getType() instanceof ArrayType) {
					opNamePrefix = "LIST_";
				}
				if(opnd.getType() instanceof DequeType) {
					opNamePrefix = "DEQUE_";
				}
			}

			sb.append("new GRGEN_EXPR." + opNamePrefix + Operator.opNames[op.getOpCode()] + "(");
			switch (op.arity()) {
				case 1:
					genExpressionTree(sb, op.getOperand(0), className, pathPrefix, alreadyDefinedEntityToName);
					break;
				case 2:
					genExpressionTree(sb, op.getOperand(0), className, pathPrefix, alreadyDefinedEntityToName);
					sb.append(", ");
					genExpressionTree(sb, op.getOperand(1), className, pathPrefix, alreadyDefinedEntityToName);
					if(op.getOpCode()==Operator.IN) {
						if(op.getOperand(0) instanceof GraphEntityExpression)
							sb.append(", \"" + formatElementInterfaceRef(op.getOperand(0).getType()) + "\"");
						boolean isDictionary = op.getOperand(1).getType() instanceof SetType || op.getOperand(1).getType() instanceof MapType;
						sb.append(isDictionary ? ", true" : ", false");
					}
					break;
				case 3:
					if(op.getOpCode()==Operator.COND) {
						genExpressionTree(sb, op.getOperand(0), className, pathPrefix, alreadyDefinedEntityToName);
						sb.append(", ");
						genExpressionTree(sb, op.getOperand(1), className, pathPrefix, alreadyDefinedEntityToName);
						sb.append(", ");
						genExpressionTree(sb, op.getOperand(2), className, pathPrefix, alreadyDefinedEntityToName);
						break;
					}
					// FALLTHROUGH
				default:
					throw new UnsupportedOperationException(
						"Unsupported operation arity (" + op.arity() + ")");
			}
			sb.append(")");
		}
		else if(expr instanceof Qualification) {
			Qualification qual = (Qualification) expr;
			Entity owner = qual.getOwner();
			Entity member = qual.getMember();
			if(!Expression.isGlobalVariable(owner)) {
				sb.append("new GRGEN_EXPR.Qualification(\"" + formatElementInterfaceRef(owner.getType())
					+ "\", \"" + formatEntity(owner, pathPrefix, alreadyDefinedEntityToName) + "\", \"" + formatIdentifiable(member) + "\")");
			} else {
				sb.append("new GRGEN_EXPR.GlobalVariableQualification(\"" + formatType(owner.getType())
						+ "\", \"" + formatIdentifiable(owner, pathPrefix, alreadyDefinedEntityToName) + "\", \"" + formatIdentifiable(member) + "\")");
			}
		}
		else if(expr instanceof EnumExpression) {
			EnumExpression enumExp = (EnumExpression) expr;
			sb.append("new GRGEN_EXPR.ConstantEnumExpression(\"" + enumExp.getType().getIdent().toString()
					+ "\", \"" + enumExp.getEnumItem().toString() + "\")");
		}
		else if(expr instanceof Constant) { // gen C-code for constant expressions
			Constant constant = (Constant) expr;
			sb.append("new GRGEN_EXPR.Constant(\"" + escapeDoubleQuotes(getValueAsCSSharpString(constant)) + "\")");
		}
		else if(expr instanceof Nameof) {
			Nameof no = (Nameof) expr;
			sb.append("new GRGEN_EXPR.Nameof("
					+ (no.getEntity()==null ? "null" : "\""+formatEntity(no.getEntity(), pathPrefix, alreadyDefinedEntityToName)+"\"")
					+ ")");
		}
		else if(expr instanceof Typeof) {
			Typeof to = (Typeof) expr;
			sb.append("new GRGEN_EXPR.Typeof(\"" + formatEntity(to.getEntity(), pathPrefix, alreadyDefinedEntityToName) + "\")");
		}
		else if(expr instanceof Cast) {
			Cast cast = (Cast) expr;
			String typeName = getTypeNameForCast(cast);
			if(typeName == "object") {
				// no cast needed
				genExpressionTree(sb, cast.getExpression(), className, pathPrefix, alreadyDefinedEntityToName);
			} else {
				sb.append("new GRGEN_EXPR.Cast(\"" + typeName + "\", ");
				genExpressionTree(sb, cast.getExpression(), className, pathPrefix, alreadyDefinedEntityToName);
				if(cast.getExpression().getType() instanceof SetType 
					|| cast.getExpression().getType() instanceof MapType
					|| cast.getExpression().getType() instanceof ArrayType 
					|| cast.getExpression().getType() instanceof DequeType) {
					sb.append(", true");
				} else {
					sb.append(", false");
				}
				sb.append(")");
			}
		}
		else if(expr instanceof VariableExpression) {
			Variable var = ((VariableExpression) expr).getVariable();
			if(!Expression.isGlobalVariable(var)) {
				sb.append("new GRGEN_EXPR.VariableExpression(\"" + formatEntity(var, pathPrefix, alreadyDefinedEntityToName) + "\")");
			} else {
				sb.append("new GRGEN_EXPR.GlobalVariableExpression(\"" + formatIdentifiable(var) + "\", \"" + formatType(var.getType()) + "\")");
			}
		}
		else if(expr instanceof GraphEntityExpression) {
			GraphEntity ent = ((GraphEntityExpression) expr).getGraphEntity();
			if(!Expression.isGlobalVariable(ent)) {
				sb.append("new GRGEN_EXPR.GraphEntityExpression(\"" + formatEntity(ent, pathPrefix, alreadyDefinedEntityToName) + "\")");
			} else {
				sb.append("new GRGEN_EXPR.GlobalVariableExpression(\"" + formatIdentifiable(ent) + "\", \"" + formatType(ent.getType()) + "\")");
			}
		}
		else if(expr instanceof Visited) {
			Visited vis = (Visited) expr;
			sb.append("new GRGEN_EXPR.Visited(");
			genExpressionTree(sb, vis.getEntity(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, vis.getVisitorID(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if(expr instanceof RandomExpr) {
			RandomExpr re = (RandomExpr) expr;
			sb.append("new GRGEN_EXPR.Random(");
			if(re.getNumExpr()!=null)
				genExpressionTree(sb, re.getNumExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof StringLength) {
			StringLength strlen = (StringLength) expr;
			sb.append("new GRGEN_EXPR.StringLength(");
			genExpressionTree(sb, strlen.getStringExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof StringSubstring) {
			StringSubstring strsubstr = (StringSubstring) expr;
			sb.append("new GRGEN_EXPR.StringSubstring(");
			genExpressionTree(sb, strsubstr.getStringExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strsubstr.getStartExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strsubstr.getLengthExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof StringIndexOf) {
			StringIndexOf strio = (StringIndexOf) expr;
			sb.append("new GRGEN_EXPR.StringIndexOf(");
			genExpressionTree(sb, strio.getStringExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strio.getStringToSearchForExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof StringLastIndexOf) {
			StringLastIndexOf strlio = (StringLastIndexOf) expr;
			sb.append("new GRGEN_EXPR.StringLastIndexOf(");
			genExpressionTree(sb, strlio.getStringExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strlio.getStringToSearchForExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof StringReplace) {
			StringReplace strrepl = (StringReplace) expr;
			sb.append("new GRGEN_EXPR.StringReplace(");
			genExpressionTree(sb, strrepl.getStringExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strrepl.getStartExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strrepl.getLengthExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, strrepl.getReplaceStrExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof IndexedAccessExpr) {
			IndexedAccessExpr ia = (IndexedAccessExpr)expr;
			if(ia.getTargetExpr().getType() instanceof MapType)
				sb.append("new GRGEN_EXPR.MapAccess(");
			else //if(ia.getTargetExpr().getType() instanceof ArrayType)
				sb.append("new GRGEN_EXPR.ArrayAccess(");
			genExpressionTree(sb, ia.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ia.getKeyExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			if(ia.getKeyExpr() instanceof GraphEntityExpression)
				sb.append(", \"" + formatElementInterfaceRef(ia.getKeyExpr().getType()) + "\"");
			sb.append(")");
		}
		else if (expr instanceof MapSizeExpr) {
			MapSizeExpr ms = (MapSizeExpr)expr;
			sb.append("new GRGEN_EXPR.MapSize(");
			genExpressionTree(sb, ms.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MapEmptyExpr) {
			MapEmptyExpr me = (MapEmptyExpr)expr;
			sb.append("new GRGEN_EXPR.MapEmpty(");
			genExpressionTree(sb, me.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MapDomainExpr) {
			MapDomainExpr md = (MapDomainExpr)expr;
			sb.append("new GRGEN_EXPR.MapDomain(");
			genExpressionTree(sb, md.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MapRangeExpr) {
			MapRangeExpr mr = (MapRangeExpr)expr;
			sb.append("new GRGEN_EXPR.MapRange(");
			genExpressionTree(sb, mr.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MapPeekExpr) {
			MapPeekExpr mp = (MapPeekExpr)expr;
			sb.append("new GRGEN_EXPR.MapPeek(");
			genExpressionTree(sb, mp.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, mp.getNumberExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof SetSizeExpr) {
			SetSizeExpr ss = (SetSizeExpr)expr;
			sb.append("new GRGEN_EXPR.SetSize(");
			genExpressionTree(sb, ss.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof SetEmptyExpr) {
			SetEmptyExpr se = (SetEmptyExpr)expr;
			sb.append("new GRGEN_EXPR.SetEmpty(");
			genExpressionTree(sb, se.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof SetPeekExpr) {
			SetPeekExpr sp = (SetPeekExpr)expr;
			sb.append("new GRGEN_EXPR.SetPeek(");
			genExpressionTree(sb, sp.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, sp.getNumberExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ArraySizeExpr) {
			ArraySizeExpr as = (ArraySizeExpr)expr;
			sb.append("new GRGEN_EXPR.ArraySize(");
			genExpressionTree(sb, as.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ArrayEmptyExpr) {
			ArrayEmptyExpr ae = (ArrayEmptyExpr)expr;
			sb.append("new GRGEN_EXPR.ArrayEmpty(");
			genExpressionTree(sb, ae.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ArrayPeekExpr) {
			ArrayPeekExpr ap = (ArrayPeekExpr)expr;
			sb.append("new GRGEN_EXPR.ArrayPeek(");
			genExpressionTree(sb, ap.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			if(ap.getNumberExpr()!=null) {
				sb.append(", ");
				genExpressionTree(sb, ap.getNumberExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			}
			sb.append(")");
		}
		else if (expr instanceof ArrayIndexOfExpr) {
			ArrayIndexOfExpr ai = (ArrayIndexOfExpr)expr;
			sb.append("new GRGEN_EXPR.ArrayIndexOf(");
			genExpressionTree(sb, ai.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ai.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ArrayLastIndexOfExpr) {
			ArrayLastIndexOfExpr ali = (ArrayLastIndexOfExpr)expr;
			sb.append("new GRGEN_EXPR.ArrayLastIndexOf(");
			genExpressionTree(sb, ali.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ali.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ArraySubarrayExpr) {
			ArraySubarrayExpr asa = (ArraySubarrayExpr)expr;
			sb.append("new GRGEN_EXPR.ArraySubarray(");
			genExpressionTree(sb, asa.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, asa.getStartExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, asa.getLengthExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof DequeSizeExpr) {
			DequeSizeExpr ds = (DequeSizeExpr)expr;
			sb.append("new GRGEN_EXPR.DequeSize(");
			genExpressionTree(sb, ds.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof DequeEmptyExpr) {
			DequeEmptyExpr de = (DequeEmptyExpr)expr;
			sb.append("new GRGEN_EXPR.DequeEmpty(");
			genExpressionTree(sb, de.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof DequePeekExpr) {
			DequePeekExpr dp = (DequePeekExpr)expr;
			sb.append("new GRGEN_EXPR.DequePeek(");
			genExpressionTree(sb, dp.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			if(dp.getNumberExpr()!=null) {
				sb.append(", ");
				genExpressionTree(sb, dp.getNumberExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			}
			sb.append(")");
		}
		else if (expr instanceof DequeIndexOfExpr) {
			DequeIndexOfExpr di = (DequeIndexOfExpr)expr;
			sb.append("new GRGEN_EXPR.DequeIndexOf(");
			genExpressionTree(sb, di.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, di.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof DequeLastIndexOfExpr) {
			DequeLastIndexOfExpr dli = (DequeLastIndexOfExpr)expr;
			sb.append("new GRGEN_EXPR.DequeLastIndexOf(");
			genExpressionTree(sb, dli.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, dli.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof DequeSubdequeExpr) {
			DequeSubdequeExpr dsd = (DequeSubdequeExpr)expr;
			sb.append("new GRGEN_EXPR.DequeSubdeque(");
			genExpressionTree(sb, dsd.getTargetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, dsd.getStartExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, dsd.getLengthExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MapInit) {
			MapInit mi = (MapInit)expr;
			if(mi.isConstant()) {
				sb.append("new GRGEN_EXPR.StaticMap(\"" + className + "\", \"" + mi.getAnonymousMapName() + "\")");
			} else {
				sb.append("new GRGEN_EXPR.MapConstructor(\"" + className + "\", \"" + mi.getAnonymousMapName() + "\",");
				int openParenthesis = 0;
				for(MapItem item : mi.getMapItems()) {
					sb.append("new GRGEN_EXPR.MapItem(");
					genExpressionTree(sb, item.getKeyExpr(), className, pathPrefix, alreadyDefinedEntityToName);
					sb.append(", ");
					if(item.getKeyExpr() instanceof GraphEntityExpression)
						sb.append("\"" + formatElementInterfaceRef(item.getKeyExpr().getType()) + "\", ");
					else
						sb.append("null, ");
					genExpressionTree(sb, item.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
					sb.append(", ");
					if(item.getValueExpr() instanceof GraphEntityExpression)
						sb.append("\"" + formatElementInterfaceRef(item.getValueExpr().getType()) + "\", ");
					else
						sb.append("null, ");
					++openParenthesis;
				}
				sb.append("null");
				for(int i=0; i<openParenthesis; ++i) sb.append(")");
				sb.append(")");
			}
		}
		else if (expr instanceof SetInit) {
			SetInit si = (SetInit)expr;
			if(si.isConstant()) {
				sb.append("new GRGEN_EXPR.StaticSet(\"" + className + "\", \"" + si.getAnonymousSetName() + "\")");
			} else {
				sb.append("new GRGEN_EXPR.SetConstructor(\"" + className + "\", \"" + si.getAnonymousSetName() + "\", ");
				int openParenthesis = 0;
				for(SetItem item : si.getSetItems()) {
					sb.append("new GRGEN_EXPR.SetItem(");
					genExpressionTree(sb, item.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
					sb.append(", ");
					if(item.getValueExpr() instanceof GraphEntityExpression)
						sb.append("\"" + formatElementInterfaceRef(item.getValueExpr().getType()) + "\", ");
					else
						sb.append("null, ");
					++openParenthesis;
				}
				sb.append("null");
				for(int i=0; i<openParenthesis; ++i) sb.append(")");
				sb.append(")");
			}
		}
		else if (expr instanceof ArrayInit) {
			ArrayInit ai = (ArrayInit)expr;
			if(ai.isConstant()) {
				sb.append("new GRGEN_EXPR.StaticArray(\"" + className + "\", \"" + ai.getAnonymousArrayName() + "\")");
			} else {
				sb.append("new GRGEN_EXPR.ArrayConstructor(\"" + className + "\", \"" + ai.getAnonymousArrayName() + "\", ");
				int openParenthesis = 0;
				for(ArrayItem item : ai.getArrayItems()) {
					sb.append("new GRGEN_EXPR.ArrayItem(");
					genExpressionTree(sb, item.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
					sb.append(", ");
					if(item.getValueExpr() instanceof GraphEntityExpression)
						sb.append("\"" + formatElementInterfaceRef(item.getValueExpr().getType()) + "\", ");
					else
						sb.append("null, ");
					++openParenthesis;
				}
				sb.append("null");
				for(int i=0; i<openParenthesis; ++i) sb.append(")");
				sb.append(")");
			}
		}
		else if (expr instanceof DequeInit) {
			DequeInit di = (DequeInit)expr;
			if(di.isConstant()) {
				sb.append("new GRGEN_EXPR.StaticDeque(\"" + className + "\", \"" + di.getAnonymousDequeName() + "\")");
			} else {
				sb.append("new GRGEN_EXPR.DequeConstructor(\"" + className + "\", \"" + di.getAnonymousDequeName() + "\", ");
				int openParenthesis = 0;
				for(DequeItem item : di.getDequeItems()) {
					sb.append("new GRGEN_EXPR.DequeItem(");
					genExpressionTree(sb, item.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
					sb.append(", ");
					if(item.getValueExpr() instanceof GraphEntityExpression)
						sb.append("\"" + formatElementInterfaceRef(item.getValueExpr().getType()) + "\", ");
					else
						sb.append("null, ");
					++openParenthesis;
				}
				sb.append("null");
				for(int i=0; i<openParenthesis; ++i) sb.append(")");
				sb.append(")");
			}
		}
		else if (expr instanceof ComputationInvocationExpr) {
			ComputationInvocationExpr ci = (ComputationInvocationExpr) expr;
			sb.append("new GRGEN_EXPR.ComputationInvocation(\"" + ci.getComputation().getIdent() + "\", new GRGEN_EXPR.Expression[] {");
			for(int i=0; i<ci.arity(); ++i) {
				Expression argument = ci.getArgument(i);
				genExpressionTree(sb, argument, className, pathPrefix, alreadyDefinedEntityToName);
				sb.append(", ");
			}
			sb.append("}, ");
			sb.append("new String[] {");
			for(int i=0; i<ci.arity(); ++i) {
				Expression argument = ci.getArgument(i);
				if(argument.getType() instanceof InheritanceType) {
					sb.append("\"" + formatElementInterfaceRef(argument.getType()) + "\"");
				} else {
					sb.append("null");
				}
				sb.append(", ");
			}
			sb.append("}");
			sb.append(")");
		}
		else if (expr instanceof ExternalFunctionInvocationExpr) {
			ExternalFunctionInvocationExpr efi = (ExternalFunctionInvocationExpr)expr;
			sb.append("new GRGEN_EXPR.ExternalFunctionInvocation(\"" + efi.getExternalFunc().getIdent() + "\", new GRGEN_EXPR.Expression[] {");
			for(int i=0; i<efi.arity(); ++i) {
				Expression argument = efi.getArgument(i);
				genExpressionTree(sb, argument, className, pathPrefix, alreadyDefinedEntityToName);
				sb.append(", ");
			}
			sb.append("}, ");
			sb.append("new String[] {");
			for(int i=0; i<efi.arity(); ++i) {
				Expression argument = efi.getArgument(i);
				if(argument.getType() instanceof InheritanceType) {
					sb.append("\"" + formatElementInterfaceRef(argument.getType()) + "\"");
				} else {
					sb.append("null");
				}
				sb.append(", ");
			}
			sb.append("}");
			sb.append(")");
		}
		else if (expr instanceof EdgesExpr) {
			EdgesExpr e = (EdgesExpr) expr;
			sb.append("new GRGEN_EXPR.Edges(");
			genExpressionTree(sb, e.getEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof NodesExpr) {
			NodesExpr n = (NodesExpr) expr;
			sb.append("new GRGEN_EXPR.Nodes(");
			genExpressionTree(sb, n.getNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof SourceExpr) {
			SourceExpr s = (SourceExpr) expr;
			sb.append("new GRGEN_EXPR.Source(");
			genExpressionTree(sb, s.getEdgeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof TargetExpr) {
			TargetExpr t = (TargetExpr) expr;
			sb.append("new GRGEN_EXPR.Target(");
			genExpressionTree(sb, t.getEdgeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof OppositeExpr) {
			OppositeExpr o = (OppositeExpr) expr;
			sb.append("new GRGEN_EXPR.Opposite(");
			genExpressionTree(sb, o.getEdgeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(",");
			genExpressionTree(sb, o.getNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof IncidentEdgeExpr) {
			IncidentEdgeExpr ie = (IncidentEdgeExpr) expr;
			if(ie.Direction()==IncidentEdgeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.Outgoing(");
			} else if(ie.Direction()==IncidentEdgeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.Incoming(");
			} else {
				sb.append("new GRGEN_EXPR.Incident(");
			}
			genExpressionTree(sb, ie.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ie.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ie.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof AdjacentNodeExpr) {
			AdjacentNodeExpr an = (AdjacentNodeExpr) expr;
			if(an.Direction()==AdjacentNodeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.AdjacentOutgoing(");
			} else if(an.Direction()==AdjacentNodeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.AdjacentIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.Adjacent(");
			}
			genExpressionTree(sb, an.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, an.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, an.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof IsAdjacentNodeExpr) {
			IsAdjacentNodeExpr ian = (IsAdjacentNodeExpr) expr;
			if(ian.Direction()==IsAdjacentNodeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.IsAdjacentOutgoing(");
			} else if(ian.Direction()==IsAdjacentNodeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.IsAdjacentIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.IsAdjacent(");
			}
			genExpressionTree(sb, ian.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ian.getEndNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ian.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ian.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof IsIncidentEdgeExpr) {
			IsIncidentEdgeExpr iie = (IsIncidentEdgeExpr) expr;
			if(iie.Direction()==IsIncidentEdgeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.IsOutgoing(");
			} else if(iie.Direction()==IsIncidentEdgeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.IsIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.IsIncident(");
			}
			genExpressionTree(sb, iie.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, iie.getEndEdgeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, iie.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, iie.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ReachableEdgeExpr) {
			ReachableEdgeExpr re = (ReachableEdgeExpr) expr;
			if(re.Direction()==ReachableEdgeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.ReachableEdgesOutgoing(");
			} else if(re.Direction()==ReachableEdgeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.ReachableEdgesIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.ReachableEdges(");
			}
			genExpressionTree(sb, re.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, re.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, re.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof ReachableNodeExpr) {
			ReachableNodeExpr rn = (ReachableNodeExpr) expr;
			if(rn.Direction()==ReachableNodeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.ReachableOutgoing(");
			} else if(rn.Direction()==ReachableNodeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.ReachableIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.Reachable(");
			}
			genExpressionTree(sb, rn.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, rn.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, rn.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof IsReachableNodeExpr) {
			IsReachableNodeExpr irn = (IsReachableNodeExpr) expr;
			if(irn.Direction()==IsReachableNodeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.IsReachableOutgoing(");
			} else if(irn.Direction()==IsReachableNodeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.IsReachableIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.IsReachable(");
			}
			genExpressionTree(sb, irn.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, irn.getEndNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, irn.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, irn.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof IsReachableEdgeExpr) {
			IsReachableEdgeExpr ire = (IsReachableEdgeExpr) expr;
			if(ire.Direction()==IsReachableEdgeExpr.OUTGOING) {
				sb.append("new GRGEN_EXPR.IsReachableEdgesOutgoing(");
			} else if(ire.Direction()==IsReachableEdgeExpr.INCOMING) {
				sb.append("new GRGEN_EXPR.IsReachableEdgesIncoming(");
			} else {
				sb.append("new GRGEN_EXPR.IsReachableEdges(");
			}
			genExpressionTree(sb, ire.getStartNodeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ire.getEndEdgeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ire.getIncidentEdgeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, ire.getAdjacentNodeTypeExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof InducedSubgraphExpr) {
			InducedSubgraphExpr is = (InducedSubgraphExpr) expr;
			sb.append("new GRGEN_EXPR.InducedSubgraph(");
			genExpressionTree(sb, is.getSetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof DefinedSubgraphExpr) {
			DefinedSubgraphExpr ds = (DefinedSubgraphExpr) expr;
			sb.append("new GRGEN_EXPR.DefinedSubgraph(");
			genExpressionTree(sb, ds.getSetExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MaxExpr) {
			MaxExpr m = (MaxExpr) expr;
			sb.append("new GRGEN_EXPR.Max(");
			genExpressionTree(sb, m.getLeftExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, m.getRightExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof MinExpr) {
			MinExpr m = (MinExpr) expr;
			sb.append("new GRGEN_EXPR.Min(");
			genExpressionTree(sb, m.getLeftExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
			genExpressionTree(sb, m.getRightExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof AbsExpr) {
			AbsExpr a = (AbsExpr) expr;
			sb.append("new GRGEN_EXPR.Abs(");
			genExpressionTree(sb, a.getExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof SinCosTanExpr) {
			SinCosTanExpr sct = (SinCosTanExpr)expr;
			switch(sct.getWhich()) {
			case SinCosTanExpr.SIN:
				sb.append("new GRGEN_EXPR.Sin(");
				break;
			case SinCosTanExpr.COS:
				sb.append("new GRGEN_EXPR.Cos(");
				break;
			case SinCosTanExpr.TAN:
				sb.append("new GRGEN_EXPR.Tan(");
				break;
			}
			genExpressionTree(sb, sct.getExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof CanonizeExpr) {
			CanonizeExpr c = (CanonizeExpr) expr;
			sb.append("new GRGEN_EXPR.Canonize(");
			genExpressionTree(sb, c.getGraphExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof LogExpr) {
			LogExpr l = (LogExpr) expr;
			sb.append("new GRGEN_EXPR.Log(");
			genExpressionTree(sb, l.getLeftExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			if(l.getRightExpr()!=null) {
				sb.append(", ");
				genExpressionTree(sb, l.getRightExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			}
			sb.append(")");
		}
		else if (expr instanceof PowExpr) {
			PowExpr p = (PowExpr) expr;
			sb.append("new GRGEN_EXPR.Pow(");
			if(p.getLeftExpr()!=null) {
				genExpressionTree(sb, p.getLeftExpr(), className, pathPrefix, alreadyDefinedEntityToName);
				sb.append(", ");
			}
			genExpressionTree(sb, p.getRightExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(")");
		}
		else if (expr instanceof VAllocExpr
			|| expr instanceof GraphAddNodeExpr
			|| expr instanceof GraphAddEdgeExpr
			|| expr instanceof StartTransactionExpr) {
			throw new UnsupportedOperationException("valloc, add(NodeType), add(EdgeType, SrcNode, TgtNode) are not available in rule LHS expressions");
		}
		else throw new UnsupportedOperationException("Unsupported expression type (" + expr + ")");
	}

	///////////////////////////////////////
	// Static searchplan cost generation //
	///////////////////////////////////////

	private double computePriosMax(double max, PatternGraph pattern) {
		max = computePriosMax(pattern.getNodes(), max);
		max = computePriosMax(pattern.getEdges(), max);
		for(PatternGraph neg : pattern.getNegs()) {
			max = computePriosMax(max, neg);
		}
		for(PatternGraph idpt : pattern.getIdpts()) {
			max = computePriosMax(max, idpt);
		}
		for(Alternative alt : pattern.getAlts()) {
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				max = computePriosMax(max, altCasePattern);
			}
		}
		for(Rule iter : pattern.getIters()) {
			PatternGraph iterPattern = iter.getLeft();
			max = computePriosMax(max, iterPattern);
		}
		return max;
	}

	private double computePriosMax(Collection<? extends Entity> nodesOrEdges, double max) {
		for(Entity noe : nodesOrEdges) {
			Object prioO = noe.getAnnotations().get("prio");

			if (prioO != null && prioO instanceof Integer) {
				double val = ((Integer)prioO).doubleValue();
				assert val > 0 : "value of prio attribute of decl is out of range.";

				if(max < val)
					max = val;
			}
		}
		return max;
	}

	private void appendPrio(StringBuffer sb, Entity entity, double max) {
		Object prioO = entity.getAnnotations().get("prio");

		double prio;
		if (prioO != null && prioO instanceof Integer) {
			prio = ((Integer)prioO).doubleValue();
			prio = 10.0 - (prio / max) * 9.0;
		}
		else {
			prio = 5.5;
		}

		sb.append(prio + "F, ");
	}

	//////////////////////
	// Expression stuff //
	//////////////////////

	protected void genQualAccess(StringBuffer sb, Qualification qual, Object modifyGenerationState) {
		Entity owner = qual.getOwner();
		Entity member = qual.getMember();
		genQualAccess(sb, owner, member);
	}

	protected void genQualAccess(StringBuffer sb, Entity owner, Entity member) {
		sb.append("((I" + getNodeOrEdgeTypePrefix(owner) +
					  formatIdentifiable(owner.getType()) + ") ");
		sb.append(formatEntity(owner) + ").@" + formatIdentifiable(member));
	}

	protected void genMemberAccess(StringBuffer sb, Entity member) {
		throw new UnsupportedOperationException("Member expressions not allowed in actions!");
	}

	/////////////////////////////////////////////
	// Static constructor calling static inits //
	/////////////////////////////////////////////

	protected void genStaticConstructor(StringBuffer sb, String className, List<String> staticInitializers)
	{
		sb.append("\n");
		sb.append("\t\tstatic " + className + "() {\n");
		for(String staticInit : staticInitializers) {
			sb.append("\t\t\t" + staticInit + "();\n");
		}
		sb.append("\t\t}\n");
		sb.append("\n");
	}

	//////////////////////////////
	// Match objects generation //
	//////////////////////////////

	private void genPatternMatchInterface(StringBuffer sb, PatternGraph pattern, String name,
			String base, String pathPrefixForElements, boolean iterated, boolean alternativeCase)
	{
		genMatchInterface(sb, pattern, name,
				base, pathPrefixForElements, iterated, alternativeCase);

		for(PatternGraph neg : pattern.getNegs()) {
			String negName = neg.getNameOfGraph();
			genPatternMatchInterface(sb, neg, pathPrefixForElements+negName,
					"GRGEN_LIBGR.IMatch", pathPrefixForElements + negName + "_",
					false, false);
		}

		for(PatternGraph idpt : pattern.getIdpts()) {
			String idptName = idpt.getNameOfGraph();
			genPatternMatchInterface(sb, idpt, pathPrefixForElements+idptName,
					"GRGEN_LIBGR.IMatch", pathPrefixForElements + idptName + "_",
					false, false);
		}

		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			genAlternativeMatchInterface(sb, pathPrefixForElements + altName);
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				String altPatName = pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph();
				genPatternMatchInterface(sb, altCasePattern, altPatName,
						"IMatch_"+pathPrefixForElements+altName,
						pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph() + "_",
						false, true);
			}
		}

		for(Rule iter : pattern.getIters()) {
			PatternGraph iterPattern = iter.getLeft();
			String iterName = iterPattern.getNameOfGraph();
			genPatternMatchInterface(sb, iterPattern, pathPrefixForElements+iterName,
					"GRGEN_LIBGR.IMatch", pathPrefixForElements + iterName + "_",
					true, false);
		}
	}

	private void genPatternMatchImplementation(StringBuffer sb, PatternGraph pattern, String name,
			String patGraphVarName, String className,
			String pathPrefixForElements, boolean iterated, boolean independent)
	{
		genMatchImplementation(sb, pattern, name,
				patGraphVarName, className, pathPrefixForElements, iterated, independent);

		for(PatternGraph neg : pattern.getNegs()) {
			String negName = neg.getNameOfGraph();
			genPatternMatchImplementation(sb, neg, pathPrefixForElements+negName,
					pathPrefixForElements+negName, className,
					pathPrefixForElements+negName+"_", false, false);
		}

		for(PatternGraph idpt : pattern.getIdpts()) {
			String idptName = idpt.getNameOfGraph();
			genPatternMatchImplementation(sb, idpt, pathPrefixForElements+idptName,
					pathPrefixForElements+idptName, className,
					pathPrefixForElements+idptName+"_", false, true);
		}

		for(Alternative alt : pattern.getAlts()) {
			String altName = alt.getNameOfGraph();
			for(Rule altCase : alt.getAlternativeCases()) {
				PatternGraph altCasePattern = altCase.getLeft();
				String altPatName = pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph();
				genPatternMatchImplementation(sb, altCasePattern, altPatName,
						altPatName, className,
						pathPrefixForElements + altName + "_" + altCasePattern.getNameOfGraph() + "_", 
						false, false);
			}
		}

		for(Rule iter : pattern.getIters()) {
			PatternGraph iterPattern = iter.getLeft();
			String iterName = iterPattern.getNameOfGraph();
			genPatternMatchImplementation(sb, iterPattern, pathPrefixForElements+iterName,
					pathPrefixForElements+iterName, className,
					pathPrefixForElements+iterName+"_", true, false);
		}
	}

	private void genMatchInterface(StringBuffer sb, PatternGraph pattern,
			String name, String base,
			String pathPrefixForElements, boolean iterated, boolean alternativeCase)
	{
		String interfaceName = "IMatch_" + name;
		sb.append("\t\tpublic interface "+interfaceName+" : "+base+"\n");
		sb.append("\t\t{\n");

		for(int i=MATCH_PART_NODES; i<MATCH_PART_END; ++i) {
			genMatchedEntitiesInterface(sb, pattern,
					name, i, pathPrefixForElements);
		}

		sb.append("\t\t\t// further match object stuff\n");

		if(iterated) {
			sb.append("\t\t\tbool IsNullMatch { get; }\n");
		}

		if(alternativeCase) {
			sb.append("\t\t\tnew void SetMatchOfEnclosingPattern(GRGEN_LIBGR.IMatch matchOfEnclosingPattern);\n");
		} else {
			sb.append("\t\t\tvoid SetMatchOfEnclosingPattern(GRGEN_LIBGR.IMatch matchOfEnclosingPattern);\n");
		}
		sb.append("\t\t}\n");
		sb.append("\n");
	}

	private void genAlternativeMatchInterface(StringBuffer sb, String name)
	{
		String interfaceName = "IMatch_" + name;
		sb.append("\t\tpublic interface "+interfaceName+" : GRGEN_LIBGR.IMatch\n");
		sb.append("\t\t{\n");

		sb.append("\t\t\tvoid SetMatchOfEnclosingPattern(GRGEN_LIBGR.IMatch matchOfEnclosingPattern);\n");

		sb.append("\t\t}\n");
		sb.append("\n");
	}

	private void genMatchImplementation(StringBuffer sb, PatternGraph pattern, String name,
			String patGraphVarName, String ruleClassName,
			String pathPrefixForElements, boolean iterated, boolean independent)
	{
		String interfaceName = "IMatch_" + name;
		String className = "Match_" + name;
		sb.append("\t\tpublic class "+className+" : GRGEN_LGSP.ListElement<"+className+">, "+interfaceName+"\n");
		sb.append("\t\t{\n");

		for(int i=MATCH_PART_NODES; i<MATCH_PART_END; ++i) {
			genMatchedEntitiesImplementation(sb, pattern, name,
					i, pathPrefixForElements);
			genMatchEnum(sb, pattern, name,
					i, pathPrefixForElements);
			genIMatchImplementation(sb, pattern, name,
					i, pathPrefixForElements);
			sb.append("\t\t\t\n");
		}

		sb.append("\t\t\tpublic GRGEN_LIBGR.IPatternGraph Pattern { get { return "+ruleClassName+".instance."+patGraphVarName+"; } }\n");
		if(iterated) {
			sb.append("\t\t\tpublic bool IsNullMatch { get { return _isNullMatch; } }\n");
			sb.append("\t\t\tpublic bool _isNullMatch;\n");
		}
		sb.append("\t\t\tpublic GRGEN_LIBGR.IMatch MatchOfEnclosingPattern { get { return _matchOfEnclosingPattern; } }\n");
		sb.append("\t\t\tpublic GRGEN_LIBGR.IMatch Clone() { return new "+className+"(this); }\n");
		sb.append("\t\t\tpublic GRGEN_LIBGR.IMatch _matchOfEnclosingPattern;\n");
		sb.append("\t\t\tpublic void SetMatchOfEnclosingPattern(GRGEN_LIBGR.IMatch matchOfEnclosingPattern) { _matchOfEnclosingPattern = matchOfEnclosingPattern; }\n");
		sb.append("\t\t\tpublic override string ToString() { return \"Match of \" + Pattern.Name; }\n");
		sb.append("\t\t\tpublic bool _flag;\n");
		sb.append("\t\t\tpublic void Mark(bool flag) { _flag = flag; }\n");
		sb.append("\t\t\tpublic bool IsMarked() { return _flag; }\n");

		sb.append("\n\t\t\tpublic "+className+"("+className +" that)\n");
		sb.append("\t\t\t{\n");
		for(int i=MATCH_PART_NODES; i<MATCH_PART_END; ++i) {
			genCopyMatchedEntities(sb, pattern, name, i, pathPrefixForElements);
		}
		sb.append("\t\t\t}\n");
		sb.append("\t\t\tpublic "+className+"()\n");
		sb.append("\t\t\t{\n");
		sb.append("\t\t\t}\n");

		sb.append("\t\t}\n");
		sb.append("\n");
	}

	private void genMatchedEntitiesInterface(StringBuffer sb, PatternGraph pattern,
			String name, int which, String pathPrefixForElements)
	{
		// the getters for the elements
		sb.append("\t\t\t//"+matchedEntitiesNamePlural(which)+"\n");
		switch(which)
		{
		case MATCH_PART_NODES:
			for(Node node : pattern.getNodes()) {
				sb.append("\t\t\t"+formatElementInterfaceRef(node.getType())+" "+formatEntity(node)+" { get; }\n");
			}
			break;
		case MATCH_PART_EDGES:
			for(Edge edge : pattern.getEdges()) {
				sb.append("\t\t\t"+formatElementInterfaceRef(edge.getType())+" "+formatEntity(edge)+" { get; }\n");
			}
			break;
		case MATCH_PART_VARIABLES:
			for(Variable var : pattern.getVars()) {
				sb.append("\t\t\t"+formatAttributeType(var.getType())+" @"+formatEntity(var)+" { get; }\n");
			}
			break;
		case MATCH_PART_EMBEDDED_GRAPHS:
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				sb.append("\t\t\t@"+matchType(sub.getSubpatternAction().getPattern(), true, "")+" @"+formatIdentifiable(sub)+" { get; }\n");
			}
			break;
		case MATCH_PART_ALTERNATIVES:
			for(Alternative alt : pattern.getAlts()) {
				String altName = alt.getNameOfGraph();
				sb.append("\t\t\tIMatch_"+pathPrefixForElements+altName+" "+altName+" { get; }\n");
			}
			break;
		case MATCH_PART_ITERATEDS:
			for(Rule iter : pattern.getIters()) {
				String iterName = iter.getLeft().getNameOfGraph();
				sb.append("\t\t\tGRGEN_LIBGR.IMatchesExact<IMatch_"+pathPrefixForElements+iterName+"> "+iterName+" { get; }\n");
			}
			break;
		case MATCH_PART_INDEPENDENTS:
			for(PatternGraph idpt : pattern.getIdpts()) {
				String idptName = idpt.getNameOfGraph();
				sb.append("\t\t\tIMatch_"+pathPrefixForElements+idptName+" "+idptName+" { get; }\n");
			}
			break;
		default:
			assert(false);
		}
	}

	private void genMatchedEntitiesImplementation(StringBuffer sb, PatternGraph pattern,
			String name, int which, String pathPrefixForElements)
	{
		// the element itself and the getter for it
		switch(which)
		{
		case MATCH_PART_NODES:
			for(Node node : pattern.getNodes()) {
				sb.append("\t\t\tpublic "+formatElementInterfaceRef(node.getType())+" "+formatEntity(node)
						+ " { get { return ("+formatElementInterfaceRef(node.getType())+")"+formatEntity(node, "_")+"; } }\n");
			}
			for(Node node : pattern.getNodes()) {
				sb.append("\t\t\tpublic GRGEN_LGSP.LGSPNode "+formatEntity(node, "_")+";\n");
			}
			break;
		case MATCH_PART_EDGES:
			for(Edge edge : pattern.getEdges()) {
				sb.append("\t\t\tpublic "+formatElementInterfaceRef(edge.getType())+" "+formatEntity(edge)
						+ " { get { return ("+formatElementInterfaceRef(edge.getType())+")"+formatEntity(edge, "_")+"; } }\n");
			}
			for(Edge edge : pattern.getEdges()) {
				sb.append("\t\t\tpublic GRGEN_LGSP.LGSPEdge "+formatEntity(edge, "_")+";\n");
			}
			break;
		case MATCH_PART_VARIABLES:
			for(Variable var : pattern.getVars()) {
				sb.append("\t\t\tpublic "+formatAttributeType(var.getType())+" "+formatEntity(var)+" { get { return "+formatEntity(var, "_")+"; } }\n");
			}
			for(Variable var : pattern.getVars()) {
				sb.append("\t\t\tpublic "+formatAttributeType(var.getType())+" "+formatEntity(var, "_")+";\n");
			}
			break;
		case MATCH_PART_EMBEDDED_GRAPHS:
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				sb.append("\t\t\tpublic @"+matchType(sub.getSubpatternAction().getPattern(), true, "")+" @"+formatIdentifiable(sub)+" { get { return @"+formatIdentifiable(sub, "_")+"; } }\n");
			}
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				sb.append("\t\t\tpublic @"+matchType(sub.getSubpatternAction().getPattern(), true, "")+" @"+formatIdentifiable(sub, "_")+";\n");
			}
			break;
		case MATCH_PART_ALTERNATIVES:
			for(Alternative alt : pattern.getAlts()) {
				String altName = alt.getNameOfGraph();
				sb.append("\t\t\tpublic IMatch_"+pathPrefixForElements+altName+" "+altName+" { get { return _"+altName+"; } }\n");
			}
			for(Alternative alt : pattern.getAlts()) {
				String altName = alt.getNameOfGraph();
				sb.append("\t\t\tpublic IMatch_"+pathPrefixForElements+altName+" _"+altName+";\n");
			}
			break;
		case MATCH_PART_ITERATEDS:
			for(Rule iter : pattern.getIters()) {
				String iterName = iter.getLeft().getNameOfGraph();
				sb.append("\t\t\tpublic GRGEN_LIBGR.IMatchesExact<IMatch_"+pathPrefixForElements+iterName+"> "+iterName+" { get { return _"+iterName+"; } }\n");
			}
			for(Rule iter : pattern.getIters()) {
				String iterName = iter.getLeft().getNameOfGraph();
				sb.append("\t\t\tpublic GRGEN_LGSP.LGSPMatchesList<Match_"+pathPrefixForElements+iterName+", IMatch_"+pathPrefixForElements+iterName+"> _"+iterName+";\n");
			}
			break;
		case MATCH_PART_INDEPENDENTS:
			for(PatternGraph idpt : pattern.getIdpts()) {
				String idptName = idpt.getNameOfGraph();
				sb.append("\t\t\tpublic IMatch_"+pathPrefixForElements+idptName+" "+idptName+" { get { return _"+idptName+"; } }\n");
			}
			for(PatternGraph idpt : pattern.getIdpts()) {
				String idptName = idpt.getNameOfGraph();
				sb.append("\t\t\tpublic IMatch_"+pathPrefixForElements+idptName+" _"+idptName+";\n");
			}
			break;
		default:
			assert(false);
		}
	}


	private void genCopyMatchedEntities(StringBuffer sb, PatternGraph pattern,
			String name, int which, String pathPrefixForElements)
	{
		switch(which)
		{
		case MATCH_PART_NODES:
			for(Node node : pattern.getNodes()) {
				String nodeName = formatEntity(node, "_");
				sb.append("\t\t\t\t"+nodeName+" = that."+nodeName+";\n");
			}
			break;
		case MATCH_PART_EDGES:
			for(Edge edge : pattern.getEdges()) {
				String edgeName = formatEntity(edge, "_");
				sb.append("\t\t\t\t"+edgeName+" = that."+edgeName+";\n");
			}
			break;
		case MATCH_PART_VARIABLES:
			for(Variable var : pattern.getVars()) {
				String varName = formatEntity(var, "_");
				sb.append("\t\t\t\t"+varName+" = that."+varName+";\n");
			}
			break;
		case MATCH_PART_EMBEDDED_GRAPHS:
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				String subName = "@" + formatIdentifiable(sub, "_");
				sb.append("\t\t\t\t"+subName+" = that."+subName+";\n");
			}
			break;
		case MATCH_PART_ALTERNATIVES:
			for(Alternative alt : pattern.getAlts()) {
				String altName = "_" + alt.getNameOfGraph();
				sb.append("\t\t\t\t"+altName+" = that."+altName+";\n");
			}
			break;
		case MATCH_PART_ITERATEDS:
			for(Rule iter : pattern.getIters()) {
				String iterName = "_" + iter.getLeft().getNameOfGraph();
				sb.append("\t\t\t\t"+iterName+" = that."+iterName+";\n");
			}
			break;
		case MATCH_PART_INDEPENDENTS:
			for(PatternGraph idpt : pattern.getIdpts()) {
				String idptName = "_" + idpt.getNameOfGraph();
				sb.append("\t\t\t\t"+idptName+" = that."+idptName+";\n");
			}
			break;
		default:
			assert(false);
		}
	}

	
	private void genIMatchImplementation(StringBuffer sb, PatternGraph pattern,
			String name, int which, String pathPrefixForElements)
	{
		// the various match part getters

		String enumerableName = "GRGEN_LGSP."+matchedEntitiesNamePlural(which)+"_Enumerable";
		String enumeratorName = "GRGEN_LGSP."+matchedEntitiesNamePlural(which)+"_Enumerator";
		String typeOfMatchedEntities = typeOfMatchedEntities(which);
		int numberOfMatchedEntities = numOfMatchedEntities(which, pattern);
		String matchedEntitiesNameSingular = matchedEntitiesNameSingular(which);
		String matchedEntitiesNamePlural = matchedEntitiesNamePlural(which);

		sb.append("\t\t\tpublic IEnumerable<"+typeOfMatchedEntities+"> "+matchedEntitiesNamePlural+" { get { return new "+enumerableName+"(this); } }\n");
		sb.append("\t\t\tpublic IEnumerator<"+typeOfMatchedEntities+"> "+matchedEntitiesNamePlural+"Enumerator { get { return new " + enumeratorName + "(this); } }\n");
		sb.append("\t\t\tpublic int NumberOf"+matchedEntitiesNamePlural+" { get { return " + numberOfMatchedEntities + ";} }\n");

	    // -----------------------------

		sb.append("\t\t\tpublic "+typeOfMatchedEntities+" get"+matchedEntitiesNameSingular+"At(int index)\n");
		sb.append("\t\t\t{\n");
		sb.append("\t\t\t\tswitch(index) {\n");

		switch(which)
		{
		case MATCH_PART_NODES:
			for(Node node : pattern.getNodes()) {
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + formatIdentifiable(node) + ": return " + formatEntity(node, "_") + ";\n");
			}
			break;
		case MATCH_PART_EDGES:
			for(Edge edge : pattern.getEdges()) {
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + formatIdentifiable(edge) + ": return " + formatEntity(edge, "_") + ";\n");
			}
			break;
		case MATCH_PART_VARIABLES:
			for(Variable var : pattern.getVars()) {
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + formatIdentifiable(var) + ": return " + formatEntity(var, "_") + ";\n");
			}
			break;
		case MATCH_PART_EMBEDDED_GRAPHS:
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + formatIdentifiable(sub) + ": return " + formatIdentifiable(sub, "_") + ";\n");
			}
			break;
		case MATCH_PART_ALTERNATIVES:
			for(Alternative alt : pattern.getAlts()) {
				String altName = alt.getNameOfGraph();
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + altName + ": return _" + altName+ ";\n");
			}
			break;
		case MATCH_PART_ITERATEDS:
			for(Rule iter : pattern.getIters()) {
				String iterName = iter.getLeft().getNameOfGraph();
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + iterName + ": return _" + iterName + ";\n");
			}
			break;
		case MATCH_PART_INDEPENDENTS:
			for(PatternGraph idpt : pattern.getIdpts()) {
				String idptName = idpt.getNameOfGraph();
				sb.append("\t\t\t\tcase (int)" + entitiesEnumName(which, pathPrefixForElements) + ".@" + idptName + ": return _" + idptName + ";\n");
			}
			break;
		default:
			assert(false);
			break;
		}

		sb.append("\t\t\t\tdefault: return null;\n");
		sb.append("\t\t\t\t}\n");
	    sb.append("\t\t\t}\n");
	    
	    // -----------------------------
	    
		sb.append("\t\t\tpublic "+typeOfMatchedEntities+" get"+matchedEntitiesNameSingular+"(string name)\n");
		sb.append("\t\t\t{\n");
		sb.append("\t\t\t\tswitch(name) {\n");

		switch(which)
		{
		case MATCH_PART_NODES:
			for(Node node : pattern.getNodes()) {
				sb.append("\t\t\t\tcase \"" + formatIdentifiable(node) + "\": return " + formatEntity(node, "_") + ";\n");
			}
			break;
		case MATCH_PART_EDGES:
			for(Edge edge : pattern.getEdges()) {
				sb.append("\t\t\t\tcase \"" + formatIdentifiable(edge) + "\": return " + formatEntity(edge, "_") + ";\n");
			}
			break;
		case MATCH_PART_VARIABLES:
			for(Variable var : pattern.getVars()) {
				sb.append("\t\t\t\tcase \"" + formatIdentifiable(var) + "\": return " + formatEntity(var, "_") + ";\n");
			}
			break;
		case MATCH_PART_EMBEDDED_GRAPHS:
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				sb.append("\t\t\t\tcase \"" + formatIdentifiable(sub) + "\": return " + formatIdentifiable(sub, "_") + ";\n");
			}
			break;
		case MATCH_PART_ALTERNATIVES:
			for(Alternative alt : pattern.getAlts()) {
				String altName = alt.getNameOfGraph();
				sb.append("\t\t\t\tcase \"" + altName + "\": return _" + altName+ ";\n");
			}
			break;
		case MATCH_PART_ITERATEDS:
			for(Rule iter : pattern.getIters()) {
				String iterName = iter.getLeft().getNameOfGraph();
				sb.append("\t\t\t\tcase \"" + iterName + "\": return _" + iterName + ";\n");
			}
			break;
		case MATCH_PART_INDEPENDENTS:
			for(PatternGraph idpt : pattern.getIdpts()) {
				String idptName = idpt.getNameOfGraph();
				sb.append("\t\t\t\tcase \"" + idptName + "\": return _" + idptName + ";\n");
			}
			break;
		default:
			assert(false);
			break;
		}

		sb.append("\t\t\t\tdefault: return null;\n");
		sb.append("\t\t\t\t}\n");
	    sb.append("\t\t\t}\n");
	}


	private void genMatchEnum(StringBuffer sb, PatternGraph pattern,
			String name, int which, String pathPrefixForElements)
	{
		// generate enum mapping entity names to consecutive integers
		sb.append("\t\t\tpublic enum "+entitiesEnumName(which, pathPrefixForElements)+" { ");
		switch(which)
		{
		case MATCH_PART_NODES:
			for(Node node : pattern.getNodes()) {
				sb.append("@"+formatIdentifiable(node)+", ");
			}
			break;
		case MATCH_PART_EDGES:
			for(Edge edge : pattern.getEdges()) {
				sb.append("@"+formatIdentifiable(edge)+", ");
			}
			break;
		case MATCH_PART_VARIABLES:
			for(Variable var : pattern.getVars()) {
				sb.append("@"+formatIdentifiable(var)+", ");
			}
			break;
		case MATCH_PART_EMBEDDED_GRAPHS:
			for(SubpatternUsage sub : pattern.getSubpatternUsages()) {
				sb.append("@"+formatIdentifiable(sub)+", ");
			}
			break;
		case MATCH_PART_ALTERNATIVES:
			for(Alternative alt : pattern.getAlts()) {
				sb.append("@"+alt.getNameOfGraph()+", ");
			}
			break;
		case MATCH_PART_ITERATEDS:
			for(Rule iter : pattern.getIters()) {
				sb.append("@"+iter.getLeft().getNameOfGraph()+", ");
			}
			break;
		case MATCH_PART_INDEPENDENTS:
			for(PatternGraph idpt : pattern.getIdpts()) {
				sb.append("@"+idpt.getNameOfGraph()+", ");
			}
			break;
		default:
			assert(false);
			break;
		}
		sb.append("END_OF_ENUM };\n");
	}


	private String matchedEntitiesNameSingular(int which)
	{
		switch(which)
		{
		case MATCH_PART_NODES:
			return "Node";
		case MATCH_PART_EDGES:
			return "Edge";
		case MATCH_PART_VARIABLES:
			return "Variable";
		case MATCH_PART_EMBEDDED_GRAPHS:
			return "EmbeddedGraph";
		case MATCH_PART_ALTERNATIVES:
			return "Alternative";
		case MATCH_PART_ITERATEDS:
			return "Iterated";
		case MATCH_PART_INDEPENDENTS:
			return "Independent";
		default:
			assert(false);
			return "";
		}
	}

	private String matchedEntitiesNamePlural(int which)
	{
		return matchedEntitiesNameSingular(which)+"s";
	}

	private String entitiesEnumName(int which, String pathPrefixForElements)
	{
		switch(which)
		{
		case MATCH_PART_NODES:
			return pathPrefixForElements + "NodeNums";
		case MATCH_PART_EDGES:
			return pathPrefixForElements + "EdgeNums";
		case MATCH_PART_VARIABLES:
			return pathPrefixForElements + "VariableNums";
		case MATCH_PART_EMBEDDED_GRAPHS:
			return pathPrefixForElements + "SubNums";
		case MATCH_PART_ALTERNATIVES:
			return pathPrefixForElements + "AltNums";
		case MATCH_PART_ITERATEDS:
			return pathPrefixForElements + "IterNums";
		case MATCH_PART_INDEPENDENTS:
			return pathPrefixForElements + "IdptNums";
		default:
			assert(false);
			return "";
		}
	}

	private String typeOfMatchedEntities(int which)
	{
		switch(which)
		{
		case MATCH_PART_NODES:
			return "GRGEN_LIBGR.INode";
		case MATCH_PART_EDGES:
			return "GRGEN_LIBGR.IEdge";
		case MATCH_PART_VARIABLES:
			return "object";
		case MATCH_PART_EMBEDDED_GRAPHS:
			return "GRGEN_LIBGR.IMatch";
		case MATCH_PART_ALTERNATIVES:
			return "GRGEN_LIBGR.IMatch";
		case MATCH_PART_ITERATEDS:
			return "GRGEN_LIBGR.IMatches";
		case MATCH_PART_INDEPENDENTS:
			return "GRGEN_LIBGR.IMatch";
		default:
			assert(false);
			return "";
		}
	}

	private int numOfMatchedEntities(int which, PatternGraph pattern)
	{
		switch(which)
		{
		case MATCH_PART_NODES:
			return pattern.getNodes().size();
		case MATCH_PART_EDGES:
			return pattern.getEdges().size();
		case MATCH_PART_VARIABLES:
			return pattern.getVars().size();
		case MATCH_PART_EMBEDDED_GRAPHS:
			return pattern.getSubpatternUsages().size();
		case MATCH_PART_ALTERNATIVES:
			return pattern.getAlts().size();
		case MATCH_PART_ITERATEDS:
			return pattern.getIters().size();
		case MATCH_PART_INDEPENDENTS:
			return pattern.getIdpts().size();
		default:
			assert(false);
			return 0;
		}
	}

	////////////////////////////////////
	// Yielding assignment generation //
	////////////////////////////////////

	private void genYield(StringBuffer sb, EvalStatement evalStmt, String className,
			String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		if(evalStmt instanceof AssignmentVarIndexed) { // must come before AssignmentVar
			genAssignmentVarIndexed(sb, (AssignmentVarIndexed) evalStmt, 
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof AssignmentVar) {
			genAssignmentVar(sb, (AssignmentVar) evalStmt, 
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof AssignmentGraphEntity) {
			genAssignmentGraphEntity(sb, (AssignmentGraphEntity) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof AssignmentIdentical) {
			//nothing to generate, was assignment . = . optimized away;
		}
		else if(evalStmt instanceof CompoundAssignmentVarChangedVar) {
			genCompoundAssignmentVarChangedVar(sb, (CompoundAssignmentVarChangedVar) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof CompoundAssignmentVar) { // must come after the changed versions
			genCompoundAssignmentVar(sb, (CompoundAssignmentVar) evalStmt, "\t\t\t\t",
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof MapVarRemoveItem) {
			genMapVarRemoveItem(sb, (MapVarRemoveItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		} 
		else if(evalStmt instanceof MapVarClear) {
			genMapVarClear(sb, (MapVarClear) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		} 
		else if(evalStmt instanceof MapVarAddItem) {
			genMapVarAddItem(sb, (MapVarAddItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof SetVarRemoveItem) {
			genSetVarRemoveItem(sb, (SetVarRemoveItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof SetVarClear) {
			genSetVarClear(sb, (SetVarClear) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof SetVarAddItem) {
			genSetVarAddItem(sb, (SetVarAddItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ArrayVarRemoveItem) {
			genArrayVarRemoveItem(sb, (ArrayVarRemoveItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ArrayVarClear) {
			genArrayVarClear(sb, (ArrayVarClear) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ArrayVarAddItem) {
			genArrayVarAddItem(sb, (ArrayVarAddItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof DequeVarRemoveItem) {
			genDequeVarRemoveItem(sb, (DequeVarRemoveItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof DequeVarClear) {
			genDequeVarClear(sb, (DequeVarClear) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof DequeVarAddItem) {
			genDequeVarAddItem(sb, (DequeVarAddItem) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof IteratedAccumulationYield) {
			genIteratedAccumulationYield(sb, (IteratedAccumulationYield) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ContainerAccumulationYield) {
			genContainerAccumulationYield(sb, (ContainerAccumulationYield) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ForLookup) {
			genForLookup(sb, (ForLookup) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ForFunction) {
			genForFunction(sb, (ForFunction) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ConditionStatement) {
			genConditionStatement(sb, (ConditionStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof WhileStatement) {
			genWhileStatement(sb, (WhileStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof DoWhileStatement) {
			genDoWhileStatement(sb, (DoWhileStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof DefDeclVarStatement) {
			genDefDeclVarStatement(sb, (DefDeclVarStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof DefDeclGraphEntityStatement) {
			genDefDeclGraphEntityStatement(sb, (DefDeclGraphEntityStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof BreakStatement) {
			genBreakStatement(sb, (BreakStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof ContinueStatement) {
			genContinueStatement(sb, (ContinueStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof EmitStatement) {
			genEmitStatement(sb, (EmitStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof HighlightStatement) {
			genHighlightStatement(sb, (HighlightStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof RecordStatement) {
			genRecordStatement(sb, (RecordStatement) evalStmt,
					className, pathPrefix, alreadyDefinedEntityToName);
		}
		else if(evalStmt instanceof GraphClear
				|| evalStmt instanceof GraphRemove
				|| evalStmt instanceof VFreeStatement
				|| evalStmt instanceof VFreeNonResetStatement
				|| evalStmt instanceof VResetStatement) {
			throw new UnsupportedOperationException("Visited flag manipulation and graph manipulation not allowed as yield statements; \"" + evalStmt + "\"");
		}
		else {
			throw new UnsupportedOperationException("Unexpected yield statement \"" + evalStmt + "\"");
		}
	}

	private void genAssignmentVar(StringBuffer sb, AssignmentVar ass,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = ass.getTarget();
		Expression expr = ass.getExpression();

		sb.append("\t\t\t\tnew GRGEN_EXPR.YieldAssignment(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", true, ");
		genExpressionTree(sb, expr, className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genAssignmentVarIndexed(StringBuffer sb, AssignmentVarIndexed ass,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = ass.getTarget();
		Expression expr = ass.getExpression();
		Expression index = ass.getIndex();

		sb.append("\t\t\t\tnew GRGEN_EXPR.YieldAssignmentIndexed(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\", ");
		genExpressionTree(sb, expr, className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(", ");
		genExpressionTree(sb, index, className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genAssignmentGraphEntity(StringBuffer sb, AssignmentGraphEntity ass,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		GraphEntity target = ass.getTarget();
		Expression expr = ass.getExpression();

		sb.append("\t\t\t\tnew GRGEN_EXPR.YieldAssignment(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", false, ");
		genExpressionTree(sb, expr, className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genCompoundAssignmentVarChangedVar(StringBuffer sb, CompoundAssignmentVarChangedVar cass,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName)
	{
		String changedOperation;
		if(cass.getChangedOperation()==CompoundAssignment.UNION)
			changedOperation = "GRGEN_EXPR.YieldChangeDisjunctionAssignment";
		else if(cass.getChangedOperation()==CompoundAssignment.INTERSECTION)
			changedOperation = "GRGEN_EXPR.YieldChangeConjunctionAssignment";
		else //if(cass.getChangedOperation()==CompoundAssignment.ASSIGN)
			changedOperation = "GRGEN_EXPR.YieldChangeAssignment";

		Variable changedTarget = cass.getChangedTarget();	
		sb.append("\t\t\t\tnew " + changedOperation + "(");
		sb.append("\"" + formatEntity(changedTarget, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		genCompoundAssignmentVar(sb, cass, "", className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genCompoundAssignmentVar(StringBuffer sb, CompoundAssignmentVar cass, String prefix,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName)
	{
		Variable target = cass.getTarget();
		assert(target.getType() instanceof MapType || target.getType() instanceof SetType);
		Expression expr = cass.getExpression();

		sb.append(prefix + "new GRGEN_EXPR.");
		if(cass.getOperation()==CompoundAssignment.UNION)
			sb.append("SetMapUnion(");
		else if(cass.getOperation()==CompoundAssignment.INTERSECTION)
			sb.append("SetMapIntersect(");
		else //if(cass.getOperation()==CompoundAssignment.WITHOUT)
			sb.append("SetMapExcept(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		genExpressionTree(sb, expr, className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genMapVarRemoveItem(StringBuffer sb, MapVarRemoveItem mvri,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = mvri.getTarget();

		StringBuffer sbtmp = new StringBuffer();
		genExpressionTree(sbtmp, mvri.getKeyExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String keyExprStr = sbtmp.toString();

		sb.append("\t\t\t\tnew GRGEN_EXPR.SetMapRemove(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		sb.append(keyExprStr);
		sb.append(")");
		
		assert mvri.getNext()==null;
	}

	private void genMapVarClear(StringBuffer sb, MapVarClear mvc,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = mvc.getTarget();

		sb.append("\t\t\t\tnew GRGEN_EXPR.Clear(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(")");
		
		assert mvc.getNext()==null;
	}

	private void genMapVarAddItem(StringBuffer sb, MapVarAddItem mvai,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = mvai.getTarget();

		StringBuffer sbtmp = new StringBuffer();
		genExpressionTree(sbtmp, mvai.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String valueExprStr = sbtmp.toString();
		sbtmp.delete(0, sbtmp.length());
		genExpressionTree(sbtmp, mvai.getKeyExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String keyExprStr = sbtmp.toString();

		sb.append("\t\t\t\tnew GRGEN_EXPR.MapAdd(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		sb.append(keyExprStr);
		sb.append(", ");
		sb.append(valueExprStr);
		sb.append(")");

		assert mvai.getNext()==null;
	}

	private void genSetVarRemoveItem(StringBuffer sb, SetVarRemoveItem svri,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = svri.getTarget();

		StringBuffer sbtmp = new StringBuffer();
		genExpressionTree(sbtmp, svri.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String valueExprStr = sbtmp.toString();

		sb.append("\t\t\t\tnew GRGEN_EXPR.SetMapRemove(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		sb.append(valueExprStr);
		sb.append(")");
		
		assert svri.getNext()==null;
	}

	private void genSetVarClear(StringBuffer sb, SetVarClear svc,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = svc.getTarget();

		sb.append("\t\t\t\tnew GRGEN_EXPR.Clear(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(")");
		
		assert svc.getNext()==null;
	}

	private void genSetVarAddItem(StringBuffer sb, SetVarAddItem svai,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = svai.getTarget();

		StringBuffer sbtmp = new StringBuffer();
		genExpressionTree(sbtmp, svai.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String valueExprStr = sbtmp.toString();

		sb.append("\t\t\t\tnew GRGEN_EXPR.SetAdd(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		sb.append(valueExprStr);
		sb.append(")");
		
		assert svai.getNext()==null;
	}

	private void genArrayVarRemoveItem(StringBuffer sb, ArrayVarRemoveItem avri,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = avri.getTarget();

		sb.append("\t\t\t\tnew GRGEN_EXPR.ArrayRemove(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		if(avri.getIndexExpr()!=null) {
			sb.append(", ");
			StringBuffer sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, avri.getIndexExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			String indexExprStr = sbtmp.toString();
			sb.append(indexExprStr);
		}
		sb.append(")");
		
		assert avri.getNext()==null;
	}

	private void genArrayVarClear(StringBuffer sb, ArrayVarClear avc,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = avc.getTarget();

		sb.append("\t\t\t\tnew GRGEN_EXPR.Clear(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(")");
		
		assert avc.getNext()==null;
	}

	private void genArrayVarAddItem(StringBuffer sb, ArrayVarAddItem avai,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = avai.getTarget();

		StringBuffer sbtmp = new StringBuffer();
		genExpressionTree(sbtmp, avai.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String valueExprStr = sbtmp.toString();

		sb.append("\t\t\t\tnew GRGEN_EXPR.ArrayAdd(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		sb.append(valueExprStr);
		if(avai.getIndexExpr()!=null) {
			sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, avai.getIndexExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			String indexExprStr = sbtmp.toString();
			sb.append(", ");
			sb.append(indexExprStr);
		}
		sb.append(")");
		
		assert avai.getNext()==null;
	}

	private void genDequeVarRemoveItem(StringBuffer sb, DequeVarRemoveItem dvri,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = dvri.getTarget();

		sb.append("\t\t\t\tnew GRGEN_EXPR.DequeRemove(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		if(dvri.getIndexExpr()!=null) {
			sb.append(", ");
			StringBuffer sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, dvri.getIndexExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			String indexExprStr = sbtmp.toString();
			sb.append(indexExprStr);
		}
		sb.append(")");
		
		assert dvri.getNext()==null;
	}

	private void genDequeVarClear(StringBuffer sb, DequeVarClear dvc,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = dvc.getTarget();

		sb.append("\t\t\t\tnew GRGEN_EXPR.Clear(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(")");
		
		assert dvc.getNext()==null;
	}

	private void genDequeVarAddItem(StringBuffer sb, DequeVarAddItem dvai,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable target = dvai.getTarget();

		StringBuffer sbtmp = new StringBuffer();
		genExpressionTree(sbtmp, dvai.getValueExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		String valueExprStr = sbtmp.toString();

		sb.append("\t\t\t\tnew GRGEN_EXPR.DequeAdd(");
		sb.append("\"" + formatEntity(target, pathPrefix, alreadyDefinedEntityToName) + "\"");
		sb.append(", ");
		sb.append(valueExprStr);
		if(dvai.getIndexExpr()!=null) {
			sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, dvai.getIndexExpr(), className, pathPrefix, alreadyDefinedEntityToName);
			String indexExprStr = sbtmp.toString();
			sb.append(", ");
			sb.append(indexExprStr);
		}
		sb.append(")");
		
		assert dvai.getNext()==null;
	}

	private void genIteratedAccumulationYield(StringBuffer sb, IteratedAccumulationYield iay,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable iterationVar = iay.getIterationVar();
		Rule iterated = iay.getIterated();

		sb.append("\t\t\t\tnew GRGEN_EXPR.IteratedAccumulationYield(");
		sb.append("\"" + formatEntity(iterationVar, pathPrefix, alreadyDefinedEntityToName) + "\", ");
		sb.append("\"" + formatIdentifiable(iterationVar) + "\", ");
		sb.append("\"" + formatIdentifiable(iterated) + "\", ");
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : iay.getAccumulationStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}");
		sb.append(")");
	}

	private void genContainerAccumulationYield(StringBuffer sb, ContainerAccumulationYield cay,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable iterationVar = cay.getIterationVar();
		Variable indexVar = cay.getIndexVar();
		Variable container = cay.getContainer();

		sb.append("\t\t\t\tnew GRGEN_EXPR.ContainerAccumulationYield(");
		sb.append("\"" + formatEntity(iterationVar, pathPrefix, alreadyDefinedEntityToName) + "\", ");
		sb.append("\"" + formatIdentifiable(iterationVar) + "\", ");
		Type valueType;
		Type indexType = null;
		if(container.getType() instanceof SetType) {
			valueType = ((SetType)container.getType()).getValueType();
		} else if(container.getType() instanceof MapType) {
			valueType = ((MapType)container.getType()).getValueType();
			indexType = ((MapType)container.getType()).getKeyType();
		} else if(container.getType() instanceof ArrayType) {
			valueType = ((ArrayType)container.getType()).getValueType();
			indexType = IntType.getType();
		} else {
			valueType = ((DequeType)container.getType()).getValueType();
			indexType = IntType.getType();
		}
		sb.append("\"" + formatType(valueType) + "\", ");
		if(indexVar!=null)
		{
			sb.append("\"" + formatEntity(indexVar, pathPrefix, alreadyDefinedEntityToName) + "\", ");
			sb.append("\"" + formatIdentifiable(indexVar) + "\", ");
			sb.append("\"" + formatType(indexType) + "\", ");
		}
		sb.append("\"" + formatEntity(container, pathPrefix, alreadyDefinedEntityToName) + "\", ");
		sb.append("\"" + formatIdentifiable(container) + "\", ");
		sb.append("\"" + formatType(container.getType()) + "\", ");
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : cay.getAccumulationStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}");
		sb.append(")");
	}

	private void genForLookup(StringBuffer sb, ForLookup fl,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable iterationVar = fl.getIterationVar();
		Type iterationVarType = iterationVar.getType();

		sb.append("\t\t\t\tnew GRGEN_EXPR.ForLookup(");
		sb.append("\"" + formatEntity(iterationVar, pathPrefix, alreadyDefinedEntityToName) + "\", ");
		sb.append("\"" + formatIdentifiable(iterationVar) + "\", ");
		sb.append("\"" + formatElementClassRef(iterationVarType) + "\", ");
		sb.append(iterationVarType instanceof NodeType ? "true, " : "false, ");
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : fl.getLoopedStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}");
		sb.append(")");
	}

	private void genForFunction(StringBuffer sb, ForFunction ff,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable iterationVar = ff.getIterationVar();
		Type iterationVarType = iterationVar.getType();

		sb.append("\t\t\t\tnew GRGEN_EXPR.ForFunction(");
		sb.append("\"" + formatEntity(iterationVar, pathPrefix, alreadyDefinedEntityToName) + "\", ");
		sb.append("\"" + formatIdentifiable(iterationVar) + "\", ");
		sb.append("\"" + formatElementClassRef(iterationVarType) + "\", ");
		if(ff.getFunction() instanceof AdjacentNodeExpr) {
			AdjacentNodeExpr adjacentExpr = (AdjacentNodeExpr)ff.getFunction();
			StringBuffer sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, adjacentExpr, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(sbtmp.toString() + ", ");
		} else if(ff.getFunction() instanceof IncidentEdgeExpr) {
			IncidentEdgeExpr incidentExpr = (IncidentEdgeExpr)ff.getFunction();
			StringBuffer sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, incidentExpr, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(sbtmp.toString() + ", ");
		} else if(ff.getFunction() instanceof ReachableNodeExpr) {
			ReachableNodeExpr reachableExpr = (ReachableNodeExpr)ff.getFunction();
			StringBuffer sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, reachableExpr, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(sbtmp.toString() + ", ");
		} else if(ff.getFunction() instanceof ReachableEdgeExpr) {
			ReachableEdgeExpr reachableExpr = (ReachableEdgeExpr)ff.getFunction();
			StringBuffer sbtmp = new StringBuffer();
			genExpressionTree(sbtmp, reachableExpr, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(sbtmp.toString() + ", ");
		}
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : ff.getLoopedStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}");
		sb.append(")");
	}

	private void genConditionStatement(StringBuffer sb, ConditionStatement cs,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.ConditionStatement(");
		genExpressionTree(sb, cs.getConditionExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(",");
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : cs.getTrueCaseStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}, ");
		if(cs.getFalseCaseStatements()!=null) {
			sb.append("new GRGEN_EXPR.Yielding[] { ");
			for(EvalStatement statement : cs.getFalseCaseStatements()) {
				genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
				sb.append(", ");
			}
			sb.append("}");
		} else {
			sb.append("null");
		}
		sb.append(")");
	}

	private void genWhileStatement(StringBuffer sb, WhileStatement ws,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.WhileStatement(");
		genExpressionTree(sb, ws.getConditionExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(",");
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : ws.getLoopedStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}");
		sb.append(")");
	}

	private void genDoWhileStatement(StringBuffer sb, DoWhileStatement dws,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.DoWhileStatement(");
		sb.append("new GRGEN_EXPR.Yielding[] { ");
		for(EvalStatement statement : dws.getLoopedStatements()) {
			genYield(sb, statement, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}");
		sb.append(",");
		genExpressionTree(sb, dws.getConditionExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genDefDeclVarStatement(StringBuffer sb, DefDeclVarStatement ddvs,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		Variable var = ddvs.getTarget();
		sb.append("\t\t\t\tnew GRGEN_EXPR.DefDeclaration(");
		sb.append("\"" + formatEntity(var, pathPrefix, alreadyDefinedEntityToName) + "\",");
		sb.append("\"" + formatType(var.getType()) + "\",");
		if(var.initialization!=null) {
			genExpressionTree(sb, var.initialization, className, pathPrefix, alreadyDefinedEntityToName);		
		} else {
			sb.append("null");
		}
		sb.append(")");
	}

	private void genDefDeclGraphEntityStatement(StringBuffer sb, DefDeclGraphEntityStatement ddges,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		GraphEntity graphEntity = ddges.getTarget();
		sb.append("\t\t\t\tnew GRGEN_EXPR.DefDeclaration(");
		sb.append("\"" + formatEntity(graphEntity, pathPrefix, alreadyDefinedEntityToName) + "\",");
		sb.append("\"" + formatType(graphEntity.getType()) + "\",");
		if(graphEntity.initialization!=null) {
			genExpressionTree(sb, graphEntity.initialization, className, pathPrefix, alreadyDefinedEntityToName);		
		} else {
			sb.append("null");
		}
		sb.append(")");
	}

	private void genBreakStatement(StringBuffer sb, BreakStatement bs,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.BreakStatement()");
	}

	private void genContinueStatement(StringBuffer sb, ContinueStatement cs,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.ContinueStatement()");
	}

	private void genEmitStatement(StringBuffer sb, EmitStatement es,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.EmitStatement(");
		genExpressionTree(sb, es.getToEmitExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	private void genHighlightStatement(StringBuffer sb, HighlightStatement hs,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.HighlightStatement(");
		sb.append("new GRGEN_EXPR.Expression[] { ");
		for(Expression expr : hs.getToHighlightExpressions()) {
			genExpressionTree(sb, expr, className, pathPrefix, alreadyDefinedEntityToName);
			sb.append(", ");
		}
		sb.append("}, ");
		sb.append("new string[] { ");
		for(Expression expr : hs.getToHighlightExpressions()) {
			if(expr instanceof VariableExpression) {
				sb.append("\"");
				formatIdentifiable(((VariableExpression)expr).getVariable());
				sb.append("\", ");
			} else {
				sb.append("null, ");
			}
		}
		sb.append("}");
		sb.append(")");
	}

	private void genRecordStatement(StringBuffer sb, RecordStatement rs,
			String className, String pathPrefix, HashMap<Entity, String> alreadyDefinedEntityToName) {
		sb.append("\t\t\t\tnew GRGEN_EXPR.RecordStatement(");
		genExpressionTree(sb, rs.getToRecordExpr(), className, pathPrefix, alreadyDefinedEntityToName);
		sb.append(")");
	}

	///////////////////////
	// Private variables //
	///////////////////////

	private SearchPlanBackend2 be;
	private ModifyGen mg;
	private ModifyGen modifyGenForComputations;
	private Model model;
}

