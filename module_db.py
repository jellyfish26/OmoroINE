import sqlite3, json

# 駅情報を検索
def getStation(Search):
    result = access("SELECT * from StationCode where LocalCode = '{}' and LineCode = '{}' and CodeStation = '{}';".format(Search[0], Search[1], Search[2]), 'db/stationCode.db')
    return result

# 駅データを返却
def returnData(data):
    try:
        result = getStation(data)
        out = []
        for i in range(3,7):
            out.append(result[0][i])
        dict_g = {'Company' : out[0],'LineName' : out[1] ,'StatineName' : out[2],'Memo' : out[3]}
    except:
        dict_g = {}
        return json.dumps(dict_g)
    return json.dumps(dict_g, ensure_ascii=False, indent=4)

# SQLiteクエリ実行
def access(query, db_name):
    connection = sqlite3.connect(db_name)
    cursor = connection.cursor()
    result = cursor.execute(query).fetchall()
    connection.commit()
    cursor.close()
    connection.close()
    return result
