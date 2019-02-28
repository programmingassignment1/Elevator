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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ElevatorScene.outSem[currFloor].release(ElevatorScene.scene.getNumberOfPeopleInElevator(0));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("number of exited at floor " + currFloor + " " + ElevatorScene.scene.exitedCount);
            // það fólk sem vill fara út fer út

            // acquire

            // lyftan acquire-ar outSem 6 sinnum
            /*try {
                ElevatorScene.outSem[currFloor].acquire(ElevatorScene.scene.getNumberOfPeopleInElevator(0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            // fólk acquire-ar insem
            int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(currFloor);
            int numberOfEmptySpacesInElevator = ElevatorScene.scene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);

            int oldCount = ElevatorScene.scene.getNumberOfPeopleInElevator(0);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // lyftan release-ar inSem jafn oft og peopleWaitingAtFloor (min fallið her)
            ElevatorScene.inSem.release(min(numberOfEmptySpacesInElevator, numberOfPeopleWaitingAtFloor));

            /*try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            // lyftan acquire-ar inSem jafn oft og numberOfPeopleInElevator

            System.out.println("number of people in ele: " + ElevatorScene.scene.getNumberOfPeopleInElevator(0));

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
                    // Farið á milli hæða
                //ElevatorScene.testMutex.release();
                ElevatorScene.scene.currentFloorForElevator.set(0, currFloor);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*} catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            // lyftan breytir um hæð


            /*if(currFloor == 0) {
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
            }*/
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