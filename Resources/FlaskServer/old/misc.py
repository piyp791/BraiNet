import numpy as np
import ast

str = '[[1.0, 2.0], [3.0, 3.0]]'

print 'str->', str
arr =  ast.literal_eval(str)


print arr[1]
