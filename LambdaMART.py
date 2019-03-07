import xgboost as xgb
from sklearn.datasets import load_svmlight_file

def save_data(group_data,output_feature,output_group):
    if len(group_data) == 0:
        return

    output_group.write(str(len(group_data))+"\n")
    for data in group_data:
        # only include nonzero features
        feats = [ p for p in data[2:] if float(p.split(':')[1]) != 0.0 ]        
        output_feature.write(data[0] + " " + " ".join(feats) + "\n")

# LambdaMART with ndcg and 300 trees
params = {'objective': 'rank:ndcg', 'n_estimators': 300 , 'eta': 0.1, 'gamma': 1.0,
               'min_child_weight': 0.1, 'max_depth': 6}


fi = open("src\main\java\data\\lambdaMART.txt", encoding="utf8")
output_feature = open("src\main\java\data\qac.valid","w")
output_group = open("src\main\java\data\qac.valid.group","w")


group_data = []
group = ""

for line in fi:
    if not line:
        break
    if "#" in line:
        line = line[:line.index("#")]
    splits = line.strip().split(" ")
    if splits[1] != group:
        save_data(group_data,output_feature,output_group)
        group_data = []
    group = splits[1]
    group_data.append(splits)

save_data(group_data,output_feature,output_group)

fi.close()
output_feature.close()
output_group.close()


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
'''