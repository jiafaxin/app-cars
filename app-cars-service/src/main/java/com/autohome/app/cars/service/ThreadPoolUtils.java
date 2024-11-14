package com.autohome.app.cars.service;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
    public static ExecutorService defaultThreadPoolExecutor = new ThreadPoolExecutor(
            50,
            1000,
            120, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());  //阻塞队列

}
