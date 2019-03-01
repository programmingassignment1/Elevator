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

	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds
	public static final int maxNumberOfPeopleInElevator = 6;

	private int numberOfFloors;
	private int numberOfElevators;

	// instance of ElevatorScene
	public static ElevatorScene scene;

	public static boolean elevatorsMayDie;

	// fylki sem heldur utan um hversu margar personur eru a hverri hæð
	public ArrayList<Integer> personCount; //use if you want but throw away and implement differently if it suits you
	// heldur utan um hversu margir exituðu á hverri hæð
	public ArrayList<Integer> exitedCount = null;

	public ArrayList<Thread> elevatorThreads;
	public ArrayList<Integer> currentFloorForElevator;
	public ArrayList<Integer> numberOfPeopleInElevator;

	// Mutual exclusion Semophores
	public static Semaphore exitedCountMutex;
	public static Semaphore personCountMutex;
	public static ArrayList<Semaphore> inSem;
	public static ArrayList<Semaphore> outSem;

	public void restartScene(int numberOfFloors, int numberOfElevators) {

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

		initializeVariables();
		joinThreads();

		createElevatorThreads();
	}

	private void initializeVariables() {

		// initialize the instance of ElevatorScene
		scene = this;

		currentFloorForElevator = new ArrayList<Integer>();
		elevatorThreads = new ArrayList<Thread>();
		numberOfPeopleInElevator = new ArrayList<Integer>();
		personCount = new ArrayList<Integer>();

		personCountMutex = new Semaphore(1);
		exitedCountMutex = new Semaphore(1);

		inSem = new ArrayList<Semaphore>();
		outSem = new ArrayList<Semaphore>();

	
		for(int i = 0; i < getNumberOfFloors(); i++) {
			outSem.add(new Semaphore(0));
			inSem.add(new Semaphore(0));
		}


		for(int i = 0; i < getNumberOfElevators(); i++) {
			this.currentFloorForElevator.add(0);
			this.numberOfPeopleInElevator.add(0);
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}

		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.personCount.add(0);
			this.exitedCount.add(0);
		}
	}

	private void joinThreads() {
		elevatorsMayDie = true;

		for(Thread thread : elevatorThreads) {
			if (thread != null) {
				if (thread.isAlive()) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		elevatorsMayDie = false;
	}

	private void createElevatorThreads() {
		for(int i = 0; i < getNumberOfElevators(); i++) {
			Elevator elevator = new Elevator(getNumberOfFloors());
			Thread elevatorThread = new Thread(elevator);
			elevatorThreads.add(elevatorThread);
			elevatorThreads.get(i).start();
		}
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		Person person = new Person(sourceFloor, destinationFloor);
		Thread personThread = new Thread(person);

		personThread.start();

		incrementNumberOfPeopleWaitingAtFloor(sourceFloor);

		return personThread;
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		return currentFloorForElevator.get(elevator);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {

		return numberOfPeopleInElevator.get(elevator);
	}

	public void incrementNumberOfPeopleInElevator(int elevator) {
		try {
			ElevatorScene.personCountMutex.acquire();
			numberOfPeopleInElevator.set(elevator, numberOfPeopleInElevator.get(elevator) + 1);
			ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void decrementNumberOfPeopleInElevator(int elevator) {
		try {
			ElevatorScene.personCountMutex.acquire();
			numberOfPeopleInElevator.set(elevator, numberOfPeopleInElevator.get(elevator) - 1);
			ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {

		try {
			ElevatorScene.personCountMutex.acquire();
			personCount.set(floor, (personCount.get(floor) -1));
			ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {

		try {
			ElevatorScene.personCountMutex.acquire();
			personCount.set(floor, (personCount.get(floor) +1));
			ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

			ElevatorScene.exitedCountMutex.acquire();
			exitedCount.set(floor, (exitedCount.get(floor) + 1));
			ElevatorScene.exitedCountMutex.release();

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