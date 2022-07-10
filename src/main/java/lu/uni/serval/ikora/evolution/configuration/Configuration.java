package lu.uni.serval.ikora.evolution.configuration;

import java.io.File;

public abstract class Configuration {
    private File folder = null;

    public File getFolder(){
        return folder;
    }

    public void setFolder(File folder){
        this.folder = folder;
    }
}
