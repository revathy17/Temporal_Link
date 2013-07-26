package test.model;

public class Association {
		int id;
		int document_id;
		int sentence_id;
		String sentence;
		String temporal_expression;
		String medical_concept;		
		int association_flag;
		
		public Association (int id, int document_id, int sentence_id, String sentence, String temporal_expression,
				String medical_concept) {			
				this.id = id;
				this.document_id = document_id;
				this.sentence_id = sentence_id;
				this.sentence = sentence;
				this.temporal_expression = temporal_expression;
				this.medical_concept = medical_concept;			
				this.association_flag = -1;
		}
		
		public int getId() {
				return this.id;
		}
		public int getDocumentId() {
				return this.document_id;
		}
		
		public int getSentenceId() {
				return this.sentence_id;
		}
		
		public String getSentence() {
				return this.sentence;
		}
		
		public String getTemporalExpression() {
				return this.temporal_expression;
		}
		
		public String getMedicalConcept() {
				return this.medical_concept;
		}
		
		public void setAssociationFlag(int flag) {
				this.association_flag = flag;
		}
		
		public int getAssociationFlag() {
				return this.association_flag;
		}
}
