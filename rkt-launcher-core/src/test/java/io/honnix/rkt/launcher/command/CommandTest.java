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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.options.Options;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.zeroturnaround.exec.ProcessOutput;
import org.zeroturnaround.exec.ProcessResult;

public class CommandTest {

  private Command command;

  @Before
  public void setUp() {
    command = new Command() {
      @Override
      public Optional<Options> options() {
        return null;
      }

      @Override
      public List<String> asList() {
        return null;
      }
    };
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldParseOutput() throws RktException {
    command.parse(new ProcessResult(0, new ProcessOutput("output".getBytes())));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldParseEmptyOutput() throws RktException {
    command.parse(new ProcessResult(0, new ProcessOutput(new byte[]{})));
  }

  @Test
  public void shouldThrowRktException() {
    try {
      command.parse(new ProcessResult(254, new ProcessOutput("output".getBytes())));
      fail();
    } catch (RktException e) {
      assertEquals("output", e.getMessage());
      assertEquals(254, e.getExitCode());
    }
  }
}
