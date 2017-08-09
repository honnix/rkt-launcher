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
import io.honnix.rkt.launcher.options.TrustOptions;
import io.honnix.rkt.launcher.output.TrustOutput;
import org.junit.Before;
import org.junit.Test;

public class TrustTest {

  private Trust trust;

  @Before
  public void setUp() {
    trust = Trust.builder()
        .options(TrustOptions.builder()
                     .root(true)
                     .build())
        .args(ImmutableList.of("arg1", "arg2"))
        .build();
  }

  @Test
  public void shouldBuildCorrectList() {
    final ImmutableList<String> expected = ImmutableList.of(
        "trust",
        "--root=true",
        "--skip-fingerprint-review=true",
        "arg1", "arg2");
    assertEquals(expected, trust.asList());
  }

  @Test
  public void shouldParseOutputForRoot() {
    final String output = "pubkey: prefix: \"\"\n"
                          + "key: \"test.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"\" without fingerprint review.\n"
                          + "Added root key at \"/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    final TrustOutput trustOutput = trust.parse(output);
    assertEquals(1, trustOutput.trustedPubkeys().size());
    assertEquals("", trustOutput.trustedPubkeys().get(0).prefix());
    assertEquals("test.key", trustOutput.trustedPubkeys().get(0).key());
    assertEquals("/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9",
                 trustOutput.trustedPubkeys().get(0).location());
  }

  @Test
  public void shouldParseOutputForSpecifiedPrefix() {
    final String output = "pubkey: prefix: \"example.com/foo\"\n"
                          + "key: \"test.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"example.com/foo\" without fingerprint review.\n"
                          + "Added key for prefix \"example.com/foo\" at \"/etc/rkt/trustedkeys/prefix.d/example.com/foo/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    final TrustOutput trustOutput = trust.parse(output);
    assertEquals(1, trustOutput.trustedPubkeys().size());
    assertEquals("example.com/foo", trustOutput.trustedPubkeys().get(0).prefix());
    assertEquals("test.key", trustOutput.trustedPubkeys().get(0).key());
    assertEquals(
        "/etc/rkt/trustedkeys/prefix.d/example.com/foo/33671532095f91c5cb267b35882bfd6b87cfc5e9",
        trustOutput.trustedPubkeys().get(0).location());
  }

  @Test
  public void shouldParseOutputForSpecifiedPrefixMultiple() {
    final String output = "pubkey: prefix: \"example.com/foo\"\n"
                          + "key: \"test1.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"example.com/foo\" without fingerprint review.\n"
                          + "Added key for prefix \"example.com/foo\" at \"/etc/rkt/trustedkeys/prefix.d/example.com/foo/33671532095f91c5cb267b35882bfd6b87cfc5e9\"\n"
                          + "pubkey: prefix: \"example.com/bar\"\n"
                          + "key: \"test2.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"example.com/foo\" without fingerprint review.\n"
                          + "Added key for prefix \"example.com/foo\" at \"/etc/rkt/trustedkeys/prefix.d/example.com/bar/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    final TrustOutput trustOutput = trust.parse(output);
    assertEquals(2, trustOutput.trustedPubkeys().size());
    assertEquals("example.com/foo", trustOutput.trustedPubkeys().get(0).prefix());
    assertEquals("test1.key", trustOutput.trustedPubkeys().get(0).key());
    assertEquals(
        "/etc/rkt/trustedkeys/prefix.d/example.com/foo/33671532095f91c5cb267b35882bfd6b87cfc5e9",
        trustOutput.trustedPubkeys().get(0).location());
    assertEquals("example.com/bar", trustOutput.trustedPubkeys().get(1).prefix());
    assertEquals("test2.key", trustOutput.trustedPubkeys().get(1).key());
    assertEquals(
        "/etc/rkt/trustedkeys/prefix.d/example.com/bar/33671532095f91c5cb267b35882bfd6b87cfc5e9",
        trustOutput.trustedPubkeys().get(1).location());
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenNoPrefix() {
    final String output = "pubkey: foo: \"\"\n"
                          + "key: \"test.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"\" without fingerprint review.\n"
                          + "Added root key at \"/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    trust.parse(output);
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenNoKey() {
    final String output = "pubkey: prefix: \"\"\n"
                          + "bar: \"test.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"\" without fingerprint review.\n"
                          + "Added root key at \"/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    trust.parse(output);
  }

  @Test(expected = RktUnexpectedOutputException.class)
  public void shouldThrowWhenNoLocation() {
    final String output = "pubkey: prefix: \"\"\n"
                          + "key: \"test.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"\" without fingerprint review.\n"
                          + "Foobar at \"/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    trust.parse(output);
  }
}
