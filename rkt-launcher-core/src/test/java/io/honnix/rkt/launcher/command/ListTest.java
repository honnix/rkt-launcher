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
import io.honnix.rkt.launcher.model.Pod;
import io.honnix.rkt.launcher.options.ListOptions;
import org.junit.Before;
import org.junit.Test;

public class ListTest {

  private List list;

  @Before
  public void setUp() {
    list = List.builder()
        .options(ListOptions.builder()
                     .build())
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "list",
        "--format=json");
    assertEquals(expected, list.asList());
  }

  @Test
  public void shouldParseOutput() {
    //language=JSON
    final String json = "[\n"
                        + "  {\n"
                        + "    \"name\": \"1e4bb8f2-ea9c-4c5d-8c20-66ef94a2c74b\",\n"
                        + "    \"state\": \"running\",\n"
                        + "    \"networks\": [\n"
                        + "      {\n"
                        + "        \"netName\": \"default\",\n"
                        + "        \"netConf\": \"net/99-default.conf\",\n"
                        + "        \"pluginPath\": \"stage1/rootfs/usr/lib/rkt/plugins/net/ptp\",\n"
                        + "        \"ifName\": \"eth0\",\n"
                        + "        \"ip\": \"172.16.28.4\",\n"
                        + "        \"args\": \"\",\n"
                        + "        \"mask\": \"255.255.255.0\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"app_names\": [\n"
                        + "      \"nginx\"\n"
                        + "    ],\n"
                        + "    \"apps\": [\n"
                        + "      {\n"
                        + "        \"name\": \"nginx\",\n"
                        + "        \"state\": \"running\",\n"
                        + "        \"created_at\": 1502192853851821300,\n"
                        + "        \"started_at\": 1502192853947821300,\n"
                        + "        \"image_id\": \"sha512-5ab3bca3027d512e181862990e153361ed37e67af9b4aeeb0637100ee473d234\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"started_at\": 1502192853\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"name\": \"9c1c7e0b-8927-45e1-a1de-5cd34f04a246\",\n"
                        + "    \"state\": \"running\",\n"
                        + "    \"networks\": [\n"
                        + "      {\n"
                        + "        \"netName\": \"default\",\n"
                        + "        \"netConf\": \"net/99-default.conf\",\n"
                        + "        \"pluginPath\": \"stage1/rootfs/usr/lib/rkt/plugins/net/ptp\",\n"
                        + "        \"ifName\": \"eth0\",\n"
                        + "        \"ip\": \"172.16.28.2\",\n"
                        + "        \"args\": \"\",\n"
                        + "        \"mask\": \"255.255.255.0\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"app_names\": [\n"
                        + "      \"nginx\"\n"
                        + "    ],\n"
                        + "    \"apps\": [\n"
                        + "      {\n"
                        + "        \"name\": \"nginx\",\n"
                        + "        \"state\": \"running\",\n"
                        + "        \"created_at\": 1502192853851821300,\n"
                        + "        \"started_at\": 1502192853947821300,\n"
                        + "        \"image_id\": \"sha512-5ab3bca3027d512e181862990e153361ed37e67af9b4aeeb0637100ee473d234\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"started_at\": 1502192853\n"
                        + "  }\n"
                        + "]";

    final java.util.List<Pod> pods = list.parse(json).pods();
    assertEquals(2, pods.size());
  }

  @Test
  public void shouldParseOutputWhenThereIsNoPod() {
    //language=JSON
    final String json = "null";

    final java.util.List<Pod> pods = list.parse(json).pods();
    assertEquals(0, pods.size());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenInvalidJson() {
    list.parse("[not_a_json");
  }
}
