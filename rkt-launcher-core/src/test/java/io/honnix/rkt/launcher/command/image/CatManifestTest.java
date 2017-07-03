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
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.model.schema.ImageManifest;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CatManifestTest {

  private CatManifest catManifest;

  @Before
  public void setUp() {
    catManifest = CatManifest.builder()
        .addArg("arg1")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "image",
        "cat-manifest",
        "arg1");
    assertEquals(expected, catManifest.asList());
  }

  @Test
  public void shouldParseOutputWithoutApp() {
    //language=JSON
    final String json = "{\n"
                        + "  \"acKind\": \"ImageManifest\",\n"
                        + "  \"acVersion\": \"0.8.10\",\n"
                        + "  \"name\": \"coreos.com/rkt/stage1-coreos\",\n"
                        + "  \"labels\": [\n"
                        + "    {\n"
                        + "      \"name\": \"version\",\n"
                        + "      \"value\": \"1.25.0\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"arch\",\n"
                        + "      \"value\": \"amd64\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"os\",\n"
                        + "      \"value\": \"linux\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"annotations\": [\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/run\",\n"
                        + "      \"value\": \"/init\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/enter\",\n"
                        + "      \"value\": \"/enter\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/gc\",\n"
                        + "      \"value\": \"/gc\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/stop\",\n"
                        + "      \"value\": \"/stop\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/add\",\n"
                        + "      \"value\": \"/app_add\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/rm\",\n"
                        + "      \"value\": \"/app_rm\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/start\",\n"
                        + "      \"value\": \"/app_start\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/stop\",\n"
                        + "      \"value\": \"/app_stop\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/attach\",\n"
                        + "      \"value\": \"/attach\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/interface-version\",\n"
                        + "      \"value\": \"5\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";

    final ImageManifest imageManifest = catManifest.parse(json).imageManifest();
    assertEquals("0.8.10", imageManifest.acVersion());
    Assert.assertEquals(ACKind.IMAGE_MANIFEST, imageManifest.acKind());
    assertTrue(imageManifest.labels().isPresent());
    assertEquals(3, imageManifest.labels().get().size());
    assertTrue(imageManifest.annotations().isPresent());
    assertEquals(10, imageManifest.annotations().get().size());
  }

  @Test
  public void shouldParseOutputWithApp() {
    //language=JSON
    final String json = "{\n"
                        + "  \"acKind\": \"ImageManifest\",\n"
                        + "  \"acVersion\": \"0.5.1\",\n"
                        + "  \"name\": \"coreos.com/etcd\",\n"
                        + "  \"labels\": [\n"
                        + "    {\n"
                        + "      \"name\": \"os\",\n"
                        + "      \"value\": \"linux\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"arch\",\n"
                        + "      \"value\": \"amd64\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"version\",\n"
                        + "      \"value\": \"v2.2.5\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"app\": {\n"
                        + "    \"exec\": [\n"
                        + "      \"/etcd\"\n"
                        + "    ],\n"
                        + "    \"user\": \"0\",\n"
                        + "    \"group\": \"0\",\n"
                        + "    \"environment\": [\n"
                        + "      {\n"
                        + "        \"name\": \"ETCD_DATA_DIR\",\n"
                        + "        \"value\": \"/data-dir\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"mountPoints\": [\n"
                        + "      {\n"
                        + "        \"name\": \"data-dir\",\n"
                        + "        \"path\": \"/data-dir\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"ports\": [\n"
                        + "      {\n"
                        + "        \"name\": \"legacy-client\",\n"
                        + "        \"protocol\": \"tcp\",\n"
                        + "        \"port\": 4001,\n"
                        + "        \"count\": 1,\n"
                        + "        \"socketActivated\": false\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"name\": \"client\",\n"
                        + "        \"protocol\": \"tcp\",\n"
                        + "        \"port\": 2379,\n"
                        + "        \"count\": 1,\n"
                        + "        \"socketActivated\": false\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"name\": \"legacy-peer\",\n"
                        + "        \"protocol\": \"tcp\",\n"
                        + "        \"port\": 7001,\n"
                        + "        \"count\": 1,\n"
                        + "        \"socketActivated\": false\n"
                        + "      },\n"
                        + "      {\n"
                        + "        \"name\": \"peer\",\n"
                        + "        \"protocol\": \"tcp\",\n"
                        + "        \"port\": 2380,\n"
                        + "        \"count\": 1,\n"
                        + "        \"socketActivated\": false\n"
                        + "      }\n"
                        + "    ]\n"
                        + "  }\n"
                        + "}";

    final ImageManifest imageManifest = catManifest.parse(json).imageManifest();
    assertEquals("0.5.1", imageManifest.acVersion());
    Assert.assertEquals(ACKind.IMAGE_MANIFEST, imageManifest.acKind());
    assertTrue(imageManifest.labels().isPresent());
    assertEquals(3, imageManifest.labels().get().size());
    assertTrue(imageManifest.app().isPresent());
    Assert.assertEquals("0", imageManifest.app().get().group());
    Assert.assertEquals("0", imageManifest.app().get().user());
    Assert.assertEquals("/etcd", imageManifest.app().get().exec().get(0));
    assertTrue(imageManifest.app().get().environment().isPresent());
    Assert.assertEquals(1, imageManifest.app().get().environment().get().size());
    assertTrue(imageManifest.app().get().mountPoints().isPresent());
    Assert.assertEquals(1, imageManifest.app().get().mountPoints().get().size());
    assertTrue(imageManifest.app().get().ports().isPresent());
    Assert.assertEquals(4, imageManifest.app().get().ports().get().size());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenInvalidJson() {
    catManifest.parse("[not_a_json");
  }
}
