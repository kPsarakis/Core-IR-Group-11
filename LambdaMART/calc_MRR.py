# function that calculates the lambdaMART's MRR
def calc_mrr(rank, group, labels):
    # group number
    n = 0
    # the reciprocal rank
    rr = 0
    # previous groups index
    pg = 0
    # for every group
    for g in group:
        # prediction vector of the group
        x = rank[pg:(pg+g-1)]
        # labels vector of the group
        y = labels[pg:(pg+g-1)]
        # sorted top 8 desc labels
        s = [l for _, l in sorted(zip(x, y), reverse=True)][0:7]
        # find if the relevant suggested query exists in the top 8, if so calculate the reciprocal rank
        if 1 in s:
            rr += 1/(s.index(1)+1)
        # increase the group counter
        n += 1
        # increase the group index
        pg += g-1

    # return the final MRR
    return rr/n
