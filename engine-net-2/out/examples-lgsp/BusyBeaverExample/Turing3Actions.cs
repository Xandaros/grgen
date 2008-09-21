// This file has been generated automatically by GrGen.
// Do not modify this file! Any changes will be lost!
// Generated from "..\..\examples\Turing3\Turing3.grg" on Sun Sep 21 16:42:08 GMT+01:00 2008

using System;
using System.Collections.Generic;
using System.Text;
using GRGEN_LIBGR = de.unika.ipd.grGen.libGr;
using GRGEN_LGSP = de.unika.ipd.grGen.lgsp;
using GRGEN_EXPR = de.unika.ipd.grGen.expression;
using de.unika.ipd.grGen.Model_Turing3;

namespace de.unika.ipd.grGen.Action_Turing3
{
	public class Rule_readZeroRule : GRGEN_LGSP.LGSPRulePattern
	{
		private static Rule_readZeroRule instance = null;
		public static Rule_readZeroRule Instance { get { if (instance==null) { instance = new Rule_readZeroRule(); instance.initialize(); } return instance; } }

		private static object[] ReturnArray = new object[1];

		public static GRGEN_LIBGR.NodeType[] readZeroRule_node_s_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] readZeroRule_node_wv_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] readZeroRule_node_bp_AllowedTypes = null;
		public static bool[] readZeroRule_node_s_IsAllowedType = null;
		public static bool[] readZeroRule_node_wv_IsAllowedType = null;
		public static bool[] readZeroRule_node_bp_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] readZeroRule_edge_rv_AllowedTypes = null;
		public static bool[] readZeroRule_edge_rv_IsAllowedType = null;
		public enum readZeroRule_NodeNums { @s, @wv, @bp, };
		public enum readZeroRule_EdgeNums { @rv, };
		public enum readZeroRule_VariableNums { };
		public enum readZeroRule_SubNums { };
		public enum readZeroRule_AltNums { };
		GRGEN_LGSP.PatternGraph pat_readZeroRule;


#if INITIAL_WARMUP
		public Rule_readZeroRule()
#else
		private Rule_readZeroRule()
#endif
		{
			name = "readZeroRule";

			inputs = new GRGEN_LIBGR.GrGenType[] { NodeType_State.typeVar, NodeType_BandPosition.typeVar, };
			inputNames = new string[] { "readZeroRule_node_s", "readZeroRule_node_bp", };
			outputs = new GRGEN_LIBGR.GrGenType[] { NodeType_WriteValue.typeVar, };
		}
		public override void initialize()
		{
			bool[,] readZeroRule_isNodeHomomorphicGlobal = new bool[3, 3] {
				{ false, false, false, },
				{ false, false, false, },
				{ false, false, false, },
			};
			bool[,] readZeroRule_isEdgeHomomorphicGlobal = new bool[1, 1] {
				{ false, },
			};
			GRGEN_LGSP.PatternNode readZeroRule_node_s = new GRGEN_LGSP.PatternNode((int) NodeTypes.@State, "readZeroRule_node_s", "s", readZeroRule_node_s_AllowedTypes, readZeroRule_node_s_IsAllowedType, 5.5F, 0);
			GRGEN_LGSP.PatternNode readZeroRule_node_wv = new GRGEN_LGSP.PatternNode((int) NodeTypes.@WriteValue, "readZeroRule_node_wv", "wv", readZeroRule_node_wv_AllowedTypes, readZeroRule_node_wv_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode readZeroRule_node_bp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "readZeroRule_node_bp", "bp", readZeroRule_node_bp_AllowedTypes, readZeroRule_node_bp_IsAllowedType, 5.5F, 1);
			GRGEN_LGSP.PatternEdge readZeroRule_edge_rv = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@readZero, "readZeroRule_edge_rv", "rv", readZeroRule_edge_rv_AllowedTypes, readZeroRule_edge_rv_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternCondition cond_0 = new GRGEN_LGSP.PatternCondition(
				new GRGEN_EXPR.EQ(new GRGEN_EXPR.Qualification("IBandPosition", "readZeroRule_node_bp", "value"), new GRGEN_EXPR.Constant("0")),
				new String[] { "readZeroRule_node_bp" }, new String[] {  }, new String[] {  });
			pat_readZeroRule = new GRGEN_LGSP.PatternGraph(
				"readZeroRule",
				"",
				false,
				new GRGEN_LGSP.PatternNode[] { readZeroRule_node_s, readZeroRule_node_wv, readZeroRule_node_bp }, 
				new GRGEN_LGSP.PatternEdge[] { readZeroRule_edge_rv }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] {  }, 
				new GRGEN_LGSP.PatternCondition[] { cond_0,  }, 
				new bool[3, 3] {
					{ true, false, false, },
					{ false, true, false, },
					{ false, false, true, },
				},
				new bool[1, 1] {
					{ true, },
				},
				readZeroRule_isNodeHomomorphicGlobal,
				readZeroRule_isEdgeHomomorphicGlobal
			);
			pat_readZeroRule.edgeToSourceNode.Add(readZeroRule_edge_rv, readZeroRule_node_s);
			pat_readZeroRule.edgeToTargetNode.Add(readZeroRule_edge_rv, readZeroRule_node_wv);

			readZeroRule_node_s.PointOfDefinition = null;
			readZeroRule_node_wv.PointOfDefinition = pat_readZeroRule;
			readZeroRule_node_bp.PointOfDefinition = null;
			readZeroRule_edge_rv.PointOfDefinition = pat_readZeroRule;

			patternGraph = pat_readZeroRule;
		}


		public override object[] Modify(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_wv = curMatch.Nodes[(int)readZeroRule_NodeNums.@wv];
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)readZeroRule_NodeNums.@bp];
			@IBandPosition inode_bp = (@IBandPosition) node_bp;
			@IWriteValue inode_wv = (@IWriteValue) node_wv;
			graph.SettingAddedNodeNames( readZeroRule_addedNodeNames );
			graph.SettingAddedEdgeNames( readZeroRule_addedEdgeNames );
			int tempvar_i = inode_wv.@value;
			graph.ChangingNodeAttribute(node_bp, NodeType_BandPosition.AttributeType_value, inode_bp.@value, tempvar_i);
			inode_bp.@value = tempvar_i;
			ReturnArray[0] = node_wv;
			return ReturnArray;
		}
		private static String[] readZeroRule_addedNodeNames = new String[] {  };
		private static String[] readZeroRule_addedEdgeNames = new String[] {  };

		public override object[] ModifyNoReuse(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_wv = curMatch.Nodes[(int)readZeroRule_NodeNums.@wv];
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)readZeroRule_NodeNums.@bp];
			@IBandPosition inode_bp = (@IBandPosition) node_bp;
			@IWriteValue inode_wv = (@IWriteValue) node_wv;
			graph.SettingAddedNodeNames( readZeroRule_addedNodeNames );
			graph.SettingAddedEdgeNames( readZeroRule_addedEdgeNames );
			int tempvar_i = inode_wv.@value;
			graph.ChangingNodeAttribute(node_bp, NodeType_BandPosition.AttributeType_value, inode_bp.@value, tempvar_i);
			inode_bp.@value = tempvar_i;
			ReturnArray[0] = node_wv;
			return ReturnArray;
		}
	}

	public class Rule_readOneRule : GRGEN_LGSP.LGSPRulePattern
	{
		private static Rule_readOneRule instance = null;
		public static Rule_readOneRule Instance { get { if (instance==null) { instance = new Rule_readOneRule(); instance.initialize(); } return instance; } }

		private static object[] ReturnArray = new object[1];

		public static GRGEN_LIBGR.NodeType[] readOneRule_node_s_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] readOneRule_node_wv_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] readOneRule_node_bp_AllowedTypes = null;
		public static bool[] readOneRule_node_s_IsAllowedType = null;
		public static bool[] readOneRule_node_wv_IsAllowedType = null;
		public static bool[] readOneRule_node_bp_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] readOneRule_edge_rv_AllowedTypes = null;
		public static bool[] readOneRule_edge_rv_IsAllowedType = null;
		public enum readOneRule_NodeNums { @s, @wv, @bp, };
		public enum readOneRule_EdgeNums { @rv, };
		public enum readOneRule_VariableNums { };
		public enum readOneRule_SubNums { };
		public enum readOneRule_AltNums { };
		GRGEN_LGSP.PatternGraph pat_readOneRule;


#if INITIAL_WARMUP
		public Rule_readOneRule()
#else
		private Rule_readOneRule()
#endif
		{
			name = "readOneRule";

			inputs = new GRGEN_LIBGR.GrGenType[] { NodeType_State.typeVar, NodeType_BandPosition.typeVar, };
			inputNames = new string[] { "readOneRule_node_s", "readOneRule_node_bp", };
			outputs = new GRGEN_LIBGR.GrGenType[] { NodeType_WriteValue.typeVar, };
		}
		public override void initialize()
		{
			bool[,] readOneRule_isNodeHomomorphicGlobal = new bool[3, 3] {
				{ false, false, false, },
				{ false, false, false, },
				{ false, false, false, },
			};
			bool[,] readOneRule_isEdgeHomomorphicGlobal = new bool[1, 1] {
				{ false, },
			};
			GRGEN_LGSP.PatternNode readOneRule_node_s = new GRGEN_LGSP.PatternNode((int) NodeTypes.@State, "readOneRule_node_s", "s", readOneRule_node_s_AllowedTypes, readOneRule_node_s_IsAllowedType, 5.5F, 0);
			GRGEN_LGSP.PatternNode readOneRule_node_wv = new GRGEN_LGSP.PatternNode((int) NodeTypes.@WriteValue, "readOneRule_node_wv", "wv", readOneRule_node_wv_AllowedTypes, readOneRule_node_wv_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode readOneRule_node_bp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "readOneRule_node_bp", "bp", readOneRule_node_bp_AllowedTypes, readOneRule_node_bp_IsAllowedType, 5.5F, 1);
			GRGEN_LGSP.PatternEdge readOneRule_edge_rv = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@readOne, "readOneRule_edge_rv", "rv", readOneRule_edge_rv_AllowedTypes, readOneRule_edge_rv_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternCondition cond_0 = new GRGEN_LGSP.PatternCondition(
				new GRGEN_EXPR.EQ(new GRGEN_EXPR.Qualification("IBandPosition", "readOneRule_node_bp", "value"), new GRGEN_EXPR.Constant("1")),
				new String[] { "readOneRule_node_bp" }, new String[] {  }, new String[] {  });
			pat_readOneRule = new GRGEN_LGSP.PatternGraph(
				"readOneRule",
				"",
				false,
				new GRGEN_LGSP.PatternNode[] { readOneRule_node_s, readOneRule_node_wv, readOneRule_node_bp }, 
				new GRGEN_LGSP.PatternEdge[] { readOneRule_edge_rv }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] {  }, 
				new GRGEN_LGSP.PatternCondition[] { cond_0,  }, 
				new bool[3, 3] {
					{ true, false, false, },
					{ false, true, false, },
					{ false, false, true, },
				},
				new bool[1, 1] {
					{ true, },
				},
				readOneRule_isNodeHomomorphicGlobal,
				readOneRule_isEdgeHomomorphicGlobal
			);
			pat_readOneRule.edgeToSourceNode.Add(readOneRule_edge_rv, readOneRule_node_s);
			pat_readOneRule.edgeToTargetNode.Add(readOneRule_edge_rv, readOneRule_node_wv);

			readOneRule_node_s.PointOfDefinition = null;
			readOneRule_node_wv.PointOfDefinition = pat_readOneRule;
			readOneRule_node_bp.PointOfDefinition = null;
			readOneRule_edge_rv.PointOfDefinition = pat_readOneRule;

			patternGraph = pat_readOneRule;
		}


		public override object[] Modify(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_wv = curMatch.Nodes[(int)readOneRule_NodeNums.@wv];
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)readOneRule_NodeNums.@bp];
			@IBandPosition inode_bp = (@IBandPosition) node_bp;
			@IWriteValue inode_wv = (@IWriteValue) node_wv;
			graph.SettingAddedNodeNames( readOneRule_addedNodeNames );
			graph.SettingAddedEdgeNames( readOneRule_addedEdgeNames );
			int tempvar_i = inode_wv.@value;
			graph.ChangingNodeAttribute(node_bp, NodeType_BandPosition.AttributeType_value, inode_bp.@value, tempvar_i);
			inode_bp.@value = tempvar_i;
			ReturnArray[0] = node_wv;
			return ReturnArray;
		}
		private static String[] readOneRule_addedNodeNames = new String[] {  };
		private static String[] readOneRule_addedEdgeNames = new String[] {  };

		public override object[] ModifyNoReuse(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_wv = curMatch.Nodes[(int)readOneRule_NodeNums.@wv];
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)readOneRule_NodeNums.@bp];
			@IBandPosition inode_bp = (@IBandPosition) node_bp;
			@IWriteValue inode_wv = (@IWriteValue) node_wv;
			graph.SettingAddedNodeNames( readOneRule_addedNodeNames );
			graph.SettingAddedEdgeNames( readOneRule_addedEdgeNames );
			int tempvar_i = inode_wv.@value;
			graph.ChangingNodeAttribute(node_bp, NodeType_BandPosition.AttributeType_value, inode_bp.@value, tempvar_i);
			inode_bp.@value = tempvar_i;
			ReturnArray[0] = node_wv;
			return ReturnArray;
		}
	}

	public class Rule_ensureMoveLeftValidRule : GRGEN_LGSP.LGSPRulePattern
	{
		private static Rule_ensureMoveLeftValidRule instance = null;
		public static Rule_ensureMoveLeftValidRule Instance { get { if (instance==null) { instance = new Rule_ensureMoveLeftValidRule(); instance.initialize(); } return instance; } }

		private static object[] ReturnArray = new object[0];

		public static GRGEN_LIBGR.NodeType[] ensureMoveLeftValidRule_node_wv_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] ensureMoveLeftValidRule_node__node0_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] ensureMoveLeftValidRule_node_bp_AllowedTypes = null;
		public static bool[] ensureMoveLeftValidRule_node_wv_IsAllowedType = null;
		public static bool[] ensureMoveLeftValidRule_node__node0_IsAllowedType = null;
		public static bool[] ensureMoveLeftValidRule_node_bp_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] ensureMoveLeftValidRule_edge__edge0_AllowedTypes = null;
		public static bool[] ensureMoveLeftValidRule_edge__edge0_IsAllowedType = null;
		public enum ensureMoveLeftValidRule_NodeNums { @wv, @_node0, @bp, };
		public enum ensureMoveLeftValidRule_EdgeNums { @_edge0, };
		public enum ensureMoveLeftValidRule_VariableNums { };
		public enum ensureMoveLeftValidRule_SubNums { };
		public enum ensureMoveLeftValidRule_AltNums { };
		GRGEN_LGSP.PatternGraph pat_ensureMoveLeftValidRule;

		public static GRGEN_LIBGR.NodeType[] ensureMoveLeftValidRule_neg_0_node__node0_AllowedTypes = null;
		public static bool[] ensureMoveLeftValidRule_neg_0_node__node0_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] ensureMoveLeftValidRule_neg_0_edge__edge0_AllowedTypes = null;
		public static bool[] ensureMoveLeftValidRule_neg_0_edge__edge0_IsAllowedType = null;
		public enum ensureMoveLeftValidRule_neg_0_NodeNums { @_node0, @bp, };
		public enum ensureMoveLeftValidRule_neg_0_EdgeNums { @_edge0, };
		public enum ensureMoveLeftValidRule_neg_0_VariableNums { };
		public enum ensureMoveLeftValidRule_neg_0_SubNums { };
		public enum ensureMoveLeftValidRule_neg_0_AltNums { };
		GRGEN_LGSP.PatternGraph ensureMoveLeftValidRule_neg_0;


