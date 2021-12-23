package lu.uni.serval.ikora.evolution.results;

public interface BaseRecord {
    String[] getKeys();
    Object[] getValues(boolean isHashNames);
}
