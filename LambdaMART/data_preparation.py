# save data function for the group and feature files
def save_data(group_data, output_feature, output_group, n_grams_flag):
    if len(group_data) == 0:
        return

    output_group.write(str(len(group_data))+"\n")
    for data in group_data:
        # only include nonzero features
        if n_grams_flag:
            # n-grams and other features
            feats = [p for p in data[2:] if float(p.split(':')[1]) != 0.0]
        else:
            #  only other features
            feats = [p for p in data[8:] if float(p.split(':')[1]) != 0.0]

        output_feature.write(data[0] + " " + " ".join(feats) + "\n")


# transform the svmlight data to the format required by xgboost's lambdaMART
def trans_data(in_file, out_feature, out_group, n_grams_flag):
    # open the files
    fi = open(in_file, encoding="utf8")
    output_feature = open(out_feature, "w")
    output_group = open(out_group, "w")
    # initialize the group lists
    group_data = []
    group = ""
    # for every line in the file extract feature and group data
    for line in fi:
        if not line:
            break
        if "#" in line:
            line = line[:line.index("#")]
        splits = line.strip().split(" ")
        if splits[1] != group:
            save_data(group_data, output_feature, output_group, n_grams_flag)
            group_data = []
        group = splits[1]
        group_data.append(splits)

    save_data(group_data, output_feature, output_group, n_grams_flag)

    # close the opened files
    fi.close()
    output_feature.close()
    output_group.close()
