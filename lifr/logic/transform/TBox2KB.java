/*
 * TBox2KB.java
 *
 * Created on 18. Mai 2005, 17:51
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

//import fpocketkrhyper.logic.firstorder.LogicFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.String;

import lifr.logic.dl.Axiom;
import lifr.logic.dl.Role;
import lifr.logic.dl.TBox;
import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.KnowledgeBase;

/**
 * Transforms a TBox to a KRHyper clause knowledge base
 * @author  sinner, tomkl
 * @version $Name:  $ $Revision: 1.7 $
 * added addToKB to extend kb
 */
public class TBox2KB {
    
    /** Creates a new instance of TBox2KB */
    private TBox2KB() {
    }
    
    public static final KnowledgeBase toKnowledgeBase(TBox tbox ){
        KnowledgeBase kb = new KnowledgeBase();
        //kb.addClause(LogicFactory.newClause("top(X)."));
        //kb.addClause(LogicFactory.newClause(":-bottom(X)."));
        addToKnowledgeBase(tbox, kb, "", new Hashtable<String, String>(1));
        return kb;
    }
    
    public static final KnowledgeBase toKnowledgeBase(TBox tbox, Hashtable<String, String> weightedConcepts){
        KnowledgeBase kb = new KnowledgeBase();
        //kb.addClause(LogicFactory.newClause("top(X)."));
        //kb.addClause(LogicFactory.newClause(":-bottom(X)."));
        addToKnowledgeBase(tbox, kb, "", weightedConcepts);
        return kb;
    }
    
    public static final KnowledgeBase toKnowledgeBase(TBox tbox, String subconceptChar, Hashtable<String, String> weightedConcepts){
        KnowledgeBase kb = new KnowledgeBase();
        //kb.addClause(LogicFactory.newClause("top(X)."));
        //kb.addClause(LogicFactory.newClause(":-bottom(X)."));
        addToKnowledgeBase(tbox, kb, subconceptChar, weightedConcepts);
        return kb;
    }
        
    public static final void addToKnowledgeBase(TBox tbox, KnowledgeBase kb){
        KBClauseListener clauseListener = new KBClauseListener(kb);
        TransformDL2fol trans = new StandardDL2fol(clauseListener, "", new Hashtable<String, String>(1));
        //Enumeration e;
        for (Enumeration<Axiom> e = tbox.getAxioms();e.hasMoreElements();){
            Axiom currentAxiom = (Axiom)e.nextElement();
            trans.translateAxiom(currentAxiom);
        }
        for (Enumeration<Role> e=tbox.getRoles(); e.hasMoreElements();) {
            Role currentRole = (Role) e.nextElement();
            trans.translateRole(currentRole);
        }
    }
    
    public static final void addToKnowledgeBase(TBox tbox, KnowledgeBase kb, String subconceptChar, Hashtable<String, String> weightedConcepts){
        KBClauseListener clauseListener = new KBClauseListener(kb);
        TransformDL2fol trans = new StandardDL2fol(clauseListener, subconceptChar, weightedConcepts);
        //Enumeration e;
        for (Enumeration<Axiom> e = tbox.getAxioms();e.hasMoreElements();){
            Axiom currentAxiom = (Axiom)e.nextElement();
            trans.translateAxiom(currentAxiom);
        }
        for (Enumeration<Role> e=tbox.getRoles(); e.hasMoreElements();) {
            Role currentRole = (Role) e.nextElement();
            trans.translateRole(currentRole);
        }
    }
        
    private static final class KBClauseListener implements ClauseListener{
        KnowledgeBase kb;
        Vector<String> cparts = new Vector<String>();
        KBClauseListener(KnowledgeBase kb){
            this.kb = kb;
        }
        
        public void receiveClause(Clause c){
        	if (!cparts.contains(c.toString())){
        		kb.addClause(c);
        		cparts.addElement(c.toString());
        	}
            
        }
    }
}
