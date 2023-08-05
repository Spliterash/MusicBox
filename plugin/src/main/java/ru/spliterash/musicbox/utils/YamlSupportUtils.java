package ru.spliterash.musicbox.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import ru.spliterash.musicbox.MusicBoxConfig;

import java.lang.reflect.Constructor;

@UtilityClass
public class YamlSupportUtils {
    @SneakyThrows
    @SuppressWarnings("JavaReflectionMemberAccess")
    public static CustomClassLoaderConstructor createCustomClassLoaderConstructor() {
        try {
            Constructor<?> constructor = CustomClassLoaderConstructor.class.getConstructor(Class.class, ClassLoader.class);
            return (CustomClassLoaderConstructor) constructor.newInstance(MusicBoxConfig.class, MusicBoxConfig.class.getClassLoader());
        } catch (NoSuchMethodException e){
            return new CustomClassLoaderConstructor(MusicBoxConfig.class, MusicBoxConfig.class.getClassLoader(), new LoaderOptions());
        }
    }
}
