package tech.ikora.evolution.differences;

import org.apache.commons.lang3.NotImplementedException;
import tech.ikora.analytics.Difference;
import tech.ikora.model.SourceNode;
import tech.ikora.smells.SmellResult;

import java.util.Set;

public class SmellFixes {
    public static long count(SmellResult smellResult, Set<Difference> changes) {
        return changes.stream().filter(c -> isFix(c, smellResult)).count();
    }

    private static boolean isFix(Difference change, SmellResult smellResult){
        switch (smellResult.getType()){
            case HARD_CODED_VALUES: return isHardCodedValuesFix(change, smellResult.getNodes());
            case CONDITIONAL_TEST_LOGIC: return isConditionalTestLogicFix(change, smellResult.getNodes());
            case LONG_TEST_STEPS: return isLongTestStepFix(change, smellResult.getNodes());
            case TEST_CLONES: return isTestClonesFix(change, smellResult.getNodes());
            case MIDDLE_MAN: return isMiddleManFix(change, smellResult.getNodes());
            case LACK_OF_ENCAPSULATION: return isLackOfEncapsulationFix(change, smellResult.getNodes());
            case LOGGING_IN_FIXTURE_CODE: return isLoggingInFixtureCodeFix(change, smellResult.getNodes());
            case HIDING_TEST_DATA_IN_FIXTURE_CODE: return isHidingTestDataInFixtureCodeFix(change, smellResult.getNodes());
            case STINKY_SYNCHRONIZATION_SYNDROME: return isStinkySynchronizationFix(change, smellResult.getNodes());
            case CALCULATE_EXPECTED_RESULTS_ON_THE_FLY: return isResultOnTheFlyFix(change, smellResult.getNodes());
            case COMPLICATED_SETUP_SCENARIOS: return isComplicatedSetupScenariosFix(change, smellResult.getNodes());
            case COMPLEX_LOCATORS: return isComplexLocatorsFix(change, smellResult.getNodes());
            case EAGER_TEST: return isEagerTestFix(change, smellResult.getNodes());
            case USING_PERSONAL_PRONOUN: return isPersonalPronounFix(change, smellResult.getNodes());
            case MISSING_ASSERTION: return isMissingAssertionFix(change, smellResult.getNodes());
            case HARDCODED_ENVIRONMENT_CONFIGURATIONS: return isHardCodedEnvironmentConfigurationFix(change, smellResult.getNodes());
            case CONDITIONAL_ASSERTION: return isConditionalAssertionFix(change, smellResult.getNodes());
            case OVER_CHECKING: return isOverCheckingFix(change, smellResult.getNodes());
            case SNEAKY_CHECKING: return isSneakyCheckingFix(change, smellResult.getNodes());
            case LACK_OF_DOCUMENTATION: return isLackOfDocumentationFix(change, smellResult.getNodes());
            default: throw new NotImplementedException(String.format("Cannot compute fixes for %s", smellResult.getType().name()));
        }
    }

    private static boolean isHardCodedValuesFix(Difference change, Set<SourceNode> nodes) {
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
}
