package com.bilalekrem.ruddergame.net;

/**
 * This is base class for Server threads. They all have start
 * and stop methods to starting thread and stopping with a 
 * boolean variable. Avoiding repating codes, using this class
 * a base class for threads. 
 */
abstract class ServerThread implements Runnable {
    boolean run;

    ServerThread() {
        run = true;
    }

    @Override
    public abstract void run();

    public void stop() {
        run = false;
    }

    public void start() {
        run = true;
        new Thread(this).start();
    }
}