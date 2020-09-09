package lifr.logic.fuzzy;

public class Weight {
	private double weight;
	private double originalWeight;

    public Weight(String w) {
    	this.weight = Double.parseDouble(w);
    	this.originalWeight = Double.parseDouble(w);
    }
    
    public Weight(double w) {
    	this.weight = w;
    	this.originalWeight = w;
    }
        
    public static final Weight defaultWeight = new Weight(1.0);
        
    public double getWeight(){
    	return this.weight;
    }
    
    public Weight retainWeight(){
    	return new Weight(this.originalWeight);
    }
    
    public Weight inverseWeight(){
    	return new Weight(1 - this.getWeight());
    }
    
    public void setWeight(double w){
    	this.weight = w;
    }
    
    public void resetToDefault(){
    	this.weight = 1.0;
    }
    
    public void resetWeight(){
    	this.weight = this.originalWeight;
    }
    
    public String toString(){
    	return "*" + this.weight;
    }
}
