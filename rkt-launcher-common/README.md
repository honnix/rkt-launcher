# rkt-launcher-common

## Model

For a few cases, [rkt] can be instruct to output structured data such
as JSON, and for these cases, POJOs are used to capture the output.

For other cases, `rkt` outputs plain string, and for those cases, regex
is used to parse the output and POJOs are used to capture the result.

## Options

Command options passed to `rkt`.

## Output

Representing `rkt` command output.

## Utility

Handling JSON serialization/deserialization, and time.

## Why interfaces?

[AutoMatter] is heavily used to save thousands lines of code, as well as
to provide a very clean interface and usage pattern.

[rkt]: https://coreos.com/rkt/
[AutoMatter]: https://github.com/danielnorberg/auto-matter
