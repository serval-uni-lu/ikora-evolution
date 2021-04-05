package lu.uni.serval.ikora.evolution.utils;

import lu.uni.serval.ikora.core.model.SourceNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.Set;

public class VersionUtils {
    private VersionUtils() {}

    public static Optional<SourceNode> findOther(Set<Pair<? extends SourceNode, ? extends SourceNode>> pairs, SourceNode node){
        for(Pair<? extends SourceNode, ? extends SourceNode> pair: pairs){
            if(pair.getLeft() == node){
                return Optional.ofNullable(pair.getRight());
            }

            if(pair.getRight() == node) {
                return Optional.ofNullable(pair.getLeft());
            }
        }

        return Optional.empty();
    }
}
