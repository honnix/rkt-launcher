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

import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.Output;
import java.util.List;
import java.util.Optional;
import org.zeroturnaround.exec.ProcessResult;

public interface Command<T extends Options, S extends Output> {

  Optional<T> options();

  List<String> asList();

  default S parse(final ProcessResult processResult) throws RktException {
    if (processResult.getExitValue() == 0) {
      return parse(processResult.outputUTF8());
    } else {
      throw new RktException(processResult.getExitValue(), processResult.outputUTF8());
    }
  }

  default S parse(final String output) {
    throw new UnsupportedOperationException();
  }
}
