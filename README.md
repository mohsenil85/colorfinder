
Find the most prevalent color
=============================

![Java CI](https://github.com/mohsenil85/colorfinder/workflows/Java%20CI/badge.svg)

build and run with `mvn clean package dependency:properties exec:exec`

What this is
------------
Taken from:  https://gist.github.com/ehmo/e736c827ca73d84581d812b3a27bb132

Notes
-----
1. assumes a java 8 runtime
1. uses Project Lombok for concision (eg, `@Builder`)
1. project generated with
`mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-simple -DarchetypeVersion=1.4`
1. uses https://github.com/SvenWoltmann/color-thief-java to determine color palettes 


How it works
------------

There's 2 main libraries used in this project: [color-thief-java](https://github.com/SvenWoltmann/color-thief-java)
to determine palettes, and [quasar](https://github.com/puniverse/quasar)
to facilitate concurrency.  This was my first time using fibers 
(quasar's implementation of userspace threads) and I quite enjoyed it.

The main logic of the program is the function `#processOneImage`, 
which downloads an image, checks the cache, and if it doesn't find it in the cache,
calls the Colorthief library function on it.

The rest of the program is pretty much standard plumbing and glue.  One surprise for me was that
flushing the writer buffer after every write didn't add much overhead, while improving resiliency.
Also, I explicitly 
opted in to java 8's parallel stream processing.

Fibers were very interesting to use, the whole idea is any remotly expensive blocking operation should be
forked off into its own fiber.  The intention is to convert any _thread blocking_ 
operations in to _fiber blocking_ operations, which frees up the (user space) scheduler to do more.
In fact, there's even a a function in quasar to convert an async operation back into a fiber blocking operation.
Overall, the experience with fibers was very positive, and I'm excited for userspace threads to come to
the jvm.

Next Steps
----------

1. logging
1. consume argv
1. add statistics reporting
1. make a pipeline to facilitate ^
