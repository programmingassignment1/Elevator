package com.ru.usty.elevator;

public class Elevator implements Runnable {

    int currFloor;
    int numberOfFloors;
    boolean isGoingUp;

    Elevator(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        currFloor = 0;
        isGoingUp = true;
    }

    @Override
    public void run() {
        System.out.println("ELEVATOR");
        
        while(true) {
            if(currFloor == 0) {
                isGoingUp = true;
            }
            else if(numberOfFloors - 1 == currFloor) {
                isGoingUp = false;
            }

        }
    }
}
