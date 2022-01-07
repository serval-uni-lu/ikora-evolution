package lu.uni.serval.ikora.evolution.results;

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

import lu.uni.serval.ikora.core.model.Argument;
import lu.uni.serval.ikora.evolution.utils.Hash;

import java.util.Set;

public class VariableChangeRecord implements BaseRecord {
    private final String beforeCall;
    private final String beforeName;
    private final String beforeType;
    private final String beforeValues;

    private final String afterCall;
    private final String afterName;
    private final String afterType;
    private final String afterValues;

    public VariableChangeRecord(Argument before, Set<String> beforeValues, Argument after, Set<String> afterValues) {
        this.beforeCall = before.getAstParent(true).getName();
        this.beforeName = before.getName();
        this.beforeValues = "[" + String.join(";", beforeValues) + "]";
        this.beforeType = before.getType().getName();

        this.afterCall = after.getAstParent(true).getName();
        this.afterName = after.getName();
        this.afterValues = "[" + String.join(";", afterValues) + "]";
        this.afterType = after.getType().getName();
    }

    public String getBeforeCall() {
        return beforeCall;
    }

    public String getBeforeName() {
        return beforeName;
    }

    public String getBeforeType() {
        return beforeType;
    }

    public String getBeforeValues() {
        return beforeValues;
    }

    public String getAfterCall() {
        return afterCall;
    }

    public String getAfterName() {
        return afterName;
    }

    public String getAfterType() {
        return afterType;
    }

    public String getAfterValues() {
        return afterValues;
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
    public Object[] getValues(boolean isHashName) {
        return new String[] {
                isHashName ? Hash.sha512(this.beforeCall) : this.beforeCall,
                isHashName ? Hash.sha512(this.beforeName) : this.beforeName,
                isHashName ? Hash.sha512(this.beforeValues) : this.beforeValues,
                this.beforeType,
                isHashName ? Hash.sha512(this.afterCall) : this.afterCall,
                isHashName ? Hash.sha512(this.afterName) : this.afterName,
                isHashName ? Hash.sha512(this.afterValues) : this.afterValues,
                this.afterType
        };
    }
}
