import json
import ast
from flask import Flask
from flask import abort, redirect, url_for
import unicodedata
from process_for_DTW import process_for_DTW
from process_for_DTW import registerUserBrainwave
from process_for_DTW import registerUSerInfo

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

    if intent == 'LOGIN':
        print 'login intent'
        #should authorize
        #call dtw method
        
        user_id = authorize_brain_wave(arr, id)
        result['ID'] = user_id    

    elif intent == 'REGISTER':
        print 'register intent'
        #just save in the DB
        #call db_processing
        print 'type of data-->', type(arr[0]), type(arr[0][0])
        print 'size of list--->', len(arr[0]), len(arr[1])
        id = registerUserBrainwave(arr[0], arr[1], id)

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
            result['status'] = 'success'

        else:
            result['status'] = 'failure'        
    
    else:
        result['status'] = 'failure'
    result_data = json.dumps(result)
    print result_data
    #return result
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
    return True

def insert_data(data):
    name = data["NAME"]
    age = data["AGE"]
    gender = data["GENDER"]
    userId = registerUSerInfo(name, age, gender)
    return userId

def authorize_brain_wave(data, id):

    print 'type of data-->', type(data[0]), type(data[0][0])
    result = process_for_DTW(data[0], data[1], id)
    print 'result from process_DTW->', result
    return result