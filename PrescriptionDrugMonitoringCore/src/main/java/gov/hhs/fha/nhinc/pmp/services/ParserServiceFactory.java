/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

/**
 * @author mpnguyen
 *
 */
public class ParserServiceFactory {
    public static ParserServiceFactory getInstance() {
        return new ParserServiceFactory();
    }

    /**
     * @param htmlSourcePath
     * @return
     */
    public static ParserService getHTMLParser(final String htmlSourcePath) {
        return new HtmlParserService(htmlSourcePath);
    }
}
