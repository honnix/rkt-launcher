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
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface RunPreparedOptions extends Options {

  Optional<List<String>> dns();

  Optional<List<String>> dnsDomain();

  Optional<List<String>> dnsOpt();

  Optional<List<String>> dnsSearch();

  Optional<String> hostname();

  Optional<List<String>> hostsEntry();

  Optional<Boolean> mdsRegister();

  Optional<List<Network>> net();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
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
    return builder.build();
  }

  static RunPreparedOptionsBuilder builder() {
    return new RunPreparedOptionsBuilder();
  }
}
