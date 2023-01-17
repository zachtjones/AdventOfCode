

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"

    //application
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21")
    // a good constraint satisfaction library: api("org.choco-solver:choco-solver:4.10.10")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.7.21")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("io.kotest:kotest-assertions-jvm:4.0.7")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

group = "me.zjones2"
version = "1.0-SNAPSHOT"
description = "AdventOfCode"
java.sourceCompatibility = JavaVersion.VERSION_11

/*
publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}



tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
*/