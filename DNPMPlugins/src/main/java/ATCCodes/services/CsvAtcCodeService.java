package ATCCodes.services;

import ATCCodes.AgentCode;
import ATCCodes.AtcCode;
import ATCCodes.FileParsingException;
import org.apache.commons.csv.CSVFormat;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to query for agent codes based on WHO xml file
 *
 * @author Paul-Christian Volkmer
 * @since 0.1.0
 */
@Service
public class CsvAtcCodeService extends FileBasedAgentCodeService {

    public CsvAtcCodeService(final ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    protected List<AgentCode> parseFile(final ResourceLoader resourceLoader) {
        var result = new ArrayList<AgentCode>();
        var filename = getFilePath("atc.csv");
        try {
            var inputStream = resourceLoader.getResource(filename).getInputStream();
            var parser = CSVFormat.RFC4180
                    .withHeader()
                    .withSkipHeaderRecord()
                    .parse(new InputStreamReader(inputStream));
            for (var row : parser) {
                if (!row.isMapped("CODE") || !row.isMapped("NAME")) {
                    throw new FileParsingException("No CSV column 'CODE' or 'NAME' found");
                }
                result.add(new AtcCode(row.get("CODE"), row.get("NAME")));
            }
            logger.info("Found CSV file for ATC-Codes.");
            return result;
        } catch (IOException | FileParsingException e) {
            logger.warn("Error reading CSV file '{}' for ATC-Codes. Proceeding without data", filename);
        }
        return result;
    }

}