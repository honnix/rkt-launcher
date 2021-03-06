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
package io.honnix.rkt.launcher.model.schema;

import io.honnix.rkt.launcher.model.schema.type.ACKind;
import io.honnix.rkt.launcher.model.schema.type.Annotation;
import io.honnix.rkt.launcher.model.schema.type.App;
import io.honnix.rkt.launcher.model.schema.type.Dependency;
import io.honnix.rkt.launcher.model.schema.type.Label;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface ImageManifest {

  String acVersion();

  ACKind acKind();

  String name();

  Optional<List<Label>> labels();

  Optional<App> app();

  Optional<List<Annotation>> annotations();

  Optional<List<Dependency>> dependencies();

  Optional<List<String>> pathWhitelist();
}
