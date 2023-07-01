package lifr.util.examples;

import lifr.logic.firstorder.KnowledgeBase;

public class Problems {
	
	public static String basicTBox(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES B C)" + "\n";
    	s += "(IMPLIES D E)" + "\n";
    	return s;
    }
	
	public static String basicABox(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE k D >= 0.8)" + "\n";
    	return s;
    }
	
	public static String basicABox2(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m D >= 0.8)" + "\n";
    	return s;
    }
	
	public static String tBox1(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES B C)" + "\n";
    	s += "(DISJOINT C D)" + "\n";
    	return s;
    }
	
	
	public static String tBox2(){
    	String s = "(IMPLIES A B)" + "\n";
    	s += "(IMPLIES C (NOT B))" + "\n";
    	return s;
    }
	
	public static String aBox1(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m C >= 0.6)" + "\n";
    	return s;
    }
	
	public static String aBox2(){
    	String s = "(INSTANCE m A >= 0.75)" + "\n";
    	s += "(INSTANCE m C < 0.4)" + "\n";
    	return s;
    }
	
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
		
		//Inconsistent (and unsatisfiable)
		//Due to implicit clash (degree clash)
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
	
			//Consistent (and unsatisfiable)
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
	
	public static void main(String[] args) {
		basicTest();
//		unsatisfiableTest();
//		inconsistentTest1();
//		inconsistentTest2();
//		consistentTest3();
		
		
	}
	

}
