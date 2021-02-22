package lu.uni.serval.ikora.evolution.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

public class FolderConfiguration {
    public enum NameFormat{
        VERSION,
        DATE
    }

    @JsonProperty(value = "root folder", required = true)
    File rootFolder;
    @JsonProperty(value = "name format", required = true)
    NameFormat nameFormat;
    @JsonProperty(value = "date format")
    String dateFormat;

    public File getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    public NameFormat getNameFormat() {
        return nameFormat;
    }

    public void setNameFormat(NameFormat nameFormat) {
        this.nameFormat = nameFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
