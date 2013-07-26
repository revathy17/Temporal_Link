package org.knoesis.rulemanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.knoesis.config.Dictionary;
import org.knoesis.model.Dependency;
import org.knoesis.model.Link;
import org.knoesis.util.CommonFunctions;
import org.knoesis.util.DependencyParser;
import org.knoesis.util.Preprocess;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

/**
 * @date 3-July-2013
 * @author revathy
 */

public class ApplyRules {
	
		List<Dependency> dependencies;
		List<String> sentenceWords;		
		List<TaggedWord> taggedWords;
		String sentence;
		static Logger log = Logger.getLogger(ApplyRules.class.getName());
			
		/**
		 * Initialize the object
		 * @param sentence
		 * @param parser		  
		 */
		public ApplyRules(String sentence, LexicalizedParser parser) throws Exception {
				this.sentence = Preprocess.removePunctuation(sentence);			
				DependencyParser dp = new DependencyParser(parser);
				dependencies = dp.getCollapsedDependencies(this.sentence);
				sentenceWords = dp.getWords();	
				taggedWords = dp.getTaggedWords();				
		}
		
		
		/**
		 * Check for the association between each temporal expression and medical concept identified in the sentence
		 * sentence (original sentence split into words using Stanford Parser) is initialized while creating the object 
		 * Example: "left circumflex coronary artery in July 1994 with stenting of the RCA in February 2002"
		 * @param List<String> temporalExpression
		 * 		   Example: {"July 1994","February 2002"}		  
		 * @param medicalConcept
		 * 		   Example: {"left circumflex","stenting"}
		 * @param rules
		 * 		   The order in which the rules are to be executed	
		 */
		public List<Link> returnAssociationLinks(List<String> temporalExpression, List<String> medicalConcept, List<Integer> rules) {
					boolean associationFlag = false;
					List<Link> allLinks = new ArrayList<Link>();
					
					if(rules.contains(1)){
							/**
							 * default association
							 * If this rule is applicable on the sentence then do not apply other rules
							 */							
							allLinks = OtherRules.defaultAssociation(temporalExpression, medicalConcept);
					} 					 
					
					if(allLinks.isEmpty()) {
							/**
							 * Apply dependency rules if default association is not applicable
							 */							
							for(String te : temporalExpression) {
									String processedTe = Preprocess.removePunctuation(te);						
									for(String mc : medicalConcept) {
											String processedMc = Preprocess.removePunctuation(mc);							
											Map<Integer, String> temporalExpr = Preprocess.returnLocation(sentenceWords, processedTe);
											Map<Integer, String> concept = Preprocess.returnLocation(sentenceWords, processedMc);																	
											for(Integer i : rules) {										
													associationFlag = applyDependencyRule(temporalExpr, concept, i);
													if(associationFlag == true)
														break;
											}
											int linkFlag = (associationFlag) ? 1 : 0; 
											Link link = new Link(te, mc, linkFlag);
											allLinks.add(link);
											associationFlag = false; //reset the flag before the next loop
									}
							}
					}	
					
					/**
					 * Only after dependency relations between TE and MC are checked, check for association between concept and concept
					 **/
					if(rules.contains(Dictionary.CONCEPT_CONCEPT_RELATION)) {
							allLinks = applyDependencyRulesIteratively(allLinks);
					}
					
					return allLinks;				
		}	
		
		
		/**
		 * In a sentence if you establish an association between concept A and the temporal expression, check if 
		 * concept B is association with concept A. If yes, then we can conclude that concept B is also associated with concept A
		 * Example: "Gangrene of left second toe in 2010-06-06 followed by an amputation". "Gangrene" is associated with 
		 * "2010-06-06" using the dependency relationship but amputation is not related to the same date using the dependency 
		 * relationship. But "amputation" is related to "Gangrene" so we can conclude that "amputation" is also associated 
		 * with the same date.      
		 * @param List<Link>
		 *         Example: [{"2010-06-06","Gangrene",1},{"2010-06-06","amputation",0}]         
		 * @return List<Link>		  		   
		 * 		    Example: [{"2010-06-06","Gangrene",1},{"2010-06-06","amputation",1}]
		 */
		public List<Link> applyDependencyRulesIteratively(List<Link> links) {							
					List<Integer> rules = new ArrayList<Integer>();
					rules.add(Dictionary.DIRECT_RELATIONSHIP);
					rules.add(Dictionary.SAME_ANCESTOR);
					rules.add(Dictionary.CONCEPT_IN_ANCESTOR);				
									
					List<String> expressionSetOne = new ArrayList<String>();
					List<String> expressionSetTwo = new ArrayList<String>();
					List<Link> linkBetweenConcepts = new ArrayList<Link>();
								
					for(Link l : links) {
						if(l.getLink() == 1)
							expressionSetOne.add(l.getConcept2());
						else 
							expressionSetTwo.add(l.getConcept2());						
					}	
					
					if(!expressionSetOne.isEmpty() && !expressionSetTwo.isEmpty()) { 
							linkBetweenConcepts = returnAssociationLinks(expressionSetOne, expressionSetTwo, rules);
							links = getAssociatedLinkDate(links, linkBetweenConcepts);						
					}
					return links;				
		}	
		
		
		/**
		 * Continuing the same example of "amputation" and "Gangrene" - after determining that "amputation" is associated with 
		 * "Gangrene", determine the date associated with "Gangrene" and tag it with "amputation"
		 * @param List<Link> originalLinks
		 *         Example: [{"2010-06-06","Gangrene",1},{"2010-06-06","amputation",0}]      
		 * @param List<Link> linksBetweenConcepts
		 * 		   Example: [{"Gangrene","amputation",1}]
		 * @return List<Link>
		 * 		   Example: [{"2010-06-06","amputation",1}]		  		   
		 */
		private List<Link> getAssociatedLinkDate(List<Link> originalLinks, List<Link> linksBetweenConcepts) {				
				for(Link lbc : linksBetweenConcepts) {
					if(lbc.getLink() == 1) {
							String parentConcept = lbc.getConcept1();					
							String date = getDate(originalLinks, parentConcept);
							String concept = lbc.getConcept2();
							for(Link ol : originalLinks) {
									if(ol.getConcept1().equalsIgnoreCase(date) && ol.getConcept2().equalsIgnoreCase(concept)) {
											ol.setLink(1);
									}
							}
					}
				}
				return originalLinks;
		}
		
