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
import io.honnix.rkt.launcher.command.CatManifest;
import io.honnix.rkt.launcher.command.Command;
import io.honnix.rkt.launcher.command.Config;
import io.honnix.rkt.launcher.command.Fetch;
import io.honnix.rkt.launcher.command.Gc;
import io.honnix.rkt.launcher.command.Prepare;
import io.honnix.rkt.launcher.command.Rm;
import io.honnix.rkt.launcher.command.Run;
import io.honnix.rkt.launcher.command.RunPrepared;
import io.honnix.rkt.launcher.command.Stop;
import io.honnix.rkt.launcher.command.Version;
import io.honnix.rkt.launcher.exception.RktException;
import io.honnix.rkt.launcher.exception.RktLauncherException;
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.ListOptions;
import io.honnix.rkt.launcher.options.Options;
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
import io.honnix.rkt.launcher.output.Output;
import io.honnix.rkt.launcher.output.PrepareOutput;
import io.honnix.rkt.launcher.output.RmOutput;
import io.honnix.rkt.launcher.output.RunOutput;
import io.honnix.rkt.launcher.output.StatusOutput;
import io.honnix.rkt.launcher.output.StopOutput;
import io.honnix.rkt.launcher.output.VersionOutput;
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

final class RktCommandResource {

  private static final Logger LOG = LoggerFactory.getLogger(RktCommandResource.class);

  private static final Function<RktLauncherConfig, RktLauncher>
      DEFAULT_RKT_LAUNCHER_FACTORY = RktLauncher::new;

  private static final String DEFAULT_HTTP_METHOD = "POST";

  private final RktLauncherConfig rktLauncherConfig;

  private Function<RktLauncherConfig, RktLauncher> rktLauncherFactory;

  RktCommandResource(final RktLauncherConfig rktLauncherConfig) {
    this(rktLauncherConfig, DEFAULT_RKT_LAUNCHER_FACTORY);
  }

  RktCommandResource(final RktLauncherConfig rktLauncherConfig,
                     final Function<RktLauncherConfig, RktLauncher> rktLauncherFactory) {
    this.rktLauncherConfig = Objects.requireNonNull(rktLauncherConfig);
    this.rktLauncherFactory = Objects.requireNonNull(rktLauncherFactory);
  }

  Stream<? extends Route<? extends AsyncHandler<? extends Response<ByteString>>>> routes() {
    final String base = "/rkt";

    final EntityMiddleware em =
        EntityMiddleware.forCodec(JacksonEntityCodec.forMapper(Json.OBJECT_MAPPER));

    final List<Route<AsyncHandler<Response<ByteString>>>> entityRoutes = Stream.of(
        Route.with(
            em.serializerResponse(CatManifestOutput.class),
            DEFAULT_HTTP_METHOD, base + "/cat-manifest/<id>",
            rc -> catManifest(getId(rc))),
        Route.with(
            em.serializerResponse(ConfigOutput.class),
            DEFAULT_HTTP_METHOD, base + "/config",
            rc -> config()),
        Route.with(
            em.serializerResponse(FetchOutput.class),
            DEFAULT_HTTP_METHOD, base + "/fetch",
            rc -> fetch(rc.request())),
        Route.with(
            em.serializerResponse(GcOutput.class),
            DEFAULT_HTTP_METHOD, base + "/gc",
            rc -> gc(rc.request())),
        Route.with(
            em.serializerResponse(ListOutput.class),
            DEFAULT_HTTP_METHOD, base + "/list",
            rc -> list()),
        Route.with(
            em.response(PrepareOptions.class, PrepareOutput.class),
            DEFAULT_HTTP_METHOD, base + "/prepare",
            rc -> this::prepare),
        Route.with(
            em.serializerResponse(RmOutput.class),
            DEFAULT_HTTP_METHOD, base + "/rm",
            rc -> rm(rc.request())),
        Route.with(
            em.serializerResponse(RmOutput.class),
            DEFAULT_HTTP_METHOD, base + "/rm/<id>",
            rc -> rm(getId(rc))),
        Route.with(
            em.response(RunOptions.class, RunOutput.class),
            DEFAULT_HTTP_METHOD, base + "/run",
            rc -> payload -> run(rc.request(), payload)),
        Route.with(
            em.serializerResponse(RunOutput.class),
            DEFAULT_HTTP_METHOD, base + "/run-prepared/<id>",
            rc -> runPrepared(getId(rc), rc.request())),
        Route.with(
            em.serializerResponse(StatusOutput.class),
            DEFAULT_HTTP_METHOD, base + "/status/<id>",
            rc -> status(getId(rc), rc.request())),
        Route.with(
            em.serializerResponse(StopOutput.class),
            DEFAULT_HTTP_METHOD, base + "/stop",
            rc -> stop(rc.request())),
        Route.with(
            em.serializerResponse(StopOutput.class),
            DEFAULT_HTTP_METHOD, base + "/stop/<id>",
            rc -> stop(getId(rc), rc.request())),
        Route.with(
            em.serializerResponse(VersionOutput.class),
            DEFAULT_HTTP_METHOD, base + "/version",
            rc -> version())
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

  private <T extends Options, S extends Output> Response<S> runCommand(
      final Command<T, S> command) {
    try {
      return Response.forPayload(rktLauncherFactory.apply(rktLauncherConfig).run(command));
    } catch (RktLauncherException e) {
      LOG.error("unable to execute command [{}]]", command, e);
      throw new RktLauncherServiceException(e);
    } catch (RktException e) {
      LOG.debug("non zero exit code [{}] received from rkt when executing command [{}]]",
                e.getExitCode(), command, e);
      return Response.forStatus(Status.UNPROCESSABLE_ENTITY.withReasonPhrase(e.toString()));
    } catch (RktUnexpectedOutputException e) {
      LOG.error("unexpected output received from rkt when executing command [{}]", command, e);
      throw e;
    }
  }

  private Response<CatManifestOutput> catManifest(final String id) {
    final CatManifest catManifest = CatManifest.builder()
        .args(ImmutableList.of(id))
        .build();
    return runCommand(catManifest);
  }

  private Response<ConfigOutput> config() {
    return runCommand(Config.COMMAND);
  }

  private Response<FetchOutput> fetch(final Request request) {
    final List<String> images = request.parameters().get("image");
    if (images == null) {
      return Response
          .forStatus(Status.BAD_REQUEST.withReasonPhrase("Missing 'image' query parameter"));
    }

    final FetchOptions options;
    try {
      options = readPayloadIfExists(request, FetchOptions.class);
    } catch (IOException e) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase(e.getMessage()));
    }

    final Fetch fetch = Fetch.builder()
        .options(options)
        .args(images)
        .build();
    return runCommand(fetch);
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
    final io.honnix.rkt.launcher.command.List list =
        io.honnix.rkt.launcher.command.List.builder()
            .options(ListOptions.builder().build())
            .build();
    return runCommand(list);
  }

