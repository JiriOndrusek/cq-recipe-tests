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
package org.apache.camel.quarkus.update.v3_0;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

public class CamelEIPRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new CamelQuarkusMigrationRecipe(true))
                .parser(JavaParser.fromJavaVersion().logCompilationWarningsAndErrors(true).classpath("camel-activemq"))
                .typeValidationOptions(TypeValidation.none());
        ;
    }

    @Test
    void testRemovedEIPInOptionalOut() {
        rewriteRun(java("""
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .inOut("activemq:queue:testqueue")
                            .to("log:result_a");
                        }
                    }
                """, """
                    import org.apache.camel.ExchangePattern;
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .setExchangePattern(ExchangePattern.InOut).to("activemq:queue:testqueue")
                            .to("log:result_a");
                        }
                    }
                """

        ));
    }

    @Test
    void testRemovedEIPOutOptionalIn() {
        rewriteRun(java("""
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .inOut("activemq:queue:testqueue")
                            .to("log:result_a");
                        }
                    }
                """, """
                    import org.apache.camel.ExchangePattern;
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .setExchangePattern(ExchangePattern.InOut).to("activemq:queue:testqueue")
                            .to("log:result_a");
                        }
                    }
                """

        ));
    }

    @Test
    void testRemovedEIPOutIn() {
        rewriteRun(java("""
                        import org.apache.camel.ExchangePattern;
                        import org.apache.camel.builder.RouteBuilder;

                        public class MySimpleToDRoute extends RouteBuilder {

                            @Override
                            public void configure() {
                                from("direct:a")
                                .setExchangePattern(ExchangePattern.InOut).to("activemq:queue:testqueue")
                                .to("log:result_a");
                            }
                        }
                """

        ));
    }

}
