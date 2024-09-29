package org.cis1200;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for MarkovChain */
public class MarkovChainTest {

    /*
     * Writing tests for Markov Chain can be a little tricky.
     * We provide a few tests below to help you out, but you still need
     * to write your own.
     */

    /**
     * Helper function to make it easier to create singleton sets of Strings;
     * use this function in your tests as needed.
     *
     * @param s - the String to add to the set
     * @return - a Set containing just s
     */
    private static Set<String> singleton(String s) {
        Set<String> set = new TreeSet<>();
        set.add(s);
        return set;
    }

    /* **** ****** ***** ***** EXAMPLE TWEETS ***** ***** ****** **** */

    /*
     * Test your MarkovChain implementation!
     * Run this test case and check the printed results to see whether
     * your MarkovChain training agrees with the output below.
     *
     * ILLUSTRATIVE EXAMPLE MARKOV CHAIN:
     * startWords: { "a":2 }
     * bigramFrequencies:
     * "!": { "and":1 }
     * "?": { "<END>":1 }
     * "a": { "banana":2 "chair":1 "table":1 }
     * "and": { "a":2 }
     * "banana": { "!":1 "?":1 }
     * "chair": { "<END>":1 }
     * "table": { "and":1 }
     *
     * Add additional code to the test case to completely characterize the state of
     * the
     * MarkovChain.
     */
    @Test
    public void testIllustrativeExampleMarkovChain() {
        /*
         * Note: we provide the pre-parsed sequence of tokens. See the
         * corresonding test cases in TweetParserTest
         */
        String[] tweet1 = { "a", "table", "and", "a", "chair" };
        String[] tweet2 = { "a", "banana", "!", "and", "a", "banana", "?" };

        MarkovChain mc = new MarkovChain();
        mc.addSequence(Arrays.stream(tweet1).iterator());
        mc.addSequence(Arrays.stream(tweet2).iterator());

        // Print out the Markov chain
        System.out.println("ILLUSTRATIVE EXAMPLE MARKOV CHAIN:\n" + mc.toString());

        ProbabilityDistribution<String> pdBang = mc.get("!");
        assertEquals(singleton("and"), pdBang.keySet());
        assertEquals(1, pdBang.count("and"));

        ProbabilityDistribution<String> pdQuestion = mc.get("?");
        assertEquals(singleton(MarkovChain.END_TOKEN), pdQuestion.keySet());
        assertEquals(1, pdQuestion.count(MarkovChain.END_TOKEN));

        assertEquals(2, mc.startTokens.getTotal());
        assertEquals(2, mc.startTokens.count("a"));
        ProbabilityDistribution<String> pdA = mc.get("a");
        Set<String> keysA = new TreeSet<>();
        keysA.add("banana");
        keysA.add("chair");
        keysA.add("table");
        assertEquals(keysA, pdA.keySet());
        assertEquals(2, pdA.count("banana"));
        assertEquals(1, pdA.count("chair"));
        assertEquals(1, pdA.count("table"));

        ProbabilityDistribution<String> pdTable = mc.get("table");
        assertEquals(singleton("and"), pdTable.keySet());
        assertEquals(1, pdTable.count("and"));

        ProbabilityDistribution<String> pdAnd = mc.get("and");
        assertEquals(singleton("a"), pdAnd.keySet());
        assertEquals(2, pdAnd.count("a"));

        ProbabilityDistribution<String> pdBanana = mc.get("banana");
        Set<String> bananaKeys = new TreeSet<>();
        bananaKeys.add("!");
        bananaKeys.add("?");
        assertEquals(bananaKeys, pdBanana.keySet());
        assertEquals(1, pdBanana.count("!"));
        assertEquals(1, pdBanana.count("?"));

        ProbabilityDistribution<String> pdChair = mc.get("chair");
        assertEquals(singleton(MarkovChain.END_TOKEN), pdChair.keySet());
        assertEquals(1, pdChair.count(MarkovChain.END_TOKEN));
    }

