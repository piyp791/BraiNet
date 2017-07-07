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

#
# Authenticates the user with its current brainwave anad previously recorded brainwave using DTW
# Returns 1 if the user is authenticated, 0 otherwise
#
def process_for_DTW(x_arr, y_arr, currentUserId):
    
    # DB initialization
    db = DBHelper()
    cnx = db.getConn()
    
    #Combine x_arr and y_arr
    x_arr = np.array(x_arr)
    y_arr = np.array(y_arr)
    xy_arr = np.vstack((x_arr, y_arr)).T
    cursor = db.fetchColumnFrom("UserInfo", "userID", cnx)
    userID_list = cursor.fetchall()
    distances = []
    #print userID_list
    for uid_item in userID_list:
        # Get the latest brainwave entry
        condExpr = 'userID = ' + str(uid_item[0])+ " ORDER BY timestamp DESC LIMIT 1"
        #   print uid_item[0], currentUserId
        cursor = db.fetchFromWhere( "UBrainData", condExpr, cnx)

        #print cursor.rowcount
        if cursor.rowcount != 0:
            series_list = cursor.fetchone()
        
            series_list_str_x = str(series_list[3])
            print 'new type -->', type(series_list_str_x)
            #print 'list string ----->', series_list_str
            series_list_obj = eval(series_list_str_x)
            print 'type of data-->', type(series_list_obj[0]), type(series_list_obj)

        
            #x_test_arr = np.array(json.loads(series_list[3])).tolist()
            x_test_arr = np.array(json.loads(series_list[3])).tolist()
            y_test_arr = np.array(json.loads(series_list[4])).tolist()
            dist1, path1 = fastdtw(x_arr, x_test_arr)
            dist2, path2 = fastdtw(y_arr, y_test_arr)
            dist = math.sqrt(dist1*dist1 + dist2*dist2)
            #dist = math.sqrt(dist2*dist2)
            distances.append([uid_item[0], dist])

    #Close connections
    cursor.close()
    db.closeConn(cnx)

    distances = sorted(distances,key=lambda x: x[1])
    print ' distances-->', distances
    print "Authenticated UserID :", distances[0][0], "\n DTW Distance: ", distances[0][1]
    print 'current user ID-->', currentUserId
    '''if currentUserId == distances[0][0]:
        return 1
    else:
        return 0'''
    return distances[0][0]


def registerUSerInfo(name, age, gender):
    # DB initialization
    db = DBHelper()
    cnx = db.getConn()

    # Insert User Info in the USerInfo table and get the unique user id
    userID = db.insertIntoUserInfo(name, gender, age, cnx)

    db.closeConn(cnx)
    return userID

def registerUserBrainwave(x_series, y_series, userID):
    # DB initialization
    db = DBHelper()
    cnx = db.getConn()

    # Insert user brainwave data into the UBrainData table
    sessionID = db.insertIntoUBrainData(userID, str(datetime.datetime.now()), x_series, y_series, cnx)

    db.closeConn(cnx)
    return sessionID

def main():
    x_arr = [1,2,3,4]
    y_arr = [4,5,6,7]
    a = process_for_DTW(x_arr, y_arr, "1")

if __name__ == '__main__':
    main()