  private Response<PrepareOutput> prepare(final PrepareOptions options) {
    final Prepare prepare = Prepare.builder()
        .options(options)
        .build();
    return runCommand(prepare);
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

  private Response<RunOutput> run(final Request request, final RunOptions options) {
    final boolean daemonize = request.parameter("daemonize").orElse("true").equals("true");

    if (options.uuidFileSave().isPresent()) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase("UUID file not supported"));
    }

    final Run run = Run.builder()
        .daemonize(daemonize)
        .options(options)
        .build();
    return runCommand(run);
  }

  private Response<RunOutput> runPrepared(final String id, final Request request) {
    final RunPreparedOptions options;
    try {
      options = readPayloadIfExists(request, RunPreparedOptions.class);
    } catch (IOException e) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase(e.getMessage()));
    }

    final boolean daemonize = request.parameter("daemonize").orElse("true").equals("true");
    final RunPrepared runPrepared = RunPrepared.builder()
        .daemonize(daemonize)
        .options(options)
        .addArg(id)
        .build();
    return runCommand(runPrepared);
  }

  private Response<StatusOutput> status(final String id, final Request request) {
    final StatusOptions userOptions;
    try {
      userOptions = readPayloadIfExists(request, StatusOptions.class);
    } catch (IOException e) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase(e.getMessage()));
    }

    final StatusOptions options =
        userOptions == null ? StatusOptions.builder().build() : userOptions;

    final io.honnix.rkt.launcher.command.Status status =
        io.honnix.rkt.launcher.command.Status.builder()
            .options(options)
            .addArg(id)
            .build();
    return runCommand(status);
  }

  private Response<StopOutput> stop(final List<String> ids, final Request request) {
    final StopOptions options;
    try {
      options = readPayloadIfExists(request, StopOptions.class);
    } catch (IOException e) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase(e.getMessage()));
    }

    if (options != null && options.uuidFile().isPresent()) {
      return Response.forStatus(Status.BAD_REQUEST.withReasonPhrase("UUID file not supported"));
    }

    final Stop stop = Stop.builder()
        .options(options)
        .args(ids)
        .build();
    return runCommand(stop);
  }

  private Response<StopOutput> stop(final Request request) {
    final List<String> ids = request.parameters().get("id");
    if (ids == null) {
      return Response
          .forStatus(Status.BAD_REQUEST.withReasonPhrase("Missing 'id' query parameter"));
    }

    return stop(ids, request);
  }

  private Response<StopOutput> stop(final String id, final Request request) {
    return stop(ImmutableList.of(id), request);
  }

  private Response<VersionOutput> version() {
    return runCommand(Version.COMMAND);
  }
}
