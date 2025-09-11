import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.internal.minecraft.task.RemapJarTaskImpl

plugins {
    id("xyz.wagyourtail.unimined")
    alias(libs.plugins.shadow)
}

evaluationDependsOn(":versions")

val minecraft_version: String by extra
val yarn_version: String by extra

val fabric_version: String by extra
val forge_version: String by extra

val fabric_api_version: String by extra
val modmenu_version: String by extra

val core = rootProject.sourceSets.main.get()

val archives_base_name: String by extra

base {
    archivesName = "$archives_base_name-$minecraft_version"
}

val client by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + rootProject.sourceSets.main.get().output + core.output
    runtimeClasspath += sourceSets.main.get().output + rootProject.sourceSets.main.get().output + core.output
}

val forge by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + client.output + core.output
    runtimeClasspath += sourceSets.main.get().output + client.output + core.output
}

val fabric by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + client.output + core.output
    runtimeClasspath += sourceSets.main.get().output + client.output + core.output
}

sourceSets.main {
    compileClasspath += core.output
    runtimeClasspath += core.output
}

manifold {
    preprocessor {
        config {
            property("MCV", minecraft_version.split(".").joinToString("") { it.padStart(2, '0') }.removePrefix("0"))
        }
    }
}

unimined.minecraft {
    version(minecraft_version)
    side("server")

    mappings {
        intermediary()
        yarn(yarn_version)

        stubs("intermediary", "yarn") {
            c("net/minecraft/class_1496") {
                m("method_56680;()Lnet/minecraft/class_1263;", "getInventoryVanilla")
            }
            c("net/minecraft/class_329") {
                m("method_1759;(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", "renderHotbarVanilla")
            }
        }

        devNamespace("yarn")
    }

    accessWidener {
        accessWidener(file("src/main/resources/jsmacros.accesswidener"))
    }

    if (sourceSet == sourceSets.main.get() || sourceSet == client) {
        defaultRemapJar = false
        runs.off = true
    }
}

unimined.minecraft(client) {
    combineWith("main")
    combineWith(":main")
    side("joined")
}

unimined.minecraft(forge) {
    combineWith("main")
    side("joined")

    minecraftRemapper.config {
        ignoreConflicts(true)
    }

    neoForge {
        loader(forge_version)
        accessTransformer(aw2at(file("src/main/resources/jsmacros.accesswidener")))

        mixinConfig = listOf(
            "jsmacros-forge.mixins.json",
            "jsmacros-client.mixins.json",
            "jsmacros-common.mixins.json",
        )
    }
}

unimined.minecraft(fabric) {
    combineWith("main")
    side("joined")

    fabric {
        loader(fabric_version)
        accessWidener(file("src/main/resources/jsmacros.accesswidener"))
    }
}

val minecraftLibraries by configurations.getting

configurations.implementation.configure {
    isCanBeResolved = true
}

val jsmacrosExtensionInclude by configurations.creating
val shadowForge by configurations.creating

val clientCompileOnly by configurations.getting {
    extendsFrom(configurations.compileOnly.get())
}

