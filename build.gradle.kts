plugins {
    java
    id("io.freefair.lombok") version "6.3.0"
    `maven-publish`
}

group = "io.heartpattern"
version = "1.0.0"

repositories {
    maven("https://repo.heartpattern.io/repository/maven-public/")
}

dependencies {
    implementation("io.papermc.paper", "paper-api", "1.17.1-R0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    create("generatePluginYml") {
        doLast {
            buildDir.mkdirs()
            val file = File(buildDir, "plugin.yml")
            file.createNewFile()

            file.writeText(
                """
                name: SquareHealthCheck
                version: $version
                main: io.heartpattern.squarehealthcheck.SquareHealthCheckPlugin
                description: Lightweight healthcheck framework for paper.
                website: https://github.com/HeartPattern/square-health-check/
                author: HeartPattern
                """.trimIndent()
            )
        }
    }

    jar {
        dependsOn("generatePluginYml")
        from(File(buildDir, "plugin.yml"))
        archiveFileName.set("square-health-check.jar")
    }
}

val mavenUsername = properties["maven.username"] as? String
val mavenPassword = properties["maven.password"] as? String

if (mavenUsername != null && mavenPassword != null) {
    publishing {
        repositories {
            maven(
                if (project.version.toString().endsWith("SNAPSHOT"))
                    "https://repo.heartpattern.io/repository/maven-public-snapshots/"
                else
                    "https://repo.heartpattern.io/repository/maven-public-releases/"
            ) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }

        publications {
            create<MavenPublication>("heartpattern") {
                artifactId = project.name
                groupId = project.group.toString()
                version = project.version.toString()

                from(components["java"])
            }
        }
    }
}
