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

            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            System.out.println("Permits of outSem before release: " + ElevatorScene.outSem[currFloor].availablePermits());
            ElevatorScene.outSem[currFloor].release(ElevatorScene.scene.getNumberOfPeopleInElevator(0));
            System.out.println("Permits of outSem after release: " + ElevatorScene.outSem[currFloor].availablePermits());
            System.out.println("Letting people out of elevator");
            try {
                Thread.sleep(500);
                System.out.println("Waiting 0.5 s .....");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("number of exited at floor " + currFloor + " " + ElevatorScene.scene.exitedCount);

            // fólk acquire-ar insem
            int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(currFloor);
            System.out.println("People waiting at floor " + currFloor + ": " + numberOfPeopleWaitingAtFloor);
            int numberOfEmptySpacesInElevator = ElevatorScene.scene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);
            System.out.println("Empty spaces in elevator: " + numberOfEmptySpacesInElevator);

            int oldCount = ElevatorScene.scene.getNumberOfPeopleInElevator(0);

            // lyftan release-ar inSem jafn oft og peopleWaitingAtFloor (min fallið her)
            System.out.println("Permits of inSem before release: " + ElevatorScene.inSem[currFloor].availablePermits());
            ElevatorScene.inSem[currFloor].release(min(numberOfEmptySpacesInElevator, numberOfPeopleWaitingAtFloor));
            System.out.println("Permits of inSem after release: " + ElevatorScene.inSem[currFloor].availablePermits());
            System.out.println("Letting people into elevator");
            try {
                Thread.sleep(500);
                System.out.println("Waiting 0.5 s .....");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            // lyftan acquire-ar inSem jafn oft og numberOfPeopleInElevator

            System.out.println("number of people in elevator: " + ElevatorScene.scene.getNumberOfPeopleInElevator(0));

            int inCount = ElevatorScene.scene.getNumberOfPeopleInElevator(0) - oldCount;
            /*try {
                ElevatorScene.inSem.acquire(6 - inCount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            //try {
                //ElevatorScene.testMutex.acquire();
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
            //ElevatorScene.testMutex.acquire();


            System.out.println("Moving between floors, now at " + ElevatorScene.scene.getCurrentFloorForElevator(0));
                ElevatorScene.scene.currentFloorForElevator.set(0, currFloor);
            System.out.println("...And now at " + ElevatorScene.scene.getCurrentFloorForElevator(0));

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