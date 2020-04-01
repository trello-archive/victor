import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Test

/*
 * Copyright 2015 Trello, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Run via gradle
 *
 * ```
 *  ./gradlew :victor:cleanTest :victor:test --tests "PluginTest"
 * ```
 */
class PluginTest {
    @Lazy
    private runner = GradleRunner.create()
            .withProjectDir(new File('test/app'))
            .withPluginClasspath()

    @Test
    void testRasterize() {
        try {
            def result = runner.withArguments(
//                    "-debug",
                    "--rerun-tasks",
                    "--stacktrace",
                    "rasterizeSvgsForDebug",
                    "rasterizeSvgsForRelease"
            )
                    .build()

            println(result.output)
        } catch(UnexpectedBuildFailure e) {
            println(e.buildResult.output)
            throw e
        }
    }
}