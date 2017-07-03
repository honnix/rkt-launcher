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
import io.honnix.rkt.launcher.options.RmOptions;
import io.honnix.rkt.launcher.output.RmOutput;
import org.junit.Before;
import org.junit.Test;

public class RmTest {

  private Rm rm;

  @Before
  public void setUp() {
    rm = Rm.builder()
        .options(RmOptions.builder()
                     .build())
        .args(ImmutableList.of("1e4bb8f2", "9c1c7e0b"))
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "rm",
        "1e4bb8f2", "9c1c7e0b");
    assertEquals(expected, rm.asList());
  }

  @Test
  public void shouldParseOutput() {
    final String output = "\"1e4bb8f2\"\n"
                          + "\"9c1c7e0b\"";
    final RmOutput rmOutput = rm.parse(output);
    assertEquals(2, rmOutput.removed().size());
    assertEquals("1e4bb8f2", rmOutput.removed().get(0));
    assertEquals("9c1c7e0b", rmOutput.removed().get(1));
  }

  @Test
  public void shouldParseEmptyOutput() {
    final RmOutput rmOutput = rm.parse("");
    assertTrue(rmOutput.removed().isEmpty());
  }
}
