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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.Client;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.PrepareOptions;
import io.honnix.rkt.launcher.options.RunOptions;
import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.options.StatusOptions;
import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.options.TrustOptions;
import io.honnix.rkt.launcher.output.CatManifestOutput;
import io.honnix.rkt.launcher.output.ConfigOutput;
import io.honnix.rkt.launcher.output.FetchOutput;
import io.honnix.rkt.launcher.output.GcOutput;
import io.honnix.rkt.launcher.output.ListOutput;
import io.honnix.rkt.launcher.output.PrepareOutput;
import io.honnix.rkt.launcher.output.RmOutput;
import io.honnix.rkt.launcher.output.RunOutput;
import io.honnix.rkt.launcher.output.StatusOutput;
import io.honnix.rkt.launcher.output.StopOutput;
import io.honnix.rkt.launcher.output.TrustOutput;
import io.honnix.rkt.launcher.output.VersionOutput;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class RktCommandRemoteImpl implements RktCommandRemote {

  private final URI apiHost;

  private final Client client;

  public RktCommandRemoteImpl(final URI apiHost, final Client client) {
    this.apiHost = Objects.requireNonNull(apiHost);
    this.client = Objects.requireNonNull(client);
  }

  @Override
  public CompletionStage<CatManifestOutput> catManifest(final String id) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "cat-manifest", id),
                                        CatManifestOutput.class);
  }

  @Override
  public CompletionStage<ConfigOutput> config() {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "config"),
                                        ConfigOutput.class);
  }

  @Override
  public CompletionStage<FetchOutput> fetch(final FetchOptions options,
                                            final boolean async,
                                            final String image,
                                            final String... images) {
    return RktCommandHelper.sendRequest(
        client,
        RktCommandHelper.uri(apiHost,
                             ImmutableMap.of("async", ImmutableList.of(Boolean.toString(async)),
                                             "image", RktCommandHelper.merge(image, images)),
                             "fetch"),
        options,
        FetchOutput.class);
  }

  @Override
  public CompletionStage<FetchOutput> fetch(final boolean async,
                                            final String image,
                                            String... images) {
    return RktCommandHelper.sendRequest(
        client,
        RktCommandHelper.uri(apiHost,
                             ImmutableMap.of("async", ImmutableList.of(Boolean.toString(async)),
                                             "image", RktCommandHelper.merge(image, images)),
                             "fetch"),
        FetchOutput.class);
  }

  @Override
  public CompletionStage<GcOutput> gc(final GcOptions options) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "gc"),
                                        options,
                                        GcOutput.class);
  }

  @Override
  public CompletionStage<GcOutput> gc() {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "gc"),
                                        GcOutput.class);
  }

  @Override
  public CompletionStage<ListOutput> list() {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "list"),
                                        ListOutput.class);
  }

  @Override
  public CompletionStage<PrepareOutput> prepare(final PrepareOptions options, final boolean async) {
    return RktCommandHelper.sendRequest(
        client,
        RktCommandHelper.uri(apiHost,
                             ImmutableMap.of("async", ImmutableList.of(Boolean.toString(async))),
                             "prepare"),
        options,
        PrepareOutput.class);
  }

  @Override
  public CompletionStage<RmOutput> rm(final String id, final String... ids) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost,
                                                             ImmutableMap.of("id", RktCommandHelper
                                                                 .merge(id, ids)),
                                                             "rm"),
                                        RmOutput.class);
  }

  @Override
  public CompletionStage<RunOutput> run(final RunOptions options, boolean daemonize) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, ImmutableMap
                                                                 .of("daemonize",
                                                                     ImmutableList.of(Boolean.toString(daemonize))),
                                                             "run"),
                                        options,
                                        RunOutput.class);
  }

  @Override
  public CompletionStage<RunOutput> runPrepared(final RunPreparedOptions options, final String id,
                                                boolean daemonize) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, ImmutableMap
                                                                 .of("daemonize",
                                                                     ImmutableList.of(Boolean.toString(daemonize))),
                                                             "run-prepared", id),
                                        options,
                                        RunOutput.class);
  }

  @Override
  public CompletionStage<RunOutput> runPrepared(String id, boolean daemonize) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, ImmutableMap
                                                                 .of("daemonize",
                                                                     ImmutableList.of(Boolean.toString(daemonize))),
                                                             "run-prepared", id),
                                        RunOutput.class);
  }

  @Override
  public CompletionStage<StatusOutput> status(final StatusOptions options, final String id) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "status", id),
                                        options,
                                        StatusOutput.class);
  }

  @Override
  public CompletionStage<StatusOutput> status(String id) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "status", id),
                                        StatusOutput.class);
  }

  @Override
  public CompletionStage<StopOutput> stop(final StopOptions options, final String id,
                                          final String... ids) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost,
                                                             ImmutableMap.of("id", RktCommandHelper
                                                                 .merge(id, ids)),
                                                             "stop"),
                                        options,
                                        StopOutput.class);
  }

  @Override
  public CompletionStage<StopOutput> stop(final String id, final String... ids) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost,
                                                             ImmutableMap.of("id", RktCommandHelper
                                                                 .merge(id, ids)),
                                                             "stop"),
                                        StopOutput.class);
  }

  @Override
  public CompletionStage<TrustOutput> trust(TrustOptions options, String... pubkeys) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost,
                                                             ImmutableMap.of("pubkey", ImmutableList
                                                                 .copyOf(pubkeys)),
                                                             "trust"),
                                        options,
                                        TrustOutput.class);
  }

  @Override
  public CompletionStage<TrustOutput> trust(String... pubkeys) {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost,
                                                             ImmutableMap.of("pubkey", ImmutableList
                                                                 .copyOf(pubkeys)),
                                                             "trust"),
                                        TrustOutput.class);
  }

  @Override
  public CompletionStage<VersionOutput> version() {
    return RktCommandHelper.sendRequest(client,
                                        RktCommandHelper.uri(apiHost, "version"),
                                        VersionOutput.class);
  }
}
