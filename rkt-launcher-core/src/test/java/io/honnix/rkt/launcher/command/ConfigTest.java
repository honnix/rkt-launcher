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
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.model.config.Stage0Entry;
import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

  private Config config;

  @Before
  public void setUp() {
    config = Config.COMMAND;
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "config");
    assertEquals(expected, config.asList());
  }

  @Test
  public void shouldParseOutput() {
    //language=JSON
    final String json = "{\n"
                        + "  \"stage0\": [\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"paths\",\n"
                        + "      \"data\": \"/var/lib/rkt\",\n"
                        + "      \"stage1-images\": \"/usr/lib/rkt\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"stage1\",\n"
                        + "      \"name\": \"coreos.com/rkt/stage1-coreos\",\n"
                        + "      \"version\": \"0.15.0+git\",\n"
                        + "      \"location\": \"/usr/libexec/rkt/stage1-coreos.aci\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";

    final io.honnix.rkt.launcher.model.config.Config configOutput = config.parse(json).config();
    final Stage0Entry.Paths paths = (Stage0Entry.Paths) configOutput.stage0().get(0);
    assertEquals("/var/lib/rkt", paths.data());
    assertEquals("/usr/lib/rkt", paths.stage1Images());
    final Stage0Entry.Stage1 stage1 = (Stage0Entry.Stage1) configOutput.stage0().get(1);
    assertEquals("coreos.com/rkt/stage1-coreos", stage1.name());
    assertEquals("0.15.0+git", stage1.version());
    assertEquals("/usr/libexec/rkt/stage1-coreos.aci", stage1.location());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenInvalidJson() {
    config.parse("[not_a_json");
  }
}
