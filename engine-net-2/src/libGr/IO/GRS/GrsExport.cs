/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.6
 * Copyright (C) 2003-2010 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;

namespace de.unika.ipd.grGen.libGr
{
    /// <summary>
    /// Exports graphs to the GRS format.
    /// </summary>
    public class GRSExport : IDisposable
    {
        StreamWriter writer;
        bool withVariables;

        protected GRSExport(String filename, bool withVariables) 
            : this(new StreamWriter(filename), withVariables)
        {
        }

        protected GRSExport(StreamWriter writer, bool withVariables)
        {
            this.writer = writer;
            this.withVariables = withVariables;
        }

        public void Dispose()
        {
            if (writer != null)
            {
                writer.Dispose();
                writer = null;
            }
        }

        /// <summary>
        /// Exports the given graph to a GRS file with the given filename.
        /// Any errors will be reported by exception.
        /// </summary>
        /// <param name="graph">The graph to export. If a NamedGraph is given, it will be exported including the names.</param>
        /// <param name="exportFilename">The filename for the exported file.</param>
        /// <param name="withVariables">Export the graph variables, too?</param>
        public static void Export(IGraph graph, String exportFilename, bool withVariables)
        {
            using(GRSExport export = new GRSExport(exportFilename, withVariables))
                export.Export(graph);
        }

        /// <summary>
        /// Exports the given graph to the file given by the stream writer in grs format.
        /// Any errors will be reported by exception.
        /// </summary>
        /// <param name="graph">The graph to export. If a NamedGraph is given, it will be exported including the names.</param>
        /// <param name="writer">The stream writer to export to.</param>
        /// <param name="withVariables">Export the graph variables, too?</param>
        public static void Export(IGraph graph, StreamWriter writer, bool withVariables)
        {
            using(GRSExport export = new GRSExport(writer, withVariables))
                export.Export(graph);
        }

        protected void Export(IGraph graph)
        {
            ExportYouMustCloseStreamWriter(graph, writer, withVariables, "");
        }

        /// <summary>
        /// Exports the given graph to the file given by the stream writer in grs format.
        /// Any errors will be reported by exception.
        /// </summary>
        /// <param name="graph">The graph to export. If a NamedGraph is given, it will be exported including the names.</param>
        /// <param name="sw">The stream writer of the file to export into. The stream writer is not closed automatically.</param>
        /// <param name="withVariables">Export the graph variables, too?</param>
        public static void ExportYouMustCloseStreamWriter(IGraph graph, StreamWriter sw, bool withVariables, string modelPathPrefix)
        {
            sw.WriteLine("# begin of graph \"{0}\" saved by GrsExport", graph.Name);
            sw.WriteLine();

            sw.WriteLine("new graph \"" + modelPathPrefix + graph.Model.ModelName + "\" \"" + graph.Name + "\"");

            if (!(graph is NamedGraph)) {
                // assign arbitrary but unique names
                graph = new NamedGraph(graph);
            }

            int numNodes = 0;
            foreach (INode node in graph.Nodes)
            {
                sw.Write("new :{0}($ = \"{1}\"", node.Type.Name, graph.GetElementName(node));
                foreach (AttributeType attrType in node.Type.AttributeTypes)
                {
                    object value = node.GetAttribute(attrType.Name);
                    // TODO: Add support for null values, as the default initializers could assign non-null values!
                    if(value != null) {
                        EmitAttributeInitialization(attrType, value, sw);
                    }
                }
                sw.WriteLine(")");
                if(withVariables)
                {
                    LinkedList<Variable> vars = graph.GetElementVariables(node);
                    if(vars != null)
                    {
                        foreach(Variable var in vars)
                        {
                            sw.WriteLine("{0} = @(\"{1}\")", var.Name, graph.GetElementName(node));
                        }
                    }
                }
                numNodes++;
            }
            sw.WriteLine("# total number of nodes: {0}", numNodes);
            sw.WriteLine();

            int numEdges = 0;
            foreach (INode node in graph.Nodes)
            {
                foreach (IEdge edge in node.Outgoing)
                {
                    sw.Write("new @(\"{0}\") - :{1}($ = \"{2}\"", graph.GetElementName(node),
                        edge.Type.Name, graph.GetElementName(edge));
                    foreach (AttributeType attrType in edge.Type.AttributeTypes)
                    {
                        object value = edge.GetAttribute(attrType.Name);
                        // TODO: Add support for null values, as the default initializers could assign non-null values!
                        if(value != null) {
                            EmitAttributeInitialization(attrType, value, sw);
                        }
                    }
                    sw.WriteLine(") -> @(\"{0}\")", graph.GetElementName(edge.Target));

                    if(withVariables)
                    {
                        LinkedList<Variable> vars = graph.GetElementVariables(edge);
                        if(vars != null)
                        {
                            foreach(Variable var in vars)
                            {
                                sw.WriteLine("{0} = @(\"{1}\")", var.Name, graph.GetElementName(edge));
                            }
                        }
                    }
                    numEdges++;
                }
            }
            sw.WriteLine("# total number of edges: {0}", numEdges);
            sw.WriteLine();

            sw.WriteLine("# end of graph \"{0}\" saved by GrsExport", graph.Name);
            sw.WriteLine();
        }

