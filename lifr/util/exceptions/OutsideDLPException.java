package lifr.util.exceptions;

public class OutsideDLPException  extends Exception{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of ParseException 
     * @param name the name of the thing which causes the exception
     */
    public OutsideDLPException(String name) {
        this.name = name;
    }
    
    /**
     * Returns the detail message string of this throwable.
     *
     * @return  the detail message string of this <tt>Throwable</tt> instance
     *          (which may be <tt>null</tt>).
     */
    public String getMessage() {
        return name;
    }

    private String name;

}
