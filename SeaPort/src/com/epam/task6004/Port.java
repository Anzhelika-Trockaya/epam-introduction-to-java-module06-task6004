package com.epam.task6004;

import java.util.concurrent.atomic.AtomicInteger;

public class Port {
    private final int capacity;
    private final Pier[] piers;
    private final AtomicInteger realNumberOfContainers;
    private final AtomicInteger prospectiveNumberOfContainers;

    public Port(int capacity, int piersNumber, int numberOfContainers) {
        this.capacity = capacity;
        this.piers = new Pier[piersNumber];
        for (int i = 0; i < piersNumber; i++) {
            piers[i] = new Pier();
        }
        realNumberOfContainers = new AtomicInteger(numberOfContainers);
        prospectiveNumberOfContainers = new AtomicInteger(numberOfContainers);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRealNumberOfContainers() {
        return realNumberOfContainers.get();
    }

    public void processShip(Ship ship) throws InterruptedException {
        if (ship.getNumberOfContainers() > 0) {
            unloadShip(ship);
        } else {
            loadShip(ship);
        }
        Thread.sleep(3000);
        sendOutTheShip(ship);
    }

    public synchronized boolean isPossibleToTakeTheShip(Ship ship) {
        return hasFreePiers() && ship.getNumberOfContainers() <= numberOfMissingContainers()
                && (ship.getNumberOfContainers() != 0 || this.prospectiveNumberOfContainers.get() != 0);
    }

    public boolean tookShip(Ship ship) throws InterruptedException {
        synchronized (this) {
            if (isPossibleToTakeTheShip(ship)) {
                if (ship.getNumberOfContainers() > 0) {
                    prospectiveNumberOfContainers.getAndAdd(ship.getNumberOfContainers());
                } else {
                    prospectiveNumberOfContainers.getAndAdd(-ship.getCapacity());
                }
                takeTheShipToAFreePier(ship);
            } else {
                return false;
            }
        }
        processShip(ship);
        return true;
    }

    public synchronized void takeTheShipToAFreePier(Ship ship) throws InterruptedException {
        for (Pier pier : piers) {
            if (pier.isFree()) {
                pier.takeTheShip(ship);
                Thread.sleep(1000);
                System.out.println("Ship #" + ship.getId() + " -> pier №" + pier.getId());
                return;
            }
        }
        throw new RuntimeException("All piers are not free!");
    }

    public void sendOutTheShip(Ship ship) {
        for (Pier pier : piers) {
            if (pier.getShip() != null && pier.getShip().equals(ship)) {
                pier.sendTheShip();
                return;
            }
        }
        throw new RuntimeException("Ship not found!");
    }

    public void unloadShip(Ship ship) throws InterruptedException {
        int shipNumberOfContainers = ship.getNumberOfContainers();
        if (this.realNumberOfContainers.get() + shipNumberOfContainers <= capacity) {
            System.out.println("Ship #" + ship.getId() + " started unload. Number of containers = "
                    + shipNumberOfContainers + ".");
            Thread.sleep(3000);
            this.realNumberOfContainers.getAndAdd(shipNumberOfContainers);
            ship.setNumberOfContainers(0);
            System.out.println("Ship #" + ship.getId() + " ended unload. Number of containers = "
                    + ship.getNumberOfContainers() + "."
                    + "\n\t\t- Unloaded " + shipNumberOfContainers + " containers.\t\t\tPorts number of containers = "
                    + realNumberOfContainers.get());
        } else {
            Thread.sleep(100);
            unloadShip(ship);
        }
    }

    public void loadShip(Ship ship) throws InterruptedException {
        if (this.realNumberOfContainers.get() > 0) {
            System.out.println("Ship #" + ship.getId() + " started load. Capacity = " + ship.getCapacity());
            Thread.sleep(3000);
            if (this.realNumberOfContainers.get() >= ship.getCapacity()) {
                this.realNumberOfContainers.getAndAdd(-ship.getCapacity());
                ship.setNumberOfContainers(ship.getCapacity());
            } else {
                ship.setNumberOfContainers(this.realNumberOfContainers.get());
                this.realNumberOfContainers.set(0);
            }
            System.out.println("Ship #" + ship.getId() + " ended load. Number of containers = "
                    + ship.getNumberOfContainers() + "."
                    + "\n\t\t - Loaded " + ship.getNumberOfContainers() + " containers.  \t\t\tPorts number of containers = "
                    + realNumberOfContainers.get());
        } else {
            Thread.sleep(100);
            loadShip(ship);
        }
    }

    public synchronized int numberOfMissingContainers() {
        return capacity - prospectiveNumberOfContainers.get();
    }

    public synchronized boolean hasFreePiers() {
        for (Pier pier : piers) {
            if (pier.isFree()) {
                return true;
            }
        }
        return false;
    }


    public static class Pier {
        private static int currentId = 1;
        private final int id;
        private volatile Ship ship;

        public Pier() {
            id = currentId;
            currentId++;
            ship = null;
        }

        public int getId() {
            return id;
        }

        public boolean isFree() {
            return ship == null;
        }

        public Ship getShip() {
            return ship;
        }

        public void takeTheShip(Ship ship) {
            if (this.ship == null) {
                this.ship = ship;
            } else {
                throw new RuntimeException("Pier is not free! " + this.ship);
            }

        }

        public void sendTheShip() {
            ship.sailOutOfThePort();
            System.out.println(" - - - Pier №" + id + " is free. - - -");
            ship = null;
        }

    }
}
