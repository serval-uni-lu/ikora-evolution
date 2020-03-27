package tech.ikora.evolution.export;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.ikora.evolution.results.CsvRecord;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvolutionExport {
    private static Logger logger = LogManager.getLogger(EvolutionExport.class);

    public boolean contains(Statistics statistics) {
        return exporterMap.keySet().contains(statistics);
    }

    public enum Statistics{
        PROJECT,
        SMELL
    }

    private final Map<Statistics, CsvExporter> exporterMap;

    public EvolutionExport(Map<Statistics, File> outputFiles){
        exporterMap = new HashMap<>();

        for(Map.Entry<Statistics, File> outputFile: outputFiles.entrySet()){
            initializeExporter(outputFile.getKey(), outputFile.getValue());
        }
    }

    private void initializeExporter(Statistics statistic, File location){
        if(location != null){
            try {
                CsvExporter smellExporter = new CsvExporter(location.getAbsolutePath());
                this.exporterMap.put(statistic, smellExporter);
            } catch (IOException e) {
                logger.error(String.format("Failed to create csv writer for %s at location '%s'",
                        statistic.name(),
                        location.getAbsolutePath()));
            }
        }
    }

    public void export(Statistics statistics, List<? extends CsvRecord> records) throws IOException {
        final CsvExporter csvExporter = exporterMap.get(statistics);

        if(csvExporter != null){
            csvExporter.addRecords(records);
        }
    }

    public void export(Statistics statistics, CsvRecord record) throws IOException {
        final CsvExporter csvExporter = exporterMap.get(statistics);

        if(csvExporter != null){
            csvExporter.addRecord(record);
        }
    }

    @Override
    public void finalize() throws IOException {
        for(CsvExporter exporter: exporterMap.values()){
            if(exporter != null){
                exporter.finalize();
            }
        }
    }
}
