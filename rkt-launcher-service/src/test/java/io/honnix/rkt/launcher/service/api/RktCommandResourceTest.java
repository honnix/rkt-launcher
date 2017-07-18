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
package io.honnix.rkt.launcher.service.api;

import static com.spotify.apollo.test.unit.ResponseMatchers.hasStatus;
import static com.spotify.apollo.test.unit.StatusTypeMatchers.belongsToFamily;
import static com.spotify.apollo.test.unit.StatusTypeMatchers.withCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.Environment;
import com.spotify.apollo.Response;
import com.spotify.apollo.StatusType;
import io.honnix.rkt.launcher.RktLauncher;
import io.honnix.rkt.launcher.RktLauncherConfig;
import io.honnix.rkt.launcher.command.CatManifest;
import io.honnix.rkt.launcher.command.Config;
import io.honnix.rkt.launcher.command.Fetch;
import io.honnix.rkt.launcher.command.Gc;
import io.honnix.rkt.launcher.command.List;
import io.honnix.rkt.launcher.command.Prepare;
import io.honnix.rkt.launcher.command.Rm;
import io.honnix.rkt.launcher.command.Run;
import io.honnix.rkt.launcher.command.RunPrepared;
import io.honnix.rkt.launcher.command.Status;
import io.honnix.rkt.launcher.command.Stop;
import io.honnix.rkt.launcher.command.Version;
import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.exception.RktLauncherException;
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.model.PodBuilder;
import io.honnix.rkt.launcher.model.PullPolicy;
import io.honnix.rkt.launcher.model.config.ConfigBuilder;
import io.honnix.rkt.launcher.model.config.PathsBuilder;
import io.honnix.rkt.launcher.model.config.Stage1Builder;
import io.honnix.rkt.launcher.model.schema.PodManifestBuilder;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.ListOptions;
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
import io.honnix.rkt.launcher.util.Json;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Test;

public class RktCommandResourceTest extends VersionedApiTest {

  private static final String DEFAULT_HTTP_METHOD = "POST";

  private RktLauncher rktLauncher;

  private ExecutorService executorService;

  public RktCommandResourceTest(final Api.Version version) {
    super("/rkt", version, "rkt-command-resource-test");
  }

  @Override
  protected void init(final Environment environment) {
    rktLauncher = mock(RktLauncher.class);
    executorService = spy(Executors.newCachedThreadPool());
    final Function<RktLauncherConfig, RktLauncher> rktLauncherFactory =
        (rktLauncherConfig) -> rktLauncher;
    environment.routingEngine()
        .registerRoutes(new RktCommandResource(RktLauncherConfig.builder().build(),
                                               rktLauncherFactory,
                                               executorService).routes());
  }

