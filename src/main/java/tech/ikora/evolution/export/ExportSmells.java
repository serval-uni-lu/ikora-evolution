package tech.ikora.evolution.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import tech.ikora.evolution.results.SmellResults;

import java.io.FileWriter;
import java.io.IOException;

public class ExportSmells {
    public static void export(String output, SmellResults smellResults) throws IOException {
        final FileWriter out = new FileWriter(output);

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(getHeaders()))) {
            printRecords(printer, smellResults);
        }
    }

    private static String[] getHeaders() {
        return new String[] {
                "version",
                "test_case",
                "smell_name",
                "smell_metric"
        };
    }

    private static void printRecords(CSVPrinter printer, SmellResults smellResults) throws IOException {
        for(SmellResults.Record record: smellResults){
            Object[] line = new Object[] {
                    record.getVersion(),
                    record.getTestCase().getName().getText(),
                    record.getSmellMetric().getType().name(),
                    String.valueOf(record.getSmellMetric().getValue())
            };

            printer.printRecord(line);
        }
    }
}
