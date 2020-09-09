/*
 * TimerThread.java
 *
 * Created on 6. Dezember 2004, 10:00
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
 *
 * @author  sinner
 * @version $Name:  $ $Revision: 1.3 $
 */
public class TimerThread extends Thread {
    
    /** Creates a new instance of TimerThread */
    public TimerThread(long timeout) {
        this.timeout = timeout;
        finished = false;
        //        monitor = new Object();
    }
    
    public synchronized void finish(){
        finished = true;
        // would like to interrupt, but is not implemented in J2ME :-(
    }
    
    public void run() {
        finished = false;
        if (timeout != 0){
            try {
                synchronized (this){
                    wait(timeout);
                }
            } catch (InterruptedException ex) {
                //nothing happens
            }
            finished = true;
        }
    }
    
    
    public final synchronized boolean isFinished(){
        return finished;
    }
    
    private long timeout;
    
    private boolean finished;
    
}
