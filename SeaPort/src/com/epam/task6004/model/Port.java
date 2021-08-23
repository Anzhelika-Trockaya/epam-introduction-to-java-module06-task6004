package com.epam.task6004.model;

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

    private void processShip(Ship ship) throws InterruptedException {
        if (ship.getNumberOfContainers() > 0) {
            unloadShip(ship);
        } else {
            loadShip(ship);
        }
        Thread.sleep(3000);
        sendOutTheShip(ship);
    }

    private synchronized boolean isPossibleToTakeTheShip(Ship ship) {
        return hasFreePiers() && ship.getNumberOfContainers() <= (capacity - prospectiveNumberOfContainers.get())
                && (ship.getNumberOfContainers() != 0 || this.prospectiveNumberOfContainers.get() >= ship.getCapacity());
    }

    boolean tookShip(Ship ship) throws InterruptedException {
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

    private synchronized void takeTheShipToAFreePier(Ship ship) throws InterruptedException {
        for (Pier pier : piers) {
            if (pier.isFree()) {
                pier.takeTheShip(ship);
                Thread.sleep(1000);
                System.out.println(" - - - Pier №" + pier.getId() + " <- Ship #" + ship.getId() + " - - -");
                return;
            }
        }
        throw new RuntimeException("All piers are not free!");
    }

    private void sendOutTheShip(Ship ship) {
        for (Pier pier : piers) {
            if (pier.getShip() != null && pier.getShip().equals(ship)) {
                pier.sendTheShip();
                return;
            }
        }
        throw new RuntimeException("Ship not found!");
    }

    private void unloadShip(Ship ship) throws InterruptedException {
        int shipNumberOfContainers = ship.getNumberOfContainers();
        if (shipNumberOfContainers <= capacity - realNumberOfContainers.get()) {
            synchronized (realNumberOfContainers) {
                System.out.println("Ship #" + ship.getId() + " started unload. Number of containers = "
                        + shipNumberOfContainers + ".");
                Thread.sleep(3000);
                this.realNumberOfContainers.getAndAdd(shipNumberOfContainers);
                ship.setNumberOfContainers(0);
                System.out.println("Ship #" + ship.getId() + " ended unload. Number of containers = "
                        + ship.getNumberOfContainers() + "."
                        + "\n\t\t- Unloaded " + shipNumberOfContainers + " containers.             "
                        + "Ports number of containers = " + realNumberOfContainers.get());
            }
        } else {
            Thread.sleep(100);
            unloadShip(ship);
        }
    }

    private void loadShip(Ship ship) throws InterruptedException {
        if (this.realNumberOfContainers.get() >= ship.getCapacity()) {
            synchronized (realNumberOfContainers) {
                System.out.println("Ship #" + ship.getId() + " started load. Capacity = " + ship.getCapacity());
                Thread.sleep(3000);
                this.realNumberOfContainers.getAndAdd(-ship.getCapacity());
                ship.setNumberOfContainers(ship.getCapacity());
                System.out.println("Ship #" + ship.getId() + " ended load. Capacity = "
                        + ship.getCapacity() + "."
                        + "\n\t\t - Loaded " + ship.getNumberOfContainers() + " containers.              "
                        + "Ports number of containers = " + realNumberOfContainers.get());
            }
        } else {
            Thread.sleep(100);
            loadShip(ship);
        }
    }

    private synchronized boolean hasFreePiers() {
        for (Pier pier : piers) {
            if (pier.isFree()) {
                return true;
            }
        }
        return false;
    }


    static class Pier {
        private static int currentId = 1;
        private final int id;
        private volatile Ship ship;

        Pier() {
            id = currentId;
            currentId++;
            ship = null;
        }

        int getId() {
            return id;
        }

        boolean isFree() {
            return ship == null;
        }

        Ship getShip() {
            return ship;
        }

        void takeTheShip(Ship ship) {
            if (this.ship == null) {
                this.ship = ship;
            } else {
                throw new RuntimeException("Pier is not free! " + this.ship);
            }

        }

        void sendTheShip() {
            ship.sailOutOfThePort();
            System.out.println(" - - - Pier №" + id + " is free. - - -");
            ship = null;
        }

    }
}
