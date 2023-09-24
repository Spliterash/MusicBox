java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.koca2000:NoteBlockAPI:1.6.1")

    api("com.github.cryptomorin:XSeries:9.5.0")
    api("io.github.bananapuncher714:nbteditor:7.18.7")
    api("org.bstats:bstats-bukkit:3.0.2")

    api(project(":nms"))

    compileOnly("org.yaml:snakeyaml:2.0")

}