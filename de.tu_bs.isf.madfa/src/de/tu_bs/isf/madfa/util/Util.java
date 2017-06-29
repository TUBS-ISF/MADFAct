package de.tu_bs.isf.madfa.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tu_bs.isf.madfa.automaton.Automaton;
import de.tu_bs.isf.madfa.automaton.State;
import de.tu_bs.isf.madfa.automaton.Transition;

/**
 * Class for methods that are used by all algorithms
 * Helper methods for the algorithms
 * String and automaton manipulation
 * Tests for minimality
 * @author Tobias
 *
 */
public class Util {

	/**
	 * Checks if a end state from the state exists with a transitions with the delivered label
	 * @param state the start state
	 * @param label the label of the transition
	 * @return boolean if a end state exists
	 */
	public static boolean stateExist(State state, String label) {
		for (Transition transition : state.getOutgoingTransitions()) {
			if (transition.getLabel().equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the end state from the state
	 * @param state the start state
	 * @param label the label of the transition
	 * @return the end state if it exists
	 */
	public static State getNextState(State state, String label) {
		for (Transition transition : state.getOutgoingTransitions()) {
			if (transition.getLabel().equals(label)) {
				return transition.getEndState();
			}
		}
		return null;
	}
	
	/**
	 * Gets the last state that is reachable by following the transitions for the word
	 * @param state the start state
	 * @param word the word
	 * @return the last reachable state
	 */
	public static State getLastNextState(State state, String word) {
		List<State> states = Util.pathOfState(state, word);
		State searchedState = states.get(states.size() - 1);
		return searchedState;
	}
	
	/**
	 * Returns the right language of the state (all accepted words from this state)
	 * @param state the state
	 * @return the right language
	 */
	public static List<String> rightLanguageOfState(State state) {
//		long nanoTime = System.nanoTime();
		List<String> rightLanguage = new ArrayList<>();
		for (Transition transition : state.getOutgoingTransitions()) {
			String word = transition.getLabel();
			if (transition.getEndState().isFinalState()) {
				rightLanguage.add(word);
			}
			rightLanguageOfState(rightLanguage, word, transition.getEndState());
		}
		if (state.isFinalState()) {
			rightLanguage.add("");
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("rightLanguageMethodCalls", 1);
//		RuntimeMap.insertInto("rightLanguageMethodRuntime", nanoTime);
		return rightLanguage;
	}
	
	/**
	 * Recursive helper method to collect the right language
	 * @param rightLanguage the intermediate right language
	 * @param word the intermediate word
	 * @param intermediateState the intermediate state
	 */
	private static void rightLanguageOfState(List<String> rightLanguage, String word, State intermediateState) {
		for (Transition transition : intermediateState.getOutgoingTransitions()) {
			String newWord = word.toString();
			newWord += transition.getLabel();
			if (transition.getEndState().isFinalState()) {
				rightLanguage.add(newWord);
			}
			rightLanguageOfState(rightLanguage, newWord, transition.getEndState());
		}
	}
	
	/**
	 * Returns the left language of the state (all words from the start state to the deliverd state)
	 * @param automaton the automaton (to get the start state)
	 * @param state the end state
	 * @return the left language of the state
	 */
	public static List<String> leftLanguageOfState(Automaton automaton, State state) {
//		long nanoTime = System.nanoTime();
		List<String> leftLanguage = new ArrayList<>();
		for (Transition transition : state.getIncomingTransitions()) {
			String word = transition.getLabel();
			if (transition.getStartState().equals(automaton.getStartState())) {
				leftLanguage.add(word);
			}
			leftLanguageOfState(automaton, leftLanguage, word, transition.getStartState());
		}
		if (state.equals(automaton.getStartState())) {
			leftLanguage.add("");
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("leftLanguageMethodCalls", 1);
//		RuntimeMap.insertInto("leftLanguageMethodRuntime", nanoTime);
		return leftLanguage;
	}
	
	/**
	 * Recursive helper method to collect the left language
	 * @param leftLanguage the intermediate left language
	 * @param word the intermediate word
	 * @param state the end state
	 * @param intermediateState the intermediate state
	 */
	private static void leftLanguageOfState(Automaton automaton, List<String> leftLanguage, String word, State intermediateState) {
		for (Transition transition : intermediateState.getIncomingTransitions()) {
			String newWord = word.toString();
			newWord += transition.getLabel();
			if (transition.getStartState().equals(automaton.getStartState())) {
				String addWord = reverse(newWord);
				leftLanguage.add(addWord);
			}
			leftLanguageOfState(automaton, leftLanguage, newWord, transition.getStartState());
		}
	}
	
	/**
	 * Creates a list with all states that are on the path of the state with the word
	 * @param state the start state
	 * @param word the word
	 * @return a list with all states on the path
	 */
	public static List<State> pathOfState(State state, String word) {
//		long nanoTime = System.nanoTime();
		List<State> path = new ArrayList<>();
		path.add(state);
		for (Transition transition : state.getOutgoingTransitions()) {
			if (!word.isEmpty() && transition.getLabel().equals(Util.head(word))) {
				path.add(transition.getEndState());
				pathOfState(path, Util.tail(word), transition.getEndState());
			}
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("pathOfStateMethodCalls", 1);
//		RuntimeMap.insertInto("pathOfStateMethodRuntime", nanoTime);
		return path;
	}

	/**
	 * Recursive helper method to collect the states on the path
	 * @param path the intermediate path
	 * @param word the word
	 * @param state the intermediate state
	 */
	private static void pathOfState(List<State> path, String word, State state) {
		for (Transition transition : state.getOutgoingTransitions()) {
			if (!word.isEmpty() && transition.getLabel().equals(Util.head(word))) {
				path.add(transition.getEndState());
				pathOfState(path, Util.tail(word), transition.getEndState());
			}
		}
	}
	
	/**
	 * Creates a list with all reachable states of the start state
	 * @param state the start state
	 * @return a list with all reachable states of the start state
	 */
	public static List<State> reachableStates(State state) {
		List<State> reachable = new ArrayList<>();
		reachable.add(state);
		for (Transition transition : state.getOutgoingTransitions()) {
			reachable.addAll(reachableStates(transition.getEndState()));
		}
		return reachable;
	}

	/**
	 * Checks if the two states share a transition
	 * @param start the start state
	 * @param end the end state
	 * @return boolean if the end state is a successor of the start state
	 */
	public static boolean successorExists(State start, State end) {
		List<State> reachableStates = reachableStates(start);
			return reachableStates.contains(end);
	}
	
	/**
	 * Returns all successors of the start state
	 * @param state the start state
	 * @return a list with all successors of the start state
	 */
	public static Set<State> successorsOfState(State state) {
		Set<State> reachable = new HashSet<>();
		for (Transition transition : state.getOutgoingTransitions()) {
			reachable.add(transition.getEndState());
		}
		return reachable;
	}
	
	/**
	 * Returns the length of the longest word in the right language of the state
	 * @param state the state
	 * @return integer with the length of the longest word
	 */
	public static int longestRightWordLength(State state) {
		List<String> rightLanguage = rightLanguageOfState(state);
		int length = 0;
		for (String word : rightLanguage) {
			if (word.length() > length) {
				length = word.length();
			}
		}
		return length;
	}
	
	/**
	 * Collects all states that have the same height level
	 * @param automaton the automaton
	 * @param height the height level 
	 * @return all states that have the delivered height level
	 */
	public static List<State> heightLevel(Automaton automaton, int height) {
		List<State> states = new ArrayList<State>();
		for (State state : automaton.getStates()) {
			if (longestRightWordLength(state) == height) {
				states.add(state);
			}
		}
		return states;
	}
		
	/**
	 * Returns the length of the shortest word in the left language of the state
	 * @param automaton the automaton
	 * @param state the state
	 * @return integer with the length of the shortest word
	 */
	public static int shortestLeftWordLength(Automaton automaton, State state) {
		List<String> leftLanguage = leftLanguageOfState(automaton, state);
		int length = Integer.MAX_VALUE;
		for (String word : leftLanguage) {
			if (word.length() < length) {
				length = word.length();
			}
		}
		return length;
	}
	
	/**
	 * Collects all states that have the same depth level
	 * @param automaton the automaton
	 * @param depth the depth level 
	 * @return all states that have the delivered depth level
	 */
	public static List<State> depthLevel(Automaton automaton, int depth) {
		List<State> states = new ArrayList<>();
		for (State state : automaton.getStates()) {
			if (shortestLeftWordLength(automaton, state) == depth) {
				states.add(state);
			}
		}
		return states;
	}
	
	/**
	 * Collects all states that have a depth level between i and j
	 * @param automaton
	 * @param i the lower integer (not included)
	 * @param j the higher integer (included)
	 * @return all states that have a depth level between i and j
	 */
	public static List<State> depthLevelBetweenIJ(Automaton automaton, int i, int j) {
		List<State> states = new ArrayList<>();
		for (State state : automaton.getStates()) {
			int shortestLeftWordLength = shortestLeftWordLength(automaton, state);
			if (shortestLeftWordLength > i && shortestLeftWordLength <= j) {
				states.add(state);
			}
		}
		return states;
	}
	
	/**
	 * Collects all states that have a depth level bigger as j
	 * @param automaton the automaton
	 * @param j the lower bound (not included)
	 * @return all states that have a depth level bigger as j
	 */
	public static List<State> depthLevelBiggerJ(Automaton automaton, int j) {
		List<State> states = new ArrayList<>();
		for (State state : automaton.getStates()) {
			if (shortestLeftWordLength(automaton, state) > j) {
				states.add(state);
			}
		}
		return states;
	}
	
	/**
	 * Identifies the length of the shortest word that is accepted
	 * @param automaton the automaton
	 * @return integer with the length of the shortest accepted word
	 */
	public static int minWordLengthOfDFA(Automaton automaton) {
		for (int i = 0; i <= automaton.getStates().size(); i++) {
			for (State state : automaton.getStates()) {
				if (shortestLeftWordLength(automaton, state) == i) {
					if (state.isFinalState()) {
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	/**
	 * Returns the lexicographic biggest accepted word
	 * @param automaton the automaton
	 * @return the lexicographic biggest accepted word
	 */
	public static String lexGreatestWord(Automaton automaton) {
		String word = "";
		String greatestLabel = "a";
		Transition takeTransition = null;
		for (Transition transition : automaton.getStartState().getOutgoingTransitions()) {
			if (transition.getLabel().compareTo(greatestLabel) >= 0) {
				greatestLabel = transition.getLabel();
				takeTransition = transition;
			}
		}
		if (takeTransition != null) {
			word += greatestLabel;
			word = lexGreatestWord(takeTransition.getEndState(), word);
		}
		return word;
	}
	
	/**
	 * Helper Method to find the the lexicographic biggest word
	 * @param state a intermediate state
	 * @param word the intermediate word
	 */
	private static String lexGreatestWord(State state, String word) {
		String greatestLabel = "a";
		Transition takeTransition = null;
		for (Transition transition : state.getOutgoingTransitions()) {
			if (transition.getLabel().compareTo(greatestLabel) >= 0) {
				greatestLabel = transition.getLabel();
				takeTransition = transition;
			}
		}
		if (takeTransition != null) {
			word += greatestLabel;
			word = lexGreatestWord(takeTransition.getEndState(), word);
		}
		
		return word;
	}
	
	/**
	 * Merges the to states
	 * The deleted state is merged into the target state
	 * @param automaton the automaton
	 * @param deleted the state that is going to be deleted
	 * @param target the target state
	 */
	public static void merge(Automaton automaton, State deleted, State target) {
//		long nanoTime = System.nanoTime();
		if (automaton.getStates().contains(deleted) && automaton.getStates().contains(target)) {
			for (Transition transition : deleted.getIncomingTransitions()) {
				transition.setEndState(target);
				target.addIncomingTransition(transition);
			}
			for (Transition transition : deleted.getOutgoingTransitions()) {
				transition.getEndState().getIncomingTransitions().remove(transition);
				automaton.getTransitions().remove(transition);
			}
			deleted.getIncomingTransitions().clear();
			deleted.getOutgoingTransitions().clear();
			automaton.getStates().remove(deleted);
			if (deleted.isFinalState()) {
				automaton.getFinalStates().remove(deleted);
			}
		}
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("mergeMethodCalls", 1);
//		RuntimeMap.insertInto("mergeMethodRuntime", nanoTime);
	}
	
	/**
	 * Orders the words lexicographic ascending
	 * @param words the words that should be ordered
	 */
	public static List<String> orderWords(List<String> words) {
		List<String> returnList = new ArrayList<>();
		for(String word : words) {
			returnList.add(word);
		}
		Collections.sort(returnList);
		return returnList;
	}
	
	/**
	 * Orders the words by decreasing length
	 * @param words the words that should be ordered
	 * @return a list with the ordered words
	 */
	public static List<String> orderWordsMinLen(List<String> words) {
		List<String> returnList = new ArrayList<>();
		int longestWordLength = 0;
		for(String word : words) {
			if(word.length() > longestWordLength) {
				longestWordLength = word.length();
			}
		}
		for(int i = longestWordLength; i >= 0; i--) {
			for (String word : words) {
				if(word.length() == i) {
					returnList.add(word);
				}
			}
		}
		return returnList;
	}
	
	/**
	 * Checks if the states in the list are inequivalent
	 * @param states a list with the states
	 * @return boolean if the states in the list are inequivalent
	 */
	public static boolean inequivalentStates(Collection<State> states) {
		for (State firstState : states) {
			for (State secondState : states) {
				if (!firstState.equals(secondState)) {
					if(State.isEquivalenceFast(firstState, secondState)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if the states in the list are pairwise inequivalent
	 * @param firstStates a list with the states
	 * @param secondStates a list with the states
	 * @return boolean if the states in the lists are pairwise inequivalent
	 */
	public static boolean pairwiseInequivalentStates(List<State> firstStates, List<State> secondStates) {
		for (State firstState : firstStates) {
			for (State secondState : secondStates) {
				if (!firstState.equals(secondState)) {
					if(State.isEquivalenceFast(firstState, secondState)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks if the states in the set are confluence free
	 * @param states the set of states
	 * @return boolean if the states are confluence free
	 */
	public static boolean statesAreConfluenceFree(Set<State> states) {
		for (State state : states) {
			if(state.isConfluence()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the states in the list are confluence free
	 * @param states the list of states
	 * @return boolean if the states are confluence free
	 */
	public static boolean statesAreConfluenceFree(List<State> states) {
		for (State state : states) {
			if(state.isConfluence()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the set and the list contain the same words
	 * @param firstWords a set of words
	 * @param secondWords a list of words
	 * @return boolean if they contain the same words
	 */
	public static boolean wordSetsAreEquivalence(Collection<String> firstWords, Collection<String> secondWords) {
		if (firstWords.size() == secondWords.size()) {
			for (String word : firstWords) {
				boolean result = false;
				for (String word2 : secondWords) {
					if (word.equals(word2)) {
						result = true;
					}
				}
				if (result == false) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * clones the states (all outgoing transitions are cloned too)
	 * @param automaton the automaton
	 * @param state the state that should be cloned
	 * @return a clone of the state
	 */
	public static State cloneState(Automaton automaton, State state) {
//		long nanoTime = System.nanoTime();
		State clone = new State();
		State.nextIdUp();
		for (Transition transition : state.getOutgoingTransitions()) {
			Transition transitionClone = new Transition(clone, transition.getEndState(), transition.getLabel());
			clone.addOutgoingTransition(transitionClone);
			transition.getEndState().addIncomingTransition(transitionClone);
			automaton.addTransition(transitionClone);
		}
		clone.setFinalState(state.isFinalState());
//		nanoTime = System.nanoTime() - nanoTime;
//		RuntimeMap.insertInto("cloneStateMethodCalls", 1);
//		RuntimeMap.insertInto("cloneStateMethodRuntime", nanoTime);
		return clone;
	}
	
	/**
	 * Method to return the common prefix of both words
	 * @param first the first word
	 * @param second the second word
	 * @return the common prefix of both words
	 */
	public static String longestCommonPrefix(String first, String second) {
		String prefix = "";
		
		int max;
		if (first.length() > second.length()) {
			max = second.length();
		} else {
			max = first.length();
		}
		
		for (int i = 0; i < max; i++) {
			if (first.charAt(i) == second.charAt(i)) {
				prefix += second.charAt(i);
			} else {
				break;
			}
		}
		return prefix;
	}
	
	/**
	 * Method to return second word without the common prefix of both words
	 * @param first the first word
	 * @param second the second word
	 * @return  second word without the common prefix of both words
	 */
	public static String leftDerivate(String first, String second) {
		String prefix = longestCommonPrefix(first, second);
		String returnList = second.substring(prefix.length(), second.length());
		return returnList;
	}
	
	/**
	 * Method to reverse the word
	 */
	public static String reverse(String string) {
		char[] tmpArray = string.toCharArray();
		int left = 0;
		int right = tmpArray.length - 1;
		for(left = 0; left < right; left++, right--) {
			char tmp = tmpArray[left];
			tmpArray[left] = tmpArray[right];
			tmpArray[right] = tmp;
		}
		String newString = "";
		for(char c : tmpArray) {
			newString += c;
		}
		return newString;
	}
	
	/**
	 * Returns the first letter of the word
	 * @return the first letter of the word
	 */
	public static String head(String word) {
		if(word.length() >= 1) {
			return word.substring(0, 1);
		} else {
			return "";
		}
		
	}
	
	/**
	 * returns the word without the first letter
	 * @return the word without the first letter
	 */
	public static String tail(String word) {
		String returnWord = "";
		if(word.length() >= 1) {
			returnWord = word.substring(1, word.length());
		} else {
			returnWord = "";
		}
		return returnWord;
	}
}
