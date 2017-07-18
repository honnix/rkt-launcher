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
import java.util.concurrent.CompletionStage;

public interface RktCommandRemote {

  CompletionStage<CatManifestOutput> catManifest(final String id);

  CompletionStage<ConfigOutput> config();

  CompletionStage<FetchOutput> fetch(final FetchOptions options, final boolean async,
                                     final String image,
                                     final String... images);

  CompletionStage<FetchOutput> fetch(final boolean async,
                                     final String image,
                                     final String... images);

  CompletionStage<GcOutput> gc(final GcOptions options);

  CompletionStage<GcOutput> gc();

  CompletionStage<ListOutput> list();

  CompletionStage<PrepareOutput> prepare(final PrepareOptions options, final boolean async);

  CompletionStage<RmOutput> rm(final String id, final String... ids);

  CompletionStage<RunOutput> run(final RunOptions options, boolean daemonize);

  CompletionStage<RunOutput> runPrepared(final RunPreparedOptions options, final String id,
                                         boolean daemonize);

  CompletionStage<RunOutput> runPrepared(final String id, boolean daemonize);

  CompletionStage<StatusOutput> status(final StatusOptions options, final String id);

  CompletionStage<StatusOutput> status(final String id);

  CompletionStage<StopOutput> stop(final StopOptions options, final String id, final String... ids);

  CompletionStage<StopOutput> stop(final String id, final String... ids);

  CompletionStage<VersionOutput> version();
}
