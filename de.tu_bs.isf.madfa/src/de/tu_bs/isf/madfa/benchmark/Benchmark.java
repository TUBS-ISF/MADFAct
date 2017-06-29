package de.tu_bs.isf.madfa.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_D;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_I;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_N;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_R;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_S;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_T;
import de.tu_bs.isf.madfa.algorithms.spl.AlgorithmSPL_W;
import de.tu_bs.isf.madfa.multithreading.Multithreading;

/**
 * Class to create word lists and create output files
 * helper methods for the benchmarking
 * @author Tobias
 *
 */
public class Benchmark {
	
	private static String benchmarkFileContent = "";
	private static List<String> benchmarkContentT = new ArrayList<>();
	private static List<String> benchmarkContentN = new ArrayList<>();
	private static List<String> benchmarkContentI = new ArrayList<>();
	private static List<String> benchmarkContentR = new ArrayList<>();
	private static List<String> benchmarkContentS = new ArrayList<>();
	private static List<String> benchmarkContentD = new ArrayList<>();
	private static List<String> benchmarkContentW = new ArrayList<>();

	/**
	 * reads the bible and creates a list of words that are contained in the bible
	 * @return list of words that are contained in the bible
	 */
	public static List<String> readBible() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Benchmark.class.getResourceAsStream("/words.txt")));
			Set<String> wordSet = new HashSet<>();
			
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				line = line.toLowerCase();
				String[] lineArray = line.split(" ");
				for (String string : lineArray) {
					wordSet.add(string);
				}
			}
			bufferedReader.close();
			List<String> wordList = new ArrayList<>();
			for (String word : wordSet) {
				wordList.add(word);
			}
			wordList.add("");
			return wordList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * reads the list of English words and creates a list of words that are contained in this list
	 * @return list of words that are contained in the word list
	 */
	public static List<String> readWordList() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Benchmark.class.getResourceAsStream("/wordsEn.txt")));
			List<String> wordList = new ArrayList<>();
			
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				wordList.add(line);
			}
			bufferedReader.close();

			return wordList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads the dna and returns a list of word with the committed length
	 * @param length the length of the words
	 * @param amount the amount of the words
	 * @return a list with the words
	 */
	public static List<String> readEcoliSameLength(int length, int amount) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Benchmark.class.getResourceAsStream("/ecoli.txt")));
			Set<String> wordSet = new HashSet<>();
			
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				line = line.toLowerCase();
				for(int i = 0; i < amount * length; i = i + length) {
					String string = line.substring(i, i + length);
					wordSet.add(string);
				}
			}
			bufferedReader.close();
			List<String> wordList = new ArrayList<>();
			for (String word : wordSet) {
				wordList.add(word);
			}
			return wordList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads the dna and returns a list of word of different length
	 * @param amount the amount of the words
	 * @return a list with the words
	 */
	public static List<String> readEcoliDifferentLength(int amount) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Benchmark.class.getResourceAsStream("/ecoli.txt")));
			Set<String> wordSet = new HashSet<>();
			
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				line = line.toLowerCase();
				int current = 0;
				for(int i = 1; i <= 32; i = i * 2) {
					for(int j = 0; j < amount; j++) {
						String string = line.substring(current, current + i);
						current = current + i;
						wordSet.add(string);
					}
				}
			}
			bufferedReader.close();
			List<String> wordList = new ArrayList<>();
			for (String word : wordSet) {
				wordList.add(word);
			}
			return wordList;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads the dna and returns a list of word with the committed length
	 * @param length the length of the words
	 * @param amount the amount of the words
	 * @return a list with the words
	 */
	public static String readEcoliToString() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Benchmark.class.getResourceAsStream("/ecoli.txt")));
			String ecoli = "";
			
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				line = line.toLowerCase();
				ecoli += line;
			}
			bufferedReader.close();
			
			return ecoli;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Adds a line to the output file
	 * @param line the line as string
	 */
	public static void addLineToBenchmarkFile(String line) {
		String lineSeparator = System.getProperty("line.separator");
		benchmarkFileContent += line + lineSeparator;
	}
	
	/**
	 * Writes the output file
	 */
	public static void writeContentToFile() {
        String dat = "benchmark.txt";
        if(checkFile(new File(dat))) {
        	System.out.println(dat + " created");
        }
        try {
			FileWriter fw = new FileWriter(dat);
			fw.write(benchmarkFileContent);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Writes the content in the list to the output file
	 */
	public static void writeListContentToFile(String file, List<String> contentList) {
        if(checkFile(new File(file))) {
        	System.out.println(file + " created");
        }
        try {
			FileWriter fw = new FileWriter(file, true);
			for(String content : contentList) {
				fw.write(content + System.getProperty("line.separator"));
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	/**
	 * Checks if the file already exists. If not the file is created.
	 * @param file the file
	 * @return boolean if the file exists
	 */
	 private static boolean checkFile(File file) {
		 if (file != null) {
			 try {
				 file.createNewFile();
			 } catch (IOException e) {
				 System.err.println("Error creating " + file.toString());
			 }
			 if (file.isFile() && file.canWrite() && file.canRead())
				 return true;
		 }
		 return false;
	 }
	 
	 /**
	  * Starts all seven algorithms for a given word list
	  * @param data the name of the data set (bible, ecoli spaghetti or ecoli varying length)
	  * @param wordList the list of words
	  * @param run the number of the run (between 1 and 30)
	  * @param ecoliLength the length of the ecoli words (for spaghetti)
	  */
	 private static void runAllAlgorithms(String data, List<String> wordList, int run, int ecoliLength) {
		 String outputLine = "";
		 int threats = 2;
		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoT;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoT;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_T algoT = new AlgorithmSPL_T();
			 Multithreading multiT = new Multithreading(algoT, threats);
			 multiT.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
			 //outputLine += t.getStates().size() + ";" + t.getTransitions().size() + ";";
		 }
		 benchmarkContentT.add(outputLine);
		 writeListContentToFile("BenchmarkT.txt", benchmarkContentT);
		 benchmarkContentT = new ArrayList<>();
		 
		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoN;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoN;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_N algoN = new AlgorithmSPL_N();
			 Multithreading multiN = new Multithreading(algoN, threats);
			 multiN.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
		 }
		 benchmarkContentN.add(outputLine);
		 writeListContentToFile("BenchmarkN.txt", benchmarkContentN);
		 benchmarkContentN = new ArrayList<>();

		 
		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoI;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoI;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_I algoI = new AlgorithmSPL_I();
			 Multithreading multiI = new Multithreading(algoI, threats);
			 multiI.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
		 }
		 benchmarkContentI.add(outputLine);
		 writeListContentToFile("BenchmarkI.txt", benchmarkContentI);
		 benchmarkContentI = new ArrayList<>();

		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoR;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoR;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_R algoR = new AlgorithmSPL_R();
			 Multithreading multiR = new Multithreading(algoR, threats);
			 multiR.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
		 }
		 benchmarkContentR.add(outputLine);
		 writeListContentToFile("BenchmarkR.txt", benchmarkContentR);
		 benchmarkContentR = new ArrayList<>();

		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoS;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoS;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
//		 wordList = Util.orderWords(wordList);
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_S algoS = new AlgorithmSPL_S();
			 Multithreading multiS = new Multithreading(algoS, threats);
			 multiS.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
		 }
		 benchmarkContentS.add(outputLine);
		 writeListContentToFile("BenchmarkS.txt", benchmarkContentS);
		 benchmarkContentS = new ArrayList<>();

		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoD;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoD;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
//		 wordList = Util.orderWordsMinLen(wordList);
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_D algoD = new AlgorithmSPL_D();
			 Multithreading multiD = new Multithreading(algoD, threats);
			 multiD.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
		 }
		 benchmarkContentD.add(outputLine);
		 writeListContentToFile("BenchmarkD.txt", benchmarkContentD);
		 benchmarkContentD = new ArrayList<>();

		 if(ecoliLength == 0) {
			 outputLine = data + ";AlgoW;" + wordList.size() + ";" + run + ";";
		 } else {
			 outputLine = data + ";AlgoW;" + wordList.size() + ";" + ecoliLength + ";" + run + ";";
		 }
		 for(int l = 0; l < 5; l++) {
			 long nanoTime = System.nanoTime();
			 AlgorithmSPL_W algoW = new AlgorithmSPL_W();
			 Multithreading multiW = new Multithreading(algoW, threats);
			 multiW.createMadfa(wordList);
			 nanoTime = System.nanoTime() - nanoTime;
			 outputLine += nanoTime + ";";
		 }
		 benchmarkContentW.add(outputLine);
		 writeListContentToFile("BenchmarkW.txt", benchmarkContentW);
		 benchmarkContentW = new ArrayList<>();
	 }
	 
	 /**
	  * Starts the benchmark for the bible and ecoli
	  * Iterates over different sizes of word sets
	  */
	 public static void benchmark() {
		 Random random = new Random(42);
		 List<String> bibleWords = readBible(); //size 12602
		 String ecoli = readEcoliToString(); //length 4638690
		 List<String> wordListEN = readWordList(); //size 109583
		 String data = "";
		 Set<String> wordSet = new HashSet<>();
		 List<String> wordList = new ArrayList<>();

		 //bible
		 for(int i = 4; i < 8200; i = i * 2) { //size of set
			 for(int j = 1; j <= 30; j++) { //run 1-30 different sets
				 while(wordSet.size() < i) {
					 wordSet.add(bibleWords.get(random.nextInt(12602)));
				 }
				 for(String tmpWord : wordSet) {
					 wordList.add(tmpWord);
				 }
				 data = "bible";
				 runAllAlgorithms(data, wordList, j, 0);
				 
				 wordSet = new HashSet<>();
				 wordList = new ArrayList<>();
			 }
		 }
		 
		 //ecoli spaghetti
		 for(int i = 4; i < 1050; i = i * 2) { //size of set
			 for(int l = 1; l < 66; l = l * 2) { //length of ecoli string
				 for(int j = 1; j <= 30; j++) { //run 1-30 different sets
					 while(wordSet.size() < i) {
						 int start = random.nextInt(4600000);
						 wordSet.add(ecoli.substring(start, start + l));
						 if(wordSet.size() == Math.pow(4, l)) {
							 break;
						 }
					 }
					 for(String tmpWord : wordSet) {
						 wordList.add(tmpWord);
					 }
					 data = "EcoliSpaghetti";
					 runAllAlgorithms(data, wordList, j, l);
					 
					 wordSet = new HashSet<>();
					 wordList = new ArrayList<>();
				 }
			 }
		 }
		 
		//ecoli varying length
		 for(int i = 4; i < 1050; i = i * 2) { //size of set
			 for(int j = 1; j <= 30; j++) { //run 1-30 different sets
				 while(wordSet.size() < i) {
					 int start = random.nextInt(4600000);
					 int length = random.nextInt(7);
					 wordSet.add(ecoli.substring(start, start + (int) Math.pow(2, length)));
				 }
				 for(String tmpWord : wordSet) {
					 wordList.add(tmpWord);
				 }
				 data = "EcoliVaryingLength";
				 runAllAlgorithms(data, wordList, j, 0);
				 
				 wordSet = new HashSet<>();
				 wordList = new ArrayList<>();
			 }
		 }
		 
		//wordListEN
		 for(int i = 4; i < 109584; i = i * 2) { //size of set
			 for(int j = 1; j <= 30; j++) { //run 1-30 different sets
				 while(wordSet.size() < i) {
					 wordSet.add(wordListEN.get(random.nextInt(109583)));
				 }
				 for(String tmpWord : wordSet) {
					 wordList.add(tmpWord);
				 }
				 data = "wordsEN";
				 runAllAlgorithms(data, wordList, j, 0);
				 
				 wordSet = new HashSet<>();
				 wordList = new ArrayList<>();
			 }
		 }
	 }
}
