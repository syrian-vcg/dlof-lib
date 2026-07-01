plugins {
    java
    kotlin("jvm") version "1.9.24"
    `maven-publish`
}

group = "org.dlof"
version = "1.0.1"
repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// مكتبة dlof بدون أي اعتماديات خارجية وقت التشغيل (JDK فقط: DOM parser + java.util.zip).
// هذا يجعلها قابلة للاستخدام مباشرة داخل تطبيق Android (android-app/) أو أي مشروع JVM آخر
// عبر: implementation(project(":dlof-lib"))  أو  implementation("org.dlof:dlof-lib:1.0.0")

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("GITHUB_REPOSITORY")}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
