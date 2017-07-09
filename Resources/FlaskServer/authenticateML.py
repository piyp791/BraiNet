# -*- coding: utf-8 -*-
"""
Created on Sat Jul 08 21:01:11 2017

@author: Shalmali
"""

import numpy as np
import math
from sklearn.svm import SVC
from sklearn.linear_model import SGDClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix
from DBHelper import DBHelper

def featureVecs(out, sample_size):
    #sample_size = 120
    fVec = np.zeros((sample_size,6), dtype = float)
    
    i=0
    j=0
    while (i < len(out) and j < sample_size):
        fVec[j][:5] = out[i:i+5]
        #print 'fVec : ', fVec[j][:5], out[i:i+5], i
        i = i+6
        j = j+1
    return fVec


def FeatureExt(signal, signal_len):
    # in seconds
    #signal_len = 120;
    # sample rate in HZ
    SR = 512;
    out = np.zeros((signal_len * 6), dtype=float);
    S_FFT = np.zeros((SR), dtype=float);
    Temp = np.zeros((SR), dtype=float);

    for i in range(0, signal_len):
        offset = (i-1)*SR
        S_FFT[0:SR-1] = FFT(signal[offset:offset+SR-1])[:,0]
        out[(i-1)*6:(i-1)*6+5] = S_FFT[7:12]

    featureVectors = featureVecs(out, signal_len)
    return featureVectors


def FFT(signal):
    Fs = 512; # Sampling frequency
    T = 1 / Fs; # Sample time
    L = len(signal[0]); # Length of signal
    #t = [0:L - 1]*T; # Time vector
    NFFT = 2 ^ nextpow2(L); # Next power of 2 from length of y
    f = Fs / 2 * np.linspace(0, 1, NFFT / 2 + 1);
    Y = np.fft.fft(signal, NFFT) / L;
    # y = 2 * abs(Y(1:NFFT / 2 + 1));
    y = 2 * abs(Y);
    return y

def nextpow2(n):
    n = abs(n)
    for i in range (0, n):
        if math.pow(2,i) >= n:
            return i

def NaiveBayes(train_arr, test_arr, train_size, test_size):
    #sample_size = 120
    labels = np.ones((train_size+test_size), dtype=float)
    labels[train_size-1:train_size+test_size-1] = 0

    ### Al data
    train_data = np.zeros((train_size+test_size,6), dtype=float)
    train_data[1:train_size][:] = train_arr[0:train_size-1][:]
    train_data[train_size+1:train_size+test_size][:] = test_arr[0:test_size-1][:]
    
    gnb = GaussianNB()
    y_pred = gnb.fit(train_data, labels).predict(train_data)
    mat = confusion_matrix(labels, y_pred)
    print 'Confusion Matrix: ', mat
    
    if (mat[0][0] > mat[0][1]) and (mat[1][1] > mat[1][0]):
        authenticated = 0
    else:
        authenticated = 1
    return authenticated

def MLP(train_arr, test_arr, train_size, test_size):
    #sample_size = 120
    labels = np.ones((train_size+test_size), dtype=float)
    labels[train_size-1:train_size+test_size-1] = 0

    ### Al data
    train_data = np.zeros((train_size+test_size,6), dtype=float)
    train_data[1:train_size][:] = train_arr[0:train_size-1][:]
    train_data[train_size+1:train_size+test_size][:] = test_arr[0:test_size-1][:]
    
    mlp = MLPClassifier(solver='lbfgs', alpha=1e-5,
                     hidden_layer_sizes=(5, 2), random_state=1)
    y_pred = mlp.fit(train_data, labels).predict(train_data)
    mat = confusion_matrix(labels, y_pred)
    print 'Confusion Matrix: ', mat
    
    if (mat[0][0] > mat[0][1]) and (mat[1][1] > mat[1][0]):
        authenticated = 0
    else:
        authenticated = 1
    return authenticated

