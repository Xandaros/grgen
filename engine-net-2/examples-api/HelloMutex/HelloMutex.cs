/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.6
 * Copyright (C) 2003-2010 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

using de.unika.ipd.grGen.lgsp;
using de.unika.ipd.grGen.libGr;

class HelloMutex
{
    static void Main(string[] args)
    {
        LGSPGraph graph;
        LGSPActions actions;
        new LGSPBackend().CreateFromSpec("Mutex.grg", out graph, out actions);

        NodeType processType = graph.GetNodeType("Process");
        EdgeType nextType = graph.GetEdgeType("next");

        INode p1 = graph.AddNode(processType);
        INode p2 = graph.AddNode(processType);
        graph.AddEdge(nextType, p1, p2);
        graph.AddEdge(nextType, p2, p1);

        actions.ApplyGraphRewriteSequence("newRule[3] && mountRule && requestRule[5] "
            + "&& (takeRule && releaseRule && giveRule)*");

        using(VCGDumper dumper = new VCGDumper("HelloMutex.vcg"))
            graph.Dump(dumper);
    }
}
