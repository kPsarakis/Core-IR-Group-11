import xgboost as xgb
from xgboost import DMatrix
from sklearn.datasets import load_svmlight_file
from data_preparation import trans_data
from calc_MRR import calc_mrr

# if true compute the other + the n-gram features if false just the other features
n_grams_flag = True

# create all the required files for xgboost's LambdaMART implementation
trans_data("data_lambdaMART\\vali.txt", 
           "data_lambdaMART\\qac.valid", 
           "data_lambdaMART\\qac.valid.group", n_grams_flag)

trans_data("data_lambdaMART\\test.txt", 
           "data_lambdaMART\\qac.test", 
           "data_lambdaMART\\qac.test.group", n_grams_flag)

trans_data("data_lambdaMART\\train.txt", 
           "data_lambdaMART\\qac.train", 
           "data_lambdaMART\\qac.train.group", n_grams_flag)

# load the svmlight files that we created in the feature mining procedure
x_train, y_train = load_svmlight_file("data_lambdaMART\\qac.train")
x_valid, y_valid = load_svmlight_file("data_lambdaMART\\qac.valid")
x_test, y_test = load_svmlight_file("data_lambdaMART\\qac.test")

# create the groups from the files (same prefixes goes in the same group for the lambdaMART part)
group_train = []
with open("data_lambdaMART\\qac.train.group", "r") as f:
    data = f.readlines()
    for line in data:
        group_train.append(int(line.split("\n")[0]))
group_valid = []
with open("data_lambdaMART\\qac.valid.group", "r") as f:
    data = f.readlines()
    for line in data:
        group_valid.append(int(line.split("\n")[0]))
group_test = []
with open("data_lambdaMART\\qac.test.group", "r") as f:
    data = f.readlines()
    for line in data:
        group_test.append(int(line.split("\n")[0]))

# create the train/validation/test DMatrix objects required by xgboost;s lambdaMART implementation
train_dmatrix = DMatrix(x_train, y_train)
valid_dmatrix = DMatrix(x_valid, y_valid)
test_dmatrix = DMatrix(x_test)

# set the groups for the training and validation sets
train_dmatrix.set_group(group_train)
valid_dmatrix.set_group(group_valid)

# LambdaMART parameters
params = {'objective': 'rank:pairwise', 'n_estimators': 300, 'eta': 0.1, 'gamma': 1.0, 'min_child_weight': 0.1,
          'max_depth': 6}

# create lambdaMart with the aforementioned parameters
xgb_model = xgb.train(params, train_dmatrix, num_boost_round=4, evals=[(valid_dmatrix, 'validation')])

# run the prediction process on the test set
predictions = xgb_model.predict(test_dmatrix)

# plot the importance of the features in the training and validation sets
xgb.plot_importance(xgb_model)

# calculate the final lambdaMART MRR
print("MRR: ", calc_mrr(predictions, group_test, y_test))
