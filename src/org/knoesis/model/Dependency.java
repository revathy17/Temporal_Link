package org.knoesis.model;

/**
 * Use this class to represent the dependency between words in a sentence
 * Example: nsubj(makes-8,Bell-1)
 * relationship - nsubj
 * head - makes
 * headIndex - 8 (Location of the head-word in the sentence)
 * modifier - Bell 
 * modifierIndex - 1 (Location of the modifier-word in the sentence)
 *  
 * @author revathy  
 */

public class Dependency {
		
				String relationship;
				String head;
				int headIndex;
				String modifier;
				int modifierIndex;
			
				public Dependency(String relationship, String head, int headIndex, String modifier, int modifierIndex) {
							this.relationship = relationship;
							this.head = head;
							this.headIndex = headIndex;
							this.modifier = modifier;
							this.modifierIndex = modifierIndex;				
				}
			
				public String getRelationship() {
							return this.relationship;
				}
			
				public String getHead() {
							return this.head;
				}
			
				public int getHeadIndex() {
							return this.headIndex;
				}
			
				public String getModifier() {
							return this.modifier;
				}
			
				public int getModifierIndex() {
							return this.modifierIndex;
				}
			
}
