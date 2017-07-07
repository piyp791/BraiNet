# -*- coding: utf-8 -*-
"""
Created on Tue Jul 04 18:29:05 2017

@author: Shalmali
"""
from fastdtw import fastdtw
import numpy as np
import json
import math
from DBHelper import DBHelper
import datetime
import pywt
from scipy.spatial.distance import euclidean

#
# Authenticates the user with its current brainwave anad previously recorded brainwave using DTW
# Returns 1 if the user is authenticated, 0 otherwise
#
def process_for_DTW(arr, currentUserId):
    
    # DB initialization
    db = DBHelper()
    cnx = db.getConn()
    
    #Combine x_arr and y_arr
    cA, cD = pywt.dwt(arr, 'db1')
    cA = list(cA)

    print 'cA values--->', cA
    arr = np.array(cA)
    cursor = db.fetchColumnFrom("UserInfo", "userID", cnx)
    userID_list = cursor.fetchall()
    distances = []
    #print userID_list
    for uid_item in userID_list:
        # Get the latest brainwave entry
        print 'uid_item-->', uid_item[0]
        condExpr = 'ID = ' + str(uid_item[0])
        #   print uid_item[0], currentUserId
        cursor = db.fetchFromWhere("UBrainData", condExpr, cnx)
        #print 'cursor result-->', cursor
        #print cursor.rowcount
        if cursor.rowcount != 0:
            series_list = cursor.fetchall()
            #print 'series list-->', series_list
            data = []
            for row in series_list:
                data.append(float(row[3]))

            cA, cD = pywt.dwt(data, 'db1')
            cA = list(cA)
            '''series_list_str_x = str(series_list[3])
            print 'new type -->', type(series_list_str_x)
            series_list_obj = eval(series_list_str_x)
            print 'type of data-->', type(series_list_obj[0]), type(series_list_obj)

        
            #x_test_arr = np.array(json.loads(series_list[3])).tolist()
            x_test_arr = np.array(series_list[3]).tolist()
            y_test_arr = np.array(series_list[4]).tolist()
            dist1, path1 = fastdtw(x_arr, x_test_arr)'''
            #data = eval(data)
            arr = np.array(arr)
            data = np.array(cA)
            print 'arr types -->', type(arr), type(arr[0])
            print 'arr types -->', type(data), type(data[0])
            dist2, path2 = fastdtw(arr, data, dist=euclidean)
            distances.append([uid_item[0], dist2])

    #Close connections
    cursor.close()
    db.closeConn(cnx)

    distances = sorted(distances,key=lambda x: x[1])
    print ' distances-->', distances
    '''print "Authenticated UserID :", distances[0][0], "\n DTW Distance: ", distances[0][1]
    print 'current user ID-->', currentUserId'''
    return 5


def registerUSerInfo(name, age, gender):
    # DB initialization
    db = DBHelper()
    cnx = db.getConn()

    # Insert User Info in the USerInfo table and get the unique user id
    userID = db.insertIntoUserInfo(name, gender, age, cnx)

    db.closeConn(cnx)
    return userID

def registerUserBrainwave(data_series, userID, sessionID):
    # DB initialization
    db = DBHelper()
    cnx = db.getConn()

    # Insert user brainwave data into the UBrainData table
    
    userID_list = [userID]*len(data_series)
    datetime_list = [str(datetime.datetime.now())]*len(data_series)
    sessionId_list = [sessionID]*len(data_series)
    sessionID = db.batchInsertBrainData(userID_list, datetime_list, sessionId_list, data_series, cnx)

    db.closeConn(cnx)
    return sessionID

def main():
    x_arr = [1,2,3,4]
    y_arr = [4,5,6,7]
    a = process_for_DTW(x_arr, y_arr, "1")

if __name__ == '__main__':
    main()