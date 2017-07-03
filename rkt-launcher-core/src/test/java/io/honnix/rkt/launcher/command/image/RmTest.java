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
import io.honnix.rkt.launcher.options.image.RmOptions;
import io.honnix.rkt.launcher.output.image.RmOutput;
import org.junit.Before;
import org.junit.Test;

public class RmTest {

  private Rm rm;

  @Before
  public void setUp() {
    rm = Rm.builder()
        .options(RmOptions.builder()
                     .uuidFile("foo")
                     .build())
        .addArg("arg1")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "image",
        "rm",
        "--uuid-file=foo",
        "arg1");
    assertEquals(expected, rm.asList());
  }

  @Test
  public void shouldParseOutput() {
    final String
        output =
        "successfully removed aci for image: \"sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444\"\n"
        + "successfully removed aci for image: \"sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d\"\n"
        + "rm: 2 image(s) successfully removed";
    final RmOutput rmOutput = rm.parse(output);
    assertEquals(ImmutableList
                     .of("sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444",
                         "sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d"),
                 rmOutput.removed());
  }

  @Test
  public void shouldParseEmptyOutput() {
    final RmOutput rmOutput = rm.parse("");
    assertTrue(rmOutput.removed().isEmpty());
  }
}
