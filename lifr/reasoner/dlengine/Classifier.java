/*
 * Classifier.java
 *
 * Created on 6. Januar 2005, 16:42
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
 * Classifier is an interface which defines the subsumption relationships between concept expressions.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.3 $
 */
public interface Classifier {
    
    /**
     * Check wether a concept subsumes another.
     * @param concept1 a first ConceptExpression
     * @param concept2 a second ConceptExpression
     * @return true if concept1 subsumes (is more general) than concept2
     */
    public boolean subsumes(ConceptExpression concept1, ConceptExpression concept2);
    
}