    /* **** ****** **** **** ADD BIGRAMS TESTS **** **** ****** **** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void testAddBigram() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("1", "2");
        assertTrue(mc.bigramFrequencies.containsKey("1"));
        ProbabilityDistribution<String> pd = mc.bigramFrequencies.get("1");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(1, pd.count("2"));
    }

    @Test
    public void testAddBigramWithSpecialCharacters() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("@user", "#hashtag");
        assertTrue(mc.bigramFrequencies.containsKey("@user"));
        ProbabilityDistribution<String> pd = mc.bigramFrequencies.get("@user");
        assertTrue(pd.getRecords().containsKey("#hashtag"));
        assertEquals(1, pd.count("#hashtag"));
    }

    @Test
    public void testAddBigramIncrementExisting() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("hello", "world");
        mc.addBigram("hello", "world"); // Add the same bigram to increment count
        ProbabilityDistribution<String> pd = mc.bigramFrequencies.get("hello");
        assertEquals(2, pd.count("world"));
    }

    @Test
    public void testAddBigramsWithMultipleOccurrences() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("repeat", "word");
        mc.addBigram("repeat", "word");
        mc.addBigram("repeat", "word");
        assertEquals(3, mc.bigramFrequencies.get("repeat").count("word"));
    }

    @Test
    public void testAddBigramsWithDifferentSuccessors() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("start", "option1");
        mc.addBigram("start", "option2");
        assertEquals(1, mc.bigramFrequencies.get("start").count("option1"));
        assertEquals(1, mc.bigramFrequencies.get("start").count("option2"));
    }

    @Test
    public void testAddBigramsWithNull() {
        MarkovChain mc = new MarkovChain();
        assertThrows(IllegalArgumentException.class, () -> mc.addBigram(null, "nulltest"));
        assertThrows(IllegalArgumentException.class, () -> mc.addBigram("nulltest", null));
    }

    @Test
    public void testAddBigramsCaseSensitivity() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("Hello", "World");
        mc.addBigram("hello", "world");
        assertEquals(1, mc.bigramFrequencies.get("Hello").count("World"));
        assertEquals(1, mc.bigramFrequencies.get("hello").count("world"));
    }

    /* ***** ****** ***** ***** ADD SEQUENCE TESTS ***** ***** ****** ***** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void testAddSequence() {
        MarkovChain mc = new MarkovChain();
        String sentence = "1 2 3";
        mc.addSequence(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(3, mc.bigramFrequencies.size());
        ProbabilityDistribution<String> pd1 = mc.bigramFrequencies.get("1");
        assertTrue(pd1.getRecords().containsKey("2"));
        assertEquals(1, pd1.count("2"));
        ProbabilityDistribution<String> pd2 = mc.bigramFrequencies.get("2");
        assertTrue(pd2.getRecords().containsKey("3"));
        assertEquals(1, pd2.count("3"));
        ProbabilityDistribution<String> pd3 = mc.bigramFrequencies.get("3");
        assertTrue(pd3.getRecords().containsKey(MarkovChain.END_TOKEN));
        assertEquals(1, pd3.count(MarkovChain.END_TOKEN));
    }

    @Test
    public void testAddEmptySequence() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(new ArrayList<String>().iterator());
        assertTrue(mc.bigramFrequencies.isEmpty());
    }

    @Test
    public void testAddSequenceWithPunctuationAndSpecialTokens() {
        MarkovChain mc = new MarkovChain();
        String sentence = "@user hello! #world";
        mc.addSequence(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(3, mc.bigramFrequencies.size());
        ProbabilityDistribution<String> pdUser = mc.bigramFrequencies.get("@user");
        assertTrue(pdUser.getRecords().containsKey("hello!"));
        assertEquals(1, pdUser.count("hello!"));
        ProbabilityDistribution<String> pdHello = mc.bigramFrequencies.get("hello!");
        assertTrue(pdHello.getRecords().containsKey("#world"));
        assertEquals(1, pdHello.count("#world"));
    }

    @Test
    public void testAddSequenceSpecialCharacters() {
        MarkovChain mc = new MarkovChain();
        String sentence = "#hashtag @user";
        mc.addSequence(Arrays.stream(sentence.split(" ")).iterator());
        assertTrue(mc.bigramFrequencies.containsKey("#hashtag"));
    }

    @Test
    public void testAddSequenceWithDuplicates() {
        MarkovChain mc = new MarkovChain();
        String sentence = "duplicate duplicate duplicate";
        mc.addSequence(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(2, mc.bigramFrequencies.get("duplicate").count("duplicate"));
    }

    @Test
    public void testAddSequenceStartingPunctuation() {
        MarkovChain mc = new MarkovChain();
        String sentence = "!exclamation start middle end";
        mc.addSequence(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(1, mc.bigramFrequencies.get("!exclamation").count("start"));
    }

    /* **** ****** ****** MARKOV CHAIN CLASS TESTS ***** ****** ***** */

