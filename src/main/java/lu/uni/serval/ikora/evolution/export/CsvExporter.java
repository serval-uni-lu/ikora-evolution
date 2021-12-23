package lu.uni.serval.ikora.evolution.export;

import lu.uni.serval.ikora.evolution.results.BaseRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
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
            logger.log(Level.INFO, "Create folder {}", folder.getAbsolutePath());
        }

        this.out = new FileWriter(output);
        this.isHashNames = isHashNames;
    }

    private void initialize(String[] headers) throws IOException {
        final CSVFormat csv = CSVFormat.Builder.create().setHeader(headers).build();
        this.printer = new CSVPrinter(out, csv);
    }

    public void addRecord(BaseRecord baseRecord) throws IOException {
        if(baseRecord == null){
            return;
        }

        if(this.printer == null){
            initialize(baseRecord.getKeys());
        }

        if(printer == null){
            return;
        }

        try {
            this.printer.printRecord(baseRecord.getValues(this.isHashNames));
            this.printer.flush();
        } catch (IOException e) {
            this.printer.close();
        }
    }

    public void addRecords(List<BaseRecord> records) throws IOException {
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
            for(BaseRecord baseRecord : records){
                this.printer.printRecord(baseRecord.getValues(this.isHashNames));
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
