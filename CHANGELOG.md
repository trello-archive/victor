Change Log
==========

1.1.2
-----
*2021-06-08*

- ([#81](https://github.com/trello/victor/pull/81), [#83](https://github.com/trello/victor/pull/83), [#84](https://github.com/trello/victor/pull/84), [#85](https://github.com/trello/victor/pull/85)) Added Gradle 7 support.

1.1.1
-----
*2021-03-24*

There are no differences between 1.1.0 and 1.1.1; we simply moved to a different host, and I wanted
to make sure there was no confusion when switching over (since builds will fail now if you keep
trying to access this via jcenter).

- ([#78](https://github.com/trello/victor/pull/78)) Moved to Gradle publishing portal

1.1.0
-----
*2020-05-08*

- ([#71](https://github.com/trello/victor/pull/71)) Added support for Gradle 6
- ([#72](https://github.com/trello/victor/pull/72)) Upgraded to Batik 1.12

1.0.0
-----
*2017-07-19*

I'm calling this 1.0 for no other reason than that this is going to be considered stable; no major
changes to the API from here on out.

- ([#65](https://github.com/trello/victor/pull/65)) Upgraded to Batik 1.9

0.3.0
-----
*2016-06-14*

- ([#43](https://github.com/trello/victor/pull/43)) Experimental SVG -> Android drawable support

0.2.0
-----
*2016-03-24*

- ([#40](https://github.com/trello/victor/pull/40)) Fixed some codec issues with Batik 1.8
- ([#38](https://github.com/trello/victor/pull/38)) Support Gradle 2.12

0.1.5
-----
*2015-08-07*

- (#28) Upgraded underlying rasterization lib to batik 1.8

0.1.4
-----
*2015-05-07*

- (#17) Fixed rasterize task being needlessly re-executed when using the Gradle daemon

0.1.3
-----
*2015-04-30*

- (#14) Added support for using Victor in Android libraries

0.1.2
-----
*2015-03-10*

- Fixed plugin when no excluded densities are defined

0.1.1
-----
*2015-03-10*

- #8: Handle invalid SVGs gracefully

0.1.0
-----
*2015-03-03*

Initial release! It has support for defining SVG sourcesets and rasterizing them for use as resources.
