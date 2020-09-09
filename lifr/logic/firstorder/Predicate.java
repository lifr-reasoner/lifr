/*
 * Predicate.java
 *
 * Created on 21. September 2004, 20:52
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

import java.util.Vector;
////import java.util.Stack;
import java.util.Enumeration;

import lifr.logic.fuzzy.DegOperator;
import lifr.logic.fuzzy.Degree;
import lifr.logic.fuzzy.Weight;
import lifr.util.EmptyEnumerationTerm;
//import fpocketkrhyper.util.EmptyEnumeration;


/**
 * The Predicate class represents first order logic predicates.
 * @author sinner
 */
public class Predicate {
    
    /** Creates a new instance of Predicate
     * predicates are all lower case.
     */
    Predicate(String name){
        this(name, null, Degree.EmptyDegree, Weight.defaultWeight);
    }
        
    /** Creates a new instance of Predicate
     * predicates are all lower case.
     */
    Predicate(String name, Vector<Term> terms) {
        this.name = name.toLowerCase();
        this.terms = terms;
        this.degree = Degree.EmptyDegree;
        this.weight = Weight.defaultWeight;
    }
    
    /** Creates a new instance of Predicate
     * predicates are all lower case.
     */
    public Predicate(String name, Vector<Term> terms, Degree degree, Weight weight) {
        this.name = name.toLowerCase();
        this.terms = terms;
        this.degree = degree;
        this.weight = weight;
    }
    
    public void setDegree(Degree degree){
    	this.degree = degree;
    }
    
    public void setDegree(DegOperator degop, double deg){
    	this.degree.setOperator(degop);
    	this.degree.setDegree(deg);
    }
    
    public void setDegree(String degop, double deg){
    	this.degree.setOperator(degop);
    	this.degree.setDegree(deg);
    }
    
    public void setWeight(Weight weight){
    	this.weight = weight;
//    	this.weight = new Weight(this.weight.getWeight() * weight.getWeight());
    }
    
    /**
     * Get the complement of a Predicate. A complement is characterized by a - (minus)
     * sign in front of the Predicate. Please note that predicates named with more than 
     * one - sign in front will do bad things!
     * The semantics of the complement is not defined here. You have to implement it
     * on a reasoner level.
     * @return the complement of a given predicate
     */
    public Predicate complement(){
        if (this.name.charAt(0) == '-'){
            return LogicFactory.newPredicate(this.name.substring(1), terms);
        } else {
            return LogicFactory.newPredicate('-'+this.name, terms);
        }
    }
    
    /**
     * get the name of the predicate.
     * @return a string representing the name of the term. In case of functions, this is not equivalent to toString().
     */
    public String getName(){
        return name;
    }
    
    public String printDegree(){
        return degree.toString();
    }
    
    public String printWeight(){
        return weight.toString();
    }
    
    public Degree getDegree(){
        return this.degree;
    }
    
    public Weight getWeight(){
        return this.weight;
    }
    
    /**
     * Get all the variables (including duplicates) occurring in the terms of
     * the predicate.
     * @return an Enumeration of all Variables occurring in the predicate
     */
    public Enumeration<Variable> getVariables(){
    	Vector<Variable> var;
    	if (terms == null){
        	var = new Vector<Variable>(0);
            //return EmptyEnumeration.INSTANCE;
        } else {
            //Vector<Variable> var = new Vector<Variable>();
        	var = new Vector<Variable>();
            for (Enumeration<Term> e = terms.elements() ; e.hasMoreElements();){
                Term t = (Term)e.nextElement();
                for (Enumeration<Term> e1 = t.getAllSubTerms(); e1.hasMoreElements();){
                    Term t1 = (Term)e1.nextElement();
                    if (t1.hasType(TermType.VARIABLE)){
                        var.addElement((Variable)t1);
                    }
                }
            }
            //return var.elements();
        }
        return var.elements();
    }
    
    public Vector<Variable> getVars(){
        if (terms == null){
            return null;
        } else {
            Vector<Variable> var = new Vector<Variable>();
            for (Enumeration<Term> e = terms.elements() ; e.hasMoreElements();){
                Term t = (Term)e.nextElement();
                for (Enumeration<Term> e1 = t.getAllSubTerms(); e1.hasMoreElements();){
                    Term t1 = (Term)e1.nextElement();
                    if (t1.hasType(TermType.VARIABLE)){
                        var.addElement((Variable)t1);
                    }
                }
            }
            return var;
        }
    }
    
    /**
     * Get the first level terms (without subterms) of the predicate.
     * @return an Enumeration of all parameter terms of the predicate.
     */
    public Enumeration<Term> getTerms(){
        if (terms == null){
        	//terms = new Vector<Term>(0);
            return EmptyEnumerationTerm.INSTANCE;
        } else {
            return terms.elements();
        }
        //return terms.elements();
    }
    /**
     * Get the arity (number of terms) for a given predicate.
     * @return the size of the Vector containing the terms.
     */
    public final int arity(){
        if (terms == null){
            return 0;
        } else {
            return terms.size();
        }
    }
    
    public final boolean unary(){
    	//System.out.println("Check if unary: " + this.toString());
    	if (terms.size() == 2) return false;
    	else return true;
    }
    
