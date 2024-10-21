plugins {
    id("io.papermc.paperweight.userdev")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}