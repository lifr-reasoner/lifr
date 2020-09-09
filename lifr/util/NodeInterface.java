/*
 * Node.java
 *
 * Created on 5. Oktober 2004, 14:02
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
import java.util.Enumeration;
import java.util.Vector;

/**
 * NodeInterface is a generic interface for tree nodes. Each node has several 
 * child nodes, leaf nodes have no child nodes, and all nodes except the root
 * has exactly one parent node. The root node has no parent node.
 *
 * Each node may store an arbitrary object. 
 * @author  sinner
 * @version $Name:  $ $Revision: 1.1 $
 */
public interface NodeInterface {
    
    /**
     * Get the parent node.
     * If this is the root node, null is returned.
     */
    NodeInterface getParent();
    
    /**
     * Get the child nodes as an Enumeration.
     * If this is a leaf node, the Enumeration is empty.
     */
    Enumeration<NodeInterface> getChildren();
    
    /**
     * A node is a container for some object, which is returned from 
     * this method.
     */
    Object getContent();
    
    /**
     * Set the content of a node. This replaces any previous content.
     */
    void setContent(Object content);
    
    /**
     * Set the parent of a node. Any previous parent is replaced.
     * This does not imply that the parent gets a new child node!
     */
    void setParent(NodeInterface parent);
    /**
     * Add a single child to a node.
     * this does not imply that the child node gets a new parent!
     */
    void addChild(NodeInterface child);
    
    /**
     * Set the children of the Node. This replaces any previous children.
     */
    void setChildren(Vector<NodeInterface> children);
   
    /**
     * Check whether this node is root.
     * There is only one root node per tree.
     */
    boolean isRoot();
    
    /**
     * deleta all child nodes
     */
    void prune();
    
    /**
     * Check whether this node is a leaf. This is the case if it has no children.
     */
    boolean isLeaf();
    
    boolean isClosed();
    
    void close();
}
