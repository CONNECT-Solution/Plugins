/** 
 * FHIR Reference Client used without modification from repo:
 * https://github.com/cnanjo/FhirJavaReferenceClient
 * 
 * Reference Client license: http://www.apache.org/licenses/LICENSE-2.0.txt
*/

package org.hl7.fhir.client;

/**
 * Enumeration for preferred AtomFeed resource formats.
 * 
 * @author Claude Nanjo
 *
 */
public enum FeedFormat {
    FEED_XML("application/atom+xml"),
    FEED_JSON("application/fhir+json");

	
	private String header;
	
	private FeedFormat(String header) {
		this.header = header;
	}
	
	public String getHeader() {
		return this.header;
	}

}
