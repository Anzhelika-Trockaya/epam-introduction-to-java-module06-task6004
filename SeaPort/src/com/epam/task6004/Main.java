package com.epam.task6004;

public class Main {
    public static void main(String[] args) {
        Port port = new Port(10000, 3, 7000);
        ShipCreator shipCreator;

        System.out.println("\t\t\t\t\t\t\t\t\t\t\tPorts number of containers = " + port.getRealNumberOfContainers());

        shipCreator = new ShipCreator(port, 20);
        shipCreator.run();


    }
}