    /*
     * Here's an example test case for walking through the Markov Chain.
     * Be sure to add your own as well
     */
    @Test
    public void testWalk() {
        /*
         * Using the training data "CIS 1200 rocks" and "CIS 1200 beats CIS 1600",
         * we're going to put some bigrams into the Markov Chain.
         *
         * The given sequence of numbers acts as a path through the Markov Model
         * that should be followed by {@code walk}. Note that the sequence
         * includes a choice for {@code END_TOKEN}, so the length is one longer
         * than the {@code expectedTokens}.
         *
         */

        String[] expectedTokens = { "CIS", "1200", "beats", "CIS", "1200", "rocks" };
        MarkovChain mc = new MarkovChain();

        String sentence1 = "CIS 1200 rocks";
        String sentence2 = "CIS 1200 beats CIS 1600";
        mc.addSequence(Arrays.stream(sentence1.split(" ")).iterator());
        mc.addSequence(Arrays.stream(sentence2.split(" ")).iterator());

        // it can be illustrative to print out the state of the Markov Chain at this
        // point
        System.out.println(mc.toString());

        Integer[] seq = { 0, 0, 0, 0, 0, 1, 0 };
        List<Integer> choices = new ArrayList<>(Arrays.asList(seq));
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(choices));
        for (String token : expectedTokens) {
            assertTrue(walk.hasNext());
            assertEquals(token, walk.next());
        }
        assertFalse(walk.hasNext());

    }

    @Test
    public void testWalk2() {
        /* We can also use the provided method */

        String[] expectedWords = { "CIS", "1600" };
        MarkovChain mc = new MarkovChain();

        String sentence1 = "CIS 1200 rocks";
        String sentence2 = "CIS 1200 beats CIS 1600";
        mc.addSequence(Arrays.stream(sentence1.split(" ")).iterator());
        mc.addSequence(Arrays.stream(sentence2.split(" ")).iterator());

        List<Integer> choices = mc.findWalkChoices(new ArrayList<>(Arrays.asList(expectedWords)));
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(choices));
        for (String word : expectedWords) {
            assertTrue(walk.hasNext());
            assertEquals(word, walk.next());
        }
    }

    @Test
    public void testWalkWithSingleToken() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(Collections.singletonList("alone").iterator());
        Iterator<String> walk = mc.getRandomWalk();
        assertTrue(walk.hasNext());
        assertEquals("alone", walk.next());
        assertFalse(walk.hasNext());
    }

    @Test
    public void testWalkBeginningToEnd() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(Arrays.asList("start", "middle", "end").iterator());
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(Arrays.asList(0, 0, 0)));
        assertTrue(walk.hasNext() && "start".equals(walk.next()));
        assertTrue(walk.hasNext() && "middle".equals(walk.next()));
        assertTrue(walk.hasNext() && "end".equals(walk.next()));
        assertFalse(walk.hasNext());
    }

    @Test
    public void testWalkWithNoTransitions() {
        MarkovChain mc = new MarkovChain();
        mc.addSequence(Collections.singletonList("lonely").iterator());
        Iterator<String> walk = mc.getWalk(new ListNumberGenerator(Collections.singletonList(0)));
        assertTrue(walk.hasNext() && "lonely".equals(walk.next()));
        assertFalse(walk.hasNext());
    }

    @Test
    public void testWalkWithInvalidStartToken() {
        MarkovChain mc = new MarkovChain();

        mc.addSequence(Arrays.asList("a", "table", "and", "a", "chair").iterator());
        mc.addSequence(Arrays.asList("a", "banana", "!", "and", "a", "banana", "?").iterator());

        mc.addSequence(
                Arrays.asList(
                        MarkovChain.END_TOKEN, "a",
                        "table", "and", "a", "chair"
                ).iterator()
        );

        NumberGenerator ng = new NumberGenerator() {
            @Override
            public int next(int bound) {
                return 0;
            }
        };

        Iterator<String> walk = mc.getWalk(ng);

        assertTrue(walk.hasNext());

        String firstToken = walk.next();
        assertNotEquals(
                "The first token should not be END_TOKEN",
                MarkovChain.END_TOKEN, firstToken
        );
    }
}