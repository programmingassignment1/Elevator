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

           for (int i = 0; i < 10; i++) {
               ElevatorScene.sem.release();
           }

        /*System.out.println("ELEVATOR");
        while(true) {
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
        }*/
       }

    }
}
