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
package io.honnix.rkt.launcher.remote.command;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.spotify.apollo.Client;
import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.output.image.CatManifestOutput;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.ListOutput;
import io.honnix.rkt.launcher.output.image.RmOutput;
import java.net.URI;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(RktCommandHelper.class)
@RunWith(PowerMockRunner.class)
public class RktImageCommandRemoteImplTest {

  private RktImageCommandRemote rktImageCommandRemote;

  @Mock
  private Client client;

  @Before
  public void setUp() throws Exception {
    rktImageCommandRemote =
        new RktImageCommandRemoteImpl(URI.create("http://localhost:8080"), client);
    mockStatic(RktCommandHelper.class);
    when(RktCommandHelper.uri(any(), any())).thenCallRealMethod();
    when(RktCommandHelper.uri(any(), any(), any())).thenCallRealMethod();
    when(RktCommandHelper.class, "builder", any(), any()).thenCallRealMethod();
    when(RktCommandHelper.merge(any())).thenCallRealMethod();
  }

  @Test
  public void shouldCallCatManifest() {
    rktImageCommandRemote.catManifest("id1");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/image/cat-manifest/id1",
                                 CatManifestOutput.class);
  }

  @Test
  public void shouldCallGc() {
    final GcOptions options = GcOptions.builder()
        .gracePeriod(Duration.ofSeconds(10))
        .build();
    rktImageCommandRemote.gc(options);
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/image/gc",
                                 options,
                                 GcOutput.class);
  }

  @Test
  public void shouldCallGcWithoutOptions() {
    rktImageCommandRemote.gc();
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/image/gc",
                                 GcOutput.class);
  }

  @Test
  public void shouldCallList() {
    rktImageCommandRemote.list();
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/image/list",
                                 ListOutput.class);
  }

  @Test
  public void shouldCallRm() {
    rktImageCommandRemote.rm("id1");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/image/rm?id=id1",
                                 RmOutput.class);
  }
}
