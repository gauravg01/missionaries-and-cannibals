import java.util.List;

public class DepthFirstSearch {

	public StateSpace DFS(StateSpace initialState) {
		int limit = 20;
		return DFS(initialState, limit);
	}

	private StateSpace DFS(StateSpace state, int limit) {
		if (state.isGoal()) {
			return state;
		} else if (limit == 0) {
			return null;
		} else {
			List<StateSpace> successors = state.generateSuccessors();
			for (StateSpace child : successors) {
				StateSpace result = DFS(child, limit - 1);
				if (null != result) {
					return result;
				}
			}
			return null;
		}
	}
}