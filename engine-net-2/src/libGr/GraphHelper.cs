/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 3.5
 * Copyright (C) 2003-2012 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

using System;
using System.Collections.Generic;

// this is not related in any way to IGraphHelpers.cs

namespace de.unika.ipd.grGen.libGr
{
    public class GraphHelper
    {
        /// <summary>
        /// Returns set of nodes adjacent to the source node, under the type constraints given
        /// </summary>
        public static IDictionary<INode, SetValueType> Adjacent(INode sourceNode, EdgeType incidentEdgeType, NodeType adjacentNodeType)
        {
            Dictionary<INode, SetValueType> adjacentNodesSet = new Dictionary<INode,SetValueType>();
            foreach(IEdge edge in sourceNode.GetCompatibleOutgoing(incidentEdgeType))
            {
                INode adjacentNode = edge.Target;
                if(!adjacentNode.InstanceOf(adjacentNodeType))
                    continue;
                adjacentNodesSet[adjacentNode] = null;
            }
            foreach(IEdge edge in sourceNode.GetCompatibleIncoming(incidentEdgeType))
            {
                INode adjacentNode = edge.Source;
                if(!adjacentNode.InstanceOf(adjacentNodeType))
                    continue;
                adjacentNodesSet[adjacentNode] = null;
            }
            return adjacentNodesSet;
        }

        /// <summary>
        /// Returns set of nodes adjacent to the source node via outgoing edges, under the type constraints given
        /// </summary>
        public static IDictionary<INode, SetValueType> AdjacentOutgoing(INode sourceNode, EdgeType incidentEdgeType, NodeType adjacentNodeType)
        {
            Dictionary<INode, SetValueType> adjacentNodesSet = new Dictionary<INode, SetValueType>();
            foreach(IEdge edge in sourceNode.GetCompatibleOutgoing(incidentEdgeType))
            {
                INode adjacentNode = edge.Target;
                if(!adjacentNode.InstanceOf(adjacentNodeType))
                    continue;
                adjacentNodesSet[adjacentNode] = null;
            }
            return adjacentNodesSet;
        }

        /// <summary>
        /// Returns set of nodes adjacent to the source node via incoming edges, under the type constraints given
        /// </summary>
        public static IDictionary<INode, SetValueType> AdjacentIncoming(INode sourceNode, EdgeType incidentEdgeType, NodeType adjacentNodeType)
        {
            Dictionary<INode, SetValueType> adjacentNodesSet = new Dictionary<INode, SetValueType>();
            foreach(IEdge edge in sourceNode.GetCompatibleIncoming(incidentEdgeType))
            {
                INode adjacentNode = edge.Source;
                if(!adjacentNode.InstanceOf(adjacentNodeType))
                    continue;
                adjacentNodesSet[adjacentNode] = null;
            }
            return adjacentNodesSet;
        }

        /// <summary>
        /// Returns set of edges incident to the source node, under the type constraints given
        /// </summary>
        public static IDictionary<IEdge, SetValueType> Incident(INode node, EdgeType edgeType, NodeType adjacentNodeType)
        {
            Dictionary<IEdge, SetValueType> incidentEdgeSet = new Dictionary<IEdge, SetValueType>();
            foreach(IEdge edge in node.GetCompatibleOutgoing(edgeType))
            {
                INode adjacentNode = edge.Target;
                if(!adjacentNode.InstanceOf(adjacentNodeType))
                    continue;
                incidentEdgeSet[edge] = null;
            }
            foreach(IEdge edge in node.GetCompatibleIncoming(edgeType))
            {
                INode adjacentNode = edge.Source;
                if(!adjacentNode.InstanceOf(adjacentNodeType))
                    continue;
                incidentEdgeSet[edge] = null;
            }
            return incidentEdgeSet;
        }

        /// <summary>
        /// Returns set of edges outgoing from the source node, under the type constraints given
        /// </summary>
        public static IDictionary<IEdge, SetValueType> Outgoing(INode node, EdgeType edgeType, NodeType targetNodeType)
        {
            Dictionary<IEdge, SetValueType> incidentEdgeSet = new Dictionary<IEdge, SetValueType>();
            foreach(IEdge edge in node.GetCompatibleOutgoing(edgeType))
            {
                INode adjacentNode = edge.Target;
                if(!adjacentNode.InstanceOf(targetNodeType))
                    continue;
                incidentEdgeSet[edge] = null;
            }
            return incidentEdgeSet;
        }

