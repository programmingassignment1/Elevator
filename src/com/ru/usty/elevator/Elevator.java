package com.ru.usty.elevator;

public class Elevator implements Runnable {

    private int currFloor;
    private int numberOfFloors;
    private boolean isGoingUp;

    Elevator(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        currFloor = 0;
        isGoingUp = true;
    }

    @Override
    public void run() {

        while(true) {
            if (ElevatorScene.elevatorsMayDie) {
                return;
            }

            letPeopleOutOfElevator();
            letPeopleIntoElevator();
            moveElevator();
        }
    }

    private void moveElevator() {
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

        threadSleep();
    }

    private void letPeopleOutOfElevator() {

        ElevatorScene.outSem.get(currFloor).release(ElevatorScene.scene.getNumberOfPeopleInElevator(0));

        try {
            threadSleep();
            ElevatorScene.outSem.get(currFloor).acquire(ElevatorScene.scene.getNumberOfPeopleInElevator(0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadSleep();
    }

    private void letPeopleIntoElevator() {

        int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(currFloor);
        int numberOfEmptySpacesInElevator = ElevatorScene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);

        ElevatorScene.inSem.get(currFloor).release(min(numberOfEmptySpacesInElevator, numberOfPeopleWaitingAtFloor));
        threadSleep();
    }

    private void threadSleep() {

        try {
            Thread.sleep(ElevatorScene.VISUALIZATION_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int min(int former, int latter) {
        if(former < latter) {
            return former;
        } else {
            return latter;
        }
    }
}