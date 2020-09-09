/*
 * Node.java
 *
 * Created on 5. Oktober 2004, 15:41
 * Pocket KrHyper - 
 * an automated theorem proving library for the 
 * Java 2 Platform, Micro Edition (J2ME)
 * Copyright (C) 2005 Thomas Kleemann and Alex Sinner
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *
 */

package lifr.util;

import java.util.Vector;

import lifr.logic.fuzzy.Degree;
import lifr.logic.fuzzy.Weight;


/**
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.1 $
 */
public class Node implements NodeInterface{
    
    /** Creates a new instance of Node */
    public Node(Object content) {
        this.children = new Vector<NodeInterface>();
        this.content = content;
        this.parent = null;
        this.degree = Degree.EmptyDegree;
        this.weight = Weight.defaultWeight;
    }
    
    public Node(Object content, NodeInterface parent){
        this.children = new Vector<NodeInterface>();
        this.content = content;
        this.parent = parent;
        parent.addChild(this);
    }

    public void addChild(NodeInterface child) {
        children.addElement((Node)child);
    }

    public void setParent(NodeInterface parent) {
        this.parent = parent;
    }
    
    public java.util.Enumeration<NodeInterface> getChildren() {
        return children.elements();
    }
    
    public int numOfChildren(){
    	return children.size();
    }
    
    public Object getContent() {
        return content;
    }
    
    public Degree getDegree() {
        return degree;
    }
    
    public Weight getWeight() {
        return weight;
    }
    
    public NodeInterface getParent() {
        return parent;
    }
    
    public boolean isLeaf() {
        return (children.size() == 0);
    }
    
    public boolean isRoot() {
        return (parent == null);
    }
    
    public void setChildren(Vector<NodeInterface> children) {
        this.children = children;
    }
    
    public void setContent(Object content) {
        this.content = content;
    }
    
    public void setDegree(Degree deg) {
        this.degree = deg;
    }
    
    public void setWeight(Weight w) {
        this.weight = w;
    }
    
    public boolean isClosed(){
        return closed;
    }
    
    /**
     * Close the node.
     * Since subnodes are no longer needed, they are removed.
     * The closed flag is set to true.
     */
    public void close(){
        children.removeAllElements();
        degree = Degree.EmptyDegree;
        weight = Weight.defaultWeight;
        closed = true;
    }
    
    public static Node exampleSetup(){
    	Node root = new Node("A");
    	/*
        Node B = new Node("B", root);
        Node C = new Node("C", root);
        Node D = new Node("D", B);
        */
        return root;
    }
    
    /**
     * An node representing an empty tree.
     */
    public static Node EMPTYNODE = new Node(null);

        
    protected Object content;
    protected NodeInterface parent;
    protected Vector<NodeInterface> children;
    protected boolean closed;
    protected Degree degree;
    protected Weight weight;

    public void prune() {
        children.removeAllElements();
        degree = Degree.EmptyDegree;
        weight = Weight.defaultWeight;
    }

}
