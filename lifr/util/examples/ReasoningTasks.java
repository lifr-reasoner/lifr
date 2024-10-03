package lifr.util.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import lifr.logic.firstorder.Clause;
import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.firstorder.Predicate;
import lifr.reasoner.ProofNotFoundException;
import lifr.reasoner.Reasoner;
import lifr.reasoner.krhyper.KrHyper;

/**
 * A Class that exemplifies how to call the reasoner and how to perform the basic supported 
 * reasoning tasks.
 */
public class ReasoningTasks {
	
	/** An imposed Timeout for KBs with high computational complexity. */
	protected static final int reasonerTimeout = 6000;
    
    /** A counter to track how many problems failed for this reasoner instance due to Out of Memory problems. */
    protected static int unsolved_memory = 0;
    
    /** A counter to track how many problems failed for this reasoner instance because they timed out. */
    protected static int unsolved_timeout = 0;
    
    /** The imposed term depth. Used for skolemization-based problems. Out of expressivity for this LiFR version, but supported (unstable) in the core original PocketKRHyper algorithm. */
    protected static final int mintermweight = 2;
    
    /** A counter to track how many refutations (KB clashes) were found for this reasoner instance. */
    public static int refuted = 0;
    
    
    /**
     * The basic functionality of the reasoner. 
     * 
     * All other methods in this class are derivations of this functionality, 
     * untangling some properties and showing how to use them to achieve the supported reasoning tasks. 
     *
     * @param kb the input KB
     */
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
    
    /**
     * A method to show how you can determine KB consistency with the reasoner. 
     *
     * @param kb the input KB
     * @return the truth value; can be true (consistent), false (inconsistent) or undetermined (due to timeout or OOM error)
     */
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
    
    /**
     * A method to show how you can determine KB satisfiability with the reasoner. 
     *
     * @param kb the input KB
     * @return the truth value; can be true (satisfiable), false (unsatisfiable) or undetermined (due to timeout or OOM error)
     */
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
    
    /**
     * A method to show how you can see if a predicate (pred1) subsumes another predicate (pred2) within a TBox with the reasoner. 
     *
     * In short it answers the question "Does pred1 subsume pred2?".
     *
     * @param pred1 the first predicate
     * @param pred2 the second predicate
     * @param tbox the input tbox
     * @return the truth value; can be true (pred1 subsumes pred2), false (pred1 does not subsume pred2) or undetermined (due to timeout or OOM error)
     */
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
    
    /**
     * A method to show how you can retrieve all predicates in a TBox that a given predicate subsumes. 
     *
     * @param predicate the given predicate
     * @param tbox the input tbox
     * @return the predicates subsumed by the given predicate
     */
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
    
    /**
     * Gets the full model that satisfies the input fuzzy knowledge base. This contains both the asserted as well as the inferred model. 
     * 
     * Similar to the basic "reason" method but returns the full model as a predicate vector.  
     *
     * @param kb the inout KB
     * @return the full model
     */
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
    
    /**
     * Gets the asserted model of the input KB.
     *
     * @param kb the input KB
     * @return the asserted model
     */
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
    
    
    /**
     * Gets the inferred model of the input KB. 
     * 
     * This contains all fuzzy entailments, with the degree of each entailment pertaining to the Best Entailment Degree (BED).
     *
     * @param kb the kb
     * @return the inferred model
     */
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
    
    /**
     * An example use case that may pertain to a real-world problem, 
     * where the problem requires the presence of a target (goal) predicate in the model to be satisfied.
     *
     * @param goalPredicateName the goal (target) predicate's name
     * @param kb the input KB
     * @return true, if target predicate exists in the inferred model (can be switched to full model)
     */
    public static boolean meetGoalPredicate(String goalPredicateName, KnowledgeBase kb) {
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
//            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
            			Vector<Predicate> inferredModel = reasoner.getModel();
            			for(Enumeration<Predicate> iter = inferredModel.elements(); iter.hasMoreElements();){
            				Predicate pred = iter.nextElement();
            				if(pred.getName().equalsIgnoreCase(goalPredicateName)) {
            					System.out.println(pred.toWeightandDegreeString());
            					return true;
            				}
            			}
            	}else {
					System.out.println(">>> Model found but goal is not entailed.");
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
		System.out.println(">>> Model is empty.");
        return false;
    }
	
