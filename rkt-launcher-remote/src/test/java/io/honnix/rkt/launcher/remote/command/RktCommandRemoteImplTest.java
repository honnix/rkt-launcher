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
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.PerImageOptions;
import io.honnix.rkt.launcher.options.PrepareOptions;
import io.honnix.rkt.launcher.options.RunOptions;
import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.options.StatusOptions;
import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.output.CatManifestOutput;
import io.honnix.rkt.launcher.output.ConfigOutput;
import io.honnix.rkt.launcher.output.FetchOutput;
import io.honnix.rkt.launcher.output.GcOutput;
import io.honnix.rkt.launcher.output.ListOutput;
import io.honnix.rkt.launcher.output.PrepareOutput;
import io.honnix.rkt.launcher.output.RmOutput;
import io.honnix.rkt.launcher.output.RunOutput;
import io.honnix.rkt.launcher.output.StatusOutput;
import io.honnix.rkt.launcher.output.StopOutput;
import io.honnix.rkt.launcher.output.VersionOutput;
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
public class RktCommandRemoteImplTest {

  private RktCommandRemote rktCommandRemote;

  @Mock
  private Client client;

  @Before
  public void setUp() throws Exception {
    rktCommandRemote =
        new RktCommandRemoteImpl(URI.create("http://localhost:8080"), client);
    mockStatic(RktCommandHelper.class);
    when(RktCommandHelper.uri(any(), any())).thenCallRealMethod();
    when(RktCommandHelper.uri(any(), any(), any())).thenCallRealMethod();
    when(RktCommandHelper.class, "builder", any(), any()).thenCallRealMethod();
    when(RktCommandHelper.merge(any(), any())).thenCallRealMethod();
  }

  @Test
  public void shouldCallCatManifest() {
    rktCommandRemote.catManifest("id1");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/cat-manifest/id1",
                                 CatManifestOutput.class);
  }

  @Test
  public void shouldCallConfig() {
    rktCommandRemote.config();
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/config",
                                 ConfigOutput.class);
  }

  @Test
  public void shouldCallFetch() {
    final FetchOptions options = FetchOptions.builder()
        .full(true)
        .build();
    rktCommandRemote.fetch(options, "image1", "image2");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/fetch?image=image1&image=image2",
                                 options,
                                 FetchOutput.class);
  }

  @Test
  public void shouldCallFetchWithoutOptions() {
    rktCommandRemote.fetch("image1", "image2");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/fetch?image=image1&image=image2",
                                 FetchOutput.class);
  }

  @Test
  public void shouldCallGc() {
    final GcOptions options = GcOptions.builder()
        .markOnly(true)
        .build();
    rktCommandRemote.gc(options);
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/gc",
                                 options,
                                 GcOutput.class);
  }

  @Test
  public void shouldCallGcWithoutOptions() {
    rktCommandRemote.gc();
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/gc",
                                 GcOutput.class);
  }

  @Test
  public void shouldCallList() {
    rktCommandRemote.list();
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/list",
                                 ListOutput.class);
  }

  @Test
  public void shouldCallPrepare() {
    final PrepareOptions options = PrepareOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .addImagesOption(PerImageOptions.builder().image("docker://mysql").build())
        .build();
    rktCommandRemote.prepare(options);
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/prepare",
                                 options,
                                 PrepareOutput.class);
  }

  @Test
  public void shouldCallRm() {
    rktCommandRemote.rm("id1", "id2");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/rm?id=id1&id=id2",
                                 RmOutput.class);
  }

  @Test
  public void shouldCallRun() {
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .build();
    rktCommandRemote.run(options, true);
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/run?daemonize=true",
                                 options,
                                 RunOutput.class);
  }

  @Test
  public void shouldCallRunPrepared() {
    final RunPreparedOptions options = RunPreparedOptions.builder()
        .mdsRegister(true)
        .build();
    rktCommandRemote.runPrepared(options, "id1", true);
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/run-prepared/id1?daemonize=true",
                                 options,
                                 RunOutput.class);
  }

  @Test
  public void shouldCallRunPreparedWithoutOptions() {
    rktCommandRemote.runPrepared("id1", true);
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/run-prepared/id1?daemonize=true",
                                 RunOutput.class);
  }

  @Test
  public void shouldCallStatus() {
    final StatusOptions options = StatusOptions.builder()
        .waitTillFinish(Duration.ofSeconds(10))
        .build();
    rktCommandRemote.status(options, "id1");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/status/id1",
                                 options,
                                 StatusOutput.class);
  }

  @Test
  public void shouldCallStatusWithoutOptions() {
    rktCommandRemote.status("id1");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/status/id1",
                                 StatusOutput.class);
  }

  @Test
  public void shouldCallStop() {
    final StopOptions options = StopOptions.builder()
        .force(true)
        .build();
    rktCommandRemote.stop(options, "id1", "id2");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/stop?id=id1&id=id2",
                                 options,
                                 StopOutput.class);
  }

  @Test
  public void shouldCallStopWithoutOptions() {
    rktCommandRemote.stop("id1", "id2");
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/stop?id=id1&id=id2",
                                 StopOutput.class);
  }

  @Test
  public void shouldCallVersion() {
    rktCommandRemote.version();
    verifyStatic();
    RktCommandHelper.sendRequest(client,
                                 "http://localhost:8080/api/v0/rkt/version",
                                 VersionOutput.class);
  }
}
