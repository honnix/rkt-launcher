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
package io.honnix.rkt.launcher.remote;

import com.spotify.apollo.Client;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.PrepareOptions;
import io.honnix.rkt.launcher.options.RunOptions;
import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.options.StatusOptions;
import io.honnix.rkt.launcher.options.StopOptions;
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
import io.honnix.rkt.launcher.output.VersionOutput;
import io.honnix.rkt.launcher.remote.command.RktCommandRemote;
import io.honnix.rkt.launcher.remote.command.RktCommandRemoteImpl;
import io.honnix.rkt.launcher.remote.command.RktImageCommandRemote;
import io.honnix.rkt.launcher.remote.command.RktImageCommandRemoteImpl;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

class RktLauncherRemoteImpl implements RktLauncherRemote {

  private final RktCommandRemote rktCommandRemote;

  private final RktImageCommandRemote rktImageCommandRemote;

  RktLauncherRemoteImpl(final URI uri, final Client client) {
    rktCommandRemote = new RktCommandRemoteImpl(uri, client);
    rktImageCommandRemote = new RktImageCommandRemoteImpl(uri, client);
  }

  RktLauncherRemoteImpl(final RktCommandRemote rktCommandRemote,
                        final RktImageCommandRemote rktImageCommandRemote) {
    this.rktCommandRemote = Objects.requireNonNull(rktCommandRemote);
    this.rktImageCommandRemote = Objects.requireNonNull(rktImageCommandRemote);
  }

  @Override
  public RktImageCommandRemote image() {
    return rktImageCommandRemote;
  }

  @Override
  public CompletionStage<CatManifestOutput> catManifest(final String id) {
    return rktCommandRemote.catManifest(id);
  }

  @Override
  public CompletionStage<ConfigOutput> config() {
    return rktCommandRemote.config();
  }

  @Override
  public CompletionStage<FetchOutput> fetch(final FetchOptions options, final String image,
                                            final String... images) {
    return rktCommandRemote.fetch(options, image, images);
  }

  @Override
  public CompletionStage<FetchOutput> fetch(final String image, final String... images) {
    return rktCommandRemote.fetch(image, images);
  }

  @Override
  public CompletionStage<GcOutput> gc(final GcOptions options) {
    return rktCommandRemote.gc(options);
  }

  @Override
  public CompletionStage<GcOutput> gc() {
    return rktCommandRemote.gc();
  }

  @Override
  public CompletionStage<ListOutput> list() {
    return rktCommandRemote.list();
  }

  @Override
  public CompletionStage<PrepareOutput> prepare(final PrepareOptions options) {
    return rktCommandRemote.prepare(options);
  }

  @Override
  public CompletionStage<RmOutput> rm(final String id, final String... ids) {
    return rktCommandRemote.rm(id, ids);
  }

  @Override
  public CompletionStage<RunOutput> run(final RunOptions options, boolean daemonize) {
    return rktCommandRemote.run(options, daemonize);
  }

  @Override
  public CompletionStage<RunOutput> runPrepared(final RunPreparedOptions options, final String id,
                                                boolean daemonize) {
    return rktCommandRemote.runPrepared(options, id, daemonize);
  }

  @Override
  public CompletionStage<RunOutput> runPrepared(String id, boolean daemonize) {
    return rktCommandRemote.runPrepared(id, daemonize);
  }

  @Override
  public CompletionStage<StatusOutput> status(final StatusOptions options, final String id) {
    return rktCommandRemote.status(options, id);
  }

  @Override
  public CompletionStage<StatusOutput> status(String id) {
    return rktCommandRemote.status(id);
  }

  @Override
  public CompletionStage<StopOutput> stop(final StopOptions options, final String id,
                                          final String... ids) {
    return rktCommandRemote.stop(options, id, ids);
  }

  @Override
  public CompletionStage<StopOutput> stop(final String id, final String... ids) {
    return rktCommandRemote.stop(id, ids);
  }

  @Override
  public CompletionStage<VersionOutput> version() {
    return rktCommandRemote.version();
  }
}