		private String getDate(List<Link> links, String concept) {
				String date = "";		
				for(Link l : links) {
					if(l.getConcept2().equals(concept))
						date = l.getConcept1();
				}
				return date;
		}
		
		
		
		/**
		 * Test the association between a single concept and a single temporal expression
		 * If the medical concept/temporal expression is a phrase (i.e have more than one word) then check if any of the word in the phrase have an association
		 * @param List<String> temporalExpression
		 * 		   Example: {"July","1994"}
		 * @param List<String> medicalConcept
		 * 		   Example: {"stenting"}
		 */
		private boolean applyDependencyRule(Map<Integer, String> date, Map<Integer, String> concept, int ruleNumber) {
				boolean associationFlag = false;
				DependencyRules dr = new DependencyRules(dependencies, taggedWords);
				
				switch(ruleNumber) {
					case Dictionary.DIRECT_RELATIONSHIP:
							if(dr.checkForDirectRelation(date, concept))
								associationFlag = true;
							break;
										
					case Dictionary.SAME_ANCESTOR:
							if(dr.checkForSameAncestor(date, concept))
								associationFlag = true;
							break;
										
					case Dictionary.CONCEPT_IN_ANCESTOR:
							if(dr.checkForConceptInAncestor(date, concept))
								associationFlag = true;
							break;
										
					case Dictionary.EVENT_MODEL:
							List<String> events = getEvents(concept, date);
							for(String event : events) {
								Map<Integer, String> et = Preprocess.returnLocation(sentenceWords,event);									
								if(dr.checkForEventAssociation(date, concept, et))
									associationFlag = true;
								}
							break;			
					
					default:
							break;
				}
					
				return associationFlag;
		}
			
		/**
		 * Check for a verb acting on the medical concept or temporal expression 
		 */
		private List<String> getEvents(Map<Integer, String> concept, Map<Integer, String> date) {
				List<String> events = new ArrayList<String>();
				List<String> verbForms = Arrays.asList(Dictionary.VERB_FORMS);
		
				for(Dependency dep : dependencies) {
						if(CommonFunctions.contains(date,dep.getModifier(),dep.getModifierIndex())) {
							if(verbForms.contains(CommonFunctions.getPOSTag(dep.getHead(), taggedWords)))
								events.add(dep.getHead());																					 
						}
						if(CommonFunctions.contains(concept,dep.getModifier(),dep.getModifierIndex())) {
							if(verbForms.contains(CommonFunctions.getPOSTag(dep.getHead(), taggedWords)))
								events.add(dep.getHead());								
						}
				}
				return events;
				
		}		
}
