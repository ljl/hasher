package no.iskald;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class WordIndexer {

    private final char SKIP_CHAR = '#';

    public WordIndexer() {

    }

    public List<String> indexFile(File wordListFile) throws IOException {
        List<String> wordList = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(wordListFile));
        String line = reader.readLine();

        while (line != null) {
            if (line.toCharArray()[0] == SKIP_CHAR) {
                line = reader.readLine();
                continue;
            }
            wordList.add(line);
            //System.out.println(line);
            line = reader.readLine();
        }
        return wordList;
    }
}
