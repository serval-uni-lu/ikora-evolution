package lu.uni.serval.ikora.evolution.export;

import java.io.IOException;

public class ExporterFactory {
    public static Exporter create(Exporter.Strategy strategy, String absolutePath) throws IOException {
        switch (strategy){
            case IN_MEMORY: return new InMemoryExporter(absolutePath);
            case CSV: return new CsvExporter(absolutePath);
        }

        return null;
    }
}
