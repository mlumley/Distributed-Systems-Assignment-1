
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Represents the data structure to store the dictionary. Uses a
 * ConcurrentHashMap where the key is a String and the value is an ArrayList of
 * Strings.
 */
public class Dictionary {

	private Gson gson = new Gson();
	private ConcurrentHashMap<String, ArrayList<String>> dictionary = null;

	/**
	 * Creates a Dictionary object and loads a JSON file to populate the dictionary
	 * @param fileName Path to the dictionary file
	 */
	public Dictionary(String fileName) {
		this.dictionary = this.loadDictionary(fileName);
	}

	/**
	 * Check if the dictionary contains a word
	 * @param word The word to lookup
	 * @return If the word is in the dictionary
	 */
	public boolean hasWord(String word) {
		return this.dictionary.containsKey(word);
	}

	/**
	 * Add a word and definitions to the dictionary
	 * @param word The word to add
	 * @param definition The definitions to add
	 */
	public void addWord(String word, ArrayList<String> definition) {
		this.dictionary.put(word, definition);
	}

	/**
	 * Delete a word from the dictionary
	 * @param word The word to delete
	 */
	public void deleteWord(String word) {
		this.dictionary.remove(word);
	}

	/**
	 * Get the definitions for a word
	 * @param word The word to search
	 * @return The definitions
	 */
	public ArrayList<String> getDefinitions(String word) {
		return this.dictionary.get(word);
	}

	/**
	 * Open the dictionary file
	 * @param fileName The path to the file
	 * @return A FileReader for the file
	 */
	private FileReader readDictionaryFile(String fileName) {
		FileReader dictionaryFile = null;

		try {
			dictionaryFile = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Could not find file" + fileName);
			System.exit(0);
		}

		return dictionaryFile;
	}

	/**
	 * Load the contents of the dictionary file into the ConcurrentHashMap
	 * @param fileName The path to the file
	 * @return The ConcurrentHashMap with the loaded contents of the file
	 */
	private ConcurrentHashMap<String, ArrayList<String>> loadDictionary(String fileName) {
		Type dictionaryType = new TypeToken<ConcurrentHashMap<String, ArrayList<String>>>() {
		}.getType();
		ConcurrentHashMap<String, ArrayList<String>> dictionary = gson.fromJson(this.readDictionaryFile(fileName), dictionaryType);

		return dictionary;
	}

}
