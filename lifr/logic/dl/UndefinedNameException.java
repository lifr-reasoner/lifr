/*
 * UndefinedNameException.java
 *
 * Created on 29. Juni 2004, 15:07
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

/**
 * A UndefinedNameException is to be thrown if you try to access a concept
 * which has not been defined.
 * @author  sinner
 * @version $Name:  $ $Revision: 1.3 $
 */
public class UndefinedNameException extends RuntimeException{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of UndefinedNameException 
     * @param name the name of the thing which causes the exception
     */
    public UndefinedNameException(String name) {
        this.name = name;
    }
 
    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage() {
        return "Trying to use a name which does not exist: "+name;
    }

    private String name;
}
