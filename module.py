import json, requests, os

# 位置情報から線と駅情報を取得
def isStation(position):
    # APIから情報を取得
    url = ('http://express.heartrails.com/api/json?method=getStations&x={}&y={}'.format(position[1], position[0]))
    response = (requests.get(url)).json()
    out = []
    # 駅名と線情報を取得
    # リストに書き込み
    for station in response['response']['station']:[
        out.append((station['line'], station['name']))
    ]
    return out


if __name__ == '__main__':
    position = [34.697164, 135.508032]
    result = isStation(position)
    print(result)
