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
package io.honnix.rkt.launcher.command;

import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.model.schema.PodManifest;
import io.honnix.rkt.launcher.options.CatManifestOptions;
import io.honnix.rkt.launcher.output.CatManifestOutput;
import io.honnix.rkt.launcher.util.Json;
import io.norberg.automatter.AutoMatter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface CatManifest
    extends CommandWithOptionalArgs<CatManifestOptions, CatManifestOutput> {

  @Override
  Optional<CatManifestOptions> options();

  @Override
  default List<String> asList() {
    return asList("cat-manifest");
  }

  @Override
  default CatManifestOutput parse(final String output) {
    try {
      return CatManifestOutput.builder()
          .podManifest(Json.deserialize(output, PodManifest.class))
          .build();
    } catch (IOException e) {
      throw new RktUnexpectedOutputException("failed parsing JSON output", e);
    }
  }

  static CatManifestBuilder builder() {
    return new CatManifestBuilder();
  }
}