        /// <summary>
        /// Returns set of edges incoming to the source node, under the type constraints given
        /// </summary>
        public static IDictionary<IEdge, SetValueType> Incoming(INode node, EdgeType edgeType, NodeType sourceNodeType)
        {
            Dictionary<IEdge, SetValueType> incidentEdgeSet = new Dictionary<IEdge, SetValueType>();
            foreach(IEdge edge in node.GetCompatibleIncoming(edgeType))
            {
                INode adjacentNode = edge.Source;
                if(!adjacentNode.InstanceOf(sourceNodeType))
                    continue;
                incidentEdgeSet[edge] = null;
            }
            return incidentEdgeSet;
        }

        /// <summary>
        /// Returns the induced subgraph of the given node set
        /// </summary>
        public static IGraph InducedSubgraph(IDictionary<INode, SetValueType> nodeSet, IGraph graph)
        {
            IGraph inducedGraph = graph.CreateEmptyEquivalent("induced_from_" + graph.Name);
            IDictionary<INode, INode> nodeToCloned = new Dictionary<INode, INode>(nodeSet.Count);
            foreach(KeyValuePair<INode, SetValueType> nodeEntry in nodeSet)
            {
                INode node = nodeEntry.Key;
                INode clone = node.Clone();
                nodeToCloned.Add(node, clone);
                inducedGraph.AddNode(clone);
            }
            //graph.Check();
            //inducedGraph.Check();

            foreach(KeyValuePair<INode, SetValueType> nodeEntry in nodeSet)
            {
                INode node = nodeEntry.Key;
                foreach(IEdge edge in node.Outgoing)
                {
                    if(nodeToCloned.ContainsKey(edge.Target))
                    {
                        IEdge clone = edge.Clone(nodeToCloned[node], nodeToCloned[edge.Target]);
                        inducedGraph.AddEdge(clone);
                    }
                }
            }
            //graph.Check();
            //inducedGraph.Check();
            
            return inducedGraph;
        }

        /// <summary>
        /// Returns the edge induced/defined subgraph of the given edge set
        /// </summary>
        public static IGraph DefinedSubgraph(IDictionary<IEdge, SetValueType> edgeSet, IGraph graph)
        {
            IGraph definedGraph = graph.CreateEmptyEquivalent("defined_from_" + graph.Name);
            IDictionary<INode, INode> nodeToCloned = new Dictionary<INode, INode>(edgeSet.Count*2);
            foreach(KeyValuePair<IEdge, SetValueType> edgeEntry in edgeSet)
            {
                IEdge edge = edgeEntry.Key;
                if(!nodeToCloned.ContainsKey(edge.Source))
                {
                    INode clone = edge.Source.Clone();
                    nodeToCloned.Add(edge.Source, clone);
                    definedGraph.AddNode(clone);

                }
                if(!nodeToCloned.ContainsKey(edge.Target))
                {
                    INode clone = edge.Target.Clone();
                    nodeToCloned.Add(edge.Target, clone);
                    definedGraph.AddNode(clone);
                }
            }
            //graph.Check();
            //definedGraph.Check();

            foreach(KeyValuePair<IEdge, SetValueType> edgeEntry in edgeSet)
            {
                IEdge edge = edgeEntry.Key;
                IEdge clone = edge.Clone(nodeToCloned[edge.Source], nodeToCloned[edge.Target]);
                definedGraph.AddEdge(clone);
            }
            //graph.Check();
            //definedGraph.Check();

            return definedGraph;
        }

        /// <summary>
        /// Inserts a copy of the induced subgraph of the given node set to the graph
        /// returns the copy of the dedicated root node
        /// the root node is processed as if it was in the given node set even if it isn't
        /// </summary>
        public static INode InsertInduced(IDictionary<INode, SetValueType> nodeSet, INode rootNode, IGraph graph)
        {
            IDictionary<INode, INode> nodeToCloned = new Dictionary<INode, INode>(nodeSet.Count+1);
            foreach(KeyValuePair<INode, SetValueType> nodeEntry in nodeSet)
            {
                INode node = nodeEntry.Key;
                INode clone = node.Clone();
                nodeToCloned.Add(node, clone);
                graph.AddNode(clone);
            }
            if(!nodeSet.ContainsKey(rootNode))
            {
                INode clone = rootNode.Clone();
                nodeToCloned.Add(rootNode, clone);
                graph.AddNode(clone);
            }
            //graph.Check();

            foreach(KeyValuePair<INode, SetValueType> nodeEntry in nodeSet)
            {
                INode node = nodeEntry.Key;
                foreach(IEdge edge in node.Outgoing)
                {
                    if(nodeToCloned.ContainsKey(edge.Target))
                    {
                        IEdge clone = edge.Clone(nodeToCloned[node], nodeToCloned[edge.Target]);
                        graph.AddEdge(clone);
                    }
                }
            }
            if(!nodeSet.ContainsKey(rootNode))
            {
                foreach(IEdge edge in rootNode.Outgoing)
                {
                    if(nodeToCloned.ContainsKey(edge.Target))
                    {
                        IEdge clone = edge.Clone(nodeToCloned[rootNode], nodeToCloned[edge.Target]);
                        graph.AddEdge(clone);
                    }
                }
                foreach(IEdge edge in rootNode.Incoming)
                {
                    if(nodeToCloned.ContainsKey(edge.Source) && edge.Source!=rootNode)
                    {
                        IEdge clone = edge.Clone(nodeToCloned[edge.Source], nodeToCloned[rootNode]);
                        graph.AddEdge(clone);
                    }
                }
            }
            //graph.Check();

            return nodeToCloned[rootNode];
        }