#if INITIAL_WARMUP
		public Rule_ensureMoveLeftValidRule()
#else
		private Rule_ensureMoveLeftValidRule()
#endif
		{
			name = "ensureMoveLeftValidRule";

			inputs = new GRGEN_LIBGR.GrGenType[] { NodeType_WriteValue.typeVar, NodeType_BandPosition.typeVar, };
			inputNames = new string[] { "ensureMoveLeftValidRule_node_wv", "ensureMoveLeftValidRule_node_bp", };
			outputs = new GRGEN_LIBGR.GrGenType[] { };
		}
		public override void initialize()
		{
			bool[,] ensureMoveLeftValidRule_isNodeHomomorphicGlobal = new bool[3, 3] {
				{ false, false, false, },
				{ false, false, false, },
				{ false, false, false, },
			};
			bool[,] ensureMoveLeftValidRule_isEdgeHomomorphicGlobal = new bool[1, 1] {
				{ false, },
			};
			GRGEN_LGSP.PatternNode ensureMoveLeftValidRule_node_wv = new GRGEN_LGSP.PatternNode((int) NodeTypes.@WriteValue, "ensureMoveLeftValidRule_node_wv", "wv", ensureMoveLeftValidRule_node_wv_AllowedTypes, ensureMoveLeftValidRule_node_wv_IsAllowedType, 5.5F, 0);
			GRGEN_LGSP.PatternNode ensureMoveLeftValidRule_node__node0 = new GRGEN_LGSP.PatternNode((int) NodeTypes.@State, "ensureMoveLeftValidRule_node__node0", "_node0", ensureMoveLeftValidRule_node__node0_AllowedTypes, ensureMoveLeftValidRule_node__node0_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode ensureMoveLeftValidRule_node_bp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "ensureMoveLeftValidRule_node_bp", "bp", ensureMoveLeftValidRule_node_bp_AllowedTypes, ensureMoveLeftValidRule_node_bp_IsAllowedType, 5.5F, 1);
			GRGEN_LGSP.PatternEdge ensureMoveLeftValidRule_edge__edge0 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@moveLeft, "ensureMoveLeftValidRule_edge__edge0", "_edge0", ensureMoveLeftValidRule_edge__edge0_AllowedTypes, ensureMoveLeftValidRule_edge__edge0_IsAllowedType, 5.5F, -1);
			bool[,] ensureMoveLeftValidRule_neg_0_isNodeHomomorphicGlobal = new bool[2, 2] {
				{ false, false, },
				{ false, false, },
			};
			bool[,] ensureMoveLeftValidRule_neg_0_isEdgeHomomorphicGlobal = new bool[1, 1] {
				{ false, },
			};
			GRGEN_LGSP.PatternNode ensureMoveLeftValidRule_neg_0_node__node0 = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "ensureMoveLeftValidRule_neg_0_node__node0", "_node0", ensureMoveLeftValidRule_neg_0_node__node0_AllowedTypes, ensureMoveLeftValidRule_neg_0_node__node0_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternEdge ensureMoveLeftValidRule_neg_0_edge__edge0 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@right, "ensureMoveLeftValidRule_neg_0_edge__edge0", "_edge0", ensureMoveLeftValidRule_neg_0_edge__edge0_AllowedTypes, ensureMoveLeftValidRule_neg_0_edge__edge0_IsAllowedType, 5.5F, -1);
			ensureMoveLeftValidRule_neg_0 = new GRGEN_LGSP.PatternGraph(
				"neg_0",
				"ensureMoveLeftValidRule_",
				false,
				new GRGEN_LGSP.PatternNode[] { ensureMoveLeftValidRule_neg_0_node__node0, ensureMoveLeftValidRule_node_bp }, 
				new GRGEN_LGSP.PatternEdge[] { ensureMoveLeftValidRule_neg_0_edge__edge0 }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] {  }, 
				new GRGEN_LGSP.PatternCondition[] {  }, 
				new bool[2, 2] {
					{ true, false, },
					{ false, true, },
				},
				new bool[1, 1] {
					{ true, },
				},
				ensureMoveLeftValidRule_neg_0_isNodeHomomorphicGlobal,
				ensureMoveLeftValidRule_neg_0_isEdgeHomomorphicGlobal
			);
			ensureMoveLeftValidRule_neg_0.edgeToSourceNode.Add(ensureMoveLeftValidRule_neg_0_edge__edge0, ensureMoveLeftValidRule_neg_0_node__node0);
			ensureMoveLeftValidRule_neg_0.edgeToTargetNode.Add(ensureMoveLeftValidRule_neg_0_edge__edge0, ensureMoveLeftValidRule_node_bp);

			pat_ensureMoveLeftValidRule = new GRGEN_LGSP.PatternGraph(
				"ensureMoveLeftValidRule",
				"",
				false,
				new GRGEN_LGSP.PatternNode[] { ensureMoveLeftValidRule_node_wv, ensureMoveLeftValidRule_node__node0, ensureMoveLeftValidRule_node_bp }, 
				new GRGEN_LGSP.PatternEdge[] { ensureMoveLeftValidRule_edge__edge0 }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] { ensureMoveLeftValidRule_neg_0,  }, 
				new GRGEN_LGSP.PatternCondition[] {  }, 
				new bool[3, 3] {
					{ true, false, false, },
					{ false, true, false, },
					{ false, false, true, },
				},
				new bool[1, 1] {
					{ true, },
				},
				ensureMoveLeftValidRule_isNodeHomomorphicGlobal,
				ensureMoveLeftValidRule_isEdgeHomomorphicGlobal
			);
			pat_ensureMoveLeftValidRule.edgeToSourceNode.Add(ensureMoveLeftValidRule_edge__edge0, ensureMoveLeftValidRule_node_wv);
			pat_ensureMoveLeftValidRule.edgeToTargetNode.Add(ensureMoveLeftValidRule_edge__edge0, ensureMoveLeftValidRule_node__node0);
			ensureMoveLeftValidRule_neg_0.embeddingGraph = pat_ensureMoveLeftValidRule;

			ensureMoveLeftValidRule_node_wv.PointOfDefinition = null;
			ensureMoveLeftValidRule_node__node0.PointOfDefinition = pat_ensureMoveLeftValidRule;
			ensureMoveLeftValidRule_node_bp.PointOfDefinition = null;
			ensureMoveLeftValidRule_edge__edge0.PointOfDefinition = pat_ensureMoveLeftValidRule;
			ensureMoveLeftValidRule_neg_0_node__node0.PointOfDefinition = ensureMoveLeftValidRule_neg_0;
			ensureMoveLeftValidRule_neg_0_edge__edge0.PointOfDefinition = ensureMoveLeftValidRule_neg_0;

			patternGraph = pat_ensureMoveLeftValidRule;
		}


		public override object[] Modify(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)ensureMoveLeftValidRule_NodeNums.@bp];
			graph.SettingAddedNodeNames( ensureMoveLeftValidRule_addedNodeNames );
			@BandPosition node__node1 = @BandPosition.CreateNode(graph);
			graph.SettingAddedEdgeNames( ensureMoveLeftValidRule_addedEdgeNames );
			@right edge__edge1 = @right.CreateEdge(graph, node__node1, node_bp);
			return EmptyReturnElements;
		}
		private static String[] ensureMoveLeftValidRule_addedNodeNames = new String[] { "_node1" };
		private static String[] ensureMoveLeftValidRule_addedEdgeNames = new String[] { "_edge1" };

		public override object[] ModifyNoReuse(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)ensureMoveLeftValidRule_NodeNums.@bp];
			graph.SettingAddedNodeNames( ensureMoveLeftValidRule_addedNodeNames );
			@BandPosition node__node1 = @BandPosition.CreateNode(graph);
			graph.SettingAddedEdgeNames( ensureMoveLeftValidRule_addedEdgeNames );
			@right edge__edge1 = @right.CreateEdge(graph, node__node1, node_bp);
			return EmptyReturnElements;
		}
	}

	public class Rule_ensureMoveRightValidRule : GRGEN_LGSP.LGSPRulePattern
	{
		private static Rule_ensureMoveRightValidRule instance = null;
		public static Rule_ensureMoveRightValidRule Instance { get { if (instance==null) { instance = new Rule_ensureMoveRightValidRule(); instance.initialize(); } return instance; } }

		private static object[] ReturnArray = new object[0];

		public static GRGEN_LIBGR.NodeType[] ensureMoveRightValidRule_node_wv_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] ensureMoveRightValidRule_node__node0_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] ensureMoveRightValidRule_node_bp_AllowedTypes = null;
		public static bool[] ensureMoveRightValidRule_node_wv_IsAllowedType = null;
		public static bool[] ensureMoveRightValidRule_node__node0_IsAllowedType = null;
		public static bool[] ensureMoveRightValidRule_node_bp_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] ensureMoveRightValidRule_edge__edge0_AllowedTypes = null;
		public static bool[] ensureMoveRightValidRule_edge__edge0_IsAllowedType = null;
		public enum ensureMoveRightValidRule_NodeNums { @wv, @_node0, @bp, };
		public enum ensureMoveRightValidRule_EdgeNums { @_edge0, };
		public enum ensureMoveRightValidRule_VariableNums { };
		public enum ensureMoveRightValidRule_SubNums { };
		public enum ensureMoveRightValidRule_AltNums { };
		GRGEN_LGSP.PatternGraph pat_ensureMoveRightValidRule;

		public static GRGEN_LIBGR.NodeType[] ensureMoveRightValidRule_neg_0_node__node0_AllowedTypes = null;
		public static bool[] ensureMoveRightValidRule_neg_0_node__node0_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] ensureMoveRightValidRule_neg_0_edge__edge0_AllowedTypes = null;
		public static bool[] ensureMoveRightValidRule_neg_0_edge__edge0_IsAllowedType = null;
		public enum ensureMoveRightValidRule_neg_0_NodeNums { @bp, @_node0, };
		public enum ensureMoveRightValidRule_neg_0_EdgeNums { @_edge0, };
		public enum ensureMoveRightValidRule_neg_0_VariableNums { };
		public enum ensureMoveRightValidRule_neg_0_SubNums { };
		public enum ensureMoveRightValidRule_neg_0_AltNums { };
		GRGEN_LGSP.PatternGraph ensureMoveRightValidRule_neg_0;


#if INITIAL_WARMUP
		public Rule_ensureMoveRightValidRule()
#else
		private Rule_ensureMoveRightValidRule()
#endif
		{
			name = "ensureMoveRightValidRule";

			inputs = new GRGEN_LIBGR.GrGenType[] { NodeType_WriteValue.typeVar, NodeType_BandPosition.typeVar, };
			inputNames = new string[] { "ensureMoveRightValidRule_node_wv", "ensureMoveRightValidRule_node_bp", };
			outputs = new GRGEN_LIBGR.GrGenType[] { };
		}
		public override void initialize()
		{
			bool[,] ensureMoveRightValidRule_isNodeHomomorphicGlobal = new bool[3, 3] {
				{ false, false, false, },
				{ false, false, false, },
				{ false, false, false, },
			};
			bool[,] ensureMoveRightValidRule_isEdgeHomomorphicGlobal = new bool[1, 1] {
				{ false, },
			};
			GRGEN_LGSP.PatternNode ensureMoveRightValidRule_node_wv = new GRGEN_LGSP.PatternNode((int) NodeTypes.@WriteValue, "ensureMoveRightValidRule_node_wv", "wv", ensureMoveRightValidRule_node_wv_AllowedTypes, ensureMoveRightValidRule_node_wv_IsAllowedType, 5.5F, 0);
			GRGEN_LGSP.PatternNode ensureMoveRightValidRule_node__node0 = new GRGEN_LGSP.PatternNode((int) NodeTypes.@State, "ensureMoveRightValidRule_node__node0", "_node0", ensureMoveRightValidRule_node__node0_AllowedTypes, ensureMoveRightValidRule_node__node0_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode ensureMoveRightValidRule_node_bp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "ensureMoveRightValidRule_node_bp", "bp", ensureMoveRightValidRule_node_bp_AllowedTypes, ensureMoveRightValidRule_node_bp_IsAllowedType, 5.5F, 1);
			GRGEN_LGSP.PatternEdge ensureMoveRightValidRule_edge__edge0 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@moveRight, "ensureMoveRightValidRule_edge__edge0", "_edge0", ensureMoveRightValidRule_edge__edge0_AllowedTypes, ensureMoveRightValidRule_edge__edge0_IsAllowedType, 5.5F, -1);
			bool[,] ensureMoveRightValidRule_neg_0_isNodeHomomorphicGlobal = new bool[2, 2] {
				{ false, false, },
				{ false, false, },
			};
			bool[,] ensureMoveRightValidRule_neg_0_isEdgeHomomorphicGlobal = new bool[1, 1] {
				{ false, },
			};
			GRGEN_LGSP.PatternNode ensureMoveRightValidRule_neg_0_node__node0 = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "ensureMoveRightValidRule_neg_0_node__node0", "_node0", ensureMoveRightValidRule_neg_0_node__node0_AllowedTypes, ensureMoveRightValidRule_neg_0_node__node0_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternEdge ensureMoveRightValidRule_neg_0_edge__edge0 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@right, "ensureMoveRightValidRule_neg_0_edge__edge0", "_edge0", ensureMoveRightValidRule_neg_0_edge__edge0_AllowedTypes, ensureMoveRightValidRule_neg_0_edge__edge0_IsAllowedType, 5.5F, -1);
			ensureMoveRightValidRule_neg_0 = new GRGEN_LGSP.PatternGraph(
				"neg_0",
				"ensureMoveRightValidRule_",
				false,
				new GRGEN_LGSP.PatternNode[] { ensureMoveRightValidRule_node_bp, ensureMoveRightValidRule_neg_0_node__node0 }, 
				new GRGEN_LGSP.PatternEdge[] { ensureMoveRightValidRule_neg_0_edge__edge0 }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] {  }, 
				new GRGEN_LGSP.PatternCondition[] {  }, 
				new bool[2, 2] {
					{ true, false, },
					{ false, true, },
				},
				new bool[1, 1] {
					{ true, },
				},
				ensureMoveRightValidRule_neg_0_isNodeHomomorphicGlobal,
				ensureMoveRightValidRule_neg_0_isEdgeHomomorphicGlobal
			);
			ensureMoveRightValidRule_neg_0.edgeToSourceNode.Add(ensureMoveRightValidRule_neg_0_edge__edge0, ensureMoveRightValidRule_node_bp);
			ensureMoveRightValidRule_neg_0.edgeToTargetNode.Add(ensureMoveRightValidRule_neg_0_edge__edge0, ensureMoveRightValidRule_neg_0_node__node0);

			pat_ensureMoveRightValidRule = new GRGEN_LGSP.PatternGraph(
				"ensureMoveRightValidRule",
				"",
				false,
				new GRGEN_LGSP.PatternNode[] { ensureMoveRightValidRule_node_wv, ensureMoveRightValidRule_node__node0, ensureMoveRightValidRule_node_bp }, 
				new GRGEN_LGSP.PatternEdge[] { ensureMoveRightValidRule_edge__edge0 }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] { ensureMoveRightValidRule_neg_0,  }, 
				new GRGEN_LGSP.PatternCondition[] {  }, 
				new bool[3, 3] {
					{ true, false, false, },
					{ false, true, false, },
					{ false, false, true, },
				},
				new bool[1, 1] {
					{ true, },
				},
				ensureMoveRightValidRule_isNodeHomomorphicGlobal,
				ensureMoveRightValidRule_isEdgeHomomorphicGlobal
			);
			pat_ensureMoveRightValidRule.edgeToSourceNode.Add(ensureMoveRightValidRule_edge__edge0, ensureMoveRightValidRule_node_wv);
			pat_ensureMoveRightValidRule.edgeToTargetNode.Add(ensureMoveRightValidRule_edge__edge0, ensureMoveRightValidRule_node__node0);
			ensureMoveRightValidRule_neg_0.embeddingGraph = pat_ensureMoveRightValidRule;

			ensureMoveRightValidRule_node_wv.PointOfDefinition = null;
			ensureMoveRightValidRule_node__node0.PointOfDefinition = pat_ensureMoveRightValidRule;
			ensureMoveRightValidRule_node_bp.PointOfDefinition = null;
			ensureMoveRightValidRule_edge__edge0.PointOfDefinition = pat_ensureMoveRightValidRule;
			ensureMoveRightValidRule_neg_0_node__node0.PointOfDefinition = ensureMoveRightValidRule_neg_0;
			ensureMoveRightValidRule_neg_0_edge__edge0.PointOfDefinition = ensureMoveRightValidRule_neg_0;

			patternGraph = pat_ensureMoveRightValidRule;
		}


		public override object[] Modify(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)ensureMoveRightValidRule_NodeNums.@bp];
			graph.SettingAddedNodeNames( ensureMoveRightValidRule_addedNodeNames );
			@BandPosition node__node1 = @BandPosition.CreateNode(graph);
			graph.SettingAddedEdgeNames( ensureMoveRightValidRule_addedEdgeNames );
			@right edge__edge1 = @right.CreateEdge(graph, node_bp, node__node1);
			return EmptyReturnElements;
		}
		private static String[] ensureMoveRightValidRule_addedNodeNames = new String[] { "_node1" };
		private static String[] ensureMoveRightValidRule_addedEdgeNames = new String[] { "_edge1" };

		public override object[] ModifyNoReuse(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_bp = curMatch.Nodes[(int)ensureMoveRightValidRule_NodeNums.@bp];
			graph.SettingAddedNodeNames( ensureMoveRightValidRule_addedNodeNames );
			@BandPosition node__node1 = @BandPosition.CreateNode(graph);
			graph.SettingAddedEdgeNames( ensureMoveRightValidRule_addedEdgeNames );
			@right edge__edge1 = @right.CreateEdge(graph, node_bp, node__node1);
			return EmptyReturnElements;
		}
	}

	public class Rule_moveLeftRule : GRGEN_LGSP.LGSPRulePattern
	{
		private static Rule_moveLeftRule instance = null;
		public static Rule_moveLeftRule Instance { get { if (instance==null) { instance = new Rule_moveLeftRule(); instance.initialize(); } return instance; } }

		private static object[] ReturnArray = new object[2];

		public static GRGEN_LIBGR.NodeType[] moveLeftRule_node_wv_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] moveLeftRule_node_s_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] moveLeftRule_node_lbp_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] moveLeftRule_node_bp_AllowedTypes = null;
		public static bool[] moveLeftRule_node_wv_IsAllowedType = null;
		public static bool[] moveLeftRule_node_s_IsAllowedType = null;
		public static bool[] moveLeftRule_node_lbp_IsAllowedType = null;
		public static bool[] moveLeftRule_node_bp_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] moveLeftRule_edge__edge0_AllowedTypes = null;
		public static GRGEN_LIBGR.EdgeType[] moveLeftRule_edge__edge1_AllowedTypes = null;
		public static bool[] moveLeftRule_edge__edge0_IsAllowedType = null;
		public static bool[] moveLeftRule_edge__edge1_IsAllowedType = null;
		public enum moveLeftRule_NodeNums { @wv, @s, @lbp, @bp, };
		public enum moveLeftRule_EdgeNums { @_edge0, @_edge1, };
		public enum moveLeftRule_VariableNums { };
		public enum moveLeftRule_SubNums { };
		public enum moveLeftRule_AltNums { };
		GRGEN_LGSP.PatternGraph pat_moveLeftRule;


