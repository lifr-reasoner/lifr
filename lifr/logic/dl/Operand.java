package lifr.logic.dl;

public abstract class Operand {
	
	protected Operand(String name){
        this.name = name;
    }
	
	protected String name;
	
    /**
     * get the Name of a concept.
     * @return the String representing the name of the concept
     */
    public final String getName(){
        return name;
    }
    
    
    
    /**
     * get the String representation of a concept.
     * @return the String representation of the concept.
     */
    @Override
    public String toString(){
        return name;
    }

}
