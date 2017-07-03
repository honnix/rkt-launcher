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
package io.honnix.rkt.launcher.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.honnix.rkt.launcher.model.Image;
import io.honnix.rkt.launcher.model.Pod;
import io.honnix.rkt.launcher.model.config.Config;
import io.honnix.rkt.launcher.model.config.ConfigBuilder;
import io.honnix.rkt.launcher.model.config.PathsBuilder;
import io.honnix.rkt.launcher.model.config.Stage0Entry;
import io.honnix.rkt.launcher.model.config.Stage1Builder;
import io.honnix.rkt.launcher.model.schema.PodManifest;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class JsonTest {

  @Test
  public void shouldDeserializePodManifest() throws Exception {
    //language=JSON
    final String json = "{\n"
                        + "  \"acVersion\": \"1.25.0\",\n"
                        + "  \"acKind\": \"PodManifest\",\n"
                        + "  \"apps\": [\n"
                        + "    {\n"
                        + "      \"name\": \"nginx\",\n"
                        + "      \"image\": {\n"
                        + "        \"name\": \"registry-1.docker.io/library/nginx\",\n"
                        + "        \"id\": \"sha512-572c5b51abf596a30c2c9df4798b5da3baa4525ed2b413f33a84ced36a99e9ee\",\n"
                        + "        \"labels\": [\n"
                        + "          {\n"
                        + "            \"name\": \"arch\",\n"
                        + "            \"value\": \"amd64\"\n"
                        + "          },\n"
                        + "          {\n"
                        + "            \"name\": \"os\",\n"
                        + "            \"value\": \"linux\"\n"
                        + "          },\n"
                        + "          {\n"
                        + "            \"name\": \"version\",\n"
                        + "            \"value\": \"latest\"\n"
                        + "          }\n"
                        + "        ]\n"
                        + "      },\n"
                        + "      \"app\": {\n"
                        + "        \"exec\": [\n"
                        + "          \"nginx\",\n"
                        + "          \"-g\",\n"
                        + "          \"daemon off;\"\n"
                        + "        ],\n"
                        + "        \"user\": \"0\",\n"
                        + "        \"group\": \"0\",\n"
                        + "        \"environment\": [\n"
                        + "          {\n"
                        + "            \"name\": \"PATH\",\n"
                        + "            \"value\": \"/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\"\n"
                        + "          },\n"
                        + "          {\n"
                        + "            \"name\": \"NGINX_VERSION\",\n"
                        + "            \"value\": \"1.11.13-1~jessie\"\n"
                        + "          }\n"
                        + "        ],\n"
                        + "        \"ports\": [\n"
                        + "          {\n"
                        + "            \"name\": \"443-tcp\",\n"
                        + "            \"protocol\": \"tcp\",\n"
                        + "            \"port\": 443,\n"
                        + "            \"count\": 1,\n"
                        + "            \"socketActivated\": false\n"
                        + "          },\n"
                        + "          {\n"
                        + "            \"name\": \"80-tcp\",\n"
                        + "            \"protocol\": \"tcp\",\n"
                        + "            \"port\": 80,\n"
                        + "            \"count\": 1,\n"
                        + "            \"socketActivated\": false\n"
                        + "          }\n"
                        + "        ]\n"
                        + "      }\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"volumes\": [],\n"
                        + "  \"isolators\": [],\n"
                        + "  \"annotations\": [\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/mutable\",\n"
                        + "      \"value\": \"false\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"ports\": []\n"
                        + "}\n";
    final PodManifest podManifest = Json.deserialize(json, PodManifest.class);
    assertEquals("1.25.0", podManifest.acVersion());
    Assert.assertEquals(ACKind.POD_MANIFEST, podManifest.acKind());
    assertTrue(podManifest.volumes().isEmpty());
    assertTrue(podManifest.isolators().isEmpty());
    assertTrue(podManifest.ports().isEmpty());
    assertEquals(1, podManifest.apps().size());
  }

  @Test
  public void shouldDeserializeConfig() throws IOException {
    //language=JSON
    final String json = "{\n"
                        + "  \"stage0\": [\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"auth\",\n"
                        + "      \"domains\": [\n"
                        + "        \"bar.com\"\n"
                        + "      ],\n"
                        + "      \"type\": \"oauth\",\n"
                        + "      \"credentials\": {\n"
                        + "        \"token\": \"someToken\"\n"
                        + "      }\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"dockerAuth\",\n"
                        + "      \"registries\": [\n"
                        + "        \"foo.com\",\n"
                        + "        \"bar.com\"\n"
                        + "      ],\n"
                        + "      \"credentials\": {\n"
                        + "        \"user\": \"user\",\n"
                        + "        \"password\": \"password\"\n"
                        + "      }\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"paths\",\n"
                        + "      \"data\": \"/var/lib/rkt\",\n"
                        + "      \"stage1Images\": \"/usr/lib/rkt\"\n"
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
    final Config config = Json.deserialize(json, Config.class);
    final Stage0Entry.Auth auth = (Stage0Entry.Auth) config.stage0().get(0);
    assertEquals("oauth", auth.type());
    assertEquals(ImmutableList.of("bar.com"), auth.domains());
    assertEquals(ImmutableMap.of("token", "someToken"), auth.credentials());
    final Stage0Entry.DockerAuth dockerAuth = (Stage0Entry.DockerAuth) config.stage0().get(1);
    assertEquals(ImmutableList.of("foo.com", "bar.com"), dockerAuth.registries());
    assertEquals("user", dockerAuth.credentials().user());
    assertEquals("password", dockerAuth.credentials().password());
    final Stage0Entry.Paths paths = (Stage0Entry.Paths) config.stage0().get(2);
    assertEquals("/var/lib/rkt", paths.data());
    assertEquals("/usr/lib/rkt", paths.stage1Images());
    final Stage0Entry.Stage1 stage1 = (Stage0Entry.Stage1) config.stage0().get(3);
    assertEquals("coreos.com/rkt/stage1-coreos", stage1.name());
    assertEquals("0.15.0+git", stage1.version());
    assertEquals("/usr/libexec/rkt/stage1-coreos.aci", stage1.location());
  }

  @Test
  public void shouldDeserializeArray() throws IOException {
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
                        + "    \"appNames\": [\n"
                        + "      \"nginx\"\n"
                        + "    ],\n"
                        + "    \"startedAt\": 1497280167\n"
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
                        + "    \"appNames\": [\n"
                        + "      \"nginx\"\n"
                        + "    ],\n"
                        + "    \"startedAt\": 1497112016\n"
                        + "  }\n"
                        + "]";
    final List<Pod> pods = Json.deserialize(json, new TypeReference<List<Pod>>() {
    });
    assertEquals(2, pods.size());
  }

  @Test
  public void shouldDeserializeArrayWithSnakeCase() throws IOException {
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
    final List<Image> images = Json.deserializeSnakeCase(json, new TypeReference<List<Image>>() {
    });
    assertEquals(4, images.size());
  }

  @Test
  public void shouldDeserializeBytes() throws IOException {
    //language=JSON
    final String json = "{\n"
                        + "  \"stage0\": [\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"auth\",\n"
                        + "      \"domains\": [\n"
                        + "        \"bar.com\"\n"
                        + "      ],\n"
                        + "      \"type\": \"oauth\",\n"
                        + "      \"credentials\": {\n"
                        + "        \"token\": \"someToken\"\n"
                        + "      }\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";
    final Config config = Json.deserialize(json.getBytes(), Config.class);
    final Stage0Entry.Auth auth = (Stage0Entry.Auth) config.stage0().get(0);
    assertEquals("oauth", auth.type());
    assertEquals(ImmutableList.of("bar.com"), auth.domains());
    assertEquals(ImmutableMap.of("token", "someToken"), auth.credentials());
  }
  
  @Test
  public void shouldSerialize() throws IOException {
    final Config config = new ConfigBuilder()
        .stage0(new PathsBuilder()
                    .rktVersion("v1")
                    .rktKind("paths")
                    .data("/var/lib/rkt")
                    .stage1Images("/usr/lib/rkt")
                    .build(),
                new Stage1Builder()
                    .rktVersion("v1")
                    .rktKind("stage1")
                    .name("coreos.com/rkt/stage1-coreos")
                    .version("0.15.0+git")
                    .location("/usr/libexec/rkt/stage1-coreos.aci")
                    .build())
        .build();
    final String json = new String(Json.serialize(config));
    assertEquals(config, Json.deserialize(json, Config.class));
  }

  @Test
  public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
                                                InvocationTargetException, InstantiationException {
    Constructor<Json> constructor = Json.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
