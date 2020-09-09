/*
 * Concept.java
 *
 * Created on 7. Juni 2004, 17:42
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

import java.util.Hashtable;

import lifr.util.Named;

/**
 * Concept represents atomic DL concepts. They simply consist of a name.
 * <br>
 * Since we assume a unique name assumption, the constructor of Concept is not public,
 * rather, there is a static factory method create which creates new Concepts and registers them
 * in  a Hashtable, avoiding duplicate Concepts.
 * <br>
 * To use existing Concepts, use the static method get(name).
 * @author sinner
 */
public class Concept extends Operand implements Named{
    
    private Concept(String name){
        super(name);
    }
    
    
    private static Hashtable<String, Concept> concepts = new Hashtable<String, Concept>();
    
    /**
     * Factory method for creating a new Concept.<br>
     * To ensure a unique name assumption, every concept first has to be declared (created)
     * This method creates an instance of a concept, registers it in an internal Hashtable
     * and returns it. If you try to create  an existing concept, a
     * UniqueNameAssumptionException is thrown.
     * @return the instance of the new concept
     * @param name the name of the new concept.
     * @throws UniqueNameAssumptionException when the concept to be created violates the unique name assumption
     */
    public final static Concept create(String name){
        if (exists(name)){
            throw new UniqueNameAssumptionException(name);
        }
        Concept c = new Concept(name);
        concepts.put(name, c);
        return c;
    }
    
    
    
    /**
     * Get an already existing concept by name.<br>
     * Before using this method, make sure that the given concept has been created
     * using the create method. If the concept exists, it is returned, otherwise
     * a UndefinedNameException is thrown.
     * @return the instance of the specified concept
     * @param name the name of the concept to get.
     * @throws UndefinedNameException when the concept has not been created previously
     */
    public final static Concept get(String name){
        Concept stored = (Concept)concepts.get(name);
        if (stored == null){
            throw new UndefinedNameException(name);
        }
        return stored;
    }
    
    /**
     * Checks wether a given Concept exists and creates it if necessary.
     * @param name the name of the given concept
     * @return the concept instance named name
     */
    public static final Concept getOrCreate(String name){
        if (exists(name)){
            return get(name);
        }else{
            return create(name);
        }
    }
    
    /**
     * Checks whether a specified concept already exists. A concept exists if it was
     * created previously using create.
     * @param name the name of the concept whose existence is checked.
     * @return true if the specified concept exists, false otherwise.
     */
    public final static boolean exists(String name){
        return (concepts.get(name) != null);
    }
    
    /**
     * The TOP concept, which is always true.
     */
    public final static Concept TOP = Concept.create("TOP");
    
    /**
     * The BOTTOM concept, which is always false.
     */
    public final static Concept BOTTOM = Concept.create("BOTTOM");
}
