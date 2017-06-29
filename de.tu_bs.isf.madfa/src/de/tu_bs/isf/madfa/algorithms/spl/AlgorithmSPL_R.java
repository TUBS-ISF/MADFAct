package de.tu_bs.isf.madfa.algorithms.spl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.Automaton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of algorithm Reverse
 * Algorithm reverses and determinizes the automaton
 * @author Tobias
 *
 */
public class AlgorithmSPL_R extends AlgorithmSPL_T{

	@Override
	protected void addWord(String word) {
		String reversedWord = Util.reverse(word);
		super.addWord(reversedWord);
	}
	
	@Override
	protected void cleanUp() {
//		long nanoTime = System.nanoTime();
		
		Automaton reversedAutomat = new Automaton();
		State startState = new State();
		reversedAutomat.setStartState(startState);
		reversedAutomat.addState(startState);
		List<Set<State>> toDo = new ArrayList<>();
		List<State> registry = new ArrayList<>();
		registry.add(startState);
		String stateString = "";
		Set<State> stateSet = new HashSet<>();
		for (State finalState : this.automaton.getFinalStates()) {
			stateSet.add(finalState);
			stateString += finalState.getId() + ";";
		}
		toDo.add(stateSet);
		startState.setId(stateString);
		
		while(!toDo.isEmpty()) {
			Set<State> nextStateSet = toDo.get(0);
			toDo.remove(0);
			String startStateString = "";
			boolean isFinal = false;
			for (State tmpState : nextStateSet) {
				startStateString += tmpState.getId() + ";";
				if(automaton.getStartState().equals(tmpState)) {
					isFinal = true;
				}
			}
			for(char letter = 'a'; letter <= 'z'; letter++) {
				String letterAsString = Character.toString(letter);
				Set<State> newStateSet = new HashSet<>();
				String newStateString = "";
				for(State nextState : nextStateSet) {
					for(Transition trans : nextState.getIncomingTransitions()) {
						if(trans.getLabel().equals(letterAsString)) {
							newStateSet.add(trans.getStartState());
							newStateString += trans.getStartState().getId() + ";";
						}
					}
				}
				startState = getStateForStateID(startStateString, registry);
				if(isFinal) {
					startState.setFinalState(isFinal);
				}
				if (!newStateSet.isEmpty()) {
					boolean newState = true;
					for (State registryState : registry) {
						if(compareStateId(registryState.getId(), newStateString)) {
							newState = false;
						}
					}
					if(newState == true) {
						toDo.add(newStateSet);
						State newStateForStateSet = new State();
						newStateForStateSet.setId(newStateString);
						registry.add(newStateForStateSet);
						Transition transition = new Transition(startState, newStateForStateSet, letterAsString);
						startState.addOutgoingTransition(transition);
						newStateForStateSet.addIncomingTransition(transition);
					} else {
						State endState = getStateForStateID(newStateString, registry);
						Transition transition = new Transition(startState, endState, letterAsString);
						startState.addOutgoingTransition(transition);
						endState.addIncomingTransition(transition);
					}
			}
		}
	}
		for (State state : registry) {
			if (!reversedAutomat.getStates().contains(state)) {
				reversedAutomat.addState(state);
			}
			if (!reversedAutomat.getFinalStates().contains(state) && state.isFinalState()) {
				reversedAutomat.addFinalState(state);
			}
			for (Transition transition : state.getOutgoingTransitions()) {
				if(!reversedAutomat.getTransitions().contains(transition)) {
					reversedAutomat.addTransition(transition);
				}
			}
		}
//		int i = 0;
//		for(State tmpState : reversedAutomat.getStates()) {
//			tmpState.setId("" + i);
//			i++;
//		}
		automaton = reversedAutomat;
		
//		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("cleanUp: " + nanoTime + "ns");
//		RuntimeMap.insertInto("CleanUpMethodCalls", 1);
//		RuntimeMap.insertInto("CleanUpMethodRuntime", nanoTime);
	}
	
	/**
	 * Method to compare two sets of ids if they contain the same states
	 * @param firstString the first set of ids
	 * @param secondString the second set of ids
	 * @return boolean if they are the same
	 */
	private boolean compareStateId(String firstString, String secondString) {
		if(firstString.length() == secondString.length()) {
			String[] firstArray = firstString.split(";");
			String[] secondArray = secondString.split(";");
			for(int i = 0; i < firstArray.length; i++){
				String letterOfFirstString = firstArray[i];
				boolean containsLetter = false;
				for(int j = 0; j < secondArray.length; j++) {
					String letterOfSecondString = secondArray[j];
					if(letterOfFirstString.equals(letterOfSecondString)) {
						containsLetter = true;
					}
				}
				if(containsLetter == false) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * gets the state with same id as IdString
	 * @param IdString the id of the state that should be returned
	 * @param registry the list of states
	 * @return the state with the same id as IdString
	 */
	private State getStateForStateID(String IdString, List<State> registry) {
		for(State state : registry) {
			if (compareStateId(IdString, state.getId())) {
				return state;
			}
		}
		return null;
	}
	
	@Override
	public AlgorithmSkeleton copy() {
		return new AlgorithmSPL_R();
	}
}
