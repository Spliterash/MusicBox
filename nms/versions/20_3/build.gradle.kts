plugins {
    id("io.papermc.paperweight.userdev")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
dependencies {
    paperweight.paperDevBundle("1.20.3-R0.1-SNAPSHOT")
}