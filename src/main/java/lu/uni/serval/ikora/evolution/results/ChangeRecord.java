package lu.uni.serval.ikora.evolution.results;

public interface ChangeRecord {
    String[] getKeys();
    Object[] getValues(boolean isHashNames);
}
