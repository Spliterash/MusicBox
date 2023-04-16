java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.1")

    api("com.github.cryptomorin:XSeries:9.3.1")
    api("io.github.bananapuncher714:nbteditor:7.18.5")
    api("org.bstats:bstats-bukkit:3.0.1")

    api(project(":nms"))
}