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
    public static CustomClassLoaderConstructor createCustomClassLoaderConstructor() {
        Constructor<?> constructor = CustomClassLoaderConstructor.class.getConstructors()[1];
        if (constructor.getParameterCount() == 2)
            return (CustomClassLoaderConstructor) constructor.newInstance(MusicBoxConfig.class, MusicBoxConfig.class.getClassLoader());
        else
            return new CustomClassLoaderConstructor(MusicBoxConfig.class, MusicBoxConfig.class.getClassLoader(), new LoaderOptions());
    }
}
