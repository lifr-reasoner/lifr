/*
 * Comparable.java
 *
 * Created on 4. Januar 2005, 12:47
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

/**
 * <p>
 * The Comparable interface is not part of current CLDC/MIDP. (1.1/2.0)
 * Therefore we define the same interface for use with J2ME. Should you port
 * software using this interface to J2SE, simply forget about this interface and use
 * java.lang.Comparable.
 * </p>
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.1 $
 */
public interface Comparable {
    /**
     * <p>
     * Compares this object with the specified object for order. It returns a positive
     * integer if it is greater, 0 if equal and a negative integer if it is less
     * than the object compared to.</p>
     * <p>compareTo defines a partial ordering, so it is reflexive, transitive and
     * antisymmetric</p>
     * <p>If two objects are not compatible for comparison, a ClassCastException is thrown.
     * @param o is the object we compare against.
     * @throws ClassCastException  - if the specified object's type prevents it from being compared to this Object.
     */
    int compareTo(Object o);
}
