package lu.uni.serval.ikora.evolution.versions;

import lu.uni.serval.ikora.core.model.Projects;

import java.io.File;
import java.io.IOException;

public interface VersionProvider extends Iterable<Projects> {
    File getRootFolder() throws IOException;
    void clean() throws IOException;
}
