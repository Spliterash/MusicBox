package ru.spliterash.musicbox.utils.classes;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class PeekList<T> {

    private final List<T> list;
    private final Lock lock = new ReentrantLock();

    public PeekList(List<T> source) {
        this.list = source;
    }

    int last = -1;

    public synchronized List<T> getNextElements(int size) {
        int currentLast = last;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(peek());
        }
        last = currentLast;
        return list;
    }

    public T current() {
        return list.get(last);
    }

    public T peek() {
        lock.lock();
        try {
            last++;
            if (last == list.size()) {
                last = 0;
            }
            return current();
        } finally {
            lock.unlock();
        }
    }

    public T peekPrev() {
        lock.lock();
        try {
            last--;
            if (last == -1) {
                last = list.size() - 1;
            }
            return current();
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
                list.add(peekPrev());
            }
            last = currentLast;
            return list;
        } finally {
            lock.unlock();
        }
    }
}
