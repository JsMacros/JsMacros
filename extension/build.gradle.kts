plugins {
    java
    alias(libs.plugins.shadow)
}

val archives_base_name: String by project.properties

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation(rootProject.sourceSets.main.get().output)

    for (dependency in rootProject.configurations.implementation.get().dependencies) {
        implementation(dependency)
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.jetbrains:annotations:20.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

subprojects {
    apply(plugin= "java")
    apply(plugin= "com.github.johnrengelman.shadow")

    base {
        archivesName = archives_base_name + "-${project.name}-extension"
    }

    java {
        sourceCompatibility = JavaVersion.values()[rootProject.libs.versions.java.get().toInt() - 1]
        targetCompatibility = JavaVersion.values()[rootProject.libs.versions.java.get().toInt() - 1]

        toolchain {
            languageVersion = JavaLanguageVersion.of(rootProject.libs.versions.java.get().toInt())
        }
    }

    val jsmacrosExtensionInclude by configurations.creating

    configurations.implementation.configure {
        extendsFrom(jsmacrosExtensionInclude)
    }

    repositories {
        mavenCentral()
        maven("https://libraries.minecraft.net/")
    }

    dependencies {

        implementation(parent!!.sourceSets.main.get().output)
        for (dependency in parent!!.configurations.implementation.get().dependencies) {
            implementation(dependency)
        }

        testImplementation(parent!!.sourceSets.test.get().output)
    }

    afterEvaluate {
        var includeFiles = files(jsmacrosExtensionInclude) - files(parent!!.configurations.findByName("jsmacrosExtensionInclude") ?: emptySet<File>()).filter{ it.name.endsWith(".jar") }

        tasks.jar {
            from(includeFiles) {
                include("*")
                into("META-INF/jsmacrosdeps")
            }

            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }

        tasks.processResources {
            filesMatching("jsmacros.ext.*.json") {
                expand("dependencies" to includeFiles.joinToString("\", \"") { "META-INF/jsmacrosdeps/${it.name}" })
            }
        }
    }

}