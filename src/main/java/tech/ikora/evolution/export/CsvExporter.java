package tech.ikora.evolution.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.evolution.results.Record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExporter implements Exporter {
    private static final Logger logger = LogManager.getLogger(CsvExporter.class);

    private final FileWriter out;
    private CSVPrinter printer;

    public CsvExporter(String output) throws IOException {
        final File folder = new File(FilenameUtils.getFullPathNoEndSeparator(output));
        if(folder.mkdirs()){
            logger.info(String.format("Create folder %s", folder.getAbsolutePath()));
        }

        this.out = new FileWriter(output);
    }

    private void initialize(String[] headers) throws IOException {
        this.printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
    }

    public void addRecord(Record record) throws IOException {
        if(record == null){
            return;
        }

        if(this.printer == null){
            initialize(record.getKeys());
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

    public void addRecords(List<Record> records) throws IOException {
        if(records == null){
            return;
        }

        if(this.printer == null && !records.isEmpty()){
            initialize(records.get(0).getKeys());
        }

        if(printer == null){
            return;
        }

        try {
            for(Record record: records){
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
