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
           ElevatorScene.inSem.get(sourceFloor).acquire(); // Wait

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Person is through barrier hér
        ElevatorScene.scene.incrementNumberOfPeopleInElevator(0);
        ElevatorScene.scene.incrementDestinationFloors(destinationFloor);
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

        try {
            ElevatorScene.outSem.get(destinationFloor).acquire(); // Wait
            ElevatorScene.scene.personExitsAtFloor(destinationFloor);
            ElevatorScene.scene.decrementNumberOfPeopleInElevator(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Person thread released");

    }
}

