import sqlite3
import secrets


def add(id, type, timestamp, value):
    try:
        access('INSERT INTO temp(line_id, type, value, timestamp) VALUES {}'.format((id, type, value, timestamp)))
    except sqlite3.IntegrityError:
        access("DELETE FROM temp WHERE line_id == '{}'".format(id))
        access('INSERT INTO temp(line_id, type, value, timestamp) VALUES {}'.format((id, type, value, timestamp)))

def add_location(id, station):
    try:
        access('INSERT INTO temp_location(line_id,station) VALUES {}'.format((id, station)))
    except sqlite3.IntegrityError:
        access("DELETE FROM temp_location WHERE line_id == '{}'".format(id))
        access('INSERT INTO temp_location(line_id,station) VALUES {}'.format((id, station)))

def get(id):
    return access("SELECT * FROM temp WHERE line_id == '{}'".format(id))

def get_location(id):
    return access("SELECT * FROM temp_location WHERE line_id == '{}'".format(id))

def delete(id):
    access("DELETE FROM temp WHERE line_id == '{}'".format(id))

def delete_location(id):
    access("DELETE FROM temp_location WHERE line_id == '{}'".format(id))

def issue_token(id):
    token = secrets.token_hex()
    access('''INSERT INTO rating_token(line_id, token) VALUES {}'''.format((id, str(token))))
    return token

def isvalid_token(id, token):
    result = access('''SELECT * FROM rating_token WHERE line_id == "{}" AND token == "{}"'''.format(id, token))
    if result:
        return True
    return False

def delete_token(id, token):
    access("DELETE FROM rating_token WHERE line_id == '{}' AND token == '{}'".format(id, token))


def access(query):
    connection = sqlite3.connect('db/temp.db')
    cursor = connection.cursor()
    try:
        result = cursor.execute(query).fetchall()
    except sqlite3.IntegrityError:
        cursor.close()
        connection.close()
        raise sqlite3.IntegrityError

    connection.commit()
    cursor.close()
    connection.close()
    return result

def init_temp_db():
    # access('''CREATE TABLE temp(line_id TEXT NOT NULL,type TEXT NOT NULL ,value TEXT NOT NULL,timestamp INTEGER NOT NULL, PRIMARY KEY(line_id));''')
    # access('''CREATE TABLE temp_location(line_id TEXT NOT NULL PRIMARY KEY ,station TEXT NOT NULL);''')
    access('''CREATE TABLE rating_token(line_id TEXT NOT NULL PRIMARY KEY ,token TEXT NOT NULL);''')


if __name__ == '__main__':
    init_temp_db()
