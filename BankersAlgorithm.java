import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

/*
 * Benjamin Marquis
 * 2/8/2022
 * This program finds all possible safe sequences to navigate the bankers algorithm
 * this program requires this file BankersAlgorithm.java ProcessDict.java and a .txt file of inputs formatted correctly
*/
public class BankersAlgorithm {
	public static ArrayList<ArrayList<Integer>> safeSequences = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<Integer[]> allocation = new ArrayList<Integer[]>();
	public static ArrayList<Integer[]> max = new ArrayList<Integer[]>();
	public static ArrayList<Integer[]> need;
	public static Integer[] available, total;

	public static void main(String[] args) throws Exception {
		// request input file from user
		System.out.println("Enter the file name: ");
		Scanner myScanner = new Scanner(System.in);
		String fileName = myScanner.next();
		// open file
		File file = new File(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String throwaway = reader.readLine();
		String line = reader.readLine();
		StringTokenizer myT = new StringTokenizer(line);
		int r = myT.countTokens();// r is for number of resources
		Integer[] p = new Integer[r];
		// read line by line adding values to Allocation 2d array
		while (!line.equals("Max")) {
			for (int i = 0; i < r; i++) {
				p[i] = Integer.parseInt(myT.nextToken());
			}
			line = reader.readLine();
			myT = new StringTokenizer(line);
			allocation.add(p.clone());
		}
		// print allocation array
		System.out.println("allocation: ");
		for (int i = 0; i < allocation.size(); i++) {
			for (int k = 0; k < allocation.get(i).length; k++) {
				System.out.print(allocation.get(i)[k] + " ");
			}
			System.out.println();
		}
		line = reader.readLine();
		myT = new StringTokenizer(line);
		// read line by line adding values to Max 2d array
		while (!line.equals("Total")) {
			for (int i = 0; i < r; i++) {
				p[i] = Integer.parseInt(myT.nextToken());
			}
			line = reader.readLine();
			myT = new StringTokenizer(line);
			max.add(p.clone());
		}
		// print Max array
		System.out.println("Max: ");
		for (int i = 0; i < max.size(); i++) {
			for (int k = 0; k < max.get(i).length; k++) {
				System.out.print(max.get(i)[k] + " ");
			}
			System.out.println();
		}

		line = reader.readLine();
		myT = new StringTokenizer(line);
		total = new Integer[r];
		available = new Integer[r];
		// read total resources available line
		for (int i = 0; i < r; i++) {
			total[i] = Integer.parseInt(myT.nextToken());
		}
		// print total resources available
		System.out.println("Total: ");
		for (int k = 0; k < r; k++) {
			System.out.print(total[k] + " ");
		}

		System.out.println();
		// fill need 2D array using generate need method
		need = generateNeed(r, allocation, max);
		// create Processes array with createProcessDicts method
		ArrayList<ProcessDict> Processes = createProcessDicts();
		// generate starting available resources array
		available = generateAvailable(Processes);
		System.out.println("Starting Available: ");
		for (int k = 0; k < r; k++) {
			System.out.print(available[k] + " ");
		}
		// begin recursive execution process finding all available safe sequences
		startRecursivelyExecute(Processes, available);
		System.out.println();
		// print safe sequences
		if (safeSequences.size() > 0) {
			System.out.println("Safe Sequences: ");
			for (int i = 0; i < safeSequences.size(); i++) {
				for (int j = 0; j < safeSequences.get(i).size(); j++) {
					System.out.print(safeSequences.get(i).get(j));
				}
				System.out.println();
			}
			// if system is unsafe
		} else {
			System.out.println("The System is in an unsafe state, there are no available safe sequences");
		}
	}

	/*
	 * this method determines the amount of resources available to the system at the
	 * beginning of the algorithm
	 */
	public static Integer[] generateAvailable(ArrayList<ProcessDict> Processes) {
		Integer[] sums = new Integer[Processes.get(0).getAlloc().length];
		// fills sums and available array with 0s
		for (int i = 0; i < sums.length; i++) {
			sums[i] = 0;
			available[i] = 0;
		}
		// iterates through all allocations and adds their values together to create
		// sums array
		// sums is the sum of all allocated resources at the start of the algorithm
		for (int i = 0; i < Processes.size(); i++) {
			for (int j = 0; j < Processes.get(i).getAlloc().length; j++) {
				sums[j] += Processes.get(i).getAlloc()[j];
			}
		}
		// generates starting available array by subtracting the sums of allocated
		// resources from the total resources
		for (int i = 0; i < sums.length; i++) {
			available[i] = total[i] - sums[i];
		}
		// returns the available array for accurate starting availability to the system
		return available;
	}

	/*
	 * this creates an array list of ProcessDicts which is an object type created to
	 * simplify the getting and setting of information that is necessary to the
	 * system
	 */
	public static ArrayList<ProcessDict> createProcessDicts() {
		ArrayList<ProcessDict> Processes = new ArrayList<ProcessDict>();
		for (int i = 0; i < allocation.size(); i++) {
			Processes.add(new ProcessDict(i, allocation.get(i), max.get(i), need.get(i)));
		}
		return Processes;
	}

	/*
	 * this method searches the currently available processes and returns an
	 * arraylist of processes that are executable with current system resources
	 * represented by available pass
	 */
	public static ArrayList<Integer> findExecutables(ArrayList<ProcessDict> Processes, Integer[] availablePass) {
		ArrayList<Integer> executables = new ArrayList<Integer>();
		for (int i = 0; i < Processes.size(); i++) {
			if (Processes.get(i).isExecutable(availablePass)) {
				executables.add(Processes.get(i).getKey());
			}
		}
		return executables;
	}

	/*
	 * This method begins the recursive execution process to find all possible safe
	 * sequences available
	 */
	public static void startRecursivelyExecute(ArrayList<ProcessDict> Processes, Integer[] available) {
		// this array list will hold the keys of processes that can be executed safely
		// in that order
		ArrayList<Integer> safeSequence = new ArrayList<Integer>();
		// this array list holds the keys of all processes that are currently executable
		ArrayList<Integer> executables = findExecutables(Processes, available);
		// this integer array holds the current system available resources
		Integer[] availablePass = Arrays.copyOf(available, available.length);
		for (int i = 0; i < executables.size(); i++) {
			// recursively execute each process that is executable
			recursivelyExecute(Processes, safeSequence, availablePass, executables.get(i));
		}
	}

	/*
	 * this method is responsible for follow on recursive executions after the first
	 * process has been selected from the executables list
	 */
	public static void recursivelyExecute(ArrayList<ProcessDict> Processes, ArrayList<Integer> safeSequence,
			Integer[] availablePass, int executeMe) {
		// deep copy of the current safe sequence that is passed to the method
		ArrayList<Integer> safeSequenceA = (ArrayList<Integer>) safeSequence.clone();
		// Integer array deep copy of currently available resources
		Integer[] availablePassA = Arrays.copyOf(availablePass, availablePass.length);
		// executes process, which updates the currently available resources by adding
		// the resources allocated to the process
		executeProcess(executeMe, Processes, availablePassA);
		// adds the executed process to the safe sequence
		safeSequenceA.add(executeMe);
		// if the safe sequence has reached the end of the processes successfully it is
		// added to the list of safe sequences
		if (safeSequenceA.size() == Processes.size()) {
			safeSequences.add(safeSequenceA);
		}
		// after the process is found all executable processes are found
		ArrayList<Integer> executables = findExecutables(Processes, availablePassA);
		for (int i = 0; i < executables.size(); i++) {
			// if any executable process that has not been added to the sequence is found it
			// is executed recursively
			if (!safeSequenceA.contains(executables.get(i))) {
				recursivelyExecute(Processes, safeSequenceA, availablePassA, executables.get(i));
			}
		}
	}

	/*
	 * this method "executes" a given process all it does is add the allocated
	 * resources from a given process to the array of available resources
	 */
	public static void executeProcess(int index, ArrayList<ProcessDict> Processes, Integer[] availablePass) {
		for (int i = 0; i < availablePass.length; i++) {
			availablePass[i] += Processes.get(index).getAlloc()[i];
		}
	}

	/*
	 * this method generates the need 2 d array
	 */
	public static ArrayList<Integer[]> generateNeed(int r, ArrayList<Integer[]> allocation, ArrayList<Integer[]> max) {
		ArrayList<Integer[]> need = new ArrayList<Integer[]>();
		Integer[] process = new Integer[r];
		// this nested for loop iterates through the allocation and max arrays and
		// subtracts them to find the need
		for (int j = 0; j < max.size(); j++) {
			for (int i = 0; i < process.length; i++) {
				process[i] = max.get(j)[i] - allocation.get(j)[i];
			}
			need.add(process.clone());
		}
		// prints need 2d array
		System.out.println("Need: ");
		for (int i = 0; i < need.size(); i++) {
			for (int k = 0; k < need.get(i).length; k++) {
				System.out.print(need.get(i)[k] + " ");
			}
			System.out.println();
		}
		return need;
	}
}
