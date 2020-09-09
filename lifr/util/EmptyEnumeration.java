/*
 * EmptyEnumerator.java
 *
 * Created on 22. September 2004, 12:05
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

package lifr.util;

import java.util.Enumeration;
//import fpocketkrhyper.logic.firstorder.Predicate;
/**
 * Defines an empty Enumeration. There is only one instance of this class,
 * EmptyEnumeration.INSTANCE, since this implementation of the Enumeration
 * interface defines that hasMoreElements() always returns false, and therefore
 * is not connected to any collection.
 *
 * Usage example: for (Enumeration e = EmptyEnumeration.INSTANCE ; e.hasNext();)
 * This is a loop which immediately returns.
 * @author  sinner
 */
public final class EmptyEnumeration implements Enumeration<Object>{
    
    /** Creates a new instance of EmptyEnumerator */
    private EmptyEnumeration() {
    }
    
    /**
     * Since the empty Enumeration has no Elements, there never is a next element.
     * Therefore, this method always returns false.
     * @return false
     */    
    public boolean hasMoreElements() {
        return false;
    }
    
    /**
     * There are no elements in the EmptyEnumeration, so null is returned.
     * @return null;
     */
    public Object nextElement() {
        return null;
    }

    /**
     * The only instance of the EmptyEnumeration.
     */    
    public static final EmptyEnumeration INSTANCE = new EmptyEnumeration();
    
    /*
    public enum ENUM_TYPE{
    	OBJECT,
    	PREDICATE,
    	TERM;
    }
    */
}
