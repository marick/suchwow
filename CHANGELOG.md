# Change Log
This project adheres to [Semantic Versioning](http://semver.org/).
See [here](http://keepachangelog.com/) for the change log format.

## [6.0.2] - 2017-12-27
- Move `defalias` and `alias-var` code over from `clojure-commons`

## [6.0.1] - 2017-12-27
- Upgrade to Specter 1.0.4 to avoid `any? in com.rpl.specter.impl` warning
- Fix reflection warnings
- Bump libs

## [6.0.0] - 2016-10-26
- Upgrade to Specter 0.13
- ... which caused Clojure 1.6 to be abandoned, so major version bump.

## [5.2.4]
- Update dependencies.

## [5.2.3]
- Fixes bad `ns` form. @puredanger

## [5.2.1]
- Specter 0.12.0 broke downstream dependencies. Reverting to 0.11.2

## [5.2.0]
- ADD: Better error message when trying to immigrate a variable that does not exist.
- Update dependencies

## [5.1.4]
- CHANGE: Guard against Clojure 1.9's `any?` - Børge Svingen

## [5.1.3]
- CHANGE: Bump structural typing dependency

## [5.1.2]
- CHANGE: Update `structural-typing` because of annoying circular dependency.

## [5.1.1]
- CHANGE: Update versions, most notably `specter`, but also `environ`,
  `combinatorics`, `structural-typing`, and `compojure`.

## [5.1]
- ADD: such.imperfection has variants that print to *err* of these functions:
  `pr`, `prn`, `print`, `println`, `pprint`, `-pprint-`, `-prn-`, `tag-`, and `-tag`.

## [5.0]
- CHANGED: No longer support Clojure 1.5
- ADD: such.control-flow/let-maybe
- ADD: An *experimental* such.relational namespace. See the API docs and [the wiki](https://github.com/marick/suchwow/wiki/such.relational)

## [4.4.3]
- DEPRECATION: In upcoming release, immigration functions will not `require` namespaces themselves.

## [4.4.2]

- bump versions of dependencies, including commons-codec

## [4.4.1]

- Use commons-codec 1.6 instead of 1.10. ring-codec uses 1.6. Since it's more popular,
  we might as well just track its preference to avoid shoving version conflicts into
  people's faces.

## [4.4.0]

### Added

- such.maps now supplies Clojure 1.7's `update` under Clojure 1.6.
- -tag and tag- can now take any value, not just a string.

## [4.3.0]

### Added

- Functions in such.imperfection and such.readable can now be used with Clojure 1.6.

## [4.2.0]

### Added
- `such.metadata/contains?`

### Fixed
- All the `such.immigration` functions now do requires at compile time.
- Workaround to prevent Codox from crashing on vars imported with `import-prefixed-vars`.

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


