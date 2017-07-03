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

import org.junit.Before;
import org.junit.Test;

public class OptionsTest {

  private Options options;

  @Before
  public void setUp() {
    options = new Options() {
    };
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrow() {
    options.asList();
  }

  @Test
  public void shouldJoin() {
    assertEquals("a=b", options.join("a", "b"));
  }
}
