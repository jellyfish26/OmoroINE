import sqlite3, json, random


# 駅情報を検索
def getStation(Search):
    result = access(
        "SELECT * from StationCode where LocalCode = '{}' and LineCode = '{}' and CodeStation = '{}';".format(Search[0],Search[1],Search[2]),'db/stationCode.db')
    return result


# 駅データを返却
def returnData(data):
    try:
        result = getStation(data)
        out = []
        for i in range(3, 7):
            out.append(result[0][i])
        dict_g = {'Company': out[0], 'LineName': out[1], 'StationName': out[2], 'Memo': out[3]}
    except:
        dict_g = {}
        return json.dumps(dict_g)
    return json.dumps(dict_g, ensure_ascii=False, indent=4)


# SQLiteクエリ実行
def access(query, db_name):
    print(query)
    connection = sqlite3.connect(db_name)
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
    # print(result)
    return result


# データ追加
def add(data):
    try:
        access(
            '''INSERT into main(line, station, type, value, rating, rating_count) values("{}", "{}", "{}", "{}", 3, 1);'''.format(data[0],data[1],data[2],data[3]),'db/main.db')
        return True
    except:
        return False


def random_select(line, station):
    try:
        arts = access('''SELECT * FROM main WHERE line=={} AND station=={}'''.format(line, station), 'db/main.db')
        return random.choice(arts)
    except:
        return None


def update(u_id, rating):
    try:
        r = access('''SELECT rating, rating_count FROM main WHERE u_id=={}'''.format(u_id), 'db/main.db')[0]
        rating = (r[0]*r[1]+rating)/(r[1]+1)
        access('''UPDATE main SET rating = {}, rating_count = {} WHERE u_id=={}'''.format(rating, r[1]+1, u_id), 'db/main.db')
        return True
    except:
        return False


# 初期化
def init_main_db():
    access('''CREATE TABLE main(
        u_id INTEGER PRIMARY KEY,
        line TEXT NOT NULL , 
        station TEXT NOT NULL, 
        type TEXT NOT NULL, 
        value TEXT NOT NULL,
        rating REAL NOT NULL,
        rating_count INTEGER NOT NULL);'''
           , 'db/main.db')


if __name__ == '__main__':
    # init_main_db()
    data = ['"橿原線"', '"近鉄郡山駅"', '"text"', '"古い"', '1']
    add(data)

    # u_id INTEGER PRIMARY KEY,