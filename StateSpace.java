import java.util.ArrayList;
import java.util.List;

enum Position {RIGHT, LEFT}

public class StateSpace implements Comparable<StateSpace> {

	private int cannibalLeft;
	private int missionaryLeft;
	private int cannibalRight;
	private int missionaryRight;
	private Position boat;
	private int routesCount;

	private StateSpace parentState;
	
	private int N; // Number of Cannibals/Missionaries
    private int M; // Boat capacity
    private int K; // Max number of routes

    // Heuristic score
    private int score;

    @SuppressWarnings("unused")
	private StateSpace father = null;

    StateSpace(int N, int M, int K) {
        this.N = N;
        this.M = M;
        this.K = K;

        routesCount = 0;
        cannibalLeft = N;
        missionaryLeft = N;
        boat = Position.LEFT;
        cannibalRight = 0;
        missionaryRight = 0;
    }


    // Constructor for creating copy of the state.
    StateSpace(StateSpace father) {
        this.N = father.N;
        this.M = father.M;
        this.K = father.K;
        this.cannibalLeft = father.cannibalLeft;
        this.cannibalRight = father.cannibalRight;
        this.missionaryLeft = father.missionaryLeft;
        this.missionaryRight = father.missionaryRight;
        this.boat = father.boat;
        this.routesCount = father.routesCount;
        this.father = father;
    }
    
    private boolean constraints (int m, int c, Position p) {
        // Boat constraint
        // Check if any Missionaries will board
        if (m == 0) {
            // At least one Cannibal or <= boat capacity Cannibals can board
            if (c <= 0 || c > M) {
                return false;
            }
        }
        else {
            // Missionaries & Cannibals that board must fit in the boat
            // and Cannibals must be less than or equal to the Missionaries
            if (m + c > M || m < c) {
                return false;
            }
        }

        // Initial constraint
        if (p == Position.LEFT) {
            // Check if there are any Missionaries left on this side (left)
            if (missionaryLeft - m != 0) {
                // Remaining Cannibals must be less than or equal
                // to the remaining Missionaries
                if (cannibalLeft - c > missionaryLeft - m) {
                    return false;
                }
            }
        }
        else {
            // Check if there are any Missionaries left on this side (right)
            if (missionaryRight - m != 0) {
                // Remaining Cannibals must be less than or equal
                // to the remaining Missionaries
                if (cannibalRight - c > missionaryRight - m) {
                    return false;
                }
            }
        }

        // Final constraint
        if (p == Position.LEFT) {
            // Check the other side (right) to see if there
            // will be Missionaries there after the boat arrives
            if (missionaryRight + m != 0) {
                // Existing + arriving Cannibals must be less than or equal
                // to the existing + arriving Missionaries
                if (cannibalRight + c > missionaryRight + m) {
                    return false;
                }
            }
        }
        else {
            // Check the other side (left) to see if there
            // will be Missionaries there after the boat arrives
            if (missionaryLeft + m != 0) {
                // Existing + arriving Cannibals must be less than or equal
                // to the existing + arriving Missionaries
                if (cannibalLeft + c > missionaryLeft + m) {
                    return false;
                }
            }
        }
        return true;
    }

