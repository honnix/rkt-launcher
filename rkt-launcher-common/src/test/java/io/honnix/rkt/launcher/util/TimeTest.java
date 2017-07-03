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

import static io.honnix.rkt.launcher.util.Time.durationToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.Duration;
import org.junit.Test;

public class TimeTest {

  @Test
  public void shouldConvertFull() {
    final String duration = durationToString(Duration.ofHours(10).plusMinutes(10).plusSeconds(10));
    assertEquals("10h10m10s", duration);
  }

  @Test
  public void shouldConvertNoHour() {
    final String duration = durationToString(Duration.ofMinutes(10).plusSeconds(10));
    assertEquals("0h10m10s", duration);
  }

  @Test
  public void shouldConvertNoHourNoMinite() {
    final String duration = durationToString(Duration.ofSeconds(10));
    assertEquals("0h0m10s", duration);
  }

  @Test
  public void shouldConvertZero() {
    final String duration = durationToString(Duration.ofSeconds(0));
    assertEquals("0h0m0s", duration);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowForNegativeValue() {
    durationToString(Duration.ofSeconds(-10));
  }

  @Test
  public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
                                                InvocationTargetException, InstantiationException {
    Constructor<Time> constructor = Time.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
