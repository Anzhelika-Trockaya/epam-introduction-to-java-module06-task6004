package com.epam.task6004.main;

import com.epam.task6004.model.Port;
import com.epam.task6004.model.ShipCreator;

//Задание 4. Многопоточность. Порт . Корабли заходят в порт для
//разгрузки/загрузки контейнеров. Число контейнеров, находящихся в текущий момент
//в порту и на корабле, должно быть неотрицательным и превышающим заданную
//грузоподъемность судна и вместимость порта. В порту работает несколько причалов.
//У одного причала может стоять один корабль. Корабль может загружаться у причала
//или разгружаться.

public class Main {
    public static void main(String[] args) {
        Port port;
        ShipCreator shipCreator;
        int portsCapacity;
        int piersNumber;
        int portsNumberOfContainer;
        int numberOfShip;

        portsCapacity = 10000;
        piersNumber = 3;
        portsNumberOfContainer = 7000;
        numberOfShip = 20;

        port = new Port(portsCapacity, piersNumber, portsNumberOfContainer);

        System.out.println("                                                            Ports capacity = "
                + portsCapacity);
        System.out.println("                                                Ports number of containers = "
                + port.getRealNumberOfContainers());

        shipCreator = new ShipCreator(port, numberOfShip);
        shipCreator.run();

    }
}
