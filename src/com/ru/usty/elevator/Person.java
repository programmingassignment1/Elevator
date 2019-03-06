package com.ru.usty.elevator;

public class Person implements Runnable {

    private int sourceFloor, destinationFloor, elevator;

    Person(int sourceFloor, int destinationFloor) {

        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.elevator = 0; //NOTE: hard-coded 0, but it is supposed to be the number of the elevator that the person uses.
    }

    @Override
    public void run() {

       try {
    	   // acquire inSemaphore here for person to get into elevator
           ElevatorScene.inSem.get(sourceFloor).acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //incrementing and decrementing numbers
        ElevatorScene.scene.incrementNumberOfPeopleInElevator(elevator);   
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

        try {
        	// acquire the outSem for the person to go out of the elevator
            ElevatorScene.outSem.get(destinationFloor).acquire(); 
            
            //incrementing and decrementing numbers
            ElevatorScene.scene.personExitsAtFloor(destinationFloor);
            ElevatorScene.scene.decrementNumberOfPeopleInElevator(elevator);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Person thread released");

    }
}

