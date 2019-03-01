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

            int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(0);
            int numberOfEmptySpacesInElevator = ElevatorScene.scene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);

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

            if(currFloor == 0) {
                ElevatorScene.inSem.release(min(numberOfEmptySpacesInElevator, numberOfPeopleWaitingAtFloor));
                
                // þarf lyftan að acquirea 6 - numberOfPeopleInElevator sinnum til að læsa semaphorunni?
            }
            else if(numberOfFloors - 1 == currFloor) {
                ElevatorScene.outSem.release(ElevatorScene.scene.getNumberOfPeopleInElevator(0));
                
            }


            try {
                Thread.sleep(500);
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