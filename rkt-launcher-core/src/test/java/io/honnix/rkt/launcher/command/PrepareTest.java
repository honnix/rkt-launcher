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
import io.honnix.rkt.launcher.options.PrepareOptions;
import io.honnix.rkt.launcher.output.PrepareOutput;
import org.junit.Before;
import org.junit.Test;

public class PrepareTest {

  private Prepare prepare;

  @Before
  public void setUp() {
    prepare = Prepare.builder()
        .options(PrepareOptions.builder()
                     .addImagesOption(PerImageOptions.builder().image("foo").build())
                     .addImagesOption(PerImageOptions.builder().image("bar").build())
                     .build())
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "prepare",
        "--quiet=true",
        "foo",
        "---",
        "bar",
        "---");
    assertEquals(expected, prepare.asList());
  }

  @Test
  public void shouldParseOutput() {
    final PrepareOutput prepareOutput = prepare.parse("6c8158de-e3f0-46af-9100-88591c17d302\n");
    assertEquals("6c8158de-e3f0-46af-9100-88591c17d302", prepareOutput.prepared());
  }

  @Test
  public void shouldParseOutputWithGarbage() {
    final PrepareOutput prepareOutput =
        prepare.parse("some random garbage\n6c8158de-e3f0-46af-9100-88591c17d302\n");
    assertEquals("6c8158de-e3f0-46af-9100-88591c17d302", prepareOutput.prepared());
  }
}
