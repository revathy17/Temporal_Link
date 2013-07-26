package org.knoesis.rulemanager;

import java.util.ArrayList;
import java.util.List;

import org.knoesis.model.Link;

/**
 * This class contains rules that do not use dependency between words to associate medical concepts with
 * temporal expressions
 * @author revathy
 * @date 18-July-2013
 */

public class OtherRules {

			/**
			 * If a sentence contains a single temporal expression the associate all concepts with the same temporal expression 
			 * @param List<String> temporalExpression - List of Temporal Expressions from the sentence
			 * @param List<String> medicalConcept - List of Medical Concepts from the sentence
			 * @return List<Link> - links between the temporal expressions and medical concepts
			 */	
			public static List<Link> defaultAssociation(List<String> temporalExpression, List<String> medicalConcept) {
						List<Link> allLinks = new ArrayList<Link>();
						if(temporalExpression.size() == 1) {
								String te = temporalExpression.get(0);
								for(String mc : medicalConcept) {									
									Link link = new Link(te, mc, 1);
									allLinks.add(link);
								}
						}						
						return allLinks;					
			}
			
			
}
