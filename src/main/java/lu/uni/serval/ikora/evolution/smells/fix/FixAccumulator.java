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

public class FixAccumulator {
    private FixAccumulator() {}

    public static Set<FixResult> collect(Projects version,
                                         TestCase testCase,
                                         SmellMetric.Type type,
                                         SmellConfiguration configuration,
                                         History history){

        final Optional<SourceNode> previousTestCase = history.findPreviousNode(version, testCase);

        if(previousTestCase.isEmpty()){
            return Collections.emptySet();
        }

        if(!TestCase.class.isAssignableFrom(previousTestCase.get().getClass())){
            return Collections.emptySet();
        }

        return history.getEdits(version).stream()
                .filter(e -> checkTestCase(e, (TestCase) previousTestCase.get(), testCase))
                .map(e -> getFix(version, type, e, configuration, history))
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

    private static FixResult getFix(Projects version,
                                    SmellMetric.Type type,
                                    Edit edit,
                                    SmellConfiguration configuration,
                                    History history){
        switch (type){
            case ARMY_OF_CLONES: return new FixArmyOfClones(configuration, history).getFix(version, edit);
            case CONDITIONAL_ASSERTION: return new FixConditionalAssertion(configuration, history).getFix(version, edit);
            case HARDCODED_ENVIRONMENT_CONFIGURATIONS: return new FixHardcodedEnvironment(configuration, history).getFix(version, edit);
            case HARD_CODED_VALUES: return new FixHardCodedValues(configuration, history).getFix(version, edit);
            case HIDING_TEST_DATA: return new FixHiddenTestData(configuration, history).getFix(version, edit);
            case LACK_OF_ENCAPSULATION: return new FixLackOfEncapsulation(configuration, history).getFix(version, edit);
            case LONG_TEST_STEPS: return new FixLongTestSteps(configuration, history).getFix(version, edit);
            case MIDDLE_MAN: return new FixMiddleMan(configuration, history).getFix(version, edit);
            case MISSING_ASSERTION: return new FixMissingAssertion(configuration, history).getFix(version, edit);
            case MISSING_DOCUMENTATION: return new FixMissingDocumentation(configuration, history).getFix(version, edit);
            case NARCISSISTIC: return new FixNarcissistic(configuration, history).getFix(version, edit);
            case NOISY_LOGGING: return new FixNoisyLogging(configuration, history).getFix(version, edit);
            case ON_THE_FLY: return new FixOnTheFly(configuration, history).getFix(version, edit);
            case OVER_CHECKING: return new FixOverChecking(configuration, history).getFix(version, edit);
            case SENSITIVE_LOCATOR: return new FixSensitiveLocator(configuration, history).getFix(version, edit);
            case SNEAKY_CHECKING: return new FixSneakyChecking(configuration, history).getFix(version, edit);
            case STINKY_SYNCHRONIZATION_SYNDROME: return new FixStinkySynchronizationSyndrome(configuration, history).getFix(version, edit);

            default: return FixResult.noFix();
        }
    }
}
