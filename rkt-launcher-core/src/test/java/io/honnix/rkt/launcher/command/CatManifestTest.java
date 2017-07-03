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
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.options.CatManifestOptions;
import io.honnix.rkt.launcher.model.schema.PodManifest;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import org.junit.Before;
import org.junit.Test;

public class CatManifestTest {

  private CatManifest catManifest;

  @Before
  public void setUp() {
    catManifest = CatManifest.builder()
        .options(CatManifestOptions.builder()
                     .build())
        .args(ImmutableList.of("arg1"))
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "cat-manifest",
        "arg1");
    assertEquals(expected, catManifest.asList());
  }

  @Test
  public void shouldParseOutput() {
    //language=JSON
    final String json = "{\n"
                        + "  \"acVersion\": \"1.25.0\",\n"
                        + "  \"acKind\": \"PodManifest\",\n"
                        + "  \"apps\": [],\n"
                        + "  \"volumes\": [\n"
                        + "    {\n"
                        + "      \"name\": \"volume1\",\n"
                        + "      \"kind\": \"host\",\n"
                        + "      \"source\": \"/source\",\n"
                        + "      \"readOnly\": true\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"volume1\",\n"
                        + "      \"kind\": \"empty\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"isolators\": [],\n"
                        + "  \"annotations\": [\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/mutable\",\n"
                        + "      \"value\": \"false\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"ports\": []\n"
                        + "}\n";

    final PodManifest podManifest = catManifest.parse(json).podManifest();
    assertEquals("1.25.0", podManifest.acVersion());
    assertEquals(ACKind.POD_MANIFEST, podManifest.acKind());
    assertEquals(2, podManifest.volumes().size());
    assertTrue(podManifest.isolators().isEmpty());
    assertTrue(podManifest.ports().isEmpty());
    assertTrue(podManifest.apps().isEmpty());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenInvalidJson() {
    catManifest.parse("[not_a_json");
  }
}
