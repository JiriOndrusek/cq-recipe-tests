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

import static org.openrewrite.maven.Assertions.pomXml;

import java.util.Arrays;
import java.util.stream.Stream;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.SourceSpecs;

public class BaseCamelQuarkusTest implements RewriteTest {

    SourceSpecs[] withCamel(SourceSpecs... sourceSpecs) {
        return Stream.concat(Stream.of(camelPom()), Arrays.stream(sourceSpecs)).toArray(SourceSpecs[]::new);
    }

    private SourceSpecs camelPom() {
        return pomXml("""
                <project>
                <modelVersion>4.0.0</modelVersion>

                <artifactId>test</artifactId>
                <groupId>org.apache.camel.quarkus.test</groupId>
                <version>1.0.0</version>

                <properties>
                <quarkus.platform.version>2.13.3.Final</quarkus.platform.version>
                </properties>

                <dependencyManagement>
                <dependencies>
                   <dependency>
                       <groupId>io.quarkus.platform</groupId>
                       <artifactId>quarkus-camel-bom</artifactId>
                       <version>2.13.7.Final</version>
                       <type>pom</type>
                       <scope>import</scope>
                   </dependency>
                </dependencies>
                </dependencyManagement>

                <dependencies>
                <dependency>
                   <groupId>org.apache.camel.quarkus</groupId>
                   <artifactId>camel-quarkus-activemq</artifactId>
                </dependency>
                </dependencies>

                </project>
                                        """,

                """
                        <project>
                        <modelVersion>4.0.0</modelVersion>

                        <artifactId>test</artifactId>
                        <groupId>org.apache.camel.quarkus.test</groupId>
                        <version>1.0.0</version>

                        <properties>
                        <quarkus.platform.version>2.13.3.Final</quarkus.platform.version>
                        </properties>

                        <dependencyManagement>
                        <dependencies>
                           <dependency>
                               <groupId>io.quarkus.platform</groupId>
                               <artifactId>quarkus-camel-bom</artifactId>
                               <version>2.13.7.Final</version>
                               <type>pom</type>
                               <scope>import</scope>
                           </dependency>
                        </dependencies>
                        </dependencyManagement>

                        <dependencies>
                        <!--Extension camel-quarkus-activemq was removed, consider camel-quarkus-jms or camel-quarkus-sjms or camel-quarkus-amqp instead.\s
                        <dependency>
                           <groupId>org.apache.camel.quarkus</groupId>
                           <artifactId>camel-quarkus-activemq</artifactId>
                        </dependency>-->
                        </dependencies>

                        </project>
                                                """);
    }

}