  @Test
  public void shouldReturnServerError() throws Exception {
    sinceVersion(Api.Version.V0);
    final CatManifest catManifest = CatManifest.builder()
        .args(ImmutableList.of("123"))
        .build();
    when(rktLauncher.run(catManifest))
        .thenThrow(new RktLauncherException("error", new IOException()));
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/cat-manifest/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SERVER_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("RktLauncherException"));
  }

  @Test
  public void shouldReturnServerErrorForUnexpectedOutput() throws Exception {
    sinceVersion(Api.Version.V0);
    final CatManifest catManifest = CatManifest.builder()
        .args(ImmutableList.of("123"))
        .build();
    when(rktLauncher.run(catManifest))
        .thenThrow(new RktUnexpectedOutputException("forced error"));
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/cat-manifest/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SERVER_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("forced error"));
  }

  @Test
  public void shouldReturnClientError() throws Exception {
    sinceVersion(Api.Version.V0);
    final CatManifest catManifest = CatManifest.builder()
        .args(ImmutableList.of("123"))
        .build();
    when(rktLauncher.run(catManifest)).thenThrow(new RktException(254, "error"));
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/cat-manifest/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
    assertEquals("exit_code: 254, message: error", response.status().reasonPhrase());
  }

  @Test
  public void shouldRunCatManifest() throws Exception {
    sinceVersion(Api.Version.V0);
    final CatManifest catManifest = CatManifest.builder()
        .args(ImmutableList.of("123"))
        .build();
    final CatManifestOutput catManifestOutput = CatManifestOutput.builder()
        .podManifest(new PodManifestBuilder()
                         .acKind(ACKind.POD_MANIFEST)
                         .acVersion("1.0.0")
                         .build())
        .build();
    when(rktLauncher.run(catManifest)).thenReturn(catManifestOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/cat-manifest/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    Assert.assertEquals(catManifestOutput,
                        Json.deserialize(response.payload().get().toByteArray(),
                                         CatManifestOutput.class));
  }

  @Test
  public void shouldRunConfig() throws Exception {
    sinceVersion(Api.Version.V0);
    final ConfigOutput configOutput = ConfigOutput.builder()
        .config(new ConfigBuilder()
                    .stage0(new PathsBuilder()
                                .rktVersion("v1")
                                .rktKind("paths")
                                .data("/var/lib/rkt")
                                .stage1Images("/usr/lib/rkt")
                                .build(),
                            new Stage1Builder()
                                .rktVersion("v1")
                                .rktKind("stage1")
                                .name("coreos.com/rkt/stage1-coreos")
                                .version("0.15.0+git")
                                .location("/usr/libexec/rkt/stage1-coreos.aci")
                                .build())
                    .build())
        .build();
    when(rktLauncher.run(Config.COMMAND)).thenReturn(configOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/config")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(configOutput,
                 Json.deserialize(response.payload().get().toByteArray(), ConfigOutput.class));
  }

  @Test
  public void shouldRunFetchWithPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final FetchOptions options = FetchOptions.builder()
        .full(true)
        .pullPolicy(PullPolicy.UPDATE)
        .build();
    final Fetch fetch = Fetch.builder()
        .options(options)
        .addArg("image1")
        .addArg("image2")
        .build();
    final FetchOutput fetchOutput = FetchOutput.builder()
        .hash("hash")
        .signature("sig")
        .build();
    when(rktLauncher.run(fetch)).thenReturn(fetchOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?image=image1&image=image2"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(fetchOutput,
                 Json.deserialize(response.payload().get().toByteArray(), FetchOutput.class));
  }

  @Test
  public void shouldRunFetchWithoutPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Fetch fetch = Fetch.builder()
        .addArg("image1")
        .addArg("image2")
        .build();
    final FetchOutput fetchOutput = FetchOutput.builder()
        .hash("hash")
        .signature("sig")
        .build();
    when(rktLauncher.run(fetch)).thenReturn(fetchOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?image=image1&image=image2")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(fetchOutput,
                 Json.deserialize(response.payload().get().toByteArray(), FetchOutput.class));
  }

  @Test
  public void shouldRunFetchWithEmptyPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Fetch fetch = Fetch.builder()
        .addArg("image1")
        .addArg("image2")
        .build();
    final FetchOutput fetchOutput = FetchOutput.builder()
        .hash("hash")
        .signature("sig")
        .build();
    when(rktLauncher.run(fetch)).thenReturn(fetchOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?image=image1&image=image2"), ByteString.of()));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(fetchOutput,
                 Json.deserialize(response.payload().get().toByteArray(), FetchOutput.class));
  }

  @Test
  public void shouldNotRunFetchWithInvalidPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?image=image1&image=image2"),
                              ByteString.of("this is payload".getBytes())));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
  }

  @Test
  public void shouldNotRunFetchMissingImage() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/fetch")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("Missing"));
  }

  @Test
  public void shouldRunFetchAsync() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?async=true&image=image1&image=image2"), ByteString.of()));
    assertThat(response, hasStatus(withCode(com.spotify.apollo.Status.ACCEPTED)));
    assertFalse(response.payload().isPresent());
    verify(executorService).submit(any(Runnable.class));
  }

  @Test
  public void shouldRunFetchAsyncThrowRktLauncherException() throws Exception {
    sinceVersion(Api.Version.V0);
    final Fetch fetch = Fetch.builder()
        .addArg("image1")
        .addArg("image2")
        .build();
    when(rktLauncher.run(fetch))
        .thenThrow(new RktLauncherException("error", new IOException()));
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?async=true&image=image1&image=image2"), ByteString.of()));
    assertThat(response, hasStatus(withCode(com.spotify.apollo.Status.ACCEPTED)));
    assertFalse(response.payload().isPresent());
    verify(executorService).submit(any(Runnable.class));
  }

  @Test
  public void shouldRunFetchAsyncThrowRktException() throws Exception {
    sinceVersion(Api.Version.V0);
    final Fetch fetch = Fetch.builder()
        .addArg("image1")
        .addArg("image2")
        .build();
    when(rktLauncher.run(fetch)).thenThrow(new RktException(254, "error"));
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?async=true&image=image1&image=image2"), ByteString.of()));
    assertThat(response, hasStatus(withCode(com.spotify.apollo.Status.ACCEPTED)));
    assertFalse(response.payload().isPresent());
    verify(executorService).submit(any(Runnable.class));
  }

  @Test
  public void shouldRunFetchAsyncThrowRktUnexpectedOutputException() throws Exception {
    sinceVersion(Api.Version.V0);
    final Fetch fetch = Fetch.builder()
        .addArg("image1")
        .addArg("image2")
        .build();
    when(rktLauncher.run(fetch)).thenThrow(new RktUnexpectedOutputException("forced error"));
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path(
            "/fetch?async=true&image=image1&image=image2"), ByteString.of()));
    assertThat(response, hasStatus(withCode(com.spotify.apollo.Status.ACCEPTED)));
    assertFalse(response.payload().isPresent());
    verify(executorService).submit(any(Runnable.class));
  }

  @Test
  public void shouldRunGcWithPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final GcOptions options = GcOptions.builder()
        .markOnly(true)
        .build();
    final Gc gc = Gc.builder()
        .options(options)
        .build();
    final GcOutput gcOutput = GcOutput.builder()
        .removed("removed1", "removed2")
        .unremoved("unremoved1", "unremoved2")
        .build();
    when(rktLauncher.run(gc)).thenReturn(gcOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/gc"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(gcOutput,
                 Json.deserialize(response.payload().get().toByteArray(), GcOutput.class));
  }

  @Test
  public void shouldRunGcWithoutPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Gc gc = Gc.builder()
        .build();
    final GcOutput gcOutput = GcOutput.builder()
        .removed("removed1", "removed2")
        .unremoved("unremoved1", "unremoved2")
        .build();
    when(rktLauncher.run(gc)).thenReturn(gcOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/gc")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(gcOutput,
                 Json.deserialize(response.payload().get().toByteArray(), GcOutput.class));
  }

  @Test
  public void shouldNotRunGcWithInvalidPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/gc"),
                              ByteString.of("this is payload".getBytes())));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
  }

  @Test
  public void shouldRunList()
      throws Exception {
    sinceVersion(Api.Version.V0);
    final List list = List.builder()
        .options(ListOptions.builder().build())
        .build();
    final ListOutput listOutput = ListOutput.builder()
        .pods(new PodBuilder()
                  .name("nginx1")
                  .state("running")
                  .build(),
              new PodBuilder()
                  .name("nginx2")
                  .state("running")
                  .build())
        .build();
    when(rktLauncher.run(list)).thenReturn(listOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/list")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(listOutput,
                 Json.deserialize(response.payload().get().toByteArray(), ListOutput.class));
  }

  @Test
  public void shouldRunPrepare() throws Exception {
    sinceVersion(Api.Version.V0);
    final PrepareOptions options = PrepareOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .addImagesOption(PerImageOptions.builder().image("docker://mysql").build())
        .build();
    final Prepare prepare = Prepare.builder()
        .options(options)
        .build();
    final PrepareOutput prepareOutput = PrepareOutput.builder()
        .prepared("123")
        .build();
    when(rktLauncher.run(prepare)).thenReturn(prepareOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/prepare"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(prepareOutput,
                 Json.deserialize(response.payload().get().toByteArray(), PrepareOutput.class));
  }

  @Test
  public void shouldRunAsync() throws Exception {
    sinceVersion(Api.Version.V0);
    final PrepareOptions options = PrepareOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .addImagesOption(PerImageOptions.builder().image("docker://mysql").build())
        .build();
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/prepare?async=true"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(withCode(com.spotify.apollo.Status.ACCEPTED)));
    assertFalse(response.payload().isPresent());
    verify(executorService).submit(any(Runnable.class));
  }

  @Test
  public void shouldRunRm() throws Exception {
    sinceVersion(Api.Version.V0);
    final Rm rm = Rm.builder()
        .args(ImmutableList.of("123"))
        .build();
    final RmOutput rmOutput = RmOutput.builder()
        .removed("123")
        .build();
    when(rktLauncher.run(rm)).thenReturn(rmOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/rm/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(rmOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RmOutput.class));
  }

  @Test
  public void shouldRunRmMultiple() throws Exception {
    sinceVersion(Api.Version.V0);
    final Rm rm = Rm.builder()
        .args(ImmutableList.of("123", "345"))
        .build();
    final RmOutput rmOutput = RmOutput.builder()
        .removed("123", "345")
        .build();
    when(rktLauncher.run(rm)).thenReturn(rmOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/rm?id=123&id=345")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(rmOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RmOutput.class));
  }


  @Test
  public void shouldNotRunRmMissingId() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/rm")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("Missing"));
  }

  @Test
  public void shouldRunRun() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .build();
    final Run run = Run.builder()
        .options(options)
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("NA")
        .build();
    when(rktLauncher.run(run)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run?daemonize=false"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunDefault() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .build();
    final Run run = Run.builder()
        .options(options)
        .daemonize(true)
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("run-r69ece8681fbc4e7787e639a78b141599.service")
        .build();
    when(rktLauncher.run(run)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunAsDaemon() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .build();
    final Run run = Run.builder()
        .options(options)
        .daemonize(true)
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("run-r69ece8681fbc4e7787e639a78b141599.service")
        .build();
    when(rktLauncher.run(run)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run?daemonize=true"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldNotRunWithUUIDFileSave() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .uuidFileSave("file")
        .build();
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run?daemonize=true"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("UUID"));
  }

  @Test
  public void shouldRunRunPreparedWithPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunPreparedOptions options = RunPreparedOptions.builder()
        .mdsRegister(true)
        .build();
    final RunPrepared runPrepared = RunPrepared.builder()
        .options(options)
        .addArg("123")
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("NA")
        .build();
    when(rktLauncher.run(runPrepared)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123?daemonize=false"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunPreparedWithPayloadAsDaemon() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunPreparedOptions options = RunPreparedOptions.builder()
        .mdsRegister(true)
        .build();
    final RunPrepared runPrepared = RunPrepared.builder()
        .options(options)
        .daemonize(true)
        .addArg("123")
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("run-r69ece8681fbc4e7787e639a78b141599.service")
        .build();
    when(rktLauncher.run(runPrepared)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123?daemonize=true"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunPreparedWithPayloadDefault() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunPreparedOptions options = RunPreparedOptions.builder()
        .mdsRegister(true)
        .build();
    final RunPrepared runPrepared = RunPrepared.builder()
        .options(options)
        .daemonize(true)
        .addArg("123")
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("run-r69ece8681fbc4e7787e639a78b141599.service")
        .build();
    when(rktLauncher.run(runPrepared)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunPreparedWithoutPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunPrepared runPrepared = RunPrepared.builder()
        .addArg("123")
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("NA")
        .build();
    when(rktLauncher.run(runPrepared)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123?daemonize=false")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunPreparedWithoutPayloadAsDaemon() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunPrepared runPrepared = RunPrepared.builder()
        .daemonize(true)
        .addArg("123")
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("run-r69ece8681fbc4e7787e639a78b141599.service")
        .build();
    when(rktLauncher.run(runPrepared)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123?daemonize=true")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldRunRunPreparedWithoutPayloadDefault() throws Exception {
    sinceVersion(Api.Version.V0);
    final RunPrepared runPrepared = RunPrepared.builder()
        .daemonize(true)
        .addArg("123")
        .build();
    final RunOutput runOutput = RunOutput.builder()
        .service("run-r69ece8681fbc4e7787e639a78b141599.service")
        .build();
    when(rktLauncher.run(runPrepared)).thenReturn(runOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(runOutput,
                 Json.deserialize(response.payload().get().toByteArray(), RunOutput.class));
  }

  @Test
  public void shouldNotRunRunPreparedWithInvalidPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/run-prepared/123"),
                              ByteString.of("this is payload".getBytes())));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
  }

  @Test
  public void shouldRunStatusWithPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final StatusOptions options = StatusOptions.builder()
        .waitTillFinish(Duration.ofSeconds(10))
        .build();
    final Status status =
        Status.builder()
            .options(options)
            .addArg("123")
            .build();
    final StatusOutput statusOutput = StatusOutput.builder()
        .status(new PodBuilder()
                    .name("nginx")
                    .state("running")
                    .build())
        .build();
    when(rktLauncher.run(status)).thenReturn(statusOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/status/123"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(statusOutput,
                 Json.deserialize(response.payload().get().toByteArray(), StatusOutput.class));
  }

  @Test
  public void shouldRunStatusWithoutPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final StatusOptions options = StatusOptions.builder().build();
    final Status status =
        Status.builder()
            .options(options)
            .addArg("123")
            .build();
    final StatusOutput statusOutput = StatusOutput.builder()
        .status(new PodBuilder()
                    .name("nginx")
                    .state("running")
                    .build())
        .build();
    when(rktLauncher.run(status)).thenReturn(statusOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/status/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(statusOutput,
                 Json.deserialize(response.payload().get().toByteArray(), StatusOutput.class));
  }

  @Test
  public void shouldNotRunStatusWithInvalidPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/status/123"),
                              ByteString.of("this is payload".getBytes())));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
  }

  @Test
  public void shouldRunStopWithPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final StopOptions options = StopOptions.builder()
        .force(true)
        .build();
    final Stop stop = Stop.builder()
        .options(options)
        .args(ImmutableList.of("123"))
        .build();
    final StopOutput stopOutput = StopOutput.builder()
        .stopped("123")
        .build();
    when(rktLauncher.run(stop)).thenReturn(stopOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop/123"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(stopOutput,
                 Json.deserialize(response.payload().get().toByteArray(), StopOutput.class));
  }

  @Test
  public void shouldRunStopWithPayloadMultiple() throws Exception {
    sinceVersion(Api.Version.V0);
    final StopOptions options = StopOptions.builder()
        .force(true)
        .build();
    final Stop stop = Stop.builder()
        .options(options)
        .args(ImmutableList.of("123", "345"))
        .build();
    final StopOutput stopOutput = StopOutput.builder()
        .stopped("123", "345")
        .build();
    when(rktLauncher.run(stop)).thenReturn(stopOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop?id=123&id=345"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(stopOutput,
                 Json.deserialize(response.payload().get().toByteArray(), StopOutput.class));
  }

  @Test
  public void shouldNotRunStopMissingId() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
  }

  @Test
  public void shouldNotRunStopWithPayloadWithUUIDFile() throws Exception {
    sinceVersion(Api.Version.V0);
    final StopOptions options = StopOptions.builder()
        .force(true)
        .uuidFile("file")
        .build();
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop/123"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("UUID"));
  }

  @Test
  public void shouldNotRunStopWithPayloadMultipleWithUUIDFile() throws Exception {
    sinceVersion(Api.Version.V0);
    final StopOptions options = StopOptions.builder()
        .force(true)
        .uuidFile("file")
        .build();
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop?id=123&id=345"),
                              ByteString.of(Json.serialize(options))));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
    assertTrue(response.status().reasonPhrase().contains("UUID"));
  }

  @Test
  public void shouldRunStopWithoutPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Stop stop = Stop.builder()
        .args(ImmutableList.of("123"))
        .build();
    final StopOutput stopOutput = StopOutput.builder()
        .stopped("123")
        .build();
    when(rktLauncher.run(stop)).thenReturn(stopOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(stopOutput,
                 Json.deserialize(response.payload().get().toByteArray(), StopOutput.class));
  }

  @Test
  public void shouldRunStopWithoutPayloadMultiple() throws Exception {
    sinceVersion(Api.Version.V0);
    final Stop stop = Stop.builder()
        .args(ImmutableList.of("123", "345"))
        .build();
    final StopOutput stopOutput = StopOutput.builder()
        .stopped("123", "345")
        .build();
    when(rktLauncher.run(stop)).thenReturn(stopOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop?id=123&id=345")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(stopOutput,
                 Json.deserialize(response.payload().get().toByteArray(), StopOutput.class));
  }

  @Test
  public void shouldNotRunStopWithInvalidPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/stop/123"),
                              ByteString.of("this is payload".getBytes())));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.CLIENT_ERROR)));
  }

  @Test
  public void shouldRunVersion() throws Exception {
    sinceVersion(Api.Version.V0);
    final VersionOutput versionOutput = VersionOutput.builder()
        .rktVersion("1.25.0")
        .appcVersion("0.8.10")
        .goVersion("go1.7.4")
        .goOSArch("linux/amd64")
        .features("-TPM", "+SDJOURNAL")
        .build();
    when(rktLauncher.run(Version.COMMAND)).thenReturn(versionOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/version")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(versionOutput,
                 Json.deserialize(response.payload().get().toByteArray(), VersionOutput.class));
  }
}
