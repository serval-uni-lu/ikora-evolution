package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.analytics.KeywordStatistics;
import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.model.*;
import lu.uni.serval.ikora.core.utils.Ast;
import lu.uni.serval.ikora.core.utils.Cfg;
import lu.uni.serval.ikora.evolution.utils.VersionUtils;
import lu.uni.serval.ikora.smells.NodeUtils;
import lu.uni.serval.ikora.smells.SmellConfiguration;
import lu.uni.serval.ikora.smells.SmellMetric;
import lu.uni.serval.ikora.smells.utils.LocatorUtils;
import lu.uni.serval.ikora.smells.utils.NLPUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class FixCounter {
    private final static Set<SmellMetric.Type> noAccumulation = new HashSet<>();

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
            case HARDCODED_ENVIRONMENT_CONFIGURATIONS:
            case HARD_CODED_VALUES: return isFix(nodes, edit, Edit.Type.CHANGE_VALUE_TYPE);

            case TEST_CLONES: return isFix(nodes, edit, Edit.Type.REMOVE_USER_KEYWORD);

            case CONDITIONAL_TEST_LOGIC:
            case STINKY_SYNCHRONIZATION_SYNDROME:
            case LACK_OF_ENCAPSULATION: return isFix(nodes, edit, Edit.Type.REMOVE_STEP, Edit.Type.CHANGE_STEP);

            case LOGGING_IN_FIXTURE_CODE:
            case EAGER_TEST:
            case OVER_CHECKING:
            case HIDING_TEST_DATA_IN_FIXTURE_CODE: return isFix(nodes, edit, Edit.Type.REMOVE_STEP);

            case SNEAKY_CHECKING: return isFix(nodes, edit, Edit.Type.REMOVE_NODE);

            case MISSING_DOCUMENTATION: return isFix(nodes, edit, Edit.Type.ADD_DOCUMENTATION);

            case MIDDLE_MAN: return isFixMiddleMan(nodes, edit);
            case LONG_TEST_STEPS: return isFixLongTestSteps(nodes, edit, configuration);
            case CALCULATE_EXPECTED_RESULTS_ON_THE_FLY: return isFixCalculateExpectedResultsOnTheFly(nodes, edit);
            case COMPLICATED_SETUP_SCENARIOS: return isFixComplicatedSetup(nodes, edit);
            case COMPLEX_LOCATORS: return isFixComplexLocator(nodes, edit, configuration);
            case USING_PERSONAL_PRONOUN: return isFixUsingPersonalPronoun(nodes, edit);
            case MISSING_ASSERTION: return isFixMissingAssertionCheck(edit);
            case CONDITIONAL_ASSERTION: return isFixConditionalAssertion(nodes, edit);
        }

        throw new IllegalArgumentException(String.format("Computing fix for %s is not supported", type.name()));
    }

    private static boolean isFix(Set<SourceNode> nodes, Edit edit, Edit.Type... types){
        if(Arrays.stream(types).noneMatch(t -> edit.getType() == t)){
            return false;
        }

        return nodes.contains(edit.getLeft());
    }

    public static boolean isFixLongTestSteps(Set<SourceNode> nodes, Edit edit, SmellConfiguration configuration) {
        final KeywordDefinition previousStep = getPreviousStep(edit, nodes);

        if(previousStep == null){
            return false;
        }

        return KeywordStatistics.getSequenceSize(previousStep) < configuration.getMaximumStepSize();
    }

    private static KeywordDefinition getPreviousStep(Edit edit, Set<SourceNode> nodes){
        if(edit.getType() != Edit.Type.REMOVE_STEP){
            return null;
        }

        if(!Step.class.isAssignableFrom(edit.getLeft().getClass())){
            return null;
        }

        return getRelevantStep((Step) edit.getLeft(), nodes);
    }

    private static KeywordDefinition getRelevantStep(Step step, Set<SourceNode> nodes){
        for(SourceNode node: nodes){
            final Optional<KeywordDefinition> parent = Cfg.getCallerByName(step, node.getNameToken());

            if(parent.isPresent() ){
                return parent.get();
            }
        }

        return null;
    }

    private static boolean isFixMiddleMan(Set<SourceNode> nodes, Edit edit) {
        if(isFix(nodes, edit, Edit.Type.REMOVE_USER_KEYWORD)){
            return true;
        }

        if(edit.getType() == Edit.Type.CHANGE_STEP){
            final Optional<UserKeyword> parent = Ast.getParentByType(edit.getLeft(), UserKeyword.class);

            if(!parent.isPresent() || !nodes.contains(parent.get())){
                return false;
            }

            if(!Step.class.isAssignableFrom(edit.getRight().getClass())){
                return false;
            }

            return ((Step)edit.getRight()).getKeywordCall()
                    .filter(call -> call.getKeywordType() != Keyword.Type.USER).isPresent();
        }

        return false;
    }

    private static boolean isFixCalculateExpectedResultsOnTheFly(Set<SourceNode> nodes, Edit edit) {
        if(edit.getLeft() == null){
            return false;
        }

        if(Variable.class.isAssignableFrom(edit.getLeft().getClass())){
            return isFix(nodes, edit, Edit.Type.CHANGE_VALUE_TYPE);
        }

        return false;
    }

    private static boolean isFixComplicatedSetup(Set<SourceNode> nodes, Edit edit) {
        if(nodes.contains(edit.getLeft())){
            int oldSize = KeywordStatistics.getSequenceSize(edit.getLeft());
            int newSize = KeywordStatistics.getSequenceSize(edit.getRight());

            return oldSize > newSize;
        }

        return false;
    }

    private static boolean isFixComplexLocator(Set<SourceNode> nodes, Edit edit, SmellConfiguration configuration) {
        return nodes.contains(edit.getLeft())
                && Literal.class.isAssignableFrom(edit.getRight().getClass())
                && !LocatorUtils.isComplex(edit.getRight().getName(), configuration.getMaximumLocatorSize());
    }

    private static boolean isFixUsingPersonalPronoun(Set<SourceNode> nodes, Edit edit) {
        if(edit.getType() != Edit.Type.CHANGE_NAME){
            return false;
        }

        return nodes.contains(edit.getLeft()) && !NLPUtils.isUsingPersonalPronoun((Step) edit.getRight());
    }

    private static boolean isFixMissingAssertionCheck(Edit edit) {
        if(edit.getRight() == null){
            return false;
        }

        if(edit.getType() == Edit.Type.ADD_STEP && isAddAssertion((Step)edit.getRight())){
            return true;
        }

        return edit.getType() == Edit.Type.ADD_USER_KEYWORD && isAddAssertion((UserKeyword) edit.getRight());
    }

    private static boolean isAddAssertion(Step step){
        return step.getKeywordCall()
                .flatMap(Step::getKeywordCall)
                .map(KeywordCall::getKeywordType)
                .map(t -> t == Keyword.Type.ASSERTION)
                .orElse(false);
    }

    private static boolean isAddAssertion(UserKeyword keyword){
        return keyword.getSteps().stream()
                .anyMatch(FixCounter::isAddAssertion);
    }

    private static boolean isFixConditionalAssertion(Set<SourceNode> nodes, Edit edit){
        return nodes.contains(edit.getLeft()) && NodeUtils.isCallType(edit.getRight(), Keyword.Type.ASSERTION, true);
    }
}
