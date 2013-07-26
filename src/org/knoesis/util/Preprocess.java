package org.knoesis.util;

/**
 * @desc Preprocess the temporal expression, the medical procedure/problem and the original sentence
 * @author revathy
 * @date 1-July-2013
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.knoesis.config.*;

/**
 * @author revathy
 * @date 2-July-2013
 */
public class Preprocess {
	
	/**
	 * Split the expression into words using space as the splitting character
	 */
	public static List<String> splitExpression(String expression) {			
			String splitBy = "\\s+";
			List<String> words = Arrays.asList(expression.split(splitBy));
			return words;			
	}
	
	
	/**
	 * Remove punctuation marks from the sentence since they are not included in dependencies
	 */
	public static String removePunctuation(String sentence) {		 	
			for(String punct : Dictionary.PUNCTUATION) {
		 		if(sentence.contains(punct)) {
		 			sentence = sentence.replace(punct,"");
		 		}
		 	}			
		 	return sentence;		 	
	}	
	
	/**
	 * Check the location of words in a sentence
	 * 1. Loop through the sentence to find the first word from the expression 
	 * 2. When the first word is found, continue to check if the rest of the expression is found in that sequence - if not, then reset counter on the expression
	 * 3. Continue step 2 till the entire phrase is found
	 * @param sentenceWords - List of words in a sentence
	 * @param expression - words whose location in the sentence has to be determined
	 * @returns Map<Integer, String> - <index of the word in the sentence, word>
	 */
	
	public static Map<Integer,String> returnLocation(List<String> sentenceWords, String expression) {
			List<String> exprWords = splitExpression(expression);
			Map<Integer, String> exprLoc = new HashMap<Integer,String>();			
			int j = 0;		
			for(int i=0; i<sentenceWords.size() && j<exprWords.size(); i++) {
					String exprWord = exprWords.get(j);
					
					if(sentenceWords.get(i).equals(exprWord)) {							
							exprLoc.put(i+1,exprWord);
							int newCounter = i+1;
							j++;
							boolean flag = true;
							while(j < exprWords.size() && flag) {
								exprWord = exprWords.get(j);							
								if(sentenceWords.get(newCounter++).equalsIgnoreCase(exprWord)) { 
									exprLoc.put(newCounter,exprWord);
									j++;
								}
								else{
									exprLoc.clear();
									j = 0;
									flag = false;
								}
									
							}
					} 
				
			}				
			return exprLoc;			
	}
		
}
