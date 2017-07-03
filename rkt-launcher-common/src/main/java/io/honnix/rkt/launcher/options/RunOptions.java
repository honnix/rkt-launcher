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
package io.honnix.rkt.launcher.options;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.model.Network;
import io.honnix.rkt.launcher.model.schema.type.Environment;
import io.honnix.rkt.launcher.model.schema.type.Mount;
import io.honnix.rkt.launcher.model.schema.type.Volume;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface RunOptions extends Options {

  Optional<List<Volume>> volume();

  Optional<List<Mount>> mount();

  Optional<String> podManifest();

  Optional<List<Environment>> setEnv();

  Optional<String> setEnvFile();

  Optional<String> uuidFileSave();

  Optional<List<String>> dns();

  Optional<List<String>> dnsDomain();

  Optional<List<String>> dnsOpt();

  Optional<List<String>> dnsSearch();

  Optional<String> hostname();

  Optional<List<String>> hostsEntry();

  Optional<Boolean> mdsRegister();

  Optional<List<Network>> net();

  List<PerImageOptions> imagesOptions();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    volume().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--volume", x.toOption())).iterator()));
    mount().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--mount", x.toOption())).iterator()));
    podManifest().ifPresent(v -> builder.add(
        join("--pod-manifest", v)));
    setEnv().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--set-env", x.toOption())).iterator()));
    setEnvFile().ifPresent(v -> builder.add(
        join("--set-env-file", v)));
    uuidFileSave().ifPresent(v -> builder.add(
        join("--uuid-file-save", v)));
    dns().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--dns", x)).iterator()));
    dnsDomain().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--dns-domain", x)).iterator()));
    dnsOpt().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--dns-opt", x)).iterator()));
    dnsSearch().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--dns-search", x)).iterator()));
    hostname().ifPresent(v -> builder.add(
        join("--hostname", v)));
    hostsEntry().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--hosts-entry", x)).iterator()));
    mdsRegister().ifPresent(v -> builder.add(
        join("--mds-register", v)));
    net().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--net", x.toOption())).iterator()));
    imagesOptions().forEach(x -> builder.addAll(
        x.asList()).add("---"));
    return builder.build();
  }

  static RunOptionsBuilder builder() {
    return new RunOptionsBuilder();
  }
}
