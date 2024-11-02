plugins {
    id("io.papermc.paperweight.userdev")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
dependencies {
    // 1.21.2 нет, но нарушать порядок версий у себя я не хочу, так что давайте представим что тут 1.21.2
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
}