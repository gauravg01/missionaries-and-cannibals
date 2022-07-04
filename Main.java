import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		menu();
	}

	public static void menu() {
		System.out.println("/----------3 Missionaries and 3 Cannibals Problem Solver-------------/");
		System.out.println("Choose any one: ");
		System.out.println("1. Breadth-first search");
		System.out.println("2. Depth-first search");
		System.out.println("3. A* search");
		System.out.println("4. Greedy Best First Search");
		System.out.println("0. Exit");
		System.out.print("\nType your choice and press ENTER: ");

		String choice = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			choice = in.readLine();
		} catch (IOException e) {
			System.out.println("Incorrect option try again.");
			menu();
		}

		int ch = Integer.parseInt(choice);
		StateSpace initialState = new StateSpace (3, 3, Position.LEFT, 0, 0);
		switch(ch) {
		case 1:
			runBFS(initialState);menu();
			break;
		case 2:
			runDFS(initialState);menu();
			break;
		case 3:
			runAStar();menu();
			break;
		case 4:
			runGFS(initialState);menu();
			break;
		case 0: System.out.println("Thank you!");
			break;
		default:
			System.out.println("Invalid input please try again!");menu();
		}
	}

	private static void runBFS(StateSpace initialState) {
		BreadthFirstSearch bfs = new BreadthFirstSearch();
		StateSpace solution = bfs.BFS(initialState);
		printSolution(solution);
	}
	
	private static void runGFS(StateSpace initialState) {
		GreedyFirstSearch gfs = new GreedyFirstSearch();
		StateSpace solution = gfs.GFS(initialState);
		printSolution(solution);
	}

	private static void runDFS(StateSpace initialState) {
		DepthFirstSearch dfs = new DepthFirstSearch();
		StateSpace solution = dfs.DFS(initialState);
		printSolution(solution);
	}

	private static void runAStar() {
		//N=No of M and C, M=Capacity of boat, K=No of routes allowed
		StateSpace initialState = new StateSpace(3, 3, 20); // Initial state
		AStarSearch as = new AStarSearch(20);
		StateSpace solution = as.AStarClosedSet(initialState);
		if(solution == null)
			System.out.println("Could not find a solution.");
		else {
			// Print the path from beginning to start
			StateSpace temp = solution; // Begin from the end
			ArrayList<StateSpace> path = new ArrayList<>();
			path.add(solution);

			// If father is null, then we are at the root
			while (temp.getParentState() != null) {
				path.add(temp.getParentState());
				temp = temp.getParentState();
			}

			// Reverse the path and print
			Collections.reverse(path);
			for (StateSpace item: path) {
				item.print();
			}

			System.out.println("Total Depth: " + solution.getRoutesCount());
			System.out.println();
		}
	}

	private static void printSolution(StateSpace solution) {
		if (null == solution) {
			System.out.print("\nNo solution found.");
		} else {
			System.out.println("\nSolution (cannibalLeft,missionaryLeft,boat,cannibalRight,missionaryRight): ");
			List<StateSpace> path = new ArrayList<StateSpace>();
			StateSpace state = solution;
			while(null!=state) {
				path.add(state);
				state = state.getParentState();
			}

			int depth = path.size() - 1;
			for (int i = depth; i >= 0; i--) {
				state = path.get(i);
				if (state.isGoal()) {
					System.out.println(state.toString());
				} else {
					System.out.println(state.toString() + " -> ");
				}
			}
			System.out.println("\nTotal Depth is: " + depth);
			System.out.println("/---------------------------------------------------------/\n");
		}
	}
}