    /**
     * Check if this predicate subsumes another, i.e. is more general than another
     * Precondition : The Set of Variables for both this predicate and p are disjunct
     * @param p the predicate for the subsumption test
     * @return true if p is more specific than this predicate.
     */
    public final boolean subsumes(Predicate p){
        return Unification.subsumes(this, p);
        //        if ((this.arity() != p.arity()) || (!this.name.equals(p.name))){
//            return false;
//        }
////        p = p.renameVariables(0);
//        Substitution s = new Substitution();
//        for (Enumeration e1 = this.getTerms(), e2 = p.getTerms() ; e1.hasMoreElements() && e2.hasMoreElements() ;){
//            Term t1 = (Term) e1.nextElement();
//            Term t2 = (Term) e2.nextElement();
//            for (Enumeration terms1 = t1.getAllSubTerms(), terms2 = t2.getAllSubTerms() ; terms1.hasMoreElements() && terms2.hasMoreElements() ;){
//                Term term1 = (Term) terms1.nextElement();
//                Term term2 = (Term) terms2.nextElement();
////                term1 = s.applyToTerm(term1); // apply the current substitution to term1
//                if (term1.hasType(TermType.VARIABLE)){
//                    if (s.containsSubstitutionFor(term1) ){
//                        if (term2.hasType(TermType.VARIABLE) && !term2.equals(term1)){
//                            return false;
//                        } else {
//                            continue;
//                        }
//                    }
//                    s.addSubstitution((Variable)term1, term2);
//                } else if (term1.hasType(TermType.CONSTANT)){
//                    if (!term1.equals(term2)){
//                        return false;
//                    } else {
//                        continue;
//                    }
//                } else { //Function
//                    if ((term1.arity() != term2.arity()) || (!term1.name.equals(term2.name))){
//                        return false;
//                    } else {
//                        continue;
//                    }
//                }
//            }
//        }
//        return true; //no clashes found
    }

    /**
     * Return a new Predicate, where each Variable is renamed to its old name with 
     * an appended $counter
     * @param counter an int
     * @return a Predicate whose variables have been renamed.
     */
    public Predicate renameVariables(int counter){
        Substitution s = new Substitution();
        for (Enumeration<Variable> e = this.getVariables() ; e.hasMoreElements() ;){
            Variable current = (Variable)e.nextElement();
            if (!s.containsSubstitutionFor(current)){
                s.addSubstitution(current, LogicFactory.getTerm(current.toString()+"$"+counter));
            }
        }
        return s.applyToPredicate(this);
    }

    /**
     * A simple function for the term weight of a Predicate. It is simply the 
     * term depth of the deepest term. For a definition of the depth of a term, 
     * please refer to Term.termDepth()
     * @return the term depth of the predicate.
     */
    public int getTermWeight(){
        int termWeight = 0;
        for (Enumeration<Term> e = this.getTerms() ; e.hasMoreElements() ;){
            Term t = (Term) e.nextElement();
            int depth = t.termDepth();
            if (depth > termWeight) {
                termWeight = depth;
            }
        }
        return termWeight;
    }
    
    /**
     * Get a String representation of a Predicate in Protein syntax.
     * @return a String representation of this predicate
     */
    @Override
    public String toString(){
        if (this.degree.isInterval()) return this.degree.getLowerBound().getDegree()+" "+(this.degree.getLowerBound().getInvertOperator()).toString()+" "+ name(this.name, this.terms)+this.weight.toString()+" "+this.degree.toString();
        else return name(this.name, this.terms)+this.weight.toString()+" "+this.degree.toString();
    }
    
    public String toDegreeString(){
    	if (this.degree.isInterval()) return this.degree.getLowerBound().getDegree()+" "+(this.degree.getLowerBound().getInvertOperator()).toString()+" "+ name(this.name, this.terms)+" "+this.degree.toString();
        else return name(this.name, this.terms)+" "+this.degree.toString();
//        //if (this.degree.isInterval()) this.degree.getLowerBound().getDegree()+" "+(this.degree.getLowerBound().getOperator()).toString()+" "+ name(this.name, this.terms)+" "+this.degree.toString();
//        //else 
//        return name(this.name, this.terms)+" "+this.degree.toString();
    }
    
    public String toWeightString(){
        //if (this.degree.isInterval()) this.degree.getLowerBound().getDegree()+" "+(this.degree.getLowerBound().getOperator()).toString()+" "+ name(this.name, this.terms)+" "+this.degree.toString();
        //else 
        return name(this.name, this.terms)+" "+this.weight.toString();
    }
    
    public String toWeightandDegreeString(){
        //if (this.degree.isInterval()) this.degree.getLowerBound().getDegree()+" "+(this.degree.getLowerBound().getOperator()).toString()+" "+ name(this.name, this.terms)+" "+this.degree.toString();
        //else 
        return name(this.name, this.terms)+this.weight.toString()+" "+this.degree.toString();
    }
    
    public static String name(String name, Vector<Term> terms){
        StringBuffer str = new StringBuffer(name);
        if (terms != null){
            if (terms.size()>0){
                str.append("(");
                for (Enumeration<Term> e = terms.elements(); e.hasMoreElements();){
                    Term t = (Term)e.nextElement();
                    str.append(t.toString());
                    if (e.hasMoreElements()){
                        str.append(",");
                    }
                }
                str.append(")");
            }
        }
        return str.toString();
    }

    //private Vector terms;
    //protected String name;
    public Vector<Term> terms;
    public String name;
    public Degree degree;
    public Weight weight;
}
