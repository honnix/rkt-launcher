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

import static io.honnix.rkt.launcher.options.image.ListOptions.Order.ASC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.model.Image;
import io.honnix.rkt.launcher.model.ImageBuilder;
import io.honnix.rkt.launcher.options.image.ListOptions;
import io.honnix.rkt.launcher.util.Json;
import org.junit.Before;
import org.junit.Test;

public class ListTest {

  private List list;

  @Before
  public void setUp() {
    list = List.builder()
        .options(ListOptions.builder()
                     .order(ASC)
                     .build())
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "image",
        "list",
        "--order=asc",
        "--format=json");
    assertEquals(expected, list.asList());
  }

  @Test
  public void shouldParseOutputWithApp() {
    //language=JSON
    final String json = "[\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-887890e697d9a0229eff22436def3c436cb4b18f72ac274c8c05427b39539307\",\n"
                        + "    \"name\": \"coreos.com/rkt/stage1-coreos:1.25.0\",\n"
                        + "    \"import_time\": 1491859470078086852,\n"
                        + "    \"last_used_time\": 1497150284380714857,\n"
                        + "    \"size\": 234530175\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-572c5b51abf596a30c2c9df4798b5da3baa4525ed2b413f33a84ced36a99e9ee\",\n"
                        + "    \"name\": \"registry-1.docker.io/library/nginx:latest\",\n"
                        + "    \"import_time\": 1491859491703293806,\n"
                        + "    \"last_used_time\": 1491859492487748062,\n"
                        + "    \"size\": 374494953\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-938efe6e0cba2f0d56f2675244026d442c668fb18bcb18c2ee778c2ddf7c32cf\",\n"
                        + "    \"name\": \"coreos.com/etcd:v2.2.5\",\n"
                        + "    \"import_time\": 1491860749840089680,\n"
                        + "    \"last_used_time\": 1491860749963552618,\n"
                        + "    \"size\": 29588992\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-9946ffed69f415f644d4b337d3d6f4b228e4527286897345198e11f20cb4b5f7\",\n"
                        + "    \"name\": \"registry.example.com/trusty:0.26\",\n"
                        + "    \"import_time\": 1494618447524974892,\n"
                        + "    \"last_used_time\": 1494618478192533276,\n"
                        + "    \"size\": 771228160\n"
                        + "  }\n"
                        + "]";

    final java.util.List<Image> images = list.parse(json).images();
    assertEquals(4, images.size());
  }

  @Test
  public void shouldParseOutputWhenThereIsNoImage() {
    //language=JSON
    final String json = "null";

    final java.util.List<Image> images = list.parse(json).images();
    assertEquals(0, images.size());
  }
  
  @Test
  public void shouldSerializeToNano() throws JsonProcessingException {
    final Image image = new ImageBuilder()
        .id("sha512-887890e697d9a0229eff22436def3c436cb4b18f72ac274c8c05427b39539307")
        .name("coreos.com/rkt/stage1-coreos:1.25.0")
        .importTime(new Image.Nano2Instant().convert(1491859470078086852L))
        .lastUsedTime(new Image.Nano2Instant().convert(1497150284380714857L))
        .size(234530175)
        .build();
    final String json = new String(Json.serialize(image));
    assertTrue(json.contains("1491859470078086852"));
    assertTrue(json.contains("1497150284380714857"));
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenInvalidJson() {
    list.parse("[not_a_json");
  }
}
