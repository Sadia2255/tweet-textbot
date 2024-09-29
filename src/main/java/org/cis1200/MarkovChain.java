package org.cis1200;

import java.util.*;

public class MarkovChain {

    /** probability distribution of initial words in a sentence */
    final ProbabilityDistribution<String> startTokens;

    /** for each word, probability distribution of next word in a sentence */
    final Map<String, ProbabilityDistribution<String>> bigramFrequencies;

    /** end of sentence marker */
    static final String END_TOKEN = "<END>";

    /**
     * Construct an empty {@code MarkovChain} that can later be trained.
     *
     * This constructor is implemented for you.
     */
    public MarkovChain() {
        this.bigramFrequencies = new TreeMap<>();
        this.startTokens = new ProbabilityDistribution<>();
    }

    /**
     * Construct a trained {@code MarkovChain} from the given list of training data.
     * The training data is assumed to be non-null. Uses the {@link #addSequence}
     * method
     * on each of the provided sequences. (It is recommended that you implement
     * {@link #addBigram} and {@link #addSequence} first.)
     *
     * @param trainingData - the input sequences of tokens from which to construct
     *                     the {@code MarkovChain}
     */
    public MarkovChain(List<List<String>> trainingData) {
        this.bigramFrequencies = new TreeMap<>();
        this.startTokens = new ProbabilityDistribution<>();

        if (trainingData == null) {
            throw new IllegalArgumentException("Training data cannot be null.");
        }

        for (List<String> sequence : trainingData) {
            addSequence(sequence.iterator());
        }
    }