#if INITIAL_WARMUP
		public Rule_moveLeftRule()
#else
		private Rule_moveLeftRule()
#endif
		{
			name = "moveLeftRule";

			inputs = new GRGEN_LIBGR.GrGenType[] { NodeType_WriteValue.typeVar, NodeType_BandPosition.typeVar, };
			inputNames = new string[] { "moveLeftRule_node_wv", "moveLeftRule_node_bp", };
			outputs = new GRGEN_LIBGR.GrGenType[] { NodeType_State.typeVar, NodeType_BandPosition.typeVar, };
		}
		public override void initialize()
		{
			bool[,] moveLeftRule_isNodeHomomorphicGlobal = new bool[4, 4] {
				{ false, false, false, false, },
				{ false, false, false, false, },
				{ false, false, false, false, },
				{ false, false, false, false, },
			};
			bool[,] moveLeftRule_isEdgeHomomorphicGlobal = new bool[2, 2] {
				{ false, false, },
				{ false, false, },
			};
			GRGEN_LGSP.PatternNode moveLeftRule_node_wv = new GRGEN_LGSP.PatternNode((int) NodeTypes.@WriteValue, "moveLeftRule_node_wv", "wv", moveLeftRule_node_wv_AllowedTypes, moveLeftRule_node_wv_IsAllowedType, 5.5F, 0);
			GRGEN_LGSP.PatternNode moveLeftRule_node_s = new GRGEN_LGSP.PatternNode((int) NodeTypes.@State, "moveLeftRule_node_s", "s", moveLeftRule_node_s_AllowedTypes, moveLeftRule_node_s_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode moveLeftRule_node_lbp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "moveLeftRule_node_lbp", "lbp", moveLeftRule_node_lbp_AllowedTypes, moveLeftRule_node_lbp_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode moveLeftRule_node_bp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "moveLeftRule_node_bp", "bp", moveLeftRule_node_bp_AllowedTypes, moveLeftRule_node_bp_IsAllowedType, 5.5F, 1);
			GRGEN_LGSP.PatternEdge moveLeftRule_edge__edge0 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@moveLeft, "moveLeftRule_edge__edge0", "_edge0", moveLeftRule_edge__edge0_AllowedTypes, moveLeftRule_edge__edge0_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternEdge moveLeftRule_edge__edge1 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@right, "moveLeftRule_edge__edge1", "_edge1", moveLeftRule_edge__edge1_AllowedTypes, moveLeftRule_edge__edge1_IsAllowedType, 5.5F, -1);
			pat_moveLeftRule = new GRGEN_LGSP.PatternGraph(
				"moveLeftRule",
				"",
				false,
				new GRGEN_LGSP.PatternNode[] { moveLeftRule_node_wv, moveLeftRule_node_s, moveLeftRule_node_lbp, moveLeftRule_node_bp }, 
				new GRGEN_LGSP.PatternEdge[] { moveLeftRule_edge__edge0, moveLeftRule_edge__edge1 }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] {  }, 
				new GRGEN_LGSP.PatternCondition[] {  }, 
				new bool[4, 4] {
					{ true, false, false, false, },
					{ false, true, false, false, },
					{ false, false, true, false, },
					{ false, false, false, true, },
				},
				new bool[2, 2] {
					{ true, false, },
					{ false, true, },
				},
				moveLeftRule_isNodeHomomorphicGlobal,
				moveLeftRule_isEdgeHomomorphicGlobal
			);
			pat_moveLeftRule.edgeToSourceNode.Add(moveLeftRule_edge__edge0, moveLeftRule_node_wv);
			pat_moveLeftRule.edgeToTargetNode.Add(moveLeftRule_edge__edge0, moveLeftRule_node_s);
			pat_moveLeftRule.edgeToSourceNode.Add(moveLeftRule_edge__edge1, moveLeftRule_node_lbp);
			pat_moveLeftRule.edgeToTargetNode.Add(moveLeftRule_edge__edge1, moveLeftRule_node_bp);

			moveLeftRule_node_wv.PointOfDefinition = null;
			moveLeftRule_node_s.PointOfDefinition = pat_moveLeftRule;
			moveLeftRule_node_lbp.PointOfDefinition = pat_moveLeftRule;
			moveLeftRule_node_bp.PointOfDefinition = null;
			moveLeftRule_edge__edge0.PointOfDefinition = pat_moveLeftRule;
			moveLeftRule_edge__edge1.PointOfDefinition = pat_moveLeftRule;

			patternGraph = pat_moveLeftRule;
		}


		public override object[] Modify(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_s = curMatch.Nodes[(int)moveLeftRule_NodeNums.@s];
			GRGEN_LGSP.LGSPNode node_lbp = curMatch.Nodes[(int)moveLeftRule_NodeNums.@lbp];
			graph.SettingAddedNodeNames( moveLeftRule_addedNodeNames );
			graph.SettingAddedEdgeNames( moveLeftRule_addedEdgeNames );
			ReturnArray[0] = node_s;
			ReturnArray[1] = node_lbp;
			return ReturnArray;
		}
		private static String[] moveLeftRule_addedNodeNames = new String[] {  };
		private static String[] moveLeftRule_addedEdgeNames = new String[] {  };

		public override object[] ModifyNoReuse(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_s = curMatch.Nodes[(int)moveLeftRule_NodeNums.@s];
			GRGEN_LGSP.LGSPNode node_lbp = curMatch.Nodes[(int)moveLeftRule_NodeNums.@lbp];
			graph.SettingAddedNodeNames( moveLeftRule_addedNodeNames );
			graph.SettingAddedEdgeNames( moveLeftRule_addedEdgeNames );
			ReturnArray[0] = node_s;
			ReturnArray[1] = node_lbp;
			return ReturnArray;
		}
	}

	public class Rule_moveRightRule : GRGEN_LGSP.LGSPRulePattern
	{
		private static Rule_moveRightRule instance = null;
		public static Rule_moveRightRule Instance { get { if (instance==null) { instance = new Rule_moveRightRule(); instance.initialize(); } return instance; } }

		private static object[] ReturnArray = new object[2];

		public static GRGEN_LIBGR.NodeType[] moveRightRule_node_wv_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] moveRightRule_node_s_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] moveRightRule_node_bp_AllowedTypes = null;
		public static GRGEN_LIBGR.NodeType[] moveRightRule_node_rbp_AllowedTypes = null;
		public static bool[] moveRightRule_node_wv_IsAllowedType = null;
		public static bool[] moveRightRule_node_s_IsAllowedType = null;
		public static bool[] moveRightRule_node_bp_IsAllowedType = null;
		public static bool[] moveRightRule_node_rbp_IsAllowedType = null;
		public static GRGEN_LIBGR.EdgeType[] moveRightRule_edge__edge0_AllowedTypes = null;
		public static GRGEN_LIBGR.EdgeType[] moveRightRule_edge__edge1_AllowedTypes = null;
		public static bool[] moveRightRule_edge__edge0_IsAllowedType = null;
		public static bool[] moveRightRule_edge__edge1_IsAllowedType = null;
		public enum moveRightRule_NodeNums { @wv, @s, @bp, @rbp, };
		public enum moveRightRule_EdgeNums { @_edge0, @_edge1, };
		public enum moveRightRule_VariableNums { };
		public enum moveRightRule_SubNums { };
		public enum moveRightRule_AltNums { };
		GRGEN_LGSP.PatternGraph pat_moveRightRule;


#if INITIAL_WARMUP
		public Rule_moveRightRule()
#else
		private Rule_moveRightRule()
