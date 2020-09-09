/*
 * simpleDL2fol.java
 *
 * Created on 15. Februar 2005, 16:21
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

package lifr.logic.transform;

import java.util.*;

import lifr.logic.dl.Axiom;
import lifr.logic.dl.AxiomType;
import lifr.logic.dl.Concept;
import lifr.logic.dl.ConceptExpression;
import lifr.logic.dl.DLExpression;
import lifr.logic.dl.DLOperator;
import lifr.logic.dl.Role;
import lifr.logic.dl.RoleExpression;
import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.Function;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.LogicFactoryTerms;
import lifr.logic.firstorder.Predicate;
import lifr.logic.firstorder.Term;
import lifr.logic.firstorder.Variable;
import lifr.logic.fuzzy.Degree;
import lifr.logic.fuzzy.Weight;
import lifr.logic.transform.ClauseListener;
import lifr.logic.transform.TransformDL2fol;


/**
 *
 * @author tomkl
 */
public class StandardDL2fol implements TransformDL2fol {
    
    private ClauseListener cListener;
    private Hashtable<String, String> weightedConcepts;
    static private int countNewTerms = 0;
    private String schar; 
    
    /** Creates a new instance of simpleDL2fol */
    public StandardDL2fol(ClauseListener cl, String subconceptChar, Hashtable<String, String> wConcepts) {
        cListener = cl;
        weightedConcepts = wConcepts;
        // countNewTerms = 0;
        schar = subconceptChar;
//        System.out.println(weightedConcepts.toString());
    }
    
