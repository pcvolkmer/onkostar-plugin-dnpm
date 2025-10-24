/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.dnpm.oshelper.atc.services;

import dev.dnpm.oshelper.atc.AgentCode;
import dev.dnpm.oshelper.atc.AtcCode;
import dev.dnpm.oshelper.exceptions.FileParsingException;
import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service to query for agent codes based on ATC codes file
 *
 * @author Paul-Christian Volkmer
 * @since 2.0.0
 */
public class CsvAgentCodeService implements AgentCodeService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final List<AgentCode> codeList = new ArrayList<>();

    private static final String ATC_CODE_COLUMN = "ATC-Code";
    private static final String ATC_NAME_COLUMN = "ATC-Bedeutung";
    private static final String ATC_VERSION_COLUMN = "Version";

    public CsvAgentCodeService() {
        this.codeList.addAll(parseFile());
    }

    /**
     * Queries source for agents code starting with or name containing query string.
     * If size is zero, all available results will be returned.
     *
     * @param query The query string
     * @param size  Maximal amount of responses
     * @return A list with agent codes
     */
    @Override
    public List<AgentCode> findAgentCodes(final String query, final int size) {
        var resultStream = this.codeList.stream().filter(agentCode ->
                agentCode.getCode().toLowerCase().startsWith(query.toLowerCase())
                        || agentCode.getName().toLowerCase().contains(query.toLowerCase())
        );

        if (size > 0) {
            return resultStream.limit(size).collect(Collectors.toList());
        }
        return resultStream.collect(Collectors.toList());
    }

    protected List<AgentCode> parseFile() {
        var result = new ArrayList<AgentCode>();

        try {
            var inputStream = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("atc.csv"));
            var parser = CSVFormat.RFC4180
                    .withHeader()
                    .withDelimiter('\t')
                    .withIgnoreEmptyLines()
                    .withSkipHeaderRecord()
                    .parse(new InputStreamReader(inputStream));
            for (var row : parser) {
                if (!row.isMapped(ATC_CODE_COLUMN) || !row.isMapped(ATC_NAME_COLUMN)) {
                    throw new FileParsingException("No CSV column for ATC code or name found");
                }
                if (row.isMapped(ATC_VERSION_COLUMN)) {
                    result.add(new AtcCode(row.get(ATC_CODE_COLUMN).trim(), row.get(ATC_NAME_COLUMN), row.get(ATC_VERSION_COLUMN)));
                } else {
                    result.add(new AtcCode(row.get(ATC_CODE_COLUMN).trim(), row.get(ATC_NAME_COLUMN)));
                }
            }
            return result;
        } catch (IOException | FileParsingException e) {
            logger.warn("Error reading information from ATC codes");
        }
        logger.info("Found {} ATC codes", result.size());
        return result;
    }

}
