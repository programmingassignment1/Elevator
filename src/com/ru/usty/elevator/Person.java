package com.ru.usty.elevator;

public class Person implements Runnable {

    private int sourceFloor, destinationFloor;

    Person(int sourceFloor, int destinationFloor) {

        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    @Override
    public void run() {

       try {
           ElevatorScene.elevatorWaitMutex.acquire();
             ElevatorScene.inSem.acquire(); // Wait
           System.out.println("inSem acquired by Person");
           ElevatorScene.elevatorWaitMutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Person is through barrier hér
        ElevatorScene.scene.incrementNumberOfPeopleInElevator(0);
        //ElevatorScene.scene.incrementDestinationFloors(destinationFloor);
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

        /*try {
            Thread.sleep(500);
            System.out.println("Waiting in Person class 0.5 s .....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        try {
            Thread.sleep(500);
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

