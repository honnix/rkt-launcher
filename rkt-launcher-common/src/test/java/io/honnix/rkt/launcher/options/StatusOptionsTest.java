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
import java.time.Duration;
import org.junit.Test;

public class StatusOptionsTest {

  @Test
  public void shouldWaitTillFinishForever() {
    final StatusOptions statusOptions = StatusOptions.builder()
        .waitTillFinish(Duration.ofSeconds(-1))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--wait=true",
        "--format=json");
    assertEquals(expected, statusOptions.asList());
  }

  @Test
  public void shouldWaitTillReadyForever() {
    final StatusOptions statusOptions = StatusOptions.builder()
        .waitTillReady(Duration.ofSeconds(-1))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--wait-ready=true",
        "--format=json");
    assertEquals(expected, statusOptions.asList());
  }

  @Test
  public void shouldWaitTillReadyAndFinishForever() {
    final StatusOptions statusOptions = StatusOptions.builder()
        .waitTillReady(Duration.ofSeconds(-1))
        .waitTillFinish(Duration.ofSeconds(-1))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--wait=true",
        "--wait-ready=true",
        "--format=json");
    assertEquals(expected, statusOptions.asList());
  }

  @Test
  public void shouldWaitTillFinish() {
    final StatusOptions statusOptions = StatusOptions.builder()
        .waitTillFinish(Duration.ofSeconds(10))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--wait=0h0m10s",
        "--format=json");
    assertEquals(expected, statusOptions.asList());
  }

  @Test
  public void shouldWaitTillReady() {
    final StatusOptions statusOptions = StatusOptions.builder()
        .waitTillReady(Duration.ofSeconds(10))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--wait-ready=0h0m10s",
        "--format=json");
    assertEquals(expected, statusOptions.asList());
  }

  @Test
  public void shouldWaitTillReadyAndFinish() {
    final StatusOptions statusOptions = StatusOptions.builder()
        .waitTillReady(Duration.ofSeconds(10))
        .waitTillFinish(Duration.ofSeconds(10))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--wait=0h0m10s",
        "--wait-ready=0h0m10s",
        "--format=json");
    assertEquals(expected, statusOptions.asList());
  }
}