    /**
     * translates a given GCI left => right into set of clauses
     */
    public void translateImplication(ConceptExpression left, ConceptExpression right, boolean notClauses) {
        Vector<Predicate> body = new Vector<Predicate>();
        Vector<Predicate> head = new Vector<Predicate>();
        Vector<Predicate> notbody = new Vector<Predicate>();
        Vector<Predicate> nothead = new Vector<Predicate>();
        Clause notDisjunction = new Clause(null, null);
        Vector<Clause> complClauses = new Vector<Clause>();
//         System.out.println("left:  "+left.toString()+((left.getOperator()==DLOperator.ATOM)?" atomar":" complex"));
//         System.out.println("right: "+right.toString()+((right.getOperator()==DLOperator.ATOM)?" atomar":" complex"));
        boolean leftIsComplex = true;
        boolean rightIsComplex = true;
        boolean rightIsBottom = false;
        //boolean noNotClauses = false;
        boolean noNotClauses = notClauses;
        boolean noComplements = false;
        
        if (right.getOperator()==DLOperator.ATOM) {
        	Concept con =(Concept) right.getOperands().nextElement();
        	//Concept con = Concept.getOrCreate(((ConceptExpression)right.getOperands().nextElement()).getAtomName()); 
            if (con == Concept.TOP) {
                return; // ridiculous implication
            } else { if (con == Concept.BOTTOM) {
//                head = null;
                rightIsComplex = false;
                rightIsBottom=true;
//                System.out.println("BOTTOM on rhs");
            } else {
                String cname = con.getName();
                String ncname;
               
//                if (cname.startsWith("-")) noComplements = true;
                if (cname.startsWith("-"))
                	ncname = cname.substring(cname.indexOf('-') + 1);
                else
                	ncname = "-"+cname;
                Variable varxterm = LogicFactory.newVariable("X");
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varxterm);
                Weight weight = Weight.defaultWeight;
                Weight nweight = Weight.defaultWeight;
                if (weightedConcepts.containsKey(cname)){
                	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
                	nweight = weight.inverseWeight(); 
                }
                head.addElement(LogicFactory.newPredicate(cname,varvector, weight));
                notbody.addElement(LogicFactory.newPredicate(ncname,varvector, nweight));
            	if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
                rightIsComplex = false;
            }}
        }
        // System.out.println("left operator "+left.getOperator().toString());
        if (left.getOperator()==DLOperator.ATOM) {
            Concept con =(Concept) left.getOperands().nextElement();
        	//Concept con = ConceptExpression.getConceptExpressionConcept(left.getOperands().nextElement().getAtomName());
        	if (con == Concept.TOP) {
//                body = null;
                leftIsComplex = false;
            } else { if (con == Concept.BOTTOM) {
                return;
            } else {
                String cname = con.getName();
                String ncname = "";
//                if (cname.startsWith("-")) noComplements = true;
                if (cname.startsWith("-"))
                	ncname = cname.substring(cname.indexOf('-') + 1);
                else
                	ncname = "-"+cname;
                Variable varxterm = LogicFactory.newVariable("X");
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varxterm);
                Weight weight = Weight.defaultWeight;
                Weight nweight = Weight.defaultWeight;
                if (weightedConcepts.containsKey(cname)){
                	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
                	nweight = weight.inverseWeight(); 
                }
                body.addElement(LogicFactory.newPredicate(cname,varvector, weight));
                nothead.addElement(LogicFactory.newPredicate(ncname,varvector, nweight));
                if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
                leftIsComplex = false;
            }}
        } else { if ((left.getOperator()==DLOperator.NOT) && (((ConceptExpression) left.getOperands().nextElement()).getOperator()==DLOperator.ATOM)) {
            // left is not concept
//            noNotClauses = true;
            Concept notc = (Concept) ((ConceptExpression) left.getOperands().nextElement()).getOperands().nextElement() ;
            //Concept notc = ConceptExpression.getConceptExpressionConcept(((ConceptExpression) left.getOperands().nextElement()).getOperands().nextElement().getAtomName());
            String cname = (String) notc.getName() ;
            String ncname;
            if (cname.startsWith("-"))
            	ncname = cname.substring(cname.indexOf('-' + 1)); 
            else
            	ncname = "-"+cname;
            Variable varterm = new Variable("X");
            Vector<Term> varvector = new Vector<Term>();
            varvector.addElement(varterm);
            Weight weight = Weight.defaultWeight;
            Weight nweight = Weight.defaultWeight;
            if (weightedConcepts.containsKey(cname)){
            	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
            	nweight = weight.inverseWeight(); 
            }
            nothead.addElement(LogicFactory.newPredicate(cname,varvector, weight));
            body.addElement(LogicFactory.newPredicate(ncname,varvector, nweight));
            //notbody.addElement(LogicFactory.newPredicate(ncname,varvector));
            if (!cname.startsWith("-")){
        		notDisjunction = notConcept(cname, ncname);
        		complClauses.addElement(notDisjunction);
        	}
        } else { if ((left.getOperator()==DLOperator.EXISTS) && (right.getOperator()==DLOperator.ATOM) ) {
            // Exist R.D sqsubset C, C atomar
            noNotClauses = true;
            Enumeration<Object> e = left.getOperands();
            RoleExpression re = (RoleExpression) e.nextElement();
            String rname="unknownrole";
            try {
                rname = ((Role) re.getOperands().nextElement()).getName(); // derzeit nur einfache Rollen
                //rname = re.getOperands().nextElement().getAtomName();
            } catch (Exception rex) { System.err.println("dont use inv"); return; }
            ConceptExpression ce = (ConceptExpression) e.nextElement();
            String cname, ncname;
            if (ce.getOperator() != DLOperator.ATOM) { // D complex
                ConceptExpression newce;
                countNewTerms++;
                cname = "subConcept"+schar+countNewTerms;
                Concept newc = Concept.create(cname);
                newce = ConceptExpression.atom(newc);
                //ncname = "";
                ncname = "-"+cname;
                translateImplication(ce, newce, false);
            } else { // D atomar
                cname = ((Concept) ce.getOperands().nextElement()).getName();
            	//cname = ce.getOperands().nextElement().getAtomName();
                ncname = "-"+cname;
            }
            Variable varxterm = LogicFactory.newVariable("X");
            Variable varyterm = LogicFactory.newVariable("Y");
            Vector<Term> varvector = new Vector<Term>();
            varvector.addElement(varyterm);
            Weight weight = Weight.defaultWeight;
            if (weightedConcepts.containsKey(cname)){
            	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
            }
            body.addElement(LogicFactory.newPredicate(cname,varvector, weight)); // D(Y)
            //if (!ncname.equalsIgnoreCase("")) nothead.addElement(LogicFactory.newPredicate(ncname, varvector));
            if (!cname.startsWith("-")){
        		notDisjunction = notConcept(cname, ncname);
        		complClauses.addElement(notDisjunction);
        	}
            varvector = new Vector<Term>();
            varvector.addElement(varxterm);
            varvector.addElement(varyterm);
            body.addElement(LogicFactory.newPredicate(rname,varvector)); // R(X,Y)
            
            //not clause:
            Concept con =(Concept) right.getOperands().nextElement();
            //Concept con = ConceptExpression.getConceptExpressionConcept(right.getOperands().nextElement().getAtomName());
            if (!con.getName().startsWith("-")){
            	Concept lc = Concept.getOrCreate("-"+con.getName());
            	Concept nc = Concept.getOrCreate(ncname);
            	ConceptExpression nce = ConceptExpression.forall(re, ConceptExpression.atom(nc));
            	translateImplication(ConceptExpression.atom(lc), nce, false);
            	notDisjunction = notConcept(cname, ncname);
            	complClauses.addElement(notDisjunction);
            }
        } else { if ((left.getOperator()==DLOperator.FORALL) && (right.getOperator()==DLOperator.ATOM)) {
            // all R.D sqsubset C, C atomar
            noNotClauses = true;
            Enumeration<Object> e = left.getOperands();
            RoleExpression re = (RoleExpression) e.nextElement();
            String rname="unknownrole";
            try {
                rname = ((Role) re.getOperands().nextElement()).getName(); // derzeit nur einfache Rollen
                //rname = re.getOperands().nextElement().getAtomName();
            } catch (Exception rex) { System.err.println("dont use inv"); return; }
            ConceptExpression ce = (ConceptExpression) e.nextElement();
            String cname, ncname;
            if (ce.getOperator() != DLOperator.ATOM) { // D complex
                ConceptExpression newce;
                countNewTerms++;
                cname = "subConcept"+schar+countNewTerms;
                ncname = "";
                Concept newc = Concept.create(cname);
                newce = ConceptExpression.atom(newc);
                translateImplication(ce, newce, false);
            } else { // D atomar
                cname = ((Concept) ce.getOperands().nextElement()).getName();
            	//cname = ce.getOperands().nextElement().getAtomName();
                ncname = "-"+cname;
            }
            Variable varxterm = LogicFactory.newVariable("X");
            countNewTerms++;
            String fname = "sk_"+rname+"_"+cname+"_"+countNewTerms;
            Function varyterm = LogicFactory.newFunction(fname, varxterm);
            Vector<Term> varvector = new Vector<Term>();
            varvector.addElement(varyterm);
            // Vector body2 = new Vector(body);
            Vector<Predicate> body2 = new Vector<Predicate>();
            Weight weight = Weight.defaultWeight;
            if (weightedConcepts.containsKey(cname)){
            	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
            }
            for (Enumeration<Predicate> en=body.elements(); en.hasMoreElements(); body2.addElement(en.nextElement()));
            body2.addElement(LogicFactory.newPredicate(cname,varvector, weight)); // D(sk(X))
            Clause clause = LogicFactory.newClause(head, body2);
            // Die translation geht hier kaputt. PocketKrHyper kann das momentan nicht
            cListener.receiveClause(clause); // C(x):-D(sk(X)).
            if (!ncname.equalsIgnoreCase("")) {
            	if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            }
            if (!rightIsBottom) {
               System.out.println("% Problem all "+rname+".D => C "+clause); // C(X):-D(sk(X)).
            }
            // System.out.println(clause);
            // domain restriction fehlt auch dom(sk(X)) :- dom(X).
            varvector = new Vector<Term>();
            varvector.addElement((Variable)varxterm);
            varvector.addElement((Function)varyterm);
            head.addElement(LogicFactory.newPredicate(rname,varvector)); // R(X,sk(X))
            if (rightIsBottom) {
               // weiter geht's
            } else {
               // return; // hier wird abgeschnitten !!!! return muss raus.
            }
            /*
            //not clause:
            Concept con =(Concept) right.getOperands().nextElement();
            if (!con.getName().startsWith("-")){
            	Concept lc = Concept.getOrCreate("-"+con.getName());
            	Concept nc = Concept.getOrCreate(ncname);
            	ConceptExpression nce = ConceptExpression.exists(re, ConceptExpression.atom(nc));
            	translateImplication(ConceptExpression.atom(lc), nce, false);
            	notDisjunction = notConcept(con.getName(), lc.getName());
            	complClauses.addElement(notDisjunction);
            }
            */
        } else { if ( (left.getOperator()==DLOperator.AND) && (right.getOperator()==DLOperator.ATOM) ) {
            // And C1, C2...
            
            for (Enumeration<Object> e = left.getOperands(); e.hasMoreElements(); ) {
                ConceptExpression ce = (ConceptExpression) e.nextElement();
                String cname,ncname;
                if (ce.getOperator() != DLOperator.ATOM) { // D complex
                    ConceptExpression newce, newnce;
                    countNewTerms++;
                    cname = "subConcept"+schar+countNewTerms;
                    ncname = "-"+cname;
                    Concept newc = Concept.getOrCreate(cname);
                    Concept newnc = Concept.getOrCreate(ncname);
                    newce = ConceptExpression.atom(newc);
                    newnce = ConceptExpression.atom(newnc);
                    translateImplication(ce, newce, false);
                    //translateImplication(newnce, ce, false);
                } else { // D atomar
                    cname = ((Concept) ce.getOperands().nextElement()).getName();
                    //cname = ce.getOperands().nextElement().getAtomName();
                    
                    ncname = "-"+cname;
                }
                Variable varxterm = LogicFactory.newVariable("X");
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varxterm);
                Weight weight = Weight.defaultWeight;
                Weight nweight = Weight.defaultWeight;
                if (weightedConcepts.containsKey(cname)){
                	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
                	nweight = weight.inverseWeight(); 
                }
                body.addElement(LogicFactory.newPredicate(cname,varvector, weight)); // C_i(X)
                nothead.addElement(LogicFactory.newPredicate(ncname,varvector, nweight));
                if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            }
        } else { if (left.getOperator()==DLOperator.OR)  {
            // Or C1, C2... => D, D atomar
            for (Enumeration<Object> e = left.getOperands(); e.hasMoreElements(); ) {
                ConceptExpression ce = (ConceptExpression) e.nextElement();
               	translateImplication(ce, right, false);
            }
            
            return;
        }}}}}}
        if (right.getOperator()==DLOperator.OR) {
            // => OR C1, C2...
            
            for (Enumeration<Object> e = right.getOperands(); e.hasMoreElements(); ) {
                ConceptExpression ce = (ConceptExpression) e.nextElement();
                String cname, ncname;
                if (ce.getOperator() != DLOperator.ATOM) { // D complex
                    ConceptExpression newce, newnce;
                    countNewTerms++;
                    cname = "subConcept"+schar+countNewTerms;
                    ncname = "-"+cname;
                    Concept newc = Concept.getOrCreate(cname);
                    Concept newnc = Concept.getOrCreate(ncname);
                    newce = ConceptExpression.atom(newc);
                    newnce = ConceptExpression.atom(newnc);
                    translateImplication(newce, ce, false);
                    //translateImplication(ce, newnce, false);
                } else { // D atomar
                    cname = ((Concept) ce.getOperands().nextElement()).getName();
                    //cname = ce.getOperands().nextElement().getAtomName();
                    ncname = "-"+cname;
                }
                Variable varxterm = LogicFactory.newVariable("X");
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varxterm);
                Weight weight = Weight.defaultWeight;
                Weight nweight = Weight.defaultWeight;
                if (weightedConcepts.containsKey(cname)){
                	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
                	nweight = weight.inverseWeight(); 
                }
                head.addElement(LogicFactory.newPredicate(cname,varvector,weight)); // C_i(X)
                notbody.addElement(LogicFactory.newPredicate(ncname,varvector,nweight));
                if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            }
        } else { if (right.getOperator()==DLOperator.AND) {
            // => AND C1, C2...
            for (Enumeration<Object> e = right.getOperands(); e.hasMoreElements(); ) {
                ConceptExpression ce = (ConceptExpression) e.nextElement();
                translateImplication(left, ce, false);
            }
            return;
        } else { if ((right.getOperator()==DLOperator.NOT)) {
            // => NOT C
//            noNotClauses = true;
            ConceptExpression rce = (ConceptExpression) right.getOperands().nextElement();
            if (!leftIsComplex && (rce.getOperator()==DLOperator.ATOM)) {
//                System.out.println("=> not simple "+rce);
                String cname = ((Concept) rce.getOperands().nextElement()).getName();
            	//String cname = rce.getOperands().nextElement().getAtomName();
                String ncname = "-"+cname;
                Variable varxterm = LogicFactory.newVariable("X");
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varxterm);
                Weight weight = Weight.defaultWeight;
                Weight nweight = Weight.defaultWeight;
                if (weightedConcepts.containsKey(cname)){
                	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
                	nweight = weight.inverseWeight(); 
                }
                notbody.addElement(LogicFactory.newPredicate(cname,varvector, weight)); // C(X)
                head.addElement(LogicFactory.newPredicate(ncname,varvector, nweight));
                //nothead.addElement(LogicFactory.newPredicate(ncname,varvector));
                if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            } else {
//                System.out.println("=> not complex "+rce);
                translateImplication(ConceptExpression.and(left, rce), ConceptExpression.BOTTOM, false);
                return;
            }
        } else { if ((right.getOperator()==DLOperator.FORALL) && (left.getOperator()==DLOperator.ATOM)) {
            noNotClauses = true;
            // C sqsubset All R.D, C atomar
            Enumeration<Object> e = right.getOperands();
            RoleExpression re = (RoleExpression) e.nextElement();
            String rname="unknownrole";
            try {
                rname = ((Role) re.getOperands().nextElement()).getName(); // derzeit nur einfache Rollen
                //rname = re.getOperands().nextElement().getAtomName();
            } catch (Exception rex) { System.err.println("dont use role constructors"); return; }
            ConceptExpression ce = (ConceptExpression) e.nextElement();
            String cname, ncname;
            if (ce.getOperator() != DLOperator.ATOM) { // D complex
                ConceptExpression newce;
                countNewTerms++;
                cname = "subConcept"+schar+countNewTerms;
                //ncname = "";
                ncname = "-"+cname;
                Concept newc = Concept.create(cname);
                newce = ConceptExpression.atom(newc);
                translateImplication(newce, ce, false);
            } else { // D atomar
                cname = ((Concept) ce.getOperands().nextElement()).getName();
                //cname = ce.getOperands().nextElement().getAtomName();
                ncname = "-"+cname;
            }
            Variable varxterm = LogicFactory.newVariable("X");
            Variable varyterm = LogicFactory.newVariable("Y");
            Vector<Term> varvector = new Vector<Term>();
            varvector.addElement(varyterm);
            Weight weight = Weight.defaultWeight;
            if (weightedConcepts.containsKey(cname)){
            	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
            }
            head.addElement(LogicFactory.newPredicate(cname,varvector, weight)); // D(Y)
            if (!ncname.equalsIgnoreCase("")) {
            	if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            }
            varvector = new Vector<Term>();
            varvector.addElement(varxterm);
            varvector.addElement(varyterm);
            body.addElement(LogicFactory.newPredicate(rname,varvector)); // R(X,Y)
            
            //not clause:
            Concept con =(Concept) left.getOperands().nextElement();
            //Concept con = ConceptExpression.getConceptExpressionConcept(left.getOperands().nextElement().getAtomName());
            if (!con.getName().startsWith("-")){
            	Concept lc = Concept.getOrCreate("-"+con.getName());
            	Concept nc = Concept.getOrCreate(ncname);
            	ConceptExpression nce = ConceptExpression.exists(re, ConceptExpression.atom(nc));
            	translateImplication(nce, ConceptExpression.atom(lc), false);
            	if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            }
        } else { if ((right.getOperator()==DLOperator.EXISTS) && (left.getOperator()==DLOperator.ATOM)) {
            // C sqsubset ? R.D, C atomar
            noNotClauses = true;
            Enumeration<Object> e = right.getOperands();
            RoleExpression re = (RoleExpression) e.nextElement();
            String rname="unknownrole";
            try {
                rname = ((Role) re.getOperands().nextElement()).getName(); // derzeit nur einfache Rollen
                //rname = re.getOperands().nextElement().getAtomName();
            } catch (Exception rex) { System.err.println("dont use inv"); return; }
            ConceptExpression ce = (ConceptExpression) e.nextElement();
            String cname, ncname;
            if (ce.getOperator() != DLOperator.ATOM) { // D complex
                ConceptExpression newce;
                countNewTerms++;
                cname = "subConcept"+schar+countNewTerms;
                ncname = "-"+cname;
                Concept newc = Concept.create(cname);
                newce = ConceptExpression.atom(newc);
                translateImplication(newce, ce, false);
            } else { // D atomar
                cname = ((Concept) ce.getOperands().nextElement()).getName();
                //cname = ce.getOperands().nextElement().getAtomName();
                ncname = "-"+cname;
            }
            Variable varxterm = LogicFactory.newVariable("X");
            countNewTerms++;
            String fname = "sk_"+rname+"_"+cname+"_"+countNewTerms;
            Function varyterm = LogicFactory.newFunction(fname, varxterm);
            Vector<Term> varvector = new Vector<Term>();
            varvector.addElement(varyterm);
            // Vector head2 = new Vector(head);
            Vector<Predicate> head2 = new Vector<Predicate>();
            Weight weight = Weight.defaultWeight;
            if (weightedConcepts.containsKey(cname)){
            	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
            }
            for (Enumeration<Predicate> en=head.elements(); en.hasMoreElements(); head2.addElement(en.nextElement()));
            head2.addElement(LogicFactory.newPredicate(cname,varvector,weight)); // D(sk(X))
            //if (!ncname.equalsIgnoreCase("")) notbody.addElement(LogicFactory.newPredicate(ncname, varvector));
            if (!ncname.equalsIgnoreCase("")) {
            	if (!cname.startsWith("-")){
            		notDisjunction = notConcept(cname, ncname);
            		complClauses.addElement(notDisjunction);
            	}
            }
            Clause clause = LogicFactory.newClause(head2, body);
            cListener.receiveClause(clause);
            // System.out.println(clause);
            varvector = new Vector<Term>();
            varvector.addElement(varxterm);
            varvector.addElement(varyterm);
            head.addElement(LogicFactory.newPredicate(rname,varvector)); // R(X,sk(X))
            // yields undecidable clauses if infinite model possible
            /*
            //not clause:
            Concept con =(Concept) left.getOperands().nextElement();
            if (!con.getName().startsWith("-")){
            	Concept lc = Concept.getOrCreate("-"+con.getName());
            	Concept nc = Concept.getOrCreate(ncname);
            	ConceptExpression nce = ConceptExpression.forall(re, ConceptExpression.atom(nc));
            	translateImplication(nce, ConceptExpression.atom(lc), false);
            	notDisjunction = notConcept(con.getName(), lc.getName());
            	complClauses.addElement(notDisjunction);
            }
            */
        } else { if (rightIsComplex && leftIsComplex) {
            ConceptExpression ce, nce;
            countNewTerms++;
            String cname = "subConcept"+schar+countNewTerms;
            String ncname = "-"+cname;
            Concept c = Concept.getOrCreate(cname);
            Concept nc = Concept.getOrCreate(ncname);
            ce = ConceptExpression.atom(c);
            nce = ConceptExpression.atom(nc);
            translateImplication(left, ce, false);
            //translateImplication(nce, left, false);
            translateImplication(ce,right, false);
            //translateImplication(right, nce, false);
            return;
        }}}}}}
        Clause clause = LogicFactory.newClause(head, body);
		Clause not_clause = LogicFactory.newClause(nothead, notbody);
		Clause disjunction = LogicFactory.newClause(nothead, notbody);
        cListener.receiveClause(clause);
        
        if ((not_clause.getHeadVector() != null) && (not_clause.getBodyVector() != null) && !noNotClauses) {
        	//if ((right.getOperator()!=DLOperator.EXISTS) && (left.getOperator()!=DLOperator.EXISTS)&&
        	//	(right.getOperator()!=DLOperator.FORALL) && (left.getOperator()!=DLOperator.FORALL))
        			cListener.receiveClause(not_clause);
        }
        if ((notDisjunction.getBodyVector() != null) && !noComplements) {
        	for (Enumeration<Clause> ec = complClauses.elements(); ec.hasMoreElements();){
                cListener.receiveClause((Clause)ec.nextElement());
        	}
        	//cListener.receiveClause(notDisjunction);
        }
        
