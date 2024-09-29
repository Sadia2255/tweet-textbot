# Tweet Textbot Project

## Overview
This is a machine learning application that reads tweets from CSV files, processes the data, and generates new tweets using a Markov Chain model. 
The generated tweets are based on the patterns found in the original tweet data, creating outputs that mimic real tweets.

## How It Works

1. **Read Tweets from CSV**:
   The program begins by reading a CSV file containing tweets. Each tweet is processed line-by-line using a custom iterator.

2. **Data Cleaning**:
   The tweets are cleaned to remove any unwanted elements like URLs, special characters, or numbers. The cleaned tweets are then broken down into sentences and individual words.

3. **Markov Chain Model**:
   The cleaned tweet data is used to train a Markov Chain. The model identifies word pairs (bigrams) and learns the likelihood of one word following another in the tweet sequences.

4. **Tweet Generation**:
   Using the trained Markov Chain, the program generates new tweets by selecting a starting word and then building a sequence of words based on the learned probabilities from the training data.

5. **Output**:
   The generated tweets are printed to the console, simulating realistic tweets based on the input data.
