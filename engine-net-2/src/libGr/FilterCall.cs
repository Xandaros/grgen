/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 4.4
 * Copyright (C) 2003-2016 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos; and free programmers
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

// by Edgar Jakumeit

using System;
using System.Text;
using System.Collections.Generic;

namespace de.unika.ipd.grGen.libGr
{
    /// <summary>
    /// An object representing a filter call.
    /// It specifies the filter and potential arguments.
    /// </summary>
    public class FilterCall
    {
        /// <summary>
        /// The name of the filter.
        /// </summary>
        public String Name;

        /// <summary>
        /// null if this is a call of a global filter, otherwise the package the call target is contained in.
        /// </summary>
        public String Package;

        /// <summary>
        /// The name of the filter, prefixed by the package it is contained in (separated by a double colon), if it is contained in a package.
        /// </summary>
        public String PackagePrefixedName;

        /// <summary>
        /// null if this is a call of a global filter, otherwise the package the call target is contained in.
        /// May be even null for a call of a package target, if done from a context where the package is set.
        /// </summary>
        public String PrePackage;

        /// <summary>
        /// The package this invocation is contained in (the calling source, not the filter call target).
        /// Needed to resolve names from the local package accessed without package prefix.
        /// </summary>
        public String PrePackageContext;

        /// <summary>
        /// The entity the filter is based on, in case of a def-variable based auto-generated filter, otherwise null.
        /// </summary>
        public String Entity;

        /// <summary>
        /// True in case this is the call of an auto-supplied filter.
        /// In this case, there must be exactly one Argument or ArgumentExpression given.
        /// </summary>
        public bool IsAutoSupplied;

        /// <summary>
        /// An array of expressions used to compute the input arguments for a filter function (or auto-supplied filter).
        /// It must have the same length as Arguments.
        /// If an entry is null, the according entry in Arguments is used unchanged.
        /// Otherwise the entry in Arguments is filled with the evaluation result of the expression.
        /// The sequence parser generates argument expressions for every entry;
        /// they may be omitted by a user assembling an invocation at API level.
        /// </summary>
        public SequenceExpression[] ArgumentExpressions;

        /// <summary>
        /// Buffer to store the argument values for the filter function call (or auto-supplied filter call);
        /// used by libGr to avoid unneccessary memory allocations.
        /// </summary>
        public object[] Arguments;


        /// <summary>
        /// Instantiates a new FilterCall object for a filter function or auto
        /// </summary>
        public FilterCall(String package, String name, List<SequenceExpression> argumentExpressions, String packageContext)
        {
            PrePackage = package;
            Name = name;
            ArgumentExpressions = new SequenceExpression[argumentExpressions.Count];
            Arguments = new object[argumentExpressions.Count];
            for(int i = 0; i < argumentExpressions.Count; ++i)
            {
                ArgumentExpressions[i] = argumentExpressions[i];
                Arguments[i] = null;
            }
            PrePackageContext = packageContext;
        }

        /// <summary>
        /// Instantiates a new FilterCall object for an auto-generated filter
        /// dummy is only existing so that FilterCall(null, "auto", null, null, true) can be resolved to this constructor
        /// </summary>
        public FilterCall(String package, String name, String entity, String packageContext, bool dummy)
        {
            PrePackage = package;
            Name = name;
            Entity = entity;
            ArgumentExpressions = new SequenceExpression[0];
            Arguments = new object[0];
            PrePackageContext = packageContext;
        }

        /// <summary>
        /// Instantiates a new FilterCall object for an auto-supplied filter (with sequence expression parameter)
        /// </summary>
        public FilterCall(String package, String name, SequenceExpression argument, String packageContext)
        {
            PrePackage = package;
            Name = name;
            IsAutoSupplied = true;
            ArgumentExpressions = new SequenceExpression[1];
            ArgumentExpressions[0] = argument;
            Arguments = new object[1];
            Arguments[0] = null;
            PrePackageContext = packageContext;
        }


        public bool IsAutoGenerated
        {
            get
            {
                if(Entity != null)
                    return true;
                if(Name == "auto")
                    return true;
                return false;
            }
        }

        public string FullName
        {
            get
            {
                if(Entity != null)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.Append(Name);
                    sb.Append("<");
                    sb.Append(Entity);
                    sb.Append(">");
                    return sb.ToString();
                }
                else
                    return Name;
            }
        }

        public string PackagePrefixedFullName
        {
            get
            {
                if(Entity != null)
                {
                    StringBuilder sb = new StringBuilder();
                    if(Package != null)
                    {
                        sb.Append(Package);
                        sb.Append("::");
                    }
                    sb.Append(Name);
                    sb.Append("<");
                    sb.Append(Entity);
                    sb.Append(">");
                    return sb.ToString();
                }
                else
                {
                    return PackagePrefixedName;
                }
            }
        }

        public bool IsContainedIn(List<IFilter> filters)
        {
            return IsContainedIn(filters.ToArray());
        }

        public bool IsContainedIn(IFilter[] filters)
        {
            for(int i = 0; i < filters.Length; ++i)
            {
                if(filters[i] is IFilterAutoGenerated)
                {
                    IFilterAutoGenerated filter = (IFilterAutoGenerated)filters[i];
                    if(filter.PackagePrefixedName == PackagePrefixedName)
                    {
                        if(Name == "auto")
                            return true;
                        else if(filter.Entity == Entity)
                            return true;
                    }
                }
                else //if(filters[i] is IFilterFunction)
                {
                    IFilterFunction filter = (IFilterFunction)filters[i];
                    if(filter.PackagePrefixedName == PackagePrefixedName)
                        return true;
                }
            }
            return false;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append(Name);
            if(Entity != null)
            {
                sb.Append("<");
                sb.Append(Entity);
                sb.Append(">");
            }
            if(Arguments != null)
            {
                sb.Append("(");
                for(int i=0; i<ArgumentExpressions.Length; ++i)
                {
                    if(ArgumentExpressions[i] != null)
                    {
                        sb.Append(ArgumentExpressions[i].Symbol);
                    }
                    else
                    {
                        if(Arguments[i] is Double)
                            sb.Append(((double)Arguments[i]).ToString(System.Globalization.CultureInfo.InvariantCulture));
                        else
                            sb.Append(Arguments[i].ToString());
                    }
                }
                sb.Append(")");
            }
            return sb.ToString();
        }
    }
}
