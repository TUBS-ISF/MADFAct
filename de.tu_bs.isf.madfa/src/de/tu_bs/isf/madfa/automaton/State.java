package de.tu_bs.isf.madfa.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tu_bs.isf.madfa.util.Util;

/**
 * Class for the states of the automaton
 * @author Tobias
 *
 */
public class State {

	private List<Transition> outgoingTransitions;
	private List<Transition> incomingTransitions;
	private String id;
	private boolean finalState;
	private static int nextId = 0;
	
	public List<Transition> getOutgoingTransitions() {
		return outgoingTransitions;
	}
	
	public List<Transition> getIncomingTransitions() {
		return incomingTransitions;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public synchronized static int getNextId() {
		nextIdUp();
		return nextId - 1;
	}

	public synchronized static void nextIdUp() {
		nextId++;
	}
	
	public static void nextIdToZero() {
		nextId = 0;
	}

	public boolean isFinalState() {
		return finalState;
	}
	
	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}
	
	public void addIncomingTransition(Transition transition) {
		incomingTransitions.add(transition);
	}
	
	public void addOutgoingTransition(Transition transition) {
		outgoingTransitions.add(transition);
	}

	/**
	 * Constructor to create a new non-final state
	 */
	public State() {
		this.outgoingTransitions = new ArrayList<Transition>();
		this.incomingTransitions = new ArrayList<Transition>();
		this.id = "" + getNextId();
		this.finalState = false;
	}
	
	/**
	 * Constructor to create a new state
	 * @param finalState boolean, if the state is final
	 */
	public State(boolean finalState) {
		this.outgoingTransitions = new ArrayList<Transition>();
		this.incomingTransitions = new ArrayList<Transition>();
		this.id = "" + getNextId();
		this.finalState = finalState;
	}
	
	/**
	 * collect and return all outgoing labels of the state
	 * @return a list with all outgoing labels of the state
	 */
	public List<String> outgoingLabels() {
		List<String> labels = new ArrayList<>();
		for (Transition transition : this.getOutgoingTransitions()) {
			labels.add(transition.getLabel());
		}
		return labels;
	}
	
	/**
	 * checks if the state is a confluence state (if it has more than one in-transition)
	 * @return boolean if the state is a confluence state
	 */
	public boolean isConfluence() {
		return incomingTransitions.size() > 1;
	}
	
	/**
	 * checks if two states are equivalent (they have the same right language)
	 * @param first the first state
	 * @param second the second state
	 * @return boolean if two states are equivalent
	 */
	public static boolean isEquivalence(State first, State second) {
//		long nanoTime = System.nanoTime();
		List<String> firstWords = Util.rightLanguageOfState(first);
		List<String> secondWords = Util.rightLanguageOfState(second);
		if (firstWords.size() == secondWords.size()) {
			Collections.sort(firstWords);
			Collections.sort(secondWords);
			return firstWords.equals(secondWords);
		} else {
			return false;
		}
		
//		if (firstWords.size() == secondWords.size()) {
//			for (String word : firstWords) {
//				boolean result = false;
//				for (String word2 : secondWords) {
//					if (word.equals(word2)) {
//						result = true;
//					}
//				}
//				if (result == false) {
//					nanoTime = System.nanoTime() - nanoTime;
//					RuntimeMap.insertInto("EquivalenceMethodCalls", 1);
//					RuntimeMap.insertInto("EquivalenceMethodRuntime", nanoTime);
//					return false;
//				}
//			}
//			nanoTime = System.nanoTime() - nanoTime;
//			RuntimeMap.insertInto("EquivalenceMethodCalls", 1);
//			RuntimeMap.insertInto("EquivalenceMethodRuntime", nanoTime);
//			return true;
//		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("EquivalenceMethodCalls", 1);
//		RuntimeMap.insertInto("EquivalenceMethodRuntime", nanoTime);
//		return false;
	}
	
	/**
	 * checks if two states are equivalent (they have the same right language)
	 * @param first the first state
	 * @param second the second state
	 * @return boolean if two states are equivalent
	 */
	public static boolean isEquivalenceFast(State first, State second) {
//		long nanoTime = System.nanoTime();
		List<String> firstLabels = first.outgoingLabels();
		List<String> secondLabels = second.outgoingLabels();
		if (firstLabels.size() == secondLabels.size() && first.isFinalState() == second.isFinalState()) {
			for (String nextChar : firstLabels) {
				boolean result = false;
				for (String nextChar2 : secondLabels) {
					if (nextChar.equals(nextChar2)) {
						if(Util.getNextState(first, nextChar).equals(Util.getNextState(second, nextChar))) {
							result = true;
							break;
						}
					}
				}
				if (result == false) {
//					nanoTime = System.nanoTime() - nanoTime;
//					RuntimeMap.insertInto("EquivalenceFastMethodCalls", 1);
//					RuntimeMap.insertInto("EquivalenceFastMethodRuntime", nanoTime);
					return false;
				}
			}
//			nanoTime = System.nanoTime() - nanoTime;
//			RuntimeMap.insertInto("EquivalenceFastMethodCalls", 1);
//			RuntimeMap.insertInto("EquivalenceFastMethodRuntime", nanoTime);
			return true;
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("EquivalenceFastMethodCalls", 1);
//		RuntimeMap.insertInto("EquivalenceFastMethodRuntime", nanoTime);
		return false;
	}

	/**
	 * returns a string with the content of the state
	 * @param tabs tabs for readability
	 * @return string with the content of the state
	 */
	public String printState(String tabs) {
		String s = "" + this.getId();
		if(this.isFinalState()) {
			s += "F";
		}
		for (Transition transition : this.getOutgoingTransitions()) {
			s += "\n" + tabs + this.getId() + "->" +  transition.getLabel() + "->" + transition.getEndState().printState(tabs + "-");
		}
		return s;
	}
	
	/**
	 * returns the transition to the end state with the label
	 * @param endState the end state
	 * @param label the label
	 * @return the transition of the state to the end state
	 */
	public Transition getTransition(State endState, String label) {
		for (Transition transition : outgoingTransitions) {
			if (transition.getEndState().equals(endState) && transition.getLabel().equals(label)) {
				return transition;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "" + id;
	}
}
