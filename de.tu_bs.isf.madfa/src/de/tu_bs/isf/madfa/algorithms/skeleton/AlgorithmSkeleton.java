package de.tu_bs.isf.madfa.algorithms.skeleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tu_bs.isf.madfa.automaton.Automaton;
import de.tu_bs.isf.madfa.automaton.State;

/**
 * Class for the algorithm skeleton
 * Skeleton to create a MADFA with the abstract algorithms addWord and cleanUp
 * @author Tobias
 *
 */
public abstract class AlgorithmSkeleton {
	
	protected Automaton automaton;

	public AlgorithmSkeleton() {
	}

	/**
	 * Creates a new automaton for the list of words
	 * @param words the list of words
	 * @return the MADFA for the words
	 */
	public Automaton createMadfa(List<String> words) {
		return createMadfa(words, true);
	}
	
	/**
	 * Creates a new automaton for the list of words
	 * @param words the list of words
	 * @param nextIdToZero boolean if the Id of states should be reseted to 0
	 * @return the MADFA for the words
	 */
	public Automaton createMadfa(List<String> words, boolean nextIdToZero) {
		this.automaton = new Automaton();
		if (nextIdToZero) State.nextIdToZero();
		State startState = new State();
		this.automaton.addState(startState);
		this.automaton.setStartState(startState);
		List<String> done = new ArrayList<>();
		List<String> todo = new ArrayList<>();
		for(String tmpWord : words) {
			todo.add(tmpWord);
		}
		
		for (Iterator<String> it = todo.iterator(); it.hasNext();) {
			String nextWord = it.next();
			addWord(nextWord);
			done.add(nextWord);
			it.remove();
		}
		cleanUp();
//		automaton.printAutomat();
		return this.automaton;
	}
	
	/**
	 * Creates a automaton on the base of the delivered automaton for the list of words
	 * @param words the list of words
	 * @param automat the base automaton
	 * @return the created MADFA for the words
	 */
	public Automaton createMadfa(List<String> words, Automaton automat) {
		this.automaton = automat;
		List<String> done = new ArrayList<>();
		List<String> todo = new ArrayList<>();
		for(String tmpWord : words) {
			todo.add(tmpWord);
		}
		
		for (Iterator<String> it = todo.iterator(); it.hasNext();) {
			String nextWord = it.next();
			addWord(nextWord);
			done.add(nextWord);
			it.remove();
		}
		cleanUp();
		return this.automaton;
	}
	
	/**
	 * Method to add a word to the automaton
	 * @param word the word that should be added
	 */
	protected abstract void addWord(String word);
	
	/**
	 * Method to clean the automaton (minimize the automaton)
	 */
	protected abstract void cleanUp();
	
	/**
	 * Method to copy this algorithm so that threads can work in parallel
	 * @return a copy of this algorithm object
	 */
	public abstract AlgorithmSkeleton copy();
}