    // Function that returns an ArrayList with all the valid descendant States
    ArrayList<StateSpace> getChildren() {
        ArrayList<StateSpace> children = new ArrayList<>();

        if (boat == Position.LEFT) {
            for (int i = 0; i <= missionaryLeft; i++) {
                for (int j = 0; j <= cannibalLeft; j++) {
                    // very important to create a copy of current state before each move.
                    StateSpace child = new StateSpace(this);

                    // If the new child is a valid state, calculate its attributes
                    // and add it to the ArrayList
                    if (constraints(i, j, boat)) {
                        child.setCannibalLeft(cannibalLeft - j);
                        child.setCannibalRight(cannibalRight + j);
                        child.setMissionaryLeft(missionaryLeft - i);
                        child.setMissionaryRight(missionaryRight + i);
                        child.setBoat(Position.RIGHT);
                        child.incrementRoutesCount();
                        child.heuristic();
                        children.add(child);
                        System.out.println(child);
                    }
                }
            }
        }
        else if (boat == Position.RIGHT) {
            for (int i = 0; i <= missionaryRight; i++) {
                for (int j = 0; j <= cannibalRight; j++) {
                    // very important to create a copy of current state before each move.
                    StateSpace child = new StateSpace(this);

                    // If the new child is a valid state, calculate its attributes
                    // and add it to the ArrayList
                    if (constraints(i, j, boat)) {
                        child.setCannibalLeft(cannibalLeft + j);
                        child.setCannibalRight(cannibalRight - j);
                        child.setMissionaryLeft(missionaryLeft + i);
                        child.setMissionaryRight(missionaryRight - i);
                        child.setBoat(Position.LEFT);
                        child.incrementRoutesCount();
                        child.heuristic();
                        children.add(child);
                        System.out.println(child);
                    }
                }
            }
        }
        return children;
    }

    // Heuristic Function
    // Calculates a score which indicates the minimum number of routes needed to reach the final state.
    // Doesn't take into account the "Missionaries >= Cannibals" constraint
    //M=Capacity of boat
    private void heuristic() {
        int sum = missionaryLeft + cannibalLeft;
        if (sum % (M-1) > 1){
            score++;
        }
        else if (sum % (M-1) < 1) {
            score--;
        }
        if (boat == Position.RIGHT) {
            score++;
        }
    }

	public StateSpace(int cannibalLeft, int missionaryLeft, Position boat, int cannibalRight, int missionaryRight) {
		this.cannibalLeft = cannibalLeft;
		this.missionaryLeft = missionaryLeft;
		this.boat = boat;
		this.cannibalRight = cannibalRight;
		this.missionaryRight = missionaryRight;
	}

	public boolean isGoal() {
		return cannibalLeft == 0 && missionaryLeft == 0;
	}

	public boolean isValid() {
		if (missionaryLeft >= 0 && missionaryRight >= 0 && cannibalLeft >= 0 && cannibalRight >= 0
				&& (missionaryLeft == 0 || missionaryLeft >= cannibalLeft)
				&& (missionaryRight == 0 || missionaryRight >= cannibalRight)) {
			return true;
		}
		return false;
	}

	public List<StateSpace> generateSuccessors() {
		List<StateSpace> successors = new ArrayList<StateSpace>();
		if (boat == Position.LEFT) {
			testAndAdd(successors, new StateSpace(cannibalLeft, missionaryLeft - 2, Position.RIGHT,
					cannibalRight, missionaryRight + 2)); // Two missionaries cross left to right.
			testAndAdd(successors, new StateSpace(cannibalLeft - 2, missionaryLeft, Position.RIGHT,
					cannibalRight + 2, missionaryRight)); // Two cannibals cross left to right.
			testAndAdd(successors, new StateSpace(cannibalLeft - 1, missionaryLeft - 1, Position.RIGHT,
					cannibalRight + 1, missionaryRight + 1)); // One missionary and one cannibal cross left to right.
			testAndAdd(successors, new StateSpace(cannibalLeft, missionaryLeft - 1, Position.RIGHT,
					cannibalRight, missionaryRight + 1)); // One missionary crosses left to right.
			testAndAdd(successors, new StateSpace(cannibalLeft - 1, missionaryLeft, Position.RIGHT,
					cannibalRight + 1, missionaryRight)); // One cannibal crosses left to right.
		} else {
			testAndAdd(successors, new StateSpace(cannibalLeft, missionaryLeft + 2, Position.LEFT,
					cannibalRight, missionaryRight - 2)); // Two missionaries cross right to left.
			testAndAdd(successors, new StateSpace(cannibalLeft + 2, missionaryLeft, Position.LEFT,
					cannibalRight - 2, missionaryRight)); // Two cannibals cross right to left.
			testAndAdd(successors, new StateSpace(cannibalLeft + 1, missionaryLeft + 1, Position.LEFT,
					cannibalRight - 1, missionaryRight - 1)); // One missionary and one cannibal cross right to left.
			testAndAdd(successors, new StateSpace(cannibalLeft, missionaryLeft + 1, Position.LEFT,
					cannibalRight, missionaryRight - 1)); // One missionary crosses right to left.
			testAndAdd(successors, new StateSpace(cannibalLeft + 1, missionaryLeft, Position.LEFT,
					cannibalRight - 1, missionaryRight)); // One cannibal crosses right to left.
		}
		return successors;
	}

