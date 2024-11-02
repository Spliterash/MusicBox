plugins {
    `java-library`
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.freefair.lombok") version "8.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
}

bukkit {
    name = "MusicBox"
    main = "ru.spliterash.musicbox.MusicBox"
    apiVersion = "1.13"
    authors = listOf("Spliterash")
    depend = listOf("NoteBlockAPI")
    softDepend = listOf("Vault")
    commands {
        register("musicbox")
    }
}

group = "ru.spliterash"

allprojects {
    apply(plugin = "java-library");
    apply(plugin = "io.freefair.lombok")

    version = "2.1.4"

    repositories {
        mavenCentral()
        mavenLocal()

        maven("https://jitpack.io")
        maven("https://repo.codemc.org/repository/maven-public")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.clojars.org/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
    }
}


val startPath = "ru.spliterash.musicbox.shadow."
tasks.shadowJar {
    relocate("com.cryptomorin.xseries", startPath + "xseries")
    relocate("org.bstats", startPath + "bstats")
    relocate("io.github.bananapuncher714.nbteditor", startPath + "nbteditor")
}

tasks.assemble { dependsOn(tasks.shadowJar) }

dependencies {
    api(project(":plugin"))
    api(project(":nms"))
    api(project(":nms:shared"))
    api(project(":nms:versions:12"))
    api(project(":nms:versions:13-16"))
    api(project(":nms:versions:17"))
    api(project(":nms:versions:18"))
    api(project(":nms:versions:19_2", "reobf"))
    api(project(":nms:versions:19_3", "reobf"))
    api(project(":nms:versions:19_4", "reobf"))
    api(project(":nms:versions:20_1", "reobf"))
    api(project(":nms:versions:20_2", "reobf"))
    api(project(":nms:versions:20_3", "reobf"))
    api(project(":nms:versions:20_5", "reobf"))
    api(project(":nms:versions:21", "reobf"))
    api(project(":nms:versions:21_2", "reobf"))
}
