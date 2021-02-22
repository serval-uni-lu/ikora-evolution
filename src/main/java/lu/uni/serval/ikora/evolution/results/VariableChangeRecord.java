package lu.uni.serval.ikora.evolution.results;

import lu.uni.serval.ikora.model.Argument;
import lu.uni.serval.ikora.utils.ArgumentUtils;

import java.util.List;

public class VariableChangeRecord implements Record {
    private final String beforeCall;
    private final String beforeName;
    private final String beforeType;
    private final String beforeValues;

    private final String afterCall;
    private final String afterName;
    private final String afterType;
    private final String afterValues;

    public VariableChangeRecord(Argument before, List<String> beforeValues, Argument after, List<String> afterValues) {
        this.beforeCall = before.getAstParent(true).getName();
        this.beforeName = before.getName();
        this.beforeValues = "[" + String.join(";", beforeValues) + "]";
        this.beforeType = ArgumentUtils.getArgumentType(before).getSimpleName();

        this.afterCall = after.getAstParent(true).getName();
        this.afterName = after.getName();
        this.afterValues = "[" + String.join(";", afterValues) + "]";
        this.afterType = ArgumentUtils.getArgumentType(after).getSimpleName();
    }

    @Override
    public String[] getKeys() {
        return new String[] {
                "before_call",
                "before_name",
                "before_values",
                "before_type",
                "after_call",
                "after_name",
                "after_values",
                "after_type"
        };
    }

    @Override
    public Object[] getValues() {
        return new String[] {
                this.beforeCall,
                this.beforeName,
                this.beforeValues,
                this.beforeType,
                this.afterCall,
                this.afterName,
                this.afterValues,
                this.afterType
        };
    }
}
