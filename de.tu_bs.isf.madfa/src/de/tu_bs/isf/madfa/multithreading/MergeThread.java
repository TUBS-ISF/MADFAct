package de.tu_bs.isf.madfa.multithreading;

import de.tu_bs.isf.madfa.automaton.Automaton;

public class MergeThread extends Thread {

	private Automaton firstAutomaton;
	private Automaton secondAutomaton;
	private Automaton mergedAutomaton;
	   
	   
	public MergeThread(Automaton firstAutomaton, Automaton secondAutomaton, Automaton mergedAutomaton) {
		this.firstAutomaton = firstAutomaton;
		this.secondAutomaton = secondAutomaton;
		this.mergedAutomaton = mergedAutomaton;
	}


	public void run() {
		Multithreading.mergeAutomata(firstAutomaton, secondAutomaton, mergedAutomaton);
		Multithreading.minimizeMergedAutomaton(mergedAutomaton);
	}
}