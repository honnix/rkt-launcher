# rkt-launcher

[![CircleCI](https://circleci.com/gh/honnix/rkt-launcher/tree/master.svg?style=shield)](https://circleci.com/gh/honnix/rkt-launcher)
[![Coverage Status](https://codecov.io/gh/honnix/rkt-launcher/branch/master/graph/badge.svg)](https://codecov.io/gh/honnix/rkt-launcher)
[![Maven Central](https://img.shields.io/maven-central/v/io.honnix/rkt-launcher.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.honnix%22%20rkt-launcher)
[![License](https://img.shields.io/github/license/honnix/rkt-launcher.svg)](LICENSE)

To launch [rkt] in a type-safe way using Java (a.k.a rkt Java API).

`rkt` is designed to be used as a command line executable without having
a daemon. To understand the design philosophy, you are encouraged to read
[this document][rkt-vs-other-projects].

`rkt-launcher` starts with a core lib that can be used to fork `rkt`
process in a type-safe way; then it provides a service exposing RESTful
APIs through which the user can interact with `rkt` remotely; and last
but not least, a client is shipped to ease the process talking to the
service.

This project is in beta stage. Most of the features have been
implemented and can be used for production. Backward compatibility will
be ensured on a best efforts basis.

## Usage

### Prerequisite

* `rkt` has been installed, check [here][trying-out-rkt] for details
* JDK8 has been installed
* Maven has been installed

### To build

```
$ git clone git@github.com:honnix/rkt-launcher.git
$ mvn package
```

### To start the service:

```
$ java -jar rkt-launcher-service/target/rkt-launcher-service.jar -Dhttp.server.port=8080
```

### To start hacking

Import the maven project to your favorite IDE or choose whatever editor
you like. Well, it's just plain Java.

## rkt-launcher-common

This module contains options passed to `rkt`, models capturing `rkt`
output and a few utilities handling JSON, time, etc.

[Read More](rkt-launcher-common/README.md)

## rkt-launcher-core

Core library to fork `rkt` process and handle output.

[Read More](rkt-launcher-core/README.md)

## rkt-launcher-service

This module exposes RESTful APIs to fork `rkt` process and handle output.

This service is built using [Apollo] framework.

[Read More](rkt-launcher-service/README.md)

## rkt-launcher-remote

Talking to `rkt-launcher-service` remotely.

[Read More](rkt-launcher-remote/README.md)

[rkt]: https://coreos.com/rkt/
[rkt-vs-other-projects]: https://coreos.com/rkt/docs/latest/rkt-vs-other-projects.html
[trying-out-rkt]: https://coreos.com/rkt/docs/latest/trying-out-rkt.html
[Apollo]: https://github.com/spotify/apollo
[Automatter]: https://github.com/danielnorberg/auto-matter
