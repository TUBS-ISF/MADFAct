package de.tu_bs.isf.madfa.algorithms.spl;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of algorithm Trie
 * Minimizes the MADFA per height level
 * @author Tobias
 *
 */
public class AlgorithmSPL_T extends AlgorithmSkeleton {

	@Override
	protected void addWord(String word) {
//		long nanoTime = System.nanoTime();
		State currentState = this.automaton.getStartState();
		String left = "";
		String right = word;
		
		while (!right.isEmpty() && Util.stateExist(currentState, Util.head(right))) {
			String nextChar = Util.head(right);
			currentState = Util.getNextState(currentState, nextChar);
			left += nextChar;
			right = Util.tail(right);
		}
		
		while (!right.isEmpty()) {
			String nextChar = Util.head(right);
			State newState = new State();
			Transition transition = new Transition(currentState, newState, nextChar);
			currentState.addOutgoingTransition(transition);
			newState.addIncomingTransition(transition);
			automaton.addState(newState);
			automaton.addTransition(transition);
			currentState = newState;
			left += nextChar;
			right = Util.tail(right);
		}
		
		currentState.setFinalState(true);
		automaton.addFinalState(currentState);
		
//		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("addWord " + word + ": " + nanoTime + "ns");
//		RuntimeMap.insertInto("addWordMethodCalls", 1);
//		RuntimeMap.insertInto("addWordMethodRuntime", nanoTime);
		
		
	}

	@Override
	protected void cleanUp() {
		
//		long nanoTime = System.nanoTime();
//		long nanoTimeLoop = 0;
		
		int height = 0;
		List<State> heightLevel = new ArrayList<>();
		while (!(heightLevel = Util.heightLevel(automaton, height)).isEmpty()) {
//			nanoTimeLoop = System.nanoTime();
			for(State stateP : heightLevel) {
				if (automaton.getStates().contains(stateP)) {
					for(State stateQ : heightLevel) {
						if (automaton.getStates().contains(stateQ)) {
							if (!stateP.equals(stateQ)) {
								if (State.isEquivalenceFast(stateP, stateQ)) {
									Util.merge(automaton, stateQ, stateP);
								}
							}
						}
					}
				}
			}
//			nanoTimeLoop = System.nanoTime() - nanoTimeLoop;
//			RuntimeMap.insertInto("CleanUpLoopMethodCalls", 1);
//			RuntimeMap.insertInto("CleanUpLoopMethodRuntime", nanoTimeLoop);
			height++;
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("cleanUp: " + nanoTime + "ns");
//		RuntimeMap.insertInto("CleanUpMethodCalls", 1);
//		RuntimeMap.insertInto("CleanUpMethodRuntime", nanoTime);
	}

	@Override
	public AlgorithmSkeleton copy() {
		return new AlgorithmSPL_T();
	}
}
