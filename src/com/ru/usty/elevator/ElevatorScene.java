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

	public int maxNumberOfPeopleInElevator = 6;

	// instance of ElevatorScene
	public static ElevatorScene scene;

	public static boolean elevatorsMayDie;

	// fylki sem heldur utan um hversu margar personur eru a hverri hæð
	ArrayList<Integer> personCount; //use if you want but throw away and implement differently if it suits you
	ArrayList<Integer> personDestination;

	// heldur utan um hversu margir exituðu á hverri hæð
	ArrayList<Integer> exitedCount = null;

	////// ****** ALLT Í LAGI AÐ HAFA ALLAR SEMPHORUR HÉR (KÁRI) ***** ////

	// Mutual exclusion Semophores
	public static Semaphore exitedCountMutex;
	public static Semaphore personCountMutex;
	public static Semaphore elevatorWaitMutex;
	public static Semaphore testMutex;
	public static Semaphore destinationFloorMutex;
	public static Semaphore numberOfPeopleInElevatorMutex;

	public static Semaphore[] inSem;
	public static Semaphore[] outSem;

	//ArrayList<Semaphore> floorSem;

	ArrayList<Thread> elevatorThreads;
	public ArrayList<Integer> currentFloorForElevator;
	ArrayList<Integer> numberOfPeopleInElevator;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

		elevatorsMayDie = true;

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
		currentFloorForElevator = new ArrayList<Integer>();
		elevatorThreads = new ArrayList<Thread>();
		numberOfPeopleInElevator = new ArrayList<Integer>();
		personCount = new ArrayList<Integer>();
		personDestination = new ArrayList<Integer>();

		inSem = new Semaphore[getNumberOfFloors()];
		outSem = new Semaphore[getNumberOfFloors()];

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

		// initialize the instance of ElevatorScene
		scene = this;

		// Stillt á einn => Fyrsti sem kallar á wait() á henni kemst í gegn
		// Hann mun svo setja hana aftur niður í núll þegar hann er búinn
		// Og því kemst næsti ekki inn aftur fyrr en hann er búinn --> Mutual exclusion
		personCountMutex = new Semaphore(1);
		elevatorWaitMutex = new Semaphore(1);
		destinationFloorMutex = new Semaphore(1);
		testMutex = new Semaphore(1);
		exitedCountMutex = new Semaphore(1);
		numberOfPeopleInElevatorMutex = new Semaphore(1);

		for(int i = 0; i < getNumberOfFloors(); i++) {
			inSem[i] = new Semaphore(0);
			outSem[i] = new Semaphore(0);
		}

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


		for(int i = 0; i < getNumberOfElevators(); i++) {
			this.currentFloorForElevator.add(0);
			this.numberOfPeopleInElevator.add(0);
			Elevator elevator = new Elevator(getNumberOfFloors());
			Thread elevatorThread = new Thread(elevator);
			elevatorThreads.add(elevatorThread);
			elevatorThreads.get(i).start();
		}

		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.personCount.add(0);
			this.personDestination.add(0);
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
			ElevatorScene.numberOfPeopleInElevatorMutex.acquire();
			numberOfPeopleInElevator.set(elevator, numberOfPeopleInElevator.get(elevator) + 1);
			ElevatorScene.numberOfPeopleInElevatorMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void decrementNumberOfPeopleInElevator(int elevator) {
		try {
			ElevatorScene.numberOfPeopleInElevatorMutex.acquire();
				numberOfPeopleInElevator.set(elevator, numberOfPeopleInElevator.get(elevator) - 1);
			ElevatorScene.numberOfPeopleInElevatorMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void incrementDestinationFloors(int elevator) {
		try {
			ElevatorScene.destinationFloorMutex.acquire();
			personDestination.set(elevator, personDestination.get(elevator) + 1);
			ElevatorScene.destinationFloorMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getDestinationFloors(int floor) {
		return personDestination.get(floor);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {

		// Spurning: Er nóg einn mutex hér eða þarf einn per hæð?

		try {
			ElevatorScene.personCountMutex.acquire();
				personCount.set(floor, (personCount.get(floor) -1));
			ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {

		// Spurning: Er nóg einn mutex hér eða þarf einn per hæð?

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