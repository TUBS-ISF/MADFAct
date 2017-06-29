package de.tu_bs.isf.madfa.algorithms.spl;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of the algorithm General
 * Algorithm minimizes the MADFA per height level
 * @author Tobias
 *
 */
public class AlgorithmSPL_N extends AlgorithmSPL_T {
	
	@Override
	protected void addWord(String word) {
//		long nanoTime = System.nanoTime();
		
		State currentState = this.automaton.getStartState();
		String left = "";
		String right = word;
		
		while (!right.isEmpty() && Util.stateExist(currentState, Util.head(right)) && !Util.getNextState(currentState, Util.head(right)).isConfluence()) {
			String nextChar = Util.head(right);
			currentState = Util.getNextState(currentState, nextChar);
			left += nextChar;
			right = Util.tail(right);
		}
		
		while (!right.isEmpty() && Util.stateExist(currentState, Util.head(right))) {
			String nextChar = Util.head(right);
			State nextState = Util.getNextState(currentState, nextChar);
			State cloneState = Util.cloneState(automaton, nextState);
			
			Transition transition = currentState.getTransition(nextState, nextChar);
			nextState.getIncomingTransitions().remove(transition);
			transition.setEndState(cloneState);
			cloneState.addIncomingTransition(transition);
			automaton.addState(cloneState);
			if(cloneState.isFinalState()) {
				automaton.addFinalState(cloneState);
			}
			currentState = cloneState;
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
	public AlgorithmSkeleton copy() {
		return new AlgorithmSPL_N();
	}
}