dependencies {
    val clientImplementation by configurations.getting
    val fabricModImplementation by configurations.getting
    val fabricInclude by configurations.getting
    val fabricRuntimeOnly by configurations.getting
    val forgeInclude by configurations.getting
    val forgeRuntimeOnly by configurations.getting

    compileOnly(libs.mixin)
    compileOnly(libs.mixin.extra)

    implementation(libs.asm)
    implementation(libs.joor)
    implementation(libs.nv.websocket)
    implementation(libs.javassist)

    clientImplementation(libs.prism4j)

    jsmacrosExtensionInclude(project(":extension:graal")) { isTransitive = false }
    jsmacrosExtensionInclude(project(":extension:graal:js")) { isTransitive = false }

    fabricModImplementation(fabricApi.fabricModule("fabric-api-base", fabric_api_version))
    fabricModImplementation(fabricApi.fabricModule("fabric-lifecycle-events-v1", fabric_api_version))
    fabricModImplementation(fabricApi.fabricModule("fabric-key-binding-api-v1", fabric_api_version))
    fabricModImplementation(fabricApi.fabricModule("fabric-resource-loader-v0", fabric_api_version))
    fabricModImplementation(fabricApi.fabricModule("fabric-command-api-v2", fabric_api_version))

    fabricModImplementation(libs.modmenu.get().module.toString() + ":$modmenu_version")
    fabricModImplementation(fabricApi.fabricModule("fabric-screen-api-v1", fabric_api_version))

    fabricInclude(fabricApi.fabricModule("fabric-api-base", fabric_api_version))
    fabricInclude(fabricApi.fabricModule("fabric-lifecycle-events-v1", fabric_api_version))
    fabricInclude(fabricApi.fabricModule("fabric-key-binding-api-v1", fabric_api_version))
    fabricInclude(fabricApi.fabricModule("fabric-resource-loader-v0", fabric_api_version))
    fabricInclude(fabricApi.fabricModule("fabric-command-api-v2", fabric_api_version))

    fabricInclude(libs.prism4j)
    fabricInclude(libs.nv.websocket)
    fabricInclude(libs.javassist)
    fabricInclude(libs.joor)

    forgeInclude(libs.prism4j) {
        exclude(module = "annotations-java5")
    }
    forgeInclude(libs.nv.websocket)
    forgeInclude(libs.javassist)
    forgeInclude(libs.joor)

    for (file in file("extension").listFiles() ?: emptyArray()) {
        if (!file.isDirectory || file.name in listOf("build", "src")) continue

        fabricRuntimeOnly(project(":extension:${file.name}"))
        forgeRuntimeOnly(project(":extension:${file.name}"))

        if (file.resolve("subprojects.txt").exists()) {
            for (subproject in file.resolve("subprojects.txt").readLines()) {
                fabricRuntimeOnly(project(":extension:${file.name}:$subproject"))
                forgeRuntimeOnly(project(":extension:${file.name}:$subproject"))
            }
        }
    }

}

//val processCoreResources by tasks.getting(ProcessResources::class) {
//    inputs.property("dependencies", jsmacrosExtensionInclude.files)
//
//    filesMatching("jsmacros.extension.json") {
//        expand("dependencies" to jsmacrosExtensionInclude.files.map { "\"META-INF/jsmacrosdeps/${it.name}\"" }.joinToString(", "))
//    }
//}

val processForgeResources by tasks.getting(ProcessResources::class) {
    inputs.property("version", project.version)
    inputs.property("mc_version", minecraft_version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            "version" to project.version,
            "mc_version" to minecraft_version
        )

    }
}

val processFabricResources by tasks.getting(ProcessResources::class) {
    inputs.property("version", project.version)
    inputs.property("mc_version", minecraft_version)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "mc_version" to minecraft_version
        )
    }
}

val forgeJar by tasks.getting(Zip::class) {
    dependsOn(":extension:graal:jar")
    dependsOn(":extension:graal:js:jar")
    dependsOn("forgeClasses", "clientClasses", ":classes")
    archiveClassifier.set("forge-dev")
    from(forge.output, sourceSets.main.get().output, core.output, client.output)

    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

val fabricJar by tasks.getting(Zip::class) {
    dependsOn(":extension:graal:jar")
    dependsOn(":extension:graal:js:jar")
    dependsOn("fabricClasses", "clientClasses", ":classes")
    from(fabric.output, sourceSets.main.get().output, core.output, client.output)

    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(jsmacrosExtensionInclude.files) {
        include("*")
        into("META-INF/jsmacrosdeps")
    }
}

val shadowForgeJar by tasks.registering(ShadowJar::class) {
    archiveClassifier = "forge-dev-shadow"
    from(forgeJar)

    // exclude icu that"s "accidentally" (I hope) included in graaljs jar
    exclude("com/ibm/**")

    mergeServiceFiles()

    configurations = listOf(shadowForge)
}

val remapForgeJar by tasks.getting(RemapJarTaskImpl::class) {
    dependsOn(shadowForgeJar)
    inputFile = shadowForgeJar.get().archiveFile.get().asFile

    from(jsmacrosExtensionInclude.files) {
        include("*")
        into("META-INF/jsmacrosdeps")
    }

    manifest {
        attributes(
            "MixinConnector" to "xyz.wagyourtail.jsmacros.forge.client.JsMacrosEarlyRiser",
        )
    }

    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

val remapFabricJar by tasks.getting(RemapJarTaskImpl::class) {

    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.jar {
    enabled = false
}
