package lu.uni.serval.ikora.evolution.smells;

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

import lu.uni.serval.ikora.core.model.*;

import lu.uni.serval.ikora.evolution.smells.fix.FixAccumulator;
import lu.uni.serval.ikora.evolution.smells.fix.FixResult;
import lu.uni.serval.ikora.smells.*;

import lu.uni.serval.ikora.evolution.results.BaseRecord;
import lu.uni.serval.ikora.evolution.results.SmellRecord;

import java.util.*;

public class SmellRecordAccumulator {
    private final List<BaseRecord> records = new ArrayList<>();

    public void addTestCase(Projects version,
                            TestCase testCase,
                            SmellResults smells,
                            SmellConfiguration configuration,
                            History history){


        for(SmellResult smell: smells){
            Set<FixResult> fixes = Collections.emptySet();

            if(history.hasPreviousVersion()){
                fixes = FixAccumulator.collect(version, testCase, smell.getType(), configuration, history);
            }

            records.add(new SmellRecord(version.getVersionId(), testCase, smell.getType().name(), smell.getRawValue(), smell.getNormalizedValue(), fixes));
        }
    }

    public List<BaseRecord> getRecords() {
        return records;
    }
}
