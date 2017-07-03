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
package io.honnix.rkt.launcher.options;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.model.Network;
import org.junit.Test;

public class RunPreparedOptionsTest {

  @Test
  public void shouldBuildCorrectList() {
    final RunPreparedOptions runPreparedOptions = RunPreparedOptions.builder()
        .dns(ImmutableList.of("8.8.8.8", "8.8.8.4"))
        .dnsDomain(ImmutableList.of("foo.com", "bar.com"))
        .dnsOpt(ImmutableList.of("opt1", "opt2"))
        .dnsSearch(ImmutableList.of("foo.com", "bar.com"))
        .hostname("foobar")
        .hostsEntry(ImmutableList.of("127.0.0.1 localhost"))
        .mdsRegister(true)
        .net(ImmutableList.of(
            Network.DEFAULT,
            Network.builder()
                .name("foo")
                .args(ImmutableList.of(
                    Network.Argument.builder()
                        .name("IP")
                        .value("192.168.1.1")
                        .build(),
                    Network.Argument.builder()
                        .name("MASK")
                        .value("255.255.255.0")
                        .build()))
                .build()))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--dns=8.8.8.8",
        "--dns=8.8.8.4",
        "--dns-domain=foo.com",
        "--dns-domain=bar.com",
        "--dns-opt=opt1",
        "--dns-opt=opt2",
        "--dns-search=foo.com",
        "--dns-search=bar.com",
        "--hostname=foobar",
        "--hosts-entry=127.0.0.1 localhost",
        "--mds-register=true",
        "--net=default",
        "--net=foo:IP=192.168.1.1;MASK=255.255.255.0");
    assertEquals(expected, runPreparedOptions.asList());
  }
}