#endif
		{
			name = "moveRightRule";

			inputs = new GRGEN_LIBGR.GrGenType[] { NodeType_WriteValue.typeVar, NodeType_BandPosition.typeVar, };
			inputNames = new string[] { "moveRightRule_node_wv", "moveRightRule_node_bp", };
			outputs = new GRGEN_LIBGR.GrGenType[] { NodeType_State.typeVar, NodeType_BandPosition.typeVar, };
		}
		public override void initialize()
		{
			bool[,] moveRightRule_isNodeHomomorphicGlobal = new bool[4, 4] {
				{ false, false, false, false, },
				{ false, false, false, false, },
				{ false, false, false, false, },
				{ false, false, false, false, },
			};
			bool[,] moveRightRule_isEdgeHomomorphicGlobal = new bool[2, 2] {
				{ false, false, },
				{ false, false, },
			};
			GRGEN_LGSP.PatternNode moveRightRule_node_wv = new GRGEN_LGSP.PatternNode((int) NodeTypes.@WriteValue, "moveRightRule_node_wv", "wv", moveRightRule_node_wv_AllowedTypes, moveRightRule_node_wv_IsAllowedType, 5.5F, 0);
			GRGEN_LGSP.PatternNode moveRightRule_node_s = new GRGEN_LGSP.PatternNode((int) NodeTypes.@State, "moveRightRule_node_s", "s", moveRightRule_node_s_AllowedTypes, moveRightRule_node_s_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternNode moveRightRule_node_bp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "moveRightRule_node_bp", "bp", moveRightRule_node_bp_AllowedTypes, moveRightRule_node_bp_IsAllowedType, 5.5F, 1);
			GRGEN_LGSP.PatternNode moveRightRule_node_rbp = new GRGEN_LGSP.PatternNode((int) NodeTypes.@BandPosition, "moveRightRule_node_rbp", "rbp", moveRightRule_node_rbp_AllowedTypes, moveRightRule_node_rbp_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternEdge moveRightRule_edge__edge0 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@moveRight, "moveRightRule_edge__edge0", "_edge0", moveRightRule_edge__edge0_AllowedTypes, moveRightRule_edge__edge0_IsAllowedType, 5.5F, -1);
			GRGEN_LGSP.PatternEdge moveRightRule_edge__edge1 = new GRGEN_LGSP.PatternEdge(true, (int) EdgeTypes.@right, "moveRightRule_edge__edge1", "_edge1", moveRightRule_edge__edge1_AllowedTypes, moveRightRule_edge__edge1_IsAllowedType, 5.5F, -1);
			pat_moveRightRule = new GRGEN_LGSP.PatternGraph(
				"moveRightRule",
				"",
				false,
				new GRGEN_LGSP.PatternNode[] { moveRightRule_node_wv, moveRightRule_node_s, moveRightRule_node_bp, moveRightRule_node_rbp }, 
				new GRGEN_LGSP.PatternEdge[] { moveRightRule_edge__edge0, moveRightRule_edge__edge1 }, 
				new GRGEN_LGSP.PatternVariable[] {  }, 
				new GRGEN_LGSP.PatternGraphEmbedding[] {  }, 
				new GRGEN_LGSP.Alternative[] {  }, 
				new GRGEN_LGSP.PatternGraph[] {  }, 
				new GRGEN_LGSP.PatternCondition[] {  }, 
				new bool[4, 4] {
					{ true, false, false, false, },
					{ false, true, false, false, },
					{ false, false, true, false, },
					{ false, false, false, true, },
				},
				new bool[2, 2] {
					{ true, false, },
					{ false, true, },
				},
				moveRightRule_isNodeHomomorphicGlobal,
				moveRightRule_isEdgeHomomorphicGlobal
			);
			pat_moveRightRule.edgeToSourceNode.Add(moveRightRule_edge__edge0, moveRightRule_node_wv);
			pat_moveRightRule.edgeToTargetNode.Add(moveRightRule_edge__edge0, moveRightRule_node_s);
			pat_moveRightRule.edgeToSourceNode.Add(moveRightRule_edge__edge1, moveRightRule_node_bp);
			pat_moveRightRule.edgeToTargetNode.Add(moveRightRule_edge__edge1, moveRightRule_node_rbp);

			moveRightRule_node_wv.PointOfDefinition = null;
			moveRightRule_node_s.PointOfDefinition = pat_moveRightRule;
			moveRightRule_node_bp.PointOfDefinition = null;
			moveRightRule_node_rbp.PointOfDefinition = pat_moveRightRule;
			moveRightRule_edge__edge0.PointOfDefinition = pat_moveRightRule;
			moveRightRule_edge__edge1.PointOfDefinition = pat_moveRightRule;

			patternGraph = pat_moveRightRule;
		}


		public override object[] Modify(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_s = curMatch.Nodes[(int)moveRightRule_NodeNums.@s];
			GRGEN_LGSP.LGSPNode node_rbp = curMatch.Nodes[(int)moveRightRule_NodeNums.@rbp];
			graph.SettingAddedNodeNames( moveRightRule_addedNodeNames );
			graph.SettingAddedEdgeNames( moveRightRule_addedEdgeNames );
			ReturnArray[0] = node_s;
			ReturnArray[1] = node_rbp;
			return ReturnArray;
		}
		private static String[] moveRightRule_addedNodeNames = new String[] {  };
		private static String[] moveRightRule_addedEdgeNames = new String[] {  };

		public override object[] ModifyNoReuse(GRGEN_LGSP.LGSPGraph graph, GRGEN_LGSP.LGSPMatch curMatch)
		{
			GRGEN_LGSP.LGSPNode node_s = curMatch.Nodes[(int)moveRightRule_NodeNums.@s];
			GRGEN_LGSP.LGSPNode node_rbp = curMatch.Nodes[(int)moveRightRule_NodeNums.@rbp];
			graph.SettingAddedNodeNames( moveRightRule_addedNodeNames );
			graph.SettingAddedEdgeNames( moveRightRule_addedEdgeNames );
			ReturnArray[0] = node_s;
			ReturnArray[1] = node_rbp;
			return ReturnArray;
		}
	}


    public class Action_readZeroRule : GRGEN_LGSP.LGSPAction
    {
        public Action_readZeroRule() {
            rulePattern = Rule_readZeroRule.Instance;
            patternGraph = rulePattern.patternGraph;
            DynamicMatch = myMatch;
            matches = new GRGEN_LGSP.LGSPMatches(this, 3, 1, 0, 0 + 0);
        }

        public override string Name { get { return "readZeroRule"; } }
        private GRGEN_LGSP.LGSPMatches matches;

        public static GRGEN_LGSP.LGSPAction Instance { get { return instance; } }
        private static Action_readZeroRule instance = new Action_readZeroRule();

        public GRGEN_LGSP.LGSPMatches myMatch(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters)
        {
            matches.matchesList.Clear();
            int negLevel = 0;
            // Preset readZeroRule_node_s 
            GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_s = (GRGEN_LGSP.LGSPNode) parameters[0];
            if(candidate_readZeroRule_node_s == null) {
                MissingPreset_readZeroRule_node_s(graph, maxMatches, parameters, null, null, null);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_readZeroRule_node_s.type.TypeID!=2) {
                return matches;
            }
            // Preset readZeroRule_node_bp 
            GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
            if(candidate_readZeroRule_node_bp == null) {
                MissingPreset_readZeroRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_readZeroRule_node_s);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_readZeroRule_node_bp.type.TypeID!=1) {
                return matches;
            }
            // Condition 
            if(!((((IBandPosition)candidate_readZeroRule_node_bp).@value == 0))) {
                return matches;
            }
            // Extend Outgoing readZeroRule_edge_rv from readZeroRule_node_s 
            GRGEN_LGSP.LGSPEdge head_candidate_readZeroRule_edge_rv = candidate_readZeroRule_node_s.outhead;
            if(head_candidate_readZeroRule_edge_rv != null)
            {
                GRGEN_LGSP.LGSPEdge candidate_readZeroRule_edge_rv = head_candidate_readZeroRule_edge_rv;
                do
                {
                    if(candidate_readZeroRule_edge_rv.type.TypeID!=4) {
                        continue;
                    }
                    // Implicit Target readZeroRule_node_wv from readZeroRule_edge_rv 
                    GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_wv = candidate_readZeroRule_edge_rv.target;
                    if(candidate_readZeroRule_node_wv.type.TypeID!=3) {
                        continue;
                    }
                    GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                    match.patternGraph = rulePattern.patternGraph;
                    match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@s] = candidate_readZeroRule_node_s;
                    match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@wv] = candidate_readZeroRule_node_wv;
                    match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@bp] = candidate_readZeroRule_node_bp;
                    match.Edges[(int)Rule_readZeroRule.readZeroRule_EdgeNums.@rv] = candidate_readZeroRule_edge_rv;
                    matches.matchesList.PositionWasFilledFixIt();
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        candidate_readZeroRule_node_s.MoveOutHeadAfter(candidate_readZeroRule_edge_rv);
                        return matches;
                    }
                }
                while( (candidate_readZeroRule_edge_rv = candidate_readZeroRule_edge_rv.outNext) != head_candidate_readZeroRule_edge_rv );
            }
            return matches;
        }
        public void MissingPreset_readZeroRule_node_s(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList)
        {
            int negLevel = 0;
            // Lookup readZeroRule_node_s 
            int type_id_candidate_readZeroRule_node_s = 2;
            for(GRGEN_LGSP.LGSPNode head_candidate_readZeroRule_node_s = graph.nodesByTypeHeads[type_id_candidate_readZeroRule_node_s], candidate_readZeroRule_node_s = head_candidate_readZeroRule_node_s.typeNext; candidate_readZeroRule_node_s != head_candidate_readZeroRule_node_s; candidate_readZeroRule_node_s = candidate_readZeroRule_node_s.typeNext)
            {
                // Preset readZeroRule_node_bp 
                GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
                if(candidate_readZeroRule_node_bp == null) {
                    MissingPreset_readZeroRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_readZeroRule_node_s);
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        return;
                    }
                    continue;
                }
                if(candidate_readZeroRule_node_bp.type.TypeID!=1) {
                    continue;
                }
                // Condition 
                if(!((((IBandPosition)candidate_readZeroRule_node_bp).@value == 0))) {
                    continue;
                }
                // Extend Outgoing readZeroRule_edge_rv from readZeroRule_node_s 
                GRGEN_LGSP.LGSPEdge head_candidate_readZeroRule_edge_rv = candidate_readZeroRule_node_s.outhead;
                if(head_candidate_readZeroRule_edge_rv != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_readZeroRule_edge_rv = head_candidate_readZeroRule_edge_rv;
                    do
                    {
                        if(candidate_readZeroRule_edge_rv.type.TypeID!=4) {
                            continue;
                        }
                        // Implicit Target readZeroRule_node_wv from readZeroRule_edge_rv 
                        GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_wv = candidate_readZeroRule_edge_rv.target;
                        if(candidate_readZeroRule_node_wv.type.TypeID!=3) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@s] = candidate_readZeroRule_node_s;
                        match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@wv] = candidate_readZeroRule_node_wv;
                        match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@bp] = candidate_readZeroRule_node_bp;
                        match.Edges[(int)Rule_readZeroRule.readZeroRule_EdgeNums.@rv] = candidate_readZeroRule_edge_rv;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_readZeroRule_node_s.MoveOutHeadAfter(candidate_readZeroRule_edge_rv);
                            graph.MoveHeadAfter(candidate_readZeroRule_node_s);
                            return;
                        }
                    }
                    while( (candidate_readZeroRule_edge_rv = candidate_readZeroRule_edge_rv.outNext) != head_candidate_readZeroRule_edge_rv );
                }
            }
            return;
        }
        public void MissingPreset_readZeroRule_node_bp(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList, GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_s)
        {
            int negLevel = 0;
            // Lookup readZeroRule_node_bp 
            int type_id_candidate_readZeroRule_node_bp = 1;
            for(GRGEN_LGSP.LGSPNode head_candidate_readZeroRule_node_bp = graph.nodesByTypeHeads[type_id_candidate_readZeroRule_node_bp], candidate_readZeroRule_node_bp = head_candidate_readZeroRule_node_bp.typeNext; candidate_readZeroRule_node_bp != head_candidate_readZeroRule_node_bp; candidate_readZeroRule_node_bp = candidate_readZeroRule_node_bp.typeNext)
            {
                // Condition 
                if(!((((IBandPosition)candidate_readZeroRule_node_bp).@value == 0))) {
                    continue;
                }
                // Extend Outgoing readZeroRule_edge_rv from readZeroRule_node_s 
                GRGEN_LGSP.LGSPEdge head_candidate_readZeroRule_edge_rv = candidate_readZeroRule_node_s.outhead;
                if(head_candidate_readZeroRule_edge_rv != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_readZeroRule_edge_rv = head_candidate_readZeroRule_edge_rv;
                    do
                    {
                        if(candidate_readZeroRule_edge_rv.type.TypeID!=4) {
                            continue;
                        }
                        // Implicit Target readZeroRule_node_wv from readZeroRule_edge_rv 
                        GRGEN_LGSP.LGSPNode candidate_readZeroRule_node_wv = candidate_readZeroRule_edge_rv.target;
                        if(candidate_readZeroRule_node_wv.type.TypeID!=3) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@s] = candidate_readZeroRule_node_s;
                        match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@wv] = candidate_readZeroRule_node_wv;
                        match.Nodes[(int)Rule_readZeroRule.readZeroRule_NodeNums.@bp] = candidate_readZeroRule_node_bp;
                        match.Edges[(int)Rule_readZeroRule.readZeroRule_EdgeNums.@rv] = candidate_readZeroRule_edge_rv;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_readZeroRule_node_s.MoveOutHeadAfter(candidate_readZeroRule_edge_rv);
                            graph.MoveHeadAfter(candidate_readZeroRule_node_bp);
                            return;
                        }
                    }
                    while( (candidate_readZeroRule_edge_rv = candidate_readZeroRule_edge_rv.outNext) != head_candidate_readZeroRule_edge_rv );
                }
            }
            return;
        }
    }

    public class Action_readOneRule : GRGEN_LGSP.LGSPAction
    {
        public Action_readOneRule() {
            rulePattern = Rule_readOneRule.Instance;
            patternGraph = rulePattern.patternGraph;
            DynamicMatch = myMatch;
            matches = new GRGEN_LGSP.LGSPMatches(this, 3, 1, 0, 0 + 0);
        }

        public override string Name { get { return "readOneRule"; } }
        private GRGEN_LGSP.LGSPMatches matches;

        public static GRGEN_LGSP.LGSPAction Instance { get { return instance; } }
        private static Action_readOneRule instance = new Action_readOneRule();

        public GRGEN_LGSP.LGSPMatches myMatch(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters)
        {
            matches.matchesList.Clear();
            int negLevel = 0;
            // Preset readOneRule_node_s 
            GRGEN_LGSP.LGSPNode candidate_readOneRule_node_s = (GRGEN_LGSP.LGSPNode) parameters[0];
            if(candidate_readOneRule_node_s == null) {
                MissingPreset_readOneRule_node_s(graph, maxMatches, parameters, null, null, null);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_readOneRule_node_s.type.TypeID!=2) {
                return matches;
            }
            // Preset readOneRule_node_bp 
            GRGEN_LGSP.LGSPNode candidate_readOneRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
            if(candidate_readOneRule_node_bp == null) {
                MissingPreset_readOneRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_readOneRule_node_s);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_readOneRule_node_bp.type.TypeID!=1) {
                return matches;
            }
            // Condition 
            if(!((((IBandPosition)candidate_readOneRule_node_bp).@value == 1))) {
                return matches;
            }
            // Extend Outgoing readOneRule_edge_rv from readOneRule_node_s 
            GRGEN_LGSP.LGSPEdge head_candidate_readOneRule_edge_rv = candidate_readOneRule_node_s.outhead;
            if(head_candidate_readOneRule_edge_rv != null)
            {
                GRGEN_LGSP.LGSPEdge candidate_readOneRule_edge_rv = head_candidate_readOneRule_edge_rv;
                do
                {
                    if(candidate_readOneRule_edge_rv.type.TypeID!=5) {
                        continue;
                    }
                    // Implicit Target readOneRule_node_wv from readOneRule_edge_rv 
                    GRGEN_LGSP.LGSPNode candidate_readOneRule_node_wv = candidate_readOneRule_edge_rv.target;
                    if(candidate_readOneRule_node_wv.type.TypeID!=3) {
                        continue;
                    }
                    GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                    match.patternGraph = rulePattern.patternGraph;
                    match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@s] = candidate_readOneRule_node_s;
                    match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@wv] = candidate_readOneRule_node_wv;
                    match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@bp] = candidate_readOneRule_node_bp;
                    match.Edges[(int)Rule_readOneRule.readOneRule_EdgeNums.@rv] = candidate_readOneRule_edge_rv;
                    matches.matchesList.PositionWasFilledFixIt();
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        candidate_readOneRule_node_s.MoveOutHeadAfter(candidate_readOneRule_edge_rv);
                        return matches;
                    }
                }
                while( (candidate_readOneRule_edge_rv = candidate_readOneRule_edge_rv.outNext) != head_candidate_readOneRule_edge_rv );
            }
            return matches;
        }
        public void MissingPreset_readOneRule_node_s(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList)
        {
            int negLevel = 0;
            // Lookup readOneRule_node_s 
            int type_id_candidate_readOneRule_node_s = 2;
            for(GRGEN_LGSP.LGSPNode head_candidate_readOneRule_node_s = graph.nodesByTypeHeads[type_id_candidate_readOneRule_node_s], candidate_readOneRule_node_s = head_candidate_readOneRule_node_s.typeNext; candidate_readOneRule_node_s != head_candidate_readOneRule_node_s; candidate_readOneRule_node_s = candidate_readOneRule_node_s.typeNext)
            {
                // Preset readOneRule_node_bp 
                GRGEN_LGSP.LGSPNode candidate_readOneRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
                if(candidate_readOneRule_node_bp == null) {
                    MissingPreset_readOneRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_readOneRule_node_s);
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        return;
                    }
                    continue;
                }
                if(candidate_readOneRule_node_bp.type.TypeID!=1) {
                    continue;
                }
                // Condition 
                if(!((((IBandPosition)candidate_readOneRule_node_bp).@value == 1))) {
                    continue;
                }
                // Extend Outgoing readOneRule_edge_rv from readOneRule_node_s 
                GRGEN_LGSP.LGSPEdge head_candidate_readOneRule_edge_rv = candidate_readOneRule_node_s.outhead;
                if(head_candidate_readOneRule_edge_rv != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_readOneRule_edge_rv = head_candidate_readOneRule_edge_rv;
                    do
                    {
                        if(candidate_readOneRule_edge_rv.type.TypeID!=5) {
                            continue;
                        }
                        // Implicit Target readOneRule_node_wv from readOneRule_edge_rv 
                        GRGEN_LGSP.LGSPNode candidate_readOneRule_node_wv = candidate_readOneRule_edge_rv.target;
                        if(candidate_readOneRule_node_wv.type.TypeID!=3) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@s] = candidate_readOneRule_node_s;
                        match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@wv] = candidate_readOneRule_node_wv;
                        match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@bp] = candidate_readOneRule_node_bp;
                        match.Edges[(int)Rule_readOneRule.readOneRule_EdgeNums.@rv] = candidate_readOneRule_edge_rv;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_readOneRule_node_s.MoveOutHeadAfter(candidate_readOneRule_edge_rv);
                            graph.MoveHeadAfter(candidate_readOneRule_node_s);
                            return;
                        }
                    }
                    while( (candidate_readOneRule_edge_rv = candidate_readOneRule_edge_rv.outNext) != head_candidate_readOneRule_edge_rv );
                }
            }
            return;
        }
        public void MissingPreset_readOneRule_node_bp(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList, GRGEN_LGSP.LGSPNode candidate_readOneRule_node_s)
        {
            int negLevel = 0;
            // Lookup readOneRule_node_bp 
            int type_id_candidate_readOneRule_node_bp = 1;
            for(GRGEN_LGSP.LGSPNode head_candidate_readOneRule_node_bp = graph.nodesByTypeHeads[type_id_candidate_readOneRule_node_bp], candidate_readOneRule_node_bp = head_candidate_readOneRule_node_bp.typeNext; candidate_readOneRule_node_bp != head_candidate_readOneRule_node_bp; candidate_readOneRule_node_bp = candidate_readOneRule_node_bp.typeNext)
            {
                // Condition 
                if(!((((IBandPosition)candidate_readOneRule_node_bp).@value == 1))) {
                    continue;
                }
                // Extend Outgoing readOneRule_edge_rv from readOneRule_node_s 
                GRGEN_LGSP.LGSPEdge head_candidate_readOneRule_edge_rv = candidate_readOneRule_node_s.outhead;
                if(head_candidate_readOneRule_edge_rv != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_readOneRule_edge_rv = head_candidate_readOneRule_edge_rv;
                    do
                    {
                        if(candidate_readOneRule_edge_rv.type.TypeID!=5) {
                            continue;
                        }
                        // Implicit Target readOneRule_node_wv from readOneRule_edge_rv 
                        GRGEN_LGSP.LGSPNode candidate_readOneRule_node_wv = candidate_readOneRule_edge_rv.target;
                        if(candidate_readOneRule_node_wv.type.TypeID!=3) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@s] = candidate_readOneRule_node_s;
                        match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@wv] = candidate_readOneRule_node_wv;
                        match.Nodes[(int)Rule_readOneRule.readOneRule_NodeNums.@bp] = candidate_readOneRule_node_bp;
                        match.Edges[(int)Rule_readOneRule.readOneRule_EdgeNums.@rv] = candidate_readOneRule_edge_rv;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_readOneRule_node_s.MoveOutHeadAfter(candidate_readOneRule_edge_rv);
                            graph.MoveHeadAfter(candidate_readOneRule_node_bp);
                            return;
                        }
                    }
                    while( (candidate_readOneRule_edge_rv = candidate_readOneRule_edge_rv.outNext) != head_candidate_readOneRule_edge_rv );
                }
            }
            return;
        }
    }

    public class Action_ensureMoveLeftValidRule : GRGEN_LGSP.LGSPAction
    {
        public Action_ensureMoveLeftValidRule() {
            rulePattern = Rule_ensureMoveLeftValidRule.Instance;
            patternGraph = rulePattern.patternGraph;
            DynamicMatch = myMatch;
            matches = new GRGEN_LGSP.LGSPMatches(this, 3, 1, 0, 0 + 0);
        }

        public override string Name { get { return "ensureMoveLeftValidRule"; } }
        private GRGEN_LGSP.LGSPMatches matches;

        public static GRGEN_LGSP.LGSPAction Instance { get { return instance; } }
        private static Action_ensureMoveLeftValidRule instance = new Action_ensureMoveLeftValidRule();

        public GRGEN_LGSP.LGSPMatches myMatch(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters)
        {
            matches.matchesList.Clear();
            int negLevel = 0;
            // Preset ensureMoveLeftValidRule_node_wv 
            GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node_wv = (GRGEN_LGSP.LGSPNode) parameters[0];
            if(candidate_ensureMoveLeftValidRule_node_wv == null) {
                MissingPreset_ensureMoveLeftValidRule_node_wv(graph, maxMatches, parameters, null, null, null);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_ensureMoveLeftValidRule_node_wv.type.TypeID!=3) {
                return matches;
            }
            // Preset ensureMoveLeftValidRule_node_bp 
            GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
            if(candidate_ensureMoveLeftValidRule_node_bp == null) {
                MissingPreset_ensureMoveLeftValidRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_ensureMoveLeftValidRule_node_wv);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_ensureMoveLeftValidRule_node_bp.type.TypeID!=1) {
                return matches;
            }
            // NegativePattern 
            {
                ++negLevel;
                uint prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp = candidate_ensureMoveLeftValidRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                candidate_ensureMoveLeftValidRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                // Extend Incoming ensureMoveLeftValidRule_neg_0_edge__edge0 from ensureMoveLeftValidRule_node_bp 
                GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = candidate_ensureMoveLeftValidRule_node_bp.inhead;
                if(head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0;
                    do
                    {
                        if(candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.type.TypeID!=3) {
                            continue;
                        }
                        // Implicit Source ensureMoveLeftValidRule_neg_0_node__node0 from ensureMoveLeftValidRule_neg_0_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_neg_0_node__node0 = candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.source;
                        if(candidate_ensureMoveLeftValidRule_neg_0_node__node0.type.TypeID!=1) {
                            continue;
                        }
                        if((candidate_ensureMoveLeftValidRule_neg_0_node__node0.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                        {
                            continue;
                        }
                        // negative pattern found
                        candidate_ensureMoveLeftValidRule_node_bp.flags = candidate_ensureMoveLeftValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                        --negLevel;
                        return matches;
                    }
                    while( (candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.inNext) != head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 );
                }
                candidate_ensureMoveLeftValidRule_node_bp.flags = candidate_ensureMoveLeftValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                --negLevel;
            }
            // Extend Outgoing ensureMoveLeftValidRule_edge__edge0 from ensureMoveLeftValidRule_node_wv 
            GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveLeftValidRule_edge__edge0 = candidate_ensureMoveLeftValidRule_node_wv.outhead;
            if(head_candidate_ensureMoveLeftValidRule_edge__edge0 != null)
            {
                GRGEN_LGSP.LGSPEdge candidate_ensureMoveLeftValidRule_edge__edge0 = head_candidate_ensureMoveLeftValidRule_edge__edge0;
                do
                {
                    if(candidate_ensureMoveLeftValidRule_edge__edge0.type.TypeID!=6) {
                        continue;
                    }
                    // Implicit Target ensureMoveLeftValidRule_node__node0 from ensureMoveLeftValidRule_edge__edge0 
                    GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node__node0 = candidate_ensureMoveLeftValidRule_edge__edge0.target;
                    if(candidate_ensureMoveLeftValidRule_node__node0.type.TypeID!=2) {
                        continue;
                    }
                    GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                    match.patternGraph = rulePattern.patternGraph;
                    match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@wv] = candidate_ensureMoveLeftValidRule_node_wv;
                    match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@_node0] = candidate_ensureMoveLeftValidRule_node__node0;
                    match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@bp] = candidate_ensureMoveLeftValidRule_node_bp;
                    match.Edges[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_EdgeNums.@_edge0] = candidate_ensureMoveLeftValidRule_edge__edge0;
                    matches.matchesList.PositionWasFilledFixIt();
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        candidate_ensureMoveLeftValidRule_node_wv.MoveOutHeadAfter(candidate_ensureMoveLeftValidRule_edge__edge0);
                        return matches;
                    }
                }
                while( (candidate_ensureMoveLeftValidRule_edge__edge0 = candidate_ensureMoveLeftValidRule_edge__edge0.outNext) != head_candidate_ensureMoveLeftValidRule_edge__edge0 );
            }
            return matches;
        }
        public void MissingPreset_ensureMoveLeftValidRule_node_wv(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList)
        {
            int negLevel = 0;
            // Lookup ensureMoveLeftValidRule_node_wv 
            int type_id_candidate_ensureMoveLeftValidRule_node_wv = 3;
            for(GRGEN_LGSP.LGSPNode head_candidate_ensureMoveLeftValidRule_node_wv = graph.nodesByTypeHeads[type_id_candidate_ensureMoveLeftValidRule_node_wv], candidate_ensureMoveLeftValidRule_node_wv = head_candidate_ensureMoveLeftValidRule_node_wv.typeNext; candidate_ensureMoveLeftValidRule_node_wv != head_candidate_ensureMoveLeftValidRule_node_wv; candidate_ensureMoveLeftValidRule_node_wv = candidate_ensureMoveLeftValidRule_node_wv.typeNext)
            {
                // Preset ensureMoveLeftValidRule_node_bp 
                GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
                if(candidate_ensureMoveLeftValidRule_node_bp == null) {
                    MissingPreset_ensureMoveLeftValidRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_ensureMoveLeftValidRule_node_wv);
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        return;
                    }
                    continue;
                }
                if(candidate_ensureMoveLeftValidRule_node_bp.type.TypeID!=1) {
                    continue;
                }
                // NegativePattern 
                {
                    ++negLevel;
                    uint prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                    prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp = candidate_ensureMoveLeftValidRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    candidate_ensureMoveLeftValidRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    // Extend Incoming ensureMoveLeftValidRule_neg_0_edge__edge0 from ensureMoveLeftValidRule_node_bp 
                    GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = candidate_ensureMoveLeftValidRule_node_bp.inhead;
                    if(head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 != null)
                    {
                        GRGEN_LGSP.LGSPEdge candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0;
                        do
                        {
                            if(candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.type.TypeID!=3) {
                                continue;
                            }
                            // Implicit Source ensureMoveLeftValidRule_neg_0_node__node0 from ensureMoveLeftValidRule_neg_0_edge__edge0 
                            GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_neg_0_node__node0 = candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.source;
                            if(candidate_ensureMoveLeftValidRule_neg_0_node__node0.type.TypeID!=1) {
                                continue;
                            }
                            if((candidate_ensureMoveLeftValidRule_neg_0_node__node0.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                            {
                                continue;
                            }
                            // negative pattern found
                            candidate_ensureMoveLeftValidRule_node_bp.flags = candidate_ensureMoveLeftValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                            --negLevel;
                            goto label0;
                        }
                        while( (candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.inNext) != head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 );
                    }
                    candidate_ensureMoveLeftValidRule_node_bp.flags = candidate_ensureMoveLeftValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                    --negLevel;
                }
                // Extend Outgoing ensureMoveLeftValidRule_edge__edge0 from ensureMoveLeftValidRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveLeftValidRule_edge__edge0 = candidate_ensureMoveLeftValidRule_node_wv.outhead;
                if(head_candidate_ensureMoveLeftValidRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_ensureMoveLeftValidRule_edge__edge0 = head_candidate_ensureMoveLeftValidRule_edge__edge0;
                    do
                    {
                        if(candidate_ensureMoveLeftValidRule_edge__edge0.type.TypeID!=6) {
                            continue;
                        }
                        // Implicit Target ensureMoveLeftValidRule_node__node0 from ensureMoveLeftValidRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node__node0 = candidate_ensureMoveLeftValidRule_edge__edge0.target;
                        if(candidate_ensureMoveLeftValidRule_node__node0.type.TypeID!=2) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@wv] = candidate_ensureMoveLeftValidRule_node_wv;
                        match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@_node0] = candidate_ensureMoveLeftValidRule_node__node0;
                        match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@bp] = candidate_ensureMoveLeftValidRule_node_bp;
                        match.Edges[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_EdgeNums.@_edge0] = candidate_ensureMoveLeftValidRule_edge__edge0;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_ensureMoveLeftValidRule_node_wv.MoveOutHeadAfter(candidate_ensureMoveLeftValidRule_edge__edge0);
                            graph.MoveHeadAfter(candidate_ensureMoveLeftValidRule_node_wv);
                            return;
                        }
                    }
                    while( (candidate_ensureMoveLeftValidRule_edge__edge0 = candidate_ensureMoveLeftValidRule_edge__edge0.outNext) != head_candidate_ensureMoveLeftValidRule_edge__edge0 );
                }
