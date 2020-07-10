package tech.ikora.evolution.differences;

import org.apache.commons.lang3.NotImplementedException;
import tech.ikora.analytics.Action;
import tech.ikora.analytics.Difference;
import tech.ikora.model.SourceNode;
import tech.ikora.model.Variable;
import tech.ikora.smells.SmellMetric;

import java.util.Set;
import java.util.stream.Collectors;

public class SmellFixes {
    public static long count(SmellMetric.Type type, Set<SourceNode> previousNodes, Set<Difference> changes) {
        return changes.stream().filter(c -> isFix(c, type, previousNodes)).count();
    }

    private static boolean isFix(Difference change, SmellMetric.Type type, Set<SourceNode> previousNodes){
        switch (type){
            case HARD_CODED_VALUES: return isHardCodedValuesFix(change, previousNodes);
            case CONDITIONAL_TEST_LOGIC: return isConditionalTestLogicFix(change, previousNodes);
            case LONG_TEST_STEPS: return isLongTestStepFix(change, previousNodes);
            case TEST_CLONES: return isTestClonesFix(change, previousNodes);
            case MIDDLE_MAN: return isMiddleManFix(change, previousNodes);
            case LACK_OF_ENCAPSULATION: return isLackOfEncapsulationFix(change, previousNodes);
            case LOGGING_IN_FIXTURE_CODE: return isLoggingInFixtureCodeFix(change, previousNodes);
            case HIDING_TEST_DATA_IN_FIXTURE_CODE: return isHidingTestDataInFixtureCodeFix(change, previousNodes);
            case STINKY_SYNCHRONIZATION_SYNDROME: return isStinkySynchronizationFix(change, previousNodes);
            case CALCULATE_EXPECTED_RESULTS_ON_THE_FLY: return isResultOnTheFlyFix(change, previousNodes);
            case COMPLICATED_SETUP_SCENARIOS: return isComplicatedSetupScenariosFix(change, previousNodes);
            case COMPLEX_LOCATORS: return isComplexLocatorsFix(change, previousNodes);
            case EAGER_TEST: return isEagerTestFix(change, previousNodes);
            case USING_PERSONAL_PRONOUN: return isPersonalPronounFix(change, previousNodes);
            case MISSING_ASSERTION: return isMissingAssertionFix(change, previousNodes);
            case HARDCODED_ENVIRONMENT_CONFIGURATIONS: return isHardCodedEnvironmentConfigurationFix(change, previousNodes);
            case CONDITIONAL_ASSERTION: return isConditionalAssertionFix(change, previousNodes);
            case OVER_CHECKING: return isOverCheckingFix(change, previousNodes);
            case SNEAKY_CHECKING: return isSneakyCheckingFix(change, previousNodes);
            case LACK_OF_DOCUMENTATION: return isLackOfDocumentationFix(change, previousNodes);
            default: throw new NotImplementedException(String.format("Cannot compute fixes for %s", type.name()));
        }
    }

    private static boolean isHardCodedValuesFix(Difference change, Set<SourceNode> nodes) {
        for(Action action: getActionsByType(change, Action.Type.CHANGE_STEP_ARGUMENT)){
            if(nodes.contains(action.getLeft()) && !Variable.class.isAssignableFrom(action.getRight().getClass())){
                return true;
            }
        }

        return false;
    }

    private static boolean isConditionalTestLogicFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isLongTestStepFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isTestClonesFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isMiddleManFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isLackOfEncapsulationFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isLoggingInFixtureCodeFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isHidingTestDataInFixtureCodeFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isStinkySynchronizationFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isResultOnTheFlyFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isComplicatedSetupScenariosFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isComplexLocatorsFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isEagerTestFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isPersonalPronounFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isMissingAssertionFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isHardCodedEnvironmentConfigurationFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isConditionalAssertionFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isOverCheckingFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isSneakyCheckingFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static boolean isLackOfDocumentationFix(Difference change, Set<SourceNode> nodes) {
        return false;
    }

    private static Set<Action> getActionsByType(Difference change, Action.Type type){
        return change.getActions().stream()
                .filter(a -> a.getType() == type)
                .collect(Collectors.toSet());
    }
}
