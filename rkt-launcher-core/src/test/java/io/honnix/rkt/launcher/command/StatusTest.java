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
import io.honnix.rkt.launcher.model.Pod;
import io.honnix.rkt.launcher.options.StatusOptions;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;

public class StatusTest {

  private Status status;

  @Before
  public void setUp() {
    status = Status.builder()
        .options(StatusOptions.builder()
                     .waitTillReady(Duration.ofSeconds(10))
                     .build())
        .addArg("uuid")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "status",
        "--wait-ready=0h0m10s",
        "--format=json",
        "uuid");
    assertEquals(expected, status.asList());
  }

  @Test
  public void shouldParseOutput() {
    //language=JSON
    final String json = "{\n"
                        + "  \"name\": \"1e4bb8f2-ea9c-4c5d-8c20-66ef94a2c74b\",\n"
                        + "  \"state\": \"running\",\n"
                        + "  \"networks\": [\n"
                        + "    {\n"
                        + "      \"netName\": \"default\",\n"
                        + "      \"netConf\": \"net/99-default.conf\",\n"
                        + "      \"pluginPath\": \"stage1/rootfs/usr/lib/rkt/plugins/net/ptp\",\n"
                        + "      \"ifName\": \"eth0\",\n"
                        + "      \"ip\": \"172.16.28.4\",\n"
                        + "      \"args\": \"\",\n"
                        + "      \"mask\": \"255.255.255.0\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"app_names\": [\n"
                        + "    \"nginx\"\n"
                        + "  ],\n"
                        + "  \"started_at\": 1497280167\n"
                        + "}";

    final Pod pod = status.parse(json).status();
    assertEquals("1e4bb8f2-ea9c-4c5d-8c20-66ef94a2c74b", pod.name());
    assertEquals("running", pod.state());
    assertTrue(pod.networks().isPresent());
    assertEquals(1, pod.networks().get().size());
    assertTrue(pod.appNames().isPresent());
    assertEquals("nginx", pod.appNames().get().get(0));
    assertTrue(pod.startedAt().isPresent());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenInvalidJson() {
    status.parse("[not_a_json");
  }
}