    /**
     * An example use case that may pertain to a real-world problem, 
     * where the problem requires the presence of a target (goal) entailment (consequence) in the model to be satisfied.
     *
     * This suggest the presence of a particular instance of a predicate 
     * (here: concept, but can be employed in the same way for relations) in the inferred model. 
     *
     * @param goalPredicateName the goal (target) predicate's name
     * @param goalIndividualName the goal (target) individual to instantiate the predicate (here: unary, i.e. concept)
     * @param kb the input KB
     * @return true, if target entailment exists in the inferred model (can be switched to full model)
     */
    public static boolean meetGoalEntailment(String goalPredicateName, String goalIndividualName, KnowledgeBase kb) {
    	Reasoner reasoner = new KrHyper();
    	
//    	LogicFactory.initialize();
    	
        reasoner.setKnowledgeBase(kb);
        
        @SuppressWarnings("unused")
        String s = kb.toString();
//        File f = new File("resources/check.txt");
//        FileWriter fw;
//		try {
//			fw = new FileWriter(f);
//			fw.write(s);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
        
        boolean model = false;
        try {
        	
        	Runtime r = Runtime.getRuntime();
            long currentTime = System.currentTimeMillis();
            long initBytes = r.totalMemory() - r.freeMemory();
            
            
            model = reasoner.reason(mintermweight,mintermweight+3,reasonerTimeout);
//            System.out.println(reasoner.getModel().toString());
            
            long MemoryConsumed = r.totalMemory() - r.freeMemory() - initBytes;
            long time = System.currentTimeMillis() - currentTime;
            r.gc();
            
            if (model){
            	if (!reasoner.getModel().isEmpty()){
//            			System.out.println("Model found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.");
            			Vector<Predicate> inferredModel = reasoner.getModel();
//            			System.out.println(inferredModel.toString());
            			for(Enumeration<Predicate> iter = inferredModel.elements(); iter.hasMoreElements();){
            				Predicate pred = iter.nextElement();
            				if(pred.getName().equalsIgnoreCase(goalPredicateName)) {
            					String var = pred.getTerms().nextElement().getName();
            					if(var.equalsIgnoreCase("candidate")) {
            						System.out.println(">>> Entailed goal: " + pred.toWeightandDegreeString());
            						LogicFactory.cleanup();
            						return true;
            					}
            				}
            			}
            	} else {
            		System.out.println(">>> Model is empty.");
            		LogicFactory.cleanup();
                    return false;
            	}
            } else {
//                System.out.println("Refutation found in "+time+ "ms.\nConsumed "+MemoryConsumed+" bytes.\n");
                refuted++;
                System.out.println(">>> Candidate is in clash with one or more user conditions\n");
                LogicFactory.cleanup();
                return false;
            }
        } catch (ProofNotFoundException ex){
            unsolved_timeout++;
            System.out.println("No Solution found for KRSS problem (Timeout)\n");
            LogicFactory.cleanup();
            return false;
        } catch (Error err){
            unsolved_memory++;
            System.out.println("No Solution found for KRSS problem (Out of Memory)\n");
            LogicFactory.cleanup();
            return false;
        }
//        kb.clear();
        System.out.println(">>> Model found but goal is not entailed\n");
        LogicFactory.cleanup();
        return false;
    }
	
    /**
     * The TRUTH value enumeration.
     * 
     * Can be true, false, or otherwise undecided due to reasoning failure 
     * (pertaining to high computational complexity leading to timeout or OOM error). 
     */
    public enum TRUTH{
    	
	    /** True. */
	    TRUE,
    	
	    /** False. */
	    FALSE,
    	
	    /** Undetermined. */
	    UNDETERMINED,
    	
    }

}