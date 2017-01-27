/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import java.io.IOException;

import gov.hhs.fha.nhinc.pmp.dto.ProviderReport;
import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class HtmlParserService implements ParserService {
    private static final Logger logger = LoggerFactory.getLogger(HtmlParserService.class);
    private Document htmlDocument;

    /**
     *
     */
    private HtmlParserService() {
        super();

    }

    /**
     * @param htmlSourcePath
     */
    public HtmlParserService(String htmlSourcePath) {
        File htmlSourceFile = new File(htmlSourcePath);
        logger.debug("Load resource from {}", htmlSourcePath);
        try {
            htmlDocument = Jsoup.parse(htmlSourceFile, "UTF-8");
        } catch (IOException e) {
            logger.error("Unable to parse htmlSource {}", e.getLocalizedMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see gov.hhs.fha.nhinc.pmp.services.ParserService#getAllPrescriptions(java.lang.String)
     */
    @Override
    public List<PrescriptionReport> getAllPrescriptions() {
        Element prescriptionTable = htmlDocument.getElementById("prescriptions-table");
        Elements prescriptionRows = prescriptionTable.select("tbody > tr");
        List<PrescriptionReport> prescriptionList = new ArrayList<>();
        for (Element row : prescriptionRows) {

            PrescriptionReport prescriptionReport = new PrescriptionReport();
            // retrieve TD tag Cell as lists
            Elements tDCells = row.select("td");
            prescriptionReport.setFileStrDate(tDCells.get(0).ownText());
            prescriptionReport.setDrugName(tDCells.get(2).ownText());
            prescriptionReport.setDrugCount(Integer.parseInt(tDCells.get(3).ownText()));
            prescriptionReport.setDrugDuration(Integer.parseInt(tDCells.get(4).ownText()));
            prescriptionReport.setPrescriber(tDCells.get(5).ownText());
            prescriptionReport.setPharmacyName(tDCells.get(6).ownText());
            prescriptionReport.setRefill(tDCells.get(7).ownText());
            prescriptionReport.setMgEq(Double.parseDouble(tDCells.get(8).ownText()));
            prescriptionReport.setMgEdPerDay(tDCells.get(9).ownText());
            prescriptionReport.setPaymentType(tDCells.get(10).ownText());
            prescriptionReport.setPmpState(tDCells.get(11).ownText());
            prescriptionList.add(prescriptionReport);
        }
        return prescriptionList;
    }

    /*
     * (non-Javadoc)
     *
     * @see gov.hhs.fha.nhinc.pmp.services.ParserService#getAllProviders()
     */
    @Override
    public List<ProviderReport> getAllProviders() {
        return extractProviderByTagId("providers-table");
    }

    /*
     * (non-Javadoc)
     *
     * @see gov.hhs.fha.nhinc.pmp.services.ParserService#getAllPharmacies()
     */
    @Override
    public List<ProviderReport> getAllPharmacies() {
        return extractProviderByTagId("pharmacies-table");
    }

    /**
     * @return
     */
    private final List<ProviderReport> extractProviderByTagId(final String tagId) {
        Element providerTable = htmlDocument.getElementById(tagId);
        Elements providerRows = providerTable.select("tbody > tr");
        List<ProviderReport> providerReportList = new ArrayList<>();
        for (Element row : providerRows) {

            ProviderReport providerReport = new ProviderReport();
            // retrieve TD tag Cell as lists
            Elements tDCells = row.select("td");
            providerReport.setProviderName(tDCells.get(0).ownText());
            providerReport.setAddress(tDCells.get(1).ownText());
            providerReport.setCity(tDCells.get(2).ownText());
            providerReport.setState(tDCells.get(3).ownText());
            providerReport.setZipCode(tDCells.get(4).ownText());
            providerReport.setDea(tDCells.get(5).ownText());

            providerReportList.add(providerReport);
        }
        return providerReportList;

    }
}