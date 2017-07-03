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

import static io.honnix.rkt.launcher.util.Time.durationToString;

import com.google.common.collect.ImmutableList;
import io.norberg.automatter.AutoMatter;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface GcOptions extends Options {

  Optional<Duration> expirePrepared();

  Optional<Duration> gracePeriod();

  Optional<Boolean> markOnly();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    expirePrepared().ifPresent(v -> builder.add(
        join("--expire-prepared", durationToString(v))));
    gracePeriod().ifPresent(v -> builder.add(
        join("--grace-period", durationToString(v))));
    markOnly().ifPresent(v -> builder.add(
        join("--mark-only", v)));
    return builder.build();
  }

  static GcOptionsBuilder builder() {
    return new GcOptionsBuilder();
  }
}
