
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

1. run with `mvn clean package; java -cp target/colorfinder-1.0-SNAPSHOT.jar com.lmohseni.App`

Overall plan of attack
----------------------

1.  Use a thread pool.  Each thread will:
1.  Accept as input an image url
1.  Download the image (to InputStream?)
1.  Compress the image (quality as a config param)
1.  Find the 3 color averages
1.  write the result to something that handles many writes
1.  end thread
1.  ConcurrentHashMap? -> csv
