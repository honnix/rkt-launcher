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
import io.honnix.rkt.launcher.model.PullPolicy;
import org.junit.Test;

public class FetchOptionsTest {

  @Test
  public void shouldBuildCorrectList() {
    final FetchOptions fetchOptions = FetchOptions.builder()
        .full(true)
        .pullPolicy(PullPolicy.UPDATE)
        .signature("foo")
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--full=true",
        "--pull-policy=update",
        "--signature=foo");
    assertEquals(expected, fetchOptions.asList());
  }
}
