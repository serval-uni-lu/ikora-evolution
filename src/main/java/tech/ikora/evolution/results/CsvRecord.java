package tech.ikora.evolution.results;

public interface CsvRecord {
    String[] getHeaders();
    Object[] getValues();
}
