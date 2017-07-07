# -*- coding: utf-8 -*-
"""
Created on Sat Jul 01 01:09:43 2017

@author: Shalmali
"""

import mysql.connector
from mysql.connector import errorcode
import logging
import json as json 
import datetime

class DBHelper:     
     def __init__(self):
        logging.basicConfig(filename='DBLogs.log',level=logging.DEBUG)
              
     def getConn(self):
         logger = logging.getLogger()
         try:
            cnx = mysql.connector.connect(user='root', password='RootRoot',
                                          host='group7project.cveeuzcqvex9.us-east-1.rds.amazonaws.com',
                                          database='BrainNet')
            
            logger.debug("Successfully established connection to the database!")
            
         except mysql.connector.Error as err:
            if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
                print("Invalid user name or password")
            elif err.errno == errorcode.ER_BAD_DB_ERROR:
                print("Database does not exist")
            else:
                print(err)
         return cnx
     
        
     # 
     # Helper method for Data Insertion into a table #   
     #
     def insertIntoUserInfo(self, name, gender, age, cnx):
        add_user = ("INSERT INTO UserInfo" +
                    "(Name, Gender, Age) VALUES ( %s, %s, %s)")
        user_data = (name, gender, age)
        cursor = cnx.cursor()
        try:
            cursor.execute(add_user, user_data)
            cnx.commit()
            cursor.close()
            print "Lastrowid: ", cursor.lastrowid
            print ("Data inserted successfully")
            return cursor.lastrowid
        except:

            print ("Data insertion failed!!!")
            return -1

     #
     # Helper method for Data Insertion into a table #
     #
     def insertIntoUBrainData(self, userID, timestamp, x_series, y_series , cnx):
        add_user = ("INSERT INTO UBrainData" +
                    "(UserID, timestamp, x_Series, y_series) VALUES ( %s, %s, %s, %s)")
        user_data = (userID, timestamp, json.dumps(x_series), json.dumps(y_series))
        cursor = cnx.cursor()
        try:
            cursor.execute(add_user, user_data)
            cnx.commit()
            print "Lastrowid: ", cursor.lastrowid
            cursor.close()
            print ("Data inserted successfully")
            return cursor.lastrowid
        except:
            cursor.close()
            print ("Data insertion failed!!!")

     # 
     # Helper method for Data fetching from a tablebased on a condition
     # condExpr takes series of conditions joined by logical operators
     #      
     def fetchFromWhere(self, tablename, condExpr, cnx):
         select_user = ("SELECT * FROM " +tablename + " WHERE " + condExpr)
         cursor = cnx.cursor(buffered=True)
         try:
            cursor.execute(select_user)
            print ("Data Fetching successful")
            return cursor
         except:
            print ("Data Fetching failed!!!")
            return cursor


     def fetchFrom(self, tablename, condExpr, cnx):
         select_user = ("SELECT * FROM " +tablename )
         cursor = cnx.cursor()
         try:
            cursor.execute(select_user)
            print select_user
            print ("Data Fetching successful")
         except:
            print ("Data Fetching failed!!!")
         return cursor
         cursor.close()
# 
     # Helper method for a specific column fetching from a tablebased
     # condExpr takes series of conditions joined by logical operators
     #     
     def fetchColumnFrom(self, tablename, colname, cnx):
        select_user = ("SELECT " + colname + " FROM " + tablename)
        cursor = cnx.cursor()
        try:
            cursor.execute(select_user)
            print ("Data Fetching successful")
        except:
            print ("Data Fetching failed!!!")
        return cursor
    
     # 
     # Helper method to check if the user is an admin
     # Returns 1 if admin, returns 0 if not an admin
     #      
     def checkIfAdmin(self, userId, cnx):
        select_user = ("SELECT * FROM AdminInfo")
        cursor = cnx.cursor()
        try:
            cursor.execute(select_user)
            adminId = cursor.fetchone()[0]
            #print adminId
            if userId == adminId:
                return 1
            else:
                return 0
        except:
            print ("Data Fetching failed!!!")
        cursor.close()

     #
     # Close the connection to the DB
     #
     def closeConn(self, cnx):
         cnx.close()

def main():
    db = DBHelper()
    cnx = db.getConn()
    #results = db.fetchFromWhere('UserInfo', 'Name = \'ABC\'', cnx)
    cursor = cnx.cursor()
    dump1 = json.dumps([0,12,3,21,9,6])
    dump2 = json.dumps([2,11,4,2,2,6])
    data = ("1_1", "1", "1", str(datetime.datetime.now()), dump1, dump2)
    db.insertIntoUserInfo('U7', 'Female', '12', cnx)
    #cursor.execute("INSERT INTO UBrainData(dataID, UserID, SessionID, timestamp, x_series, y_series)"+
    #"VALUES(%s, %s, %s, %s, %s, %s)", data)
    #cnx.commit()
    #cursor.close()
    #print db.checkIfAdmin(12345678, cnx)
    #cursor = db.fetchFromWhere("SELECT * FROM UserBrainData WHERE userID = 10")

    
if __name__ == '__main__':
    main()