/*
 * KrHyperComplementSplitting.java
 *
 * Created on 8. Februar 2005, 12:47
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
//import java.util.Stack;
import java.lang.StringIndexOutOfBoundsException;
//import java.lang.Math;

import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.Domain;
import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
import lifr.logic.firstorder.Substitution;
import lifr.logic.firstorder.Term;
import lifr.logic.firstorder.Unification;
import lifr.logic.firstorder.Variable;
import lifr.logic.fuzzy.DegOperator;
import lifr.logic.fuzzy.Degree;
import lifr.logic.fuzzy.Weight;
import lifr.reasoner.ProofNotFoundException;
import lifr.reasoner.Reasoner;
import lifr.util.DestructiveDepthFirstSearchEnumeration;
import lifr.util.Node;
import lifr.util.NodeInterface;
import lifr.util.TimerThread;
import lifr.util.exceptions.TimeoutException;

/**
 * KrHyper is an implementation of the Hyper Tableaux Calculus with some 
 * optimizations.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.21 $
 */
public class KrHyper implements Reasoner {
    
    /** Creates a new instance of KrHyper */
    public KrHyper() {
        kb = null;
        proofTreeRoot = null;
        proofCurrent = null;
        deltaKb = null;
        timer = null;
        exhausted = true;
        countCurrDis = 0;
        prevBranch = new Vector<String>();
        currBranch = new Vector<String>();
        herbrand = null;
        domain = null;
    }
    
    /**
     * give a new knowledgebase to the reasoner.
     * @param kb a knowledge base
     */
    public final void setKnowledgeBase(KnowledgeBase kb) {
        this.kb = kb;
    }
    
    /**
     * get the current knowledge base
     * @return the current knowledge base
     */
    public final KnowledgeBase getKnowledgeBase() {
        return kb;
    }

    /**
     * get the Model found after a successful reason() call. If no model was found,
     * null is returned.
     * The model is the set of elements found in the branch.
     * @return A Vector containing the current proof branch with the model.
     */
    public final Vector<Predicate> getModel(){
        if (proofTreeRoot.isClosed()){
            return null;
        } else {
            Vector<Predicate> model = new Vector<Predicate>();
            for (NodeInterface current = this.proofCurrent ; current != null ; current = current.getParent()){
                for (Enumeration<Predicate> e = ((HyperNode)current.getContent()).branch.elements() ; e.hasMoreElements() ;){
                    Predicate p = (Predicate)e.nextElement();
                    //if (p.getName().charAt(0) != '-'){
                    if ((!p.getName().startsWith("subconcept"))&&(!p.getName().startsWith("-subconcept"))){
                        model.addElement(p);
                    }
                }
            }
            return model;
        }
    }
    
    
    /**
     * Search for a model or refutation. This is the main method of the reasoner
     * which is to be called by external applications.
     * <p>
     * There are three parameters to control the reasoning process: a timeout, a lower bound and an upper
     * bound for the termweight. The timeout and upper bound are additional abort criteria for
     * the reasoning process. If either is reached and no proof or refutation has been found, 
     * a ProofNotFoundException is thrown.
     * <p>
     * Good values are:
     * <ul>
     *<li> timeout: 2000 (2 seconds)
     *<li> minTermWeight: 2 (functions up to a depth of 2, like f(f(x)) )
     * <li> maxTermWeight: 5 (unless you know that it has to search deeper)
     * </ul>
     * @return true when a model was found, false otherwise
     * @param timeout a timeout value in ms after which the reasoner aborts
     * @param minTermWeight The initial termweight up to which the proof tree is expanded.
     * @param maxTermWeight The maximum termeight up to which the proof tree is expanded
     * @throws lifr.reasoner.ProofNotFoundException 
     */
    public final boolean reason(int minTermWeight, int maxTermWeight, int timeout)
    throws ProofNotFoundException {
        timer = new TimerThread(timeout);
        timer.start();
        domain = new Domain(kb);
        deltaKb = new DeltaKnowledgeBase(kb);
//        System.out.println(kb.toString());
        int j = 0;
        int k = 0;
        for (int i = minTermWeight ; (i <= maxTermWeight || maxTermWeight == 0) ;i++){
            setupProof();
            try {
                if (!firstProofStep()){
                    // a refutation was found
                    this.proofCurrent.close();
                    timer.finish();
                    return false;
                }
            } catch (TimeoutException ex){
                throw new ProofNotFoundException(ex.getMessage());
            }
            prevBranch.addElement("root");
            proofsearch:
                for (DestructiveDepthFirstSearchEnumeration search = new DestructiveDepthFirstSearchEnumeration(proofTreeRoot) ; search.hasMoreElements() ;){
                    this.proofCurrent = (Node)search.nextElement();
                    HyperNode currentHyperNode = (HyperNode)this.proofCurrent.getContent();
					//close = false;
                    // expand as much as possible
                    // Each proof step (including the first one) adds expansions
                    // if there is anything to expand
                    // if not, we have to expand the proof tree with a disjunction
                    j++;
                    while (!currentHyperNode.expansions.isEmpty()) {
                    	k++;
//                    	currBranch.removeAllElements();
                    //	System.out.println("\nBEFORE EXPANDING in proofstep #"+j+" for expansion #"+k+" :\n"+this.toString());
                        expand(i);
                        try {
                            if (!proofStep()){
                                // a refutation was found
                                this.proofCurrent.close();
                                continue proofsearch;
                            }
                        } catch (TimeoutException ex){
                            throw (new ProofNotFoundException(ex.getMessage()));
                        }
                    }
                //    System.out.println("\nAFTER EXPANSION:\n"+this.toString());
                    
                    // all horn expansions are done
                    // so expand proof tree with a disjunction
                    if (currentHyperNode.disjunctions.isEmpty()){
                        //
                        if (currentHyperNode.notRangeRestrictedClauses.isEmpty()){
                            if (exhausted){ // exhausted is false when we did not expand up to the full term weight
                                // there is nothing left to do, so we have a model!
                                // @todo make an implementation which enumerates all models
                                timer.finish();
                                return true; //found a model and stop...
                            } //otherwise we have to go on searching
                            continue proofsearch;
                        } else {
                            // add new disjunctions.
                            // non-range restricted clauses are grounded with the herbrand universe
                            rangeRestrictClauses(currentHyperNode, i);
                            handleDisjunction(currentHyperNode);
                            
                        }
                    } else {
                        handleDisjunction(currentHyperNode);
                    }
                //    System.out.println("\nPROOFSEARCH #"+j+" DONE:\n"+this.toString());
                }
                // After traversing the tree, the current Node is the last visited
                // if it was a closed branch, we have to close the parents...
                //                if (!this.proofCurrent.equals(this.proofTreeRoot)){
                if (exhausted){ // there are no more open branches
                    timer.finish();
                    return false;//refutation
                }
        }
        //we were not successful in either finding a model or a refutation
        timer.finish();
        throw (new ProofNotFoundException("Max termweight reached!"));
    }

    /**
     * Tell the reasoner to stop, i.e. force a timeout.
     */
    public final void interruptReasoner(){
        timer.finish();
    }

