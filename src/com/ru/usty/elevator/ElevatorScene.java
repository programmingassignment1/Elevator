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

	public static final int VISUALIZATION_WAIT_TIME = 100;  //milliseconds
	public static final int maxNumberOfPeopleInElevator = 6;

	private int numberOfFloors;
	private int numberOfElevators;

	// instance of ElevatorScene
	public static ElevatorScene scene;
	public static boolean elevatorsMayDie;
	
	public ArrayList<Integer> numberOfPeopleWaitingAtFloor; //how many persons are waiting at each floor
	public ArrayList<Integer> exitedCount = null; // how many persons exits at each floor

	public ArrayList<Thread> elevatorThreads;
	public ArrayList<Integer> currentFloorForElevator; // where every elevator is
	public ArrayList<Integer> numberOfPeopleInElevator;	//how many people are in each elevator

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

	//Initialising the scene
	private void initializeVariables() {

		// initialize the instance of ElevatorScene
		scene = this;

		currentFloorForElevator = new ArrayList<Integer>();
		elevatorThreads = new ArrayList<Thread>();
		numberOfPeopleInElevator = new ArrayList<Integer>();
		numberOfPeopleWaitingAtFloor = new ArrayList<Integer>();

		personCountMutex = new Semaphore(1);
		exitedCountMutex = new Semaphore(1);

		inSem = new ArrayList<Semaphore>();
		outSem = new ArrayList<Semaphore>();
		
		//adding into the arrays for every elevator
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

		//adding into the arrays for every floor 
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.numberOfPeopleWaitingAtFloor.add(0);
			this.exitedCount.add(0);
			outSem.add(new Semaphore(0));
			inSem.add(new Semaphore(0));
		}
	}

	private void joinThreads() {
		elevatorsMayDie = true;

		//for every elevator
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
			int currFloor = (int)(Math.random() * numberOfFloors);
			setCurrFloor(i, currFloor);
			Elevator elevator = new Elevator(getNumberOfFloors(), i, currFloor);
			Thread elevatorThread = new Thread(elevator);
			elevatorThreads.add(elevatorThread);
			elevatorThreads.get(i).start();
		}
	}

	//Base function: definition must not change
	//making a new person and personthread, then incrementing the number of people waiting at that floor
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

	public void setCurrFloor(int elevator, int currFloor) {
		currentFloorForElevator.set(elevator, currFloor);
	}
	
	//increment people in elevator
	public void incrementNumberOfPeopleInElevator(int elevator) {
	
			try {
				//Using mutex so many threads doesn't access this at once. 
				ElevatorScene.personCountMutex.acquire();
					numberOfPeopleInElevator.set(elevator, numberOfPeopleInElevator.get(elevator) + 1);
				ElevatorScene.personCountMutex.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}

	//decrementing the people in elevator
	public void decrementNumberOfPeopleInElevator(int elevator) {
			try {
				//Using mutex so many threads doesn't access this at once. 
				ElevatorScene.personCountMutex.acquire();
					numberOfPeopleInElevator.set(elevator, numberOfPeopleInElevator.get(elevator) - 1);
				ElevatorScene.personCountMutex.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {
		return numberOfPeopleWaitingAtFloor.get(floor);
	}

	// decrementing the people waiting at floor
	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {

		try {
			//Using mutex so many threads doesn't access this at once. 
			ElevatorScene.personCountMutex.acquire();
				numberOfPeopleWaitingAtFloor.set(floor, (numberOfPeopleWaitingAtFloor.get(floor) -1));
			ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// incrementing the people waiting at floor
	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {

		try {
			//Using mutex so many threads doesn't access this at once. 
			ElevatorScene.personCountMutex.acquire();
				numberOfPeopleWaitingAtFloor.set(floor, (numberOfPeopleWaitingAtFloor.get(floor) +1));
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
			//Using mutex so many threads doesn't access this at once. 
			ElevatorScene.exitedCountMutex.acquire();
				exitedCount.set(floor, (exitedCount.get(floor) + 1));
			ElevatorScene.exitedCountMutex.release();

		} catch (InterruptedException e) {
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