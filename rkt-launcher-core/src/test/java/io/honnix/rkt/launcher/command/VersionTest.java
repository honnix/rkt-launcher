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
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.output.VersionOutput;
import org.junit.Before;
import org.junit.Test;

public class VersionTest {

  private Version version;

  @Before
  public void setUp() {
    version = Version.builder().build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "version");
    assertEquals(expected, version.asList());
  }

  @Test
  public void shouldParseOutput() {
    final String output = "rkt Version: 1.25.0\n"
                          + "appc Version: 0.8.10\n"
                          + "Go Version: go1.7.4\n"
                          + "Go OS/Arch: linux/amd64\n"
                          + "Features: -TPM +SDJOURNAL";
    final VersionOutput versionOutput = version.parse(output);
    assertEquals("1.25.0", versionOutput.rktVersion());
    assertEquals("0.8.10", versionOutput.appcVersion());
    assertEquals("go1.7.4", versionOutput.goVersion());
    assertEquals("linux/amd64", versionOutput.goOSArch());
    assertEquals(ImmutableList.of("-TPM", "+SDJOURNAL"), versionOutput.features());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenMissingRktVersion() {
    final String output = "appc Version: 0.8.10\n"
                          + "Go Version: go1.7.4\n"
                          + "Go OS/Arch: linux/amd64\n"
                          + "Features: -TPM +SDJOURNAL";
    version.parse(output);
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenMissingAppcVersion() {
    final String output = "rkt Version: 1.25.0\n"
                          + "Go Version: go1.7.4\n"
                          + "Go OS/Arch: linux/amd64\n"
                          + "Features: -TPM +SDJOURNAL";
    version.parse(output);
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenMissingGoVersion() {
    final String output = "rkt Version: 1.25.0\n"
                          + "appc Version: 0.8.10\n"
                          + "Go OS/Arch: linux/amd64\n"
                          + "Features: -TPM +SDJOURNAL";
    version.parse(output);
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenMissingGoOSArch() {
    final String output = "rkt Version: 1.25.0\n"
                          + "appc Version: 0.8.10\n"
                          + "Go Version: go1.7.4\n"
                          + "Features: -TPM +SDJOURNAL";
    version.parse(output);
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenMissingFeatures() {
    final String output = "rkt Version: 1.25.0\n"
                          + "appc Version: 0.8.10\n"
                          + "Go Version: go1.7.4\n"
                          + "Go OS/Arch: linux/amd64\n";
    version.parse(output);
  }
}
