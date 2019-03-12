def save_data(group_data,output_feature,output_group):
    if len(group_data) == 0:
        return

    output_group.write(str(len(group_data))+"\n")
    for data in group_data:
        # only include nonzero features
        #feats = [ p for p in data[8:] if float(p.split(':')[1]) != 0.0 ]     #  only other 
        feats = [ p for p in data[2:] if float(p.split(':')[1]) != 0.0 ]     # n-grams and other   
        output_feature.write(data[0] + " " + " ".join(feats) + "\n")

def trans_data(in_file, out_feature, out_group):
   
    fi = open(in_file, encoding="utf8")
    output_feature = open(out_feature,"w")
    output_group = open(out_group,"w")
    
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
