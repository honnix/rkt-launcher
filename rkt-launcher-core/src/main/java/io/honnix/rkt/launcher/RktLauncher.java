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
package io.honnix.rkt.launcher;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.command.Command;
import io.honnix.rkt.launcher.command.Daemonizable;
import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.exception.RktLauncherException;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.Output;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.zeroturnaround.exec.ProcessExecutor;

/**
 * The rkt launcher forking <code>rkt</code> process.
 */
public class RktLauncher {

  private static final String DEFAULT_RKT = "rkt";

  private static final List<String> DEFAULT_DAEMON =
      ImmutableList.of("systemd-run", "--slice=machine");

  private static final Supplier<ProcessExecutor> DEFAULT_PROCESS_EXECUTOR_SUPPLIER =
      ProcessExecutor::new;

  private final RktLauncherConfig rktLauncherConfig;

  private final Supplier<ProcessExecutor> processExecutorSupplier;

  public RktLauncher(final RktLauncherConfig rktLauncherConfig) {
    this(rktLauncherConfig, DEFAULT_PROCESS_EXECUTOR_SUPPLIER);
  }

  @VisibleForTesting
  RktLauncher(final RktLauncherConfig rktLauncherConfig,
              final Supplier<ProcessExecutor> processExecutorSupplier) {
    this.rktLauncherConfig = Objects.requireNonNull(rktLauncherConfig);
    this.processExecutorSupplier = Objects.requireNonNull(processExecutorSupplier);
  }

  /**
   * Run <code>rkt</code> command.
   *
   * @param command the <code>rkt</code> command
   * @param <T>     the type of the command options
   * @param <S>     the type of the command output
   * @return the command output
   * @throws RktLauncherException if failed to fork <code>rkt</code> process for any reason
   * @throws RktException         if <code>rkt</code> returns exit code other than <code>0</code>
   */
  public <T extends Options, S extends Output> S run(final Command<T, S> command)
      throws RktLauncherException, RktException {
    final ProcessExecutor processExecutor = processExecutorSupplier.get();
    processExecutor.readOutput(true);

    try {
      return command.parse(
          processExecutor.command(buildCompleteCommand(command)).executeNoTimeout());
    } catch (IOException | InterruptedException e) {
      throw new RktLauncherException("failed executing command " + command.getClass(), e);
    }
  }

  private <T extends Options, S extends Output> List<String> buildCompleteCommand(
      final Command<T, S> command) {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    if (command instanceof Daemonizable && ((Daemonizable) command).daemonize()) {
      builder.addAll(rktLauncherConfig.daemon().orElse(DEFAULT_DAEMON));
    }
    return builder
        .add(rktLauncherConfig.rkt().orElse(DEFAULT_RKT))
        .addAll(rktLauncherConfig.globalOptions().map(Options::asList).orElse(ImmutableList.of()))
        .addAll(command.asList())
        .build();
  }
}
