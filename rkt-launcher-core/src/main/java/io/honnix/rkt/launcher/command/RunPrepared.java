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

import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.output.RunOutput;
import io.honnix.rkt.launcher.output.RunOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface RunPrepared extends Daemonizable, CommandWithArgs<RunPreparedOptions, RunOutput> {

  @Override
  Optional<RunPreparedOptions> options();

  @Override
  default List<String> asList() {
    return asList("run-prepared");
  }

  @Override
  default RunOutput parse(final String output) {
    final RunOutputBuilder runOutputBuilder = RunOutput.builder();

    if (daemonize()) {
      final Pattern runningPattern = Pattern.compile("Running as unit (.+)\\.");

      final Matcher runningMatcher = runningPattern.matcher(output);
      while (runningMatcher.find()) {
        runOutputBuilder.service(runningMatcher.group(1));
      }
    } else {
      runOutputBuilder.service("NA");
    }

    return runOutputBuilder.build();
  }

  static RunPreparedBuilder builder() {
    return new RunPreparedBuilder();
  }
}
