package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds

	private int numberOfFloors;
	private int numberOfElevators;

	// instance of ElevatorScene
	public static ElevatorScene scene;

	// fylki sem heldur utan um hversu margar personur eru a hverri hæð
	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you
	// heldur utan um hversu margir exituðu á hverri hæð
	ArrayList<Integer> exitedCount = null;

	public static Semaphore exitedCountMutex;

	// Þegar við gerum static þá deilum við henni á milli þráða
	//public static Semaphore sem;

	ArrayList<Semaphore> inSem;
	ArrayList<Semaphore> outSem;

	ArrayList<Thread> elevatorThreads;
	public ArrayList<Integer> currentFloorForElevator;
	//ArrayList<Integer> numberOfPeopleInElevator;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

		/**
		 * Important to add code here to make new
		 * threads that run your elevator-runnables
		 *
		 * Also add any other code that initializes
		 * your system for a new run
		 *
		 * If you can, tell any currently running
		 * elevator threads to stop
		 */

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

		/***/

		// initialize the instance of ElevatorScene
		scene = this;

		currentFloorForElevator = new ArrayList<Integer>();
		elevatorThreads = new ArrayList<Thread>();

		for(int i = 0; i < getNumberOfElevators(); i++) {
			this.currentFloorForElevator.add(0);
			Elevator elevator = new Elevator(getNumberOfFloors());
			Thread elevatorThread = new Thread(elevator);
			elevatorThreads.add(elevatorThread);
			elevatorThreads.get(i).start();
		}

		/*
		numberOfPeopleInElevator = new ArrayList<Integer>();
		for(int i = 0; i < getNumberOfElevators(); i++) {
			this.numberOfPeopleInElevator.add(0);
		}*/


		personCount = new ArrayList<Integer>();
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.personCount.add(0);
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		exitedCountMutex = new Semaphore(1);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		Person person = new Person(sourceFloor, destinationFloor);
		Thread personThread = new Thread(person);

		personThread.start();

		personCount.set(sourceFloor, personCount.get(sourceFloor) + 1);

		return personThread;
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		return currentFloorForElevator.get(elevator);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {

		return 1;
		//return numberOfPeopleInElevator.get(elevator);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	//Person threads must call this function to
	//let the system know that they have exited.
	//Person calls it after being let off elevator
	//but before it finishes its run.
	public void personExitsAtFloor(int floor) {
		try {

			exitedCountMutex.acquire();
			exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.release();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}
	}


}
