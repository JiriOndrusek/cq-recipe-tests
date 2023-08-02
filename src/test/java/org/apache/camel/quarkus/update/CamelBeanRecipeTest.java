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
import static org.openrewrite.test.RewriteTest.toRecipe;

import org.apache.camel.quarkus.update.java.CamelBeanRecipe;
import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

public class CamelBeanRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new CamelQuarkusMigrationRecipe(true))
                .parser(JavaParser.fromJavaVersion().logCompilationWarningsAndErrors(true).classpath("camel-bean"))
                .typeValidationOptions(TypeValidation.none());
        ;
    }

    @Test
    void testClassTypeAndInt() {
        rewriteRun(java("""
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(com.foo.MyOrder, int)");
                        }
                    }
                """, """
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(com.foo.MyOrder.class, int.class)");
                        }
                    }
                """

        ));
    }

    @Test
    void testClassTypeAndBoolean() {
        rewriteRun(java("""
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(com.foo.MyOrder, true)");
                        }
                    }
                """, """
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(com.foo.MyOrder.class, true)");
                        }
                    }
                """

        ));
    }

    @Test
    void testClassTypeAndFloat() {
        rewriteRun(java("""
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(com.foo.MyOrder, float)");
                        }
                    }
                """, """
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(com.foo.MyOrder.class, float.class)");
                        }
                    }
                """

        ));
    }

    @Test
    void testDoubleAndChar() {
        rewriteRun(java("""
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(double, char)");
                        }
                    }
                """, """
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(double.class, char.class)");
                        }
                    }
                """

        ));
    }

    @Test
    void testMultipleTo() {
        rewriteRun(java("""
                import org.apache.camel.builder.RouteBuilder;

                public class MySimpleToDRoute extends RouteBuilder {

                    @Override
                    public void configure() {
                        from("direct:a")
                        .to("bean:myBean?method=foo(double, char)")
                        .to("bean:myBean?method=bar(float, int)");
                    }
                }
                """, """
                    import org.apache.camel.builder.RouteBuilder;

                    public class MySimpleToDRoute extends RouteBuilder {

                        @Override
                        public void configure() {
                            from("direct:a")
                            .to("bean:myBean?method=foo(double.class, char.class)")
                            .to("bean:myBean?method=bar(float.class, int.class)");
                        }
                    }
                """

        ));
    }

}
