package de.tu_bs.isf.madfa.automaton;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for the MADFA/ADFA
 * Contains states and transitions
 * @author Tobias
 *
 */
public class Automaton {

	private Set<State> states;
	private Set<Transition> transitions;
	private State startState;
	private Set<State> finalStates;
	
	public Set<State> getStates() {
		return states;
	}
	
	public void setStates(Set<State> states) {
		this.states = states;
	}
	
	public void addState(State state) {
		this.states.add(state);
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public void setTransitions(Set<Transition> transitions) {
		this.transitions = transitions;
	}
	
	public void addTransition(Transition transition) {
		this.transitions.add(transition);
	}
	
	public State getStartState() {
		return startState;
	}
	
	public void setStartState(State startState) {
		this.startState = startState;
	}
	
	public Set<State> getFinalStates() {
		return finalStates;
	}
	
	public void setFinalStates(Set<State> finalStates) {
		this.finalStates = finalStates;
	}
	
	public void addFinalState(State finalState) {
		this.finalStates.add(finalState);
	}

	/**
	 * Constructor to create an empty automaton
	 */
	public Automaton() {
		this.states = new HashSet<State>();
		this.transitions = new HashSet<Transition>();
		this.startState = null;
		this.finalStates = new HashSet<State>();
	}
	
	/**
	 * Constructor to create an automaton with states and transitions
	 * @param states the states of the automaton
	 * @param transitions the transitions of the automaton
	 * @param startState the start state
	 * @param finalStates the final states of the automaton
	 */
	public Automaton(Set<State> states, Set<Transition> transitions,
			State startState, Set<State> finalStates) {
		this.states = states;
		this.transitions = transitions;
		this.startState = startState;
		this.finalStates = finalStates;
	}
	
	/**
	 * Checks if the automaton is a trie
	 * @return boolean if the automaton is a trie
	 */
	public boolean isTrie() {
		for (State state : states) {
			if (state.isConfluence()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * prints the whole automaton to the console
	 */
	public void printAutomat() {
		String s = "";
		for (Transition transition : startState.getOutgoingTransitions()) {
			s+= "\n" + startState.getId() + "->" + transition.getLabel() + "->" + transition.getEndState().printState("-");
		}
		System.out.println(s);
	}
}