    /**
     * a selection function for choosing a disjunction.
     * The disjunction with fewest elements is chosen.
     * @return a Vector representing a disjunction.
     */
    private final Vector<Predicate> selectDisjunction(){
    	//close = true;
        Vector<Vector<Predicate>> disjunctions = ((HyperNode)this.proofCurrent.getContent()).disjunctions;
        Vector<Predicate> minDis = (Vector<Predicate>)disjunctions.elementAt(0);
        if (minDis.size() == 2) {
            disjunctions.removeElementAt(0);
            return minDis;
        }
        for (int i = 1 ; i < disjunctions.size();i++){
            Vector<Predicate> currentDis = (Vector<Predicate>)disjunctions.elementAt(i);
            if (currentDis.size()==2){
                disjunctions.removeElementAt(i);
                return currentDis;
            }
            if (currentDis.size() < minDis.size()){
                minDis = currentDis;
            }
        }
        disjunctions.removeElement(minDis);
        return minDis;
    }
    
    
    /**
     * A selection function for choosing a non-range restricted clause whose domain
     * needs to be enumerated. The clause with the smallest head ist returned.
     */
    private final Clause selectDomClause(){
        Vector<Clause> domClauses = ((HyperNode)this.proofCurrent.getContent()).notRangeRestrictedClauses;
        Clause minClause = (Clause)domClauses.elementAt(0);
        if (minClause.headSize() == 2) {
            domClauses.removeElementAt(0);
            return minClause;
        }
        for (int i = 1 ; i < domClauses.size();i++){
            Clause currentClause = (Clause)domClauses.elementAt(i);
            if (currentClause.headSize()==2){
                domClauses.removeElementAt(i);
                return currentClause;
            }
            if (currentClause.headSize() < minClause.headSize()){
                minClause = currentClause;
            }
        }
        domClauses.removeElement(minClause);
        return minClause;
    }
    
    /**
     * Setup the root Node of the proof tree. All Facts from the knowledge base
     * are put into the proof branch.
     */
    private final void setupProof(){
        // create a root node with all facts.
        HyperNode rootContent = new HyperNode();
        for (Enumeration<Clause> e = kb.facts() ; e.hasMoreElements() ;){
            Clause c = (Clause)e.nextElement();
            Predicate currentP = (Predicate)c.getHeadVector().elementAt(0);
            
            //rename all variables...
            Substitution s = new Substitution();
            int n = 0;

            for (Enumeration<Variable> v = currentP.getVariables() ; v.hasMoreElements() ;){

                Variable current = (Variable)v.nextElement();
                if (!s.containsSubstitutionFor(current)){
                    s.addSubstitution(current, LogicFactory.getTerm("X"+n));
                    n++;
                }
            }
            currentP = s.applyToPredicate(currentP);
            ////NEW
            currentP.setDegree(currentP.getDegree().getOperator(), currentP.getDegree().getDegree()*currentP.getWeight().getWeight());
            ///
            rootContent.branch.addElement(currentP);
        }
        proofTreeRoot = new Node(rootContent);
        proofCurrent = proofTreeRoot;
        exhausted = true;
        herbrand = null; // delete the old herbrand universe, so a new one will be enumerated up to
        // the new term weight
        
    }
    
    /**
     * Calculate the extensions for all clauses.
     * Additionally, the delta clauses and the delta extension is computed.
     * Precondition : setupProof has been called
     * @return false if a refutation was found.
     */
    private final boolean firstProofStep() throws TimeoutException{
        for (Enumeration<Clause> e = kb.queries() ; e.hasMoreElements() ;){
            if (timer.isFinished()){
                throw (new TimeoutException("First Proof Step"));
            }
            Clause c = (Clause) e.nextElement();
            //try to close with the current clause
            if (tryToClose(c)){
                return false;
            }
        }
        //now expand the rules
        for (Enumeration<Clause> e = kb.rules() ; e.hasMoreElements() ;){
            if (timer.isFinished()){
                throw (new TimeoutException("First Proof Step"));
            }
            Clause c = (Clause) e.nextElement();
            extensions(c);
        }
        return true;
    }
    
    /**
     * A regular proof step is different from the first proof Step in
     * that extensions are to be calculated with delta clauses, where
     * at least one literal must unify with a delta literal from the
     * previous proof step.
     * @return
     */
    private final boolean proofStep() throws TimeoutException{
        for (Enumeration<Clause> e = deltaKb.queries() ; e.hasMoreElements() ;){
            if (timer.isFinished()){
                throw (new TimeoutException("Proof Step"));
            }
            Clause c = (Clause) e.nextElement();
            //try to close with the current clause
            if (tryToCloseWithDelta(c)){
            	return false;
            }
        }
        //now expand the rules
        for (Enumeration<Clause> e = deltaKb.rules() ; e.hasMoreElements() ;){
            if (timer.isFinished()){
                throw (new TimeoutException("Proof Step"));
            }
            Clause c = (Clause) e.nextElement();
            // calculate all extensions (stored in the current hypernode)
            extensionsWithDelta(c);
        }
        return true;
    }
    
    /**
     * Performs the expansions after a proof step. This excludes the expansion of
     * disjunctions, which are treated separately.
     * Preconditions :
     * The expansions vector of the current Node is not empty
     * Postconditions :
     * the expansions vector of the current node is empty
     * the delta vector of the current node contains the corresponding delta literals
     * the branch contains the expansions
     * Only those expansions are copied which meet the regularity constraints, i.e.
     * are not subsumed by a literal from the branch.
     * @param maxTermWeight
     */
    private final void expand(int maxTermWeight){
        // check which expansions to add to the branch.
        HyperNode currentHyperNode = (HyperNode)this.proofCurrent.getContent();
        //delete the old deltas
        currentHyperNode.delta.removeAllElements();
        for (Enumeration<Predicate> e = currentHyperNode.expansions.elements() ; e.hasMoreElements() ;){
            Predicate currentP = (Predicate) e.nextElement();
            if (currentP.getTermWeight()<=maxTermWeight){
                boolean canAdd = true;
                nodes:
                    for (NodeInterface current = this.proofCurrent ; current != null ; current = current.getParent()){
                        for (Enumeration<Predicate> b = ((HyperNode)current.getContent()).branch.elements() ; b.hasMoreElements() ;){
                            Predicate currentB = (Predicate)b.nextElement();
                            if (currentB.subsumes(currentP)){
                                canAdd = false;
                                if (currentB.getDegree() == Degree.EmptyDegree) currentB.setDegree(new Degree(currentP.getDegree(), currentB.getWeight()));
                                else if (currentP.getDegree() == Degree.EmptyDegree) {} 
               					else {
               						//make proper Kleene-Dienes implication with max(1-x, y) - not max{x,y} as it was till now
               						Degree kdd = new Degree(currentB.getDegree().getOperator(), currentB.getDegree().invertDegree());
               						Degree foomax = Degree.relationDegree;
               						//currentB = x ; currentP = y
               						//NEW
               						currentB.setDegree(new Degree(getMaxDegree(kdd, currentP.getDegree()), Weight.defaultWeight));
               						//OLD
//               						currentB.setDegree(new Degree(getMaxDegree(currentB.getDegree(), currentP.getDegree()), Weight.defaultWeight));
               						//NEW
               						currentP.setDegree(new Degree(getMaxDegree(kdd, currentP.getDegree()), Weight.defaultWeight));
               						//OLD
//               						currentP.setDegree(new Degree(getMaxDegree(currentB.getDegree(), currentP.getDegree()), Weight.defaultWeight));
               					}
                				if (!currentB.unary()) currentB.setDegree(Degree.relationDegree);
                                
                                break nodes;
                            }
                        }
                    }
                    if (canAdd){
                        //rename all variables...
                        Substitution s = new Substitution();
                        int n = 0;
                        for (Enumeration<Variable> v = currentP.getVariables() ; v.hasMoreElements() ;){
                            Variable current = (Variable)v.nextElement();
                            if (!s.containsSubstitutionFor(current)){
                                s.addSubstitution(current, LogicFactory.getTerm("X"+n));
                                n++;
                            }
                        }
                        currentP = s.applyToPredicate(currentP);
                        checkBranchLiteralAgainstDisjunctions(currentP);
                        currentHyperNode.branch.addElement(currentP);
                        try
                        {
                        String currentPString = currentP.toString();
//                        System.out.println("This is my branch: " + currentPString);
                        currentPString = currentPString.substring(0, currentPString.indexOf(' '));
                        if (currentPString.startsWith("-")){
                            currentHyperNode.delta.addElement(LogicFactory.newPredicate("-d_"+currentP.name, currentP.terms, currentP.getDegree(), currentP.getWeight()));
                        } else {
                            currentHyperNode.delta.addElement(LogicFactory.newPredicate("d_"+currentP.name, currentP.terms, currentP.getDegree(), currentP.getWeight()));
                        }
                        /*
                        if (currentPString.startsWith("-")){
                            currentHyperNode.delta.addElement(LogicFactory.getPredicate("-d_"+currentPString.substring(1), currentP.getDegree(), currentP.getWeight()));
                        } else {
                        	currentHyperNode.delta.addElement(LogicFactory.getPredicate("d_"+currentPString, currentP.getDegree(), currentP.getWeight()));
                        }
                        */
                        }catch (StringIndexOutOfBoundsException ex){} 
                        /*
                        if (currentPString.startsWith("-")){
                            currentHyperNode.delta.addElement(LogicFactory.newPredicate("-d_"+currentP.name, currentP.terms));
                        } else {
                            currentHyperNode.delta.addElement(LogicFactory.newPredicate("d_"+currentP.name, currentP.terms));
                        }
                        */
                    }
            } else {
                exhausted = false;
            }
        }
        //delete the old expansions
        currentHyperNode.expansions.removeAllElements();
    }
    
