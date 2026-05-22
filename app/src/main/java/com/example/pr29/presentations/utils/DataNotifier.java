package com.example.pr29.presentations.utils;

import java.util.ArrayList;
import java.util.List;

public class DataNotifier {

    static DataNotifier instance;
    List<Runnable> listeners = new ArrayList<>();

    public static DataNotifier getInstance() {
        if (instance == null) {
            instance = new DataNotifier();
        }

        return instance;
    }

    public void subscribe(Runnable listener) {
        listeners.add(listener);
    }

    public void notifyUpdate() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
