package lu.uni.serval.ikora.evolution.export;

/*-
 * #%L
 * Ikora Evolution
 * %%
 * Copyright (C) 2020 - 2021 University of Luxembourg
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

import lu.uni.serval.ikora.evolution.results.BaseRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryExporter implements Exporter {
    private final String absolutePath;
    private final boolean isHashNames;
    private final List<BaseRecord> baseRecords = new ArrayList<>();

    public InMemoryExporter(String absolutePath, boolean isHashNames) {
        this.isHashNames = isHashNames;
        this.absolutePath = absolutePath;
    }

    @Override
    public void addRecord(BaseRecord baseRecord) throws IOException {
        this.baseRecords.add(baseRecord);
    }

    @Override
    public void addRecords(List<BaseRecord> baseRecords) throws IOException {
        this.baseRecords.addAll(baseRecords);
    }

    @Override
    public void close() throws IOException {
        //nothing to do
    }

    public List<BaseRecord> getRecords() {
        return baseRecords;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public boolean isHashNames() {
        return isHashNames;
    }
}
