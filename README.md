# BrainNet

Android App for demonstratng authentication using Brainwave (EEG ) signals

## Project motovation and set up details

https://peps0791.github.io/braiNet-password-less-authentication/

## Project Set up

### 1. Set up the  database.

1. Install MYSQL server on you local machine.
2. Run the /Resources/FlaskServer/dbsetup.sql script to set up the database schema.

### 2. Set up the web server.

**1. Install the environment dependencies.**

    Python version-> 2.7

Run the command
    
    pip install -r requirements.txt

to install the environment library dependencies.

**2. Modify the database details.**

Open the /Resources/FlaskServer/DBHelper.py file and at line

    cnx = mysql.connector.connect(user='root', password='password',
                                          host='127.0.0.1',
                                          database='BrainNet')

enter the password for the root user for your database set up in step 1 and change the host value accordingly. 

**2. Fire up the server.**

Run the python server using the command
    
    FLASK_APP=server.py flask run --host 0.0.0.0

### 3. Start the bluetooth service on the phone.

### 4. Install the android application.

1. Open the project in Android Studio.
2. Edit the HOST variable to your local machine IP in the file com.neurosky.mindwavemobiledemo.helper.Constants.java.
3. Run the application.
