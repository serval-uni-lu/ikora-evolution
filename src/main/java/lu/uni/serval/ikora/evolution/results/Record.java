package lu.uni.serval.ikora.evolution.results;

import javax.xml.bind.DatatypeConverter;

public interface Record {
    String[] getKeys();
    Object[] getValues();
}
