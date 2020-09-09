/*
 * RoleExpression.java
 *
 * Created on 10. Januar 2005, 09:50
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
import java.util.Vector;
//import java.util.Enumeration;

/**
 * A class for representing Role Expressions. This allows to express role hierarchies.
 * @author sinner
 * @version $Name:  $ $Revision: 1.4 $
 */
public class RoleExpression extends DLExpression{

    /** Creates a new instance of RoleExpression */
    private RoleExpression(DLOperator operator, Vector<Object> operands) {
        super(operator, operands);
    }

    /**
     * The universal Role.
     */
    public static final RoleExpression UNIVERSAL = atom(Role.UNIVERSAL);
    
    /**
     * The identity Role.
     */
    public static final RoleExpression IDENTITY = atom(Role.IDENTITY);
    
    /**
     * An atomic Role.
     * @param r The actual Role.
     * @return The RoleExpression corresponding to the atomic role.
     */
    public static final RoleExpression atom(Role r){
        Vector<Object> v = new Vector<Object>(1);
        v.addElement(r);
        return new RoleExpression(DLOperator.ATOM, v);
    }
   
    /**
     * The composition of two Roles.
     * @param a The first Role.
     * @param b The second Role.
     * @return The RoleExpression representing the composition of a and b.
     */
    public static final RoleExpression compose(RoleExpression a, RoleExpression b){
        Vector<Object> op = new Vector<Object>(2);
        op.addElement(a);
        op.addElement(b);
        return new RoleExpression(DLOperator.COMPOSE, op);
    }
   
    /**
     * The complement of a role.
     * @param r The parameter Role.
     * @return The complement of r.
     */
    public static final RoleExpression not(RoleExpression r){
        Vector<Object> op = new Vector<Object>(1);
        op.addElement(r);
        return new RoleExpression(DLOperator.NOT, op);
    }

    /**
     * The inverse of a Role.
     * @param r The parameter Role.
     * @return The inverse of r.
     */
    public static final RoleExpression inverse(RoleExpression r){
        Vector<Object> op = new Vector<Object>(1);
        op.addElement(r);
        return new RoleExpression(DLOperator.INVERSE, op);
    }

    /**
     * The intersection of two roles
     * @param a The first Role parameter
     * @param b The second Role parameter
     * @return The intersection Role of a and b.
     */
    public static final RoleExpression and(RoleExpression a, RoleExpression b){
        Vector<Object> op = new Vector<Object>(2);
        op.addElement(a);
        op.addElement(b);
        return new RoleExpression(DLOperator.AND, op);
    }

    /**
     * The union of two Roles.
     * @param a The first Role parameter
     * @param b The second Role parameter
     * @return The union Role of a and b.
     */
    public static final RoleExpression or(RoleExpression a, RoleExpression b){
        Vector<Object> op = new Vector<Object>(2);
        op.addElement(a);
        op.addElement(b);
        return new RoleExpression(DLOperator.OR, op);
    }
    
}
