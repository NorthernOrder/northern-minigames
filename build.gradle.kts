plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "io.github.northernorder"
version = "0.5.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonartype"
        url = uri("https://oss.sonartype.org/content/groups/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("com.destroystokyo.paper:paper-api:1.16.1-R0.1-SNAPSHOT")
    // compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation(platform("com.fasterxml.jackson:jackson-bom:2.11.1"))
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

/*
tasks.register<Copy>("filter") {
    from("src/main/resources/plugin.yml")
    into("$buildDir/resources/plugin.yml")
    // expand("version" to version)
    filter(ReplaceTokens::class, "tokens" to mapOf("version" to version))
}
*/

