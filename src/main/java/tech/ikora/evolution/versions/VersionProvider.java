package tech.ikora.evolution.versions;

import tech.ikora.model.Projects;

import java.io.File;
import java.io.IOException;

public interface VersionProvider extends Iterable<Projects> {
    File getRootFolder() throws IOException;
    void clean() throws IOException;
}
