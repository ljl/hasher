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

public class Unhasher
{
    private Set<String> hashed;

    private Long startTime;

    private int passwordsFound = 0;

    private Logger log = Logger.getLogger( Unhasher.class.getName() );

    public static void main( String... args )
        throws URISyntaxException, IOException, NoSuchAlgorithmException
    {
        Unhasher u = new Unhasher();
        u.start();
    }

    private void start()
        throws IOException, NoSuchAlgorithmException
    {
        startTime = System.currentTimeMillis();
        WordIndexer indexer = new WordIndexer();

        log.info( "Reading wordlist" );
        File wordListFile = new File( Unhasher.class.getClassLoader().getResource( "D8.DIC" ).getFile() );
        log.info( "Reading hashed strings" );
        File hashedPasswordFile = new File( Unhasher.class.getClassLoader().getResource( "hashedPasswords" ).getFile() );
        log.info( "Indexing wordlist" );
        List<String> wordList = indexer.indexFile( wordListFile );
        log.info( "Indexing hashed strings" );
        List<String> hashedPasswordList = indexer.indexFile( hashedPasswordFile );

        log.info( "Putting hashes" );
        hashed = new HashSet<String>();
        for ( String s : hashedPasswordList )
        {
            hashed.add( s );
        }

        log.info( "Checking" );
        for ( String s : wordList )
        {
            check( s );
            checkSimilar( s );
            //caseCombiner( s );
            new Combiner( s ).run();
        }

        System.out.println( "---------------------" );
        System.out.println( "Found " + passwordsFound + " passwords out of " + hashedPasswordList.size() );
        System.out.println( "Finished in " + ( System.currentTimeMillis() - startTime ) + "ms" );
    }

    private void check( String unhashed )
        throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        String hashedString = md5( unhashed );
        boolean exists = hashed.contains( hashedString );
        if ( exists )
        {
            hashed.remove( hashedString );
            System.out.println( hashedString + ":" + unhashed );
            passwordsFound++;
        }
    }

    private void checkSimilar( String input )
        throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        for ( int i = 0; i < 100; i++ )
        {
            check( input + i );
        }
    }

    public String md5( String input )
        throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
        byte[] message = input.getBytes( "UTF-8" );
        byte[] hash = messageDigest.digest( message );
        BigInteger bigInt = new BigInteger( 1, hash );
        return bigInt.toString( 16 );
    }

    private void caseCombiner(String input)
        throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        calculateWordCombinations( input, new char[input.length()], 0 );
    }

    private void calculateWordCombinations( String input, char[] chars, int index )
        throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        if ( index == input.length() )
        {
            String variation = new String( chars );
            check( variation );
        }
        else
        {
            char c = input.charAt( index );
            if ( !Character.isDigit( c ) )
            {
                chars[index] = Character.toLowerCase( c );
                calculateWordCombinations( input, chars, index + 1 );
                chars[index] = Character.toUpperCase( c );
                calculateWordCombinations( input, chars, index + 1 );
            }
            else
            {
                chars[index] = c;
                calculateWordCombinations( input, chars, index + 1 );
            }
        }
    }

    class Combiner
        implements Runnable
    {

        private String input;

        public Combiner( String input )
        {
            this.input = input;
        }

        @Override
        public void run()
        {
            try
            {
                calculateWordCombinations( input, new char[input.length()], 0 );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        private void calculateWordCombinations( String input, char[] chars, int index )
            throws UnsupportedEncodingException, NoSuchAlgorithmException
        {
            if ( index == input.length() )
            {
                String variation = new String( chars );
                check( variation );
            }
            else
            {
                char c = input.charAt( index );
                if ( !Character.isDigit( c ) )
                {
                    chars[index] = Character.toLowerCase( c );
                    calculateWordCombinations( input, chars, index + 1 );
                    chars[index] = Character.toUpperCase( c );
                    calculateWordCombinations( input, chars, index + 1 );
                }
                else
                {
                    chars[index] = c;
                    calculateWordCombinations( input, chars, index + 1 );
                }
            }
        }
    }


}
