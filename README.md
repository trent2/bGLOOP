# bGLOOP
A better GLOOP.

This is a reimplementation of the [GLOOP library](http://www.brd.nrw.de/lerntreffs/informatik/structure/material/sek2/einfuehrungen/gloop.php).
It's still work in progress but all major features have already been implemented as well as some additional
cool things. However, some side features from GLOOP are still missing.
Take a look at the [project's web page](http://trent2.github.io/bGLOOP) for a short comparison of differences to
the GLOOP library (as of January 2016).

## Prerequisites
* Java JDK >= 1.8
* [jogl](http://www.jogamp.org) >= 2.3.2

## Building
* Compile by typing <code>ant</code>

## bGLOOP and BlueJ

* Download the [latest release](https://github.com/mikebarkmin/bGLOOP/releases)
* Extract the archive
* Reference the libraries in bGLOOP/libraries in BlueJ by adding them to "User Libraries from config"
  * Open "Tools/Preferences..."
  * Go to the tab "Libraries"
  * Click on a "Add File" and add all .jar Files in the folder bGlOOP/libraries
  * Restart BlueJ and Enjoy :smile:

## Documentation
Please have a look at the [Java API documentation](http://trent2.github.io/bGLOOP/apidocs).

## Examples

See folder examples. The examples recreate the original GLOOP ones. They are modified just a little. For example is GLNebel not part of bGLOOP.
