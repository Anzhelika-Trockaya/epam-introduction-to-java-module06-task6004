package com.epam.task6004.model;

import java.util.concurrent.atomic.AtomicInteger;

public class ShipCreator implements Runnable {
    private final Port port;
    private final int numberOfShip;
    private final AtomicInteger resultNumberOfContainers;

    public ShipCreator(Port port, int numberOfShip) {
        this.port = port;
        this.numberOfShip = numberOfShip;
        resultNumberOfContainers = new AtomicInteger(port.getRealNumberOfContainers());
    }

    @Override
    public void run() {
        Ship currentShip;
        for (int i = 0; i < numberOfShip - 1; i++) {
            currentShip = createShip();
            changeResultNumberOfContainers(currentShip);
            new Thread(currentShip).start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        currentShip = createLastShip();
        new Thread(currentShip).start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Ship createShip() {
        if (resultNumberOfContainers.get() >= port.getCapacity()) {
            return createEmptyShip();
        } else if (resultNumberOfContainers.get() <= 0) {
            return createFullShip();
        } else {
            return createRandomShip();
        }
    }

    private Ship createRandomShip() {
        double random = Math.random();
        if (random >= 0.5) {
            return createFullShip();
        } else {
            return createEmptyShip();
        }
    }

    private Ship createEmptyShip() {
        int capacity = (int) Math.floor((Math.random() * (26 - 10)) + 10) * 100;
        return new Ship(port, capacity);
    }

    private Ship createFullShip() {
        int capacity = (int) Math.floor((Math.random() * (26 - 10)) + 10) * 100;
        return new Ship(port, capacity, capacity);
    }

    private Ship createLastShip() {
        Ship lastShip;
        if (resultNumberOfContainers.get() >= 2500 && resultNumberOfContainers.get() <= 7500) {
            lastShip = createRandomShip();
        } else if (resultNumberOfContainers.get() < 2500) {
            lastShip = new Ship(port, 2500, 2500);
        } else {
            lastShip = new Ship(port, 2500, 0);
        }
        return lastShip;
    }

    private void changeResultNumberOfContainers(Ship currentShip) {
        int shipNumberOfContainers = currentShip.getNumberOfContainers();
        if (shipNumberOfContainers > 0) {
            resultNumberOfContainers.getAndAdd(shipNumberOfContainers);
        } else {
            resultNumberOfContainers.getAndAdd(-currentShip.getCapacity());
        }
    }
}
