# Changelog

## [0.3.0](https://github.com/scalar/galaxy-kotlin/compare/v0.2.0...v0.3.0) (2026-10-23)


### ⚠ BREAKING CHANGES

* **api:** 5 breaking changes to the SDK surface.
    - Removed operation `planets.listAllData` (`GET /planets`).
    - Removed operation `planets.delteImage` (`POST /planets/{planetId}/image`).
    - Removed schema `ListAllDataResponseHeaders`.
    - Removed schema `DelteImageResponseHeaders`.
    - Removed schema `DelteImageStatus400ResponseHeaders`.

### Features

* **api:** remove operation planets.listAllData (+9 more changes) ([1286a0b](https://github.com/scalar/galaxy-kotlin/commit/1286a0be0d5c798fad69a5e5866b9f163aff21bb))

## [0.2.0](https://github.com/scalar/galaxy-kotlin/compare/v0.1.0...v0.2.0) (2026-10-23)


### Features

* **api:** initial SDK generation ([cdcf811](https://github.com/scalar/galaxy-kotlin/commit/cdcf811aa44cbdfdfccdf7d421f528d894c72e7e))
