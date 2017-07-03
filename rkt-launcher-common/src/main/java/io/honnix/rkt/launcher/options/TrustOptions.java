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
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface TrustOptions extends Options {

  Optional<Boolean> insecureAllowHttp();

  Optional<String> prefix();

  Optional<Boolean> root();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    insecureAllowHttp().ifPresent(v -> builder.add(
        join("--insecure-allow-http", v)));
    prefix().ifPresent(v -> builder.add(
        join("--prefix", v)));
    root().ifPresent(v -> builder.add(
        join("--root", v)));
    builder.add(
        join("--skip-fingerprint-review", "true"));
    return builder.build();
  }

  static TrustOptionsBuilder builder() {
    return new TrustOptionsBuilder();
  }
}