	private void testAndAdd(List<StateSpace> successors, StateSpace newState) {
		if (newState.isValid()) {
			newState.setParentState(this);
			successors.add(newState);
		}
	}

	public int getCannibalLeft() {
		return cannibalLeft;
	}

	public void setCannibalLeft(int cannibalLeft) {
		this.cannibalLeft = cannibalLeft;
	}

	public int getMissionaryLeft() {
		return missionaryLeft;
	}

	public void setMissionaryLeft(int missionaryLeft) {
		this.missionaryLeft = missionaryLeft;
	}

	public int getCannibalRight() {
		return cannibalRight;
	}

	public void setCannibalRight(int cannibalRight) {
		this.cannibalRight = cannibalRight;
	}

	public int getMissionaryRight() {
		return missionaryRight;
	}

	public void setMissionaryRight(int missionaryRight) {
		this.missionaryRight = missionaryRight;
	}

	public void goToLeft() {
		boat = Position.LEFT;
	}

	public void goToRight() {
		boat = Position.RIGHT;
	}

	public boolean isOnLeft() {
		return boat == Position.LEFT;
	}

	public boolean isOnRigth() {
		return boat == Position.RIGHT;
	}

	public StateSpace getParentState() {
		return parentState;
	}

	public void setParentState(StateSpace parentState) {
		this.parentState = parentState;
	}
	
	public Position getBoat() {
		return boat;
	}

	public void setBoat(Position boat) {
		this.boat = boat;
	}

	public int getRoutesCount() {
		return routesCount;
	}

	public void setRoutesCount(int routesCount) {
		this.routesCount = routesCount;
	}
	
	public void incrementRoutesCount() { 
		this.routesCount++; 
	}

	@Override
	public String toString() {
		if (boat == Position.LEFT) {
			return "(" + cannibalLeft + "," + missionaryLeft + ",L,"
					+ cannibalRight + "," + missionaryRight + ")";
		} else {
			return "(" + cannibalLeft + "," + missionaryLeft + ",R,"
					+ cannibalRight + "," + missionaryRight + ")";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StateSpace)) {
			return false;
		}
		StateSpace s = (StateSpace) obj;
		return (s.cannibalLeft == cannibalLeft && s.missionaryLeft == missionaryLeft
				&& s.boat == boat && s.cannibalRight == cannibalRight
				&& s.missionaryRight == missionaryRight);
	}


	@Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + this.missionaryLeft;
        hash = 61 * hash + this.missionaryRight;
        hash = 61 * hash + this.cannibalLeft;
        hash = 61 * hash + this.cannibalRight;
        if (boat == Position.LEFT) {
            hash = 61 * hash + 7;
        }
        else {
            hash = 61 * hash + 11;
        }

        return hash;
    }
	
	void print() {
        System.out.println("-------------------------------------");

        System.out.println("Left side -> C: " + cannibalLeft + " | M: " + missionaryLeft);
        if(boat == Position.LEFT) System.out.println("\nBoat is Left\n");
        else System.out.println("\nBoat is Right\n");
        System.out.println("Right side -> C: " + cannibalRight + " | M: " + missionaryRight);

        System.out.println("-------------------------------------");
    }
	
	@Override
	public int compareTo(StateSpace s) {
		 return Double.compare(this.score + this.routesCount, s.score + s.routesCount);
	}
}