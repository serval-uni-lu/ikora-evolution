package tech.ikora.evolution.export;

import tech.ikora.evolution.results.Record;

import java.io.IOException;
import java.util.List;

public interface Exporter {
    enum Strategy{
        IN_MEMORY,
        CSV
    }

    void addRecord(Record record) throws IOException;
    void addRecords(List<Record> records) throws IOException;
    void close() throws IOException;
}
