package tech.ikora.evolution.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import tech.ikora.evolution.results.CsvRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {
    private final FileWriter out;
    private CSVPrinter printer;

    public CsvExporter(String output) throws IOException {
        this.out = new FileWriter(output);
    }

    private void initialize(String[] headers) throws IOException {
        this.printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
    }

    public void addRecord(CsvRecord record) throws IOException {
        if(record == null){
            return;
        }

        if(this.printer == null){
            initialize(record.getHeaders());
        }

        if(printer == null){
            return;
        }

        try {
            this.printer.printRecord(record.getValues());
            this.printer.flush();
        } catch (IOException e) {
            this.printer.close();
        }
    }

    public void addRecords(List<? extends CsvRecord> records) throws IOException {
        if(records == null){
            return;
        }

        if(this.printer == null && !records.isEmpty()){
            initialize(records.get(0).getHeaders());
        }

        if(printer == null){
            return;
        }

        try {
            for(CsvRecord record: records){
                this.printer.printRecord(record.getValues());
            }

            this.printer.flush();
        } catch (IOException e) {
            this.printer.close();
        }
    }

    @Override
    public void finalize() throws IOException {
        this.printer.flush();
        this.printer.close();
    }
}
