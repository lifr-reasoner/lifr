/*
 * Function.java
 *
 * Created on 21. September 2004, 15:59
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
import java.util.Enumeration;
import java.util.Stack;
/**
 * Function is a class representing first order logic functions.
 * @author sinner
 */
public class Function extends Term{
    
    /** Creates a new instance of Function */
    protected Function(String name, Vector<Term> subTerms) {
//        super(name.toLowerCase());
        super(name);
        this.subTerms = subTerms;
    }
    
    /**
     * Checks whether a Term is of a given Type.
     * @param t The type to check against
     * @return true if t is of TermType FUNCTION
     */
    public boolean hasType(TermType t) {
        return (t.equals(TermType.FUNCTION));
    }
    
//    public Term unifyWith(Term t) {
//        if (t.hasType(TermType.VARIABLE)){
//            return this;
//        } else if (t.hasType(TermType.FUNCTION)){
//            if (t.getName().equals(this.getName()) && (this.subTerms.size() == ((Function)t).subTerms.size()))
//                
//            return null;
//        }
//        
//        return null;
//    }
    
    /**
     * Get the subterms of a function. Does'nt enumerate all subterms recursively.
     * @return An enumeration of the subterms of the function.
     */
    public Enumeration<Term> getSubTerms() {
        return subTerms.elements();
    }
    
    /**
     * Functions are written in lower case, where the function name is followed
     * by parentheses containing subterms. 
     * Example : f(x, A).
     * @return A String representation of the function.
     */
    @Override
    public String toString(){
        return name(name, subTerms);
//        StringBuffer s = new StringBuffer();
//        s.append(name);
//        if (subTerms.size()>0){
//            s.append("(");
//            for (Enumeration e = getSubTerms(); e.hasMoreElements();){
//                Term t = (Term)e.nextElement();
//                s.append(t.toString());
//                if (e.hasMoreElements()){
//                    s.append(",");
//                }
//            }
//            s.append(")");
//        }
//        return s.toString();
    }
    
    /**
     * Get the arity (number of subterms) of a term
     * @return the number of subterms
     */
    public int arity(){
        return subTerms.size();
    }

    /**
     * A static function to construct a String from a name and some terms of a function.
     * @param name the name of the function.
     * @param terms a collection of subterms
     * @return a String representation of the function.
     */
    public static String name(String name, Vector<Term> terms){
        StringBuffer s = new StringBuffer();
        s.append(name);
        if (terms.size()>0){
            s.append("(");
            for (Enumeration<Term> e = terms.elements(); e.hasMoreElements();){
                Term t = (Term)e.nextElement();
                s.append(t.toString());
                if (e.hasMoreElements()){
                    s.append(",");
                }
            }
            s.append(")");
        }
        return s.toString();
        
    }
    
    private Vector<Term> subTerms;

    /**
     * Get the maximum term depth of a term. It is 0 for variables and constants,
     * and the maximum depth of subterms for functions. 
     * f(X) has depth 1, f(f(X) has depth 2, g(f(X), X) has depth 2 etc...
     * @return the depth of the function.
     */
    public int termDepth() {
        int maxdepth = 1;
        Stack<Enumeration<Term>> search = new Stack<Enumeration<Term>>();
        search.push(this.getSubTerms());
        while (!search.empty()){
            if (((Enumeration<Term>) search.peek()).hasMoreElements()){
                Term nextTerm = (Term) ((Enumeration<Term>) search.peek()).nextElement();
                if (nextTerm.hasType(TermType.FUNCTION)){
                    search.push(nextTerm.getSubTerms());
                }
            } else {
                if (search.size() > maxdepth){
                    maxdepth = search.size();
                }
                search.pop();
            }
        }
        return maxdepth;
    }
}
