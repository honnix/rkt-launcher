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
package io.honnix.rkt.launcher.exception;

/**
 * This exception captures exit code from <code>rkt</code> process as well as detailed error
 * message.
 */
public class RktException extends Exception {

  private final int exitCode;

  public RktException(int exitCode, String message) {
    super(message);
    this.exitCode = exitCode;
  }

  /**
   * Get <code>rkt</code> exit code.
   *
   * @return <code>rkt</code> exit code
   */
  public int getExitCode() {
    return exitCode;
  }

  public String toString() {
    return "exit_code: " + getExitCode() + ", message: " + getMessage();
  }
}
