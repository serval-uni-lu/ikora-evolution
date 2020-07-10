package tech.ikora.evolution.export;

import tech.ikora.evolution.results.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryExporter implements Exporter {
    private final String absolutePath;
    private final List<Record> records = new ArrayList<>();

    public InMemoryExporter(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    @Override
    public void addRecord(Record record) throws IOException {
        this.records.add(record);
    }

    @Override
    public void addRecords(List<Record> records) throws IOException {
        this.records.addAll(records);
    }

    @Override
    public void finalize() throws IOException {
        //nothing to do;
    }

    public List<Record> getRecords() {
        return records;
    }
}
