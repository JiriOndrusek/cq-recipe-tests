/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.update;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openrewrite.yaml.Assertions;

public class NonCamelProjectTest extends BaseCamelQuarkusTest {

    @BeforeAll
    static void before() {
        // end overriding
        RecipesUtil.overrideInternallyCamelPresent(false);
    }

    @Test
    void testYamlNoCamelProject() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe()), Assertions.yaml("""
                - route-configuration:
                    - id: "__id"
                """));
    }

    @Test
    void testYamlCamelProject() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe()), withCamel(Assertions.yaml("""
                - route-configuration:
                    - id: "__id"
                """, """
                  - route-configuration:
                      id: "__id"
                """)));
    }

    @Test
    void testPropertiesNoCamelProject() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe()),
                org.openrewrite.properties.Assertions.properties("""
                           camel.threadpool.rejectedPolicy=DiscardOldest
                        """));
    }

    @Test
    void testPropertiesCamelProject() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe()).expectedCyclesThatMakeChanges(2),
                withCamel(org.openrewrite.properties.Assertions.properties("""
                           #test
                           camel.threadpool.rejectedPolicy=Discard
                        """,
                        """
                                    #test
                                    #'ThreadPoolRejectedPolicy.camel.threadpool.rejectedPolicy' has been removed, consider using 'Abort'. camel.threadpool.rejectedPolicy=Discard
                                """)));
    }

    @Test
    void testJavaNonCamel() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe()), java("""
                import org.apache.camel.builder.SimpleBuilder;
                """));
    }

    @Test
    void testJavaCamel() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe()).expectedCyclesThatMakeChanges(2),
                withCamel(java("""
                        import org.apache.camel.builder.SimpleBuilder;
                        """,
                        """
                                  /*'java.beans.SimpleBeanInfo' has been removed, (class was used internally).*/import org.apache.camel.builder.SimpleBuilder;
                                """)));
    }

}
