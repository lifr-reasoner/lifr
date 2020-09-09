/*
 * DepthFirstSearchEnumeration.java
 *
 * Created on 5. Oktober 2004, 14:47
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
import java.util.Stack;
//import fpocketkrhyper.reasoner.krhyper.*;

/**
 * This Enumeration implementation for trees allows to traverse a tree
 * from a given top node in depth-first-search order. At any time it is possible
 * to ask for the current path stack, which contains all the nodes from top to
 * the current node.
 * This destructive implementation deletes subnodes when backtracking, thus freeing
 * some memory.
 * <p>
 * Warning! The semantics of the closed flag means that that node has been searched to
 * the leaves and is backtracked.
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.2 $
 */
public class DestructiveDepthFirstSearchEnumeration implements Enumeration<NodeInterface>{
    
    /** Creates a new instance of DepthFirstSearchEnumeration */
    public DestructiveDepthFirstSearchEnumeration(NodeInterface top) {
        this.root = top;
        this.currentNode = null;
        this.pathStack = new Stack<NodeInterface>();
        this.enumStack = new Stack<Enumeration<NodeInterface>>();
    }
    
    /**
     * Get the next element in depth-first search manner.
     * When backtracking, the already visited nodes are deleted.
     *
     */
    public final NodeInterface nextElement() {
        if (currentNode == null){
            currentNode = root;
        } else if (((Enumeration<NodeInterface>) enumStack.peek()).hasMoreElements()){
            // Next child node
            currentNode = (NodeInterface)((Enumeration<NodeInterface>) enumStack.peek()).nextElement();
        } else {
            //Backtrack to next child node
            while (!(((Enumeration<NodeInterface>) enumStack.peek()).hasMoreElements())){

                enumStack.pop();
                NodeInterface backNode = (NodeInterface)pathStack.pop();
                boolean allClosed = true;
                for (Enumeration<NodeInterface> e = backNode.getChildren() ; e.hasMoreElements();){
                    if (!((NodeInterface)e.nextElement()).isClosed()){
                        allClosed = false;
                        break;
                    }
                }
                if (allClosed){
                    backNode.close();
                } else { //otherwise simply delete the subnodes
                    if (!((Enumeration<NodeInterface>)enumStack.peek()).hasMoreElements()){
                        backNode.prune();
                    }
                }
            }
            currentNode = (NodeInterface)((Enumeration<NodeInterface>) enumStack.peek()).nextElement();
        }
        pathStack.push(currentNode);
        enumStack.push(currentNode.getChildren());
        
        /*
        //DEBUG
        if (currentNode.getChildren() != null) {
        	for (Enumeration e = enumStack.elements(); e.hasMoreElements();){
        		for (Enumeration ee = ((Enumeration)e.nextElement()); ee.hasMoreElements();){
        			try {Node curr = (Node)ee.nextElement();
        			System.out.println("\n\nDDFEnum Branch:");
        			for (Enumeration eee = ((HyperNode)curr.getContent()).branch.elements() ; eee.hasMoreElements() ;){
            			System.out.println(eee.nextElement().toString());
            		}
            		System.out.println("\nDDFEnum Expansions:");
        			for (Enumeration eee = ((HyperNode)curr.getContent()).expansions.elements() ; eee.hasMoreElements() ;){
            			System.out.println(eee.nextElement().toString());
            		}
            		System.out.println("\nDDFEnum Disjunctions:");
        			for (Enumeration eee = ((HyperNode)curr.getContent()).disjunctions.elements() ; eee.hasMoreElements() ;){
            			System.out.println(eee.nextElement().toString());
            		}
            		System.out.println("\nDDFEnum Deltas:");
        			for (Enumeration eee = ((HyperNode)curr.getContent()).delta.elements() ; eee.hasMoreElements() ;){
            			System.out.println(eee.nextElement().toString());
            		}
            		}
        			catch (Exception ex) {System.out.println(ex);}
        			//System.out.println("DDFEnum children: "+((Node)ee.nextElement()).getContent().toString());
        		}
        	}
        	
        }
        ///////
        */
        
        return currentNode;
    }
    
    
    /**
     * Check whether there are any open branches left to traverse.
     */
    public final boolean hasMoreElements() {
        if (currentNode == null){// in the beginning, we have at least the top node
            return true;
        } else {
            // as long as one Open branch exists, there are more elements
            for (Enumeration<Enumeration<NodeInterface>> e = enumStack.elements(); e.hasMoreElements();){
                if (((Enumeration<NodeInterface>)e.nextElement()).hasMoreElements()){
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     * Get the path stack from top to the current node. In the beginning, it is
     * empty.
     */
    public final Stack<NodeInterface> getPath() {
        return pathStack;
    }
    
    public Stack<Enumeration<NodeInterface>> enumStack;
    protected Stack<NodeInterface> pathStack;
    protected NodeInterface root;
    protected NodeInterface currentNode;
        
}
