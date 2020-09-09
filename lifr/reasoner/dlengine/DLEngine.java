/*
 * DLEngine.java
 *
 * Created on 3. Januar 2005, 15:36
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
import lifr.logic.dl.TBox;
import lifr.logic.firstorder.KnowledgeBase;

/**
 * DLEngine is the main reasoning interface for description logics tasks.
 * Given a TBox, it can determine subsumption relationships between
 * ConceptExpressions with respect to the TBox.<p>
 * DLEngine is designed to be immutable for thread-safety. The price you have to
 * pay is that you cannot access the TBox after DLEngine has been created.
 * If you make changes to the TBox after creation of DLEngine and want those
 * changes be respected, create a new DLEngine object.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.5 $
 */
public class DLEngine implements Classifier{
    
    /**
     * DLEngine may not be instantiated from outside
     * @param tbox the tbox with respect to which the reasoning engine is to operate.
     */
    public DLEngine(TBox tbox) {
        this.foTBox = null; //@todo transform tbox to first order knowledgebase
    }

    /**
     * Factory method for DLEngine. Creates a new instance of DLEngine with a
     * non-null TBox.
     * @param tbox may not be null
     * @return a new instance of DLEngine
     * @throws IllegalArgumentException if tbox is null
     */
    public DLEngine newDLEngine(TBox tbox){
        if (tbox == null){
            throw new IllegalArgumentException("DLEngine.newDLEngine: No null tboxes allowed in DLEngine");
        }
        return new DLEngine(tbox);
    }
    
    /**
     * Checks whether concept1 subsumes concept2 with respect to the stored TBox.
     * (i.e. concept1 is more general than concept2). The semantics is that of
     * Description Logics subsumption.
     * @param concept1 A ConceptExpression
     * @param concept2 A ConceptExpression
     * @return true if concept1 subsumes concept2, false otherwise
     * @throws IllegalArgumentException if either parameter is null
     */
    public boolean subsumes(ConceptExpression concept1, ConceptExpression concept2){
        if (concept1 == null || concept2 == null){
            throw new IllegalArgumentException("DLEngine.subsumes: Null Pointer as argument");
        }
        //dummy implementation
        return true;
    }
    
    
    /** The TBox we want to reason with in first order logic*/
    private KnowledgeBase foTBox;
    
}
