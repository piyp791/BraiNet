import pywt

pt = [1]
ca, cb = pywt.dwt(pt, 'db1')
print ca
print cb
