# BrainNet

Android App for demonstratng authentication using Brainwave (EEG ) signals

## Project motovation and set up details

https://peps0791.github.io/braiNet-password-less-authentication/

## Project Set up

### 1. Set up the  database.


Set up a M   **1. Database server setup.**
YSQL dataase server on a machine.

   **2. Schema  set up.**

Pending....

### 2. Install the android application.

### 3. Set up the web server.

**1. Install the environment dependencies.**

    Python version-> 2.7

Run the command
    
    pip install -r requirements.txt

to install the environment library dependencies.

**2. Fire up the server.**

Run the python server using the command
    
    FLASK_APP=server.py flask run --host 0.0.0.0

### 4. Connect the device to the phone.

1. Start the bluetooth service on the phone.

2. Connect the device to the phone via bluetooth.
