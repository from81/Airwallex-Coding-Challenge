import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    java
    kotlin("jvm") version "1.3.21"
    application
    jacoco
}

group = "com.airwallex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.pivovarit", "parallel-collectors", "1.1.0")
    implementation("com.googlecode.json-simple", "json-simple", "1.1.1")
    implementation("org.apache.logging.log4j", "log4j-core", "2.14.1")
    implementation("org.apache.logging.log4j", "log4j-api", "2.14.1")
    implementation("org.javatuples", "javatuples", "1.2")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.9+")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9+")
    testCompile("junit:junit:4.12")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
    testImplementation("org.assertj", "assertj-core", "3.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClassName = "com.airwallex.codechallenge.App"
}

jacoco {
    toolVersion = "0.8.6"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // report is always generated after tests run
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

tasks.withType(JacocoReport::class.java).all {
    dependsOn(test) // tests are required to run before generating the report
    reports {
        csv.isEnabled = false
        xml.isEnabled = true
        xml.destination = file("output/reports/coverage/jacoco.xml")
        html.isEnabled = true
        html.destination = file("output/reports/coverage")
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage."

    dependsOn(":test", ":jacocoTestReport")
    val jacocoTestReport = tasks.findByName("jacocoTestReport")
    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
}