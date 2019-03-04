package com.ru.usty.elevator;

public class Elevator implements Runnable {

    private int currFloor;
    private int numberOfFloors;
    private boolean isGoingUp;
	private int numberOfElevator;

    Elevator(int numberOfFloors, int numberOfElevator, int numberInBeginning) {
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevator = numberOfElevator;
        currFloor = numberInBeginning;
        isGoingUp = randBool();
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
    	// if the elevator is on the bottom floor
        if(currFloor == 0) {
            isGoingUp = true;
        }
        //if the elevator is on the top floor
        else if(numberOfFloors - 1 == currFloor) {
            isGoingUp = false;
        }
        if(isGoingUp) {
            currFloor++;
        }
        else {
            currFloor--;
        }
        // setting the new position of elevator
        ElevatorScene.scene.currentFloorForElevator.set(numberOfElevator, currFloor);
        threadSleep();
    }

    
    private void letPeopleOutOfElevator() {
    	// release outSem so the person gets out of the elevator for every person in elevator
        ElevatorScene.outSem.get(currFloor).release(ElevatorScene.scene.getNumberOfPeopleInElevator(numberOfElevator)); 
        try {
            threadSleep();
            // acquire outSem back for every person who didn't go out at this floor
            ElevatorScene.outSem.get(currFloor).acquire(ElevatorScene.scene.getNumberOfPeopleInElevator(numberOfElevator));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        threadSleep();
    }

    private void letPeopleIntoElevator() {
        int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(currFloor);
        int numberOfEmptySpacesInElevator = ElevatorScene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(numberOfElevator);
        // release inSem so people can get in the elevator, as many as there is room for/how many are waiting
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

    //finding the smaller number
    private int min(int former, int latter) {
        if(former < latter) {
            return former;
        } else {
            return latter;
        }
    }
    
    //finding random bool for isGoingUp
    private boolean randBool() {
    	int temp = (int)Math.random() * 1;
    	if(temp == 1) {
    		return true;
    	}
    	return false;
    }
}