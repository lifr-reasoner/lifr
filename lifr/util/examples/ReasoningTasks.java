package lifr.util.examples;

import java.util.Enumeration;
import java.util.Vector;

import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
import lifr.reasoner.ProofNotFoundException;
import lifr.reasoner.Reasoner;
import lifr.reasoner.krhyper.KrHyper;

public class ReasoningTasks {
	
	protected static final int reasonerTimeout = 6000;
    protected static int unsolved_memory = 0;
    protected static int unsolved_timeout = 0;
    protected static final int mintermweight = 2;
    public static int refuted = 0;
    
    
    public static void reason(KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
    	LogicFactory.initialize();
    	
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
            			System.out.println("MODEL:\n" + reasoner.getModel().toString());
            	}
            } else {
                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
            System.out.println("No Solution found for KRSS problem (Timeout)\n");
        } catch (Error err){
            unsolved_memory++;
            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
        }
        kb.clear();
    }
    
    public static TRUTH consistent(KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
//    	LogicFactory.initialize();
    	
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	return TRUTH.TRUE;
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                kb.clear();
                return TRUTH.FALSE;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
//            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            kb.clear();
            return TRUTH.UNDETERMINED;
        } catch (Error err){
            unsolved_memory++;
//            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
//            kb.clear();
            return TRUTH.UNDETERMINED;
        }
    }
    
    public static TRUTH satisfiable(KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
//    	LogicFactory.initialize();
    	
    	@SuppressWarnings("unused")
        String s = kb.toString();
//        System.out.println(s);
    	
        reasoner.setKnowledgeBase(kb);
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
//            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
//            			System.out.println("MODEL:\n" + reasoner.getModel().toString());
            			
            			return TRUTH.TRUE;
            			
            	}
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                return TRUTH.FALSE;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
//            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            return TRUTH.UNDETERMINED;
        } catch (Error err){
            unsolved_memory++;
//            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
            return TRUTH.UNDETERMINED;
        }
//        kb.clear();
//        System.out.println("empty model");
        return TRUTH.FALSE;
    }
    
    //a clause pred1 which subsumes pred2 can be derived from with resolution
    //does pred1 subsume pred2
    public static TRUTH subsumes(String pred1, String pred2, KnowledgeBase tbox) {
    	Reasoner reasoner = new KrHyper();
//    	LogicFactory.initialize();
    	
    	KnowledgeBase kb = tbox;
    	kb.mergeKnowledgeBase(KRSStoInternal.parseKRSSinput("(INSTANCE ms "+ pred2 + " >= 1.0)\n"));
    	
    	@SuppressWarnings("unused")
        String s = kb.toString();
    	
        reasoner.setKnowledgeBase(kb);
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
//            		System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
        			Vector<Predicate> inferredModel = reasoner.getModel();
        			for(Enumeration<Predicate> iter = inferredModel.elements(); iter.hasMoreElements();){
        				Predicate pred = iter.nextElement();
        				if(pred.getName().equalsIgnoreCase(pred1))
        					return TRUTH.TRUE;
        			}
            			
            	}
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                return TRUTH.FALSE;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
//            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            return TRUTH.UNDETERMINED;
        } catch (Error err){
            unsolved_memory++;
//            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
            return TRUTH.UNDETERMINED;
        }
//        kb.clear();
        return TRUTH.FALSE;
    }
    
    public static Vector<Predicate> allSubsumed(String predicate, KnowledgeBase tbox) {
    	Reasoner reasoner = new KrHyper();
//    	LogicFactory.initialize();
    	
    	KnowledgeBase kb = tbox;
    	KnowledgeBase abox = KRSStoInternal.parseKRSSinput("(INSTANCE ms "+ predicate + " >= 1.0)\n");
    	kb.mergeKnowledgeBase(abox);
        reasoner.setKnowledgeBase(kb);
        
        
        @SuppressWarnings("unused")
        String s = kb.toString();
//        System.out.println(s);
        
        return getInferredModel(kb);
    }
    
    //asserted + inferred
    public static Vector<Predicate> getFullModel(KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
//    	LogicFactory.initialize();
    	
    	@SuppressWarnings("unused")
        String s = kb.toString();
    	
        reasoner.setKnowledgeBase(kb);
        
        Vector<Predicate> inferredModel = new Vector<Predicate>();
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
//            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
            			inferredModel = reasoner.getModel();
            			
            	}
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
//            System.out.println("No Solution found for KRSS problem (Timeout)\n");
        } catch (Error err){
            unsolved_memory++;
//            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
        }
//        kb.clear();
        return inferredModel;
    }
    
    public static Vector<Predicate> getAssertedModel(KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
//    	LogicFactory.initialize();
    	
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
        
        Vector<Predicate> assertedModel = new Vector<>();
        Vector<Clause> kbFacts = kb.getFacts();
        for(Enumeration<Clause> iter = kbFacts.elements(); iter.hasMoreElements();){
			Clause cl = iter.nextElement();
			Vector<Predicate> pred = cl.getHeadVector();
			assertedModel.addAll(pred);
        }
        	
        return assertedModel;
    }
    
    
    public static Vector<Predicate> getInferredModel(KnowledgeBase kb) {
    	@SuppressWarnings("unused")
        String s = kb.toString();
    	
    	Vector<Predicate> inferredModel = new Vector<Predicate>();
    	
    	Vector<Predicate> fullModel = getFullModel(kb);
//    	System.out.println("Full model: " + fullModel.toString());
    	Vector<Predicate> assertedModel = getAssertedModel(kb);
//    	System.out.println("Asserted Model: " + assertedModel.toString());
    	
    	for(Enumeration<Predicate> iter = fullModel.elements(); iter.hasMoreElements();){
			Predicate pred = iter.nextElement();
			if(!assertedModel.contains(pred)) {
				inferredModel.add(pred);
			}
    	}
    	
    	return inferredModel;
    }
    
    public static boolean meetGoal(String goalPredicateName, KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
    	
//    	LogicFactory.initialize();
    	
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
            			Vector<Predicate> inferredModel = reasoner.getModel();
            			for(Enumeration<Predicate> iter = inferredModel.elements(); iter.hasMoreElements();){
            				Predicate pred = iter.nextElement();
            				if(pred.getName().startsWith(goalPredicateName))
            					return true;
            			}
            			
            	}
            } else {
                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                return false;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            return false;
        } catch (Error err){
            unsolved_memory++;
            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
            return false;
        }
//        kb.clear();
        return false;
    }
	
    public enum TRUTH{
    	TRUE,
    	FALSE,
    	UNDETERMINED,
    	
    }

}