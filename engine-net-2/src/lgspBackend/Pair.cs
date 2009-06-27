/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.5
 * Copyright (C) 2009 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 */

namespace de.unika.ipd.grGen.lgsp
{
    public class Pair<S, T>
    {
        public Pair()
        {
        }

        public Pair(S fst, T snd)
        {
            this.fst = fst;
            this.snd = snd;
        }

        public S fst;
        public T snd;
    }
}