label0: ;
            }
            return;
        }
        public void MissingPreset_ensureMoveLeftValidRule_node_bp(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList, GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node_wv)
        {
            int negLevel = 0;
            // Lookup ensureMoveLeftValidRule_node_bp 
            int type_id_candidate_ensureMoveLeftValidRule_node_bp = 1;
            for(GRGEN_LGSP.LGSPNode head_candidate_ensureMoveLeftValidRule_node_bp = graph.nodesByTypeHeads[type_id_candidate_ensureMoveLeftValidRule_node_bp], candidate_ensureMoveLeftValidRule_node_bp = head_candidate_ensureMoveLeftValidRule_node_bp.typeNext; candidate_ensureMoveLeftValidRule_node_bp != head_candidate_ensureMoveLeftValidRule_node_bp; candidate_ensureMoveLeftValidRule_node_bp = candidate_ensureMoveLeftValidRule_node_bp.typeNext)
            {
                // NegativePattern 
                {
                    ++negLevel;
                    uint prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                    prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp = candidate_ensureMoveLeftValidRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    candidate_ensureMoveLeftValidRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    // Extend Incoming ensureMoveLeftValidRule_neg_0_edge__edge0 from ensureMoveLeftValidRule_node_bp 
                    GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = candidate_ensureMoveLeftValidRule_node_bp.inhead;
                    if(head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 != null)
                    {
                        GRGEN_LGSP.LGSPEdge candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0;
                        do
                        {
                            if(candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.type.TypeID!=3) {
                                continue;
                            }
                            // Implicit Source ensureMoveLeftValidRule_neg_0_node__node0 from ensureMoveLeftValidRule_neg_0_edge__edge0 
                            GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_neg_0_node__node0 = candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.source;
                            if(candidate_ensureMoveLeftValidRule_neg_0_node__node0.type.TypeID!=1) {
                                continue;
                            }
                            if((candidate_ensureMoveLeftValidRule_neg_0_node__node0.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                            {
                                continue;
                            }
                            // negative pattern found
                            candidate_ensureMoveLeftValidRule_node_bp.flags = candidate_ensureMoveLeftValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                            --negLevel;
                            goto label1;
                        }
                        while( (candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 = candidate_ensureMoveLeftValidRule_neg_0_edge__edge0.inNext) != head_candidate_ensureMoveLeftValidRule_neg_0_edge__edge0 );
                    }
                    candidate_ensureMoveLeftValidRule_node_bp.flags = candidate_ensureMoveLeftValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveLeftValidRule_node_bp;
                    --negLevel;
                }
                // Extend Outgoing ensureMoveLeftValidRule_edge__edge0 from ensureMoveLeftValidRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveLeftValidRule_edge__edge0 = candidate_ensureMoveLeftValidRule_node_wv.outhead;
                if(head_candidate_ensureMoveLeftValidRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_ensureMoveLeftValidRule_edge__edge0 = head_candidate_ensureMoveLeftValidRule_edge__edge0;
                    do
                    {
                        if(candidate_ensureMoveLeftValidRule_edge__edge0.type.TypeID!=6) {
                            continue;
                        }
                        // Implicit Target ensureMoveLeftValidRule_node__node0 from ensureMoveLeftValidRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_ensureMoveLeftValidRule_node__node0 = candidate_ensureMoveLeftValidRule_edge__edge0.target;
                        if(candidate_ensureMoveLeftValidRule_node__node0.type.TypeID!=2) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@wv] = candidate_ensureMoveLeftValidRule_node_wv;
                        match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@_node0] = candidate_ensureMoveLeftValidRule_node__node0;
                        match.Nodes[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_NodeNums.@bp] = candidate_ensureMoveLeftValidRule_node_bp;
                        match.Edges[(int)Rule_ensureMoveLeftValidRule.ensureMoveLeftValidRule_EdgeNums.@_edge0] = candidate_ensureMoveLeftValidRule_edge__edge0;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_ensureMoveLeftValidRule_node_wv.MoveOutHeadAfter(candidate_ensureMoveLeftValidRule_edge__edge0);
                            graph.MoveHeadAfter(candidate_ensureMoveLeftValidRule_node_bp);
                            return;
                        }
                    }
                    while( (candidate_ensureMoveLeftValidRule_edge__edge0 = candidate_ensureMoveLeftValidRule_edge__edge0.outNext) != head_candidate_ensureMoveLeftValidRule_edge__edge0 );
                }
label1: ;
            }
            return;
        }
    }

    public class Action_ensureMoveRightValidRule : GRGEN_LGSP.LGSPAction
    {
        public Action_ensureMoveRightValidRule() {
            rulePattern = Rule_ensureMoveRightValidRule.Instance;
            patternGraph = rulePattern.patternGraph;
            DynamicMatch = myMatch;
            matches = new GRGEN_LGSP.LGSPMatches(this, 3, 1, 0, 0 + 0);
        }

        public override string Name { get { return "ensureMoveRightValidRule"; } }
        private GRGEN_LGSP.LGSPMatches matches;

        public static GRGEN_LGSP.LGSPAction Instance { get { return instance; } }
        private static Action_ensureMoveRightValidRule instance = new Action_ensureMoveRightValidRule();

        public GRGEN_LGSP.LGSPMatches myMatch(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters)
        {
            matches.matchesList.Clear();
            int negLevel = 0;
            // Preset ensureMoveRightValidRule_node_wv 
            GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node_wv = (GRGEN_LGSP.LGSPNode) parameters[0];
            if(candidate_ensureMoveRightValidRule_node_wv == null) {
                MissingPreset_ensureMoveRightValidRule_node_wv(graph, maxMatches, parameters, null, null, null);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_ensureMoveRightValidRule_node_wv.type.TypeID!=3) {
                return matches;
            }
            // Preset ensureMoveRightValidRule_node_bp 
            GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
            if(candidate_ensureMoveRightValidRule_node_bp == null) {
                MissingPreset_ensureMoveRightValidRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_ensureMoveRightValidRule_node_wv);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_ensureMoveRightValidRule_node_bp.type.TypeID!=1) {
                return matches;
            }
            // NegativePattern 
            {
                ++negLevel;
                uint prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                prev_neg_0__candidate_ensureMoveRightValidRule_node_bp = candidate_ensureMoveRightValidRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                candidate_ensureMoveRightValidRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                // Extend Outgoing ensureMoveRightValidRule_neg_0_edge__edge0 from ensureMoveRightValidRule_node_bp 
                GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = candidate_ensureMoveRightValidRule_node_bp.outhead;
                if(head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0;
                    do
                    {
                        if(candidate_ensureMoveRightValidRule_neg_0_edge__edge0.type.TypeID!=3) {
                            continue;
                        }
                        // Implicit Target ensureMoveRightValidRule_neg_0_node__node0 from ensureMoveRightValidRule_neg_0_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_neg_0_node__node0 = candidate_ensureMoveRightValidRule_neg_0_edge__edge0.target;
                        if(candidate_ensureMoveRightValidRule_neg_0_node__node0.type.TypeID!=1) {
                            continue;
                        }
                        if((candidate_ensureMoveRightValidRule_neg_0_node__node0.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                        {
                            continue;
                        }
                        // negative pattern found
                        candidate_ensureMoveRightValidRule_node_bp.flags = candidate_ensureMoveRightValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                        --negLevel;
                        return matches;
                    }
                    while( (candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = candidate_ensureMoveRightValidRule_neg_0_edge__edge0.outNext) != head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 );
                }
                candidate_ensureMoveRightValidRule_node_bp.flags = candidate_ensureMoveRightValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                --negLevel;
            }
            // Extend Outgoing ensureMoveRightValidRule_edge__edge0 from ensureMoveRightValidRule_node_wv 
            GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveRightValidRule_edge__edge0 = candidate_ensureMoveRightValidRule_node_wv.outhead;
            if(head_candidate_ensureMoveRightValidRule_edge__edge0 != null)
            {
                GRGEN_LGSP.LGSPEdge candidate_ensureMoveRightValidRule_edge__edge0 = head_candidate_ensureMoveRightValidRule_edge__edge0;
                do
                {
                    if(candidate_ensureMoveRightValidRule_edge__edge0.type.TypeID!=7) {
                        continue;
                    }
                    // Implicit Target ensureMoveRightValidRule_node__node0 from ensureMoveRightValidRule_edge__edge0 
                    GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node__node0 = candidate_ensureMoveRightValidRule_edge__edge0.target;
                    if(candidate_ensureMoveRightValidRule_node__node0.type.TypeID!=2) {
                        continue;
                    }
                    GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                    match.patternGraph = rulePattern.patternGraph;
                    match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@wv] = candidate_ensureMoveRightValidRule_node_wv;
                    match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@_node0] = candidate_ensureMoveRightValidRule_node__node0;
                    match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@bp] = candidate_ensureMoveRightValidRule_node_bp;
                    match.Edges[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_EdgeNums.@_edge0] = candidate_ensureMoveRightValidRule_edge__edge0;
                    matches.matchesList.PositionWasFilledFixIt();
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        candidate_ensureMoveRightValidRule_node_wv.MoveOutHeadAfter(candidate_ensureMoveRightValidRule_edge__edge0);
                        return matches;
                    }
                }
                while( (candidate_ensureMoveRightValidRule_edge__edge0 = candidate_ensureMoveRightValidRule_edge__edge0.outNext) != head_candidate_ensureMoveRightValidRule_edge__edge0 );
            }
            return matches;
        }
        public void MissingPreset_ensureMoveRightValidRule_node_wv(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList)
        {
            int negLevel = 0;
            // Lookup ensureMoveRightValidRule_node_wv 
            int type_id_candidate_ensureMoveRightValidRule_node_wv = 3;
            for(GRGEN_LGSP.LGSPNode head_candidate_ensureMoveRightValidRule_node_wv = graph.nodesByTypeHeads[type_id_candidate_ensureMoveRightValidRule_node_wv], candidate_ensureMoveRightValidRule_node_wv = head_candidate_ensureMoveRightValidRule_node_wv.typeNext; candidate_ensureMoveRightValidRule_node_wv != head_candidate_ensureMoveRightValidRule_node_wv; candidate_ensureMoveRightValidRule_node_wv = candidate_ensureMoveRightValidRule_node_wv.typeNext)
            {
                // Preset ensureMoveRightValidRule_node_bp 
                GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
                if(candidate_ensureMoveRightValidRule_node_bp == null) {
                    MissingPreset_ensureMoveRightValidRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_ensureMoveRightValidRule_node_wv);
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        return;
                    }
                    continue;
                }
                if(candidate_ensureMoveRightValidRule_node_bp.type.TypeID!=1) {
                    continue;
                }
                // NegativePattern 
                {
                    ++negLevel;
                    uint prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                    prev_neg_0__candidate_ensureMoveRightValidRule_node_bp = candidate_ensureMoveRightValidRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    candidate_ensureMoveRightValidRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    // Extend Outgoing ensureMoveRightValidRule_neg_0_edge__edge0 from ensureMoveRightValidRule_node_bp 
                    GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = candidate_ensureMoveRightValidRule_node_bp.outhead;
                    if(head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 != null)
                    {
                        GRGEN_LGSP.LGSPEdge candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0;
                        do
                        {
                            if(candidate_ensureMoveRightValidRule_neg_0_edge__edge0.type.TypeID!=3) {
                                continue;
                            }
                            // Implicit Target ensureMoveRightValidRule_neg_0_node__node0 from ensureMoveRightValidRule_neg_0_edge__edge0 
                            GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_neg_0_node__node0 = candidate_ensureMoveRightValidRule_neg_0_edge__edge0.target;
                            if(candidate_ensureMoveRightValidRule_neg_0_node__node0.type.TypeID!=1) {
                                continue;
                            }
                            if((candidate_ensureMoveRightValidRule_neg_0_node__node0.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                            {
                                continue;
                            }
                            // negative pattern found
                            candidate_ensureMoveRightValidRule_node_bp.flags = candidate_ensureMoveRightValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                            --negLevel;
                            goto label2;
                        }
                        while( (candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = candidate_ensureMoveRightValidRule_neg_0_edge__edge0.outNext) != head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 );
                    }
                    candidate_ensureMoveRightValidRule_node_bp.flags = candidate_ensureMoveRightValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                    --negLevel;
                }
                // Extend Outgoing ensureMoveRightValidRule_edge__edge0 from ensureMoveRightValidRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveRightValidRule_edge__edge0 = candidate_ensureMoveRightValidRule_node_wv.outhead;
                if(head_candidate_ensureMoveRightValidRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_ensureMoveRightValidRule_edge__edge0 = head_candidate_ensureMoveRightValidRule_edge__edge0;
                    do
                    {
                        if(candidate_ensureMoveRightValidRule_edge__edge0.type.TypeID!=7) {
                            continue;
                        }
                        // Implicit Target ensureMoveRightValidRule_node__node0 from ensureMoveRightValidRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node__node0 = candidate_ensureMoveRightValidRule_edge__edge0.target;
                        if(candidate_ensureMoveRightValidRule_node__node0.type.TypeID!=2) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@wv] = candidate_ensureMoveRightValidRule_node_wv;
                        match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@_node0] = candidate_ensureMoveRightValidRule_node__node0;
                        match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@bp] = candidate_ensureMoveRightValidRule_node_bp;
                        match.Edges[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_EdgeNums.@_edge0] = candidate_ensureMoveRightValidRule_edge__edge0;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_ensureMoveRightValidRule_node_wv.MoveOutHeadAfter(candidate_ensureMoveRightValidRule_edge__edge0);
                            graph.MoveHeadAfter(candidate_ensureMoveRightValidRule_node_wv);
                            return;
                        }
                    }
                    while( (candidate_ensureMoveRightValidRule_edge__edge0 = candidate_ensureMoveRightValidRule_edge__edge0.outNext) != head_candidate_ensureMoveRightValidRule_edge__edge0 );
                }
