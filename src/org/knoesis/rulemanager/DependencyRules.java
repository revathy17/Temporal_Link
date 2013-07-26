package org.knoesis.rulemanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.knoesis.model.Dependency;
import org.knoesis.util.CommonFunctions;

import edu.stanford.nlp.ling.TaggedWord;

/**
 *  I. Check for the following three instances of relationships:
 *        		a. Direct relationship between Medical-Procedure/Medical-Problem and Dates
 *        		b. Date and Medical-Procedure/Medical-Problem are in the different children of the same ancestor
 *        		c. Date is in the ancestor of Medical-Procedure/Medical-Problem  
 * II. Check for relationship modeled as "CONCEPT - EVENT - DATE", where "EVENT" is a verb
 *     Example: "Replacement" - "Underwent" - "2011-10-18"        
 *      
 * @author revathy
 * @date 1-July-2013
 */

public class DependencyRules {
		 
		List<Dependency> collapsedDependencies;
		List<TaggedWord> taggedWords;
		static Logger log = Logger.getLogger(DependencyRules.class.getName());
		
		/**
		 * @param sentence Original Sentence
		 * @param dt Date
		 * @param medConcept Medical Procedure/Problem
		 */
		public DependencyRules(	List<Dependency> collapsedDependencies, List<TaggedWord> taggedWords) {
						this.collapsedDependencies = collapsedDependencies;
						this.taggedWords = taggedWords;
		}
		
		 /**
         * Check for direct relationship
         * Example: prep_in(surgery, 1997)
         */
        public boolean checkForDirectRelation(Map<Integer, String> phrase1, Map<Integer, String> phrase2) {
        			    for(Dependency d : collapsedDependencies) {
                        	    String head = d.getHead();
                                int headIndex = d.getHeadIndex();
                                String modifier = d.getModifier();
                                int modifierIndex = d.getModifierIndex();                               
                                if((CommonFunctions.contains(phrase1,head,headIndex) && CommonFunctions.contains(phrase2,modifier,modifierIndex)) ||
                                                (CommonFunctions.contains(phrase2,head,headIndex) && CommonFunctions.contains(phrase1,modifier,modifierIndex))) {   
                                				log.info("Direct Relationship: " + head + " and " + modifier);
                                                return true;
                                }
                        }
                        return false;
        }

		
		
		/**
		 * @desc Check if Date and Medical Concept are in the different children of the same ancestor	
		 *        Example: dobj(get, echocardiogram)
		 * 			       tmod(get, today)	  
		 */
		public boolean checkForSameAncestor(Map<Integer, String> phrase1, Map<Integer, String> phrase2) {
						Map<Integer,String> phrase1Ancestors = new HashMap<Integer,String>();
						Map<Integer,String> phrase2Ancestors = new HashMap<Integer,String>();
					
											
						for(Dependency d : collapsedDependencies) {
								String head = d.getHead();	
								int headIndex = d.getHeadIndex();
								String modifier = d.getModifier();
								int modifierIndex = d.getModifierIndex();
								if(CommonFunctions.contains(phrase1,modifier,modifierIndex)) 
									phrase1Ancestors.put(headIndex,head);																							
								else if(CommonFunctions.contains(phrase2,modifier,modifierIndex)) 
									phrase2Ancestors.put(headIndex,head);
																						
						}
				
						for(Map.Entry<Integer, String> ca : phrase1Ancestors.entrySet()) {
								String conceptA = ca.getValue();
								int conceptI = ca.getKey();
								for(Map.Entry<Integer, String> da : phrase2Ancestors.entrySet()) {
									if((da.getKey() == conceptI) && (ca.getValue().equalsIgnoreCase(conceptA))) {
										log.info("Same Ancestor: " + conceptA);
										return true;										
									}
										
								}
						}						
						return false;
				
		}
		
		/**
		 * @desc Check if Medical Concepts are in the ancestors of Date
		 *        Check only one level
		 *        Example: partmod(echocardiogram, done)
		 *                 tmod(done, year)
		 */
		public boolean checkForConceptInAncestor(Map<Integer, String> phrase1, Map<Integer, String> phrase2) {
				
						Map<Integer,String> parent = new HashMap<Integer, String>();						
						for(Dependency dep : collapsedDependencies) {
							if(CommonFunctions.contains(phrase2,dep.getModifier(),dep.getModifierIndex())) {
								parent.put(dep.getHeadIndex(), dep.getHead());
							}				
						}				
				
						for(Dependency dep : collapsedDependencies) {
							if(CommonFunctions.contains(phrase1,dep.getHead(),dep.getHeadIndex())) {
								if(CommonFunctions.contains(parent,dep.getModifier(),dep.getModifierIndex())) {
									log.info("Concept in Ancestor: " + dep.getModifier());
									return true;
								}
									
							}
						}					
						
						parent.clear();					
						for(Dependency dep : collapsedDependencies) {
							if(CommonFunctions.contains(phrase1,dep.getModifier(),dep.getModifierIndex())) {								
								parent.put(dep.getHeadIndex(),dep.getHead());
							}
						}
						
						for(Dependency dep : collapsedDependencies) {
							if(CommonFunctions.contains(phrase2,dep.getHead(),dep.getHeadIndex())) {
								if(CommonFunctions.contains(parent,dep.getModifier(),dep.getModifierIndex())) {
									log.info("Concept in Ancestor: " + dep.getModifier());
									return true;
								}
									
							}
						}
						return false;
		}	
		
		/**
		 * This function checks for the CONCEPT - EVENT - TEMPORAL_EXPRESSION model
		 * @param original
		 * @param medical concept
		 * @param temporal expression  
		 */
		public boolean checkForEventAssociation(Map<Integer, String> phrase1, Map<Integer, String> phrase2, Map<Integer, String> event) {
						if(checkForDirectRelation(phrase1, event) || checkForConceptInAncestor(phrase1, event) || checkForSameAncestor(phrase1, event)) {
							if(checkForDirectRelation(phrase2, event) || checkForConceptInAncestor(phrase2, event) || checkForSameAncestor(phrase2, event)) {
									return true;
							}
						}
						return false;
		}
		
		
}
