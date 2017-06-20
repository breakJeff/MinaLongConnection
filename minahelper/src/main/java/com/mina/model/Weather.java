package com.mina.model;

import java.io.Serializable;

public class Weather implements Serializable {
    public int humidity;
    public int temperature;
    public int wind;

    @Override
    public String toString() {
        return "current weather is : humidity = " + humidity + " temperature = " + temperature + " wind= " + wind;
    }
}