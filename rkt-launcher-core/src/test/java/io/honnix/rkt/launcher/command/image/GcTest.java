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
package io.honnix.rkt.launcher.command.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.output.image.GcOutput;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class GcTest {

  private Gc gc;

  @Before
  public void setUp() {
    gc = Gc.builder()
        .options(GcOptions.builder()
                     .gracePeriod(Duration.ofMinutes(5).plusSeconds(10))
                     .build())
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "image",
        "gc",
        "--grace-period=0h5m10s");
    assertEquals(expected, gc.asList());
  }

  @Test
  public void shouldParseOutput() {
    final String output =
        "gc: removed treestore \"deps-sha512-219204dd54481154aec8f6eafc0f2064d973c8a2c0537eab827b7414f0a36248\"\n"
        + "gc: removed treestore \"deps-sha512-3f2a1ad0e9739d977278f0019b6d7d9024a10a2b1166f6c9fdc98f77a357856d\"\n"
        + "gc: successfully removed aci for image: \"sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444\"\n"
        + "gc: successfully removed aci for image: \"sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d\"\n"
        + "gc: 2 image(s) successfully removed";
    final GcOutput gcOutput = gc.parse(output);
    assertEquals(ImmutableList
                     .of("deps-sha512-219204dd54481154aec8f6eafc0f2064d973c8a2c0537eab827b7414f0a36248",
                         "deps-sha512-3f2a1ad0e9739d977278f0019b6d7d9024a10a2b1166f6c9fdc98f77a357856d"),
                 gcOutput.removedTreestores());
    assertEquals(ImmutableList
                     .of("sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444",
                         "sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d"),
                 gcOutput.removedImages());
  }

  @Test
  public void shouldParseOutputNoImageRemoved() {
    final String output =
        "gc: removed treestore \"deps-sha512-219204dd54481154aec8f6eafc0f2064d973c8a2c0537eab827b7414f0a36248\"\n"
        + "gc: removed treestore \"deps-sha512-3f2a1ad0e9739d977278f0019b6d7d9024a10a2b1166f6c9fdc98f77a357856d\"";
    final GcOutput gcOutput = gc.parse(output);
    assertEquals(ImmutableList
                     .of("deps-sha512-219204dd54481154aec8f6eafc0f2064d973c8a2c0537eab827b7414f0a36248",
                         "deps-sha512-3f2a1ad0e9739d977278f0019b6d7d9024a10a2b1166f6c9fdc98f77a357856d"),
                 gcOutput.removedTreestores());
    assertTrue(gcOutput.removedImages().isEmpty());
  }

  @Test
  public void shouldParseEmptyOutput() {
    final GcOutput gcOutput = gc.parse("");
    assertTrue(gcOutput.removedTreestores().isEmpty());
    assertTrue(gcOutput.removedImages().isEmpty());
  }
}
