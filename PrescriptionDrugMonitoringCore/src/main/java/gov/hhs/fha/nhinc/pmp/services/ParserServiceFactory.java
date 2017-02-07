/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import java.io.InputStream;

/**
 * @author mpnguyen
 *
 */
public class ParserServiceFactory {
    private static ParserServiceFactory INSTANCE;

    /**
     *
     */
    private ParserServiceFactory() {

    }

    public static ParserServiceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ParserServiceFactory();
        }
        return INSTANCE;
    }

    /**
     * @param htmlSourcePath
     * @return
     */
    public ParserService getHTMLParser(final String htmlSourcePath) {
        return new HtmlParserService(htmlSourcePath);
    }

    /**
     * @param ccDAInputStream
     * @return
     */
    public CCDAParser getCCDAParser(InputStream ccDAInputStream) {
        return new CCDAParser(ccDAInputStream);
    }
}
