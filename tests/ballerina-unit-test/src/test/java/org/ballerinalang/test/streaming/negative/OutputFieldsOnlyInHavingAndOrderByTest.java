/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.test.streaming.negative;

import org.ballerinalang.launcher.util.BAssertUtil;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This contains methods to test if the proper error is thrown if the output attributes are used in select clause.
 *
 * @since 0.990.1
 */
public class OutputFieldsOnlyInHavingAndOrderByTest {

    private CompileResult result;

    @BeforeClass
    public void setup() {
        result = BCompileUtil.compile("test-src/streaming/negative/output-field-not-allowed-in-select.bal");
    }

    @Test
    public void testOutputputFieldInSelect() {
        Assert.assertEquals(result.getErrorCount(), 2);
        BAssertUtil.validateError(result, 0, "alias matching the output stream attribute, not defined", 64, 16);
        BAssertUtil.validateError(result, 1, "output field 'name' can be used in having and orderby " +
                                             "clauses only", 64, 16);
    }
}
