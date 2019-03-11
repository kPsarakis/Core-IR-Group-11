import xgboost as xgb
from xgboost import DMatrix
from sklearn.datasets import load_svmlight_file

from data_preparation import trans_data
from calc_MRR import calc_MRR

# LambdaMART with ndcg and 300 trees
params = {'objective': 'rank:pairwise', 'n_estimators': 300 , 'eta': 0.1, 'gamma': 1.0,
               'min_child_weight': 0.1, 'max_depth': 6}

trans_data("data_lambdaMART\\vali.txt", 
           "data_lambdaMART\\qac.valid", 
           "data_lambdaMART\\qac.valid.group")

trans_data("data_lambdaMART\\test.txt", 
           "data_lambdaMART\\qac.test", 
           "data_lambdaMART\\qac.test.group")

trans_data("data_lambdaMART\\train.txt", 
           "data_lambdaMART\\qac.train", 
           "data_lambdaMART\\qac.train.group")

x_train, y_train = load_svmlight_file("data_lambdaMART\\qac.train")
x_valid, y_valid = load_svmlight_file("data_lambdaMART\\qac.valid")
x_test, y_test = load_svmlight_file("data_lambdaMART\\qac.test")

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

train_dmatrix = DMatrix(x_train, y_train)

valid_dmatrix = DMatrix(x_valid, y_valid)

test_dmatrix = DMatrix(x_test)

train_dmatrix.set_group(group_train)
valid_dmatrix.set_group(group_valid)

xgb_model = xgb.train(params, train_dmatrix, num_boost_round=4,
                           evals=[(valid_dmatrix, 'validation')])
pred = xgb_model.predict(test_dmatrix)

xgb.plot_importance(xgb_model)

print("MRR: ",calc_MRR(pred, group_test, y_test))