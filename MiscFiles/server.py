import json
from flask import Flask
from flask import abort, redirect, url_for

app = Flask(__name__)

@app.route('/')
def root():
    return redirect(url_for('home'))

@app.route('/home')
def home():
    return 'Welcome to home screen'

@app.route('/authenticate/<jsonStr>')
def authenticate(jsonStr):
    #jsonStr = '{"first_name": "Guido", "last_name":"Rossum"}'
    print 'incoming json string-->', jsonStr
    #validate the function argument
    #parsed_json = json.loads(jsonStr)
    #print parsed_json	
    return jsonStr