//        System.out.println("   generate "+clause.toString());
				
        return;
    }
    
    /**
     * translates a given GCI left <=> right into set of clauses
     */
    public void translateEquivalence(ConceptExpression left, ConceptExpression right) {
        translateImplication((ConceptExpression) left, (ConceptExpression) right, false);
        translateImplication((ConceptExpression) right, (ConceptExpression) left, false);
    }
    
    public void translateDisjointness(ConceptExpression left, ConceptExpression right) {
        translateImplication(ConceptExpression.and((ConceptExpression) left, (ConceptExpression) right), ConceptExpression.BOTTOM, true);
        //translateImplication(ConceptExpression.TOP, ConceptExpression.or((ConceptExpression) left, (ConceptExpression) right), true);
        translateImplication(ConceptExpression.BOTTOM, ConceptExpression.and((ConceptExpression) left, (ConceptExpression) right), true);
    }
    
    //left => right, so right :- left, so AND(C,D) (left) => role (right)
    public void translateRoleImplication(ConceptExpression left, RoleExpression right, boolean notClauses) {
        Vector<Predicate> body = new Vector<Predicate>();
        Vector<Predicate> head = new Vector<Predicate>();
//         System.out.println("left:  "+left.toString()+((left.getOperator()==DLOperator.ATOM)?" atomar":" complex"));
//         System.out.println("right: "+right.toString()+((right.getOperator()==DLOperator.ATOM)?" atomar":" complex"));
        
        if (right.getOperator()==DLOperator.ATOM) {
        	Role role =(Role) right.getOperands().nextElement();
        	//Concept con = Concept.getOrCreate(((ConceptExpression)right.getOperands().nextElement()).getAtomName()); 
                String cname = role.getName();
               
                Variable varxterm = LogicFactory.newVariable("X");
                Function varyterm = LogicFactory.newFunction("f", varxterm);
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varxterm);
                varvector.addElement(varyterm);
                
                Predicate out = null;
                if(!LogicFactory.getAllPredicates().containsKey(cname)) {
                	out = LogicFactory.newPredicate(LogicFactory.getPredicate(cname, Degree.relationDegree).getName(),varvector);
//                	out = LogicFactory.getPredicate(cname, Degree.relationDegree);
//                	System.out.println("NEW: " + out);
                } else {
                	out = LogicFactory.newPredicate(LogicFactory.getPredicate(cname, Degree.relationDegree).getName(),varvector);
//                	System.out.println("EXISTED: " + out);
                }
                
                out.setDegree(Degree.relationDegree);
                
                head.addElement(out);
        }else {
        	System.err.println("Not valid role restriction!");
            return;
        }
        // System.out.println("left operator "+left.getOperator().toString());
        if ( (left.getOperator()==DLOperator.AND)) {
            // And C1, C2...
            
        	int count = 0;
            for (Enumeration<Object> e = left.getOperands(); e.hasMoreElements(); ) {
            	if(count > 1) {
            		System.err.println("Not valid role restriction!");
                    return;
            	}
                ConceptExpression ce = (ConceptExpression) e.nextElement();
                String cname;
                if (ce.getOperator() != DLOperator.ATOM) { // D complex
                    System.err.println("Not valid role restriction!");
                    return;
                } else { // D atomar
                    cname = ((Concept) ce.getOperands().nextElement()).getName();
                    //cname = ce.getOperands().nextElement().getAtomName();
                }
                Term varterm = null;
                Variable x = LogicFactory.newVariable("X");
                Function fx = LogicFactory.newFunction("f", x);
//                if(count == 0) {
                	if(!cname.equalsIgnoreCase("top")) {
                		varterm = x;
                	} else {
                		varterm = fx;
                	}
                	
                	
//                }
//                else if(count == 1) varterm = LogicFactory.newFunction("f", LogicFactory.newVariable("X"));
//                else {
//                	System.err.println("Not valid role restriction!");
//                    return;
//                }
                Vector<Term> varvector = new Vector<Term>();
                varvector.addElement(varterm);
                Weight weight = Weight.defaultWeight;
                if (weightedConcepts.containsKey(cname)){
                	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
                }
                
                Predicate out = null;
                if(!LogicFactory.getAllPredicates().containsKey(cname)) {
                	out = LogicFactory.newPredicate(LogicFactory.getPredicate(cname, Degree.EmptyDegree, weight).getName(),varvector, weight);
//                	out = LogicFactory.getPredicate(cname, Degree.EmptyDegree, weight);
//                	System.out.println("NEW: " + out);
                } else {
                	out = LogicFactory.newPredicate(LogicFactory.getPredicate(cname, Degree.EmptyDegree, weight).getName(),varvector, weight);
//                	System.out.println("EXISTED: " + out);
                }
                
                body.addElement(out);
//                body.addElement(LogicFactory.newPredicate(cname,varvector, weight)); // C_i(X)
                count++;
            }
        } else { 
        	return;
        }
        Clause clause = LogicFactory.newClause(head, body);
        cListener.receiveClause(clause);
				
        return;
    }
    
    public void translateAxiom(Axiom ax) {
        if (ax.getType() == AxiomType.CONCEPT_EQUALITY) {
            DLExpression left = ax.getHead();
            DLExpression right = ax.getBody();
            translateEquivalence((ConceptExpression) left, (ConceptExpression) right);
        } else if (ax.getType() == AxiomType.CONCEPT_INCLUSION) {
            DLExpression left = ax.getHead();
            DLExpression right = ax.getBody();
            translateImplication((ConceptExpression) left, (ConceptExpression) right, false);
        } else if (ax.getType() == AxiomType.ROLE_INCLUSION) {
            DLExpression left = ax.getHead();
            DLExpression right = ax.getBody();
            translateRoleImplication((ConceptExpression) left, (RoleExpression) right, false);
        } else if (ax.getType() == AxiomType.CONCEPT_DISJOINTNESS) {
        	DLExpression left = ax.getHead();
            DLExpression right = ax.getBody();
            translateDisjointness((ConceptExpression) left, (ConceptExpression) right);
        }
    }
    
    public void translateRoleAxiom(Axiom ax) {
    	DLExpression left = ax.getHead();
        DLExpression right = ax.getBody();
        translateRoleImplication((ConceptExpression) left, (RoleExpression) right, false);
    }
    
    
    private Clause notConcept (String cname, String ncname){
    	Vector<Predicate> body = new Vector<Predicate>();
    	    	    		
    	Variable varxterm = LogicFactory.newVariable("X");
        Vector<Term> varvector = new Vector<Term>();
        varvector.addElement(varxterm);
        Weight weight = Weight.defaultWeight;
        Weight nweight = Weight.defaultWeight;
        if (weightedConcepts.containsKey(cname)){
        	weight = new Weight((Double.parseDouble(weightedConcepts.get(cname))));
        	nweight = weight.inverseWeight(); 
        }
        body.addElement(LogicFactory.newPredicate(cname,varvector, weight));
        body.addElement(LogicFactory.newPredicate(ncname,varvector, nweight)); 
            
        Clause c = new Clause(null, body);
                		
    	return c;
    }
    
    /**
     * translate the given Role with attributes, parents and inverses into a set of clauses
     */
    public void translateRole(Role r) {
        Enumeration<Role> e;
        Variable varxterm = LogicFactory.newVariable("X");
        Variable varyterm = LogicFactory.newVariable("Y");
        Vector<Term> varxy = new Vector<Term>(2);
        varxy.addElement(varxterm);
        varxy.addElement(varyterm);
        Predicate predr = LogicFactory.newPredicate(r.getName(), varxy); // r(x,y)
        if (r.isReflexive()) {
            // r(x,x).
            Vector<Term> varxx = new Vector<Term>(2);
            varxx.addElement(varxterm);
            varxx.addElement(varxterm);
            Predicate predsr = LogicFactory.newPredicate(r.getName(), varxx); // r(x,y)
            Vector<Predicate> head=new Vector<Predicate>(1);
            head.addElement(predsr);
            cListener.receiveClause(LogicFactory.newClause(head,null));
        }
        if (r.isSymmetric()) {
            // r(y,x):-r(x,y).
            Vector<Term> varyx = new Vector<Term>(2);
            varyx.addElement(varyterm);
            varyx.addElement(varxterm);
            Predicate predir = LogicFactory.newPredicate(r.getName(), varyx); // r(y,x)
            Vector<Predicate> head=new Vector<Predicate>(1);
            head.addElement(predir);
            Vector<Predicate> body=new Vector<Predicate>(1);
            body.addElement(predr);
            cListener.receiveClause(LogicFactory.newClause(head,body));
        }
        if (r.isTransitive()) {
            // r(x,z):-r(x,y),r(y,z).
            Variable varzterm = LogicFactory.newVariable("Z");
            Vector<Term> varyz = new Vector<Term>(2);
            varyz.addElement(varyterm);
            varyz.addElement(varzterm);
            Predicate predyz = LogicFactory.newPredicate(r.getName(), varyz); // r(y,x)
            Vector<Term> varxz = new Vector<Term>(2);
            varxz.addElement(varxterm);
            varxz.addElement(varzterm);
            Predicate predxz = LogicFactory.newPredicate(r.getName(), varyz); // r(x,z)
            Vector<Predicate> head=new Vector<Predicate>(1);
            head.addElement(predxz);
            Vector<Predicate> body=new Vector<Predicate>(1);
            body.addElement(predr);
            body.addElement(predyz);
            cListener.receiveClause(LogicFactory.newClause(head,body));
        }
        for (e=r.getInverseRoles(); e.hasMoreElements();) {
            Role invRole = (Role) e.nextElement();
            Vector<Term> varyx = new Vector<Term>(2);
            varyx.addElement(varyterm);
            varyx.addElement(varxterm);
            Predicate predir = LogicFactory.newPredicate(invRole.getName(), varyx); // invRole(y,x)
            Vector<Predicate> head=new Vector<Predicate>(1);
            head.addElement(predr);
            Vector<Predicate> body=new Vector<Predicate>(1);
            body.addElement(predir);
            cListener.receiveClause(LogicFactory.newClause(head,body));
            cListener.receiveClause(LogicFactory.newClause(body,head));
        }
        for (e=r.getParentRoles(); e.hasMoreElements();) {
            Role parRole = (Role) e.nextElement();
            //System.out.println("Role: " + r.getName() + " - Parent: " + parRole.getName());
            Predicate predpr = LogicFactory.newPredicate(parRole.getName(), varxy); // parRole(x,y)
            Vector<Predicate> head=new Vector<Predicate>(1);
            head.addElement(predpr);
            Vector<Predicate> body=new Vector<Predicate>(1);
            body.addElement(predr);
            cListener.receiveClause(LogicFactory.newClause(head,body));
        }
    }
    
}
