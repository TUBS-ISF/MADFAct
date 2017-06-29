package de.tu_bs.isf.madfa.automaton;

/**
 * Class for the transitions of the automaton
 * @author Tobias
 *
 */
public class Transition {
	
	private State startState;
	private State endState;
	private String label;
	
	public State getStartState() {
		return startState;
	}
	
	public void setStartState(State startState) {
		this.startState = startState;
	}
	
	public State getEndState() {
		return endState;
	}
	
	public void setEndState(State endState) {
		this.endState = endState;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Constructor to create a new transition and link it to a start and end state
	 * @param startState the start state of the transition
	 * @param endState the end state of the transition
	 * @param label the label of the transition
	 */
	public Transition(State startState, State endState, String label) {
		this.startState = startState;
		this.endState = endState;
		this.label = label;
	}
	
	@Override
	public String toString() {
		return "" + startState.getId() + "->" + label + "->" + endState.getId();
	}
	
}
