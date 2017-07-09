import json
import ast
from flask import Flask
from flask import abort, redirect, url_for
import unicodedata
from process_for_DTW import process_for_DTW
from authenticateML import authenticateML
from process_for_DTW import registerUserBrainwave
from process_for_DTW import registerUSerInfo
import pywt
from DBHelper import DBHelper
from DBHelper import NumpyMySQLConverter

app = Flask(__name__)

@app.route('/')
def root():
    return redirect(url_for('home'))

@app.route('/home')
def home():
    return 'Welcome to home screen'

@app.route('/sendWave/<string:jsonStr>')
def sendWave(jsonStr):
    print 'incoming string-->', jsonStr
    json_obj = parse_data(jsonStr)
    intent = json_obj['INTENT']

    data = json_obj['DATA']
    #arr = ast.literal_eval(new_working_str)
    arr = eval(data)
    print arr
    id = json_obj['ID']
   
    result = {}

    #cA, cD = pywt.dwt(arr, 'db1')
    #cA = list(cA)

    if intent == 'LOGIN':
        print 'login intent'
        #should authorize
        #call dtw method
        
        is_authorized = authorize_brain_wave(arr, id)
        if is_authorized == True:
            result['is_authorized'] = True
        else:
            result['is_authorized'] = False

    elif intent == 'REGISTER':
        print 'register intent'
        #just save in the DB
        #call db_processing
        print 'type of data-->', type(arr), type(arr[0])
        print 'size of list--->', len(arr)


        #apply the wavelet transform
        id = registerUserBrainwave(arr, id, sessionId)

    else:
        print 'unknown intent'
        result['status'] = 'failure'
    
    
    result['status'] = 'success'
    result_data = json.dumps(result)
    print result_data
    return result_data


@app.route('/validateID/<string:jsonStr>')
def validateID(jsonStr):

    result = {}
    print 'incoming string--> ', jsonStr
    json_obj = parse_data(jsonStr)
    intent = json_obj['INTENT']
    if intent == 'LOGIN':
        print 'login intent'
        #authorize user id
        id = json_obj['ID']
        #id authorization code
        if authorize_user_id(id) == True:

            if is_admin(id) == True:
                result['is_admin'] = 'yes'
            else:
                result['is_admin'] = 'no'

            result['status'] = 'success'

        else:
            result['status'] = 'failure'        
    else:
        result['status'] = 'failure'
    result_data = json.dumps(result)
    print result_data
    #return result
    return result_data


@app.route('/search/<string:jsonStr>')
def search(jsonStr):

    result = {}
    print 'incoming string--> ', jsonStr
    json_obj = parse_data(jsonStr)
    #authorize user id
    id = json_obj['ID']
    sessionid = json_obj['SESSIONID']

    data = search(id, sessionid)
    if data == False:
        result['status'] = 'failure'
    else:
        result['status'] = 'success'        
    
    result['BRAINWAVE'] = data
    result_data = json.dumps(result)
    print result_data
    return result_data

@app.route('/fetchSessions/<string:jsonStr>')
def fetchSessions(jsonStr):

    result = {}
    print 'incoming string--> ', jsonStr
    json_obj = parse_data(jsonStr)
    #authorize user id
    
    user_data = fetchSessionID(json_obj)
    if user_data!= None:
        result['status'] = 'success'
        result['userdata'] = user_data
    else:
        result['status'] = 'failure'
   
    result_data = json.dumps(result)
    return result_data
   


@app.route('/register/<string:jsonStr>')
def register(jsonStr):
    print 'incoming string--> ', jsonStr
    result = {}

    json_obj = parse_data(jsonStr)
    intent = json_obj['INTENT']

    if intent == 'REGISTER':

        #insert data into DB
        data = json_obj['DATA']

        userid = insert_data(data)
        #get a user id
        result['userid'] = userid
        result['status'] = 'success'

    else:
        result['status'] = 'failure'

    result_data = json.dumps(result)
    print result_data
    return result_data


def parse_data(jsonStr):
    new_working_str = jsonStr.encode('ascii', 'ignore')
    print 'working str', new_working_str
    json_obj = json.loads(new_working_str)
    print json_obj
    return json_obj

def process_data(jsonStr):
   
    new_working_str = jsonStr.encode('ascii', 'ignore')
    print 'working str', new_working_str
    json_obj = json.loads(new_working_str)
    print json_obj
    intent = json_obj['INTENT']
    print intent
    data = json_obj['DATA']
    #arr = ast.literal_eval(new_working_str)
    arr = eval(data)
    print arr[0]
    return intent, arr

def authorize_user_id(id):

    db = DBHelper()
    cnx = db.getConn()
    cnx.set_converter_class(NumpyMySQLConverter)

    condExpr = 'ID = ' + str(id)
    cursor = db.fetchFromWhere("UBrainData", condExpr, cnx)
    if cursor.rowcount != 0:
        return True
    else:
        return False;


def insert_data(data):
    name = data["NAME"]
    age = data["AGE"]
    gender = data["GENDER"]
    userId = registerUSerInfo(name, age, gender)
    return userId

def authorize_brain_wave(data, id):

    print 'type of data-->', type(data), type(data[0])
    result = authenticateML(data,id)
    #result = process_for_DTW(data, id)
    print 'result from process_DTW->', result
    return result

def search(id, sessionid):

    db = DBHelper()
    cnx = db.getConn()
    cnx.set_converter_class(NumpyMySQLConverter)

    condExpr = 'ID = ' + str(id) + ' AND SESSIONID = ' + str(sessionid)
    cursor = db.fetchFromWhere("UBrainData", condExpr, cnx)
    if cursor.rowcount != 0:
        series_list = cursor.fetchall()

    data = []
    for row in series_list:
        data.append(float(row[3]))
    return data

def fetchSessionID(json_obj):

    search_str = ''
    try:
        id = json_obj["ID"]
        search_str+='UserID = ' + str(id) + ' AND'
    except:
        id = None
    try:
        name = json_obj["NAME"]
        search_str+=' NAME = \"' + str(name) + '\" AND'
    except:
        name = None
    try:
        age = json_obj["AGE"]
        search_str+=' AGE = ' + age + ' AND'
    except:
        age = None
    try:
        gender = json_obj["GENDER"]
        search_str+=' GENDER = \"' + str(gender) + '\"'
    except:
        gender = None

    if search_str.endswith('AND'):
        search_str = search_str[:-3] 


    print " id->", id, " name->", name, " age-> ", age, " gender->", gender

    db = DBHelper()
    cnx = db.getConn()
    cnx.set_converter_class(NumpyMySQLConverter)

    condExpr = search_str
    print 'cond expr->', condExpr
    cursor = db.fetchColFromWhere("UserInfo", "*", condExpr, cnx)
    if cursor.rowcount != 0:
        userInfo = cursor.fetchone()

    print 'user info-->', userInfo

    userid = userInfo[0]
    #fetch sessions
    condExpr = ' ID = ' + str(userid)
    cursor = db.fetchColFromWhere("UBrainData", "distinct SessionID", condExpr, cnx)

    if cursor.rowcount != 0:
        sessionID_list = cursor.fetchall()

    data = []
    for row in sessionID_list:
        data.append(row[0])
    print 'data->', data

    result = {}
    result['userInfo'] = userInfo
    result['data'] = data
    result_data = json.dumps(result)

    return result_data

def is_admin(id):
    db = DBHelper()
    cnx = db.getConn()
    if db.checkIfAdmin(id, cnx) == 1:
        return True
    else:
        return False