def SVM(train_arr, test_arr, train_size, test_size):
    #sample_size = 120
    labels = np.ones((train_size+test_size), dtype=float)
    labels[train_size-1:train_size+test_size-1] = 0

    ### Al data
    train_data = np.zeros((train_size+test_size,6), dtype=float)
    train_data[1:train_size][:] = train_arr[0:train_size-1][:]
    train_data[train_size+1:train_size+test_size][:] = test_arr[0:test_size-1][:]
    
    svm = SVC()
    y_pred = svm.fit(train_data, labels).predict(train_data)
    mat = confusion_matrix(labels, y_pred)
    print 'Confusion Matrix: ', mat
    
    if (mat[0][0] > mat[0][1]) and (mat[1][1] > mat[1][0]):
        authenticated = 0
    else:
        authenticated = 1
    return authenticated

def SGD(train_arr, test_arr, train_size, test_size):
    #sample_size = 120
    labels = np.ones((train_size+test_size), dtype=float)
    labels[train_size-1:train_size+test_size-1] = 0

    ### Al data
    train_data = np.zeros((train_size+test_size,6), dtype=float)
    train_data[1:train_size][:] = train_arr[0:train_size-1][:]
    train_data[train_size+1:train_size+test_size][:] = test_arr[0:test_size-1][:]
    
    sgd = SGDClassifier(loss="hinge", penalty="l2")
    y_pred = sgd.fit(train_data, labels).predict(train_data)
    mat = confusion_matrix(labels, y_pred)
    print 'Confusion Matrix: ', mat
    
    if (mat[0][0] > mat[0][1]) and (mat[1][1] > mat[1][0]):
        authenticated = 0
    else:
        authenticated = 1
    return authenticated

def main():
    db = DBHelper()
    cnx = db.getConn()
    
    # Fetch training data
    cursor= db.fetchColumnFromWhere("UBrainData", "data", "ID = 14", cnx)
    data = cursor.fetchall()
    if len(data) < 61440:
        f = int(len(data) / 512)
        data = data[0:512*f]
        data = np.asarray(data).reshape(len(data))
    
    train_size = len(data)/512    
    featureVectors_train = featureVecs(data, train_size)
    
    # Fetch test data
    cursor= db.fetchColumnFromWhere("UBrainData", "data", "ID = 5", cnx)
    data = cursor.fetchall()
    if len(data) < 61440:
        f = int(len(data) / 512)
        data = data[0:512*f]
        data = np.asarray(data).reshape(len(data))
    
    test_size = len(data)/512
    featureVectors_test = featureVecs(data, test_size)
    
    print 'NB', NaiveBayes(featureVectors_train, featureVectors_test, train_size, test_size)
    print 'MLP',MLP(featureVectors_train, featureVectors_test, train_size, test_size)
    print 'SVM',SVM(featureVectors_train, featureVectors_test, train_size, test_size)
    print 'SGD',SGD(featureVectors_train, featureVectors_test, train_size, test_size)


#
# Returns 1 if the user is authenticated, 0 otherwise 
#
def authenticateML(data, id):
    db = DBHelper()
    cnx = db.getConn()
    
    # Fetch training data
    cursor= db.fetchColumnFromWhere("UBrainData", "data", "ID = " + id, cnx)
    DBdata = cursor.fetchall()
    f = int(len(DBdata) / 512)
    DBdata = DBdata[0:512*f]
    DBdata = np.asarray(DBdata).reshape(len(DBdata))
    train_size = len(DBdata)/512    
    featureVectors_train = featureVecs(data, train_size)
    
    f = int(len(DBdata) / 512)
    DBdata = DBdata[0:512*f]
    data = np.asarray(data).reshape(len(data))
    test_size = len(data)/512    
    featureVectors_test = featureVecs(data, test_size)
    authenticated = SVM(featureVectors_train, featureVectors_test, train_size, test_size)
    
    return authenticated

if __name__ == '__main__':
    a=0
    #main()