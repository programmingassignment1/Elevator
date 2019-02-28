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

        while(true) {
            if (ElevatorScene.elevatorsMayDie) {
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(0);
            int numberOfEmptySpacesInElevator = ElevatorScene.scene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);

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

            // Farið á milli hæða
            ElevatorScene.scene.currentFloorForElevator.set(0, currFloor);

            if(currFloor == 0) {
                ElevatorScene.inSem.release(min(numberOfEmptySpacesInElevator, numberOfPeopleWaitingAtFloor));
                // þarf lyftan að acquirea 6 - numberOfPeopleInElevator sinnum til að læsa semaphorunni?
            }
            else {
                ElevatorScene.outSem[currFloor].release(ElevatorScene.scene.getDestinationFloors(currFloor));
                System.out.println( ElevatorScene.scene.getDestinationFloors(0) + " " + ElevatorScene.scene.getDestinationFloors(1) + " " + ElevatorScene.scene.getDestinationFloors(2) + " " + ElevatorScene.scene.getDestinationFloors(3) );
                ElevatorScene.scene.personDestination.set(currFloor, 0);
                System.out.println("number of exited at floor " + currFloor + " " + ElevatorScene.scene.exitedCount);
                try {
                    ElevatorScene.outSem[currFloor].acquire(ElevatorScene.scene.getDestinationFloors(currFloor));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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