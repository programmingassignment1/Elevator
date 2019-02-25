package com.ru.usty.elevator;

public class Person implements Runnable {

    int source, dest;

    Person(int source, int dest) {

        this.source = source;
        this.dest = dest;
    }

    @Override
    public void run() {

        try {
            ElevatorScene.sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Person thread");
    }
}

