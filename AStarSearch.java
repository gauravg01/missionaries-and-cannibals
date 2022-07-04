import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class AStarSearch {
    private ArrayList<StateSpace> fifo;
    private HashSet<StateSpace> closedSet;
    private int maxRoutes;

    AStarSearch(int K) {
        this.fifo = new ArrayList<>();
        this.closedSet = new HashSet<>();
        this.maxRoutes = K;
    }

    StateSpace AStarClosedSet (StateSpace initialState) {
        //step 1: put initial state in the fifo.
        this.fifo.add(initialState);

        //step 2: check for empty fifo.
        while (this.fifo.size() > 0) {
            //step 3: get the first node out of the fifo.
            StateSpace currentState = this.fifo.remove(0);

            //step 4: if final state, return.
            if (currentState.isGoal()) return currentState;

            //step 5: if the node is not in the closed set, put the children at the fifo.
            //else go to step 2.
            if (!this.closedSet.contains(currentState) && currentState.getRoutesCount() < maxRoutes) {
                this.closedSet.add(currentState);
                this.fifo.addAll(currentState.getChildren());
                // step 6: sort the fifo based on the heuristic score and the current routesCount to get best as first
                Collections.sort(this.fifo); // sort the fifo to get best as first
            }
        }
        return null;
    }
}