
public class JPWord implements Comparable<JPWord> {
    
    // instance variables
    private String wordJP;
    private String englishDef;
    private String furigana;
    private int freq;

    // constructor
    public JPWord(String wordJP, String englishDef, int freq, String furigana) {
        this.wordJP = wordJP;
        this.englishDef = englishDef;
        this.freq = freq;
        this.furigana = furigana;
    }

    public int incFreq() {
        freq++;
        freq*=2;
        return freq;
    }
    
    public int resetFreq() {
        freq = 0;
        return freq;
    }

    public String getJPWord() {
        return wordJP;
    }

    public String getENDef() {
        return englishDef;
    }

    public String getFurigana() {
        return furigana;
    }
    
    @Override
    public int compareTo(JPWord other) {
        return Integer.compare(this.freq, other.freq); // Ascending order
    }

    @Override
    public String toString() {
        StringBuilder displayInfo = new StringBuilder("Japanese Word: ");
        displayInfo.append(wordJP);
        displayInfo.append("\nFurigana: ");
        displayInfo.append(furigana);
        displayInfo.append("\nEnglish Definition: ");
        displayInfo.append(englishDef);
        displayInfo.append("\n");
        return displayInfo.toString();
    }
}
