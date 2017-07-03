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

import static java.util.stream.Collectors.toList;

import com.google.common.collect.ImmutableList;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.entity.EntityMiddleware;
import com.spotify.apollo.entity.JacksonEntityCodec;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Middleware;
import com.spotify.apollo.route.Route;
import io.honnix.rkt.launcher.RktLauncher;
import io.honnix.rkt.launcher.RktLauncherConfig;
import io.honnix.rkt.launcher.command.Command;
import io.honnix.rkt.launcher.command.image.CatManifest;
import io.honnix.rkt.launcher.command.image.Gc;
import io.honnix.rkt.launcher.command.image.Rm;
import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.exception.RktLauncherException;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.options.image.ListOptions;
import io.honnix.rkt.launcher.output.Output;
import io.honnix.rkt.launcher.output.image.CatManifestOutput;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.ListOutput;
import io.honnix.rkt.launcher.output.image.RmOutput;
import io.honnix.rkt.launcher.service.exception.RktLauncherServiceException;
import io.honnix.rkt.launcher.util.Json;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class RktImageCommandResource {

  private static final Logger LOG = LoggerFactory.getLogger(RktImageCommandResource.class);

  private static final Function<RktLauncherConfig, RktLauncher>
      DEFAULT_RKT_LAUNCHER_FACTORY = RktLauncher::new;

  private static final String DEFAULT_HTTP_METHOD = "POST";

  private final RktLauncherConfig rktLauncherConfig;

  private Function<RktLauncherConfig, RktLauncher> rktLauncherFactory;

  RktImageCommandResource(final RktLauncherConfig rktLauncherConfig) {
    this(rktLauncherConfig, DEFAULT_RKT_LAUNCHER_FACTORY);
  }

  RktImageCommandResource(final RktLauncherConfig rktLauncherConfig,
                          final Function<RktLauncherConfig, RktLauncher> rktLauncherFactory) {
    this.rktLauncherConfig = Objects.requireNonNull(rktLauncherConfig);
    this.rktLauncherFactory = Objects.requireNonNull(rktLauncherFactory);
  }

  Stream<? extends Route<? extends AsyncHandler<? extends Response<ByteString>>>> routes() {
    final String base = "/rkt/image";

    final EntityMiddleware em =
        EntityMiddleware.forCodec(JacksonEntityCodec.forMapper(Json.OBJECT_MAPPER));

    final List<Route<AsyncHandler<Response<ByteString>>>> entityRoutes = Stream.of(
        Route.with(
            em.serializerResponse(CatManifestOutput.class),
            DEFAULT_HTTP_METHOD, base + "/cat-manifest/<id>",
            rc -> catManifest(getId(rc))),
        Route.with(
            em.serializerResponse(GcOutput.class),
            DEFAULT_HTTP_METHOD, base + "/gc",
            rc -> gc(rc.request())),
        Route.with(
            em.serializerResponse(ListOutput.class),
            DEFAULT_HTTP_METHOD, base + "/list",
            rc -> list()),
        Route.with(
            em.serializerResponse(RmOutput.class),
            DEFAULT_HTTP_METHOD, base + "/rm",
            rc -> rm(rc.request())),
        Route.with(
            em.serializerResponse(RmOutput.class),
            DEFAULT_HTTP_METHOD, base + "/rm/<id>",
            rc -> rm(getId(rc)))
    )
        .map(r -> r.withMiddleware(Middleware::syncToAsync))
        .collect(toList());

    return Api.prefixRoutes(entityRoutes, Api.Version.V0);
  }

  private static String getId(final RequestContext rc) {
    return rc.pathArgs().get("id");
  }

  private static <T> T readPayloadIfExists(final Request request, final Class<T> optionsClass)
      throws IOException {
    final Optional<ByteString> payload = request.payload();
    if (payload.isPresent() && payload.get().size() != 0) {
      return Json.deserialize(payload.get().toByteArray(), optionsClass);
    } else {
      return null;
    }
  }

  private <T extends Options, S extends Output> Response<S> runCommand(final Command<T, S> command) {
    try {
      return Response.forPayload(rktLauncherFactory.apply(rktLauncherConfig).run(command));
    } catch (RktLauncherException e) {
      LOG.error("unable to execute command [{}]]", command, e);
      throw new RktLauncherServiceException(e);
    } catch (RktException e) {
      LOG.debug("non zero exit code [{}] received from rkt when executing command [{}]]",
                e.getExitCode(), command, e);
      return Response.forStatus(Status.UNPROCESSABLE_ENTITY.withReasonPhrase(e.toString()));
    }
  }

  private Response<CatManifestOutput> catManifest(final String id) {
    final CatManifest catManifest = CatManifest.builder()
        .args(ImmutableList.of(id))
        .build();
    return runCommand(catManifest);
  }

  private Response<GcOutput> gc(final Request request) {
    final GcOptions options;
    try {
      options = readPayloadIfExists(request, GcOptions.class);
    } catch (IOException e) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase(e.getMessage()));
    }

    final Gc gc = Gc.builder()
        .options(options)
        .build();
    return runCommand(gc);
  }

  private Response<ListOutput> list() {
    final io.honnix.rkt.launcher.command.image.List list =
        io.honnix.rkt.launcher.command.image.List.builder()
            .options(ListOptions.builder().build())
            .build();
    return runCommand(list);
  }

  private Response<RmOutput> rm(final Request request) {
    final List<String> ids = request.parameters().get("id");
    if (ids == null) {
      return Response
          .forStatus(Status.BAD_REQUEST.withReasonPhrase("Missing 'id' query parameter"));
    }
    final Rm rm = Rm.builder()
        .args(ids)
        .build();
    return runCommand(rm);
  }

  private Response<RmOutput> rm(final String id) {
    final Rm rm = Rm.builder()
        .args(ImmutableList.of(id))
        .build();
    return runCommand(rm);
  }
}
