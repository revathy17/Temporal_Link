package test.evaluation;

import test.data.DataManager;

public class Evaluate {

			double truePositives;
			double trueNegatives;
			double falsePositives;
			double falseNegatives;
			double accuracy;
			
			public Evaluate(int id1, int id2) {
					DataManager dm = new DataManager();
					this.truePositives = dm.getTruePositives(id1, id2);
					
					dm = new DataManager();
					this.trueNegatives = dm.getTrueNegatives(id1, id2);
					
					dm = new DataManager();
					this.falsePositives = dm.getFalsePositives(id1, id2);		
					
					dm = new DataManager();
					this.falseNegatives = dm.getFalseNegatives(id1, id2);
					
					dm = new DataManager();
					this.accuracy =  (this.truePositives + this.trueNegatives) / (dm.getManualAnnotation(id1, id2));  
			}
			
			public double getTruePositives() {
					return this.truePositives;
			}
			
			public double getTrueNegatives() {
					return this.trueNegatives;
			}
			
			public double getFalsePositives() {
					return this.falsePositives;
			}
			
			public double getFalseNegatives() {
					return this.falseNegatives;
			}
			
			public double getCorrectAnnotations() {	
					return (this.truePositives + this.trueNegatives);
			}		
			
			public double getAccuracy() {
					return this.accuracy;
			}		
			
}
