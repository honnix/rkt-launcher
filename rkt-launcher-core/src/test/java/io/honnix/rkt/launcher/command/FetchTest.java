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
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.output.FetchOutput;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class FetchTest {

  private Fetch fetch;

  @Before
  public void setUp() {
    fetch = Fetch.builder()
        .options(FetchOptions.builder()
                     .full(true)
                     .build())
        .addArg("image1")
        .addArg("image2")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "fetch",
        "--full=true",
        "image1", "image2");
    assertEquals(expected, fetch.asList());
  }

  @Test
  public void shouldParseOutput() {
    final String output =
        "image: keys already exist for prefix \"coreos.com/etcd\", not fetching again\n"
        + "Downloading signature: [=======================================] 819 B/819 B\n"
        + "Downloading ACI: [=============================================] 3.7 MB/3.7 MB\n"
        + "image: signature verified:\n"
        + "  CoreOS ACI Builder <release@coreos.com>\n"
        + "sha512-fa1cb92dc276b0f9bedf87981e61ecde93cc16432d2441f23aa006a42bb873df";
    final FetchOutput fetchOutput = fetch.parse(output);
    assertEquals(Optional.of("CoreOS ACI Builder <release@coreos.com>"), fetchOutput.signature());
    assertEquals("sha512-fa1cb92dc276b0f9bedf87981e61ecde93cc16432d2441f23aa006a42bb873df",
                 fetchOutput.hash());
  }

  @Test
  public void shouldParseOutputWithoutSignature() {
    final String output =
        "image: keys already exist for prefix \"coreos.com/etcd\", not fetching again\n"
        + "Downloading signature: [=======================================] 819 B/819 B\n"
        + "Downloading ACI: [=============================================] 3.7 MB/3.7 MB\n"
        + "sha512-fa1cb92dc276b0f9bedf87981e61ecde93cc16432d2441f23aa006a42bb873df";
    final FetchOutput fetchOutput = fetch.parse(output);
    assertEquals(Optional.empty(), fetchOutput.signature());
    assertEquals("sha512-fa1cb92dc276b0f9bedf87981e61ecde93cc16432d2441f23aa006a42bb873df",
                 fetchOutput.hash());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenNoHash() {
    final String output =
        "image: keys already exist for prefix \"coreos.com/etcd\", not fetching again\n"
        + "Downloading signature: [=======================================] 819 B/819 B\n"
        + "Downloading ACI: [=============================================] 3.7 MB/3.7 MB\n"
        + "image: signature verified:\n"
        + "  CoreOS ACI Builder <release@coreos.com>\n";
    fetch.parse(output);
  }
}
