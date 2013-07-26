package test.results;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.knoesis.model.Link;
import org.knoesis.rulemanager.ApplyRules;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

import test.data.DataManager;
import test.evaluation.Evaluate;

/**
 * Use this class to recreate results posted in the technical report
 * Dataset 1 : id 1 to 180
 * Dataset 2 : id 181 to 311  
 * @author revathy
 */

public class testResults {
		
			public static void runTest(int id1, int id2) {
					DataManager dm = new DataManager();
					List<List<Integer>> allDocSentenceIds = dm.getDistinctDocSentences(id1, id2);			
					LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");
					
					for(List<Integer> docSentId : allDocSentenceIds) {							
							int documentId = docSentId.get(0);
							int sentenceId = docSentId.get(1);
							System.out.println("Parsing document " + documentId + " and sentence " + sentenceId);
							dm = new DataManager();
							List<String> temporalExpression = dm.getData("temporal_expression", documentId, sentenceId);
							dm = new DataManager();
							List<String> medicalConcept = dm.getData("medical_concept",documentId, sentenceId);
							dm = new DataManager();
							String sentence = dm.getOriginalSentence(documentId, sentenceId);
							ApplyRules ar = null;
							
							try {
									ar = new ApplyRules(sentence, parser);
							} catch (Exception e) {
									System.out.println("Error in extracting dependencies from the sentence : " + sentence);
							}
							
							List<Integer> rules = new ArrayList<Integer>();
							rules.add(1);
							rules.add(2);
							rules.add(3);
							rules.add(4);					
					
							List<Link> links = ar.returnAssociationLinks(temporalExpression, medicalConcept, rules);							
							for(Link l : links) {
									System.out.println(l.getConcept1() + "\t" + l.getConcept2() + "\t" + l.getLink());
									dm = new DataManager();
									dm.updateAssociation(documentId,sentenceId,l.getConcept1(),l.getConcept2(),l.getLink());								
							}
					}
				
			}
			
			public static void printResults(int id1, int id2) {				
					Evaluate e = new Evaluate(id1, id2);
					System.out.println("Correct Annotations \tTrue Positives \tTrue Negatives \tFalse Positives \tFalse Negatives \tAccuracy");
					System.out.println(e.getCorrectAnnotations() + "\t\t\t" + e.getTruePositives() + "\t\t\t" + e.getTrueNegatives() + "\t\t"
							+ e.getFalsePositives() + "\t\t" + e.getFalseNegatives() + "\t\t\t" + formatDouble(e.getAccuracy() * 100) + "%");
			}

			public static String formatDouble(double number) {
					DecimalFormat df = new DecimalFormat("#.##");
					return df.format(number);
			}
			
			public static void main(String[] args) {				
					/*Run the test for Batch 1*/
					//runTest(1, 180);
					/*Print the evaluation results*/
					System.out.println("*********Batch 1*********");
					printResults(1, 180);
					
					/*Run the test for Batch 2*/
					//runTest(181, 311);
					/*Print the evaluation results*/
					System.out.println("*********Batch 2*********");
					printResults(181, 311);
					
			}		
			
}	
