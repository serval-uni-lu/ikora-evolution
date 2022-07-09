package lu.uni.serval.ikora.evolution.smells;

import lu.uni.serval.ikora.core.analytics.difference.Edit;
import lu.uni.serval.ikora.core.analytics.difference.NodeMatcher;
import lu.uni.serval.ikora.core.analytics.difference.VersionPairs;
import lu.uni.serval.ikora.core.model.Projects;
import lu.uni.serval.ikora.core.model.SourceNode;
import lu.uni.serval.ikora.smells.SmellMetric;
import lu.uni.serval.ikora.smells.SmellResult;
import lu.uni.serval.ikora.smells.SmellResults;

import java.util.*;

public class History {
    Map<Projects, Map<SmellMetric.Type, Set<SourceNode>>> smellyNodes = new HashMap<>();
    private final List<VersionPairs> versionPairs = new ArrayList<>();
    private boolean ignoreProjectName = false;
    private Projects lastVersion = null;

    public void setIgnoreProjectName(boolean ignoreProjectName) {
        this.ignoreProjectName = ignoreProjectName;
    }

    public void addVersion(Projects version){
        smellyNodes.put(version, new EnumMap<>(SmellMetric.Type.class));

        if(lastVersion != null){
            VersionPairs pairs = NodeMatcher.computeVersionsPairs(lastVersion, version, this.ignoreProjectName);
            versionPairs.add(pairs);
        }

        lastVersion = version;
    }

    public boolean hasPreviousVersion(){
        return !versionPairs.isEmpty();
    }

    public Set<Edit> getEdits(Projects version){
        return getChanges(version)
                .map(VersionPairs::getEdits)
                .orElse(Collections.emptySet());
    }

    public void addSmells(Projects version, SmellResults smellResults) {
        var nodes = smellyNodes.get(version);

        for(SmellResult smell: smellResults){
            final Set<SourceNode> nodesByType = nodes.getOrDefault(smell.getType(), new HashSet<>());
            nodesByType.addAll(smell.getNodes());
            nodes.putIfAbsent(smell.getType(), nodesByType);
        }
    }

    public Set<SourceNode> getPreviousSmellyNodes(Projects version, SmellMetric.Type smellType) {
        final Optional<Projects> previousVersion = findPreviousVersion(version);

        if(previousVersion.isEmpty()){
            return Collections.emptySet();
        }

        return smellyNodes
                .getOrDefault(previousVersion.get(), Collections.emptyMap())
                .getOrDefault(smellType, Collections.emptySet());
    }

    public List<SourceNode> getSequence(Projects version, Edit edit) {
        Optional<Projects> previousVersion = findPreviousVersion(version);

        if(previousVersion.isEmpty()){
            return Collections.emptyList();
        }

        return getSequence(previousVersion.get(), edit.getLeft());
    }

    public Optional<SourceNode> findPreviousNode(Projects version, SourceNode node){
        if(node == null){
            return Optional.empty();
        }

        final Optional<VersionPairs> pair = findPreviousPair(version);

        if(pair.isEmpty()){
            return Optional.empty();
        }

        return pair.get().findPrevious(node);
    }

    public List<SourceNode> getSequence(Projects version, SourceNode node){
        if(node == null){
            return Collections.emptyList();
        }

        final List<SourceNode> history = new LinkedList<>();
        history.add(node);

        Optional<VersionPairs> previousPair = findPreviousPair(version);

        while (previousPair.isPresent()){
            Optional<SourceNode> previousNode = previousPair.get().findPrevious(node);
            if(previousNode.isEmpty()){
                break;
            }

            node = previousNode.get();
            history.add(node);

            previousPair = findPreviousPair(previousPair.get().getLeftVersion());
        }

        return history;
    }

    private Optional<VersionPairs> getChanges(Projects version){
        return versionPairs.stream().filter(v -> v.getRightVersion() == version).findAny();
    }

    private Optional<Projects> findPreviousVersion(Projects version){
        return findPreviousPair(version).map(VersionPairs::getLeftVersion);
    }

    private Optional<VersionPairs> findPreviousPair(Projects version){
        return versionPairs.stream()
                .filter(v -> v.getRightVersion() == version)
                .findAny();
    }
}
