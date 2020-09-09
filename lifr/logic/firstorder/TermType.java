/*
 * TermType.java
 *
 * Created on 21. September 2004, 15:13
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

/**
 * Specifies whether a Term is a Constant, a Function or a Variable.
 * @author  sinner
 */
public class TermType {
    
    /** Creates a new instance of TermType */
    private TermType() {
    }
    
    /** Symbol for representing constant terms.*/
    public static final TermType CONSTANT= new TermType();
    
    /** Symbol for representing function terms.*/
    public static final TermType FUNCTION = new TermType();
    
    /** Symbol for representing variable terms.*/
    public static final TermType VARIABLE = new TermType();
    
}
