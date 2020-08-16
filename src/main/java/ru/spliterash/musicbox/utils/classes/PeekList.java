package ru.spliterash.musicbox.utils.classes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PeekList<T> {

    private final List<T> list;

    public PeekList(List<T> source) {
        this.list = source;
    }

    int last = 0;

    public List<T> getNextElements(int size) {
        int currentLast = last;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(peek());
        }
        last = currentLast;
        return list;
    }

    public T peek() {
        if (last < list.size() - 1) {
            return list.get(last++);
        } else {
            T element = list.get(last);
            last = 0;
            return element;
        }
    }
}
