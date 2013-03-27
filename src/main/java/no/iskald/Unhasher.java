package no.iskald;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Logger;

public class Unhasher {
    private static Set<String> hashed;
    private static Long startTime;
    private static int passwordsFound = 0;
    private static Logger log = Logger.getLogger(Unhasher.class.getName());

    public static void main(String... args) throws URISyntaxException, IOException, NoSuchAlgorithmException {
        startTime = System.currentTimeMillis();
        WordIndexer indexer = new WordIndexer();

        //File wordListFile = new File(Unhasher.class.getClassLoader().getResource("D8.DIC").getFile());
        log.info("Reading wordlist");
        File wordListFile = new File(Unhasher.class.getClassLoader().getResource("D8.DIC").getFile());
        log.info("Reading hashed strings");
        File hashedPasswordFile = new File(Unhasher.class.getClassLoader().getResource("hashedPasswords").getFile());
        log.info("Indexing wordlist");
        List<String> wordList = indexer.indexFile(wordListFile);
        log.info("Indexing hashed strings");
        List<String> hashedPasswordList = indexer.indexFile(hashedPasswordFile);


        log.info("Putting hashes");
        hashed = new HashSet<String>();
        for (String s : hashedPasswordList) {
            hashed.add(s);
        }

        log.info("Checking");
        for (String s : wordList) {
            check(s);
            checkSimilar(s);
            wordCombinations(s);
            //lookupMap.put(md5(s), s);
            //lookupMap.putAll(generateSimilarHashes(s));
            //lookupMap.putAll(wordCombinations(s));
        }

        System.out.println("---------------------");
        System.out.println("Found " + passwordsFound + " passwords out of " + hashedPasswordList.size());
        System.out.println("Finished in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private static void check(String unhashed) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hashedString = md5(unhashed);
        boolean exists = hashed.contains(hashedString);
        if (exists) {
            hashed.remove(hashedString);
            System.out.println(hashedString + ":" + unhashed);
            passwordsFound++;
        }
    }

    private static void checkSimilar(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        for (int i = 0; i < 100; i++) {
            check(input + i);
        }
    }

    public static String md5(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] message = input.getBytes("UTF-8");
        byte[] hash = messageDigest.digest(message);
        BigInteger bigInt = new BigInteger(1, hash);
        return bigInt.toString(16);
    }

    private static void wordCombinations(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        calculateWordCombinations(input, new char[input.length()], 0);
    }

    private static void calculateWordCombinations(String input, char[] chars, int index) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (index == input.length()) {
            String variation = new String(chars);
            check(variation);
        } else {
            char c = input.charAt(index);
            if (!Character.isDigit(c)) {
            chars[index] = Character.toLowerCase(c);
            calculateWordCombinations(input, chars, index + 1);
            chars[index] = Character.toUpperCase(c);
            calculateWordCombinations(input, chars, index + 1);
            } else {
                chars[index] = c;
                calculateWordCombinations(input, chars, index + 1);
            }
        }
    }
}
