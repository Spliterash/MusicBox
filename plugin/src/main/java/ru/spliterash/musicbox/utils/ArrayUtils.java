package ru.spliterash.musicbox.utils;

import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Random;

@UtilityClass
public class ArrayUtils {
    public void replaceOrRemove(List<String> list, String key, String value) {
        for (int i = 0; i < list.size(); i++) {
            String element = list.get(i);
            if (!element.contains(key))
                continue;
            if (value != null && !value.isEmpty()) {
                list.set(i, element.replace(key, value));
            } else {
                list.remove(i);
                i--;
            }

        }
    }

    public <T> T[] removeFirst(Class<T> clazz, T[] source) {
        if (source.length == 0)
            return source;
        //noinspection unchecked
        T[] result = (T[]) Array.newInstance(clazz, source.length - 1);
        System.arraycopy(source, 1, result, 0, source.length - 1);
        return result;
    }


    /**
     * Заменяет элементы в мапе
     * Если элемент не найден, то удаляет строку
     *
     * @param list       Лист в котором надо заменить
     * @param replaceMap Карта замены
     * @return тот же лист
     */
    public List<String> replaceOrRemove(List<String> list, Map<String, String> replaceMap) {
        for (int i = 0; i < list.size(); i++) {
            String element = list.get(i);
            boolean found = false;
            String newString = element;
            boolean contains = false;
            for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
                String key = entry.getKey();
                //Есть ли в этой строке вообще плейсхолдеры
                if (!element.contains(key))
                    continue;
                String value = entry.getValue();
                contains = true;
                if (value == null || value.isEmpty()) {
                    newString = newString.replace(entry.getKey(), "");
                    continue;
                }
                //Уже не удаляем
                found = true;
                newString = newString.replace(entry.getKey(), entry.getValue());
            }
            if (!contains)
                continue;
            if (!found) {
                list.remove(i);
                i--;
            } else {
                list.set(i, newString);
            }

        }
        return list;
    }

    public <T> T getRandom(List<T> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

}
