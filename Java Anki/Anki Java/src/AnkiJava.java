import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class AnkiJava {

    // private instance variables
    private static HashMap<String, ArrayList<JPWord>> deck;
    private static PriorityQueue<JPWord> wordsToStudy;  // words to be studied
    private static PriorityQueue<JPWord> doneStudied;   // words already studied
    private static int numCards;

    // class constants
    private static final boolean CASE_SENSITIVE = true;
    private static final boolean NOT_CASE_SENSITIVE = false;
    private static final int FIRST_LETTER = 0;
    private static final int STARTING_FREQ = 0;
    
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        gameMenu(keyboard);
        gameEnd();
        keyboard.close();
    }

    /**
     * Method that displays the options available to the client/user, reviewing decks, creating
     * decks, and bulk adding a deck
     * 
     * @param keyboard Scanner used for player input when selecting decks and 
     */
    private static void gameMenu(Scanner keyboard) {
        // creating default decks
        boolean auto = true;    // fml I forgot to implement the bulk add feature for the user
        deck = new HashMap<>();
        String defaultFiles = "Core2k6k.txt";
        deck = bulkAdd(keyboard, deck, auto, defaultFiles);
        defaultFiles = "Love_and_Deepspace.txt";
        deck = bulkAdd(keyboard, deck, auto, defaultFiles);
        // auto = false;

        System.out.println("Welcome to the Anki Ripoff!!");

        // FUCKING KEEP THESE IN CASE SHIT GOES DOWN WITH THE WHILE LOOP
        // displayDecks();
        // String input = playerInput(keyboard, true); // case sensitive is true
        // if (input.equals("add")) {
        //     startAdding(keyboard);
        //     // is there a way to not repeat the next two lines of code? 
        //     displayDecks();
        //     input = playerInput(keyboard, true); // case sensitive is true
        // }
        String input;
        do {
            displayDecks();
            input = playerInput(keyboard, CASE_SENSITIVE);
            // fuck, I need to catch a null pointer exception if the user types an invalid deck name
            if (input.toLowerCase().trim().equals("add")) {
                startAdding(keyboard);
            }
        } while (input.toLowerCase().trim().equals("add"));
        preGame(input);
        startMemorizing(keyboard);
    }

    /**
     * Displays the decks available to review
     */
    private static void displayDecks() {
        System.out.println("These are the decks available to study:");
        StringBuilder names = new StringBuilder();
        for (String deckName : deck.keySet()) {
            names.append(deckName);
            names.append("\n");
        }
        System.out.println(names.toString());
        System.out.println("Please select which deck to study (please type the name "
                + "of the deck as it is shown above OR type 'ADD' to create a new deck: ");
    }

    /**
     * Prints the program end screen and the number of cards reviewed
     */
    private static void gameEnd() {
        System.out.println("You finished!\nYou reviewed a total of: " + numCards + " cards!");
    }

    /**
     * Once a deck has been selected to review, the JPWord objects in the deck are added to a
     * priority queue before the game officially starts. Words are shown according to their
     * frequencies in ascending order. Words with a frequency of 0 will appear more often than
     * words with a frequency of 4
     * 
     * @param deckName the name of the deck selected
     */
    private static void preGame(String deckName) {
        wordsToStudy = new PriorityQueue<>();
        doneStudied = new PriorityQueue<>();
        ArrayList<JPWord> currentList = deck.get(deckName);
        for (JPWord word : currentList) {
            wordsToStudy.add(word);
            doneStudied.add(word);
        }
    }

    /**
     * Method that loops the program until the user decides to stop. Will continue to
     * display Japanese words to review and will increase how often a word is seen.
     * Words that aren't remembered have their frequencies reset to appear more often.
     * Words that are remembered will appear less frequently
     * @param keyboard
     */
    private static void startMemorizing(Scanner keyboard) {
        final int MAX = 4;
        String[] rightAnswer = {"Good job!", "You got it!", "Yaay!", "鋭いね！"};
        String[] wrongAnswer = {"Aww man", "It's okay, you'll get it next time", 
                "Practice makes perfect, it's okay", "弱い"};

        boolean keepMemorizing = true;
        boolean showText = false;
        numCards = 0;
        while (keepMemorizing) {
            JPWord currentWord = wordsToStudy.poll();
            System.out.println("Do you remember this word? Enter 'Y' for yes and 'N' for no, " 
                    + "and 'X' to exit: ");
            System.out.println(currentWord.getJPWord());    // show word
            String input = playerInput(keyboard, NOT_CASE_SENSITIVE);
            int phrase = (int)(Math.random() * (MAX)); // gets a num between 0 and 4 (exclusive)

            if (inputValid(input) || input.charAt(FIRST_LETTER) == 'x') {
                char letter = input.charAt(FIRST_LETTER);
                numCards++;
                System.out.println("Card number: " + numCards);
                if (letter == 'y') {
                    // they DO remember the word
                    System.out.println(rightAnswer[phrase]);
                    System.out.println("Next word!\n");
                    currentWord.incFreq();
                    doneStudied.poll();
                    if (doneStudied.isEmpty() && !showText) {  // only shows once
                        showText = true;
                        System.out.println("素晴らしいよね！ You've memorized every word in "
                                + "this deck at least once! You may now exit the prgram "
                                + "without a guilty conscience :D\n");
                    }
                } else if (letter == 'n') {
                    // they DON't remember the word
                    System.out.println(wrongAnswer[phrase]);
                    System.out.println(currentWord.toString());
                    currentWord.resetFreq();
                } else {
                    keepMemorizing = false; // this should make the loop end
                    numCards--;
                }
                wordsToStudy.add(currentWord);
            } else { // input is not valid
                System.out.println("Sorry, that's not a valid input.");
                Queue<JPWord> temp = new LinkedList<>();
                temp.add(currentWord);
                temp.addAll(wordsToStudy);
                wordsToStudy.clear();
                wordsToStudy.addAll(temp);
            }
        }
    }

    // checks if player input is valid when actually trying to memorize shit
    private static boolean inputValid(String input) {
        char letter = input.charAt(FIRST_LETTER);  // zero means first char of word
        return (letter == 'y' || letter == 'n');
    }

    /**
     * Method that takes in user input
     * 
     * @param keyboard Scanner used to get user's input
     * @param caseSensitive boolean for case sensitivity
     * @return returns the player's input based on whether case sensitivity matters or not
     */
    private static String playerInput(Scanner keyboard, boolean caseSensitive) {
        String playerInput;
        if (caseSensitive) {
            playerInput = keyboard.nextLine();
        } else {
            playerInput = keyboard.nextLine().toLowerCase().trim();
        }
        System.out.println();
        return playerInput;
    }

    /**
     * Allows user to manually add new Japanese words to a new deck (NOT an existing deck)
     * 
     * @param keyboard Scanner that takes in user input
     */
    private static void startAdding(Scanner keyboard) {
        System.out.println("What do you want to name this deck?");
        String nameOfDeck = playerInput(keyboard, CASE_SENSITIVE);
        ArrayList<JPWord> japWords = new ArrayList<>();

        boolean keepAdding = true;
        while (keepAdding) {
            addJPWord(keyboard, japWords);
            System.out.println("\nDo you want to add another word?"
                    + " Enter 'Y' for yes and 'N' for no");
            String input = playerInput(keyboard, NOT_CASE_SENSITIVE);
            if (inputValid(input)) {
                char letter = input.charAt(FIRST_LETTER);
                if (letter != 'y') {
                    keepAdding = false;
                }
            } else {
                // lmao, wrong input
                System.out.println("Sorry, that's not a valid input. Please try again.");
            }
        }
        deck.put(nameOfDeck, japWords);
    }

    /**
     * Displays text to request player input for the title of the dekc, the Japanese word they
     * want to add, the furigana (pronunciation in kana) and the English equivalent 
     * translation of said word
     * 
     * @param keyboard Scanner that takes user input
     * @param japWords Arraylist of JPWord objects that is added to
     */
    private static void addJPWord(Scanner keyboard, ArrayList<JPWord> japWords) {
        System.out.println("Please enter the Japanese word you want to memorize:");
        String word = keyboard.nextLine();

        System.out.println("\nPlease enter the furigana/pronunciation of the word");
        String furigana = keyboard.nextLine();

        System.out.println("\nPlease enter the definition/English "
                + "vocabulary of the Japanese word");
        String def = keyboard.nextLine();

        // use the fucking constructor you dumbass
        JPWord newWord = new JPWord(word, def, STARTING_FREQ, furigana);
        japWords.add(newWord);
        // return newWord;
    }

    /**
     * Scans a file created by the user that automatically adds Japanese words to a new
     * deck without prompting for the Japanese word, furigana, and English translation
     * of said word
     * 
     * @param keyboard Scanner that takes in user input
     * @param newDeck Arraylist of JPWord objects to be added to the main deck, which consists of
     *                different decks and Japanese words to review
     * @param auto boolean value used by getFileScanner() helper method. When auto is true,
     *             preset decks will be added to the main deck. When false, the player's
     *             custom deckw will be added
     * @param fileName String with the name of the file to be bulk added to the main deck
     * @return returns modified main deck containing the previously existing decks and the
     *         user's new deck
     */
    private static HashMap<String, ArrayList<JPWord>> bulkAdd(Scanner keyboard, 
                HashMap<String, ArrayList<JPWord>> newDeck, boolean auto, String fileName) {
        Scanner fileScanner = getFileScanner(keyboard, auto, fileName);
        String deckName = fileScanner.nextLine();
        System.out.println(deckName);
        ArrayList<JPWord> newJapWords = new ArrayList<>();
        while (fileScanner.hasNextLine()) {
            String japWord = fileScanner.next();
            String furigana = fileScanner.next();
            String engDef = fileScanner.nextLine();
            JPWord newWord = new JPWord(japWord, engDef, STARTING_FREQ, furigana);
            newJapWords.add(newWord);
            // System.out.println(newWord.toString());
        }
        System.out.println();
        newDeck.put(deckName, newJapWords);
        return newDeck;
    }

    /**
     * Propmts the user which file to bulk add into the main deck via local files
     * in the directory
     * 
     * @param keyboard Scanner that takes in user input
     * @param auto boolean value that determines if it is bulk adding preset decks or
     *             custom user decks
     * @param fileName String of the file's name
     * @return returns a readible file to be scanned by the bulkAdd() method
     */
    private static Scanner getFileScanner(Scanner keyboard, boolean auto, String fileName) {
        Scanner result;
        try {
            if (!auto) {
                System.out.println("Enter the file name you want to bulk add: ");
                fileName = keyboard.nextLine();
            }
            System.out.println("Reading file name: " + fileName);
            File file = new File(fileName);
            System.out.println("Absolute path: " + file.getAbsolutePath());
            if (!file.exists()) {
                System.out.println("File does not exist: " + file.getAbsolutePath());
            }
            result = new Scanner(file, "UTF-8");
        } catch (FileNotFoundException e) {
            System.out.println("\nProblem creating Scanner: " + e);
            System.out.println("Creating default scanner with default data " + e);
            String defaultData = "FileWasNotRead.txt";
            result = new Scanner(defaultData);
        }
        return result;
    }
}