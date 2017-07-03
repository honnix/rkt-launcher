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
package io.honnix.rkt.launcher.remote.command;

import static io.honnix.rkt.launcher.remote.command.RktCommandHelper.sendRequest;
import static io.honnix.rkt.launcher.remote.command.RktCommandHelper.uri;

import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.Client;
import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.output.image.CatManifestOutput;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.ListOutput;
import io.honnix.rkt.launcher.output.image.RmOutput;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class RktImageCommandRemoteImpl implements RktImageCommandRemote {

  private static final String RKT_LAUNCHER_SERVICE_IMAGE_PATH = "image";

  private final URI apiHost;

  private final Client client;

  public RktImageCommandRemoteImpl(final URI apiHost, final Client client) {
    this.apiHost = Objects.requireNonNull(apiHost);
    this.client = Objects.requireNonNull(client);
  }

  @Override
  public CompletionStage<CatManifestOutput> catManifest(final String id) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper
                                            .uri(apiHost, RKT_LAUNCHER_SERVICE_IMAGE_PATH, "cat-manifest", id),
                                        CatManifestOutput.class);
  }

  @Override
  public CompletionStage<GcOutput> gc(final GcOptions options) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper
                                            .uri(apiHost, RKT_LAUNCHER_SERVICE_IMAGE_PATH, "gc"),
                                        options,
                                        GcOutput.class);
  }

  @Override
  public CompletionStage<GcOutput> gc() {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper
                                            .uri(apiHost, RKT_LAUNCHER_SERVICE_IMAGE_PATH, "gc"),
                                        GcOutput.class);
  }

  @Override
  public CompletionStage<ListOutput> list() {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper
                                            .uri(apiHost, RKT_LAUNCHER_SERVICE_IMAGE_PATH, "list"),
                                        ListOutput.class);
  }

  @Override
  public CompletionStage<RmOutput> rm(final String id, final String... ids) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost,
                                                             ImmutableMap.of("id", RktCommandHelper
                                                                 .merge(id, ids)),
                                                             RKT_LAUNCHER_SERVICE_IMAGE_PATH,
                                                             "rm"),
                                        RmOutput.class);
  }
}
