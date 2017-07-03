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
package io.honnix.rkt.launcher.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import com.spotify.apollo.httpservice.HttpService;
import io.honnix.rkt.launcher.service.api.RktLauncherApi;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(HttpService.class)
@RunWith(PowerMockRunner.class)
public class RktLauncherServiceTest {

  @Test
  public void testMain() throws Exception {
    mockStatic(HttpService.class);
    RktLauncherService.main(new String[]{"arg"});
    verifyStatic();
    HttpService.boot(isA(RktLauncherApi.class), eq("rkt-launcher-remote"), eq("arg"));
  }

  @Test
  public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
                                                InvocationTargetException, InstantiationException {
    Constructor<RktLauncherService> constructor = RktLauncherService.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
