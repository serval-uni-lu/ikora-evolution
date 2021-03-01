package lu.uni.serval.ikora.evolution.versions;

import lu.uni.serval.ikora.core.model.Projects;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface VersionProvider extends Iterable<Projects>, Closeable {
    File getRootFolder() throws IOException;
}
