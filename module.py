import json, requests, uuid
from PIL import Image
import numpy as np

# 位置情報から線と駅情報を取得
def isStation(position):
    # APIから情報を取得
    url = ('http://express.heartrails.com/api/json?method=getStations&x={}&y={}'.format(position[1], position[0]))
    response = (requests.get(url)).json()
    out = []
    # 駅名と線情報を取得
    # リストに書き込み
    # for station in response['response']['station']:
    #     out.append((station['line'], station['name']))
    out = [(station['line'], station['name']) for station in response['response']['station']]
    return out

# ユニークID発行
def issueUnique():
    uniqueId = uuid.uuid4()
    return uniqueId

# Preview作成
def GeneratPreview(img_url):
    try:
        img = Image.open(img_url, 'r')
        path_list = img_url.split('/')
        if len(path_list) == 0:
            file_name = path_list[0]
        else:
            file_name = path_list[len(path_list)-1]
        extension_list = file_name.split('.')
        extension = extension_list[len(extension_list)-1]
        resize_img = img.resize((200, 200))
        resize_img.save('contents/system/preview/{}'.format(file_name), extension, quality=100, optimize=True)
    except:
        return False
    return True

if __name__ == '__main__':
    position = [34.697164, 135.508032]
    result = isStation(position)
    print(result)
    print(issueUnique())
