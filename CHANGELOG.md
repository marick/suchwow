# Change Log
This project adheres to [Semantic Versioning](http://semver.org/).
See [here](http://keepachangelog.com/) for the change log format. 

## [unreleased]

## [4.1.0] - 2015-08-19

### Added
- `imperfection` namespace for debugging and testing functions.

## [4.0.1] - 2015-08-14

### Fixed

- The generated `api` function didn't have `:arglist` metadata.

## [4.0.0] - 2015-08-14

### Changed

- Names in previous were bad: such.api -> such.doc, `open` -> `api`.

## [3.5.0] - 2015-08-14

### Added
- docs for `reduce`, `reductions`, `reduce-kv`, and `map-indexed`.
- `such.api`

## [3.4.0] - 2015-07-31

### Added
- `such.metadata` namespace

## [3.3.0] - 2015-07-11

### Added
- Docs for `sequential?`, `cond->`, and `cond->>`
- `pred:not-any?`, `pred:none-of?`

### Deprecated
- `any-pred` is now `pred:any?`
- `wrap-pred-with-catcher` is now `pred:exception->false`


