package lifr.util.examples;

import lifr.logic.firstorder.KnowledgeBase;

/**
 * An example class containing simple problems that demonstrate the available LiFR reasoning tasks.
 */
public class Problems {
	
	/**
	 * An exemplary basic TBox.
	 *
	 * @return the tbox in KRSS
	 */
	public static String basicTBox(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES B C)" + "\n";
    	s += "(IMPLIES D E)" + "\n";
    	return s;
    }
	
	/**
	 * An exemplary basic ABox.
	 *
	 * @return the abox in KRSS (fuzzy variant)
	 */
	public static String basicABox(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE k D >= 0.8)" + "\n";
    	return s;
    }
	
	/**
	 **
	 * An exemplary basic ABox.
	 *
	 * @return the abox in KRSS (fuzzy variant)
	 */
	public static String basicABox2(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m D >= 0.8)" + "\n";
    	return s;
    }
	
	/**
	 * An exemplary TBox.
	 *
	 * @return the tbox in KRSS
	 */
	public static String tBox1(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES B C)" + "\n";
    	s += "(DISJOINT C D)" + "\n";
    	return s;
    }
	
	
	/**
	 * An exemplary TBox.
	 *
	 * @return the tbox in KRSS
	 */
	public static String tBox2(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES C (NOT B))" + "\n";
    	return s;
    }
	
	/**
	 **
	 * An exemplary ABox.
	 *
	 * @return the abox in KRSS (fuzzy variant)
	 */
	public static String aBox1(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m C >= 0.6)" + "\n";
    	return s;
    }
	
	/**
	 **
	 * An exemplary ABox.
	 *
	 * @return the abox in KRSS (fuzzy variant)
	 */
	public static String aBox2(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m C < 0.4)" + "\n";
    	return s;
    }
	
	/**
	 * A test to get the results of all the supported reasoning tasks for a particular KB. 
	 * 
	 * The example KB (basic Tbox + basic Abox) is consistent and satisfiable.
	 * 
	 * Subsumption tests and the models that satisfy the input fuzzy knowledge base (full [includes asserted + inferred], inferred) are presented.
	 * 
	 * The inferred models contains the fuzzy entailments for the entire KB. 
	 * By default, this pertains the Best Entailment Degree (BED) for all combinations of individuals and concepts.
	 */
	//Consistent and satisfiable
	public static void basicTest() {
		String basicProblem = basicTBox() + basicABox();
		KnowledgeBase kb = KRSStoInternal.parseKRSSinput(basicProblem);
		
		String currentTBox = basicTBox();
		KnowledgeBase tbox = KRSStoInternal.parseKRSSinput(currentTBox);
		String pred1 = "C";
		String pred11 = "E";
		String pred2 = "A";
		String pred22 = "D";
		
//		ReasoningTasks.reason(kb);
		String model = ReasoningTasks.getFullModel(kb).toString();
		System.out.println("Full model:\n" + model + "\n");
	
		
		String consistency = ReasoningTasks.consistent(kb).toString();
		String satisfiability = ReasoningTasks.satisfiable(kb).toString();
		String fuzzyEntailement = ReasoningTasks.getInferredModel(kb).toString();
		
		String subsumes1 = ReasoningTasks.subsumes(pred1, pred2, tbox).toString();
		String subsumes2 = ReasoningTasks.subsumes(pred11, pred2, tbox).toString();
		String subsumesAll = ReasoningTasks.allSubsumed(pred2, tbox).toString();
		String subsumesAll2 = ReasoningTasks.allSubsumed(pred22, tbox).toString();
		
		System.out.println("Consistent: " + consistency);
		System.out.println("Satisfiable: "  + satisfiability);
		System.out.println("Fuzzy Entailment (all KB - fuzzy entailement degree is BED by default):\n" + fuzzyEntailement);
		System.out.println();
		System.out.println("Does " + pred1 + " subsume " + pred2 + "?: " + subsumes1);
		System.out.println("Does " + pred11 + " subsume " + pred2 + "?: " + subsumes2);
		System.out.println("All predicates subsumed by " + pred2 + ":\n" + subsumesAll);
		System.out.println("All predicates subsumed by " + pred22 + ":\n" + subsumesAll2);
		System.out.println();
	}
	
	/**
	 * A test to see whether a particular KB is consistent and satisfiable. Full model included.
	 * 
	 * The example KB (basic Tbox) is consistent but not satisfiable.
	 */
	//Consistent, unsatisfiable
	public static void unsatisfiableTest() {
		String basicProblem = basicTBox();
		KnowledgeBase kb = KRSStoInternal.parseKRSSinput(basicProblem);
		
//		ReasoningTasks.reason(kb);
		String model = ReasoningTasks.getFullModel(kb).toString();
		System.out.println("Full model:\n" + model + "\n");
	
		
		String consistency = ReasoningTasks.consistent(kb).toString();
		String satisfiability = ReasoningTasks.satisfiable(kb).toString();
		
		System.out.println("Consistent: " + consistency);
		System.out.println("Satisfiable: "  + satisfiability);
		System.out.println();
	}
	
	/**
	 * A test to see whether a particular KB is consistent and satisfiable. Full model included.
	 * 
	 * The example KB (Tbox1 + basic Abox2) is inconsistent (due to explicit clash - caused by a specific assertion in the original Abox) and unsatisfiable
	 */
	//Inconsistent (and unsatisfiable)
	//Due to explicit clash
		public static void inconsistentTest1() {
			String inconsistentProblem1 = tBox1() + basicABox2();
			KnowledgeBase kb = KRSStoInternal.parseKRSSinput(inconsistentProblem1);
			
			
//			ReasoningTasks.reason(kb);
			String model = ReasoningTasks.getFullModel(kb).toString();
			System.out.println("Full model:\n" + model + "\n");
		
			
			String consistency = ReasoningTasks.consistent(kb).toString();
			String satisfiability = ReasoningTasks.satisfiable(kb).toString();
			
			System.out.println("Consistent: " + consistency);
			System.out.println("Satisfiable: "  + satisfiability);
			System.out.println();
		}	
		
		/**
		 * A test to see whether a particular KB is consistent and satisfiable. Full model included.
		 * 
		 * The example KB (Tbox2 + Abox1) is inconsistent (due to implicit clash inferred from the original Abox) and unsatisfiable.
		 */
		//Inconsistent (and unsatisfiable)
		//Due to implicit clash (degree-dependent clash)
			public static void inconsistentTest2() {
				String inconsistentProblem2 = tBox2() + aBox1();
				KnowledgeBase kb = KRSStoInternal.parseKRSSinput(inconsistentProblem2);
				
//				ReasoningTasks.reason(kb);
				String model = ReasoningTasks.getFullModel(kb).toString();
				System.out.println("Full model:\n" + model + "\n");
			
				
				String consistency = ReasoningTasks.consistent(kb).toString();
				String satisfiability = ReasoningTasks.satisfiable(kb).toString();
				
				System.out.println("Consistent: " + consistency);
				System.out.println("Satisfiable: "  + satisfiability);
				System.out.println();
			}
	
			
			/**
			 * A test to see whether a particular KB is consistent and satisfiable. Full model included. 
			 * 
			 * The example KB (Tbox2 + Abox2) shows how the inconsistent KB of example "incosistentTest2()" 
			 * can become consistent by difference in fuzzy assertion degree. 
			 * 
			 * The KB is therefore consistent and satisfiable.
			 */
			//Consistent (and satisfiable)
			//Degree clash amended
				public static void consistentTest3() {
					String consistentProblem3 = tBox2() + aBox2();
					KnowledgeBase kb = KRSStoInternal.parseKRSSinput(consistentProblem3);
					
//					ReasoningTasks.reason(kb);
					String model = ReasoningTasks.getFullModel(kb).toString();
					System.out.println("Full model:\n" + model + "\n");
				
					
					String consistency = ReasoningTasks.consistent(kb).toString();
					String satisfiability = ReasoningTasks.satisfiable(kb).toString();
					
					System.out.println("Consistent: " + consistency);
					System.out.println("Satisfiable: "  + satisfiability);
					System.out.println();
				}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		basicTest();
//		unsatisfiableTest();
//		inconsistentTest1();
//		inconsistentTest2();
//		consistentTest3();
		
		
	}
	

}
