/*-
 * -\-\-
 * rkt-launcher
 * --
 * 
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 * -/-/-
 */
package io.honnix.rkt.launcher.command;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.PerImageOptions;
import io.honnix.rkt.launcher.options.RunOptions;
import io.honnix.rkt.launcher.output.RunOutput;
import org.junit.Before;
import org.junit.Test;

public class RunTest {

  private Run run;

  @Before
  public void setUp() {
    run = Run.builder()
        .options(RunOptions.builder()
                     .addImagesOption(PerImageOptions.builder().image("foo").build())
                     .build())
        .daemonize(true)
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "run",
        "foo",
        "---");
    assertEquals(expected, run.asList());
  }

  @Test
  public void shouldParseDaemonOutput() {
    final RunOutput runOutput =
        run.parse("Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.");
    assertEquals("run-r69ece8681fbc4e7787e639a78b141599.service", runOutput.service());
  }

  @Test
  public void shouldParseNonDaemonOutput() {
    run = Run.builder()
        .options(RunOptions.builder()
                     .addImagesOption(PerImageOptions.builder().image("foo").build())
                     .build())
        .daemonize(false)
        .build();
    final RunOutput runOutput =
        run.parse("Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.");
    assertEquals("NA", runOutput.service());
  }
}
