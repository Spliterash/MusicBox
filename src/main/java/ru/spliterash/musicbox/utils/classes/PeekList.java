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
@Getter
public class PeekList<T> {

    private final List<T> list;
    private final boolean hasEnd;
    private final Lock lock = new ReentrantLock();
    int last = 0;

    public PeekList(List<T> source) {
        this(source, false);
    }

    public PeekList(List<T> source, boolean hasEnd) {
        this.list = source;
        this.hasEnd = hasEnd;
    }

    public synchronized List<T> getNextElements(int size) {
        int currentLast = last;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (!next())
                break;
            list.add(current());
        }
        last = currentLast;
        return list;
    }

    public boolean hasNext() {
        if (hasEnd) {
            return last < list.size() - 1;
        } else
            return true;
    }

    public boolean hasPrev() {
        if (hasEnd) {
            return last > 0;
        } else
            return true;
    }

    public T current() {
        return list.get(last);
    }

    public T nextAndGet() {
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
                last++;
                if (last == list.size()) {
                    last = 0;
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
                last--;
                if (last <= -1) {
                    last = list.size() - 1;
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
            int currentLast = last;
            List<T> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if (!prev())
                    break;
                list.add(current());
            }
            last = currentLast;
            return list;
        } finally {
            lock.unlock();
        }
    }

    public int getIndexOf(T element) {
        return list.indexOf(element);
    }
}
