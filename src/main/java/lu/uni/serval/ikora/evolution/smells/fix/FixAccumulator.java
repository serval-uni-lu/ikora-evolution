package lu.uni.serval.ikora.evolution.smells.fix;

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

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.utils.Cfg;
import lu.uni.serval.ikora.evolution.smells.History;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;

import java.util.*;
import java.util.stream.Collectors;

import static lu.uni.serval.ikora.smells.SmellMetric.Type.*;

public class FixAccumulator {
    private final History history;
    private final EnumMap<SmellMetric.Type, FixDetection> fixDetectionMap;

    public FixAccumulator(SmellConfiguration configuration, History history) {
        this.history = history;
        this.fixDetectionMap = new EnumMap<>(SmellMetric.Type.class);
        initialize(configuration);
    }

    private void initialize(SmellConfiguration configuration) {
        this.fixDetectionMap.put(ARMY_OF_CLONES, new FixArmyOfClones(configuration, history));
        this.fixDetectionMap.put(CONDITIONAL_ASSERTION, new FixConditionalAssertion(configuration, history));
        this.fixDetectionMap.put(HARDCODED_ENVIRONMENT_CONFIGURATIONS, new FixHardcodedEnvironment(configuration, history));
        this.fixDetectionMap.put(HARD_CODED_VALUES, new FixHardCodedValues(configuration, history));
        this.fixDetectionMap.put(HIDING_TEST_DATA, new FixHiddenTestData(configuration, history));
        this.fixDetectionMap.put(LACK_OF_ENCAPSULATION, new FixLackOfEncapsulation(configuration, history));
        this.fixDetectionMap.put(LONG_TEST_STEPS, new FixLongTestSteps(configuration, history));
        this.fixDetectionMap.put(MIDDLE_MAN, new FixMiddleMan(configuration, history));
        this.fixDetectionMap.put(MISSING_ASSERTION, new FixMissingAssertion(configuration, history));
        this.fixDetectionMap.put(MISSING_DOCUMENTATION, new FixMissingDocumentation(configuration, history));
        this.fixDetectionMap.put(NARCISSISTIC, new FixNarcissistic(configuration, history));
        this.fixDetectionMap.put(NOISY_LOGGING, new FixNoisyLogging(configuration, history));
        this.fixDetectionMap.put(ON_THE_FLY, new FixOnTheFly(configuration, history));
        this.fixDetectionMap.put(OVER_CHECKING, new FixOverChecking(configuration, history));
        this.fixDetectionMap.put(SENSITIVE_LOCATOR, new FixSensitiveLocator(configuration, history));
        this.fixDetectionMap.put(SNEAKY_CHECKING, new FixSneakyChecking(configuration, history));
        this.fixDetectionMap.put(STINKY_SYNCHRONIZATION_SYNDROME, new FixStinkySynchronizationSyndrome(configuration, history));
    }

    public Set<FixResult> collect(Projects version, TestCase testCase, SmellMetric.Type type){
        if(!this.history.hasPreviousVersion()){
            return Collections.emptySet();
        }

        final Optional<SourceNode> previousTestCase = history.findPreviousNode(version, testCase);

        if(previousTestCase.isEmpty()){
            return Collections.emptySet();
        }

        if(!TestCase.class.isAssignableFrom(previousTestCase.get().getClass())){
            return Collections.emptySet();
        }

        return history.getEdits(version).stream()
                .filter(e -> checkTestCase(e, (TestCase) previousTestCase.get(), testCase))
                .map(e -> getFix(version, testCase, type, e))
                .filter(FixResult::isValid)
                .collect(Collectors.toSet());
    }

    private static boolean checkTestCase(Edit edit, TestCase previousTestCase, TestCase testCase){
        return edit.getLeft() != null
                ? checkTestCase(edit.getLeft(), previousTestCase)
                : checkTestCase(edit.getRight(), testCase);
    }

    private static boolean checkTestCase(SourceNode node, TestCase testCase){
        return node == testCase || Cfg.isCalledBy(node, testCase);
    }

    private FixResult getFix(Projects version, TestCase testCase, SmellMetric.Type type, Edit edit){
        final FixDetection fixDetection = this.fixDetectionMap.get(type);

        if(fixDetection == null){
            return FixResult.noFix();
        }

        return fixDetection.getFix(version, testCase, edit);
    }
}
