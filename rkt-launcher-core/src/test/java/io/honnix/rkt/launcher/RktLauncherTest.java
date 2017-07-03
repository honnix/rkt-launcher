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

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.command.Command;
import io.honnix.rkt.launcher.command.CommandWithoutArgs;
import io.honnix.rkt.launcher.command.Daemonizable;
import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.exception.RktLauncherException;
import io.honnix.rkt.launcher.options.GlobalOptions;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.Output;
import io.norberg.automatter.AutoMatter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class RktLauncherTest {

  @AutoMatter
  interface DaemonizedCommand extends Daemonizable, CommandWithoutArgs<Options, Output> {

    @Override
    default Optional<Options> options() {
      return Optional.of(Options.NULL);
    }

    @Override
    default List<String> asList() {
      return asList("mock");
    }

    @Override
    default Output parse(final String output) {
      return Output.NULL;
    }

    static DaemonizedCommandBuilder builder() {
      return new DaemonizedCommandBuilder();
    }
  }

  @Mock
  private ProcessExecutor processExecutor;

  @Mock
  private ProcessResult processResult;

  @Mock
  private Command command;

  @Mock
  private Output output;

  @Before
  public void setUp() throws InterruptedException, TimeoutException, IOException, RktException {
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    when(command.parse(any(ProcessResult.class))).thenReturn(output);
  }

  /**
   * This is purely for coverage.
   */
  @Test
  public void shouldConstruct() {
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .dir("dir")
        .build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    new RktLauncher(rktLauncherConfig);
  }

  @Test
  public void shouldRun() throws Exception {
    when(processExecutor.command(anyList())).thenReturn(processExecutor);
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .dir("dir")
        .build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    assertSame(output, rktLauncher.run(command));
    verify(processExecutor).readOutput(true);
  }

  @Test
  public void shouldRunUsingConfiguredDaemon() throws Exception {
    when(processResult.getExitValue()).thenReturn(0);
    when(processResult.outputUTF8()).thenReturn("output");
    when(processExecutor.command(ImmutableList.of("daemon", "rkt", "--dir=dir", "mock")))
        .thenReturn(processExecutor);
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .dir("dir")
        .build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .daemon(ImmutableList.of("daemon"))
        .globalOptions(globalOptions)
        .build();
    final Command command = DaemonizedCommand.builder().daemonize(true).build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    assertSame(Output.NULL, rktLauncher.run(command));
  }

  @Test
  public void shouldRunUsingDefaultDaemon() throws Exception {
    when(processResult.getExitValue()).thenReturn(0);
    when(processResult.outputUTF8()).thenReturn("output");
    when(processExecutor.command(
        ImmutableList.of("systemd-run", "--slice=machine", "rkt", "--dir=dir", "mock")))
        .thenReturn(processExecutor);
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .dir("dir")
        .build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    final Command command = DaemonizedCommand.builder().daemonize(true).build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    assertSame(Output.NULL, rktLauncher.run(command));
  }

  @Test
  public void shouldNotRunAsDaemon() throws Exception {
    when(processResult.getExitValue()).thenReturn(0);
    when(processResult.outputUTF8()).thenReturn("output");
    when(processExecutor.command(ImmutableList.of("rkt", "--dir=dir", "mock")))
        .thenReturn(processExecutor);
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .dir("dir")
        .build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    final Command command = DaemonizedCommand.builder().daemonize(false).build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    assertSame(Output.NULL, rktLauncher.run(command));
  }

  @Test(expected = RktLauncherException.class)
  public void shouldThrowRktLauncherExceptionWhenIOException() throws Exception {
    when(processExecutor.command(anyList())).thenReturn(processExecutor);
    doThrow(new IOException()).when(processExecutor).executeNoTimeout();
    final GlobalOptions globalOptions = GlobalOptions.builder().build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    rktLauncher.run(command);
  }

  @Test(expected = RktLauncherException.class)
  public void shouldThrowRktLauncherExceptionWhenInterruptedException() throws Exception {
    when(processExecutor.command(anyList())).thenReturn(processExecutor);
    doThrow(new InterruptedException()).when(processExecutor).executeNoTimeout();
    final GlobalOptions globalOptions = GlobalOptions.builder().build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    rktLauncher.run(command);
  }

  @Test(expected = RktException.class)
  public void shouldThrowRktException() throws Exception {
    when(processExecutor.command(anyList())).thenReturn(processExecutor);
    doThrow(RktException.class).when(processExecutor).executeNoTimeout();
    final GlobalOptions globalOptions = GlobalOptions.builder().build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
    rktLauncher.run(command);
  }
}
