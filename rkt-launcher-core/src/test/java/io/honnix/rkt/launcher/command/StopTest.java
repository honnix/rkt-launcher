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
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.output.StopOutput;
import org.junit.Before;
import org.junit.Test;

public class StopTest {

  private Stop stop;

  @Before
  public void setUp() {
    stop = Stop.builder()
        .options(StopOptions.builder()
                     .uuidFile("foo")
                     .build())
        .args(ImmutableList.of("uuid1", "uuid2"))
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "stop",
        "--uuid-file=foo",
        "uuid1", "uuid2");
    assertEquals(expected, stop.asList());
  }

  @Test
  public void shouldParseOutput() {
    final String output = "\"1e4bb8f2\"\n"
                          + "\"9c1c7e0b\"";
    final StopOutput stopOutput = stop.parse(output);
    assertEquals(2, stopOutput.stopped().size());
    assertEquals("1e4bb8f2", stopOutput.stopped().get(0));
    assertEquals("9c1c7e0b", stopOutput.stopped().get(1));
  }

  @Test
  public void shouldParseEmptyOutput() {
    final StopOutput stopOutput = stop.parse("");
    assertTrue(stopOutput.stopped().isEmpty());
  }
}