label2: ;
            }
            return;
        }
        public void MissingPreset_ensureMoveRightValidRule_node_bp(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList, GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node_wv)
        {
            int negLevel = 0;
            // Lookup ensureMoveRightValidRule_node_bp 
            int type_id_candidate_ensureMoveRightValidRule_node_bp = 1;
            for(GRGEN_LGSP.LGSPNode head_candidate_ensureMoveRightValidRule_node_bp = graph.nodesByTypeHeads[type_id_candidate_ensureMoveRightValidRule_node_bp], candidate_ensureMoveRightValidRule_node_bp = head_candidate_ensureMoveRightValidRule_node_bp.typeNext; candidate_ensureMoveRightValidRule_node_bp != head_candidate_ensureMoveRightValidRule_node_bp; candidate_ensureMoveRightValidRule_node_bp = candidate_ensureMoveRightValidRule_node_bp.typeNext)
            {
                // NegativePattern 
                {
                    ++negLevel;
                    uint prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                    prev_neg_0__candidate_ensureMoveRightValidRule_node_bp = candidate_ensureMoveRightValidRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    candidate_ensureMoveRightValidRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                    // Extend Outgoing ensureMoveRightValidRule_neg_0_edge__edge0 from ensureMoveRightValidRule_node_bp 
                    GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = candidate_ensureMoveRightValidRule_node_bp.outhead;
                    if(head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 != null)
                    {
                        GRGEN_LGSP.LGSPEdge candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0;
                        do
                        {
                            if(candidate_ensureMoveRightValidRule_neg_0_edge__edge0.type.TypeID!=3) {
                                continue;
                            }
                            // Implicit Target ensureMoveRightValidRule_neg_0_node__node0 from ensureMoveRightValidRule_neg_0_edge__edge0 
                            GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_neg_0_node__node0 = candidate_ensureMoveRightValidRule_neg_0_edge__edge0.target;
                            if(candidate_ensureMoveRightValidRule_neg_0_node__node0.type.TypeID!=1) {
                                continue;
                            }
                            if((candidate_ensureMoveRightValidRule_neg_0_node__node0.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                            {
                                continue;
                            }
                            // negative pattern found
                            candidate_ensureMoveRightValidRule_node_bp.flags = candidate_ensureMoveRightValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                            --negLevel;
                            goto label3;
                        }
                        while( (candidate_ensureMoveRightValidRule_neg_0_edge__edge0 = candidate_ensureMoveRightValidRule_neg_0_edge__edge0.outNext) != head_candidate_ensureMoveRightValidRule_neg_0_edge__edge0 );
                    }
                    candidate_ensureMoveRightValidRule_node_bp.flags = candidate_ensureMoveRightValidRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev_neg_0__candidate_ensureMoveRightValidRule_node_bp;
                    --negLevel;
                }
                // Extend Outgoing ensureMoveRightValidRule_edge__edge0 from ensureMoveRightValidRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_ensureMoveRightValidRule_edge__edge0 = candidate_ensureMoveRightValidRule_node_wv.outhead;
                if(head_candidate_ensureMoveRightValidRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_ensureMoveRightValidRule_edge__edge0 = head_candidate_ensureMoveRightValidRule_edge__edge0;
                    do
                    {
                        if(candidate_ensureMoveRightValidRule_edge__edge0.type.TypeID!=7) {
                            continue;
                        }
                        // Implicit Target ensureMoveRightValidRule_node__node0 from ensureMoveRightValidRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_ensureMoveRightValidRule_node__node0 = candidate_ensureMoveRightValidRule_edge__edge0.target;
                        if(candidate_ensureMoveRightValidRule_node__node0.type.TypeID!=2) {
                            continue;
                        }
                        GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                        match.patternGraph = rulePattern.patternGraph;
                        match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@wv] = candidate_ensureMoveRightValidRule_node_wv;
                        match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@_node0] = candidate_ensureMoveRightValidRule_node__node0;
                        match.Nodes[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_NodeNums.@bp] = candidate_ensureMoveRightValidRule_node_bp;
                        match.Edges[(int)Rule_ensureMoveRightValidRule.ensureMoveRightValidRule_EdgeNums.@_edge0] = candidate_ensureMoveRightValidRule_edge__edge0;
                        matches.matchesList.PositionWasFilledFixIt();
                        // if enough matches were found, we leave
                        if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                        {
                            candidate_ensureMoveRightValidRule_node_wv.MoveOutHeadAfter(candidate_ensureMoveRightValidRule_edge__edge0);
                            graph.MoveHeadAfter(candidate_ensureMoveRightValidRule_node_bp);
                            return;
                        }
                    }
                    while( (candidate_ensureMoveRightValidRule_edge__edge0 = candidate_ensureMoveRightValidRule_edge__edge0.outNext) != head_candidate_ensureMoveRightValidRule_edge__edge0 );
                }
label3: ;
            }
            return;
        }
    }

    public class Action_moveLeftRule : GRGEN_LGSP.LGSPAction
    {
        public Action_moveLeftRule() {
            rulePattern = Rule_moveLeftRule.Instance;
            patternGraph = rulePattern.patternGraph;
            DynamicMatch = myMatch;
            matches = new GRGEN_LGSP.LGSPMatches(this, 4, 2, 0, 0 + 0);
        }

        public override string Name { get { return "moveLeftRule"; } }
        private GRGEN_LGSP.LGSPMatches matches;

        public static GRGEN_LGSP.LGSPAction Instance { get { return instance; } }
        private static Action_moveLeftRule instance = new Action_moveLeftRule();

        public GRGEN_LGSP.LGSPMatches myMatch(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters)
        {
            matches.matchesList.Clear();
            int negLevel = 0;
            // Preset moveLeftRule_node_wv 
            GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_wv = (GRGEN_LGSP.LGSPNode) parameters[0];
            if(candidate_moveLeftRule_node_wv == null) {
                MissingPreset_moveLeftRule_node_wv(graph, maxMatches, parameters, null, null, null);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_moveLeftRule_node_wv.type.TypeID!=3) {
                return matches;
            }
            // Preset moveLeftRule_node_bp 
            GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
            if(candidate_moveLeftRule_node_bp == null) {
                MissingPreset_moveLeftRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_moveLeftRule_node_wv);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_moveLeftRule_node_bp.type.TypeID!=1) {
                return matches;
            }
            uint prev__candidate_moveLeftRule_node_bp;
            prev__candidate_moveLeftRule_node_bp = candidate_moveLeftRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
            candidate_moveLeftRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
            // Extend Outgoing moveLeftRule_edge__edge0 from moveLeftRule_node_wv 
            GRGEN_LGSP.LGSPEdge head_candidate_moveLeftRule_edge__edge0 = candidate_moveLeftRule_node_wv.outhead;
            if(head_candidate_moveLeftRule_edge__edge0 != null)
            {
                GRGEN_LGSP.LGSPEdge candidate_moveLeftRule_edge__edge0 = head_candidate_moveLeftRule_edge__edge0;
                do
                {
                    if(candidate_moveLeftRule_edge__edge0.type.TypeID!=6) {
                        continue;
                    }
                    // Implicit Target moveLeftRule_node_s from moveLeftRule_edge__edge0 
                    GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_s = candidate_moveLeftRule_edge__edge0.target;
                    if(candidate_moveLeftRule_node_s.type.TypeID!=2) {
                        continue;
                    }
                    // Extend Incoming moveLeftRule_edge__edge1 from moveLeftRule_node_bp 
                    GRGEN_LGSP.LGSPEdge head_candidate_moveLeftRule_edge__edge1 = candidate_moveLeftRule_node_bp.inhead;
                    if(head_candidate_moveLeftRule_edge__edge1 != null)
                    {
                        GRGEN_LGSP.LGSPEdge candidate_moveLeftRule_edge__edge1 = head_candidate_moveLeftRule_edge__edge1;
                        do
                        {
                            if(candidate_moveLeftRule_edge__edge1.type.TypeID!=3) {
                                continue;
                            }
                            // Implicit Source moveLeftRule_node_lbp from moveLeftRule_edge__edge1 
                            GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_lbp = candidate_moveLeftRule_edge__edge1.source;
                            if(candidate_moveLeftRule_node_lbp.type.TypeID!=1) {
                                continue;
                            }
                            if((candidate_moveLeftRule_node_lbp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                            {
                                continue;
                            }
                            GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                            match.patternGraph = rulePattern.patternGraph;
                            match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@wv] = candidate_moveLeftRule_node_wv;
                            match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@s] = candidate_moveLeftRule_node_s;
                            match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@lbp] = candidate_moveLeftRule_node_lbp;
                            match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@bp] = candidate_moveLeftRule_node_bp;
                            match.Edges[(int)Rule_moveLeftRule.moveLeftRule_EdgeNums.@_edge0] = candidate_moveLeftRule_edge__edge0;
                            match.Edges[(int)Rule_moveLeftRule.moveLeftRule_EdgeNums.@_edge1] = candidate_moveLeftRule_edge__edge1;
                            matches.matchesList.PositionWasFilledFixIt();
                            // if enough matches were found, we leave
                            if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                            {
                                candidate_moveLeftRule_node_bp.MoveInHeadAfter(candidate_moveLeftRule_edge__edge1);
                                candidate_moveLeftRule_node_wv.MoveOutHeadAfter(candidate_moveLeftRule_edge__edge0);
                                candidate_moveLeftRule_node_bp.flags = candidate_moveLeftRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveLeftRule_node_bp;
                                return matches;
                            }
                        }
                        while( (candidate_moveLeftRule_edge__edge1 = candidate_moveLeftRule_edge__edge1.inNext) != head_candidate_moveLeftRule_edge__edge1 );
                    }
                }
                while( (candidate_moveLeftRule_edge__edge0 = candidate_moveLeftRule_edge__edge0.outNext) != head_candidate_moveLeftRule_edge__edge0 );
            }
            candidate_moveLeftRule_node_bp.flags = candidate_moveLeftRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveLeftRule_node_bp;
            return matches;
        }
        public void MissingPreset_moveLeftRule_node_wv(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList)
        {
            int negLevel = 0;
            // Lookup moveLeftRule_node_wv 
            int type_id_candidate_moveLeftRule_node_wv = 3;
            for(GRGEN_LGSP.LGSPNode head_candidate_moveLeftRule_node_wv = graph.nodesByTypeHeads[type_id_candidate_moveLeftRule_node_wv], candidate_moveLeftRule_node_wv = head_candidate_moveLeftRule_node_wv.typeNext; candidate_moveLeftRule_node_wv != head_candidate_moveLeftRule_node_wv; candidate_moveLeftRule_node_wv = candidate_moveLeftRule_node_wv.typeNext)
            {
                // Preset moveLeftRule_node_bp 
                GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
                if(candidate_moveLeftRule_node_bp == null) {
                    MissingPreset_moveLeftRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_moveLeftRule_node_wv);
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        return;
                    }
                    continue;
                }
                if(candidate_moveLeftRule_node_bp.type.TypeID!=1) {
                    continue;
                }
                uint prev__candidate_moveLeftRule_node_bp;
                prev__candidate_moveLeftRule_node_bp = candidate_moveLeftRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                candidate_moveLeftRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                // Extend Outgoing moveLeftRule_edge__edge0 from moveLeftRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_moveLeftRule_edge__edge0 = candidate_moveLeftRule_node_wv.outhead;
                if(head_candidate_moveLeftRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_moveLeftRule_edge__edge0 = head_candidate_moveLeftRule_edge__edge0;
                    do
                    {
                        if(candidate_moveLeftRule_edge__edge0.type.TypeID!=6) {
                            continue;
                        }
                        // Implicit Target moveLeftRule_node_s from moveLeftRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_s = candidate_moveLeftRule_edge__edge0.target;
                        if(candidate_moveLeftRule_node_s.type.TypeID!=2) {
                            continue;
                        }
                        // Extend Incoming moveLeftRule_edge__edge1 from moveLeftRule_node_bp 
                        GRGEN_LGSP.LGSPEdge head_candidate_moveLeftRule_edge__edge1 = candidate_moveLeftRule_node_bp.inhead;
                        if(head_candidate_moveLeftRule_edge__edge1 != null)
                        {
                            GRGEN_LGSP.LGSPEdge candidate_moveLeftRule_edge__edge1 = head_candidate_moveLeftRule_edge__edge1;
                            do
                            {
                                if(candidate_moveLeftRule_edge__edge1.type.TypeID!=3) {
                                    continue;
                                }
                                // Implicit Source moveLeftRule_node_lbp from moveLeftRule_edge__edge1 
                                GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_lbp = candidate_moveLeftRule_edge__edge1.source;
                                if(candidate_moveLeftRule_node_lbp.type.TypeID!=1) {
                                    continue;
                                }
                                if((candidate_moveLeftRule_node_lbp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                                {
                                    continue;
                                }
                                GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                                match.patternGraph = rulePattern.patternGraph;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@wv] = candidate_moveLeftRule_node_wv;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@s] = candidate_moveLeftRule_node_s;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@lbp] = candidate_moveLeftRule_node_lbp;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@bp] = candidate_moveLeftRule_node_bp;
                                match.Edges[(int)Rule_moveLeftRule.moveLeftRule_EdgeNums.@_edge0] = candidate_moveLeftRule_edge__edge0;
                                match.Edges[(int)Rule_moveLeftRule.moveLeftRule_EdgeNums.@_edge1] = candidate_moveLeftRule_edge__edge1;
                                matches.matchesList.PositionWasFilledFixIt();
                                // if enough matches were found, we leave
                                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                                {
                                    candidate_moveLeftRule_node_bp.MoveInHeadAfter(candidate_moveLeftRule_edge__edge1);
                                    candidate_moveLeftRule_node_wv.MoveOutHeadAfter(candidate_moveLeftRule_edge__edge0);
                                    graph.MoveHeadAfter(candidate_moveLeftRule_node_wv);
                                    candidate_moveLeftRule_node_bp.flags = candidate_moveLeftRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveLeftRule_node_bp;
                                    return;
                                }
                            }
                            while( (candidate_moveLeftRule_edge__edge1 = candidate_moveLeftRule_edge__edge1.inNext) != head_candidate_moveLeftRule_edge__edge1 );
                        }
                    }
                    while( (candidate_moveLeftRule_edge__edge0 = candidate_moveLeftRule_edge__edge0.outNext) != head_candidate_moveLeftRule_edge__edge0 );
                }
                candidate_moveLeftRule_node_bp.flags = candidate_moveLeftRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveLeftRule_node_bp;
            }
            return;
        }
        public void MissingPreset_moveLeftRule_node_bp(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList, GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_wv)
        {
            int negLevel = 0;
            // Lookup moveLeftRule_node_bp 
            int type_id_candidate_moveLeftRule_node_bp = 1;
            for(GRGEN_LGSP.LGSPNode head_candidate_moveLeftRule_node_bp = graph.nodesByTypeHeads[type_id_candidate_moveLeftRule_node_bp], candidate_moveLeftRule_node_bp = head_candidate_moveLeftRule_node_bp.typeNext; candidate_moveLeftRule_node_bp != head_candidate_moveLeftRule_node_bp; candidate_moveLeftRule_node_bp = candidate_moveLeftRule_node_bp.typeNext)
            {
                uint prev__candidate_moveLeftRule_node_bp;
                prev__candidate_moveLeftRule_node_bp = candidate_moveLeftRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                candidate_moveLeftRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                // Extend Outgoing moveLeftRule_edge__edge0 from moveLeftRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_moveLeftRule_edge__edge0 = candidate_moveLeftRule_node_wv.outhead;
                if(head_candidate_moveLeftRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_moveLeftRule_edge__edge0 = head_candidate_moveLeftRule_edge__edge0;
                    do
                    {
                        if(candidate_moveLeftRule_edge__edge0.type.TypeID!=6) {
                            continue;
                        }
                        // Implicit Target moveLeftRule_node_s from moveLeftRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_s = candidate_moveLeftRule_edge__edge0.target;
                        if(candidate_moveLeftRule_node_s.type.TypeID!=2) {
                            continue;
                        }
                        // Extend Incoming moveLeftRule_edge__edge1 from moveLeftRule_node_bp 
                        GRGEN_LGSP.LGSPEdge head_candidate_moveLeftRule_edge__edge1 = candidate_moveLeftRule_node_bp.inhead;
                        if(head_candidate_moveLeftRule_edge__edge1 != null)
                        {
                            GRGEN_LGSP.LGSPEdge candidate_moveLeftRule_edge__edge1 = head_candidate_moveLeftRule_edge__edge1;
                            do
                            {
                                if(candidate_moveLeftRule_edge__edge1.type.TypeID!=3) {
                                    continue;
                                }
                                // Implicit Source moveLeftRule_node_lbp from moveLeftRule_edge__edge1 
                                GRGEN_LGSP.LGSPNode candidate_moveLeftRule_node_lbp = candidate_moveLeftRule_edge__edge1.source;
                                if(candidate_moveLeftRule_node_lbp.type.TypeID!=1) {
                                    continue;
                                }
                                if((candidate_moveLeftRule_node_lbp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                                {
                                    continue;
                                }
                                GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                                match.patternGraph = rulePattern.patternGraph;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@wv] = candidate_moveLeftRule_node_wv;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@s] = candidate_moveLeftRule_node_s;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@lbp] = candidate_moveLeftRule_node_lbp;
                                match.Nodes[(int)Rule_moveLeftRule.moveLeftRule_NodeNums.@bp] = candidate_moveLeftRule_node_bp;
                                match.Edges[(int)Rule_moveLeftRule.moveLeftRule_EdgeNums.@_edge0] = candidate_moveLeftRule_edge__edge0;
                                match.Edges[(int)Rule_moveLeftRule.moveLeftRule_EdgeNums.@_edge1] = candidate_moveLeftRule_edge__edge1;
                                matches.matchesList.PositionWasFilledFixIt();
                                // if enough matches were found, we leave
                                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                                {
                                    candidate_moveLeftRule_node_bp.MoveInHeadAfter(candidate_moveLeftRule_edge__edge1);
                                    candidate_moveLeftRule_node_wv.MoveOutHeadAfter(candidate_moveLeftRule_edge__edge0);
                                    graph.MoveHeadAfter(candidate_moveLeftRule_node_bp);
                                    candidate_moveLeftRule_node_bp.flags = candidate_moveLeftRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveLeftRule_node_bp;
                                    return;
                                }
                            }
                            while( (candidate_moveLeftRule_edge__edge1 = candidate_moveLeftRule_edge__edge1.inNext) != head_candidate_moveLeftRule_edge__edge1 );
                        }
                    }
                    while( (candidate_moveLeftRule_edge__edge0 = candidate_moveLeftRule_edge__edge0.outNext) != head_candidate_moveLeftRule_edge__edge0 );
                }
                candidate_moveLeftRule_node_bp.flags = candidate_moveLeftRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveLeftRule_node_bp;
            }
            return;
        }
    }

    public class Action_moveRightRule : GRGEN_LGSP.LGSPAction
    {
        public Action_moveRightRule() {
            rulePattern = Rule_moveRightRule.Instance;
            patternGraph = rulePattern.patternGraph;
            DynamicMatch = myMatch;
            matches = new GRGEN_LGSP.LGSPMatches(this, 4, 2, 0, 0 + 0);
        }

        public override string Name { get { return "moveRightRule"; } }
        private GRGEN_LGSP.LGSPMatches matches;

        public static GRGEN_LGSP.LGSPAction Instance { get { return instance; } }
        private static Action_moveRightRule instance = new Action_moveRightRule();

        public GRGEN_LGSP.LGSPMatches myMatch(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters)
        {
            matches.matchesList.Clear();
            int negLevel = 0;
            // Preset moveRightRule_node_wv 
            GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_wv = (GRGEN_LGSP.LGSPNode) parameters[0];
            if(candidate_moveRightRule_node_wv == null) {
                MissingPreset_moveRightRule_node_wv(graph, maxMatches, parameters, null, null, null);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_moveRightRule_node_wv.type.TypeID!=3) {
                return matches;
            }
            // Preset moveRightRule_node_bp 
            GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
            if(candidate_moveRightRule_node_bp == null) {
                MissingPreset_moveRightRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_moveRightRule_node_wv);
                // if enough matches were found, we leave
                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                {
                    return matches;
                }
                return matches;
            }
            if(candidate_moveRightRule_node_bp.type.TypeID!=1) {
                return matches;
            }
            uint prev__candidate_moveRightRule_node_bp;
            prev__candidate_moveRightRule_node_bp = candidate_moveRightRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
            candidate_moveRightRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
            // Extend Outgoing moveRightRule_edge__edge0 from moveRightRule_node_wv 
            GRGEN_LGSP.LGSPEdge head_candidate_moveRightRule_edge__edge0 = candidate_moveRightRule_node_wv.outhead;
            if(head_candidate_moveRightRule_edge__edge0 != null)
            {
                GRGEN_LGSP.LGSPEdge candidate_moveRightRule_edge__edge0 = head_candidate_moveRightRule_edge__edge0;
                do
                {
                    if(candidate_moveRightRule_edge__edge0.type.TypeID!=7) {
                        continue;
                    }
                    // Implicit Target moveRightRule_node_s from moveRightRule_edge__edge0 
                    GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_s = candidate_moveRightRule_edge__edge0.target;
                    if(candidate_moveRightRule_node_s.type.TypeID!=2) {
                        continue;
                    }
                    // Extend Outgoing moveRightRule_edge__edge1 from moveRightRule_node_bp 
                    GRGEN_LGSP.LGSPEdge head_candidate_moveRightRule_edge__edge1 = candidate_moveRightRule_node_bp.outhead;
                    if(head_candidate_moveRightRule_edge__edge1 != null)
                    {
                        GRGEN_LGSP.LGSPEdge candidate_moveRightRule_edge__edge1 = head_candidate_moveRightRule_edge__edge1;
                        do
                        {
                            if(candidate_moveRightRule_edge__edge1.type.TypeID!=3) {
                                continue;
                            }
                            // Implicit Target moveRightRule_node_rbp from moveRightRule_edge__edge1 
                            GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_rbp = candidate_moveRightRule_edge__edge1.target;
                            if(candidate_moveRightRule_node_rbp.type.TypeID!=1) {
                                continue;
                            }
                            if((candidate_moveRightRule_node_rbp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                            {
                                continue;
                            }
                            GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                            match.patternGraph = rulePattern.patternGraph;
                            match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@wv] = candidate_moveRightRule_node_wv;
                            match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@s] = candidate_moveRightRule_node_s;
                            match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@bp] = candidate_moveRightRule_node_bp;
                            match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@rbp] = candidate_moveRightRule_node_rbp;
                            match.Edges[(int)Rule_moveRightRule.moveRightRule_EdgeNums.@_edge0] = candidate_moveRightRule_edge__edge0;
                            match.Edges[(int)Rule_moveRightRule.moveRightRule_EdgeNums.@_edge1] = candidate_moveRightRule_edge__edge1;
                            matches.matchesList.PositionWasFilledFixIt();
                            // if enough matches were found, we leave
                            if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                            {
                                candidate_moveRightRule_node_bp.MoveOutHeadAfter(candidate_moveRightRule_edge__edge1);
                                candidate_moveRightRule_node_wv.MoveOutHeadAfter(candidate_moveRightRule_edge__edge0);
                                candidate_moveRightRule_node_bp.flags = candidate_moveRightRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveRightRule_node_bp;
                                return matches;
                            }
                        }
                        while( (candidate_moveRightRule_edge__edge1 = candidate_moveRightRule_edge__edge1.outNext) != head_candidate_moveRightRule_edge__edge1 );
                    }
                }
                while( (candidate_moveRightRule_edge__edge0 = candidate_moveRightRule_edge__edge0.outNext) != head_candidate_moveRightRule_edge__edge0 );
            }
            candidate_moveRightRule_node_bp.flags = candidate_moveRightRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveRightRule_node_bp;
            return matches;
        }
        public void MissingPreset_moveRightRule_node_wv(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList)
        {
            int negLevel = 0;
            // Lookup moveRightRule_node_wv 
            int type_id_candidate_moveRightRule_node_wv = 3;
            for(GRGEN_LGSP.LGSPNode head_candidate_moveRightRule_node_wv = graph.nodesByTypeHeads[type_id_candidate_moveRightRule_node_wv], candidate_moveRightRule_node_wv = head_candidate_moveRightRule_node_wv.typeNext; candidate_moveRightRule_node_wv != head_candidate_moveRightRule_node_wv; candidate_moveRightRule_node_wv = candidate_moveRightRule_node_wv.typeNext)
            {
                // Preset moveRightRule_node_bp 
                GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_bp = (GRGEN_LGSP.LGSPNode) parameters[1];
                if(candidate_moveRightRule_node_bp == null) {
                    MissingPreset_moveRightRule_node_bp(graph, maxMatches, parameters, null, null, null, candidate_moveRightRule_node_wv);
                    // if enough matches were found, we leave
                    if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                    {
                        return;
                    }
                    continue;
                }
                if(candidate_moveRightRule_node_bp.type.TypeID!=1) {
                    continue;
                }
                uint prev__candidate_moveRightRule_node_bp;
                prev__candidate_moveRightRule_node_bp = candidate_moveRightRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                candidate_moveRightRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                // Extend Outgoing moveRightRule_edge__edge0 from moveRightRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_moveRightRule_edge__edge0 = candidate_moveRightRule_node_wv.outhead;
                if(head_candidate_moveRightRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_moveRightRule_edge__edge0 = head_candidate_moveRightRule_edge__edge0;
                    do
                    {
                        if(candidate_moveRightRule_edge__edge0.type.TypeID!=7) {
                            continue;
                        }
                        // Implicit Target moveRightRule_node_s from moveRightRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_s = candidate_moveRightRule_edge__edge0.target;
                        if(candidate_moveRightRule_node_s.type.TypeID!=2) {
                            continue;
                        }
                        // Extend Outgoing moveRightRule_edge__edge1 from moveRightRule_node_bp 
                        GRGEN_LGSP.LGSPEdge head_candidate_moveRightRule_edge__edge1 = candidate_moveRightRule_node_bp.outhead;
                        if(head_candidate_moveRightRule_edge__edge1 != null)
                        {
                            GRGEN_LGSP.LGSPEdge candidate_moveRightRule_edge__edge1 = head_candidate_moveRightRule_edge__edge1;
                            do
                            {
                                if(candidate_moveRightRule_edge__edge1.type.TypeID!=3) {
                                    continue;
                                }
                                // Implicit Target moveRightRule_node_rbp from moveRightRule_edge__edge1 
                                GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_rbp = candidate_moveRightRule_edge__edge1.target;
                                if(candidate_moveRightRule_node_rbp.type.TypeID!=1) {
                                    continue;
                                }
                                if((candidate_moveRightRule_node_rbp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                                {
                                    continue;
                                }
                                GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                                match.patternGraph = rulePattern.patternGraph;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@wv] = candidate_moveRightRule_node_wv;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@s] = candidate_moveRightRule_node_s;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@bp] = candidate_moveRightRule_node_bp;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@rbp] = candidate_moveRightRule_node_rbp;
                                match.Edges[(int)Rule_moveRightRule.moveRightRule_EdgeNums.@_edge0] = candidate_moveRightRule_edge__edge0;
                                match.Edges[(int)Rule_moveRightRule.moveRightRule_EdgeNums.@_edge1] = candidate_moveRightRule_edge__edge1;
                                matches.matchesList.PositionWasFilledFixIt();
                                // if enough matches were found, we leave
                                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                                {
                                    candidate_moveRightRule_node_bp.MoveOutHeadAfter(candidate_moveRightRule_edge__edge1);
                                    candidate_moveRightRule_node_wv.MoveOutHeadAfter(candidate_moveRightRule_edge__edge0);
                                    graph.MoveHeadAfter(candidate_moveRightRule_node_wv);
                                    candidate_moveRightRule_node_bp.flags = candidate_moveRightRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveRightRule_node_bp;
                                    return;
                                }
                            }
                            while( (candidate_moveRightRule_edge__edge1 = candidate_moveRightRule_edge__edge1.outNext) != head_candidate_moveRightRule_edge__edge1 );
                        }
                    }
                    while( (candidate_moveRightRule_edge__edge0 = candidate_moveRightRule_edge__edge0.outNext) != head_candidate_moveRightRule_edge__edge0 );
                }
                candidate_moveRightRule_node_bp.flags = candidate_moveRightRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveRightRule_node_bp;
            }
            return;
        }
        public void MissingPreset_moveRightRule_node_bp(GRGEN_LGSP.LGSPGraph graph, int maxMatches, object[] parameters, Stack<GRGEN_LGSP.LGSPSubpatternAction> openTasks, List<Stack<GRGEN_LGSP.LGSPMatch>> foundPartialMatches, List<Stack<GRGEN_LGSP.LGSPMatch>> matchesList, GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_wv)
        {
            int negLevel = 0;
            // Lookup moveRightRule_node_bp 
            int type_id_candidate_moveRightRule_node_bp = 1;
            for(GRGEN_LGSP.LGSPNode head_candidate_moveRightRule_node_bp = graph.nodesByTypeHeads[type_id_candidate_moveRightRule_node_bp], candidate_moveRightRule_node_bp = head_candidate_moveRightRule_node_bp.typeNext; candidate_moveRightRule_node_bp != head_candidate_moveRightRule_node_bp; candidate_moveRightRule_node_bp = candidate_moveRightRule_node_bp.typeNext)
            {
                uint prev__candidate_moveRightRule_node_bp;
                prev__candidate_moveRightRule_node_bp = candidate_moveRightRule_node_bp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                candidate_moveRightRule_node_bp.flags |= (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel;
                // Extend Outgoing moveRightRule_edge__edge0 from moveRightRule_node_wv 
                GRGEN_LGSP.LGSPEdge head_candidate_moveRightRule_edge__edge0 = candidate_moveRightRule_node_wv.outhead;
                if(head_candidate_moveRightRule_edge__edge0 != null)
                {
                    GRGEN_LGSP.LGSPEdge candidate_moveRightRule_edge__edge0 = head_candidate_moveRightRule_edge__edge0;
                    do
                    {
                        if(candidate_moveRightRule_edge__edge0.type.TypeID!=7) {
                            continue;
                        }
                        // Implicit Target moveRightRule_node_s from moveRightRule_edge__edge0 
                        GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_s = candidate_moveRightRule_edge__edge0.target;
                        if(candidate_moveRightRule_node_s.type.TypeID!=2) {
                            continue;
                        }
                        // Extend Outgoing moveRightRule_edge__edge1 from moveRightRule_node_bp 
                        GRGEN_LGSP.LGSPEdge head_candidate_moveRightRule_edge__edge1 = candidate_moveRightRule_node_bp.outhead;
                        if(head_candidate_moveRightRule_edge__edge1 != null)
                        {
                            GRGEN_LGSP.LGSPEdge candidate_moveRightRule_edge__edge1 = head_candidate_moveRightRule_edge__edge1;
                            do
                            {
                                if(candidate_moveRightRule_edge__edge1.type.TypeID!=3) {
                                    continue;
                                }
                                // Implicit Target moveRightRule_node_rbp from moveRightRule_edge__edge1 
                                GRGEN_LGSP.LGSPNode candidate_moveRightRule_node_rbp = candidate_moveRightRule_edge__edge1.target;
                                if(candidate_moveRightRule_node_rbp.type.TypeID!=1) {
                                    continue;
                                }
                                if((candidate_moveRightRule_node_rbp.flags & (uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) != 0)
                                {
                                    continue;
                                }
                                GRGEN_LGSP.LGSPMatch match = matches.matchesList.GetNextUnfilledPosition();
                                match.patternGraph = rulePattern.patternGraph;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@wv] = candidate_moveRightRule_node_wv;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@s] = candidate_moveRightRule_node_s;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@bp] = candidate_moveRightRule_node_bp;
                                match.Nodes[(int)Rule_moveRightRule.moveRightRule_NodeNums.@rbp] = candidate_moveRightRule_node_rbp;
                                match.Edges[(int)Rule_moveRightRule.moveRightRule_EdgeNums.@_edge0] = candidate_moveRightRule_edge__edge0;
                                match.Edges[(int)Rule_moveRightRule.moveRightRule_EdgeNums.@_edge1] = candidate_moveRightRule_edge__edge1;
                                matches.matchesList.PositionWasFilledFixIt();
                                // if enough matches were found, we leave
                                if(maxMatches > 0 && matches.matchesList.Count >= maxMatches)
                                {
                                    candidate_moveRightRule_node_bp.MoveOutHeadAfter(candidate_moveRightRule_edge__edge1);
                                    candidate_moveRightRule_node_wv.MoveOutHeadAfter(candidate_moveRightRule_edge__edge0);
                                    graph.MoveHeadAfter(candidate_moveRightRule_node_bp);
                                    candidate_moveRightRule_node_bp.flags = candidate_moveRightRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveRightRule_node_bp;
                                    return;
                                }
                            }
                            while( (candidate_moveRightRule_edge__edge1 = candidate_moveRightRule_edge__edge1.outNext) != head_candidate_moveRightRule_edge__edge1 );
                        }
                    }
                    while( (candidate_moveRightRule_edge__edge0 = candidate_moveRightRule_edge__edge0.outNext) != head_candidate_moveRightRule_edge__edge0 );
                }
                candidate_moveRightRule_node_bp.flags = candidate_moveRightRule_node_bp.flags & ~((uint) GRGEN_LGSP.LGSPElemFlags.IS_MATCHED << negLevel) | prev__candidate_moveRightRule_node_bp;
            }
            return;
        }
    }


    public class Turing3Actions : de.unika.ipd.grGen.lgsp.LGSPActions
    {
        public Turing3Actions(de.unika.ipd.grGen.lgsp.LGSPGraph lgspgraph, String modelAsmName, String actionsAsmName)
            : base(lgspgraph, modelAsmName, actionsAsmName)
        {
            InitActions();
        }

        public Turing3Actions(de.unika.ipd.grGen.lgsp.LGSPGraph lgspgraph)
            : base(lgspgraph)
        {
            InitActions();
        }

        private void InitActions()
        {
            actions.Add("readZeroRule", (de.unika.ipd.grGen.lgsp.LGSPAction) Action_readZeroRule.Instance);
            actions.Add("readOneRule", (de.unika.ipd.grGen.lgsp.LGSPAction) Action_readOneRule.Instance);
            actions.Add("ensureMoveLeftValidRule", (de.unika.ipd.grGen.lgsp.LGSPAction) Action_ensureMoveLeftValidRule.Instance);
            actions.Add("ensureMoveRightValidRule", (de.unika.ipd.grGen.lgsp.LGSPAction) Action_ensureMoveRightValidRule.Instance);
            actions.Add("moveLeftRule", (de.unika.ipd.grGen.lgsp.LGSPAction) Action_moveLeftRule.Instance);
            actions.Add("moveRightRule", (de.unika.ipd.grGen.lgsp.LGSPAction) Action_moveRightRule.Instance);
        }

        public override String Name { get { return "Turing3Actions"; } }
        public override String ModelMD5Hash { get { return "3f4f1e3e3ccd5475eeca1ab5c25802bc"; } }
    }
}