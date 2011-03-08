/*
 * GrGen: graph rewrite generator tool -- release GrGen.NET 2.7
 * Copyright (C) 2003-2011 Universitaet Karlsruhe, Institut fuer Programmstrukturen und Datenorganisation, LS Goos
 * licensed under LGPL v3 (see LICENSE.txt included in the packaging of this file)
 * www.grgen.net
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Text;
using System.Diagnostics;

namespace de.unika.ipd.grGen.libGr
{
    public static class DictionaryHelper
    {
        /// <summary>
        /// If dict is dictionary, the dictionary is returned together with it's key and value type
        /// </summary>
        /// <param name="dict">The object which should be a dictionary</param>
        /// <param name="keyType">The key type of the dictionary</param>
        /// <param name="valueType">The value type of the dictionary</param>
        /// <returns>The casted input dictionary, or null if not a dictionary</returns>
        public static IDictionary GetDictionaryTypes(object dict, out Type keyType, out Type valueType)
        {
            if (!(dict is IDictionary))
            {
                keyType = null;
                valueType = null;
                return null;
            }
            Type dictType = dict.GetType();
            GetDictionaryTypes(dictType, out keyType, out valueType);
            return (IDictionary)dict;
        }

        /// <summary>
        /// The key and value types are returned of the dictionary
        /// </summary>
        /// <param name="dictType">The dictionary type</param>
        /// <param name="keyType">The key type of the dictionary</param>
        /// <param name="valueType">The value type of the dictionary</param>
        public static void GetDictionaryTypes(Type dictType, out Type keyType, out Type valueType)
        {
            Type[] dictTypeArgs = dictType.GetGenericArguments();
            keyType = dictTypeArgs[0];
            valueType = dictTypeArgs[1];
        }

        /// <summary>
        /// Returns type object for type name string, to be used for dictionary
        /// </summary>
        /// <param name="typeName">Name of the type we want some type object for</param>
        /// <param name="graph">Graph to be search for enum,node,edge types / enum,node/edge type names</param>
        /// <returns>The type object corresponding to the given string, null if type was not found</returns>
        public static Type GetTypeFromNameForDictionary(String typeName, IGraph graph)
        {
            return GetTypeFromNameForDictionary(typeName, graph.Model);
        }

        /// <summary>
        /// Returns type object for type name string, to be used for dictionary
        /// </summary>
        /// <param name="typeName">Name of the type we want some type object for</param>
        /// <param name="model">Graph model to be search for enum,node,edge types / enum,node/edge type names</param>
        /// <returns>The type object corresponding to the given string, null if type was not found</returns>
        public static Type GetTypeFromNameForDictionary(String typeName, IGraphModel model)
        {
            if(typeName == null) return null;

            switch (typeName)
            {
                case "boolean": return typeof(bool);
                case "int": return typeof(int);
                case "float": return typeof(float);
                case "double": return typeof(double);
                case "string": return typeof(string);
                case "object": return typeof(object);
            }

            if (model == null) return null;

            // No standard type, so check enums
            foreach (EnumAttributeType enumAttrType in model.EnumAttributeTypes)
            {
                if (enumAttrType.Name == typeName)
                    return enumAttrType.EnumType;
            }

            Assembly assembly = Assembly.GetAssembly(model.GetType());

            // check node and edge types
            foreach (NodeType nodeType in model.NodeModel.Types)
            {
                if (nodeType.Name == typeName)
                {
                    Type type = Type.GetType(nodeType.NodeInterfaceName); // available in libGr (INode)?
                    if (type != null) return type;
                    type = Type.GetType(nodeType.NodeInterfaceName + "," + assembly.FullName); // no -> search model assembly
                    return type;
                }
            }
            foreach (EdgeType edgeType in model.EdgeModel.Types)
            {
                if (edgeType.Name == typeName)
                {
                    Type type = Type.GetType(edgeType.EdgeInterfaceName); // available in libGr (IEdge)?
                    if (type != null) return type;
                    type = Type.GetType(edgeType.EdgeInterfaceName + "," + assembly.FullName); // no -> search model assembly
                    return type;
                }
            }

            return null;
        }

        /// <summary>
        /// Returns the qualified type name for the type name given
        /// </summary>
        public static String GetQualifiedTypeName(String typeName, IGraphModel model)
        {
            if(typeName=="de.unika.ipd.grGen.libGr.SetValueType" || typeName=="SetValueType")
                return "de.unika.ipd.grGen.libGr.SetValueType";
            Type type = GetTypeFromNameForDictionary(typeName, model);
            return type!=null ? type.Namespace+"."+type.Name : null;
        }

        /// <summary>
        /// Creates a new dictionary of the given key type and value type
        /// </summary>
        /// <param name="keyType">The key type of the dictionary to be created</param>
        /// <param name="valueType">The value type of the dictionary to be created</param>
        /// <returns>The newly created dictionary, null if unsuccessfull</returns>
        public static IDictionary NewDictionary(Type keyType, Type valueType)
        {
            if (keyType == null || valueType == null) return null;

            Type genDictType = typeof(Dictionary<,>);
            Type dictType = genDictType.MakeGenericType(keyType, valueType);
            return (IDictionary)Activator.CreateInstance(dictType);
        }

        /// <summary>
        /// Creates a new dictionary of the given key type and value type,
        /// initialized with the content of the old dictionary (clones the old dictionary)
        /// </summary>
        /// <param name="keyType">The key type of the dictionary to be created</param>
        /// <param name="valueType">The value type of the dictionary to be created</param>
        /// <param name="oldDictionary">The old dictionary to be cloned</param>
        /// <returns>The newly created dictionary, containing the content of the old dictionary,
        /// null if unsuccessfull</returns>
        public static IDictionary NewDictionary(Type keyType, Type valueType, object oldDictionary)
        {
            if (keyType == null || valueType == null || oldDictionary == null) return null;

            Type genDictType = typeof(Dictionary<,>);
            Type dictType = genDictType.MakeGenericType(keyType, valueType);
            return (IDictionary) Activator.CreateInstance(dictType, oldDictionary);
        }

        /// <summary>
        /// Creates a new dictionary and fills in all key/value pairs from
        /// <paramref name="a"/> and <paramref name="b"/>.
        /// If both dictionaries contain one key, the value from <paramref name="b"/> takes precedence
        /// (this way the common case "a = a | map<int, int> { 7 -> 13 };" would update an existing entry
        /// with key 7 to 13).
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>A new dictionary containing all elements from both parameters.</returns>
        public static Dictionary<K, V> Union<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            // Fill new dictionary with all elements from a.
            Dictionary<K, V> newDict = new Dictionary<K, V>(a);

            // Add all elements from b, potentially overwriting those of a.
            foreach(KeyValuePair<K, V> entry in b)
                newDict[entry.Key] = entry.Value;

            return newDict;
        }

        /// <summary>
        /// Creates a new dictionary containing all key/value pairs from
        /// <paramref name="a"/> whose keys are also contained in <paramref name="b"/>.
        /// If both dictionaries contain one key, the value from <paramref name="a"/> takes precedence, in contrast to union.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>A new dictionary containing all elements from <paramref name="a"/>,
        /// which are also in <paramref name="b"/>.</returns>
        public static Dictionary<K, V> Intersect<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            // Fill new dictionary with all elements from a.
            Dictionary<K, V> newDict = new Dictionary<K, V>(a);

            // Remove all elements of b not contained in a.
            foreach(KeyValuePair<K, V> entry in b)
                if(!a.ContainsKey(entry.Key))
                    newDict.Remove(entry.Key);

            return newDict;
        }

        /// <summary>
        /// Creates a new dictionary containing all key/value pairs from
        /// <paramref name="a"/> whose keys are not contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible key type to <paramref name="a"/>.</param>
        /// <returns>A new dictionary containing all elements from <paramref name="a"/>,
        /// which are not in <paramref name="b"/>.</returns>
        public static Dictionary<K, V> Except<K, V, W>(Dictionary<K, V> a, Dictionary<K, W> b)
        {
            // Fill new dictionary with all elements from a.
            Dictionary<K, V> newDict = new Dictionary<K, V>(a);

            // Remove all elements contained in b.
            foreach(KeyValuePair<K, W> entry in b)
                newDict.Remove(entry.Key);

            return newDict;
        }

        /// <summary>
        /// Adds all key/value pairs from set/map <paramref name="b"/> to <paramref name="a"/>.
        /// If both dictionaries contain one key, the value from <paramref name="b"/> takes precedence
        /// (this way the common case "a = a | map<int, int> { 7 -> 13 };" would update an existing entry
        /// with key 7 to 13).
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool UnionChanged<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            bool changed = false;

            // Add all elements from b not contained in a (different values count as not contained, overwriting old value).
            foreach(KeyValuePair<K, V> entry in b)
            {
                if(!a.ContainsKey(entry.Key) || EqualityComparer<V>.Default.Equals(a[entry.Key], entry.Value))
                {
                    a[entry.Key] = entry.Value;
                    changed = true;
                }
            }

            return changed;
        }

        /// <summary>
        /// Removes all key/value pairs from set/map <paramref name="a"/> whose keys are not also contained in <paramref name="b"/>.
        /// If both dictionaries contain one key, the value from <paramref name="a"/> takes precedence, in contrast to union.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool IntersectChanged<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            // First determine all elements from a not contained in b
            List<K> toBeRemoved = new List<K>(a.Count);
            foreach(KeyValuePair<K, V> entry in a)
                if(!b.ContainsKey(entry.Key))
                    toBeRemoved.Add(entry.Key);

            // Then remove them
            foreach(K key in toBeRemoved)
                a.Remove(key);

            return toBeRemoved.Count>0;
        }

        /// <summary>
        /// Removes all key/value pairs from set/map <paramref name="a"/> whose keys are contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible key type to <paramref name="a"/>.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool ExceptChanged<K, V, W>(Dictionary<K, V> a, Dictionary<K, W> b)
        {
            bool changed = false;

            // Remove all elements from a contained in b.
            foreach(KeyValuePair<K, W> entry in b)
                changed |= a.Remove(entry.Key);

            return changed;
        }

        /// <summary>
        /// Adds all key/value pairs from map <paramref name="b"/> to <paramref name="a"/>.
        /// If both dictionaries contain one key, the value from <paramref name="b"/> takes precedence
        /// (this way the common case "a = a | map<int, int> { 7 -> 13 };" would update an existing entry
        /// with key 7 to 13).
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the node containing the attribute which gets changed.</param>
        /// <param name="owner">The node containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool UnionChanged<K, V>(Dictionary<K, V> a, Dictionary<K, V> b,
            IGraph graph, INode owner, AttributeType attrType)
        {
            bool changed = false;

            // Add all elements from b not contained in a (different values count as not contained, overwriting old value).
            foreach(KeyValuePair<K, V> entry in b)
            {
                if(!a.ContainsKey(entry.Key) || EqualityComparer<V>.Default.Equals(a[entry.Key], entry.Value))
                {
                    graph.ChangingNodeAttribute(owner, attrType, AttributeChangeType.PutElement, entry.Value, entry.Key);
                    a[entry.Key] = entry.Value;
                    changed = true;
                }
            }

            return changed;
        }

        /// <summary>
        /// Adds all key/value pairs from map <paramref name="b"/> to <paramref name="a"/>.
        /// If both dictionaries contain one key, the value from <paramref name="b"/> takes precedence
        /// (this way the common case "a = a | map<int, int> { 7 -> 13 };" would update an existing entry
        /// with key 7 to 13).
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the edge containing the attribute which gets changed.</param>
        /// <param name="owner">The edge containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool UnionChanged<K, V>(Dictionary<K, V> a, Dictionary<K, V> b,
            IGraph graph, IEdge owner, AttributeType attrType)
        {
            bool changed = false;

            // Add all elements from b not contained in a (different values count as not contained, overwriting old value).
            foreach(KeyValuePair<K, V> entry in b)
            {
                if(!a.ContainsKey(entry.Key) || EqualityComparer<V>.Default.Equals(a[entry.Key], entry.Value))
                {
                    graph.ChangingEdgeAttribute(owner, attrType, AttributeChangeType.PutElement, entry.Value, entry.Key);
                    a[entry.Key] = entry.Value;
                    changed = true;
                }
            }

            return changed;
        }

        /// <summary>
        /// Adds all key/value pairs from set <paramref name="b"/> to <paramref name="a"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the node containing the attribute which gets changed.</param>
        /// <param name="owner">The node containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool UnionChanged<K>(Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> a,
            Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> b,
            IGraph graph, INode owner, AttributeType attrType)
        {
            bool changed = false;

            // Add all elements from b not contained in a (different values count as not contained, overwriting old value).
            foreach(KeyValuePair<K, de.unika.ipd.grGen.libGr.SetValueType> entry in b)
            {
                if(!a.ContainsKey(entry.Key))
                {
                    graph.ChangingNodeAttribute(owner, attrType, AttributeChangeType.PutElement, entry.Key, null);
                    a[entry.Key] = entry.Value;
                    changed = true;
                }
            }

            return changed;
        }

        /// <summary>
        /// Adds all key/value pairs from set <paramref name="b"/> to <paramref name="a"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the edge containing the attribute which gets changed.</param>
        /// <param name="owner">The edge containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool UnionChanged<K>(Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> a,
            Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> b,
            IGraph graph, IEdge owner, AttributeType attrType)
        {
            bool changed = false;

            // Add all elements from b not contained in a (different values count as not contained, overwriting old value).
            foreach(KeyValuePair<K, de.unika.ipd.grGen.libGr.SetValueType> entry in b)
            {
                if(!a.ContainsKey(entry.Key))
                {
                    graph.ChangingEdgeAttribute(owner, attrType, AttributeChangeType.PutElement, entry.Key, null);
                    a[entry.Key] = entry.Value;
                    changed = true;
                }
            }

            return changed;
        }

        /// <summary>
        /// Removes all key/value pairs from map <paramref name="a"/> whose keys are not also contained in <paramref name="b"/>.
        /// If both dictionaries contain one key, the value from <paramref name="a"/> takes precedence, in contrast to union.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the node containing the attribute which gets changed.</param>
        /// <param name="owner">The node containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool IntersectChanged<K, V>(Dictionary<K, V> a, Dictionary<K, V> b,
            IGraph graph, INode owner, AttributeType attrType)
        {
            // First determine all elements from a not contained in b
            List<K> toBeRemoved = new List<K>(a.Count);
            foreach(KeyValuePair<K, V> entry in a)
                if(!b.ContainsKey(entry.Key))
                    toBeRemoved.Add(entry.Key);

            // Then remove them
            foreach(K key in toBeRemoved)
            {
                graph.ChangingNodeAttribute(owner, attrType, AttributeChangeType.RemoveElement, null, key);
                a.Remove(key);
            }

            return toBeRemoved.Count > 0;
        }

        /// <summary>
        /// Removes all key/value pairs from map <paramref name="a"/> whose keys are not also contained in <paramref name="b"/>.
        /// If both dictionaries contain one key, the value from <paramref name="a"/> takes precedence, in contrast to union.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the edge containing the attribute which gets changed.</param>
        /// <param name="owner">The edge containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool IntersectChanged<K, V>(Dictionary<K, V> a, Dictionary<K, V> b,
            IGraph graph, IEdge owner, AttributeType attrType)
        {
            // First determine all elements from a not contained in b
            List<K> toBeRemoved = new List<K>(a.Count);
            foreach(KeyValuePair<K, V> entry in a)
                if(!b.ContainsKey(entry.Key))
                    toBeRemoved.Add(entry.Key);

            // Then remove them
            foreach(K key in toBeRemoved)
            {
                graph.ChangingEdgeAttribute(owner, attrType, AttributeChangeType.RemoveElement, null, key);
                a.Remove(key);
            }

            return toBeRemoved.Count > 0;
        }

        /// <summary>
        /// Removes all key/value pairs from set <paramref name="a"/> whose keys are not also contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the node containing the attribute which gets changed.</param>
        /// <param name="owner">The node containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool IntersectChanged<K>(Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> a,
            Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> b,
            IGraph graph, INode owner, AttributeType attrType)
        {
            // First determine all elements from a not contained in b
            List<K> toBeRemoved = new List<K>(a.Count);
            foreach(KeyValuePair<K, de.unika.ipd.grGen.libGr.SetValueType> entry in a)
                if(!b.ContainsKey(entry.Key))
                    toBeRemoved.Add(entry.Key);

            // Then remove them
            foreach(K key in toBeRemoved)
            {
                graph.ChangingNodeAttribute(owner, attrType, AttributeChangeType.RemoveElement, key, null);
                a.Remove(key);
            }

            return toBeRemoved.Count > 0;
        }

        /// <summary>
        /// Removes all key/value pairs from set <paramref name="a"/> whose keys are not also contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the edge containing the attribute which gets changed.</param>
        /// <param name="owner">The edge containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool IntersectChanged<K>(Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> a,
            Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> b,
            IGraph graph, IEdge owner, AttributeType attrType)
        {
            // First determine all elements from a not contained in b
            List<K> toBeRemoved = new List<K>(a.Count);
            foreach(KeyValuePair<K, de.unika.ipd.grGen.libGr.SetValueType> entry in a)
                if(!b.ContainsKey(entry.Key))
                    toBeRemoved.Add(entry.Key);

            // Then remove them
            foreach(K key in toBeRemoved)
            {
                graph.ChangingEdgeAttribute(owner, attrType, AttributeChangeType.RemoveElement, key, null);
                a.Remove(key);
            }

            return toBeRemoved.Count > 0;
        }

        /// <summary>
        /// Removes all key/value pairs from map <paramref name="a"/> whose keys are contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible key type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the node containing the attribute which gets changed.</param>
        /// <param name="owner">The node containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool ExceptChanged<K, V, W>(Dictionary<K, V> a, Dictionary<K, W> b,
            IGraph graph, INode owner, AttributeType attrType)
        {
            bool changed = false;

            // Remove all elements from a contained in b.
            foreach(KeyValuePair<K, W> entry in b)
            {
                graph.ChangingNodeAttribute(owner, attrType, AttributeChangeType.RemoveElement, null, entry.Key);
                changed |= a.Remove(entry.Key);
            }

            return changed;
        }

        /// <summary>
        /// Removes all key/value pairs from map <paramref name="a"/> whose keys are contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible key type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the edge containing the attribute which gets changed.</param>
        /// <param name="owner">The edge containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool ExceptChanged<K, V, W>(Dictionary<K, V> a, Dictionary<K, W> b,
            IGraph graph, IEdge owner, AttributeType attrType)
        {
            bool changed = false;

            // Remove all elements from a contained in b.
            foreach(KeyValuePair<K, W> entry in b)
            {
                graph.ChangingEdgeAttribute(owner, attrType, AttributeChangeType.RemoveElement, null, entry.Key);
                changed |= a.Remove(entry.Key);
            }

            return changed;
        }

        /// <summary>
        /// Removes all key/value pairs from set <paramref name="a"/> whose keys are contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible key type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the node containing the attribute which gets changed.</param>
        /// <param name="owner">The node containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool ExceptChanged<K, W>(Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> a,
            Dictionary<K, W> b,
            IGraph graph, INode owner, AttributeType attrType)
        {
            bool changed = false;

            // Remove all elements from a contained in b.
            foreach(KeyValuePair<K, W> entry in b)
            {
                graph.ChangingNodeAttribute(owner, attrType, AttributeChangeType.RemoveElement, entry.Key, null);
                changed |= a.Remove(entry.Key);
            }

            return changed;
        }

        /// <summary>
        /// Removes all key/value pairs from set <paramref name="a"/> whose keys are contained in <paramref name="b"/>.
        /// </summary>
        /// <param name="a">A dictionary to change.</param>
        /// <param name="b">Another dictionary of compatible key type to <paramref name="a"/>.</param>
        /// <param name="graph">The graph containing the edge containing the attribute which gets changed.</param>
        /// <param name="owner">The edge containing the attribute which gets changed.</param>
        /// <param name="attrType">The attribute type of the attribute which gets changed.</param>
        /// <returns>A truth value telling whether at least one element was changed in a</returns>
        public static bool ExceptChanged<K, W>(Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> a,
            Dictionary<K, W> b,
            IGraph graph, IEdge owner, AttributeType attrType)
        {
            bool changed = false;

            // Remove all elements from a contained in b.
            foreach(KeyValuePair<K, W> entry in b)
            {
                graph.ChangingEdgeAttribute(owner, attrType, AttributeChangeType.RemoveElement, entry.Key, null);
                changed |= a.Remove(entry.Key);
            }

            return changed;
        }

        /// <summary>
        /// Creates a new dictionary representing a set,
        /// containing all keys from the given dictionary representing a map <paramref name="map"/>.
        /// </summary>
        /// <param name="map">A dictionary representing a map.</param>
        /// <returns>A new set dictionary containing all keys from <paramref name="map"/>.</returns>
        public static Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> Domain<K, V>(Dictionary<K, V> map)
        {
            Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType> newDict =
                new Dictionary<K, de.unika.ipd.grGen.libGr.SetValueType>();

            // Add all keys of dictionary representing map to new dictionary representing set
            foreach (K key in map.Keys)
            {
                newDict[key] = null;
            }

            return newDict;
        }

        /// <summary>
        /// Creates a new dictionary representing a set, 
        /// containing all values from the given dictionary representing a map <paramref name="map"/>.
        /// </summary>
        /// <param name="map">A dictionary representing a map.</param>
        /// <returns>A new set dictionary containing all values from <paramref name="map"/>.</returns>
        public static Dictionary<V, de.unika.ipd.grGen.libGr.SetValueType> Range<K, V>(Dictionary<K, V> map)
        {
            Dictionary<V, de.unika.ipd.grGen.libGr.SetValueType> newDict =
                new Dictionary<V, de.unika.ipd.grGen.libGr.SetValueType>();

            // Add all values of dictionary representing map to new dictionary representing set
            foreach (V value in map.Values)
            {
                newDict[value] = null;
            }

            return newDict;
        }

        /// <summary>
        /// Returns the value from the dictionary at the nth position as defined by the iterator of the dictionary.
        /// </summary>
        /// <param name="dict">A dictionary.</param>
        /// <param name="num">The number of the element to get in the iteration sequence.</param>
        /// <returns>The element at the position to get.</returns>
        public static K Peek<K, V>(Dictionary<K, V> dict, int num)
        {
            Dictionary<K,V>.Enumerator it = dict.GetEnumerator();
            if(num >= 0) it.MoveNext();
            for(int i = 0; i < num; ++i)
            {
                it.MoveNext();
            }

            return it.Current.Key;
        }

        /// <summary>
        /// Checks if set/map <paramref name="a"/> equals set/map <paramref name="b"/>.
        /// For a map, key and value must be same to be equal.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>Boolean result of set/map comparison.</returns>
        public static bool Equal<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            if(a.Count!=b.Count) return false;
            if(LessOrEqual(a, b) && LessOrEqual(b, a)) return true;
            else return false;
        }

        /// <summary>
        /// Checks if set/map <paramref name="a"/> is not equal to set/map <paramref name="b"/>.
        /// For a map, key and value must be same to be equal.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>Boolean result of set/map comparison.</returns>
        public static bool NotEqual<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            if(a.Count!=b.Count) return true;
            if(LessOrEqual(a, b) && LessOrEqual(b, a)) return false;
            else return true;
        }

        /// <summary>
        /// Checks if set/map <paramref name="a"/> is a proper superset/map of <paramref name="b"/>.
        /// For a map, key and value must be same to be equal.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>Boolean result of set/map comparison.</returns>
        public static bool GreaterThan<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            if(GreaterOrEqual(a, b)) return b.Count!=a.Count;
            else return false;
        }

        /// <summary>
        /// Checks if set/map <paramref name="a"/> is a superset/map of <paramref name="b"/>.
        /// For a map, key and value must be same to be equal.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>Boolean result of set/map comparison.</returns>
        public static bool GreaterOrEqual<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            return LessOrEqual(b, a);
        }

        /// <summary>
        /// Checks if set/map <paramref name="a"/> is a proper subset/map of <paramref name="b"/>.
        /// For a map, key and value must be same to be equal.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>Boolean result of set/map comparison.</returns>
        public static bool LessThan<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            if(LessOrEqual(a, b)) return a.Count!=b.Count;
            else return false;
        }

        /// <summary>
        /// Checks if set/map <paramref name="a"/> is a subset/map of <paramref name="b"/>.
        /// For a map, key and value must be same to be equal.
        /// </summary>
        /// <param name="a">A dictionary.</param>
        /// <param name="b">Another dictionary of compatible type to <paramref name="a"/>.</param>
        /// <returns>Boolean result of set/map comparison.</returns>
        public static bool LessOrEqual<K, V>(Dictionary<K, V> a, Dictionary<K, V> b)
        {
            if(typeof(V)==typeof(de.unika.ipd.grGen.libGr.SetValueType))
            {
                foreach(KeyValuePair<K, V> entry in a)
                {
                    if(!b.ContainsKey(entry.Key)) return false;
                }
            }
            else
            {
                foreach(KeyValuePair<K, V> entry in a)
                {
                    if(!b.ContainsKey(entry.Key)) return false;
                    if(!entry.Value.Equals(b[entry.Key])) return false;
                }
            }
            return true;
        }

        /////////////////////////////////////////////////////////////////////////////////

        /// <summary>
        /// Returns a string representation of the given dictionary
        /// </summary>
        /// <param name="setmap">The dictionary of which to get the string representation</param>
        /// <param name="graph">The graph with the model and the element names if available, otherwise null</param>
        /// <returns>string representation of dictionary</returns>
        public static string ToString(IDictionary setmap, IGraph graph)
        {
            string type;
            string content;
            ToString(setmap, out type, out content, null, graph);
            return content;
        }

        /// <summary>
        /// Returns a string representation of the given dictionary
        /// </summary>
        /// <param name="setmap">The dictionary of which to get the string representation</param>
        /// <param name="type">The type as string, e.g set<int> or map<string,boolean> </param>
        /// <param name="content">The content as string, e.g. { 42, 43 } or { "foo"->true, "bar"->false } </param>
        /// <param name="attrType">The attribute type of the dictionary if available, otherwise null</param>
        /// <param name="graph">The graph with the model and the element names if available, otherwise null</param>
        public static void ToString(IDictionary setmap, out string type, out string content, 
            AttributeType attrType, IGraph graph)
        {
            Type keyType;
            Type valueType;
            GetDictionaryTypes(setmap, out keyType, out valueType);

            StringBuilder sb = new StringBuilder(256);
            sb.Append("{");

            AttributeType attrValueType = attrType != null ? attrType.ValueType : null;
            AttributeType attrKeyType = attrType != null ? attrType.KeyType : null;

            if(setmap != null)
            {
                if (valueType == typeof(SetValueType))
                {
                    type = "set<" + keyType.Name + ">";
                    bool first = true;
                    foreach (DictionaryEntry entry in setmap)
                    {
                        if (first) { sb.Append(ToString(entry.Key, attrValueType, graph)); first = false; }
                        else { sb.Append(","); sb.Append(ToString(entry.Key, attrValueType, graph)); }
                    }
                }
                else
                {
                    type = "map<" + keyType.Name + "," + valueType.Name + ">";
                    bool first = true;
                    foreach (DictionaryEntry entry in setmap)
                    {
                        if (first) { sb.Append(ToString(entry.Key, attrKeyType, graph)); sb.Append("->"); sb.Append(ToString(entry.Value, attrValueType, graph)); first = false; }
                        else { sb.Append(","); sb.Append(ToString(entry.Key, attrKeyType, graph)); sb.Append("->"); sb.Append(ToString(entry.Value, attrValueType, graph)); }
                    }
                }
            } else {
                type = "<INVALID>";
            }

            sb.Append("}");
            content = sb.ToString();
        }

        /// <summary>
        /// Returns a string representation of the given dictionary
        /// after the given operation with the given parameters was applied
        /// </summary>
        /// <param name="setmap">The base dictionary of the operation</param>
        /// <param name="changeType">The type of the change operation</param>
        /// <param name="newValue">The new value of the attribute, if changeType==Assign.
        ///                        Or the value to be inserted/removed if changeType==PutElement/RemoveElement on set.
        ///                        Or the new map pair value to be inserted if changeType==PutElement on map.</param>
        /// <param name="keyValue">The map pair key to be inserted/removed if changeType==PutElement/RemoveElement on map.</param>
        /// <param name="type">The type as string, e.g set<int> or map<string,boolean> </param>
        /// <param name="content">The content as string, e.g. { 42, 43 } or { "foo"->true, "bar"->false } </param>
        /// <param name="attrType">The attribute type of the dictionary</param>
        /// <param name="graph">The graph with the model and the element names</param>
        public static void ToString(IDictionary setmap, 
            AttributeChangeType changeType, Object newValue, Object keyValue,
            out string type, out string content, 
            AttributeType attrType, IGraph graph)
        {
            if(changeType==AttributeChangeType.PutElement)
            {
                Type keyType;
                Type valueType;
                GetDictionaryTypes(setmap, out keyType, out valueType);

                if(valueType==typeof(SetValueType))
                {
                    ToString(setmap, out type, out content, attrType, graph);
                    content += "|" + ToString(newValue, attrType.ValueType, graph);
                }
                else
                {
                    ToString(setmap, out type, out content, attrType, graph);
                    content += "|" + ToString(keyValue, attrType.KeyType, graph) + "->" + ToString(newValue, attrType.ValueType, graph);
                }
            }
            else if(changeType==AttributeChangeType.RemoveElement)
            {
                Type keyType;
                Type valueType;
                GetDictionaryTypes(setmap, out keyType, out valueType);

                if(valueType==typeof(SetValueType))
                {
                    ToString(setmap, out type, out content, attrType, graph);
                    content += "\\" + ToString(newValue, attrType.ValueType, graph);
                }
                else
                {
                    ToString(setmap, out type, out content, attrType, graph);
                    content += "\\" + ToString(keyValue, attrType.KeyType, graph) + "->.";
                }
            }
            else // changeType==AttributeChangeType.Assign
            {
                ToString((IDictionary)newValue, out type, out content, attrType, graph);
            }
        }

        /// <summary>
        /// Returns a string representation of the given scalar value
        /// </summary>
        /// <param name="value">The scalar value of which to get the string representation</param>
        /// <param name="graph">The graph with the model and the element names if available, otherwise null</param>
        /// <returns>string representation of scalar value</returns>
        public static string ToString(object value, IGraph graph)
        {
            string type;
            string content;
            ToString(value, out type, out content, null, graph);
            return content;
        }

        /// <summary>
        /// Returns a string representation of the given scalar value
        /// </summary>
        /// <param name="value">The scalar value of which to get the string representation</param>
        /// <param name="type">The type as string, e.g int,string,Foo </param>
        /// <param name="content">The content as string, e.g. 42,"foo",bar } </param>
        /// <param name="attrType">The attribute type of the value</param>
        /// <param name="graph">The graph with the model and the element names</param>
        public static void ToString(object value, out string type, out string content, 
            AttributeType attrType, IGraph graph)
        {
            if (attrType == null)
            {
                if(value == null)
                {
                    type = "<INVALID>";
                    content = ToString(value, attrType, graph);
                    return;
                }

                Debug.Assert(value.GetType().Name != "Dictionary`2");
                switch(value.GetType().Name)
                {
                    case "Int32": type = "int"; break;
                    case "Boolean": type = "bool"; break;
                    case "String": type = "string"; break;
                    case "Single": type = "float"; break;
                    case "Double": type = "double"; break;
                    case "Object": type = "object"; break;
                    default:
                        type = "<INVALID>";
                        if(graph != null && value is Enum)
                        {
                            foreach(EnumAttributeType enumAttrType in graph.Model.EnumAttributeTypes)
                            {
                                if(value.GetType() == enumAttrType.EnumType)
                                {
                                    type = enumAttrType.Name;
                                    break;
                                }
                            }
                        }
                        else if(value is IGraphElement)
                        {
                            type = ((IGraphElement)value).Type.Name;
                        }
                        break;
                }

                content = ToString(value, attrType, graph);
                return;
            }

            Debug.Assert(attrType.Kind != AttributeKind.SetAttr && attrType.Kind != AttributeKind.MapAttr);
            switch (attrType.Kind)
            {
                case AttributeKind.IntegerAttr: type = "int"; break;
                case AttributeKind.BooleanAttr: type = "bool"; break;
                case AttributeKind.StringAttr: type = "string"; break;
                case AttributeKind.EnumAttr: type = attrType.EnumType.Name; break;
                case AttributeKind.FloatAttr: type = "float"; break;
                case AttributeKind.DoubleAttr: type = "double"; break;
                case AttributeKind.ObjectAttr: type = "object"; break;
                case AttributeKind.NodeAttr: type = attrType.TypeName; break;
                case AttributeKind.EdgeAttr: type = attrType.TypeName; break; 
                default: type = "<INVALID>"; break;
            }

            content = ToString(value, attrType, graph);
        }

        /// <summary>
        /// If the attribute of the given name of the given element is a set or map attribute
        /// then return a clone of the given dictionary value, otherwise just return the original value;
        /// additionally returns the AttributeType of the attribute of the element.
        /// </summary>
        public static object IfAttributeOfElementIsDictionaryThenCloneDictionaryValue(
                IGraphElement element, String AttributeName, object value, out AttributeType attrType)
        {
            attrType = element.Type.GetAttributeType(AttributeName);
            if(attrType.Kind == AttributeKind.SetAttr || attrType.Kind == AttributeKind.MapAttr)
            {
                Type keyType, valueType;
                DictionaryHelper.GetDictionaryTypes(element.GetAttribute(AttributeName), out keyType, out valueType);
                return DictionaryHelper.NewDictionary(keyType, valueType, value); // by-value-semantics -> clone dictionary
            }
            return value;
        }

        /// <summary>
        /// Returns a string representation of the given scalar value
        /// </summary>
        /// <param name="value">The scalar of which to get the string representation</param>
        /// <param name="attrType">The attribute type</param>
        /// <param name="graph">The graph with the model and the element names</param>
        /// <returns>String representation of the scalar.</returns>
        private static string ToString(object value, AttributeType attrType, IGraph graph)
        {
            // enums are bitches, sometimes ToString gives the symbolic name, sometimes only the integer value
            // we always want the symbolic name, enforce this here
            if(attrType!=null && attrType.Kind==AttributeKind.EnumAttr)
            {
                Debug.Assert(attrType.Kind!=AttributeKind.SetAttr && attrType.Kind!=AttributeKind.MapAttr);
                EnumAttributeType enumAttrType = attrType.EnumType;
                EnumMember member = enumAttrType[(int)value];
                if(member!=null) return member.Name;
            }
            if(graph!=null && value is Enum)
            {
                Debug.Assert(value.GetType().Name!="Dictionary`2");
                foreach(EnumAttributeType enumAttrType in graph.Model.EnumAttributeTypes)
                {
                    if(value.GetType()==enumAttrType.EnumType)
                    {
                        EnumMember member = enumAttrType[(int)value];
                        if(member != null) return member.Name;
                        else break;
                    }
                }
            }

            // for set/map entries of node/edge type return the persistent name
            if(attrType!=null && (attrType.Kind==AttributeKind.NodeAttr || attrType.Kind==AttributeKind.EdgeAttr))
            {
                if(graph!=null && value!=null)
                {
                    return graph.GetElementName((IGraphElement)value);
                }
            }
            if(value is IGraphElement)
            {
                if(graph!=null)
                {
                    return graph.GetElementName((IGraphElement)value);
                }
            }

            return value!=null ? value.ToString() : "";
        }
    }
}
