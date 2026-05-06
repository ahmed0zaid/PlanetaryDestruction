package com.AHMED_ZAID_ERRAFII.logic;

public class Simulation {
    public long totalPopulation = 8000000000L;
    public long currentPopulation = 8000000000L;

    public void registerHit() {
        currentPopulation = Math.max(0, currentPopulation - 125000000L);
    }
}