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

import org.junit.Test;

public class PerImageOptionsTest {

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldReturnAllValues() {
    assertEquals(2, PerImageOptions.Seccomp.Mode.values().length);
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldParseFromString() {
    assertSame(PerImageOptions.Seccomp.Mode.REMOVE, PerImageOptions.Seccomp.Mode.valueOf("REMOVE"));
  }
}
