package lifr.run;

import java.util.Vector;

import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
import lifr.reasoner.ProofNotFoundException;
import lifr.reasoner.Reasoner;
import lifr.reasoner.krhyper.KrHyper;

public class Reason {
	
	protected static final int reasonerTimeout = 6000;
    protected static int unsolved_memory = 0;
    protected static int unsolved_timeout = 0;
    protected static final int mintermweight = 2;
    public static int refuted = 0;
	
	public static Object reasoning(KnowledgeBase inKB, boolean printTime, boolean printModel) {
		KnowledgeBase kb = new KnowledgeBase();
    	Reasoner reasoner = new KrHyper();
    	LogicFactory.initialize();
    	
    	Vector<Predicate> outModel = new Vector<Predicate>();
    	
    	Runtime r = Runtime.getRuntime();
        long currentTime = System.currentTimeMillis();
        long initBytes = r.totalMemory() - r.freeMemory();
        
        kb.mergeKnowledgeBase(inKB);
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
        

        boolean model = false;
        try {
        	
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
            			if(printTime) System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
            			
//            			Vector<Predicate> matches = 
            			outModel = reasoner.getModel();
            			
            			if(printModel) {
            				System.out.println(outModel.toString());
            				System.out.println();
            			}
            			
            			return outModel;
            			
            	}
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                return "Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n";
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
//            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            return "Error: No Solution found for KRSS problem (Timeout)\n";
        } catch (Error err){
            unsolved_memory++;
//            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
            return "Error: No Solution found for KRSS problem (Out of Memory)\n";
        }
        kb.clear();
		
		return "Error: Miscellaneous problem running reasoner";
	}
	
}
