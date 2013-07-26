package org.knoesis.model;

/**
 * @date 12-July-2013
 * @author revathy
 * Use this class to represent relationship between two concepts 
 */

public class Link {

				int id;
				String concept1;
				String concept2;
				int linkFlag;
			
				public Link(String concept1, String concept2, int linkFlag) {
						this.concept1 = concept1;
						this.concept2 = concept2;
						this.linkFlag = linkFlag;
				}
				
				public String getConcept1() {
						return this.concept1;
				}
			
				public void setConcept1(String concept1) {
						this.concept1 = concept1;
				}
			
				public String getConcept2() {
						return this.concept2;				
				}
			
				public void setConcept2(String concept2) {
						this.concept2 = concept2;
				}
				
				public int getLink() {
						return this.linkFlag;
				}
				
				public void setLink(int linkFlag) {
						this.linkFlag = linkFlag;
				}
}