    /**
     * Adds a bigram to the Markov Chain information by
     * recording it in the appropriate probability distribution
     * of {@code bigramFrequencies}. (If this is the first time that {@code first}
     * has appeared in a bigram, creates a new probability distribution first.)
     *
     * @param first  The first word of the Bigram (should not be null)
     * @param second The second word of the Bigram (should not be null)
     * @throws IllegalArgumentException - when either parameter is null
     */
    void addBigram(String first, String second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException(
                    "Neither the first nor second parameters can be null."
            );
        }
        ProbabilityDistribution<String> pd = bigramFrequencies.get(first);
        if (pd == null) {
            pd = new ProbabilityDistribution<>();
            bigramFrequencies.put(first, pd);
        }
        pd.record(second);

    }

    /**
     * Adds a single tweet's training data to the Markov Chain frequency
     * information, by:
     *
     * <ol>
     * <li>recording the first token in {@code startTokens}
     * <li>recording each subsequent bigram of co-occurring pairs of tokens
     * <li>recording a final bigram of the last token of the {@code tweet} and
     * {@code END_TOKEN} to mark the end of the sequence
     * </ol>
     * <p>
     * Does nothing if the tweet is empty.
     *
     * <p>
     * Note that this is where you <i>train</i> the MarkovChain.
     *
     * @param tweet an iterator representing one tweet of training data
     * @throws IllegalArgumentException when the tweet Iterator is null
     */
    public void addSequence(Iterator<String> tweet) {
        if (tweet == null) {
            throw new IllegalArgumentException("Tweet iterator cannot be null.");
        }
        if (!tweet.hasNext()) {
            return;
        }

        String prevToken = tweet.next();
        if (!prevToken.equals(END_TOKEN)) { // Ensure we do not start with END_TOKEN
            startTokens.record(prevToken);
        } else {
            return; // Exit if the first token is END_TOKEN
        }

        while (tweet.hasNext()) {
            String nextToken = tweet.next();
            addBigram(prevToken, nextToken);
            prevToken = nextToken;
        }

        addBigram(prevToken, END_TOKEN);
    }

    /**
     * Returns the ProbabilityDistribution for a given token. Returns null if
     * none exists. This function is implemented for you.
     *
     * @param token - the token for which the ProbabilityDistribution is sought
     * @throws IllegalArgumentException - when parameter is null.
     * @return a ProbabilityDistribution or null
     */
    ProbabilityDistribution<String> get(String token) {
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null.");
        }
        return bigramFrequencies.get(token);
    }

    /**
     * Gets a walk through the Markov Chain that follows
     * the path given by the {@code NumberGenerator}. See
     * {@link MarkovChainIterator} for the details.)
     *
     * This function is implemented for you.
     *
     * @param ng the path to follow (represented as a {@code NumberGenerator}
     * @return an {@code Iterator} that yields the tokens on that path
     *
     */
    public Iterator<String> getWalk(NumberGenerator ng) {
        return new MarkovChainIterator(ng);
    }

    /**
     * Gets a random walk through the Markov Chain.
     *
     * This function is implemented for you.
     *
     * @return an {@code Iterator} that yields the tokens on that path
     */
    public Iterator<String> getRandomWalk() {
        return getWalk(new RandomNumberGenerator());
    }

    class MarkovChainIterator implements Iterator<String> {
        // stores the source of numbers that determine the path of ths walk
        private NumberGenerator ng;
        private String currentToken;
        private boolean isFinished;

        // this (MarkovChainIterator) is an inner class
        // so it can access the field of the outer class (MarkovChain)

        /**
         * Constructs an iterator that follows the path specified by the given
         * {@code NumberGenerator}.The first token of the walk is chosen from
         * {@code startTokens}
         * by picking from that distribution using ng's first number. If the number
         * generator can
         * not provide a valid start index, or if there are no start tokens, returns an
         * empty
         * Iterator (i.e., one for which hasNext is always false).
         *
         * @param ng the number generator to use for this walk
         */
        MarkovChainIterator(NumberGenerator ng) {
            this.ng = ng;
            if (startTokens.getTotal() > 0) {
                int index = ng.next(startTokens.getTotal());
                if (index < 0 || index >= startTokens.getTotal()) {
                    isFinished = true;
                } else {
                    currentToken = startTokens.pick(index);
                    if (currentToken.equals(END_TOKEN) ||
                            !bigramFrequencies.containsKey(currentToken)) {
                        isFinished = true;
                    } else {
                        isFinished = false;
                    }
                }
            } else {
                // Handling empty startTokens by setting isFinished to true
                isFinished = true;
            }
        }

        /**
         * This method determines whether there is a next token in the
         * Markov Chain based on the current state of the walk. Remember that the
         * end of a sentence is denoted by the token {@code END_TOKEN}.
         * <p>
         * Your solution should be very short.
         *
         * @return true if {@link #next()} will return a non-{@code END_TOKEN} String
         *         and false otherwise
         */
        @Override
        public boolean hasNext() {
            return !isFinished;
        }

        /**
         *
         * @return the next word in the MarkovChain's walk
         * @throws NoSuchElementException if there are no more words on the walk
         *                                through the chain (i.e. it has reached
         *                                {@code END_TOKEN}),
         *                                or if the number generator provides an invalid
         *                                choice
         *                                (e.g, an illegal argument for {@code pick}).
         */
        @Override
        public String next() {
            if (isFinished) {
                throw new NoSuchElementException("End of chain reached.");
            }
            String result = currentToken;
            ProbabilityDistribution<String> distribution = bigramFrequencies.get(currentToken);
            if (distribution == null || distribution.getTotal() == 0) {
                isFinished = true;
            } else {
                int nextIndex = ng.next(distribution.getTotal());
                if (nextIndex < 0 || nextIndex >= distribution.getTotal()) {
                    throw new NoSuchElementException("Invalid number generator index.");
                }
                currentToken = distribution.pick(nextIndex);
                if (currentToken.equals(END_TOKEN)) {
                    isFinished = true;
                }
            }
            return result;
        }
    }

    /**
     * Generate a list of numbers such that if it is installed as the
     * number generator for the MarkovChain, and used as an iterator,
     * the words returned in sequence will be the list of provided words.
     */
    public List<Integer> findWalkChoices(List<String> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Invalid empty or null words");
        }
        words.add(END_TOKEN);
        List<Integer> choices = new LinkedList<>();

        String curWord = words.remove(0);
        choices.add(startTokens.index(curWord));

        while (words.size() > 0) {
            ProbabilityDistribution<String> curDist = bigramFrequencies.get(curWord);
            String nextWord = words.remove(0);
            choices.add(curDist.index(nextWord));
            curWord = nextWord;
        }
        return choices;
    }

    /**
     * Use this method to print out markov chains with words and probability
     * distributions.
     *
     * This function is implemented for you.
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("startTokens: ").append(startTokens.toString());
        res.append("\nbigramFrequencies:\n");
        for (Map.Entry<String, ProbabilityDistribution<String>> c : bigramFrequencies.entrySet()) {
            res.append("\"");
            res.append(c.getKey());
            res.append("\":\t");
            res.append(c.getValue().toString());
            res.append("\n");
        }
        return res.toString();
    }
}
