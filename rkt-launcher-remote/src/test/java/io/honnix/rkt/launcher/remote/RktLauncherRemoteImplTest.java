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
package io.honnix.rkt.launcher.remote;

import static io.honnix.rkt.launcher.remote.RktLauncherRemote.RktLauncherRemoteBuilder.Scheme.HTTP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.spotify.apollo.Client;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.options.TrustOptions;
import io.honnix.rkt.launcher.remote.command.RktCommandRemote;
import io.honnix.rkt.launcher.remote.command.RktImageCommandRemote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RktLauncherRemoteImplTest {

  private RktLauncherRemote rktLauncherRemote;

  @Mock
  private RktCommandRemote rktCommandRemote;

  @Mock
  private RktImageCommandRemote rktImageCommandRemote;

  @Before
  public void setUp() throws Exception {
    rktLauncherRemote = new RktLauncherRemoteImpl(rktCommandRemote, rktImageCommandRemote);
  }

  /**
   * This is purely for coverage.
   */
  @Test
  public void shouldConstruct() {
    assertNotNull(RktLauncherRemote.builder()
                      .scheme(HTTP)
                      .host("localhost")
                      .port(80)
                      .client(mock(Client.class))
                      .build());
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldReturnAllValues() {
    assertEquals(2, RktLauncherRemote.RktLauncherRemoteBuilder.Scheme.values().length);
  }

  /**
   * This is for jacoco enum coverage limitation.
   * https://github.com/jacoco/jacoco/wiki/FilteringOptions
   */
  @Test
  public void shouldParseFromString() {
    assertSame(RktLauncherRemote.RktLauncherRemoteBuilder.Scheme.HTTPS,
               RktLauncherRemote.RktLauncherRemoteBuilder.Scheme.valueOf("HTTPS"));
  }

  @Test
  public void shouldReturnImageCommandRemote() {
    assertSame(rktImageCommandRemote, rktLauncherRemote.image());
  }

  @Test
  public void shouldProxyCatManifest() {
    rktLauncherRemote.catManifest("id");
    verify(rktCommandRemote).catManifest("id");
  }

  @Test
  public void shouldProxyConfig() {
    rktLauncherRemote.config();
    verify(rktCommandRemote).config();
  }

  @Test
  public void shouldProxyFetch() {
    rktLauncherRemote.fetch(FetchOptions.builder().build(), true, "image1", "image2");
    verify(rktCommandRemote).fetch(FetchOptions.builder().build(), true, "image1", "image2");
  }

  @Test
  public void shouldProxyFetchWithoutOptions() {
    rktLauncherRemote.fetch(false, "image1", "image2");
    verify(rktCommandRemote).fetch(false, "image1", "image2");
  }

  @Test
  public void shouldProxyGc() {
    rktLauncherRemote.gc(null);
    verify(rktCommandRemote).gc(null);
  }

  @Test
  public void shouldProxyGcWithoutOptions() {
    rktLauncherRemote.gc();
    verify(rktCommandRemote).gc();
  }


  @Test
  public void shouldProxyList() {
    rktLauncherRemote.list();
    verify(rktCommandRemote).list();
  }

  @Test
  public void shouldProxyPrepare() {
    rktLauncherRemote.prepare(null, true);
    verify(rktCommandRemote).prepare(null, true);
  }

  @Test
  public void shouldProxyRm() {
    rktLauncherRemote.rm(null, "id1", "id2");
    verify(rktCommandRemote).rm(null, "id1", "id2");
  }

  @Test
  public void shouldProxyRun() {
    rktLauncherRemote.run(null, true);
    verify(rktCommandRemote).run(null, true);
  }

  @Test
  public void shouldProxyRunPrepared() {
    rktLauncherRemote.runPrepared(null, "id", true);
    verify(rktCommandRemote).runPrepared(null, "id", true);
  }

  @Test
  public void shouldProxyRunPreparedWithoutOptions() {
    rktLauncherRemote.runPrepared("id", true);
    verify(rktCommandRemote).runPrepared("id", true);
  }

  @Test
  public void shouldProxyStatus() {
    rktLauncherRemote.status(null, "id");
    verify(rktCommandRemote).status(null, "id");
  }

  @Test
  public void shouldProxyStatusWithoutOptions() {
    rktLauncherRemote.status("id");
    verify(rktCommandRemote).status("id");
  }

  @Test
  public void shouldProxyStop() {
    rktLauncherRemote.stop(StopOptions.builder().build(), "id1", "id2");
    verify(rktCommandRemote).stop(StopOptions.builder().build(), "id1", "id2");
  }

  @Test
  public void shouldProxyStopWithoutOptions() {
    rktLauncherRemote.stop("id1", "id2");
    verify(rktCommandRemote).stop("id1", "id2");
  }

  @Test
  public void shouldProxyTrust() {
    rktLauncherRemote.trust(TrustOptions.builder().build(), "pubkey1", "pubkey2");
    verify(rktCommandRemote).trust(TrustOptions.builder().build(), "pubkey1", "pubkey2");
  }

  @Test
  public void shouldProxyTrustWithoutOptions() {
    rktLauncherRemote.trust("pubkey1", "pubkey2");
    verify(rktCommandRemote).trust("pubkey1", "pubkey2");
  }

  @Test
  public void shouldProxyVersion() {
    rktLauncherRemote.version();
    verify(rktCommandRemote).version();
  }
}
