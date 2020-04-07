/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerinalang.compiler.internal.parser.tree;

import io.ballerinalang.compiler.syntax.tree.Annotation;
import io.ballerinalang.compiler.syntax.tree.Node;
import io.ballerinalang.compiler.syntax.tree.NonTerminalNode;

/**
 * @since 1.3.0
 */
public class STAnnotation extends STParameter {

    public final STNode atToken;
    public final STNode annotReference;
    public final STNode annotValue;

    STAnnotation(STNode atToken, STNode annotReference, STNode annotValue) {
        super(SyntaxKind.ANNOTATION);
        this.atToken = atToken;
        this.annotReference = annotReference;
        this.annotValue = annotValue;

        addChildren(atToken, annotReference, annotValue);
    }

    @Override
    public Node createFacade(int position, NonTerminalNode parent) {
        return new Annotation(this, position, parent);
    }
}
