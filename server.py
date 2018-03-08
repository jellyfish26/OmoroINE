from bottle import *
from module import *
import json
from linebot import webhook
import setting
import main

@post('/')
def line_post():
    signature = request.get_header('X-Line-Signature')
    body = request.body.read().decode('utf-8')

    events = webhook.WebhookParser(setting.CHANNEL_SECRET).parse(body, signature)
    main.main(events)

    body = json.dumps({})
    r = HTTPResponse(status=200, body=body)
    r.set_header('Content-Type', 'application/json')

@get('/')
def index():
    return ''' Hello World! '''

@get('/<name>')
def tekito(name):
    return name

# API
# 駅情報
@post('/api/station')
def postStationData():
    json_data = [request.json['LocalCode'], request.json['LineCode'], request.json['CodeStation']]
    print(json_data)
    return module_db.returnData(json_data)

# データベースに情報登録
@post('/api/register')
def postRegistData():
    try:
        json_data = ['"{}"'.format(request.json['line']), '"{}"'.format(request.json['station']), '"{}"'.format(request.json['type']), '"{}"'.format(request.json['value']), '"{}"'.format(request.json['rating_count'])] 
    except:
        return 'failure'
    if add(json_data):
        return 'success'
    else:
        return 'failure'

# PATH設定
# 送られてきた画像を保存
@route('/img/<filename>')
def route_img(filename):
    return static_file(filename, root='contents/user/img')

@route('/img/preview/<filename>')
def route_img_preview(filename):
    return static_file(filename, root='contents/system/preview')

@route('/css/<filename>')
def route_css(filename):
    return static_file(filename, root='css/', mimetype='text/css')

@route('/js/<filename>')
def route_js(filename):
    return static_file(filename, root='js/', mimetype='text/javascript')

@route('/fonts/<filename>')
def route_fonts(filename):
    return static_file(filename, root='fonts')

@route('/images/<filename>')
def route_images(filename):
    return static_file(filename, root='views/images/')

run(host='0.0.0.0', port=4490, debug=True, reloader=True)

