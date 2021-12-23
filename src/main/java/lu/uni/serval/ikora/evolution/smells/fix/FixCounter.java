package lu.uni.serval.ikora.evolution.smells.fix;

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

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.utils.Cfg;
import lu.uni.serval.ikora.evolution.utils.VersionUtils;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class FixCounter {
    private static final Set<SmellMetric.Type> noAccumulation = new HashSet<>();

    static {
        noAccumulation.add(SmellMetric.Type.MISSING_ASSERTION);
    }

    private FixCounter() {}

    public static long count(TestCase testCase,
                             SmellMetric.Type type,
                             Set<Edit> edits,
                             Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs,
                             Set<SourceNode> previousNodes,
                             SmellConfiguration configuration){
        final Set<SourceNode> testNodes = VersionUtils.findOther(pairs, testCase)
                .map(t -> getTestNodes((TestCase) t, previousNodes))
                .orElse(Collections.emptySet());

        if(testNodes.isEmpty()){
            return 0;
        }

        if(noAccumulation.contains(type)){
            return edits.stream().anyMatch(e -> isFix(type, testNodes, e, configuration)) ? 1 : 0;
        }

        return edits.stream().filter(e -> isFix(type, testNodes, e, configuration)).count();
    }

    private static Set<SourceNode> getTestNodes(TestCase oldTestCase, Set<SourceNode> previousNodes) {
        return previousNodes.stream()
                .filter(n -> oldTestCase == n || Cfg.isCalledBy(n, oldTestCase))
                .collect(Collectors.toSet());
    }

    private static boolean isFix(SmellMetric.Type type,
                                 Set<SourceNode> nodes,
                                 Edit edit,
                                 SmellConfiguration configuration){
        switch (type){
            case ARMY_OF_CLONES: return new FixArmyOfClones(configuration).isFix(nodes, edit);
            case CONDITIONAL_ASSERTION: return new FixConditionalAssertion(configuration).isFix(nodes, edit);
            case EAGER_TEST: return new FixEagerTest(configuration).isFix(nodes, edit);
            case HARDCODED_ENVIRONMENT_CONFIGURATIONS: return new FixHardcodedEnvironmentConfigurations(configuration).isFix(nodes, edit);
            case HARD_CODED_VALUES: return new FixHardCodedValues(configuration).isFix(nodes, edit);
            case HIDING_TEST_DATA: return new FixHidingTestData(configuration).isFix(nodes, edit);
            case LACK_OF_ENCAPSULATION: return new FixLackOfEncapsulation(configuration).isFix(nodes, edit);
            case LONG_TEST_STEPS: return new FixLongTestSteps(configuration).isFix(nodes, edit);
            case MIDDLE_MAN: return new FixMiddleMan(configuration).isFix(nodes, edit);
            case MISSING_ASSERTION: return new FixMissingAssertion(configuration).isFix(nodes, edit);
            case MISSING_DOCUMENTATION: return new FixMissingDocumentation(configuration).isFix(nodes, edit);
            case NARCISSISTIC: return new FixNarcissistic(configuration).isFix(nodes, edit);
            case NOISY_LOGGING: return new FixNoisyLogging(configuration).isFix(nodes, edit);
            case ON_THE_FLY: return new FixOnTheFly(configuration).isFix(nodes, edit);
            case OVER_CHECKING: return new FixOverChecking(configuration).isFix(nodes, edit);
            case SENSITIVE_LOCATOR: return new FixSensitiveLocator(configuration).isFix(nodes, edit);
            case SNEAKY_CHECKING: return new FixSneakyChecking(configuration).isFix(nodes, edit);
            case STINKY_SYNCHRONIZATION_SYNDROME: return new FixStinkySynchronizationSyndrome(configuration).isFix(nodes, edit);

            default: throw new IllegalArgumentException(String.format("Computing fix for %s is not supported", type.name()));
        }
    }
}
