import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GreedyFirstSearch {

	public StateSpace GFS(StateSpace initialState) {
		if (initialState.isGoal()) {
			return initialState;
		}
		Queue<StateSpace> fifo = new LinkedList<StateSpace>();
		Set<StateSpace> traversed = new HashSet<StateSpace>();
		fifo.add(initialState);
		while (true) {
			if (fifo.isEmpty()) {
				return null;
			}
			StateSpace state = fifo.poll();
			traversed.add(state);
			List<StateSpace> successors = state.generateSuccessors();
			for (StateSpace child : successors) {
				if (!traversed.contains(child) || !fifo.contains(child)) {
					if (child.isGoal()) {
						return child;
					}
					fifo.add(child);
				}
			}
		}
	}
}