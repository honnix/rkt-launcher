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
package io.honnix.rkt.launcher.output;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class OutputTest {

  @Test
  public void shouldCreateBuilders() {
    assertNotNull(io.honnix.rkt.launcher.output.image.CatManifestOutput.builder());
    assertNotNull(io.honnix.rkt.launcher.output.image.GcOutput.builder());
    assertNotNull(io.honnix.rkt.launcher.output.image.ListOutput.builder());
    assertNotNull(io.honnix.rkt.launcher.output.image.RmOutput.builder());
    assertNotNull(CatManifestOutput.builder());
    assertNotNull(ConfigOutput.builder());
    assertNotNull(FetchOutput.builder());
    assertNotNull(GcOutput.builder());
    assertNotNull(ListOutput.builder());
    assertSame(NullOutput.class, Output.NULL.getClass());
    assertNotNull(PrepareOutput.builder());
    assertNotNull(RmOutput.builder());
    assertNotNull(RunOutput.builder());
    assertNotNull(StatusOutput.builder());
    assertNotNull(StopOutput.builder());
    assertNotNull(TrustOutput.builder());
    assertNotNull(VersionOutput.builder());
  }
}
