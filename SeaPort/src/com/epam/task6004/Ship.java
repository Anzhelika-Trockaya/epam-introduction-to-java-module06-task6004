package com.epam.task6004;

import java.util.concurrent.atomic.AtomicBoolean;

public class Ship implements Runnable {
    private static int currentId = 1;
    private final int id;
    private final int capacity;
    private int numberOfContainers;
    private final Port port;
    private final AtomicBoolean tookThePortTheShip;

    public Ship(Port port, int capacity) {
        id = currentId;
        currentId++;
        this.capacity = capacity;
        numberOfContainers = 0;
        this.port = port;
        tookThePortTheShip = new AtomicBoolean(false);
    }

    public Ship(Port port, int capacity, int numberOfContainers) {
        id = currentId;
        currentId++;
        this.capacity = capacity;
        this.numberOfContainers = numberOfContainers;
        this.port = port;
        tookThePortTheShip = new AtomicBoolean(false);
    }

    public void sailToThePort() throws InterruptedException {
        while (!tookThePortTheShip.get()) {
            tookThePortTheShip.set(port.tookShip(this));
        }
    }

    public void sailOutOfThePort() {
        System.out.println("Ship #" + id + " sailed away.");
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getNumberOfContainers() {
        return numberOfContainers;
    }

    public void setNumberOfContainers(int numberOfContainers) {
        this.numberOfContainers = numberOfContainers;
    }

    @Override
    public String toString() {
        return "Ship #" + id + "{" +
                "capacity=" + capacity +
                ", cargoWeight=" + numberOfContainers +
                '}';
    }

    @Override
    public void run() {
        try {
            sailToThePort();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
