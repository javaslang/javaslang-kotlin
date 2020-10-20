import groovy.lang.Closure
import java.net.URI

plugins {
    `jvm-component`
    `java-lang`
    maven
    jacoco
    id("com.bmuschko.nexus") version "2.3.1"
    id("org.jetbrains.kotlin.jvm") version "1.3.11"
}

group = "io.vavr"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.vavr:vavr:1.0.0-SNAPSHOT")
    testCompile("junit:junit:4.12")
}

tasks {

    create<JacocoReport>("codeCoverageReport") {
        executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        sourceSets(sourceSets["main"])

        reports {
            xml.isEnabled = true
            xml.destination = file("$buildDir/reports/jacoco/report.xml")
            html.isEnabled = true
            csv.isEnabled = false
        }

        dependsOn(project.getTasksByName("test", false))
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        manifest {
            attributes(
                mutableMapOf(
                    "Automatic-Module-Name" to "io.vavr.kotlin"
                )
            )
        }
    }
}

val modifyPom: Closure<MavenPom> by ext

modifyPom(
    closureOf<MavenPom> {
        project {
            withGroovyBuilder {
                "name"("Vavr Kotlin")
                "description"("Vavr integration for Kotlin")
                "url"("http://vavr.io")
                "inceptionYear"("2017")

                "scm" {
                    "connection"("scm:git:git@github.com:vavr-io/vavr-kotlin.git")
                    "developerConnection"("scm:git:git@github.com:vavr-io/vavr-kotlin.git")
                    "url"("git@github.com:vavr-io/vavr-kotlin.git")
                }

                "licenses" {
                    "license" {
                        "name"("The Apache Software License, Version 2.0")
                        "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                "developers" {
                    "developer" {
                        "id"("zvozin")
                        "name"("Alex Zuzin")
                        "url"("https://github.com/zvozin")
                    }
                    "developer" {
                        "id"("ruslansennov")
                        "name"("Ruslan Sennov")
                        "email"("ruslan.sennov@gmail.com")
                    }
                }
            }
        }
    }
)

nexus {
    sign = !version.toString().endsWith("SNAPSHOT")
}
