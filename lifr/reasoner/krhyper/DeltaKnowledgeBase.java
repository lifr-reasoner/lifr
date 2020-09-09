/*
 * DeltaKnowledgeBase.java
 *
 * Created on 7. Dezember 2004, 14:56
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

package lifr.reasoner.krhyper;

import java.util.Enumeration;
import java.util.Vector;

import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
//import java.util.Hashtable;

/**
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.6 $
 * @todo implementation
 * @todo documentation
 * @todo unit testing
 */
class DeltaKnowledgeBase  extends KnowledgeBase{
    
    /** Creates a new instance of DeltaKnowledgeBase */
    DeltaKnowledgeBase(KnowledgeBase kb){
        super();
        this.facts = new Vector<Clause>(0);
        calculateDeltaClauses(kb.queries());
        calculateDeltaClauses(kb.rules());
        complements = new Vector<Predicate>();
    }
    
    /**
     * After the first reasoning step, we have the first extensions of the knowledge base.
     * These extensions are called delta, since they are new in this step.
     * In every clause in the knowledge base, we create a copy for each literal in
     * the body, where the literal in question is renamed to its delta-counterpart.
     * After calling this, all delta rules are in the deltaKb Knowledge Base.
     */
    private final void calculateDeltaClauses(Enumeration<Clause> clauses){
        while ( clauses.hasMoreElements()){
            Clause c = (Clause)clauses.nextElement();
            for (int i = 0 ; i < c.bodySize(); i++){
                Predicate currentP = (Predicate)c.getBodyVector().elementAt(i);
                // Add a new delta clause
                Vector<Predicate> deltaBody = new Vector<Predicate>(c.bodySize());
                String currentPString = currentP.toString();
                currentPString = currentPString.substring(0, currentPString.indexOf(' '));
                //System.out.println("Pstring: "+currentPString);
                if (currentPString.startsWith("-")){
                    deltaBody.addElement(LogicFactory.getPredicate("-d_"+currentPString.substring(1)));//delta literal is first!)
                } else {
                    deltaBody.addElement(LogicFactory.getPredicate("d_"+currentPString));//delta literal is first!
                }
                for (int j = 0 ; j< c.bodySize(); j++){
                    if (j!=i){ // add all other elements of the body to the delta body
                        deltaBody.addElement((Predicate)c.getBodyVector().elementAt(j));
                    }
                }
                Clause deltaClause = LogicFactory.newClause(c.getHeadVector(), deltaBody);
                addClause(deltaClause);
            }
        }
    }
    
    
    public void addComplementDeltaClause(Clause c){
        Predicate p = (Predicate) c.getBodyVector().firstElement();
        if (!complements.contains(p)){
            complements.addElement(p);
            for (int i = 0 ; i < c.bodySize(); i++){
                Predicate currentP = (Predicate)c.getBodyVector().elementAt(i);
                // Add a new delta clause
                Vector<Predicate> deltaBody = new Vector<Predicate>(c.bodySize());
                String currentPString = currentP.toString();
                if (currentPString.startsWith("-")){
                    deltaBody.addElement(LogicFactory.getPredicate("-d_"+currentPString.substring(1), currentP.getDegree(), currentP.getWeight()));//delta literal is first!)
                } else {
                    deltaBody.addElement(LogicFactory.getPredicate("d_"+currentPString, currentP.getDegree(), currentP.getWeight()));//delta literal is first!
                }
                for (int j = 0 ; j< c.bodySize(); j++){
                    if (j!=i){ // add all other elements of the body to the delta body
                        deltaBody.addElement((Predicate)c.getBodyVector().elementAt(j));
                    }
                }
                Clause deltaClause = LogicFactory.newClause(c.getHeadVector(), deltaBody);
                addClause(deltaClause);
            }
        }
    }
    
    public void addRefutationClause(Predicate p){
        if (!complements.contains(p)){
            complements.addElement(p);
            // Add a new delta clause
            Vector<Predicate> deltaBody = new Vector<Predicate>(2);
            deltaBody.addElement(LogicFactory.getPredicate("d_"+p.toString()));//delta literal is first!
            deltaBody.addElement(p);
            Clause deltaClause = LogicFactory.newClause(null, deltaBody);
            addClause(deltaClause);
        }
    }
    
    private Vector<Predicate> complements;
}
