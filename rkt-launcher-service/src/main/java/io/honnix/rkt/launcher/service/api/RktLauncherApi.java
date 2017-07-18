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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spotify.apollo.AppInit;
import com.spotify.apollo.Environment;
import com.spotify.apollo.route.Route;
import com.typesafe.config.ConfigRenderOptions;
import io.honnix.rkt.launcher.RktLauncherConfig;
import io.honnix.rkt.launcher.service.exception.RktLauncherServiceException;
import io.honnix.rkt.launcher.util.Json;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class RktLauncherApi implements AppInit {

  private static final int ASYNC_COMMAND_EXECUTOR_THREADS = 4;

  @Override
  public void create(final Environment environment) {
    final String json =
        environment.config().getValue("rktLauncher").render(ConfigRenderOptions.concise());
    final RktLauncherConfig rktLauncherConfig;
    try {
      rktLauncherConfig = Json.deserialize(json, RktLauncherConfig.class);
    } catch (IOException e) {
      throw new RktLauncherServiceException("invalid configuration", e);
    }

    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("async-command-executor-thread-%d")
        .build();
    final ExecutorService asyncCommandExecutorService =
        Executors.newFixedThreadPool(ASYNC_COMMAND_EXECUTOR_THREADS, threadFactory);

    final RktCommandResource rktCommandResource =
        new RktCommandResource(rktLauncherConfig, asyncCommandExecutorService);
    final RktImageCommandResource rktImageCommandResource =
        new RktImageCommandResource(rktLauncherConfig);

    environment.routingEngine()
        .registerAutoRoute(Route.sync("GET", "/ping", rc -> "pong"))
        .registerRoutes(rktCommandResource.routes())
        .registerRoutes(rktImageCommandResource.routes());
  }
}
