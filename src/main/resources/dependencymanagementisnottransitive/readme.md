Dependency management seems not be transitive!

See [stackoverflow](https://stackoverflow.com/questions/28312975/maven-dependencymanagement-version-ignored-in-transitive-dependencies)

Tested in the included pom files

project2.pom.xml has a dependency with com.squareup.okhttp3:okhttp:4.9.3 which has a transitivive dependency with org.jetbrains.kotlin:kotlin-stdlib:1.4.10

in the dependency management the version of org.jetbrains.kotlin:kotlin-stdlib is managed to version 1.6.10

in the dependency hierarchy of project 2 it can be seen that version 1.6.10 is effectively choosen, this can also be verified by executing mvn dependency:list


project1_uses_project2.pom.xml has a dependency with project2.pom.xml, however when viewing the dependency list of project1_uses_project2.pom.xml it is shown that org.jetbrains.kotlin:kotlin-stdlib:1.4.10 is choosen and not version 1.6.10.

**=> dependency management is not transitive**