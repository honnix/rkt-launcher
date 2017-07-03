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
import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.output.RunOutput;
import org.junit.Before;
import org.junit.Test;

public class RunPreparedTest {

  private RunPrepared runPrepared;

  @Before
  public void setUp() {
    runPrepared = RunPrepared.builder()
        .options(RunPreparedOptions.builder()
                     .build())
        .daemonize(true)
        .addArg("arg1")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "run-prepared",
        "arg1");
    assertEquals(expected, runPrepared.asList());
  }

  @Test
  public void shouldParseDaemonOutput() {
    final RunOutput runOutput =
        runPrepared.parse("Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.");
    assertEquals("run-r69ece8681fbc4e7787e639a78b141599.service", runOutput.service());
  }

  @Test
  public void shouldParseNonDaemonOutput() {
    runPrepared = RunPrepared.builder()
        .options(RunPreparedOptions.builder()
                     .build())
        .daemonize(false)
        .addArg("arg1")
        .build();
    final RunOutput runOutput =
        runPrepared.parse("Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.");
    assertEquals("NA", runOutput.service());
  }
}
