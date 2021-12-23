package lu.uni.serval.ikora.evolution.export;

import lu.uni.serval.ikora.evolution.results.BaseRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryExporter implements Exporter {
    private final String absolutePath;
    private final boolean isHashNames;
    private final List<BaseRecord> baseRecords = new ArrayList<>();

    public InMemoryExporter(String absolutePath, boolean isHashNames) {
        this.isHashNames = isHashNames;
        this.absolutePath = absolutePath;
    }

    @Override
    public void addRecord(BaseRecord baseRecord) throws IOException {
        this.baseRecords.add(baseRecord);
    }

    @Override
    public void addRecords(List<BaseRecord> baseRecords) throws IOException {
        this.baseRecords.addAll(baseRecords);
    }

    @Override
    public void close() throws IOException {
        //nothing to do
    }

    public List<BaseRecord> getRecords() {
        return baseRecords;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isHashNames() {
        return isHashNames;
    }
}
