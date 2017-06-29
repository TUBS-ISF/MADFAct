package de.tu_bs.isf.madfa.algorithms.spl;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of the algorithm Incremental
 * Incremental algorithm to keep the MADFa minimal
 * @author Tobias
 *
 */
public class AlgorithmSPL_I extends AlgorithmSPL_N {

	@Override
	protected void addWord(String word) {
		super.addWord(word);
		long nanoTime = System.nanoTime();
		visitMin(automaton.getStartState(), "", word);
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("visitMin " + word + ": " + nanoTime + "ns");
	}
	
	@Override
	protected void cleanUp() {
	}
	
	/**
	 * Helper method to visit the added states and merge them
	 * @param state the visit state
	 * @param left the left letters of the word
	 * @param right the right letters of the word
	 */
	private void visitMin(State state, String left, String right) {
//		long nanoTime = System.nanoTime();
		long nanoTimeRec = 0;
		String completeWord = left + right;
		
		if(!right.isEmpty()) {
			left += Util.head(right);
			nanoTimeRec = System.nanoTime();
			visitMin(Util.getNextState(state, Util.head(right)), left, Util.tail(right));
			nanoTimeRec = System.nanoTime() - nanoTimeRec;
		}
		
		List<State> checkStates = new ArrayList<State>();
		if(!state.getOutgoingTransitions().isEmpty()) {
			State endState = state.getOutgoingTransitions().get(0).getEndState();
			for(Transition transition : endState.getIncomingTransitions()) {
				State startState = transition.getStartState();
				if(startState.isFinalState() == state.isFinalState()) {
					checkStates.add(startState);
				}
			}
		} else if (state.isFinalState()) {
			for (State tmpState : automaton.getFinalStates()) {
				if(tmpState.getOutgoingTransitions().isEmpty()) {
					checkStates.add(tmpState);
				}
			}
		} else {
			List<State> pathOfStartState = Util.pathOfState(automaton.getStartState(), completeWord);
			for (State tmpState : automaton.getStates()) {
				checkStates.add(tmpState);
			}
			checkStates.removeAll(pathOfStartState);
		}
		
		for(State q : checkStates) {
			if (automaton.getStates().contains(state)) {
				if (!q.equals(state)) {
					if(State.isEquivalenceFast(state, q)) {
						Util.merge(automaton, state, q);
					}
				}
			} else {
				break;
			}
		}
//		nanoTime = System.nanoTime() - nanoTime - nanoTimeRec;
//		RuntimeMap.insertInto("visitMinMethodCalls", 1);
//		RuntimeMap.insertInto("visitMinMethodRuntime", nanoTime);
	}
	
	@Override
	public AlgorithmSkeleton copy() {
		return new AlgorithmSPL_I();
	}
}
