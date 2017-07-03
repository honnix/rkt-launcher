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

import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.output.image.CatManifestOutput;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.ListOutput;
import io.honnix.rkt.launcher.output.image.RmOutput;
import java.util.concurrent.CompletionStage;

public interface RktImageCommandRemote {

  CompletionStage<CatManifestOutput> catManifest(final String id);

  CompletionStage<GcOutput> gc(final GcOptions options);

  CompletionStage<GcOutput> gc();

  CompletionStage<ListOutput> list();

  CompletionStage<RmOutput> rm(final String id, final String... ids);
}
