package com.ru.usty.elevator;

public class Person implements Runnable {

    private int sourceFloor, destinationFloor;

    Person(int sourceFloor, int destinationFloor) {

        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    @Override
    public void run() {

        int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(sourceFloor);
        int numberOfEmptySpacesInElevator = ElevatorScene.scene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(0);

       try {
           ElevatorScene.elevatorWaitMutex.acquire();
             ElevatorScene.inSem.acquire(); // Wait
           System.out.println("inSem acquired by Person at floor: " + sourceFloor);
           ElevatorScene.elevatorWaitMutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Person is through barrier hér
        ElevatorScene.scene.incrementNumberOfPeopleInElevator(0);
        ElevatorScene.scene.incrementDestinationFloors(destinationFloor);
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

        /*try {
            Thread.sleep(1500);
            System.out.println("Waiting in Person class 0.5 s .....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        try {
            Thread.sleep(1500);
            //ElevatorScene.testMutex.acquire();
                ElevatorScene.outSem[destinationFloor].acquire(); // Wait
            System.out.println("outSem acquired by Person");
            //ElevatorScene.testMutex.release();
            ElevatorScene.scene.personExitsAtFloor(destinationFloor);
            ElevatorScene.scene.decrementNumberOfPeopleInElevator(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Person thread released");

    }
}

