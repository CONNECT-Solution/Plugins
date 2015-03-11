/** 
 * FHIR Reference Client used without modification from repo:
 * https://github.com/cnanjo/FhirJavaReferenceClient
 * 
 * Reference Client license: http://www.apache.org/licenses/LICENSE-2.0.txt
*/

package org.hl7.fhir.client;

/**
 * Enumeration for preferred FHIR resource formats.
 * 
 * @author Claude Nanjo
 *
 */
public enum ResourceFormat {
	
    RESOURCE_XML("application/fhir+xml"),
    RESOURCE_JSON("application/fhir+json");

	
	private String header;
	
	private ResourceFormat(String header) {
		this.header = header;
	}
	
	public String getHeader() {
		return this.header;
	}

}
