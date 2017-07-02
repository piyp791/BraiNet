# -*- coding: utf-8 -*-
"""
Created on Sat Jul 01 01:09:43 2017

@author: Shalmali
"""

import mysql.connector
from mysql.connector import errorcode
import logging

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
     def insertInto(self,tablename, name, gender, age, cnx):
        add_user = ("INSERT INTO " + tablename + 
                    "(Name, Gender, Age) VALUES ( %s, %s, %s)")
        user_data = (name, gender, age)
        cursor = cnx.cursor()
        try:
            cursor.execute(add_user, user_data)
            cnx.commit()
            print ("Data inserted successfully")
        except:
            print ("Data insertion failed!!!")
        cursor.close()
        
     # 
     # Helper method for Data fetching from a tablebased on a condition
     # condExpr takes series of conditions joined by logical operators
     #      
     def fetchFromWhere(self, tablename, condExpr, cnx):
        select_user = ("SELECT * FROM " + tablename + " WHERE " + condExpr)
        cursor = cnx.cursor()
        try:
            cursor.execute(select_user)
            print ("Data Fetching successful")
        except:
            print ("Data Fetching failed!!!")
        return cursor.fetchall()
        cursor.close()
        
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

def main():
    db = DBHelper()
    cnx = db.getConn()
    #results = db.fetchFromWhere('UserInfo', 'Name = \'ABC\'', cnx)
    print db.checkIfAdmin(12345678, cnx)
    
if __name__ == '__main__':
    main()