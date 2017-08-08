# rkt-launcher-service

An [Apollo] service offering RESTful APIs to fork [rkt] process and
handle output.

## To start the service

```
$ mvn package
$ java -jar rkt-launcher-service/target/rkt-launcher-service.jar -Dhttp.server.port=8080
```

## Configuration

This service is shipped with default logging and runtime configuration.
The user is more than welcome to customize.

### Logging

Standard [logback] configuration [here](src/main/resources/logback.xml).

### Runtime

Default configuration as
```
rktLauncher = {
  # where to find rkt binary
  rkt = "/usr/bin/rkt"
  # how to daemonize
  daemon = ["systemd-run", "--slice=machine"]
  # rkt global options, check `rkt -h` for details
  globalOptions = {
    insecureOptions = ["image"]
  }
}
```

where the value of `rktLauncher` is mapped to
[GlobalOptions.java].

## API specification

Only HTTP POST is supported to invoke `rkt` commands. Two groups of
commands are supported under `/rkt` and `/rkt/image` contexts respectively.

JSON payload can be provided if the command expects options, while path
and query parameters are used as command arguments.

`rkt prepare` and `rkt run` are a bit special due to how command line is
interpreted by `rkt`, and for those commands JSON payload is required to
be used both as options and arguments.

For details of `rkt` command, please check `rkt -h`.

### /rkt

* `/rkt/cat-manifest/<id>` without payload
* `/rkt/config` without payload
* `/rkt/fetch` with optional payload and `image` (repeatable) as query parameter
* `/rkt/gc` with optional payload
* `/rkt/list` without payload
* `/rkt/prepare` with mandatory payload
* `/rkt/rm` without payload and `id` (repeatable) as query parameter
* `/rkt/rm/<id>` without payload
* `/rkt/run` with mandatory payload and `daemonized` as an optional query parameter (default to `true`)
* `/rkt/run-prepared/<id>` with optional payload and `daemonized` as an optional query parameter (default to `true`)
* `/rkt/status/<id>` with optional payload
* `/rkt/stop` with optional payload and `id` (repeatable) as query parameter
* `/rkt/stop/<id>` with optional payload
* `/rkt/trust` with optional payload and `pubkey` (optional and repeatable) as query parameter
* `/rkt/version` without payload

Both `/rkt/fetch` and `/rkt/prepare` accept `async` as a boolean query
parameter to instruct the service run corresponding commands asynchronously.

The caller shall be aware that if calling `/rkt/run` or `/rkt/run-prepare`
with `daemonized` to `false`, the underlying HTTP connection will hang
until socket reading timeout or `rkt` finishes before timeout.

### /rkt/image

* `/rkt/image/cat-manifest/<id>` without payload
* `/rkt/image/gc` with optional payload
* `/rkt/image/list` without payload
* `/rkt/image/rm` without payload and `id` (repeatable) as query parameter
* `/rkt/image/rm/<id>` without payload

### Swagger spec

TBA.

[Apollo]: https://github.com/spotify/apollo
[rkt]: https://coreos.com/rkt/
[logback]: https://logback.qos.ch
[GlobalOptions.java]: ../rkt-launcher-common/src/main/java/io/honnix/rkt/launcher/options/GlobalOptions.java
