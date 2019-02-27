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

        int temp = 0;
        while(true) {
            if (ElevatorScene.elevatorsMayDie) {
                return;
            }

            temp = ElevatorScene.scene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(currFloor == 0) {
                isGoingUp = true;
            }
            else if(numberOfFloors - 1 == currFloor) {
                isGoingUp = false;
            }
            if(isGoingUp) {
                currFloor++;
            }
            else {
                currFloor--;
            }
            ElevatorScene.scene.currentFloorForElevator.set(0, currFloor);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(temp > 1){
                ElevatorScene.inSem.release();
            }
        }

    }
}