package de.tu_bs.isf.madfa.algorithms.spl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.util.Util;

/**
 * Implementation of algorithm Semi-Incremental
 * Minimizes the successors of final states during the call of semiMin
 * @author Tobias
 *
 */
public class AlgorithmSPL_W extends AlgorithmSPL_T {

	@Override
	protected void addWord(String word) {
		Set<State> finals = new HashSet<>();
		finals.addAll(automaton.getFinalStates());
		super.addWord(word);
		
		long nanoTime = System.nanoTime();
		List<State> states = Util.pathOfState(automaton.getStartState(), word);
		State state = states.get(states.size() - 1);
		semiMin(state, finals);
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("semiMin " + word + ": " + nanoTime + "ns");
	}
	
	@Override
	protected void cleanUp() {
		long nanoTime = System.nanoTime();
		
		Set<State> finals = new HashSet<>();
		finals.addAll(automaton.getFinalStates());
		finals.remove(automaton.getStartState());
		semiMin(automaton.getStartState(), finals);
		
		nanoTime = System.nanoTime() - nanoTime;
//		Benchmark.addLineToBenchmarkFile("cleanUp_semiMin: " + nanoTime + "ns");
	}
	
	/**
	 * Helper method to merge the successors of state p
	 * @param p the start stage for the merge
	 * @param listOfStatesU the final states
	 */
	private void semiMin(State p, Set<State> listOfStatesU) {
//		long nanoTime = System.nanoTime();
//		long nanoTimeRec = 0;
		Set<State> listOfStatesV = new HashSet<>();
//		nanoTimeRec = System.nanoTime();
		for(State q : Util.successorsOfState(p)) {
			if(!listOfStatesU.contains(q)) {
				Set<State> unionList = new HashSet<>();
				unionList.addAll(listOfStatesU);
				unionList.addAll(listOfStatesV);
				semiMin(q, unionList);
			}
			listOfStatesV.add(q);
		}
//		nanoTimeRec = System.nanoTime() - nanoTimeRec;

		Set<State> allSuccessors = new HashSet<>();
		for(State tmp : listOfStatesU) {
			allSuccessors.add(tmp);
//			long nanoTimeReach = System.nanoTime();
			List<State> tmpList = Util.reachableStates(tmp);
//			nanoTimeReach = System.nanoTime() - nanoTimeReach;
//			RuntimeMap.insertInto("reachableStatesMethodCalls", 1);
//			RuntimeMap.insertInto("reachableStatesMethodRuntime", nanoTimeReach);
			allSuccessors.addAll(tmpList);
		}
		
		for (State q : allSuccessors) {
			if(automaton.getStates().contains(q)) {
				if (!q.equals(p)) {
					if(State.isEquivalenceFast(p, q)) {
						Util.merge(automaton, p, q);
						p = q;
					}
				}
			}
		}
//		nanoTime = System.nanoTime() - nanoTime - nanoTimeRec;
//		RuntimeMap.insertInto("semiMinMethodCalls", 1);
//		RuntimeMap.insertInto("semiMinMethodRuntime", nanoTime);
	}
	
	@Override
	public AlgorithmSkeleton copy() {
		return new AlgorithmSPL_W();
	}
}
