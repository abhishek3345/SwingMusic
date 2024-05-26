package com.example.swingmusic;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseExecutor {
    private static final Executor databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static Executor getExecutor() {
        return databaseWriteExecutor;
    }
}

