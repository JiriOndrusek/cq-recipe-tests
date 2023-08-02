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

import static org.openrewrite.test.RewriteTest.toRecipe;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.yaml.Assertions;

public class CamelYamlTest extends BaseCamelQuarkusTest {

    @Test
    void testStepsToFrom1() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                  route:
                    from:
                      uri: "direct:info"
                    steps:
                      log: "message"
                """, """
                  route:
                    from:
                      uri: "direct:info"
                      steps:
                        log: "message"
                """));
    }

    @Test
    void testStepsToFrom2() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                    from:
                      uri: "direct:info"
                    steps:
                      log: "message"
                """, """
                    from:
                      uri: "direct:info"
                      steps:
                        log: "message"
                """));
    }

    @Test
    void testStepsToFrom3() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                - from:
                    uri: "direct:start"
                  steps:
                  - filter:
                      expression:
                        simple: "${in.header.continue} == true"
                      steps:
                        - to:
                            uri: "log:filtered"
                  - to:
                      uri: "log:original"
                """, """
                  - from:
                      uri: "direct:start"
                      steps:
                        - filter:
                            expression:
                              simple: "${in.header.continue} == true"
                            steps:
                              - to:
                                  uri: "log:filtered"
                        - to:
                            uri: "log:original"
                """));
    }

    @Test
    void testRouteConfigurationWithOnException() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                - route-configuration:
                    - id: "yamlRouteConfiguration"
                    - on-exception:
                        handled:
                          constant: "true"
                        exception:
                          - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                        steps:
                          - set-body:
                              constant:
                                  expression: "onException has been triggered in yamlRouteConfiguration"
                """, """
                  - route-configuration:
                      id: "yamlRouteConfiguration"
                      on-exception:
                        - on-exception:
                            handled:
                              constant: "true"
                            exception:
                              - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                            steps:
                              - set-body:
                                  constant:
                                    expression: "onException has been triggered in yamlRouteConfiguration"
                """));
    }

    @Test
    void testRouteConfigurationWithoutOnException() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                - route-configuration:
                    - id: "__id"
                """, """
                  - route-configuration:
                      id: "__id"
                """));
    }

    @Test
    void testDoubleDocument() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                - route-configuration:
                    - id: "yamlRouteConfiguration1"
                    - on-exception:
                        handled:
                          constant: "true"
                        exception:
                          - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                        steps:
                          - set-body:
                              constant:
                                  expression: "onException has been triggered in yamlRouteConfiguration"
                ---
                - route-configuration:
                    - id: "yamlRouteConfiguration2"
                    - on-exception:
                        handled:
                          constant: "true"
                        exception:
                          - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                        steps:
                          - set-body:
                              constant:
                                  expression: "onException has been triggered in yamlRouteConfiguration"
                """, """
                  - route-configuration:
                      id: "yamlRouteConfiguration1"
                      on-exception:
                        - on-exception:
                            handled:
                              constant: "true"
                            exception:
                              - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                            steps:
                              - set-body:
                                  constant:
                                    expression: "onException has been triggered in yamlRouteConfiguration"
                  ---
                  - route-configuration:
                      id: "yamlRouteConfiguration2"
                      on-exception:
                        - on-exception:
                            handled:
                              constant: "true"
                            exception:
                              - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                            steps:
                              - set-body:
                                  constant:
                                    expression: "onException has been triggered in yamlRouteConfiguration"
                """));
    }

    @Test
    void testDoubleDocumentSimple() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                - route-configuration:
                    - id: "__id1"
                ---
                - route-configuration:
                    - id: "__id2"
                """, """
                  - route-configuration:
                      id: "__id1"
                  ---
                  - route-configuration:
                      id: "__id2"
                """));
    }

    @Test
    void testRouteConfigurationIdempotent() {
        rewriteRun(spec -> spec.recipe(new CamelQuarkusMigrationRecipe(true)), Assertions.yaml("""
                  - route-configuration:
                      id: "yamlRouteConfiguration"
                      on-exception:
                        - on-exception:
                            handled:
                              constant: "true"
                            exception:
                              - "org.apache.camel.quarkus.core.it.routeconfigurations.RouteConfigurationsException"
                            steps:
                              - set-body:
                                  constant:
                                    expression: "onException has been triggered in yamlRouteConfiguration"
                """));
    }
}
