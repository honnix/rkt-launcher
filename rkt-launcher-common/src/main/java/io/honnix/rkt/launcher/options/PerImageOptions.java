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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.model.Capability;
import io.honnix.rkt.launcher.model.PullPolicy;
import io.honnix.rkt.launcher.model.schema.type.Annotation;
import io.honnix.rkt.launcher.model.schema.type.Environment;
import io.honnix.rkt.launcher.model.schema.type.ExposedPort;
import io.honnix.rkt.launcher.model.schema.type.Label;
import io.norberg.automatter.AutoMatter;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * This class is not bound to a specific command.
 */
@AutoMatter
public interface PerImageOptions extends Options {

  @AutoMatter
  interface Seccomp {

    enum Mode {
      RETAIN,
      REMOVE
    }

    Mode mode();

    String errno();

    List<String> syscalls();

    default String toOption() {
      return "mode=" + mode().name().toLowerCase()
             + ",errno=" + errno() + ","
             + Joiner.on(",").join(syscalls());
    }

    static SeccompBuilder builder() {
      return new SeccompBuilder();
    }
  }

  String image();

  Optional<List<String>> imageOptions();

  Optional<List<String>> imageArgs();

  Optional<List<Capability>> capsRemove();

  Optional<List<Capability>> capsRetain();

  Optional<String> cpu();

  Optional<Integer> cpuShares();

  Optional<List<Environment>> environment();

  Optional<String> exec();

  Optional<String> group();

  Optional<Boolean> inheritEnv();

  Optional<String> memory();

  Optional<String> name();

  Optional<Boolean> noOverlay();

  Optional<Integer> oomScoreAdj();

  Optional<List<ExposedPort>> port();

  Optional<Boolean> privateUsers();

  Optional<PullPolicy> pullPolicy();

  Optional<Boolean> readonlyRootfs();

  Optional<List<Seccomp>> seccomp();

  Optional<String> signature();

  Optional<String> stage1FromDir();

  Optional<String> stage1Hash();

  Optional<String> stage1Name();

  Optional<String> stage1Path();

  Optional<URL> stage1Url();

  Optional<List<Integer>> supplementaryGIDs();

  Optional<String> user();

  Optional<List<Annotation>> userAnnotation();

  Optional<List<Label>> userLabel();

  Optional<String> workingDir();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();

    builder.add(image());

    capsRemove().ifPresent(v -> builder.add(
        join("--caps-remove", Joiner.on(",").join(v))));
    capsRetain().ifPresent(v -> builder.add(
        join("--caps-retain", Joiner.on(",").join(v))));
    cpu().ifPresent(v -> builder.add(
        join("--cpu", v)));
    cpuShares().ifPresent(v -> builder.add(
        join("--cpu-shares", v.toString())));
    environment().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--environment", x.toOption())).iterator()));
    exec().ifPresent(v -> builder.add(
        join("--exec", v)));
    group().ifPresent(v -> builder.add(
        join("--group", v)));
    inheritEnv().ifPresent(v -> builder.add(
        join("--inherit-env", v)));
    memory().ifPresent(v -> builder.add(
        join("--memory", v)));
    name().ifPresent(v -> builder.add(
        join("--name", v)));
    noOverlay().ifPresent(v -> builder.add(
        join("--no-overlay", v)));
    oomScoreAdj().ifPresent(v -> builder.add(
        join("--oom-score-adj", v)));
    port().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--port", x.toOption())).iterator()));
    privateUsers().ifPresent(v -> builder.add(
        join("--private-users", v)));
    pullPolicy().ifPresent(v -> builder.add(
        join("--pull-policy", v.name().toLowerCase())));
    readonlyRootfs().ifPresent(v -> builder.add(
        join("--readonly-rootfs", v)));
    seccomp().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--seccomp", x.toOption())).iterator()));
    signature().ifPresent(v -> builder.add(
        join("--signature", v)));
    stage1FromDir().ifPresent(v -> builder.add(
        join("--stage1-from-dir", v)));
    stage1Hash().ifPresent(v -> builder.add(
        join("--stage1-hash", v)));
    stage1Name().ifPresent(v -> builder.add(
        join("--stage1-name", v)));
    stage1Path().ifPresent(v -> builder.add(
        join("--stage1-path", v)));
    stage1Url().ifPresent(v -> builder.add(
        join("--stage1-url", v)));
    supplementaryGIDs().ifPresent(v -> builder.add(
        join("--supplementary-gids", Joiner.on(",").join(v))));
    user().ifPresent(v -> builder.add(
        join("--user", v)));
    userAnnotation().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--user-annotation", x.toOption())).iterator()));
    userLabel().ifPresent(v -> builder.addAll(
        v.stream().map(x -> join("--user-label", x.toOption())).iterator()));
    workingDir().ifPresent(v -> builder.add(
        join("--working-dir", v)));

    if (imageOptions().isPresent() || imageArgs().isPresent()) {
      builder.add("--");
      imageOptions().ifPresent(builder::addAll);
      imageArgs().ifPresent(builder::addAll);
    }

    return builder.build();
  }

  static PerImageOptionsBuilder builder() {
    return new PerImageOptionsBuilder();
  }
}
