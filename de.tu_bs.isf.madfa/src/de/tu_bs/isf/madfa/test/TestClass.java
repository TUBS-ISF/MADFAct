package de.tu_bs.isf.madfa.test;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_T;
import de.tu_bs.isf.madfa.automaton.Automaton;
import de.tu_bs.isf.madfa.benchmark.Benchmark;
import de.tu_bs.isf.madfa.multithreading.Multithreading;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Class to test the program
 * @author Tobias
 *
 */
public class TestClass {

	/**
	 * main method
	 * @param args not used
	 */
	public static void main(String[] args) {
//		Benchmark.benchmark(); benchmark run, will take some weeks
		
		AlgorithmSkeleton algo = new AlgorithmSPL_T(); //choose algorithm T,N,I,R,S,D,W
		Multithreading multi = new Multithreading(algo, 4);
		
		
		List<String> words = new ArrayList<>();
		words = Benchmark.readBible();
//		words = Benchmark.readWordList();
//		words = Benchmark.readEcoliSameLength(64, 128);
//		words = Benchmark.readEcoliDifferentLength(256);
//		words = Util.orderWords(words); //needed for Algorithm S
//		words = Util.orderWordsMinLen(words); //needed for Algorithm D and W

		
		long timeNano = System.nanoTime();
		
		Automaton aut = algo.createMadfa(words); //standard execution
//		Automaton aut = multi.createMadfa(words); //multithreaded execution
		
		System.out.println((System.nanoTime() - timeNano) / 1000000 + " ms");
		
		List<String> acceptedWords = Util.rightLanguageOfState(aut.getStartState());
		
		System.out.println("Size of input words: " + words.size());
		System.out.println("States of automaton: " + aut.getStates().size());
		System.out.println("Final states of automaton: " + aut.getFinalStates().size());
		System.out.println("Transitions of automaton: " + aut.getTransitions().size());
		System.out.println("Size of accepted words: " + acceptedWords.size());
		System.out.println("Accepted word equivalent?: " + Util.wordSetsAreEquivalence(acceptedWords, words));
		System.out.println("Is minimal?: " + Util.inequivalentStates(aut.getStates()));
		
		System.out.println("end");
	}

}
