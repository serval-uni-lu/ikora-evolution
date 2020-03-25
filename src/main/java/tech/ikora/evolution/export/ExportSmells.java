package tech.ikora.evolution.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import tech.ikora.evolution.results.SmellResults;
import tech.ikora.smells.SmellMetric;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExportSmells {
    public static void export(String output, SmellResults smellResults) throws IOException {
        final FileWriter out = new FileWriter(output);
        final List<SmellMetric.Type> smellTypes = new ArrayList<>(smellResults.getSmellTypes());

        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(getHeaders(smellTypes)))) {
            printRecords(printer, smellResults, smellTypes);
        }
    }

    private static String[] getHeaders(List<SmellMetric.Type> smellTypes) {
        List<String> headers = new ArrayList<>(2 + smellTypes.size());

        headers.add("version");
        headers.add("test_case");

        headers.addAll(smellTypes.stream().map(Enum::name).collect(Collectors.toList()));

        return headers.toArray(new String[0]);
    }

    private static void printRecords(CSVPrinter printer, SmellResults smellResults, List<SmellMetric.Type> smellTypes) throws IOException {
        int size = 2 + smellTypes.size();

        for(SmellResults.Record record: smellResults){
            Object[] line = new String[size];
            line[0] = record.getVersion();
            line[1] = record.getTestCase().getName().getText();

            int position = 2;
            for(SmellMetric.Type smellType: smellTypes){
                line[position++] = String.valueOf(record.getSmellMetrics(smellType));
            }

            printer.printRecord(line);
        }
    }
}
