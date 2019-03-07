import string
import glob
# import sys
from datetime import datetime

# File containing the background data
background_path = '../dataset/background-queries.txt'
background_file = open(background_path, 'w', encoding='utf8')

# File containing the training data
training_path = '../dataset/training-queries.txt'
training_file = open(training_path, 'w', encoding='utf8')

# File containing the validation data
validation_path = '../dataset/validation-queries.txt'
validation_file = open(validation_path, 'w', encoding='utf8')

# File containing the test data
test_path = '../dataset/test-queries.txt'
test_file = open(test_path, 'w', encoding='utf8')

# Collecting of the txt names from the folder
txt_files = glob.glob('../*.txt')

# Sets of unique queries for training, validation and testing
unique_training_queries = set()
unique_validation_queries = set()
unique_testing_queries = set()

for path in txt_files:
    file = open(path, 'r', encoding='utf8')
    text = file.readlines()
    print('Reading ', path)
    # Variable that keeps track of the first line in each file
    first = True
    for line in text:
        values = line.split("\t")
        user_id = values[0]
        query = values[1]
        # Skip queries that simply contain a dashed line
        if query == "-":
            continue
        query_date = values[2]
        # Replacing punctuation characters with whitespace
        query_nopunc = query.translate(str.maketrans(string.punctuation, ' '*len(string.punctuation)))
        # Convert query to lowercase
        query_lower = query_nopunc.lower()
        # Removing multiple whitespaces from the final query
        query_final = ' '.join(query_lower.split())
        # Skip queries that were only containing punctuation characters
        if query_final == '':
            continue
        if first:
            # Skipping titles
            first = False
            continue
        # Converting to datetime to enable the date comparisons
        query_date = datetime.strptime(query_date, '%Y-%m-%d %H:%M:%S')
        if datetime(2006, 3, 1, 0, 0, 0) <= query_date <= datetime(2006, 4, 30, 23, 59, 59):
            background_file.write(query_final + '\n')
        elif datetime(2006, 5, 1, 0, 0, 0) <= query_date <= datetime(2006, 5, 14, 23, 59, 59):
            unique_training_queries.add(query_final + '\n')
        elif datetime(2006, 5, 15, 0, 0, 0) <= query_date <= datetime(2006, 5, 21, 23, 59, 59):
            unique_validation_queries.add(query_final + '\n')
        elif datetime(2006, 5, 22, 0, 0, 0) <= query_date <= datetime(2006, 5, 28, 23, 59, 59):
            unique_testing_queries.add(query_final + '\n')
        file.close()

# Writing unique queries to files
for query in unique_training_queries:
    training_file.write(str(query))
for query in unique_validation_queries:
    validation_file.write(str(query))
for query in unique_testing_queries:
    test_file.write(str(query))

# Closing files
background_file.close()
training_file.close()
validation_file.close()
test_file.close()
