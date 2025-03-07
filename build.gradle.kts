import java.time.Instant

plugins {
    `java-library`

    alias(libs.plugins.shadow) // Shades and relocates dependencies, see https://gradleup.com/shadow/
    alias(libs.plugins.run.paper) // Built in test server using runServer and runMojangMappedServer tasks
    alias(libs.plugins.plugin.yml) // Automatic plugin.yml generation
    //alias(libs.plugins.paperweight) // Used to develop internal plugins using Mojang mappings, See https://github.com/PaperMC/paperweight

    eclipse
    idea
}

val mainPackage = "${project.group}.${project.name.lowercase()}"
applyCustomVersion()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 8 installed for example.
    withJavadocJar() // Enable javadoc jar generation
    withSourcesJar() // Enable sources jar generation
}

repositories {
    mavenCentral() // Scoreboard-library

    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://mvn-repo.arim.space/lesser-gpl3/")

    maven("https://maven.athyrium.eu/releases") // Milkdrinkers

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://jitpack.io/") {
        content {
            includeGroup("com.github.MilkBowl") // VaultAPI
        }
    }
    maven("https://mvn.lumine.io/repository/maven-public/") { // MythicMobs
        content { includeGroup("io.lumine") }
    }
    maven("https://maven.enginehub.org/repo/") // WorldGuard
    maven("https://repo.glaremasters.me/repository/towny/") { // Towny
        content { includeGroup("com.palmergames.bukkit.towny") }
    }
}

dependencies {
    // Core dependencies
    compileOnly(libs.annotations)
    annotationProcessor(libs.annotations)
    //paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT") // Use instead of the `paper-api` entry if developing plugins using Mojang mappings
    compileOnly(libs.paper.api)
    implementation(libs.morepaperlib)

    // API
    implementation(libs.version.watch)
    implementation(libs.crate.api)
    implementation(libs.crate.yaml)
    implementation(libs.colorparser) {
        exclude("net.kyori")
    }
    implementation(libs.commandapi.shade)
    //annotationProcessor(libs.commandapi.annotations) // Uncomment if you want to use command annotations
    implementation(libs.triumph.gui) {
        exclude("net.kyori")
    }
    implementation(libs.scoreboardlibraryapi)
    runtimeOnly(libs.scoreboardlibraryruntime)
    runtimeOnly(libs.scoreboardlibrarymodernpacketadapter)

    // Plugin dependencies
    compileOnly(libs.vault)
    compileOnly(libs.placeholderapi) {
        exclude("me.clip.placeholderapi.libs", "kyori")
    }
    compileOnly(libs.mythicmobs)
    compileOnly(libs.worldguardbukkit)
    compileOnly(libs.towny)

    // Testing - Core
    testImplementation(libs.annotations)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.slf4j)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.testcontainers)
}

tasks {
    // NOTE: Use when developing plugins using Mojang mappings
//    assemble {
//        dependsOn(reobfJar)
//    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
        options.compilerArgs.addAll(arrayListOf("-Xlint:all", "-Xlint:-processing", "-Xdiags:verbose"))
    }

    javadoc {
        isFailOnError = false
        val options = options as StandardJavadocDocletOptions
        options.encoding = Charsets.UTF_8.name()
        options.overview = "src/main/javadoc/overview.html"
        options.windowTitle = "${rootProject.name} Javadoc"
        options.tags("apiNote:a:API Note:", "implNote:a:Implementation Note:", "implSpec:a:Implementation Requirements:")
        options.addStringOption("Xdoclint:none", "-quiet")
        options.use()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")

        // Shadow classes
        fun reloc(originPkg: String, targetPkg: String) = relocate(originPkg, "${mainPackage}.lib.${targetPkg}")

        reloc("space.arim.morepaperlib", "morepaperlib")
        reloc("io.github.milkdrinkers.crate", "crate")
        reloc("io.github.milkdrinkers.colorparser", "colorparser")
        reloc("io.github.milkdrinkers.versionwatch", "versionwatch")
        reloc("dev.jorel.commandapi", "commandapi")
        reloc("dev.triumphteam.gui", "gui")
        reloc("fr.mrmicky.fastboard", "fastboard")

        minimize()
    }

    test {
        useJUnitPlatform()
        failFast = false
    }

    runServer {
        // Configure the Minecraft version for our task.
        minecraftVersion("1.21.4")

        // IntelliJ IDEA debugger setup: https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true", "-DIReallyKnowWhatIAmDoingISwear", "-Dpaper.playerconnection.keepalive=6000")
        systemProperty("terminal.jline", false)
        systemProperty("terminal.ansi", true)

        // Automatically install dependencies
        downloadPlugins {
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            hangar("PlaceholderAPI", "2.11.6")
            modrinth("FastAsyncWorldEdit", "2.12.3")
            modrinth("WorldGuard", "7.0.13")
            modrinth("MythicMobs", "5.8.0")
        }
    }
}

bukkit { // Options: https://github.com/Minecrell/plugin-yml#bukkit
    // Plugin main class (required)
    main = "${mainPackage}.${project.name}"

    // Plugin Information
    name = project.name
    prefix = project.name
    version = "${project.version}"
    description = "${project.description}"
    authors = listOf("ShermansWorld")
    contributors = listOf()
    apiVersion = "1.21"
    foliaSupported = false // Mark plugin as supporting Folia

    // Misc properties
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD // STARTUP or POSTWORLD
    depend = listOf("MythicMobs", "WorldGuard")
    softDepend = listOf("Vault", "PlaceholderAPI, Towny")
    loadBefore = listOf()
    provides = listOf()
}

fun applyCustomVersion() {
    // Apply custom version arg or append snapshot version
    val ver = properties["altVer"]?.toString() ?: "${rootProject.version}-SNAPSHOT-${Instant.now().epochSecond}"

    // Strip prefixed "v" from version tag
    rootProject.version = (if (ver.first().equals('v', true)) ver.substring(1) else ver.uppercase()).uppercase()
}