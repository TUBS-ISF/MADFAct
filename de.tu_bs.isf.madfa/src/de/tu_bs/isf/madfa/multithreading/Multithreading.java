package de.tu_bs.isf.madfa.multithreading;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.isf.madfa.algorithms.skeleton.AlgorithmSkeleton;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_D;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_N;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_S;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_W;
import de.tu_bs.isf.madfa.automaton.Automaton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;
import de.tu_bs.isf.madfa.util.Util;

public class Multithreading {
	
	private AlgorithmSkeleton algorithm;
	private int numberThreads;
	
	public Multithreading(AlgorithmSkeleton algorithm, int numberThreads) {
		this.algorithm = algorithm;
		this.numberThreads = numberThreads;
	}
	
	public Automaton createMadfa(List<String> words) {
		if (numberThreads != 2 && numberThreads != 4) {
			System.out.println("Only two and four threads are implemented.");
			System.exit(0);
		}
		Automaton mergedAutomaton = new Automaton();
		State.nextIdToZero();
		List<Automaton> intermediateAutomata = new ArrayList<>();
		List<List<String>> subLists = chopIntoParts(words, numberThreads);
		List<MadfaThread> threadList = new ArrayList<>();
		
		for (int i = 0; i < numberThreads; i++) {
			List<String> subList = subLists.get(i);
			if (algorithm instanceof AlgorithmSPL_D || algorithm instanceof AlgorithmSPL_W) {
				subList = Util.orderWordsMinLen(subList);
			} else if (algorithm instanceof AlgorithmSPL_S) {
				subList = Util.orderWords(subList);
			}
			MadfaThread thread = new MadfaThread(algorithm.copy(), intermediateAutomata, subList);
			threadList.add(thread);
			thread.start();
		}
		
		for (MadfaThread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (numberThreads == 2) {
			Automaton firstAutomaton = intermediateAutomata.get(0);
			Automaton secondAutomaton = intermediateAutomata.get(1);
			mergeAutomata(firstAutomaton, secondAutomaton, mergedAutomaton);
			minimizeMergedAutomaton(mergedAutomaton);
		} else if (numberThreads == 4) {
			Automaton firstMergedAutomaton = new Automaton();
			Automaton firstAutomaton = intermediateAutomata.get(0);
			Automaton secondAutomaton = intermediateAutomata.get(1);
			MergeThread firstMergeThread = new MergeThread(firstAutomaton, secondAutomaton, firstMergedAutomaton);
			firstMergeThread.start();
			
			Automaton secondMergedAutomaton = new Automaton();
			Automaton thirdAutomaton = intermediateAutomata.get(2);
			Automaton fourthAutomaton = intermediateAutomata.get(3);
			MergeThread secondMergeThread = new MergeThread(thirdAutomaton, fourthAutomaton, secondMergedAutomaton);
			secondMergeThread.start();
			
			try {
				firstMergeThread.join();
				secondMergeThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			mergeAutomata(firstMergedAutomaton, secondMergedAutomaton, mergedAutomaton);
			minimizeMergedAutomaton(mergedAutomaton);
		}
		
		return mergedAutomaton;
	}


	static void mergeAutomata(Automaton firstAutomaton, Automaton secondAutomaton,
			Automaton mergedAutomaton) {
		State startState = new State();
		State nextState1 = firstAutomaton.getStartState();
		State nextState2 = secondAutomaton.getStartState();
		startState.setId(nextState1.getId() + ";" + nextState2.getId());
		startState.setFinalState(nextState1.isFinalState() || nextState2.isFinalState());
		
		mergedAutomaton.addState(startState);
		mergedAutomaton.setStartState(startState);
		if (startState.isFinalState()) {
			mergedAutomaton.addFinalState(startState);
		}
		
		processAutomata(startState, nextState1, nextState2, mergedAutomaton);
	}
	
	private static void processAutomata(State mergedState, State nextState1, State nextState2, Automaton mergedAutomaton) {
		if (nextState1 != null && nextState2 != null) {
			for (Transition trans1 : nextState1.getOutgoingTransitions()) {
				Transition trans2 = getTransitionWithSameLabel(nextState2, trans1.getLabel());
				if (trans2 == null) {
					State newMergedState = copyTransitionAndState(mergedState, trans1, mergedAutomaton);
					processAutomata(newMergedState, trans1.getEndState(), null, mergedAutomaton);
				} else {
					String id = trans1.getEndState().getId() + ";" + trans2.getEndState().getId();
					State newMergedState = getStateWithSameId(id, mergedAutomaton);
					if (newMergedState == null) {
						newMergedState = new State();
						newMergedState.setId(id);
						newMergedState.setFinalState(trans1.getEndState().isFinalState() || trans2.getEndState().isFinalState());
						mergedAutomaton.addState(newMergedState);
						if (newMergedState.isFinalState()) {
							mergedAutomaton.addFinalState(newMergedState);
						}
					}
					if (mergedState.getTransition(newMergedState, trans1.getLabel()) == null) {
						Transition newTransition = new Transition(mergedState, newMergedState, trans1.getLabel());
						mergedState.addOutgoingTransition(newTransition);
						newMergedState.addIncomingTransition(newTransition);
						mergedAutomaton.addTransition(newTransition);
					}
					processAutomata(newMergedState, trans1.getEndState(), trans2.getEndState(), mergedAutomaton);
				}
			}
			for (Transition trans2 : nextState2.getOutgoingTransitions()) {
				Transition trans1 = getTransitionWithSameLabel(nextState1, trans2.getLabel());
				if (trans1 == null) {
					State newMergedState = copyTransitionAndState(mergedState, trans2, mergedAutomaton);
					processAutomata(newMergedState, null, trans2.getEndState(), mergedAutomaton);
				}
			}
		} else if (nextState1 != null) {
			for (Transition trans : nextState1.getOutgoingTransitions()) {
				State newMergedState = copyTransitionAndState(mergedState, trans, mergedAutomaton);
				processAutomata(newMergedState, trans.getEndState(), null, mergedAutomaton);
			}
		} else if (nextState2 != null) {
			for (Transition trans : nextState2.getOutgoingTransitions()) {
				State newMergedState = copyTransitionAndState(mergedState, trans, mergedAutomaton);
				processAutomata(newMergedState, null, trans.getEndState(), mergedAutomaton);
			}
		}
	}

	private static State copyTransitionAndState(State mergedState, Transition trans, Automaton mergedAutomaton) {
		String id = trans.getEndState().getId();
		State newState = getStateWithSameId(id, mergedAutomaton);
		if (newState == null) {
			newState = new State();
			newState.setId(id);
			newState.setFinalState(trans.getEndState().isFinalState());
			mergedAutomaton.addState(newState);
			if (newState.isFinalState()) {
				mergedAutomaton.addFinalState(newState);
			}
		}
		if (mergedState.getTransition(newState, trans.getLabel()) == null) {
			Transition newTransition = new Transition(mergedState, newState, trans.getLabel());
			mergedState.addOutgoingTransition(newTransition);
			newState.addIncomingTransition(newTransition);
			mergedAutomaton.addTransition(newTransition);
		}
		return newState;
	}
	
	private static State getStateWithSameId(String id, Automaton automaton) {
		for (State state : automaton.getStates()) {
			if (state.getId().equals(id)) {
				return state;
			}
		}
		return null;
	}
	
	private static Transition getTransitionWithSameLabel(State state, String label) {
		for (Transition trans : state.getOutgoingTransitions()) {
			if (trans.getLabel().equals(label)) {
				return trans;
			}
		}
		return null;
	}

	public static List<List<String>> chopIntoParts(List<String> wordList, int parts) {
		List<List<String>> listParts = new ArrayList<List<String>>();
		int partSize = wordList.size() / parts;
		int leftOver = wordList.size() % parts;
		int currentPartSize;
		
		for(int i = 0; i < wordList.size(); i += currentPartSize) {
	    	if (leftOver > 0) {
	    		leftOver--;
	            currentPartSize = partSize + 1;
	    	} else {
	    		currentPartSize = partSize;
	        }
	    	listParts.add(new ArrayList<String>(wordList.subList(i, i + currentPartSize)));
		}
		return listParts;
	}
	
	static void minimizeMergedAutomaton(Automaton mergedAutomaton) {
		AlgorithmSkeleton cleanUpAlgorithm = new AlgorithmSPL_N();
		cleanUpAlgorithm.createMadfa(new ArrayList<String>(), mergedAutomaton);
	}
}
