-libraryjars <java.home>/jmods
-keepattributes *Annotation*,SourceFile,LineNumberTable,Signature
#-dontnote
-dontwarn org.slf4j.**
-dontwarn javax.**
-dontwarn ru.spliterash.musicbox.shadow.**
-dontwarn org.apache.**
-optimizationpasses 5
-overloadaggressively
-dontobfuscate
-allowaccessmodification
-dontnote

# Мои лаунчеры
-keep class * extends org.bukkit.plugin.java.JavaPlugin
# Сохраняем всё моё, разрешая запутывание
-keepclassmembers,allowobfuscation class ru.spliterash.** { *; }


-keep class sun.misc.Unsafe { *; }

# Сохраняем имена енумоф
-keepclassmembernames enum  * {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# Ещё раз на всякий случай
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Сериализацию да тоже надо
-keepclassmembernames class * extends java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Нативные методы само собой
-keepclasseswithmembers,includedescriptorclasses,allowshrinking class * {
    native <methods>;
}