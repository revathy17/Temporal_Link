package test.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;



public class DataManager {
	private Connection connection = null;	
	static Logger log = Logger.getLogger(DataManager.class.getName());
	
	public DataManager() {
				try {
					Class.forName("com.mysql.jdbc.Driver");
					connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test_temporal_link?user=root&password=admin");			
				} catch(Exception e) {
					log.error("Error in initializing datamanager", e);
				} 
	}
	
		
	public List<List<Integer>> getDistinctDocSentences(int id1, int id2) {
				String sql = "SELECT DISTINCT document_id, sentence_id FROM date_concept_association " +
							 "WHERE id BETWEEN " + id1 + " AND " + id2;
				
				Statement stmt = null;
				List<List<Integer>> docSentenceIds  = new ArrayList<List<Integer>>();
				
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);					
						while(rs.next()) {
							List<Integer> ids = new ArrayList<Integer>();
							ids.add(rs.getInt("document_id"));
							ids.add(rs.getInt("sentence_id"));
							docSentenceIds.add(ids);
						}
				} catch (Exception e) {
						log.error("Error in retrieving document and sentence ids", e);
				} finally {
						cleanUpDatabase(stmt);
				}
				return docSentenceIds;
			
	}
		
	
	/**
	 * @param field - medical_concept or temporal_expression
	 * @param document_id
	 * @param sentence_id
	 * @return
	 */
	public List<String> getData(String field, int document_id, int sentence_id) {
				String sql = "SELECT DISTINCT " + field + " FROM date_concept_association WHERE document_id = " + document_id +
							" AND sentence_id = " + sentence_id;
				Statement stmt = null;
				List<String> data = new ArrayList<String>();
				
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						
						while(rs.next()) {
								data.add(rs.getString(1));
						}
				} catch (Exception e) {
						log.error("Error in retrieving " + field + " from the database", e);
				} finally {
						cleanUpDatabase(stmt);
				}
				return data;			
	}
	
	public String getOriginalSentence(int document_id, int sentence_id) {
				String sql = "SELECT sentence FROM date_concept_association WHERE document_id = " + document_id + " AND sentence_id = "
							+ sentence_id;
				Statement stmt = null;
				
				String sentence = "";
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()) {
								sentence = rs.getString(1);
						}
					
				} catch (Exception e) {
						log.error("Error in retrieving original sentence from the database ", e);
				} finally {
						cleanUpDatabase(stmt);
				}
				return sentence;
			
	}
	
	public void updateAssociation(int documentId, int sentenceId, String date, String concept, int associationFlag) {		
				String sql = "UPDATE date_concept_association SET association_flag = ? WHERE document_id = ? AND sentence_id = ? AND medical_concept = ? AND temporal_expression = ? ";
				PreparedStatement ps = null;
				try {
						connection.setAutoCommit(false);
						ps = connection.prepareStatement(sql);						
						ps.setInt(1, associationFlag);
						ps.setInt(2, documentId);
						ps.setInt(3, sentenceId);
						ps.setString(4, concept);
						ps.setString(5, date);
						ps.execute();
		
				} catch (Exception e) {
						log.error("Error in updating association status in the database",e);
				} finally {
						cleanUpDatabase(ps);
				}
	}

	public double getTruePositives(int id1, int id2) {
				String sql = "SELECT count(id) AS count FROM date_concept_association WHERE " +
							"association_flag_manual = 1 " +
							"AND association_flag = 1 " +
							"AND id BETWEEN " + id1 + " AND " + id2;						
				
				Statement stmt = null;
				double count = -1;
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next())
							count = rs.getDouble("count");
				} catch (Exception e) {
						log.error("Error in retrieving True Positives from the database", e);
				}
				return count;
	}
	
	
	public double getTrueNegatives(int id1, int id2) {
				String sql = "SELECT count(id) AS count FROM date_concept_association WHERE " +
							"association_flag_manual = 0 " +
							"AND association_flag = 0 " +
							"AND id BETWEEN " + id1 + " AND " + id2;							
				
				Statement stmt = null;
				double count = -1;
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()) 
							count = rs.getDouble("count");
				} catch (Exception e) {
						log.error("Error in retrieving True Negatives",e);
				}
				return count;
 	}
	
	public double getFalsePositives(int id1, int id2) {
				String sql = "SELECT concept_type, count(id) AS count FROM date_concept_association WHERE " +
							"association_flag_manual = 0 " +
							"AND association_flag = 1 " +
							"AND id BETWEEN " + id1 + " AND " + id2;						
				
				Statement stmt = null;
				double count = -1;
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()) 
								count = rs.getDouble("count");
				} catch (Exception e) {
						log.error("Error in retrieving False Positives", e);
				}
				return count;
	}
	
	public double getFalseNegatives(int id1, int id2) {
				String sql = "SELECT count(id) AS count FROM date_concept_association WHERE " +
							"association_flag_manual = 1 " +
							"AND association_flag = 0 " +
							"AND id BETWEEN " + id1 + " AND " + id2;			
				Statement stmt = null;
				double count = -1;
 				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()) 
							count = rs.getDouble("count");
								
				} catch (Exception e) {
						log.error("Error in retrieving False Positives", e);
				}
				return count;
	}

	public double getManualAnnotation(int id1, int id2) {
				String sql = "SELECT count(id) AS count FROM date_concept_association WHERE " +
							 "id BETWEEN " + id1 + " AND " + id2;
				Statement stmt = null;
				
				double count = -1;
				try {
						stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()) 
							count = rs.getInt("count");
				} catch (Exception e) {
						log.error("Error in retrieving manual annotation", e);
				}
				return count;
	}
	/**
	 * @desc Database cleanup after Select operations
	 */
	private void cleanUpDatabase(Statement stmt) {		
				try {
					connection.close();
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}		
	}
	
	/**
	 * @desc Database cleanup after Insert operations
	 */
	private void cleanUpDatabase(PreparedStatement ps) {		
				try {
					ps.close();
					connection.commit();			
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}		
	}
}
