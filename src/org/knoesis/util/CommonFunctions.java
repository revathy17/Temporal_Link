package org.knoesis.util;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.TaggedWord;

public class CommonFunctions {
		
			/**
			 * This function checks if a word is present in a sentence at the same index  
			 * @param source Sentence to be checked
			 * @param target Word to be checked
			 * @param targetIndex The index of the word to be checked
			 * @return true if the word is present in the dictionary at the same location else return false
			 */
			public static boolean contains(Map<Integer, String> source, String target, int targetIndex) {
					for(Map.Entry<Integer, String> s : source.entrySet()) {
						if((s.getKey() == targetIndex) && (s.getValue().equalsIgnoreCase(target)))
							return true;
					}				
					return false;
			}	
			
			
			/**
			 * @param word
			 * @return pos tag of the word
			 */
			public static String getPOSTag(String word, List<TaggedWord> taggedWords) {
					String tag = "";
					for(TaggedWord tw : taggedWords) {
						String w = tw.word().replace("\\","");
						if(w.equalsIgnoreCase(word)) 
							tag = tw.tag();
					}
					return tag;
			}
			
}
