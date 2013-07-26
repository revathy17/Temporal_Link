package org.knoesis.util;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.knoesis.model.Dependency;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * Use the stanford dependency parser to identify:
 * 	 1. The dependency between words in a sentence
 * 	 2. Sentence as a list of words - this is used to identify the location of temporal expression and medical concept in the sentence
 *      The taggedWord list is used instead of splitting sentence using space, so the index of the words in dependency tree matches 
 *      the index of the words in temporal expression and medical concept       
 * @author revathy
 * @date 1-July-2013
 */

public class DependencyParser {
		
			LexicalizedParser parser;
			TreebankLanguagePack languagePack;
			GrammaticalStructureFactory gsf;
			List<String> words;
			List<TaggedWord> taggedWords;
			static Logger log = Logger.getLogger(DependencyParser.class.getName());
			
			/**
			 * Initialize the dependency parser
			 */
			public DependencyParser(LexicalizedParser parser) {					
					this.parser = parser;
					languagePack = new PennTreebankLanguagePack();
					gsf = languagePack.grammaticalStructureFactory();	
					words = new ArrayList<String>();
			}			
		
			/**
			 * Get the collapsed dependencies
			 * @param sentence
			 * @return List<Dependency> A list of objects of type org.knoesis.model.Dependency
			 */			
			public List<Dependency> getCollapsedDependencies(String originalSentence) throws Exception {
					Reader reader = new StringReader(originalSentence);			
					List<Dependency> allDependencies = new ArrayList<Dependency>();
					for (List<HasWord> sentence : new DocumentPreprocessor(reader)) {
					 
					 		Tree parseTree = parser.apply(sentence);
					 		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
					 		Collection<TypedDependency> dependencies = gs.typedDependenciesCollapsed();
					 		log.info("Parsing sentence : " + originalSentence);
					 		log.info("The Collapsed dependencies are: \n" + dependencies);
					 		extractWords(originalSentence, parseTree);
					 		allDependencies = parseDependencies(dependencies, words);	
					 		taggedWords = parseTree.taggedYield();
					 		
					}				 
					return allDependencies;					
			}		
			
			public List<String> getWords() {
					return words;
			}
			
			public List<TaggedWord> getTaggedWords() {
					return taggedWords;
			}
			
			private void extractWords(String sentence, Tree parseTree) throws Exception {					
					List<TaggedWord> taggedWordList = new ArrayList<TaggedWord>();
					taggedWordList = parseTree.taggedYield(taggedWordList);					
					for(TaggedWord T: taggedWordList) {
						words.add(T.word().replace("\\", ""));
					}									
			}
			
			/**
			 * Parse dependencies to get the head, modifier and the relationship
			 */
			private List<Dependency> parseDependencies(Collection<TypedDependency> dependencies, List<String> words) throws Exception {
				 	List<Dependency> allDependencies = new ArrayList<Dependency>();
				 	int headIndex = 0, modifierIndex = 0;
				 	for(TypedDependency dep : dependencies) {
				 			headIndex = dep.gov().index();
				    		modifierIndex = dep.dep().index();	
				    		String head = (headIndex == 0) ? "ROOT" : words.get(headIndex - 1);
				    		String modifier = (modifierIndex == 0) ? "ROOT" : words.get(modifierIndex - 1);
				    		String relationship = dep.reln().getShortName();				    
				    		Dependency d = new Dependency(relationship, head, headIndex, modifier, modifierIndex);
				    		allDependencies.add(d);				    		
				 	}
				 	return allDependencies;
			}	
			
			
			
}
