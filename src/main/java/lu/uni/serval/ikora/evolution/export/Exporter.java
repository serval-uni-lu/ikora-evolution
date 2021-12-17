package lu.uni.serval.ikora.evolution.export;

import lu.uni.serval.ikora.evolution.results.ChangeRecord;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface Exporter extends Closeable {
    enum Strategy{
        IN_MEMORY,
        CSV
    }

    void addRecord(ChangeRecord record) throws IOException;
    void addRecords(List<ChangeRecord> records) throws IOException;
}
