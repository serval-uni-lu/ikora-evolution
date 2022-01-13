package lu.uni.serval.ikora.evolution.versions;

/*-
 * #%L
 * Ikora Evolution
 * %%
 * Copyright (C) 2020 - 2022 University of Luxembourg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lu.uni.serval.ikora.evolution.configuration.FolderConfiguration;

import lu.uni.serval.ikora.core.BuildConfiguration;
import lu.uni.serval.ikora.core.builder.BuildResult;
import lu.uni.serval.ikora.core.builder.Builder;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.utils.FileUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FolderProvider implements VersionProvider{
    private final File rootFolder;
    private final FolderConfiguration.NameFormat nameFormat;
    private final String dateFormat;

    public FolderProvider(File rootFolder, FolderConfiguration.NameFormat nameFormat, String dateFormat){
        this.rootFolder = rootFolder;
        this.nameFormat = nameFormat;
        this.dateFormat = dateFormat;
    }

    @Override
    public File getRootFolder() {
        return this.rootFolder;
    }

    @Override
    public void close() {
        //FolderProvider does not manage the folder, nothing to do.
    }

    @Override
    public Iterator<Projects> iterator() {
        return new Iterator<Projects>() {
            private final List<File> subFolders = getSubFolders();
            private final Iterator<File> subFoldersIterator = subFolders.iterator();

            @Override
            public boolean hasNext() {
                return subFoldersIterator.hasNext();
            }

            @Override
            public Projects next() {
                final File subFolder = subFoldersIterator.next();
                final BuildResult build = Builder.build(subFolder, new BuildConfiguration(), true);
                final Projects version = build.getProjects();

                version.setVersionId(subFolder.getName());

                return version;
            }

            List<File> getSubFolders(){
                return FileUtils.getSubFolders(rootFolder).stream()
                        .sorted(this::sortSubFolder)
                        .collect(Collectors.toList());
            }

            private int sortSubFolder(File file1, File file2) {
                final String name1 = file1.getName();
                final String name2 = file2.getName();

                int compare = 0;

                switch (nameFormat){
                    case VERSION:
                        compare = name1.compareToIgnoreCase(name2);
                        break;
                    case DATE:
                        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
                        final LocalDate date1 = LocalDate.from(dateTimeFormatter.parse(name1));
                        final LocalDate date2 = LocalDate.from(dateTimeFormatter.parse(name2));
                        compare = date1.compareTo(date2);
                        break;
                    default: throw new IllegalArgumentException(String.format(
                            "Expected VERSION or DATE to sort folders, but got '%s' instead",
                            nameFormat.name()
                        ));
                }

                return compare;
            }
        };
    }
}
