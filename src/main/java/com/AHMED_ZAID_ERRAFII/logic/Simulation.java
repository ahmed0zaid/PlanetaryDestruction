package com.AHMED_ZAID_ERRAFII.logic;

public class Simulation {
    public long population = 8000000000L;
    public void handleDamage() {
        population = Math.max(0, population - 150000000L);
    }
}