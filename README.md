
Find the most prevalent color 
==============================

What this is
------------
Taken from:  https://gist.github.com/ehmo/e736c827ca73d84581d812b3a27bb132

Notes
---
1. assumes a java 8 runtime
1. uses Project Lombok for concision (eg, `@Data`)
1. project generated with
`mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-simple -DarchetypeVersion=1.4`
1. uses https://github.com/SvenWoltmann/color-thief-java to determine color palettes 

1. build with `mvn clean package`
1. run with `java -jar target/colorfinder-1.0-SNAPSHOT.jar -Xms2G -Xmx8G`

How it works
----------------------


Next Steps
----

1. logging
1. consume argv
1. create a mvn profile for the jmh runner 
(i've been running it from my IDE)
1. add statistics reporting
1. make a pipeline to facilitate ^
