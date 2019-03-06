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
            stopIfNobodyIsWaiting();
            moveElevator();
        }
    }

    private void stopIfNobodyIsWaiting() {
        while (true) {
            int peopleWaiting = 0;
            // checking if there is a person waiting at some floor
            for(int i = 0; i < numberOfFloors; i++) {
            	peopleWaiting += ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(i);
            }
            // checking if there is a person in the elevator
            for(int i = 0; i < ElevatorScene.scene.getNumberOfElevators(); i++) {
            	peopleWaiting += ElevatorScene.scene.getNumberOfPeopleInElevator(i);
            }
            // if there is no one waiting, elevator stops
            if(peopleWaiting != 0) {
            	return;
            }
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
        // out-semaphore is released 6 times so those people who want can go out
        ElevatorScene.outSem.get(currFloor).release(ElevatorScene.scene.getNumberOfPeopleInElevator(numberOfElevator)); 
        try {
            threadSleep();
            // out-semaphore is acquired for each person who didn't go out at this floor
            ElevatorScene.outSem.get(currFloor).acquire(ElevatorScene.scene.getNumberOfPeopleInElevator(numberOfElevator));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        threadSleep();
    }

    private void letPeopleIntoElevator() {
        int numberOfPeopleWaitingAtFloor = ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(currFloor);
        int numberOfEmptySpacesInElevator = ElevatorScene.maxNumberOfPeopleInElevator - ElevatorScene.scene.getNumberOfPeopleInElevator(numberOfElevator);
        // in-semaphore is released so people can get in the elevator, as many times as there is room for or how many are waiting, depending on which number is smaller
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