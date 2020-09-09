package lifr.util.tests;

import lifr.logic.dl.Axiom;
import lifr.logic.dl.Concept;
import lifr.logic.dl.ConceptExpression;
import lifr.logic.dl.Role;
import lifr.logic.dl.RoleExpression;
import lifr.logic.dl.TBox;
import lifr.logic.firstorder.KnowledgeBase;
import lifr.logic.firstorder.LogicFactory;
import lifr.logic.transform.TBox2KB;

public class FOLtest {
	
	private static void roleProblem() {
		TBox tb = new TBox(1);
    	KnowledgeBase kb = new KnowledgeBase();
		
		RoleExpression role = RoleExpression.atom(Role.getOrCreate("r"));
		
		ConceptExpression top = ConceptExpression.TOP;
		ConceptExpression concept = ConceptExpression.atom(Concept.getOrCreate("c"));
		
		ConceptExpression body = ConceptExpression.and(top, concept);
		
		Axiom rolex = Axiom.roleImplies(body, role); 
		
//		System.out.println(role.toString());
//		System.out.println(body.toString());
		
		System.out.println(rolex.toString());
		
		tb.addAxiom(rolex);
		TBox2KB.addToKnowledgeBase(tb, kb);
		
		System.out.println(kb.toString());
		
	}
	
	private static void FOLproblem() {
    	KnowledgeBase kb = new KnowledgeBase();
		
		kb.addClause(LogicFactory.newClause("foo(x,y):-c(x),d(y)."));
		kb.addClause(LogicFactory.newClause("c(a)."));
		kb.addClause(LogicFactory.newClause("d(b)."));
		System.out.println(kb.toString());
	}
	
	private static void topinFOL() {
		TBox tb = new TBox(1);
    	KnowledgeBase kb = new KnowledgeBase();
		
		ConceptExpression con1 = ConceptExpression.atom(Concept.getOrCreate("foo"));
		ConceptExpression con2 = ConceptExpression.atom(Concept.getOrCreate("bodyfoo"));
		ConceptExpression top = ConceptExpression.TOP;
		Axiom impl = Axiom.conceptImplies(ConceptExpression.and(con2, top), con1);
		
		tb.addAxiom(impl);
		TBox2KB.addToKnowledgeBase(tb, kb);
		
		System.out.println(kb.toString());
	}
	
	private static void noTop() {
		TBox tb = new TBox(1);
    	KnowledgeBase kb = new KnowledgeBase();
		
		ConceptExpression con1 = ConceptExpression.atom(Concept.getOrCreate("C"));
		ConceptExpression con2 = ConceptExpression.atom(Concept.getOrCreate("D"));
		ConceptExpression conex = ConceptExpression.and(con1, con2);
		
		RoleExpression rolex = RoleExpression.atom(Role.getOrCreate("R"));
		
		Axiom test = Axiom.roleImplies(conex, rolex);
		
		tb.addAxiom(test);
		TBox2KB.addToKnowledgeBase(tb, kb);
		
		kb.addClause(LogicFactory.newClause("c(a)."));
		kb.addClause(LogicFactory.newClause("d(b)."));
		
		System.out.println(kb.toString());
		
		ReasonTest.reasoning(kb);
	}
	
	public static void main(String[] args) {
//		roleProblem();
		
//		FOLproblem();
		
//		topinFOL();
		
		noTop();
	}

}
