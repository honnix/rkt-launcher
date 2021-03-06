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

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.Options;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class CommandWithoutArgsTest {

  @Test
  public void shouldBuildCorrectListWithoutArgsOptions() throws Exception {
    final CommandWithoutArgs commandWithoutArgs = new CommandWithoutArgs() {
      @Override
      public Optional<Options> options() {
        return Optional.empty();
      }

      @Override
      public List<String> asList() {
        return null;
      }
    };
    final ImmutableList<String> expected = ImmutableList.of("foo");
    assertEquals(expected, commandWithoutArgs.asList("foo"));
  }

  @Test
  public void shouldBuildCorrectListWithOptions() throws Exception {
    final CommandWithoutArgs commandWithoutArgs = new CommandWithoutArgs() {
      @Override
      public Optional<Options> options() {
        return Optional.of(new Options() {
          @Override
          public List<String> asList() {
            return ImmutableList.of("--option-a", "--option-b");
          }
        });
      }

      @Override
      public List<String> asList() {
        return null;
      }
    };
    final ImmutableList<String> expected = ImmutableList.of(
        "foo",
        "--option-a",
        "--option-b"
    );
    assertEquals(expected, commandWithoutArgs.asList("foo"));
  }
}
