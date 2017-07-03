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
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.output.GcOutput;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class GcTest {

  private Gc gc;

  @Before
  public void setUp() {
    gc = Gc.builder()
        .options(GcOptions.builder()
                     .expirePrepared(Duration.ofHours(24))
                     .gracePeriod(Duration.ofMinutes(5).plusSeconds(10))
                     .build())
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "gc",
        "--expire-prepared=24h0m0s",
        "--grace-period=0h5m10s");
    assertEquals(expected, gc.asList());
  }

  @Test
  public void shouldParseOutputNoRemoved() {
    final String output = "gc: moving pod \"6806ba33-0bfa-4389-84ba-3abba3dba97b\" to garbage\n"
                          + "gc: moving pod \"7806ba33-0bfa-4389-84ba-3abba3dba97b\" to garbage\n"
                          + "gc: pod \"6806ba33-0bfa-4389-84ba-3abba3dba97b\" not removed: still within grace period (5m10s)\n"
                          + "gc: pod \"7806ba33-0bfa-4389-84ba-3abba3dba97b\" not removed: still within grace period (5m10s)";
    final GcOutput gcOutput = gc.parse(output);
    assertEquals(ImmutableList.of("6806ba33-0bfa-4389-84ba-3abba3dba97b",
                                  "7806ba33-0bfa-4389-84ba-3abba3dba97b"), gcOutput.marked());
    assertTrue(gcOutput.removed().isEmpty());
    assertEquals(ImmutableList.of("6806ba33-0bfa-4389-84ba-3abba3dba97b",
                                  "7806ba33-0bfa-4389-84ba-3abba3dba97b"), gcOutput.unremoved());
  }

  @Test
  public void shouldParseOutputRemoved() {
    final String output = "gc: moving pod \"6806ba33-0bfa-4389-84ba-3abba3dba97b\" to garbage\n"
                          + "gc: moving pod \"7806ba33-0bfa-4389-84ba-3abba3dba97b\" to garbage\n"
                          + "gc: pod \"6806ba33-0bfa-4389-84ba-3abba3dba97b\" not removed: still within grace period (5m10s)\n"
                          + "Garbage collecting pod \"7806ba33-0bfa-4389-84ba-3abba3dba97b\"";
    final GcOutput gcOutput = gc.parse(output);
    assertEquals(ImmutableList.of("6806ba33-0bfa-4389-84ba-3abba3dba97b",
                                  "7806ba33-0bfa-4389-84ba-3abba3dba97b"), gcOutput.marked());
    assertEquals(ImmutableList.of("7806ba33-0bfa-4389-84ba-3abba3dba97b"), gcOutput.removed());
    assertEquals(ImmutableList.of("6806ba33-0bfa-4389-84ba-3abba3dba97b"), gcOutput.unremoved());
  }

  @Test
  public void shouldParseEmptyOutput() {
    final GcOutput gcOutput = gc.parse("");
    assertTrue(gcOutput.marked().isEmpty());
    assertTrue(gcOutput.removed().isEmpty());
    assertTrue(gcOutput.unremoved().isEmpty());
  }
}