        /// <summary>
        /// Emits the node/edge attribute initialization code in graph exporting
        /// for an attribute of the given type with the given value into the stream writer
        /// </summary>
        private static void EmitAttributeInitialization(AttributeType attrType, object value, StreamWriter sw)
        {
            sw.Write(", {0} = ", attrType.Name);
            EmitAttribute(attrType, value, sw);
        }

        /// <summary>
        /// Emits the attribute value as code
        /// for an attribute of the given type with the given value into the stream writer
        /// </summary>
        public static void EmitAttribute(AttributeType attrType, object value, StreamWriter sw)
        {
            if(attrType.Kind==AttributeKind.SetAttr)
            {
                IDictionary set=(IDictionary)value;
                sw.Write("{0}{{", attrType.GetKindName());
                bool first = true;
                foreach(DictionaryEntry entry in set)
                {
                    if(first) { sw.Write(ToString(entry.Key, attrType.ValueType)); first = false; }
                    else { sw.Write("," + ToString(entry.Key, attrType.ValueType)); }
                }
                sw.Write("}");
            }
            else if(attrType.Kind==AttributeKind.MapAttr)
            {
                IDictionary map=(IDictionary)value;
                sw.Write("{0}{{", attrType.GetKindName());
                bool first = true;
                foreach(DictionaryEntry entry in map)
                {
                    if(first) { sw.Write(ToString(entry.Key, attrType.KeyType)
                        + "->" + ToString(entry.Value, attrType.ValueType)); first = false;
                    }
                    else { sw.Write("," + ToString(entry.Key, attrType.KeyType)
                        + "->" + ToString(entry.Value, attrType.ValueType)); }
                }
                sw.Write("}");
            }
            else
            {
                sw.Write("{0}", ToString(value, attrType));
            }
        }

        public static String ToString(object value, AttributeType type)
        {
            switch(type.Kind)
            {
            case AttributeKind.IntegerAttr:
                return ((int)value).ToString();
            case AttributeKind.BooleanAttr:
                return ((bool)value).ToString();
            case AttributeKind.StringAttr:
                if(((string)value).IndexOf('\"') != -1) return "\'" + ((string)value) + "\'";
                else return "\"" + ((string)value) + "\"";
            case AttributeKind.FloatAttr:
                return ((float)value).ToString(System.Globalization.CultureInfo.InvariantCulture)+"f";
            case AttributeKind.DoubleAttr:
                return ((double)value).ToString(System.Globalization.CultureInfo.InvariantCulture);
            case AttributeKind.ObjectAttr:
                Console.WriteLine("Warning: Exporting non-null attribute of object type to null");
                return "null";
            case AttributeKind.EnumAttr:
                return type.EnumType.Name + "::" + type.EnumType[(int)value].Name;
            default:
                throw new Exception("Unsupported attribute kind in export");
            }
        }
    }
}
