package lu.uni.serval.ikora.evolution.export;

import lu.uni.serval.ikora.evolution.results.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvolutionExport {
    private static final Logger logger = LogManager.getLogger(EvolutionExport.class);

    public enum Statistics{
        PROJECT,
        SMELL,
        VARIABLE_CHANGES
    }

    private final Map<Statistics, Exporter> exporterMap;
    private final Exporter.Strategy strategy;

    public EvolutionExport(Exporter.Strategy strategy, Map<Statistics, File> outputFiles){
        exporterMap = new HashMap<>();
        this.strategy = strategy;

        for(Map.Entry<Statistics, File> outputFile: outputFiles.entrySet()){
            initializeExporter(outputFile.getKey(), outputFile.getValue());
        }
    }

    public Map<Statistics, Exporter> getExporters() {
        return exporterMap;
    }

    public boolean contains(Statistics statistics) {
        return exporterMap.containsKey(statistics);
    }

    private void initializeExporter(Statistics statistic, File location){
        if(location != null){
            try {
                final Exporter exporter = ExporterFactory.create(this.strategy, location.getAbsolutePath());
                this.exporterMap.put(statistic, exporter);
            } catch (IOException e) {
                logger.error(String.format("Failed to create csv writer for %s at location '%s'",
                        statistic.name(),
                        location.getAbsolutePath()));
            }
        }
    }

    public void export(Statistics statistics, List<Record> records) throws IOException {
        final Exporter exporter = exporterMap.get(statistics);

        if(exporter != null){
            exporter.addRecords(records);
        }
    }

    public void export(Statistics statistics, Record record) throws IOException {
        final Exporter exporter = exporterMap.get(statistics);

        if(exporter != null){
            exporter.addRecord(record);
        }
    }

    public void close() throws IOException {
        for(Exporter exporter: exporterMap.values()){
            if(exporter != null){
                exporter.close();
            }
        }
    }
}
