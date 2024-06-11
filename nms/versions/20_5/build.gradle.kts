plugins {
    id("io.papermc.paperweight.userdev")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
dependencies {
    paperweight.paperDevBundle("1.20.5-R0.1-SNAPSHOT")
}