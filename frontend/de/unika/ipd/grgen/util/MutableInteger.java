/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.5
 * Copyright (C) 2009 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 */

package de.unika.ipd.grgen.util;

public class MutableInteger
{
	int value;

	public MutableInteger (int v) {
		value = v;
	}

	public int getValue()
    {
    	return value;
    }

	public void setValue(int value)
    {
    	this.value = value;
    }
}
