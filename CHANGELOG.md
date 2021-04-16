# Changelog
All notable changes to this project will be documented in this file.

## [0.5.0 - 2021-04-xx
* Adopt breaking changes in Mockito 3.4.0 and higher (#4)
* Introduced `KMockitoAnnotations.openMocks( Any )`
* Deprecated `KMockitoAnnotations.initocks( Any )`

## [0.4.6] - 2021-03-31
* Adjustment to package renaming of dependent mockito-kotlin library (#8)

## [0.4.5] - 2021-03-30
* Switch from Bintray/JCenter to maven central (#7)
* renamed groupId from org.mockito4kotlin to io.github.wickie73
* renamed artifactId from annotation to mockito4kotlin-annotation
* API changes: renamed packages from `org.mockito4kotlin.annotation` to `io.github.wickie73.mockito4kotlin.annotation`

## [0.4.2] - 2021-01-31
### Changed
* Support 'lenient' setting in KMock annotation (#5)
* Updated kotlin 1.3.41 -> 1.4.21
* Updated mockito 2.28.1 -> 2.28.2
* Updated mockito-kotlin 2.1.0 -> 2.2.0
* Updated gradle 5.2.1-> 6.8.1

## [0.4.1] - 2020-09-02
* Fixed issue #2: Could not resolve annotation:0.4.0

## [0.4.0] - 2019-07-10
### Added
* `KMockitoAnnotations`: initialized the use of [mockito annotations](https://static.javadoc.io/org.mockito/mockito-core/2.28.1/org/mockito/MockitoAnnotations.html).
* `KMock` annotation for kotlin-based mocking.
* Support for kotlin coroutines.

### Changed
* Updated kotlin 1.2.50 -> 1.3.41
* Updated mockito 2.19 -> 2.28.1
* Updated mockito-kotlin 2.0.0-RC1 -> 2.1.0
* Updated gradle 4.7 -> 5.2.1
* Uses original [mockito's](https://static.javadoc.io/org.mockito/mockito-core/2.28.1/org/mockito/Mockito.html) `@InjectMocks`, `@Mock`, `@Spy` and `@Captor` annotations.
* Simplified tests.

### Removed
* `MockAnnotations` _(deprecated)_: Please use class `KMockitoAnnotations`.

## [0.3.0] - 2018-10-06
### Changed
* Updated kotlin 1.2.41 -> 1.2.50
* Updated mockito 2.18.3 -> 2.19
* Updated mockito-kotlin 1.5.0 -> 2.0.0-RC1
* Added tests

## [0.2.7] - 2018-06-02
