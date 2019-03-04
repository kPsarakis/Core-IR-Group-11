import string
import glob
# import sys
from datetime import datetime

# File containing the background data
background_path = '../dataset_test2/background-queries.txt'
background_file = open(background_path, 'w', encoding='utf8')

# File containing the training data
training_path = '../dataset_test2/training-queries.txt'
training_file = open(training_path, 'w', encoding='utf8')

# File containing the validation data
validation_path = '../dataset_test2/validation-queries.txt'
validation_file = open(validation_path, 'w', encoding='utf8')

# File containing the test data
test_path = '../dataset_test2/test-queries.txt'
test_file = open(test_path, 'w', encoding='utf8')

# Collecting of the txt names from the folder
txt_files = glob.glob('../*.txt')

# Dictionary of unique id/query key and it's first occurrence timestamp as a value
unique_searches = {}
# Counter to help limit the list size to 100
count = 0

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
        # Skipping first line which contains the titles
        if first:
            # Skipping titles
            first = False
            continue
        # Converting to datetime to enable the date comparisons
        query_date = datetime.strptime(query_date, '%Y-%m-%d %H:%M:%S')
        # Creating user id/query/timestamp combination
        query_check = user_id + query
        # If the combination has already been found
        if query_check in unique_searches:
            # Within the past 10 seconds, skip it
            if (query_date - unique_searches[query_check]).seconds <= 10:
                continue
        # Clear list every 100 unique queries
        if count == 100:
            count = 0
            unique_searches.clear()
        # Update unique combination list
        unique_searches[user_id + query] = query_date
        count += 1
        item_rank = values[3]
        click_url = values[4]
        # Replacing punctuation characters with whispace
        query_nopunc = query.translate(str.maketrans(string.punctuation, ' '*len(string.punctuation)))
        # Convert query to lowercase
        query_final = query_nopunc.lower()
        if datetime(2006, 3, 1, 0, 0, 0) <= query_date <= datetime(2006, 4, 30, 23, 59, 59):
            query_date = str(query_date)
            background_file.write(query_final + '\t' + query_date + '\n')
        elif datetime(2006, 5, 1, 0, 0, 0) <= query_date <= datetime(2006, 5, 14, 23, 59, 59):
            query_date = str(query_date)
            training_file.write(query_final + '\t' + query_date + '\n')
        elif datetime(2006, 5, 15, 0, 0, 0) <= query_date <= datetime(2006, 5, 21, 23, 59, 59):
            query_date = str(query_date)
            validation_file.write(query_final + '\t' + query_date + '\n')
        elif datetime(2006, 5, 22, 0, 0, 0) <= query_date <= datetime(2006, 5, 28, 23, 59, 59):
            query_date = str(query_date)
            test_file.write(query_final + '\t' + query_date + '\n')
        file.close()

# Closing files
background_file.close()
training_file.close()
validation_file.close()
test_file.close()
