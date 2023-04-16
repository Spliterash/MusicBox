plugins {
    id("io.papermc.paperweight.userdev")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
dependencies {
    paperweight.paperDevBundle("1.19.2-R0.1-SNAPSHOT")
}