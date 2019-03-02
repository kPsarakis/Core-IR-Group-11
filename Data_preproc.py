import string
import glob
import sys
from datetime import datetime

# File containing the background data
background_path = '../data/background-queries.txt'
background_file = open(background_path, 'w', encoding='utf8')

# File containing the training data
training_path = '../data/training-queries.txt'
training_file = open(training_path, 'w', encoding='utf8')

# File containing the validation data
validation_path = '../data/validation-queries.txt'
validation_file = open(validation_path, 'w', encoding='utf8')

# File containing the test data
test_path = '../data/test-queries.txt'
test_file = open(test_path, 'w', encoding='utf8')

# Collecting of the txt names from the folder
txt_files = glob.glob('../*.txt')

for path in txt_files:
    file = open(path, 'r', encoding='utf8')
    text = file.readlines()
    print('Reading ', path)

    first = True

    for line in text:
        values = line.split("\t")
        user_id = values[0]
        query = values[1]
        query_date = values[2]
        item_rank = values[3]
        click_url = values[4]
        # Removing any punctuation characters.
        query_nopunc = query.translate(str.maketrans('', '', string.punctuation))
        # Convert query to lowercase
        query_final = query_nopunc.lower()
        # Skipping first line which contains the titles
        if first:
            # Write the titles to the files
            background_file.write(query + '\t' + query_date + '\n')
            training_file.write(query + '\t' + query_date + '\n')
            validation_file.write(query + '\t' + query_date + '\n')
            test_file.write(query + '\t' + query_date + '\n')
            first = False
            continue
        # Converting to datetime to enable the date comparisons
        query_date = datetime.strptime(query_date, '%Y-%m-%d %H:%M:%S')
        if datetime(2006, 3, 1, 0, 0, 0) <= query_date <= datetime(2006, 4, 30, 23, 59, 59):
            # Converting back to string so that it can be written to the file
            query_date = str(query_date)
            background_file.write(query + '\t' + query_date + '\n')
        elif datetime(2006, 5, 1, 0, 0, 0) <= query_date <= datetime(2006, 5, 14, 23, 59, 59):
            # Converting back to string so that it can be written to the file
            query_date = str(query_date)
            training_file.write(query + '\t' + query_date + '\n')
        elif datetime(2006, 5, 15, 0, 0, 0) <= query_date <= datetime(2006, 5, 21, 23, 59, 59):
            # Converting back to string so that it can be written to the file
            query_date = str(query_date)
            validation_file.write(query + '\t' + query_date + '\n')
        elif datetime(2006, 5, 22, 0, 0, 0) <= query_date <= datetime(2006, 5, 28, 23, 59, 59):
            # Converting back to string so that it can be written to the file
            query_date = str(query_date)
            test_file.write(query + '\t' + query_date + '\n')

        file.close()

# Closing files
background_file.close()
training_file.close()
validation_file.close()
test_file.close()
