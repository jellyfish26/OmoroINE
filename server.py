from bottle import *

@post('/')
def top():
    return ''' Hello World! '''

@get('/')
def index():
    return ''' Hello World! '''

@get('/<name>')
def tekito(name):
    return name
    

run(host='0.0.0.0', port=4490, debug=True)