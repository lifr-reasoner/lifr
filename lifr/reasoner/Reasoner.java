/*
 * reasoner.java
 *
 * Created on October 26, 2004, 12:51 PM
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

package lifr.reasoner;
//import fpocketkrhyper.logic.firstorder.Clause;

import java.util.Vector;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.Predicate;

/**
 * An interface for building reasoners.
 * A reasoner is typically fed with a knowledge base, and the reasoning algorithm
 * determines whether the knowledge base is satisfiable (it has a model) or not 
 * (it is refuted)
 * @author sinner
 * @version $Name:  $ $Revision: 1.8 $
 */
public interface Reasoner {
    
    /**
     * give a new knowledgebase to the reasoner.
     * @param kb a knowledge base
     */
    void setKnowledgeBase(KnowledgeBase kb);
    /**
     * get the current knowledge base
     * @return the current knowledge base
     */
    KnowledgeBase getKnowledgeBase();
    
    /**
     * Search for a model or refutation.
     * @return true when a model was found, false otherwise
     * @param minDepth the minimum search depth for the algorithm
     * @param maxDepth the maximum search depth for the algorithm.
     * If set to 0, there is no maximum, which might imply that the algorithm does not terminate
     * @param timeout the timeout for the reasoner in ms. If 0, no timeout is used.
     * @throws iason.reasoner.ProofNotFoundException If neither a model, nor a refutation is found because maxDepth was reached or a timeout occurred,
     * a ProofNotFoundException is thrown.
     */
    boolean reason(int minDepth, int maxDepth, int timeout) throws ProofNotFoundException;
    
    /**
     * get the Model found after a successful reason() call. If no model was found,
     * null is returned.
     * The model is the set of elements found in the branch.
     * @return A Vector of Predicate containing the current proof branch with the model. null, if no
     * model was found.
     */
     Vector<Predicate> getModel();
          
     /**
      * Manually interrupt the reasoning process. Calling interruptReasoner must
      * cause a running reasoning process to stop and throw a ProofNotFoundException.
      */
     void interruptReasoner();

}
