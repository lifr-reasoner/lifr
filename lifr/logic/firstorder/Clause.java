/*
 * Clause.java
 *
 * Created on 22. September 2004, 14:23
 *
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

package lifr.logic.firstorder;

import java.util.Enumeration;
import java.util.Vector;

import lifr.logic.fuzzy.Degree;
import lifr.util.EmptyEnumerationPredicate;

//import fpocketkrhyper.util.EmptyEnumeration;
/**
 * Creates a new instance of Clause.
 * @author sinner
 */
public class Clause {
    
    /**
     * Creates a new instance of Clause.
     * The head and body vectors are initialized to null;
     */
    protected Clause(){
        this.head=null;
        this.body=null;
        this.degree = Degree.EmptyDegree;
    }
    
    /** 
     * Creates a new instance of Clause.
     * head and body vectors are given as arguments.
     * @param head a Vector of Predicate
     * @param body a Vector of Predicate
     */
    public Clause(Vector<Predicate> head, Vector<Predicate> body) {
        this.head = head;
        this.body = body;
        this.degree = Degree.EmptyDegree;
    }
    
    /** 
     * Creates a new instance of Clause.
     * head and body vectors are given as arguments.
     * @param head a Vector of Predicate
     * @param body a Vector of Predicate
     * @param degree a Degree
     */
    public Clause(Vector<Predicate> head, Vector<Predicate> body, Degree degree) {
        this.head = head;
        this.body = body;
        this.degree = degree;
    }
    
    /**
     * Get the head of the clause.
     * @return an Enumeration of the head literals.
     */
    public final Enumeration<Predicate> getHead(){
        if (head == null){
        	//head = new Vector<Predicate>(0);
            return EmptyEnumerationPredicate.INSTANCE;
        }
        return head.elements();
    }
    
    /**
     * Get the head of the clause.
     * @return a Vector containing the head literals.
     */
    public final Vector<Predicate> getHeadVector(){
        return head;
    }
    
    /**
     * Get the body of the clause.
     * @return a Vector containing the body literals.
     */
    public final Vector<Predicate> getBodyVector(){
        return body;
    }
    
    public Degree getDegree(){
        return degree;
    }
    
    /**
     * Get the body of the clause.
     * @return an Enumeration of the body literals.
     */
    public final Enumeration<Predicate> getBody(){
        if (body == null){
        	//body = new Vector<Predicate>(0);
            return EmptyEnumerationPredicate.INSTANCE;
        }
        return body.elements();
    }
    
    /**
     * Facts are clauses with a single head literal and no body literals
     * @return true if the clause has an empty body and a non-disjunctive head.
     */
    public final boolean isFact(){
        return ((head != null) && (head.size()==1) && (body == null));
    }
    
    /**
     * Queries are clauses with an empty head. Another word for query is constraint.
     * @return true if the head is empty.
     */
    public final boolean isQuery(){
        return (headSize() == 0);
    }
    
    /**
     * Get the number of literals in the head
     * @return an int >= 0
     */
    public final int headSize(){
        if (head == null){
            return 0;
        } else {
            return head.size();
        }
    }
    
    /**
     * Get the number of literals in the body
     * @return an int >= 0
     */
    public final int bodySize(){
        if (body == null){
            return 0;
        } else {
            return body.size();
        }
    }
    
    /**
     * Writes the clause in Protein/KRHyper Syntax to a String
     * @return a String representation of the clause.
     */
    public String toString(){
        StringBuffer s = new StringBuffer();
        for (Enumeration<Predicate> e = getHead(); e.hasMoreElements();){
            Predicate p = (Predicate)e.nextElement();
            if (this.isFact()) p.setDegree(this.getDegree());
            s.append(p.toString());
            if (e.hasMoreElements()){
                s.append(";");
            }
        }
        if (bodySize()>0){
            s.append(":-");
            for (Enumeration<Predicate> e = getBody(); e.hasMoreElements();){
                Predicate p = (Predicate)e.nextElement();
                s.append(p.toString());
                if (e.hasMoreElements()){
                    s.append(",");
                }
            }
        }
        s.append(".");
        return s.toString();
    }
    
    private Vector<Predicate> head;
    private Vector<Predicate> body;
    private Degree degree;
}
