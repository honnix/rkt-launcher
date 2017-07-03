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
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.image.ExtractOptions;
import io.honnix.rkt.launcher.output.Output;
import org.junit.Before;
import org.junit.Test;

public class ExtractTest {

  private Extract extract;

  @Before
  public void setUp() {
    extract = Extract.builder()
        .options(ExtractOptions.builder()
                     .overwrite(true)
                     .rootfsOnly(true)
                     .build())
        .addArg("foo")
        .addArg("bar")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "image",
        "extract",
        "--overwrite=true",
        "--rootfs-only=true",
        "foo",
        "bar");
    assertEquals(expected, extract.asList());
  }

  @Test
  public void shouldParseEmptyString() {
    final Output extractOutput = extract.parse("");
    assertNotNull(extractOutput);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfNotTwoArguments() {
    extract = Extract.builder()
        .options(ExtractOptions.builder()
                     .overwrite(true)
                     .rootfsOnly(true)
                     .build())
        .addArg("foo")
        .build();
    extract.asList();
  }
}
