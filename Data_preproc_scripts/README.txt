Data_preproc.py: replaced punctuation with whitespace, converted to lowercase, ignored empty queries and removed multiple whitespaces. Kept unique queries for validation, training and test sets

Data_preproc_test1.py: baseline preproc plus filtering out identical id-query-timestap combinations.

Data_preproc_test2.py: test1 preproc plus removing identical id-query combinations 10 seconds apart.