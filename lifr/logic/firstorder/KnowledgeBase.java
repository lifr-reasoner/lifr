/*
 * KnowledgeBase.java
 *
 * Created on 18. Oktober 2004, 16:15
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

import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
/**
 * A knowledge base is a collection of clauses. It can be exported to a tree
 * (see @link{iason.generic.util.Node}, which can then be processed by a
 * reasoning algorithm like krhyper. The clauses are best ordered by the size
 * of their heads, so that reasoning algorithms process disjunctions as late
 * as possible.
 * A knowledge base is fragmented into: ontology, profile, and query
 * methods setXboundary and truncateToX manipulate all vectors corresponding to these fragments
 * @author  sinner
 * @version $Name:  $ $Revision: 1.12 $
 */
public class KnowledgeBase {
    
    /** Creates a new instance of KnowledgeBase with an empty facts Vector
     * and an empty rules Vector.
     *
     */
    public KnowledgeBase() {
        facts = new Vector<Clause>(10);
        rules = new Vector<Clause>(100);
        queries = new Vector<Clause>(10);
        this.setOntologyBoundary();
    }
    
    /** Add a clause to the knowledge base
     * @param c the Clause to add to the knowledge base.
     */
    public void addClause(Clause c){
    	
        if (c.isFact()){
            facts.addElement(c);
            Predicate p = (Predicate)c.getHeadVector().elementAt(0);
            p = LogicFactory.newPredicate(p.name, p.terms, c.getDegree());
            nfact++;
        } else if (c.isQuery()){
            queries.addElement(c);
            nquer++;
        } else {
            rules.addElement(c);
            nrule++;
        }
    
    }
    
    /**
     * Enumerate all facts
     * @return an Enumeration of all facts.
     */
    public Enumeration<Clause> facts(){
        return facts.elements();
    }
    
    public Vector<Clause> getFacts(){
    	return facts;
    }
    
    /**
     * Enumerate all rules
     * @return An enumeration of all rules.
     */
    public Enumeration<Clause> rules(){
        return rules.elements();
    }
    
    public Vector<Clause> getRules(){
    	return rules;
    }
    
    /**
     * Enumerate all queries
     * @return An enumeration of all queries/constraints.
     */
    public Enumeration<Clause> queries(){
        return queries.elements();
    }
    public Vector<Clause> getQueries(){
    	return queries;
    }
    
    /**
     * All known facts (clauses with empty bodies)
     */
    protected Vector<Clause> facts;
    /**
     * All known rules (clauses which are not facts or queries)
     */
    protected Vector<Clause> rules;

    /**
     * All known queries (clauses with empty heads)
     */
    protected Vector<Clause> queries;

    /**
     * size of vectors containing ontology
     */
    protected int sizeOntologyRules;
    protected int sizeOntologyFacts;
    protected int sizeOntologyQueries;
    
    /**
     * size of vectors containing ontology and profile
     */
    protected int sizeProfileRules;
    protected int sizeProfileFacts;
    protected int sizeProfileQueries;
    
    /**
     * size of vectors containing ontology and profile
     */
    protected int sizeQueryRules;
    protected int sizeQueryFacts;
    protected int sizeQueryQueries;
    
    /**
     * set watermark for truncateToOntology
     */
    public void setOntologyBoundary() {
        sizeOntologyRules = rules.size();
        sizeOntologyFacts = facts.size();
        sizeOntologyQueries = queries.size();
        setProfileBoundary(); // must be on top of ontology 
        startQuery();
    }
    
    /**
     * set watermark for truncateToProfile
     */
    public void setProfileBoundary() {
        sizeProfileRules = rules.size();
        sizeProfileFacts = facts.size();
        sizeProfileQueries = queries.size();
        startQuery(); // must be on top of profile
    }

    /**
     * truncate the knowledgebase to the ontology part
     */
    public void truncateToOntology() {
        rules.setSize(sizeOntologyRules);
        facts.setSize(sizeOntologyFacts);
        queries.setSize(sizeOntologyQueries);
        setProfileBoundary(); // everything else is empty then
        startQuery();
    }
    
    /**
     * truncate the knowledgebase to the ontology and profile part
     */
    public void truncateToProfile() {
        rules.setSize(sizeProfileRules);
        facts.setSize(sizeProfileFacts);
        queries.setSize(sizeProfileQueries);
        startQuery(); // no more query left
    }
    
    /**
     * removeQuery deletes clauses that form the query, with respect to startQuery()
     */
    public void removeQuery() {
        rules.setSize(sizeQueryRules);
        facts.setSize(sizeQueryFacts);
        queries.setSize(sizeQueryQueries);
    }
    
    public void removeFacts(){
    	facts.clear();
    	//facts.setSize(0);
    }
    
    public void removeRules(){
    	rules.clear();
    	//rules.setSize(0);
    }
    
    public void removeQueries(){
    	queries.clear();
    	//queries.setSize(0);
    }
    
    /**
     * startQuery marks clauses, to be reset by remove query
     */
    public void startQuery() {
        sizeQueryRules = rules.size();
        sizeQueryFacts = facts.size();
        sizeQueryQueries = queries.size();
    }
    
    /**
     * Empty the knowledge base.
     */
    public void clear() {
        rules.setSize(0);
        facts.setSize(0);
        queries.setSize(0);
    }
    
    public void mergeKnowledgeBase(KnowledgeBase kb){
    	this.facts.addAll(kb.getFacts());
    	this.rules.addAll(kb.getRules());
    	this.queries.addAll(kb.getQueries());
//    	for(Iterator<Clause> iter  = kb.getFacts().iterator(); iter.hasNext();){
//    		Clause f = iter.next();
//    		f.
//    		if(this.facts.)
//    	}
    }
    
    public void setKnowledgeBase(KnowledgeBase kb){
    	this.clear();
    	this.facts = kb.getFacts();
    	this.rules = kb.getRules();
    	this.queries = kb.getQueries();
    }

    /**
     * Get the knowledge base in a String representation in Protein syntax.
     * @return a String representing the knowledge base.
     */
    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append("Facts:\n");
        str.append(facts.toString());
        str.append("\nRules:\n");
        str.append(rules.toString());
        str.append("\nQueries:\n");
        str.append(queries.toString());
        str.append('\n');
        return str.toString();
    }
    
    private int nfact = 0;
    private int nquer = 0;
    private int nrule = 0;
    
}
