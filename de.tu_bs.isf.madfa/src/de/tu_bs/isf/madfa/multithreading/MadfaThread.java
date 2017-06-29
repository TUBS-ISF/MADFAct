package de.tu_bs.isf.madfa.multithreading;

import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.Automaton;

public class MadfaThread extends Thread {

	private AlgorithmSkeleton algorithm;
	private List<Automaton> intermediateAutomata;
	private List<String> words;
	   
	   MadfaThread(AlgorithmSkeleton algorithm, List<Automaton> intermediateAutomata, List<String> words) {
		   this.algorithm = algorithm.copy();
		   this.intermediateAutomata = intermediateAutomata;
		   this.words = words;
	   }
	   
	public void run() {
		Automaton automaton = new Automaton();
		automaton = algorithm.createMadfa(words, false);
		synchronized(intermediateAutomata) {
			intermediateAutomata.add(automaton);
		}
	}
}
