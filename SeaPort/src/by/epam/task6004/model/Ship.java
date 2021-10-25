package by.epam.task6004.model;

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

    void sailToThePort() throws InterruptedException {
        tookThePortTheShip.set(port.tookShip(this));
    }

    void sailOutOfThePort() {
        System.out.println("Ship #" + id + " sailed away.");
    }

    int getId() {
        return id;
    }

    int getCapacity() {
        return capacity;
    }

    int getNumberOfContainers() {
        return numberOfContainers;
    }

    void setNumberOfContainers(int numberOfContainers) {
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
            while (!tookThePortTheShip.get()) {
                sailToThePort();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}