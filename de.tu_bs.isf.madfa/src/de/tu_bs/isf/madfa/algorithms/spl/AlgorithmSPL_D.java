package de.tu_bs.isf.madfa.algorithms.spl;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of algorithm Depth-Layered
 * Minimization of the MADFA per depth level
 * @author Tobias
 *
 */
public class AlgorithmSPL_D extends AlgorithmSPL_T{

	private int minLength = -1;
	
	@Override
	protected void addWord(String word) {
		super.addWord(word);
		long nanoTime = System.nanoTime();
		if(word.length() != minLength) {
			depthsMin(word.length(), minLength);
		}
		
		minLength =  word.length();
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("depthsMin " + word + ": " + nanoTime + "ns");
	}
	
	@Override
	protected void cleanUp() {
		long nanoTime = System.nanoTime();
		depthsMin(0, minLength);
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("cleanUp_depthsMin : " + nanoTime + "ns");
	}
	
	/**
	 * Helper method to create sets of words and then merge them
	 * @param i the smaller boarder for the depths min search
	 * @param j the bigger boarder for the depths min search
	 */
	private void depthsMin(int i, int j) {
//		long nanoTime = System.nanoTime();
		List<State> depthLevelIJ = new ArrayList<>();
		List<State> depthLevelBiggerJ = new ArrayList<>();
		depthLevelIJ = Util.depthLevelBetweenIJ(automaton, i, j);
		depthLevelBiggerJ = Util.depthLevelBiggerJ(automaton, j);
		
		for (State p : depthLevelIJ) {
			if(automaton.getStates().contains(p)) {
				for(State q : depthLevelIJ) {
					if(automaton.getStates().contains(q)) {
						if (!q.equals(p)) {
							if(State.isEquivalence(p, q)) {
								Util.merge(automaton, q, p);
							}
						}
					}
				}
			}
		}
		
		for (State p : depthLevelBiggerJ) {
			if(automaton.getStates().contains(p)) {
				for(State q : depthLevelIJ) {
					if(automaton.getStates().contains(q)) {
						if (!q.equals(p)) {
							if(State.isEquivalence(p, q)) {
								Util.merge(automaton, q, p);
							}
						}
					}
				}
			}
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("depthsMinMethodCalls", 1);
//		RuntimeMap.insertInto("depthsMinMethodRuntime", nanoTime);
	}
	
	@Override
	public AlgorithmSkeleton copy() {
		return new AlgorithmSPL_D();
	}
}
