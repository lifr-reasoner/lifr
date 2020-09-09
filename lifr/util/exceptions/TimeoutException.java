/*
 * ParseException.java
 *
 * Created on 23. September 2004, 14:45
 */

package lifr.util.exceptions;

/**
 * A Timeoutexception is thrown when a lengthy operation times out.
 * @author sinner
 */
public class TimeoutException extends Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of ParseException 
     * @param name the name of the thing which causes the exception
     */
    public TimeoutException(String name) {
        this.name = name;
    }
    
    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage() {
        return "Timeout: "+name;
    }

    private String name;
}
