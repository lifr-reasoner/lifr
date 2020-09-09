/*
 * DLExpression.java
 *
 * Created on 10. Januar 2005, 11:23
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

package lifr.logic.dl;
import java.util.Enumeration;
import java.util.Vector;
import java.lang.StringBuffer;
import java.lang.Exception;

/**
 * DLExpression is the common superclass of RoleExpression and ConceptExpression.
 * It provides the common accessor methods, but is abstract.
 * Both Concept- and RoleExpression classes provide their own respective factory
 * methods for object creation.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.3 $
 */
public abstract class DLExpression {
    
    /**
     * Creates a new instance of DLExpression
     * @param operator 
     * @param operands 
     */
    protected DLExpression(DLOperator operator, Vector<Object> operands) {
        this.operator = operator;
        this.operands = operands;
    }


    /**
     * Get the operator for this expression. Operators are defined in
     * the ConceptOperator class and define the type of DL expression
     * @return the operator type for this expression
     */
    public DLOperator getOperator(){
        return operator;
    }

    /**
     * Get an enumeration of all operands in the DL concept expression.
     * Since it is possible for concept expressions to have one to many
     * operands, we return them as enumeration.
     * @return an enumeration of the operands in the dl concept expression. 
     * The elements of the enumeration are again DLExpressions.
     */
    public Enumeration<Object> getOperands(){
        return operands.elements();
    }
    
    /**
     * Creates a String Expression of a DLExpression in KRSS syntax.
     * @return a new String representation of a DLExpression
     */
    @Override
    public String toString(){
        StringBuffer s = new StringBuffer();
        if (operator != DLOperator.ATOM){
            s.append('(');
        }
        s.append(operator.toString());
        for (Enumeration<Object> e = getOperands(); e.hasMoreElements();){
            s.append(' ');
            s.append(e.nextElement().toString());
        }
        if (operator != DLOperator.ATOM){
            s.append(')');
        }
        return s.toString();
    }
    
    public String getAtomName(){
        StringBuffer s = new StringBuffer();
        if (operator != DLOperator.ATOM){
            throw new IllegalArgumentException("Not an atomic concept");
        }
        Enumeration<Object> e = getOperands();
        s.append(e.nextElement().toString());
        return s.toString();
    }
    
    protected Vector<Object> operands;
    protected DLOperator operator;
}
