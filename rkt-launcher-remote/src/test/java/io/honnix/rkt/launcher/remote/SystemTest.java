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
import static org.mockito.Mockito.when;

import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import io.honnix.rkt.launcher.model.ImageBuilder;
import io.honnix.rkt.launcher.model.PodBuilder;
import io.honnix.rkt.launcher.model.PullPolicy;
import io.honnix.rkt.launcher.model.TrustedPubkey;
import io.honnix.rkt.launcher.model.config.ConfigBuilder;
import io.honnix.rkt.launcher.model.config.PathsBuilder;
import io.honnix.rkt.launcher.model.config.Stage1Builder;
import io.honnix.rkt.launcher.model.schema.ImageManifestBuilder;
import io.honnix.rkt.launcher.model.schema.PodManifestBuilder;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.PerImageOptions;
import io.honnix.rkt.launcher.options.PrepareOptions;
import io.honnix.rkt.launcher.options.RunOptions;
import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.options.StatusOptions;
import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.options.TrustOptions;
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
import io.honnix.rkt.launcher.output.TrustOutput;
import io.honnix.rkt.launcher.output.VersionOutput;
import io.honnix.rkt.launcher.util.Json;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import okio.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SystemTest {

  private static final String DEFAULT_HTTP_METHOD = "POST";

  private RktLauncherRemote rktLauncherRemote;

  @Mock
  private Client client;

  @Before
  public void setUp() throws Exception {
    rktLauncherRemote = RktLauncherRemote.builder()
        .scheme(HTTP)
        .host("localhost")
        .port(8080)
        .client(client)
        .build();
  }

  @Test
  public void shouldCallImageCatManifest() throws Exception {
    final io.honnix.rkt.launcher.output.image.CatManifestOutput output =
        io.honnix.rkt.launcher.output.image.CatManifestOutput.builder()
            .imageManifest(new ImageManifestBuilder()
                               .acKind(ACKind.IMAGE_MANIFEST)
                               .acVersion("1.0.0")
                               .name("nginx")
                               .build())
            .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(Request.forUri("http://localhost:8080/api/v0/rkt/image/cat-manifest/id1",
                                    DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final io.honnix.rkt.launcher.output.image.CatManifestOutput response =
        rktLauncherRemote.image().catManifest("id1").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallImageGc() throws Exception {
    final io.honnix.rkt.launcher.options.image.GcOptions options =
        io.honnix.rkt.launcher.options.image.GcOptions.builder()
            .gracePeriod(Duration.ofSeconds(10))
            .build();
    final io.honnix.rkt.launcher.output.image.GcOutput output =
        io.honnix.rkt.launcher.output.image.GcOutput.builder()
            .removedImages("image1", "image2")
            .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/image/gc",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final io.honnix.rkt.launcher.output.image.GcOutput response =
        rktLauncherRemote.image().gc(options).toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallImageGcWithoutOptions() throws Exception {
    final io.honnix.rkt.launcher.output.image.GcOutput output =
        io.honnix.rkt.launcher.output.image.GcOutput.builder()
            .removedImages("image1", "image2")
            .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/image/gc",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final io.honnix.rkt.launcher.output.image.GcOutput response =
        rktLauncherRemote.image().gc().toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallImageList() throws Exception {
    final io.honnix.rkt.launcher.output.image.ListOutput output =
        io.honnix.rkt.launcher.output.image.ListOutput.builder()
            .images(new ImageBuilder()
                        .id("123")
                        .name("nginx1")
                        .importTime(Instant.now())
                        .lastUsedTime(Instant.now())
                        .size(100)
                        .build(),
                    new ImageBuilder()
                        .id("345")
                        .name("nginx2")
                        .importTime(Instant.now())
                        .lastUsedTime(Instant.now())
                        .size(100)
                        .build())
            .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/image/list",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final io.honnix.rkt.launcher.output.image.ListOutput response =
        rktLauncherRemote.image().list().toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallImageRm() throws Exception {
    final io.honnix.rkt.launcher.output.image.RmOutput output =
        io.honnix.rkt.launcher.output.image.RmOutput.builder()
            .removed("123")
            .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/image/rm?id=id1&id=id2",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final io.honnix.rkt.launcher.output.image.RmOutput response =
        rktLauncherRemote.image().rm("id1", "id2").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallCatManifest() throws Exception {
    final CatManifestOutput output = CatManifestOutput.builder()
        .podManifest(new PodManifestBuilder()
                         .acKind(ACKind.POD_MANIFEST)
                         .acVersion("1.0.0")
                         .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(Request.forUri("http://localhost:8080/api/v0/rkt/cat-manifest/id1",
                                    DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final CatManifestOutput response =
        rktLauncherRemote.catManifest("id1").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallConfig() throws Exception {
    final ConfigOutput output = ConfigOutput.builder()
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
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(Request.forUri("http://localhost:8080/api/v0/rkt/config",
                                    DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final ConfigOutput response =
        rktLauncherRemote.config().toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallFetch() throws Exception {
    final FetchOptions options = FetchOptions.builder()
        .full(true)
        .pullPolicy(PullPolicy.UPDATE)
        .build();
    final FetchOutput output = FetchOutput.builder()
        .hash("hash")
        .signature("sig")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri(
            "http://localhost:8080/api/v0/rkt/fetch?async=true&image=image1&image=image2",
            DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final FetchOutput response =
        rktLauncherRemote.fetch(options, true, "image1", "image2").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallFetchWithoutOptions() throws Exception {
    final FetchOutput output = FetchOutput.builder()
        .hash("hash")
        .signature("sig")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri(
            "http://localhost:8080/api/v0/rkt/fetch?async=false&image=image1&image=image2",
            DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final FetchOutput response =
        rktLauncherRemote.fetch(false, "image1", "image2").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallGc() throws Exception {
    final GcOptions options = GcOptions.builder()
        .markOnly(true)
        .build();
    final GcOutput output = GcOutput.builder()
        .removed("removed1", "removed2")
        .unremoved("unremoved1", "unremoved2")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/gc",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final GcOutput response = rktLauncherRemote.gc(options).toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallGcWithoutOptions() throws Exception {
    final GcOutput output = GcOutput.builder()
        .removed("removed1", "removed2")
        .unremoved("unremoved1", "unremoved2")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/gc",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final GcOutput response =
        rktLauncherRemote.gc().toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallList() throws Exception {
    final ListOutput output = ListOutput.builder()
        .pods(new PodBuilder()
                  .name("nginx1")
                  .state("running")
                  .build(),
              new PodBuilder()
                  .name("nginx2")
                  .state("running")
                  .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/list",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final ListOutput response =
        rktLauncherRemote.list().toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallPrepare() throws Exception {
    final PrepareOptions options = PrepareOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .addImagesOption(PerImageOptions.builder().image("docker://mysql").build())
        .build();
    final PrepareOutput output = PrepareOutput.builder()
        .prepared("123")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/prepare?async=true",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final PrepareOutput response =
        rktLauncherRemote.prepare(options, true).toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallRm() throws Exception {
    final RmOutput output = RmOutput.builder()
        .removed("123")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/rm?id=id1&id=id2",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final RmOutput response = rktLauncherRemote.rm("id1", "id2").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallRun() throws Exception {
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .build();
    final RunOutput output = RunOutput.builder()
        .service("NA")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/run?daemonize=true",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final RunOutput response =
        rktLauncherRemote.run(options, true).toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallRunPrepared() throws Exception {
    final RunPreparedOptions options = RunPreparedOptions.builder()
        .mdsRegister(true)
        .build();
    final RunOutput output = RunOutput.builder()
        .service("NA")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/run-prepared/id1?daemonize=true",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final RunOutput response =
        rktLauncherRemote.runPrepared(options, "id1", true).toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallRunPreparedWithoutOptions() throws Exception {
    final RunOutput output = RunOutput.builder()
        .service("NA")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/run-prepared/id1?daemonize=true",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final RunOutput response =
        rktLauncherRemote.runPrepared("id1", true).toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallStatus() throws Exception {
    final StatusOptions options = StatusOptions.builder()
        .waitTillFinish(Duration.ofSeconds(10))
        .build();
    final StatusOutput output = StatusOutput.builder()
        .status(new PodBuilder()
                    .name("nginx")
                    .state("running")
                    .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/status/id1",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final StatusOutput response =
        rktLauncherRemote.status(options, "id1").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallStatusWithoutOptions() throws Exception {
    final StatusOutput output = StatusOutput.builder()
        .status(new PodBuilder()
                    .name("nginx")
                    .state("running")
                    .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/status/id1",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final StatusOutput response =
        rktLauncherRemote.status("id1").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallStop() throws Exception {
    final StopOptions options = StopOptions.builder()
        .force(true)
        .build();
    final StopOutput output = StopOutput.builder()
        .stopped("123")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/stop?id=id1&id=id2",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final StopOutput response =
        rktLauncherRemote.stop(options, "id1", "id2").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallStopWithoutOptions() throws Exception {
    final StopOutput output = StopOutput.builder()
        .stopped("123")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/stop?id=id1&id=id2",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final StopOutput response =
        rktLauncherRemote.stop("id1", "id2").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallTrust() throws Exception {
    final TrustOptions options = TrustOptions.builder()
        .insecureAllowHttp(true)
        .root(true)
        .build();
    final TrustOutput output = TrustOutput.builder()
        .addTrustedPubkey(TrustedPubkey.builder()
                              .prefix("example.com")
                              .key("pubkey1")
                              .location("")
                              .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/trust?pubkey=http://example.com/pubkey1",
                       DEFAULT_HTTP_METHOD)
            .withPayload(ByteString.of(Json.serialize(options)))))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final TrustOutput response =
        rktLauncherRemote.trust(options, "http://example.com/pubkey1").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallTrustWithoutOptions() throws Exception {
    final TrustOutput output = TrustOutput.builder()
        .addTrustedPubkey(TrustedPubkey.builder()
                              .prefix("")
                              .key("pubkey1")
                              .location("")
                              .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/trust?pubkey=http://example.com/pubkey1",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final TrustOutput response =
        rktLauncherRemote.trust("http://example.com/pubkey1").toCompletableFuture().get();
    assertEquals(output, response);
  }

  @Test
  public void shouldCallVersion() throws Exception {
    final VersionOutput output = VersionOutput.builder()
        .rktVersion("1.25.0")
        .appcVersion("0.8.10")
        .goVersion("go1.7.4")
        .goOSArch("linux/amd64")
        .features("-TPM", "+SDJOURNAL")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(output)));
    when(client.send(
        Request.forUri("http://localhost:8080/api/v0/rkt/version",
                       DEFAULT_HTTP_METHOD)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final VersionOutput response =
        rktLauncherRemote.version().toCompletableFuture().get();
    assertEquals(output, response);
  }
}
