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
import static org.junit.Assert.assertSame;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class GlobalOptionsTest {

  @Test
  public void shouldBuildCorrectList() {
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .dir("dir")
        .insecureOptions(ImmutableList.of(
            GlobalOptions.InsecureOption.ALL,
            GlobalOptions.InsecureOption.ALL_FETCH,
            GlobalOptions.InsecureOption.ALL_RUN,
            GlobalOptions.InsecureOption.CAPABILITIES,
            GlobalOptions.InsecureOption.HTTP,
            GlobalOptions.InsecureOption.IMAGE,
            GlobalOptions.InsecureOption.NONE,
            GlobalOptions.InsecureOption.ONDISK,
            GlobalOptions.InsecureOption.PATHS,
            GlobalOptions.InsecureOption.PUBKEY,
            GlobalOptions.InsecureOption.SECCOMP,
            GlobalOptions.InsecureOption.TLS))
        .localConfig("localConfig")
        .systemConfig("systemConfig")
        .trustKeysFromHttps(true)
        .userConfig("userConfig")
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--dir=dir",
        "--insecure-options=all,all-fetch,all-run,capabilities,http,image,none,ondisk,paths,pubkey,seccomp,tls",
        "--local-config=localConfig",
        "--system-config=systemConfig",
        "--trust-keys-from-https=true",
        "--user-config=userConfig");
    assertEquals(expected, globalOptions.asList());
  }

  @Test
  public void shouldReturnCorrectEnum() {
    assertEquals(GlobalOptions.InsecureOption.IMAGE,
                 GlobalOptions.InsecureOption.fromString("image"));
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldParseFromStringOfField() {
    assertSame(GlobalOptions.InsecureOption.IMAGE, GlobalOptions.InsecureOption.valueOf("IMAGE"));
  }
}
