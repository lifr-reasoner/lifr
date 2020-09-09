/*
 * Semantic.java
 *
 * Created on 3. Januar 2005, 15:12
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

package lifr.reasoner.dlengine;
import lifr.logic.dl.ConceptExpression;



/**
 * The <tt>Semantic</tt> interface should be implemented by all classes which require
 * some sort of Description Logics reasoning.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.3 $
 */
public interface Semantic {
    
    /** 
     * Return the semantics of a class as a description logics concept expression.
     * Valid expressions are all that are expressible with the means provided by
     * iason.logic.dl. <tt>null</tt> is not permitted!
     * @return a description logics concept expression
     * @see pocketkrhyper.logic.dl.ConceptExpression
     */
    ConceptExpression getSemantics();
}
