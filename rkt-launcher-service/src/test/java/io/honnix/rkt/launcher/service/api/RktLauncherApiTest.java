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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spotify.apollo.Environment;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import io.honnix.rkt.launcher.service.exception.RktLauncherServiceException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RktLauncherApiTest {

  private RktLauncherApi rktLauncherApi;

  @Mock
  private Environment environment;

  @Before
  public void setUp() {
    rktLauncherApi = new RktLauncherApi();
  }

  @Test
  public void shouldCreate() throws ExecutionException, InterruptedException {
    //language=JSON
    final String json = "{\n"
                        + "  \"rkt\": \"/usr/local/bin/rkt\",\n"
                        + "  \"daemon\": [\n"
                        + "    \"systemd-run\",\n"
                        + "    \"--slice=machine\"\n"
                        + "  ],\n"
                        + "  \"globalOptions\": {\n"
                        + "    \"insecureOptions\": [\"image\"]\n"
                        + "  }\n"
                        + "}\n";
    final Config config = mock(Config.class);
    final ConfigValue configValue = mock(ConfigValue.class);
    when(environment.config()).thenReturn(config);
    when(config.getValue("rktLauncher")).thenReturn(configValue);
    when(configValue.render(any())).thenReturn(json);

    final Environment.RoutingEngine routingEngine = mock(Environment.RoutingEngine.class);
    when(environment.routingEngine()).thenReturn(routingEngine);
    when(routingEngine.registerAutoRoute(any())).thenAnswer(invocation -> {
      final Route<AsyncHandler<String>> route = invocation.getArgument(0);
      assertEquals("pong", route.handler().invoke(null).toCompletableFuture().get());
      return routingEngine;
    });
    when(routingEngine.registerRoutes(any())).thenReturn(routingEngine);
    rktLauncherApi.create(environment);
    verify(routingEngine).registerAutoRoute(any());
    verify(routingEngine, times(2)).registerRoutes(any());
  }

  @Test(expected = RktLauncherServiceException.class)
  public void shouldThrowIfInvalidConfig() {
    //language=JSON
    final String json = "{\n"
                        + "  \"rkt\": \"/usr/local/bin/rkt\",\n"
                        + "  \"daemon\": \"systemd-run\",\n"
                        + "  \"globalOptions\": {\n"
                        + "    \"insecureOptions\": [\"image\"]\n"
                        + "  }\n"
                        + "}\n";
    final Config config = mock(Config.class);
    final ConfigValue configValue = mock(ConfigValue.class);
    when(environment.config()).thenReturn(config);
    when(config.getValue("rktLauncher")).thenReturn(configValue);
    when(configValue.render(any())).thenReturn(json);

    rktLauncherApi.create(environment);
  }
}
