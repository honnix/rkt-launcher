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
package io.honnix.rkt.launcher.service.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.Response;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import okio.ByteString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ApiTest {

  @Mock
  private Route<AsyncHandler<Response<ByteString>>> route1;

  @Mock
  private Route<AsyncHandler<Response<ByteString>>> route2;

  @Test
  public void shouldPrefixRoutes() {
    assertEquals(2, Api.prefixRoutes(ImmutableList.of(route1, route2), Api.Version.V0).count());
    verify(route1).withPrefix("/api/v0");
    verify(route2).withPrefix("/api/v0");
  }

  @Test
  public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
                                                InvocationTargetException, InstantiationException {
    Constructor<Api> constructor = Api.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldReturnAllValues() {
    assertEquals(1, Api.Version.values().length);
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldParseFromString() {
    assertSame(Api.Version.V0, Api.Version.valueOf("V0"));
  }
}