    /**
     * Calculate all extensions of a clause, given the current branch.<br>
     * This is how it is implemented:
     * <ul>
     * <li> if the clause has an empty body, the head is added to the open disjunctions and an
     * empty Enumeration of extensions is returned.
     * <li> otherwise a depth first search over all possible extensions is performed:
     * <li> the nodes of the tree contain substitutions for their respective Predicate in
     * the body
     * <li> if a substitution is found which makes the branch a model for the body,
     * we have several possibilities:
     * <li><ul>
     * <li>the head is empty: refutation
     * <li>the head has one predicate: add it to the extensions
     * <li>the head has more predicates: add them to the open disjunctions.
     * </ul>
     * </ul>
     *
     * @param c the clause for which to calculate extensions
     */
    private final void extensions(Clause c) throws TimeoutException{
        // if this is a simple disjunction (facts have already been processed)
        Vector<Degree> degrees = new Vector<Degree>();
        if (c.bodySize() == 0){
            Vector<Predicate> head = c.getHeadVector();
            if (checkDisjunctionAgainstBranch(head, Degree.EmptyDegree, Weight.defaultWeight)){
                if (!containsSharedVariables(head)){
                    ((HyperNode)this.proofCurrent.getContent()).disjunctions.addElement(head);
                } else {
                    Clause domClause = addDomainPredicates(head);
                    ((HyperNode)this.proofCurrent.getContent()).notRangeRestrictedClauses.addElement(domClause);
                }
            }
        } else {
        	
            Node rootNode = new Node(null);// root node
            Vector<Predicate> body = c.getBodyVector();
            Degree currDeg = Degree.EmptyDegree;
            boolean isRelation = false;
            boolean add = false;
            boolean some = true;
            Vector<Degree> relDegs = new Vector<Degree>();
            
            if (c.bodySize() == 2){
            	Predicate p = (Predicate)body.elementAt(1);
            	//Check if a quantifying relation exists
            	if (!p.unary()){
            		isRelation = true;
            		//Last element of a quantification axiom from DL transformation
            		//is always the quantifying property
            		Predicate pu = (Predicate)body.elementAt(0);
            		//Check whether it is an existential or a universal quantifier
            		if (pu.getVars().elementAt(0) == p.getVars().elementAt(0)) some = false;
            	}
        	}
            for (DestructiveDepthFirstSearchEnumeration e = new DestructiveDepthFirstSearchEnumeration(rootNode) ; e.hasMoreElements();){
                if (timer.isFinished()){
                    throw new TimeoutException("Extensions of Clause "+c.toString());
                }
                Node current = (Node)e.nextElement();
                //SUBSTITUTION EXISTS FOR ALL PREDICATES IN BODY (numOfPreds + root)
                //EXPAND HEAD ACCORDING TO SUBST IN BODY 
                if (e.getPath().size() == (body.size() + 1)){
                    //We have a possible extension!
                    //this means that we can apply the substitution to the head and
                    // add it to the extensions
                    expansion(c, e);
                //SUBSTITUTION DOES NOT EXISTS FOR ALL PREDICATES IN BODY - TRY TO FIND SUBST 
                //FOR *ALL* PREDS IN BODY 
                } else {
                    Predicate p = (Predicate)body.elementAt(e.getPath().size()-1);//To curr pred (0 - body.size) - edw to 1o
                    Predicate minR = null;
                    if (isRelation)	minR = (Predicate)body.elementAt(0);
                    //apply all substitutions already active in this branch
                    for (int i = 1 ; i< e.getPath().size() ; i++){
                        Substitution s = (Substitution)((Node)e.getPath().elementAt(i)).getContent();
                        p = s.applyToPredicate(p);
                    }
                    // check for a new substitution with respect to the branch
                    // each ProofNode contains only the diff of the complete branch
                    //CHECK BODY TO FIND A SUBSTITUTION FOR THE HEAD
                    
                    for (NodeInterface pcurrent = this.proofCurrent ; pcurrent != null ; pcurrent = pcurrent.getParent()){
                        for (Enumeration<Predicate> branch = ((HyperNode)pcurrent.getContent()).branch.elements(); branch.hasMoreElements();){
                            Predicate b = (Predicate)branch.nextElement();
                            
                            //now we have to make sure to rename the variables!
                            //Create a Substitution, which replaces all Variables with new ones.
                            b = b.renameVariables(e.getPath().size());
                            //unify
                            Substitution s = Unification.unify(p, b);
                            if (s != null){ // we have a substitution for p
                            	currDeg = b.getDegree();
                                Node sub = new Node(s, current);
                                if (e.getPath().size() == (body.size())) add = true;
                        		degrees.addElement(currDeg);
                            }
                            
                            if (isRelation && (e.getPath().size() < 2)){
                            	Substitution sr = Unification.unify(minR, b);
                            	if (sr != null) {
                            		currDeg = b.getDegree();
                            		relDegs.addElement(currDeg);
                            	}
                            }
                            
                        }
                        
                    }
                     
                    Degree newDeg = Degree.EmptyDegree;
                    
                    if (!isRelation){
                    	if (degrees.size() != 0)
                    		newDeg = minDegree(degrees);
                    	if ((body.size() > 1)&&(newDeg.getOperator() != null)) currDeg = newDeg;
                    }else{
                    		if (relDegs.size() != 0){
                    			if (some){
                    				newDeg = maxDegree(relDegs);
                    			}else{
                    				newDeg = minDegree(relDegs);
                    			}
                    		}
                   		
                   		if (relDegs.size() > 1) currDeg = newDeg;
                   		else if (relDegs.size() != 0) currDeg = (Degree)relDegs.elementAt(0);
                   		else currDeg = Degree.EmptyDegree;
                    }
                    
                    if (add) ((Node)e.getPath().elementAt(e.getPath().size() - 1)).setDegree(currDeg);
                    else ((Node)e.getPath().elementAt(e.getPath().size() - 1)).setDegree(Degree.EmptyDegree);
                }
                
            }
        }
    }
    
