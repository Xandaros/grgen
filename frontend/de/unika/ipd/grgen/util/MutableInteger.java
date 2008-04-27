/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET v2 beta
 * Copyright (C) 2008 Universit�t Karlsruhe, Institut f�r Programmstrukturen und Datenorganisation, LS Goos
 * licensed under GPL v3 (see LICENSE.txt included in the packaging of this file)
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
