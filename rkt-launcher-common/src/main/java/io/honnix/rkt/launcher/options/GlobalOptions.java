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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import io.norberg.automatter.AutoMatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface GlobalOptions extends Options {

  Optional<String> dir();

  enum InsecureOption {
    NONE("none"),
    IMAGE("image"),
    TLS("tls"),
    ONDISK("ondisk"),
    HTTP("http"),
    PUBKEY("pubkey"),
    CAPABILITIES("capabilities"),
    PATHS("paths"),
    SECCOMP("seccomp"),
    ALL_FETCH("all-fetch"),
    ALL_RUN("all-run"),
    ALL("all");

    private final String feature;

    InsecureOption(final String feature) {
      this.feature = feature;
    }

    @JsonValue
    @Override
    public String toString() {
      return feature;
    }

    @JsonCreator
    public static InsecureOption fromString(final String feature) {
      return Arrays.stream(InsecureOption.values()).filter(x -> x.feature.equals(feature))
          .findFirst().orElse(null);
    }
  }

  Optional<List<InsecureOption>> insecureOptions();

  Optional<String> localConfig();

  Optional<String> systemConfig();

  Optional<Boolean> trustKeysFromHttps();

  Optional<String> userConfig();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    dir().ifPresent(v -> builder.add(
        join("--dir", v)));
    insecureOptions().ifPresent(v -> builder.add(
        join("--insecure-options", Joiner.on(",").join(v))));
    localConfig().ifPresent(v -> builder.add(
        join("--local-config", v)));
    systemConfig().ifPresent(v -> builder.add(
        join("--system-config", v)));
    trustKeysFromHttps().ifPresent(v -> builder.add(
        join("--trust-keys-from-https", v)));
    userConfig().ifPresent(v -> builder.add(
        join("--user-config", v)));
    return builder.build();
  }

  static GlobalOptionsBuilder builder() {
    return new GlobalOptionsBuilder();
  }
}