    /**
     * Calculate all extensions, where each first literal from the body has
     * to unify with one of the delta literals
     * @param c
     * @return
     */
    private final void extensionsWithDelta(Clause c) throws TimeoutException{
//    	System.out.println("EXTEND: "+c.toString());
    	Vector<Degree> degrees = new Vector<Degree>();
        if (c.bodySize() > 0){ //disjunctions are ignored, since they have been processed already (in the first extensions step)
            Node rootNode = new Node(null);// root node
            Vector<Predicate> body = c.getBodyVector();
            Degree currDeg = Degree.EmptyDegree;
            boolean isRelation = false;
            boolean add = false;
            boolean some = true;
            Vector<Degree> relDegs = new Vector<Degree>();
                        
            if (c.bodySize() == 2){
            	Predicate p1 = (Predicate)body.elementAt(0);
            	Predicate p2 = (Predicate)body.elementAt(1);
            	//Check if a quantifying relation exists
            	if ((!p1.unary())||(!p2.unary())){
            		isRelation = true;
            		//Check whether it is an existential or a universal quantifier
            		if (p1.getVars().elementAt(0) == p2.getVars().elementAt(0)) some = false;
            	}
        	}
            
            for (DestructiveDepthFirstSearchEnumeration e = new DestructiveDepthFirstSearchEnumeration(rootNode) ; e.hasMoreElements();){
                if (timer.isFinished()){
                    throw new TimeoutException("Extensions of Delta Clause"+c.toString());
                }
                Node current = (Node)e.nextElement();
                if (e.getPath().size() == (body.size() + 1)){
                    //We have a possible extension!
//                	System.out.println("EXPANSION: "+c.toString());
                    expansion(c, e);
                } else {
                    Predicate p = (Predicate)body.elementAt(e.getPath().size()-1);
                    Predicate minR = null;
                    if (e.getPath().size() == 1){ //p is a delta literal
                    	if (isRelation && p.unary()) minR = p;
                        for (Enumeration<Predicate> deltaliterals = ((HyperNode)this.proofCurrent.getContent()).delta.elements() ;
                        deltaliterals.hasMoreElements() ;){
                            Predicate d = (Predicate)deltaliterals.nextElement();
                            //now we have to make sure to rename the variables!
                            d = d.renameVariables(e.getPath().size());
                            //unify
                            Substitution s = Unification.unify(p, d);
                            if (s != null){ // we have a substitution for p
                                Node sub = new Node(s, current);
                                currDeg = d.getDegree();
                                if (e.getPath().size() == (body.size())) add = true;
                        		degrees.addElement(currDeg);
                            }
                            if (isRelation && p.unary()){
                            	Substitution sr = Unification.unify(minR, d);
                            	if (sr != null) {
                            		currDeg = d.getDegree();
                            		relDegs.addElement(currDeg);
                            	}
                            }
                        }
                    } else {
                    	if (isRelation && p.unary()) minR = p;
                        // apply all substitutions to p
                        for (int i = 1 ; i< e.getPath().size() ; i++){
                            Substitution s = (Substitution)((Node)e.getPath().elementAt(i)).getContent();
                            p = s.applyToPredicate(p);
                        }
                        // check all Literals in the current branch
                        // each ProofNode contains only the diff of the complete branch
                        for (NodeInterface pcurrent = this.proofCurrent ; pcurrent != null ; pcurrent = pcurrent.getParent()){
                            for (Enumeration<Predicate> branch = ((HyperNode)pcurrent.getContent()).branch.elements(); branch.hasMoreElements();){
                                Predicate b = (Predicate)branch.nextElement();
                                //now we have to make sure to rename the variables!
                                //Create a Substitution, which replaces all Variables with new ones.
                                b = b.renameVariables(e.getPath().size());
                                //unify
                                Substitution s = Unification.unify(p, b);
                                if (s != null){ // we have a substitution for p
                                    currDeg = b.getDegree();
                                    Node sub = new Node(s, current);
                                    if (e.getPath().size() == (body.size())) add = true;
                        			degrees.addElement(currDeg);
                                }
                                if (isRelation && p.unary()){
                            		Substitution sr = Unification.unify(minR, b);
                            		if (sr != null) {
                            			currDeg = b.getDegree();
                            			relDegs.addElement(currDeg);
                            		}
                            	}
                            	
                            }
                        }
                        Degree newDeg = Degree.EmptyDegree;
                        if (!isRelation){
                    		if (degrees.size() != 0) newDeg = minDegree(degrees);
                    		if ((body.size() > 1)&&(newDeg.getOperator() != null)) currDeg = newDeg;
                    	}else{
                    		if (relDegs.size() != 0){
                    			if (some){
                    				newDeg = maxDegree(relDegs);
                    			}else{
                    				newDeg = minDegree(relDegs);
                    			}
                    		}
                   		
                    		if (relDegs.size() > 1) currDeg = newDeg;
                    		else if (relDegs.size() != 0) currDeg = (Degree)relDegs.elementAt(0);
                    		else currDeg = Degree.EmptyDegree;
                    	}
                    
                    }
                    if (add) ((Node)e.getPath().elementAt(e.getPath().size() - 1)).setDegree(currDeg);
                    else ((Node)e.getPath().elementAt(e.getPath().size() - 1)).setDegree(Degree.EmptyDegree);
                }
            }
        } else {
            System.err.println("Oops, something wrong !");
        }
    }
    
    private final void expansion(Clause c, DestructiveDepthFirstSearchEnumeration e){
//        System.out.println(c.toString());
    	if (c.headSize() == 1){
        	boolean isRelation = false;
            Predicate p = (Predicate)c.getHeadVector().elementAt(0);
            Weight w = p.getWeight();
            if (!p.unary())
           		isRelation = true;
            Degree deg = Degree.EmptyDegree;
            try{
            	deg = (Degree)((Node)e.getPath().elementAt(e.getPath().size() - 2)).getDegree();
            }catch (Exception ex){}
            for (int i = 1 ; i< e.getPath().size() ; i++){
                try{
                	Substitution s = (Substitution)((Node)e.getPath().elementAt(i)).getContent();
                	if (s == null) {} 
                	p = s.applyToPredicate(p);
                	try{
                		//deg = (Degree)((Node)e.getPath().elementAt(i - 1)).getDegree();
                		if (p.getDegree() == Degree.EmptyDegree) p.setDegree(new Degree(deg, w));
                		else if (deg == Degree.EmptyDegree) {} 
                		else p.setDegree(new Degree(getMaxDegree(deg, p.getDegree()), w));
                		if (isRelation) p.setDegree(Degree.relationDegree);
                	}catch (Exception ex){} 

                	s = null;
            	}catch (Exception ex) {} 
            }
//            System.out.println(p.toString());
            ((HyperNode)this.proofCurrent.getContent()).expansions.addElement(p);
        } else {
        	boolean isRelation = false;
        	Degree deg = Degree.EmptyDegree;
        	Weight w = Weight.defaultWeight;
            Vector<Predicate> substHead = new Vector<Predicate>(c.headSize());
            deg = (Degree)((Node)e.getPath().elementAt(e.getPath().size() - 2)).getDegree();
            for (Enumeration<Predicate> heade = c.getHead() ; heade.hasMoreElements() ;){
                Predicate p = (Predicate)heade.nextElement();
                w = p.getWeight();
                if (!p.unary())
           			isRelation = true;
                try{
//            		deg = (Degree)((Node)e.getPath().elementAt(i - 1)).getDegree();
            		if (isRelation) p.setDegree(Degree.relationDegree);
            	}catch (Exception ex){}
                for (int i = 1 ; i< e.getPath().size() ; i++){
                    Substitution s = (Substitution)((Node)e.getPath().elementAt(i)).getContent();
                    p = s.applyToPredicate(p);
                }
                p.setWeight(w);
                p.setDegree(new Degree(deg, w));
                substHead.addElement(p);
            }
            if (checkDisjunctionAgainstBranch(substHead, deg, w)){
                if (containsSharedVariables(substHead)){
                    Clause domClause = addDomainPredicates(substHead);
                    ((HyperNode)this.proofCurrent.getContent()).notRangeRestrictedClauses.addElement(domClause);
                } else {
                    ((HyperNode)this.proofCurrent.getContent()).disjunctions.addElement(substHead);
                }
            }
        }
    }
    
