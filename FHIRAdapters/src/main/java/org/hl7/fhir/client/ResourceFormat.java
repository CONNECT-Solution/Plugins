/** 
 * FHIR Reference Client used with modifications from repo:
 * https://github.com/cnanjo/FhirJavaReferenceClient
 * 
 * Reference Client license: http://www.apache.org/licenses/LICENSE-2.0.txt
*/

package org.hl7.fhir.client;

/**
 * Enumeration for preferred FHIR resource formats.
 * 
 * @author Claude Nanjo / jsmith
 *
 */
public enum ResourceFormat {
	
    RESOURCE_XML("application/xml+fhir"),
    RESOURCE_JSON("application/json+fhir");

	
	private String header;
	
	private ResourceFormat(String header) {
		this.header = header;
	}
	
	public String getHeader() {
		return this.header;
	}

}
