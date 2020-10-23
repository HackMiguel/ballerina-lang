/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.cli.task;

import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.SingleFileProject;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import java.io.PrintStream;
import java.util.List;

/**
 * Task for compiling a package.
 *
 * @since 2.0.0
 */
public class CompileTask implements Task {
    private final transient PrintStream out;
    private final transient PrintStream err;

    public CompileTask(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public void execute(Project project) {
        this.out.println();
        this.out.println("Compiling source");

        String sourceName;
        if (project instanceof SingleFileProject) {
            sourceName = project.currentPackage().getDefaultModule().document(
                    project.currentPackage().getDefaultModule().documentIds().iterator().next()).name();
        } else {
            sourceName = project.currentPackage().packageOrg().toString() + "/" +
                    project.currentPackage().packageName().toString() + ":" +
                    project.currentPackage().packageVersion();
        }
        // Print the source
        this.out.println("\t" + sourceName);

        PackageCompilation packageCompilation = project.currentPackage().getCompilation();
        List<Diagnostic> diagnostics = packageCompilation.diagnostics();
        boolean hasError = false;
        for (Diagnostic diagnostic : diagnostics) {
            // log the dignostics, fail if contains errors
            this.err.println(diagnostic.toString());
            if (!hasError && diagnostic.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR)) {
                hasError = true;
            }
        }
        if (hasError) {
            throw new RuntimeException("compilation contains errors");
        }
    }
}