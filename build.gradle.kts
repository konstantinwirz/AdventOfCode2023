plugins {
    application

    id("io.freefair.lombok") version "8.4"
}

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("dev.wirz.aoc2023.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.compileJava {
    options.compilerArgs.addAll(listOf("--enable-preview"))
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-preview", "-ea")
}