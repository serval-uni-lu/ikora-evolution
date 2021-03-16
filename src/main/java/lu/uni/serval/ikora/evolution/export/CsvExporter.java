package lu.uni.serval.ikora.evolution.export;

import lu.uni.serval.ikora.evolution.results.Record;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter implements Exporter {
    private static final Logger logger = LogManager.getLogger(CsvExporter.class);

    private final boolean isHashNames;
    private final FileWriter out;
    private CSVPrinter printer;

    public CsvExporter(String output, boolean isHashNames) throws IOException {
        final File folder = new File(FilenameUtils.getFullPathNoEndSeparator(output));

        if(folder.mkdirs()){
            logger.info(String.format("Create folder %s", folder.getAbsolutePath()));
        }

        this.out = new FileWriter(output);
        this.isHashNames = isHashNames;
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
            this.printer.printRecord(record.getValues(this.isHashNames));
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
                this.printer.printRecord(record.getValues(this.isHashNames));
            }

            this.printer.flush();
        } catch (IOException e) {
            this.printer.close();
            this.printer = null;
        }
    }

    @Override
    public void close() throws IOException {
        if(this.printer != null){
            this.printer.flush();
            this.printer.close();
        }
    }
}
