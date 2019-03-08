def calc_MRR(rank, group, labels):
    
    pg = 0 
    
    n = 0 # group number
    
    rr = 0
    
    for g in group:
        
        x = rank[pg:(pg+g-1)] # prediction vector of the group
        y = labels[pg:(pg+g-1)] # labels vector of the group
        
        s = [l for _,l in sorted(zip(x,y), reverse=True)][0:7] # sorted top 8 desc labels 
        
        if 1 in s:
            rr += 1/(s.index(1)+1)
        
        n +=1 
        
        pg += g-1
        
    return rr/n
        
    
    
    