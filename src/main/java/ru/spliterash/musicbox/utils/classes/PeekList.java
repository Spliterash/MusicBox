package ru.spliterash.musicbox.utils.classes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Класс для поочерденого получения элементов
 * Не класть листы содержащие null, а то всё пойдёт по *****
 *
 * @param <T>
 */
@SuppressWarnings("unused")
@Getter
public class PeekList<T> {

    private final List<T> list;
    private final boolean hasEnd;
    private final Lock lock = new ReentrantLock();
    int current = 0;

    public PeekList(List<T> source) {
        this(source, false);
    }

    public PeekList(List<T> source, boolean hasEnd) {
        this.list = source;
        this.hasEnd = hasEnd;
    }

    public synchronized List<T> getNextElements(int size) {
        int currentLast = current;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (!next())
                break;
            list.add(current());
        }
        current = currentLast;
        return list;
    }

    public boolean hasNext() {
        if (hasEnd) {
            return current < list.size() - 1;
        } else
            return true;
    }

    public boolean hasPrev() {
        if (hasEnd) {
            return current > 0;
        } else
            return true;
    }

    public T current() {
        return list.get(current);
    }

    /**
     * Возращает текущее значение и сдвигает указатель вперёд
     */
    public T getAndNext() {
        T c = current();
        next();
        return c;
    }

    public T prevAndGet() {
        if (prev())
            return current();
        else
            return null;
    }

    public boolean next() {
        lock.lock();
        try {
            if (hasNext()) {
                current++;
                if (current == list.size()) {
                    current = 0;
                }
                return true;
            } else
                return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean prev() {
        lock.lock();
        try {
            if (hasPrev()) {
                current--;
                if (current <= -1) {
                    current = list.size() - 1;
                }
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public List<T> getPrevElements(int size) {
        lock.lock();
        try {
            int currentLast = current;
            List<T> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if (!prev())
                    break;
                list.add(current());
            }
            current = currentLast;
            return list;
        } finally {
            lock.unlock();
        }
    }

    public int getIndexOf(T element) {
        return list.indexOf(element);
    }

    public void moveTo(T element) {
        int index = list.indexOf(element);
        if (index != -1)
            current = index;
    }
}
