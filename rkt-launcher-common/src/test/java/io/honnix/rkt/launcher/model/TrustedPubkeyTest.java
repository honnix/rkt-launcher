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
package io.honnix.rkt.launcher.model;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

public class TrustedPubkeyTest {

  @Test
  public void shouldBuild() {
    final TrustedPubkey trustedPubkey = TrustedPubkey.builder()
        .key("key")
        .prefix("prefix")
        .location("location")
        .build();
    assertEquals("key", trustedPubkey.key());
    assertEquals("prefix", trustedPubkey.prefix());
    assertEquals("location", trustedPubkey.location());
  }
}
