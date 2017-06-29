package de.tu_bs.isf.madfa.algorithms.spl;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of algorithm Sorted
 * Adds the words in lexicographic order and minimizes the automaton afterwards
 * @author Tobias
 *
 */
public class AlgorithmSPL_S extends AlgorithmSPL_T {

	private String lexGreatestWord = "";
	
	@Override
	protected void addWord(String word) {
		super.addWord(word);
		
		long nanoTime = System.nanoTime();
		String u = Util.longestCommonPrefix(lexGreatestWord, word);
		String v = Util.leftDerivate(u, lexGreatestWord);
		while(!v.isEmpty()) {
			u += Util.head(v);
			v = Util.tail(v);
			visitMin(u, v, word);
		}
		lexGreatestWord = word;
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("visitMin " + word + ": " + nanoTime + "ns");
	}
	
	@Override
	protected void cleanUp() {
		long nanoTime = System.nanoTime();
		visitMin("", lexGreatestWord, "");
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("cleanUp_visitMin: " + nanoTime + "ns");
	}
	
	/**
	 * Helper method to visit the added states and merge them
	 * @param left the left letters of the word
	 * @param right the right letters of the word
	 * @param word the whole word
	 */
	private void visitMin(String left, String right, String word) {
//		long nanoTime = System.nanoTime();
		long nanoTimeRec = 0;
		String completeWord = left + right;
		List<State> states = Util.pathOfState(automaton.getStartState(), left);
		State state = states.get(states.size() - 1);
		
		if(!right.isEmpty()) {
			left += Util.head(right);
			nanoTimeRec = System.nanoTime();
			visitMin(left, Util.tail(right), word);
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
			List<State> pathOfStartState = Util.pathOfState(automaton.getStartState(), word);
			checkStates.removeAll(pathOfStartState);
		} else {
			List<State> pathOfStartState = Util.pathOfState(automaton.getStartState(), completeWord);
			List<State> pathOfStartState2 = Util.pathOfState(automaton.getStartState(), word);
			for (State tmpState : automaton.getStates()) {
				checkStates.add(tmpState);
			}
			checkStates.removeAll(pathOfStartState);
			checkStates.removeAll(pathOfStartState2);
		}
		
		for(State q : checkStates) {
			if(automaton.getStates().contains(state)) {
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
		return new AlgorithmSPL_S();
	}
}
