package ATCCodes.services;

import ATCCodes.AgentCode;
import ATCCodes.AtcCode;
import ATCCodes.FileParsingException;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to query for agent codes based on WHO xml file
 *
 * @author Paul-Christian Volkmer
 * @since 0.1.0
 */
@Service
public class WhoAtcCodeService extends FileBasedAgentCodeService {

    public WhoAtcCodeService(final ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    protected List<AgentCode> parseFile(final ResourceLoader resourceLoader) {
        var result = new ArrayList<AgentCode>();
        var filename = getFilePath("atc.xml");
        try {
            var inputStream = resourceLoader.getResource(filename).getInputStream();
            var context = JAXBContext.newInstance(XmlResource.class);
            var xmlResource = (XmlResource) context.createUnmarshaller().unmarshal(inputStream);
            for (var row : xmlResource.data.rows) {
                if (null == row.code || null == row.name) {
                    throw new FileParsingException("No XML attribute 'ATCCode' or 'Name' found");
                }
                result.add(new AtcCode(row.code, row.name));
            }
            logger.info("Found WHO XML file for ATC-Codes.");
            return result;
        } catch (IOException e) {
            logger.warn("Error reading WHO XML file '{}' for ATC-Codes. Proceeding without inserting data", filename);
        } catch (JAXBException | FileParsingException e) {
            logger.warn("Error parsing WHO XML file '{}' for ATC-Codes. Proceeding without inserting data", filename);
        }
        return result;
    }

    @XmlRootElement(name = "xml")
    private static class XmlResource {
        @XmlElement(name = "data", namespace = "urn:schemas-microsoft-com:rowset")
        public XmlData data;
    }

    private static class XmlData {
        @XmlElement(name = "row", namespace = "#RowsetSchema")
        public List<XmlRow> rows;
    }

    private static class XmlRow {
        @XmlAttribute(name = "ATCCode")
        public String code;

        @XmlAttribute(name = "Name")
        public String name;
    }
}