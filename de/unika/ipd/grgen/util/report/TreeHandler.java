/**
 * @author shack
 * @version $Id$
 */
package de.unika.ipd.grgen.util.report;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * 
 */
public class TreeHandler extends DefaultTreeModel implements Handler {

	class EnterNode implements TreeNode {
		private Vector children;
		private String text;
		private TreeNode parent;
	
		public EnterNode(TreeNode parent, String text) {
			this.text = text;
			this.parent = parent;
			children = new Vector();
		}
	
		public void add(TreeNode n) {
			children.add(n);
		}
	
		public TreeNode getChildAt(int i) {
			return (TreeNode) children.get(i);
		}

		public int getChildCount() {
			return children.size();
		}

		public TreeNode getParent() {
			return parent;
		}

		public int getIndex(TreeNode arg0) {
			return children.indexOf(arg0);
		}

		public boolean getAllowsChildren() {
			return true;
		}

		public boolean isLeaf() {
			return children.size() == 0;
		}

		public Enumeration children() {
			return children.elements();
		}

		public String toString() {
			return text;
		}
	
		public int[] getAddedChildIndices() {
			int[] res = new int[children.size()];
			for(int i = 0; i < res.length; i++)
				res[i] = i;
			return res;
		}
	}
  /**
   * A tree node representing a log message
   * These nodes are always leaves.
   */
  private class MsgNode implements TreeNode {

		private TreeNode parent;
		private String msg;
		private int level;
		private Location loc; 

		public MsgNode(TreeNode parent, int level, Location loc, String msg) {
			this.level = level;
			this.loc = loc;
			this.msg = msg;
			this.parent = parent;
		}

    public TreeNode getChildAt(int arg0) {
      return null;
    }

    public int getChildCount() {
      return 0;
    }

    public TreeNode getParent() {
      return null;
    }

    public int getIndex(TreeNode arg0) {
      return 0;
    }

    public boolean getAllowsChildren() {
      return false;
    }

    public boolean isLeaf() {
      return true;
    }

    public Enumeration children() {
      return null;
    }
    
    public String toString() {
    	return msg;
    }
  }
  
  private EnterNode current, root;
  
  /**
   * 
   */
  public TreeHandler() {
  	super(null);
  	EnterNode root = new EnterNode(null, "ROOT"); 
  	setRoot(root);
  	current = root;
  }

  /**
   * @see de.unika.ipd.grgen.util.report.Handler#report(int, de.unika.ipd.grgen.util.report.Location, java.lang.String)
   */
  public void report(int level, Location loc, String msg) {
    current.add(new MsgNode(current, level, loc, msg));
  }

  /**
   * @see de.unika.ipd.grgen.util.report.Handler#entering(java.lang.String)
   */
  public void entering(String s) {
  	EnterNode n = new EnterNode(current, s);
  	current.add(n);
  	current = n;
  }

  /**
   * @see de.unika.ipd.grgen.util.report.Handler#leaving()
   */
  public void leaving() {
  	nodeChanged(current);
  	EnterNode p = (EnterNode) current.getParent();
		if(p != null)
		  current = p;
  }
 
}
