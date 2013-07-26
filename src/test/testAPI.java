package test;

import java.util.ArrayList;
import java.util.List;
import org.knoesis.model.Link;
import org.knoesis.rulemanager.ApplyRules;


import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

/**
 * Class showing the use of the Temporal_Link API
 * @author revathy
 *
 */
public class testAPI {
		
		public static void returnLinks(String sentence, List<String> temporalExpression, List<String> medicalConcept, List<Integer> rules) {
				
					LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");
					
					try {
							ApplyRules ar = new ApplyRules(sentence, parser);							
							List<Link> links = ar.returnAssociationLinks(temporalExpression, medicalConcept,rules);					
							for(Link l : links) 
									System.out.println(l.getConcept1() + "\t" + l.getConcept2() + "\t" + l.getLink());
					} catch (Exception e) {
							e.printStackTrace();
					}
				
		}	
				
		public static void main(String[] args) {
					String sentence = "Gangrene of left second toe in 2010-06-06 followed by an amputation";
					List<String> medicalConcept = new ArrayList<String>();
					medicalConcept.add("Gangrene");
					medicalConcept.add("amputation");
					
					List<String> temporalExpression = new ArrayList<String>();
					temporalExpression.add("2010-06-06");
					
					List<Integer> rules = new ArrayList<Integer>();
					rules.add(1);
					rules.add(2);
					rules.add(3);		
					returnLinks(sentence, temporalExpression, medicalConcept, rules);
		}
}
