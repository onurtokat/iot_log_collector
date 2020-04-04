package com.company.iot.data;

import com.company.iot.data.producer.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App class is main class of Data generator application
 * @author Onur Tokat
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger("App");

    public static void main(String[] args) {

        Thread t1 = new Thread(new DataGenerator("device1"));
        Thread t2 = new Thread(new DataGenerator("device1"));
        Thread t3 = new Thread(new DataGenerator("device1"));

        t1.start();
        t2.start();
        t3.start();
    }
}
