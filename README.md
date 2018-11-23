# bGLOOP
A better GLOOP.

This is a reimplementation of the [GLOOP library](http://www.brd.nrw.de/lerntreffs/informatik/structure/material/sek2/einfuehrungen/gloop.php).
It's still work in progress but all major features have already been implemented as well as some additional
cool things. However, some side features from GLOOP are still missing.
Take a look at the [project's web page](http://trent2.github.io/bGLOOP) for a short comparison of differences to
the GLOOP library (as of January 2016).

## Prerequisites
* Java JDK >= 1.7
* [jogl](http://www.jogamp.org) >= 2.3.2 You need to place the files jogl-all.jar
  and gluegen-rt.jar in a directory called jogl-2.3.2. If you use a more recent version
  of jogl, adjust the jogl-path in the file build.properties

## Building
* Compile by typing <code>ant</code>
* On success a file bGLOOP_unguarded_bxx.jar will be placed in the dist directory which is the bGLOOP library. Just rename
  safely to bGLOOP.jar -- the xx is a running build counter.
* If you want to use [ProGuard](https://sourceforge.net/projects/proguard/) to obfuscate the byte-code there is a target inside
  build.xml. Place proguard.jar into the lib directory and uncomment the task-definition in build.xml.
* Now you can type <code>ant proguard</code>

## Download
If you do not want to build the <code>bGLOOP.jar</code> file by yourself you can [download it here](http://trent2.github.io/bGLOOP/dist/bGLOOP.jar). Also go ahead and grab the [jogamp jar files](http://jogamp.org/deployment/v2.3.2/jar/) (as described above) and place them in your classpath. You should be good now. Finally, if you want to change the default settings in the library download the <code>.bGLOOP</code> file from above, change all necessary properties, and place it in your classpath.

## Using
* be sure to have <code>jogl-all.jar</code>, <code>jogl-all-natives-???.jar</code>, <code>gluegen-rt.jar</code>,
  and <code>gluegen-rt-natives-???.jar</code> along with <code>bGLOOP.jar</code> in your classpath
* start coding

## Documentation
Please have a look at the [Java API documentation](http://trent2.github.io/bGLOOP/apidocs).

## Examples

See folder examples. The examples recreate the original GLOOP ones. They are modified just a little. For example is GLNebel not part of bGLOOP.
