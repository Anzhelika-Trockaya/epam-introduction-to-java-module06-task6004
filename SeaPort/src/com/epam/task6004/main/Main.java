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
        Port port = new Port(10000, 3, 7000);
        ShipCreator shipCreator;

        System.out.println("                                                Ports number of containers = "
                + port.getRealNumberOfContainers());

        shipCreator = new ShipCreator(port, 20);
        shipCreator.run();
    }
}
