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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.Environment;
import com.spotify.apollo.Response;
import com.spotify.apollo.StatusType;
import io.honnix.rkt.launcher.RktLauncher;
import io.honnix.rkt.launcher.RktLauncherConfig;
import io.honnix.rkt.launcher.command.image.CatManifest;
import io.honnix.rkt.launcher.command.image.Gc;
import io.honnix.rkt.launcher.command.image.List;
import io.honnix.rkt.launcher.command.image.Rm;
import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.exception.RktLauncherException;
import io.honnix.rkt.launcher.model.ImageBuilder;
import io.honnix.rkt.launcher.model.schema.ImageManifestBuilder;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.options.image.ListOptions;
import io.honnix.rkt.launcher.output.image.CatManifestOutput;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.ListOutput;
import io.honnix.rkt.launcher.output.image.RmOutput;
import io.honnix.rkt.launcher.util.Json;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Test;

public class RktImageCommandResourceTest extends VersionedApiTest {

  private static final String DEFAULT_HTTP_METHOD = "POST";

  private RktLauncher rktLauncher;

  public RktImageCommandResourceTest(final Api.Version version) {
    super("/rkt/image", version, "rkt-image-command-resource-test");
  }

  @Override
  protected void init(final Environment environment) {
    rktLauncher = mock(RktLauncher.class);
    final Function<RktLauncherConfig, RktLauncher> rktLauncherFactory =
        (rktLauncherConfig) -> rktLauncher;
    environment.routingEngine()
        .registerRoutes(new RktImageCommandResource(RktLauncherConfig.builder().build(),
                                                    rktLauncherFactory).routes());
  }

  @Test
  public void shouldReturnServerErrorForCommandWithOutput() throws Exception {
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
  public void shouldReturnClientErrorForCommandWithOutput() throws Exception {
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
        .imageManifest(new ImageManifestBuilder()
                           .acKind(ACKind.IMAGE_MANIFEST)
                           .acVersion("1.0.0")
                           .name("nginx")
                           .build())
        .build();
    when(rktLauncher.run(catManifest)).thenReturn(catManifestOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/cat-manifest/123")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    Assert.assertEquals(catManifestOutput,
                        Json.deserialize(response.payload().get().toByteArray(), CatManifestOutput.class));
  }

  @Test
  public void shouldRunGcWithPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final GcOptions options = GcOptions.builder()
        .gracePeriod(Duration.ofSeconds(10))
        .build();
    final Gc gc = Gc.builder()
        .options(options)
        .build();
    final GcOutput gcOutput = GcOutput.builder()
        .removedImages("removed1", "removed2")
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
        .removedImages("removed1", "removed2")
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
  public void shouldRunGcWithEmptyPayload() throws Exception {
    sinceVersion(Api.Version.V0);
    final Gc gc = Gc.builder()
        .build();
    final GcOutput gcOutput = GcOutput.builder()
        .removedImages("removed1", "removed2")
        .build();
    when(rktLauncher.run(gc)).thenReturn(gcOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/gc"), ByteString.of()));
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
    when(rktLauncher.run(list)).thenReturn(listOutput);
    final Response<ByteString> response = awaitResponse(
        serviceHelper.request(DEFAULT_HTTP_METHOD, path("/list")));
    assertThat(response, hasStatus(belongsToFamily(StatusType.Family.SUCCESSFUL)));
    assertTrue(response.payload().isPresent());
    assertEquals(listOutput,
                 Json.deserialize(response.payload().get().toByteArray(), ListOutput.class));
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
}