        /// <summary>
        /// Inserts a copy of the edge induced/defined subgraph of the given edge set to the graph
        /// returns the copy of the dedicated root edge
        /// the root edge is processed as if it was in the given edge set even if it isn't
        /// </summary>
        public static IEdge InsertDefined(IDictionary<IEdge, SetValueType> edgeSet, IEdge rootEdge, IGraph graph)
        {
            IDictionary<INode, INode> nodeToCloned = new Dictionary<INode, INode>(edgeSet.Count*2 + 1);
            foreach(KeyValuePair<IEdge, SetValueType> edgeEntry in edgeSet)
            {
                IEdge edge = edgeEntry.Key;
                if(!nodeToCloned.ContainsKey(edge.Source))
                {
                    INode clone = edge.Source.Clone();
                    nodeToCloned.Add(edge.Source, clone);
                    graph.AddNode(clone);

                }
                if(!nodeToCloned.ContainsKey(edge.Target))
                {
                    INode clone = edge.Target.Clone();
                    nodeToCloned.Add(edge.Target, clone);
                    graph.AddNode(clone);
                }
            }
            if(!edgeSet.ContainsKey(rootEdge))
            {
                if(!nodeToCloned.ContainsKey(rootEdge.Source))
                {
                    INode clone = rootEdge.Source.Clone();
                    nodeToCloned.Add(rootEdge.Source, clone);
                    graph.AddNode(clone);

                }
                if(!nodeToCloned.ContainsKey(rootEdge.Target))
                {
                    INode clone = rootEdge.Target.Clone();
                    nodeToCloned.Add(rootEdge.Target, clone);
                    graph.AddNode(clone);
                }
            }
            //graph.Check();

            IEdge clonedEdge = null;
            if(edgeSet.ContainsKey(rootEdge))
            {
                foreach(KeyValuePair<IEdge, SetValueType> edgeEntry in edgeSet)
                {
                    IEdge edge = edgeEntry.Key;
                    IEdge clone = edge.Clone(nodeToCloned[edge.Source], nodeToCloned[edge.Target]);
                    graph.AddEdge(clone);
                    if(edge == rootEdge)
                        clonedEdge = clone;
                }
            }
            else
            {
                foreach(KeyValuePair<IEdge, SetValueType> edgeEntry in edgeSet)
                {
                    IEdge edge = edgeEntry.Key;
                    IEdge clone = edge.Clone(nodeToCloned[edge.Source], nodeToCloned[edge.Target]);
                    graph.AddEdge(clone);
                }

                IEdge rootClone = rootEdge.Clone(nodeToCloned[rootEdge.Source], nodeToCloned[rootEdge.Target]);
                graph.AddEdge(rootClone);
                clonedEdge = rootClone;
            }
            //graph.Check();

            return clonedEdge;
        }

        /// <summary>
        /// creates a node of given type and adds it to the graph, returns it
        /// type might be a string denoting a NodeType or a NodeType
        /// </summary>
        public static INode AddNodeOfType(object type, IGraph graph)
        {
            return graph.AddNode(type is string ? graph.Model.NodeModel.GetType((string)type) : (NodeType)type);
        }

        /// <summary>
        /// creates an edge of given type and adds it to the graph between from and to, returns it
        /// type might be a string denoting an EdgeType or an EdgeType
        /// </summary>
        public static IEdge AddEdgeOfType(object type, INode src, INode tgt, IGraph graph)
        {
            return graph.AddEdge(type is string ? graph.Model.EdgeModel.GetType((string)type) : (EdgeType)type, src, tgt);
        }
    }
}
