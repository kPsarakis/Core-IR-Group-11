import xgboost as xgb
from sklearn.datasets import load_svmlight_file

# LambdaMART with ndcg and 300 trees
params = {'objective': 'rank:ndcg', 'n_estimators': 300}

# Extra params
'''
'learning_rate': 0.1,
          'gamma': 1.0, 'min_child_weight': 0.1,
          'max_depth': 6,
'''

# START example in datasets

# TO-DO Find who to mod our dataset to fit this format

x_train, y_train = load_svmlight_file("mq2008.train")
x_valid, y_valid = load_svmlight_file("mq2008.vali")
x_test, y_test = load_svmlight_file("mq2008.test")

group_train = []
with open("mq2008.train.group", "r") as f:
    data = f.readlines()
    for line in data:
        group_train.append(int(line.split("\n")[0]))

group_valid = []
with open("mq2008.vali.group", "r") as f:
    data = f.readlines()
    for line in data:
        group_valid.append(int(line.split("\n")[0]))

group_test = []
with open("mq2008.test.group", "r") as f:
    data = f.readlines()
    for line in data:
        group_test.append(int(line.split("\n")[0]))

# END example in datasets

# Create the ranker
model = xgb.sklearn.XGBRanker(**params)

# Train the ranker
model.fit(x_train, y_train, group_train,
          eval_set=[(x_valid, y_valid)], eval_group=[group_valid])

# Get ranks
pred = model.predict(x_test)