    /**
     * Add domain predicates for all shared variables to the body of a given 
     * head.
     */
    private final Clause addDomainPredicates(Vector<Predicate> head){
        Vector<Predicate> domBody = new Vector<Predicate>();
        int i = 0;
        for (Enumeration<Variable> e = getSharedVariables(head).elements() ; e.hasMoreElements() ;){
        	i++;
            Vector<Term> body = new Vector<Term>(1);
            body.addElement(e.nextElement());
            domBody.addElement(LogicFactory.newPredicate("dom", body));
        }
        return LogicFactory.newClause(head, domBody);        
    }
    
    /**
     * Try to close the current branch with a query (clause with empty head)
     */
    private final boolean tryToClose(Clause c) throws TimeoutException{
        Node rootNode = new Node(null);// root node
        Vector<Predicate> body = c.getBodyVector();
        boolean overdegree = false;
        Vector<Degree> degrees = new Vector<Degree>();
        
        for (DestructiveDepthFirstSearchEnumeration e = new DestructiveDepthFirstSearchEnumeration(rootNode) ; e.hasMoreElements();){
            if (timer.isFinished()){
                throw new TimeoutException("Trying to close "+c.toString());
            }
            Node current = (Node)e.nextElement();
            
            // we can close the branch
            if ((e.getPath().size() == (body.size() +1))){ 
                // close the branch
                //this.proofCurrent.close();
                for (int i = 0; i < degrees.size(); i++){
                	try 
                	{
                		Degree d = (Degree)degrees.elementAt(i);
                		if (!d.isInterval()){
                			if ((d.getOperator() == DegOperator.SMALLER_EQUAL)||(d.getOperator() == DegOperator.SMALLER)) 
                				overdegree = false;
                		}
                	}catch (Exception ex){}
                }
                if (overdegree) {
                	this.proofCurrent.close();
                	return true;
                }
                else return false;
            } else {
            	
                Predicate p = (Predicate)body.elementAt(e.getPath().size()-1); 
                //apply all substitutions already active in this branch
                for (int i = 1 ; i< e.getPath().size() ; i++){
                    Substitution s = (Substitution)((Node)e.getPath().elementAt(i)).getContent();
                    p = s.applyToPredicate(p);
                    Degree deg = p.getDegree();
                    //deg.setDegree(p.getDegree().getDegree()*p.getWeight().getWeight());
        			degrees.addElement(deg); 
        			if (!deg.isInterval()){
                		if (((deg.getOperator() == DegOperator.GREATER)||(deg.getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        				overdegree = true;
                	}else {
                		if (((deg.getUpperBound().getOperator() == DegOperator.GREATER)||(deg.getUpperBound().getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        				overdegree = true;
                	}
                }
                
                
                // check for a new substitution with respect to the branch
                // each ProofNode contains only the diff of the complete branch
                for (NodeInterface pcurrent = this.proofCurrent ; pcurrent != null ; pcurrent = pcurrent.getParent()){
                    for (Enumeration<Predicate> branch = ((HyperNode)pcurrent.getContent()).branch.elements(); branch.hasMoreElements();){
                        Predicate b = (Predicate)branch.nextElement();
                        //now we have to make sure to rename the variables!
                        //Create a Substitution, which replaces all Variables with new ones.
                        b = b.renameVariables(e.getPath().size());
                        //unify
                        Substitution s = Unification.unify(p, b);
                        if (s != null){// we have a substitution for p
                            Predicate temp = s.applyToPredicate(p);
                            if (!temp.subsumes(p)){
                            	Degree deg = temp.getDegree();
        						degrees.addElement(deg); 
        						if (!deg.isInterval()){
                					if (((deg.getOperator() == DegOperator.GREATER)||(deg.getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        								overdegree = true;
                				}else {
                					if (((deg.getUpperBound().getOperator() == DegOperator.GREATER)||(deg.getUpperBound().getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        								overdegree = true;
                				}
                            }
                            Node sub = new Node(s, current);
                        }
                    }
                    
                }
            }
            
        }
        
        return false;
    }
    
    
    /**
     * Try to close the current branch with a query (clause with empty head)
     * @param c
     * @return true if we could close the current branch
     */
    private final boolean tryToCloseWithDelta(Clause c) throws TimeoutException{
    	Node rootNode = new Node(null);// root node
        Vector<Predicate> body = c.getBodyVector();
        boolean overdegree = false;
        Vector<Degree> degrees = new Vector<Degree>();
        boolean degConflict = false;
        
        for (DestructiveDepthFirstSearchEnumeration e = new DestructiveDepthFirstSearchEnumeration(rootNode) ; e.hasMoreElements();){
            if (timer.isFinished()){
                throw new TimeoutException("Try to close Delta Clause"+c.toString());
            }
            Node current = (Node)e.nextElement();
            if ((e.getPath().size() == (body.size() +1))||degConflict){
            	//proofCurrent.close();
                for (int i = 0; i < degrees.size(); i++){
                	try 
                	{
                		Degree d = (Degree)degrees.elementAt(i);
                		if (!d.isInterval()){
                			if ((d.getOperator() == DegOperator.SMALLER_EQUAL)||(d.getOperator() == DegOperator.SMALLER)) 
                				overdegree = false;
                		}
                	}catch (Exception ex){}
                }
                
                if (overdegree||degConflict) {
//                	System.out.println("Conflict here: "+c.toString());
                	proofCurrent.close();
                	return true;
                }
                else return false;
            } else {
                Predicate p = (Predicate)body.elementAt(e.getPath().size()-1);
                if (e.getPath().size() == 1){ //p is a delta literal
                	if ((p.getName()).equalsIgnoreCase("d_degreeconflict")) {
                		degConflict = true;
                		Node sub = new Node(null, current);
                	}
                    for (Enumeration<Predicate> deltaliterals = ((HyperNode)this.proofCurrent.getContent()).delta.elements() ;
                    deltaliterals.hasMoreElements() ;){
                        Predicate d = (Predicate)deltaliterals.nextElement();
                        //now we have to make sure to rename the variables!
                        d = d.renameVariables(e.getPath().size());
                        //unify
                        Substitution s = Unification.unify(p, d);
                        if (s != null){// we have a substitution for p
                        	Predicate temp = s.applyToPredicate(p);
                            if (!temp.subsumes(p)){
                            	Degree deg = temp.getDegree();
        						degrees.addElement(deg); 
        						if (!deg.isInterval()){
                					if (((deg.getOperator() == DegOperator.GREATER)||(deg.getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        								overdegree = true;
                				}else {
                					if (((deg.getUpperBound().getOperator() == DegOperator.GREATER)||(deg.getUpperBound().getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        								overdegree = true;
                				}
                            }
                            Node sub = new Node(s, current);
                        }
                    }
                } else {
                    // apply all substitutions to p
                    for (int i = 1 ; i< e.getPath().size() ; i++){
                        Substitution s = (Substitution)((Node)e.getPath().elementAt(i)).getContent();
                        p = s.applyToPredicate(p);
                        Degree deg = p.getDegree();
        				degrees.addElement(deg); 
        				if (!deg.isInterval()){
                			if (((deg.getOperator() == DegOperator.GREATER)||(deg.getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        						overdegree = true;
                		}else {
                			if (((deg.getUpperBound().getOperator() == DegOperator.GREATER)||(deg.getUpperBound().getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        						overdegree = true;
                		}
                    }
                    // check all Literals in the current branch
                    // each ProofNode contains only the diff of the complete branch
                    for (NodeInterface pcurrent = this.proofCurrent ; pcurrent != null ; pcurrent = pcurrent.getParent()){
                        for (Enumeration<Predicate> branch = ((HyperNode)pcurrent.getContent()).branch.elements(); branch.hasMoreElements();){
                            Predicate b = (Predicate)branch.nextElement();
                            //now we have to make sure to rename the variables!
                            //Create a Substitution, which replaces all Variables with new ones.
                            b = b.renameVariables(e.getPath().size());
                            //unify
                            Substitution s = Unification.unify(p, b);
                            if (s != null){// we have a substitution for p
                            	Predicate temp = s.applyToPredicate(p);
                            	if (!temp.subsumes(p)){
                            		Degree deg = temp.getDegree();
        							degrees.addElement(deg); 
        							if (!deg.isInterval()){
                						if (((deg.getOperator() == DegOperator.GREATER)||(deg.getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        									overdegree = true;
                					}else {
                						if (((deg.getUpperBound().getOperator() == DegOperator.GREATER)||(deg.getUpperBound().getOperator() == DegOperator.GREATER_EQUAL)) && (deg.getDegree() >= 0.5))
        									overdegree = true;
                					}
                           		}
                                Node sub = new Node(s, current);
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if a given disjunction is not already satisfied by the current branch.
     * If yes, false is returned, otherwise true.
     * @returns true if it has to be added to the current disjunctions, false if it can be ignored
     */
    private final boolean checkDisjunctionAgainstBranch(Vector<Predicate> disjunction, Degree degree, Weight weight){
        for (NodeInterface current = this.proofCurrent ; current != null ; current = current.getParent()){
            for (Enumeration<Predicate> e = ((HyperNode)current.getContent()).branch.elements() ; e.hasMoreElements() ;){
                Predicate branchP = (Predicate)e.nextElement();
                for (Enumeration<Predicate> disje = disjunction.elements() ; disje.hasMoreElements() ;){
                    Predicate disjP = (Predicate)disje.nextElement();
                    if (branchP.subsumes(disjP)){
                    	if (branchP.getDegree() == Degree.EmptyDegree) branchP.setDegree(new Degree(degree, branchP.getWeight()));
               			else if (disjP.getDegree().getDegree()*disjP.getWeight().getWeight() >= branchP.getDegree().getDegree()) 
               				branchP.setDegree(new Degree(degree, Weight.defaultWeight));
                		if (!branchP.unary()) branchP.setDegree(Degree.relationDegree);
                        return false;
                    }
                }
            }
        }
        return true;
        
    }
    
    /**
     * After we add a literal to the current proof branch, we can check all
     * open disjunctions if they are satisfied by the new literal.
     * If a disjunction is satisfied, it is deleted.
     */
    private final void checkBranchLiteralAgainstDisjunctions(Predicate p){
        Vector<Vector<Predicate>> disjunctions = ((HyperNode)this.proofCurrent.getContent()).disjunctions;
        disjenum:
            for (Enumeration<Vector<Predicate>> e = disjunctions.elements() ; e.hasMoreElements() ;){
                Vector<Predicate> currentD = (Vector<Predicate>)e.nextElement();
                for (Enumeration<Predicate> predicates = currentD.elements();predicates.hasMoreElements();){
                    Predicate currentP = (Predicate)predicates.nextElement();
                    if (p.subsumes(currentP)){
                        //delete the disjunction
                        if (currentP.getDegree() == Degree.EmptyDegree) currentP.setDegree(p.getDegree());
               			else if (p.getDegree().getDegree() >= currentP.getDegree().getDegree()) currentP.setDegree(p.getDegree());
                		if (!currentP.unary()) currentP.setDegree(Degree.relationDegree);
                        disjunctions.removeElement(currentD);
                        continue disjenum;
                    }
                }
            }
    }
    
    private final Vector<Variable> getSharedVariables(Vector<Predicate> disjunction){
        Vector<Variable> vars = new Vector<Variable>();
        for (int i = 0 ; i < disjunction.size()-1 ; i++){
            Predicate pi = (Predicate)disjunction.elementAt(i);
            for (int j = i+1 ; j < disjunction.size() ; j++){
                Predicate pj = (Predicate)disjunction.elementAt(j);
                // now compare all Variables ...
                varEnum:
                    for (Enumeration<Variable> vari = pi.getVariables() ; vari.hasMoreElements() ; ){
                        Variable vi = (Variable)vari.nextElement();
                        for (Enumeration<Variable> varj = pj.getVariables() ; varj.hasMoreElements() ; ){
                            Variable vj = (Variable)varj.nextElement();
                            if (vi.equals(vj)){
                                if (!vars.contains(vi)){
                                    vars.addElement(vi);
                                }
                            }
                        }
                    }
            }
        }
        return vars;
    }
    
    /** check wether a disjunction contains shared variables
     * @return true if two literals share a common variable.
     */
    private final boolean containsSharedVariables(Vector<Predicate> disjunction){
        if (disjunction.size() > 1){
            for (int i = 0 ; i < disjunction.size()-1 ; i++){
                Predicate pi = (Predicate)disjunction.elementAt(i);
                for (int j = i+1 ; j < disjunction.size() ; j++){
                    Predicate pj = (Predicate)disjunction.elementAt(j);
                    // now compare all Variables ...
                    varEnum:
                        for (Enumeration<Variable> vari = pi.getVariables() ; vari.hasMoreElements() ; ){
                            Variable vi = (Variable)vari.nextElement();
                            for (Enumeration<Variable> varj = pj.getVariables() ; varj.hasMoreElements() ; ){
                                Variable vj = (Variable)varj.nextElement();
                                if (vi.equals(vj)){
                                    return true;
                                }
                            }
                        }
                }
            }
        }
        return false;
    }
    
    private final void rangeRestrictClauses(HyperNode currentHyperNode, int currentTermweight){
        Clause clause = selectDomClause();
        Vector<Clause> newDisjunctions = new Vector<Clause>();
        newDisjunctions.addElement(LogicFactory.newClause(clause.getHeadVector(), null)); // remove the dom body
        if (herbrand == null){ // we have to enumerate the herbrand universe once for each term weight
            herbrand = domain.getHerbrandUniverse(currentTermweight);
        }
        
        //DEBUG
        else{
        	/*
        	for (Enumeration e = herbrand.elements() ; e.hasMoreElements() ;){
                if (e.hasMoreElements()) System.out.println("HERBRAND UNIVERSE: "+e.nextElement());
                else System.out.println("EMPTY HERBRAND UNIVERSE");
            }
            */
        }
        /////
        
        Vector<Variable> vars = new Vector<Variable>();
        for (Enumeration<Predicate> doms = clause.getBody() ; doms.hasMoreElements() ; ){
            Predicate domp = (Predicate) doms.nextElement();
            for (Enumeration<Variable> vare = domp.getVariables() ; vare.hasMoreElements() ;){
                Variable v = (Variable)vare.nextElement();
                if (!vars.contains(v)){
                    vars.addElement(v);
                }
            }
        }
        // Now we have both all domain variables and the herbrand universe, so we generate all ground instances
        for (int i = 0 ; i< vars.size() ; i++){
            Vector<Clause> temp = new Vector<Clause>();
            for (Enumeration<Term> he = herbrand.elements() ; he.hasMoreElements() ;){
                Substitution s = new Substitution();
                s.addSubstitution((Variable)vars.elementAt(i), (Term) he.nextElement());
                for (Enumeration<Clause> substClauses = newDisjunctions.elements() ; substClauses.hasMoreElements() ;){
                	//System.out.println("Containing subst for var: "+((Variable)vars.elementAt(i)).toString()+", for predicate: "+herbrand.elementAt(i).toString());
                    Clause c = (Clause) substClauses.nextElement();
                    temp.addElement(s.applyToClause(c));
                }
            }
            newDisjunctions = temp;
        }
        // now add all ground instances to the current disjunctions
        for (Enumeration<Clause> e = newDisjunctions.elements() ; e.hasMoreElements() ;){
            currentHyperNode.disjunctions.addElement(((Clause)e.nextElement()).getHeadVector());
        }
    }

    
    private final void handleDisjunction(HyperNode currentHyperNode){
        //create a new node with a disjunction predicate
        //search for a good disjunction
        //Vector dis = new Vector();
        //if (countCurrDis == 0) {
        	Vector<Predicate> dis = selectDisjunction();
        //} else dis = currDis; 
        // add new nodes to the proof tree
        for (int disi = 0 ; disi < dis.size() ; disi++){
            Vector<Predicate> complementSplit = new Vector<Predicate>(dis.size() - disi);
            Predicate p = (Predicate)dis.elementAt(disi);
            if (p.getDegree() != Degree.EmptyDegree) p.setDegree(p.getDegree());
            else p.setDegree(Degree.complementDegree);
            complementSplit.addElement(p);
            // add the complements
            for (int disj = 0 ; disj < disi ; disj++){
            	Predicate curr = (Predicate)dis.elementAt(disj);
            	if (curr.getDegree() != Degree.EmptyDegree) curr.setDegree(curr.getDegree());
                else curr.setDegree(Degree.complementDegree);
//                Predicate compl = ((Predicate)dis.elementAt(disj)).complement();
//                Predicate complNeg = (Predicate)dis.elementAt(disj);
//                compl.setDegree(Degree.complementDegree);
//                complNeg.setDegree(Degree.complementNegDegree);
            	Predicate compl = ((Predicate)dis.elementAt(disj)).complement();
            	compl.setDegree(Degree.complementNegDegree);
                if (!compl.unary()) compl.setDegree(Degree.relationDegree);
//                complementSplit.addElement(complNeg);
                complementSplit.addElement(compl);
                //Add a clause to the kb if necessary which says that a predicate and its complement imply false
                Vector<Predicate> clauseVector = new Vector<Predicate>(2);
//                clauseVector.addElement(dis.elementAt(disj));
//                clauseVector.addElement(complNeg);
                clauseVector.addElement(curr);
                clauseVector.addElement(compl);
                Clause complClause = LogicFactory.newClause(null, clauseVector);
                deltaKb.addComplementDeltaClause(complClause);
            }
//            System.out.println("COMPLSPLIT: "+complementSplit.toString());
            HyperNode newHyperNode = new HyperNode(complementSplit, currentHyperNode.disjunctions, currentHyperNode.notRangeRestrictedClauses);
            //add a new child
            new Node(newHyperNode, this.proofCurrent);
            countCurrDis++;
            //countCurrDis--;
        }
    }
        
     private Vector<String> existsCommonPred(){
     	Vector<String> common = new Vector<String>();
     	for (int i = 0; i < currBranch.size(); i++){ 
     		if (prevBranch != null){
     			if (prevBranch.contains(currBranch.elementAt(i).toString()))
     				common.addElement(currBranch.elementAt(i).toString());
     		}else if (prevBranch.contains("root")){
     			common = currBranch;
     		}else return null;
     	}
    	return common;
    }
    
    private Degree calcMinDegree(Degree d1, Degree d2){
    	Degree d = null;
    	DegOperator degop = null;
        double degr = -1;
    	try{
    		if ((d1.getOperator().getValue() < 2) && (d2.getOperator().getValue() > 1)){
    			degop = d1.getOperator();
               	degr = d1.getDegree();
    		}else if ((d2.getOperator().getValue() < 2) && (d1.getOperator().getValue() > 1)){
    			degop = d2.getOperator();
               	degr = d2.getDegree();
    		}else{
            	if (d1.getDegree() == d2.getDegree()){
               		degop = DegOperator.minOperator(d1.getOperator(), d2.getOperator());
               		degr = d1.getDegree();
            	}else if ((d1.getDegree() < d2.getDegree())&&(d1.getDegree() != -1)){
               		degop = d1.getOperator();
               		degr = d1.getDegree();
            	}else if ((d1.getDegree() > d2.getDegree())&&(d2.getDegree() != -1)){
               		degop = d2.getOperator();
               		degr = d2.getDegree();
            	}
    		}
        }catch (Exception exc) {} 
        d = new Degree(degop, degr);
        return d;
    }
    
    private Degree calcMaxDegree(Degree d1, Degree d2){
    	Degree d = null;
    	DegOperator degop = null;
        double degr = -1;
    	try{
    		if ((d1.getOperator().getValue() < 2) && (d2.getOperator().getValue() > 1)){
    			degop = d2.getOperator();
               	degr = d2.getDegree();
    		}else if ((d2.getOperator().getValue() < 2) && (d1.getOperator().getValue() > 1)){
    			degop = d1.getOperator();
               	degr = d1.getDegree();
    		}else{
           		if (d1.getDegree() == d2.getDegree()){
             	 	 degop = DegOperator.maxOperator(d1.getOperator(), d2.getOperator());
             	 	 degr = d1.getDegree();
           		}else if ((d1.getDegree() > d2.getDegree())&&(d1.getDegree() != -1)){
              		 degop = d1.getOperator();
              	 	degr = d1.getDegree();
           		}else if ((d1.getDegree() < d2.getDegree())&&(d2.getDegree() != -1)){
              	 	degop = d2.getOperator();
              	 	degr = d2.getDegree();
           		}
    		}
        }catch (Exception exc) {} 
        d = new Degree(degop, degr);
        return d;
    }
    
    private Degree minDegree(Vector<Degree> degrees){
        Degree d = Degree.EmptyDegree;
    	try{
            Degree deg1 = new Degree(DegOperator.GREATER, 1);
            	for (int i = 0; i < degrees.size(); i++){
            		Degree deg2 = (Degree)degrees.elementAt(i);
            		
            		if ((deg1.isInterval())&&(!deg2.isInterval())) {
                		if (DegOperator.sameOperator(deg1.getUpperBound().getOperator(), deg2.getOperator())){
                			Degree dUp = calcMinDegree(deg1.getUpperBound(), deg2);
                			d = new Degree(deg1.getLowerBound(), dUp);
                		}else{
                			Degree dLow = calcMinDegree(deg1.getLowerBound(), deg2);
                			d = new Degree(dLow, deg1.getUpperBound());
                		}
                	}else if ((!deg1.isInterval())&&(deg2.isInterval())) {
                		if (DegOperator.sameOperator(deg1.getOperator(), deg2.getUpperBound().getOperator())){
                			Degree dUp = calcMinDegree(deg1, deg2.getUpperBound());
                			d = new Degree(deg2.getLowerBound(), dUp);
                		}else{
                			Degree dLow = calcMinDegree(deg1, deg2.getLowerBound());
                			d = new Degree(dLow, deg2.getUpperBound());
                		}
                	}else if ((deg1.isInterval())&&(deg2.isInterval())) {
                		Degree dl = calcMinDegree(deg1.getLowerBound(), deg2.getLowerBound());
    					Degree du = calcMinDegree(deg1.getUpperBound(), deg2.getUpperBound());
    					d = new Degree(dl, du);
                	}else{
               			d = calcMinDegree(deg1, deg2);
            		}
            		deg1 = d;
                }
        }catch (Exception exc) {} 
        return d;
    }
    
    private Degree maxDegree(Vector<Degree> degrees){
    	
        Degree d = null;
    	try{
            Degree deg1 = new Degree(DegOperator.SMALLER, 0);
            	for (int i = 0; i < degrees.size(); i++){
            		Degree deg2 = (Degree)degrees.elementAt(i);
            		if ((deg1.isInterval())&&(!deg2.isInterval())) {
                		if (DegOperator.sameOperator(deg1.getUpperBound().getOperator(), deg2.getOperator())){
                			Degree dUp = calcMaxDegree(deg1.getUpperBound(), deg2);
                			d = new Degree(deg1.getLowerBound(), dUp);
                		}else{
                			Degree dLow = calcMaxDegree(deg1.getLowerBound(), deg2);
                			d = new Degree(dLow, deg1.getUpperBound());
                		}
                	}else if ((!deg1.isInterval())&&(deg2.isInterval())) {
                		if (DegOperator.sameOperator(deg1.getOperator(), deg2.getUpperBound().getOperator())){
                			Degree dUp = calcMaxDegree(deg1, deg2.getUpperBound());
                			d = new Degree(deg2.getLowerBound(), dUp);
                		}else{
                			Degree dLow = calcMaxDegree(deg1, deg2.getLowerBound());
                			d = new Degree(dLow, deg2.getUpperBound());
                		}
                	}else if ((deg1.isInterval())&&(deg2.isInterval())) {
                		Degree dl = calcMaxDegree(deg1.getLowerBound(), deg2.getLowerBound());
    					Degree du = calcMaxDegree(deg1.getUpperBound(), deg2.getUpperBound());
    					d = new Degree(dl, du);
                	}else{
               			d = calcMaxDegree(deg1, deg2);
            		}
            		deg1 = d;
                }
        }catch (Exception exc) {} 
        return d;
    }
    
    
    private Degree getMaxDegree(Degree d1, Degree d2){
    	Degree d = null;
    	if ((d1.isInterval())&&(!d2.isInterval())){
    		if (DegOperator.sameOperator(d1.getUpperBound().getOperator(), d2.getOperator())){
          		if ((d1.getUpperBound().getDegree() >= d2.getDegree())) d = d1;
          		else d = new Degree(d1.getLowerBound(), d2);
    		}else if (DegOperator.sameOperator(d1.getLowerBound().getOperator(), d2.getOperator())){
    			if ((d1.getLowerBound().getDegree() >= d2.getDegree())) d = d1;
          		else d = new Degree(d2, d1.getUpperBound());  			
    		}
        }else if ((d1.isInterval())&&(d2.isInterval())){
          	Degree dl = calcMaxDegree(d1.getLowerBound(), d2.getLowerBound());
    		Degree du = calcMaxDegree(d1.getUpperBound(), d2.getUpperBound());
    		d = new Degree(dl, du);
        }else if ((!d1.isInterval())&&(d2.isInterval())){
        	if (DegOperator.sameOperator(d1.getOperator(), d2.getUpperBound().getOperator())){
        		if ((d1.getDegree() >= d2.getUpperBound().getDegree())) d = new Degree(d2.getLowerBound(), d1);
        		else d = d2;
        	}else if (DegOperator.sameOperator(d1.getOperator(), d2.getLowerBound().getOperator())){
    			if ((d1.getDegree() >= d2.getLowerBound().getDegree())) d = new Degree(d1, d2.getUpperBound());
          		else d = d2;  			
    		}
        }else {
//        	System.out.println("***" + d1.getOperator().toString());
//      		System.out.println("****" + d2.getOperator().toString());
          	if (DegOperator.sameOperator(d1.getOperator(), d2.getOperator())){
          		d = calcMaxDegree(d1, d2);
        	}else {
          		if ((checkForRefutation(d1, d2))||(checkForRefutation(d2, d1))) d = d1;
          		else {
          			Degree dl = null;
          			Degree du = null;
          			if (d1.getOperator().getValue() < 2) {
          				dl = d1;
          				du = d2;
          			}else{
          				dl = d2;
          				du = d1;
          			}
          			d = new Degree(dl, du);
          		} 
          	}
        }
       return d;
    }
    
    private boolean checkForRefutation(Degree d1, Degree d2){
    		if ((
    			(
    			((d1.getOperator().getValue() == 3) && (d2.getOperator().getValue() == 0))||
    			((d1.getOperator().getValue() == 2)	&& ((d2.getOperator().getValue() == 0) || (d2.getOperator().getValue() == 1)))
    			)&& (d1.getDegree() >= d2.getDegree())
    			)
    					||
    			(((d1.getOperator().getValue() == 3) && (d2.getOperator().getValue() == 1)) 
    				&& (d1.getDegree() > d2.getDegree()))
    			) 
    		{
    			Vector<Term> terms = new Vector<Term>();
    			terms.addElement(LogicFactory.newConstant("xground"));
    			Predicate p = LogicFactory.newPredicate("degreeConflict",terms,Degree.relationDegree);
                        
               	deltaKb.addRefutationClause(p);
               	return true;
    		}else
    	return false;
    }
    
    /**
     * Only for debugging.
     * @return a String representation of the status of the proof tree.
     */
    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Current Branch: \n");
        for (NodeInterface current = this.proofCurrent ; current != null ; current = current.getParent()){
        	if ((current.isRoot())&& (!current.isClosed())) buffer.append("Current Branch Root nodes:\n");
            for (Enumeration<Predicate> e = ((HyperNode)current.getContent()).branch.elements() ; e.hasMoreElements() ;){
            	if ((current.isRoot())&& (!current.isClosed())) 
            		{
            			buffer.append(e.nextElement().toString()+"\n");
            		}
            	else {
                buffer.append(e.nextElement().toString()+"\n");
            	}
                //buffer.append(" ");
            }
        }
        buffer.append("\nCurrent Expansions: \n");
        for (Enumeration<Predicate> e = ((HyperNode)this.proofCurrent.getContent()).expansions.elements() ; e.hasMoreElements() ;){
            if ((this.proofCurrent.isRoot())&& (!this.proofCurrent.isClosed())) 
            		{
            			buffer.append("ROOT node: "+e.nextElement().toString()+"\n");
            		}
            	else {
            buffer.append(e.nextElement().toString()+"\n");
            	}
            //buffer.append(" ");
        }
        buffer.append("\nCurrent Deltas: \n");
        for (Enumeration<Predicate> e = ((HyperNode)this.proofCurrent.getContent()).delta.elements() ; e.hasMoreElements() ;){
            if ((this.proofCurrent.isRoot())&& (!this.proofCurrent.isClosed())) 
            		{
            			buffer.append("ROOT node: "+e.nextElement().toString()+"\n");
            		}
            	else {
            buffer.append(e.nextElement().toString()+"\n");
            	}
            //buffer.append(", ");
        }
        buffer.append("\nCurrent Disjunctions: \n");
        for (Enumeration<Vector<Predicate>> e = ((HyperNode)this.proofCurrent.getContent()).disjunctions.elements() ; e.hasMoreElements() ;){
            if ((this.proofCurrent.isRoot())&& (!this.proofCurrent.isClosed())) 
            		{
            			buffer.append("ROOT node: "+e.nextElement().toString()+"\n");
            		}
            	else {
            buffer.append(e.nextElement().toString()+"\n");
            	}
            //buffer.append(", ");
        }
        buffer.append("\nCurrent Not Range Restricted Clauses: \n");
        for (Enumeration<Clause> e = ((HyperNode)this.proofCurrent.getContent()).notRangeRestrictedClauses.elements() ; e.hasMoreElements() ;){
            if ((this.proofCurrent.isRoot())&& (!this.proofCurrent.isClosed())) 
            		{
            			buffer.append("ROOT node: "+e.nextElement().toString()+"\n");
            		}
            	else {
            buffer.append(e.nextElement().toString()+"\n");
            	}
            //buffer.append(", ");
        }
        return buffer.toString();
    }
    
    private KnowledgeBase kb;
    private DeltaKnowledgeBase deltaKb;
    private Node proofTreeRoot;
    private Node proofCurrent;
    private TimerThread timer;
    private boolean exhausted;
    private int countCurrDis;
    private Vector<String> currBranch;
    private Vector<String> prevBranch;
    private Vector<Term> herbrand;
    private Domain domain;
}


