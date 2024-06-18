# WordWarden Machine Learning

In this repository, we have created a models from [Kaggle](https://www.kaggle.com/datasets/ilhamfp31/indonesian-abusive-and-hate-speech-twitter-text). The dataset contains the following files:

1. README.md: This file contains detailed information about the dataset, including its description, usage, and any relevant notes or instructions.
2. abusive.csv: This file contains data related to abusive speech in Indonesian Twitter texts.
3. citation.bib: This file is a BibTeX file, which is used for referencing the dataset in academic writing.
4. data.csv: This file contains the main dataset, including both abusive and hate speech instances along with relevant annotations.
5. new_kamusalay.csv: This file contain a lexicon or dictionary used for preprocessing the text data, then to identify and normalize slang words or abbreviations.

## Data Understanding and Preprocessing

There are 5 preprocessing steps performed on the text data here, namely:

1. Case folding
2. Removing unnecessary characters (URL, username, etc.)
3. Removing punctuation marks
4. Removing stopwords
5. Converting alay words into more readable ones

## Modelling

The model to be used is Bidirectional GRU with the following configuration:

- 1 BiGRU Layer with number of hidden states 32 and dropout 0.5
- 1 Dense Layer with number of hidden states 16 and l2 regularization 0.5 and relu activation function
- 1 Dropout Layer 0,5
- Output Layer with softmax activation function
- Optimizer Adam
- Learning rate = 0,001
- Epochs = 45, but if the validation accuracy has reached more than 90%, the training process will stop immediately.
- Batch size = 256
