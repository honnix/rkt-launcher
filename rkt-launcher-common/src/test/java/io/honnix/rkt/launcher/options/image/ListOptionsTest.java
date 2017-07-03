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
package io.honnix.rkt.launcher.options.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class ListOptionsTest {

  @Test
  public void shouldBuildCorrectList() {
    final ListOptions listOptions = ListOptions.builder()
        .order(ListOptions.Order.ASC)
        .sort(ImmutableList.of(ListOptions.Field.ID, ListOptions.Field.NAME, ListOptions.Field.IMPORT_TIME, ListOptions.Field.LAST_USED_TINE, ListOptions.Field.SIZE))
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--order=asc",
        "--sort=id,name,importtime,lastused,size",
        "--format=json");
    assertEquals(expected, listOptions.asList());
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldReturnAllValuesOfField() {
    assertEquals(5, ListOptions.Field.values().length);
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldParseFromStringOfField() {
    assertSame(ListOptions.Field.ID, ListOptions.Field.valueOf("ID"));
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldReturnAllValuesOfOrder() {
    assertEquals(2, ListOptions.Order.values().length);
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldParseFromStringOfOrder() {
    assertSame(ListOptions.Order.ASC, ListOptions.Order.valueOf("ASC"));
  }
}
