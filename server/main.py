import line_api
import mainDB
import tempDB
from linebot import LineBotApi
import setting
import module
from linebot.models import PostbackTemplateAction


def main(events):
    for event in events:
        event_dict = event.as_json_dict()
        event_type = event.type
        # 送られてきたイベントタイプにわけてそれぞれのメソッドを呼び出す
        if event_type == 'message':
            message(event_dict)
        elif event_type == 'follow':
            pass
        elif event_type == 'unfollow':
            pass
        elif event_type == 'join':
            join(event_dict)
        elif event_type == 'leave':
            leave(event_dict)
        elif event_type == 'postback':
            postback(event_dict)
        elif event_type == 'beacon':
            pass


# メッセージが送られてきた時に呼ばれる
def message(event_dict):
    # 位置情報が送られてきた時
    if event_dict['message']['type'] == 'location':
        station = module.isStation((event_dict['message']['latitude'],event_dict['message']['longitude']))
        if not station:
            line_api.reply_message(event_dict['replyToken'], '近くに駅が存在しません')
            return
        if len(station) > 5:
            station = station[:4]

        line_api.reply_template_button(event_dict['replyToken'], '駅を選択して下さい', '駅を選択して下さい', station)
        return

    # 画像が送られてきた時
    if event_dict['message']['type'] == 'image':
        l = LineBotApi(setting.ACCESS_TOKEN)
        content = l.get_message_content(event_dict['message']['id'])
        uuid = str(module.issueUnique())
        file_path = 'contents/user/img/'+uuid+'.png'
        with open(file_path, 'wb') as fd:
            for chunk in content.iter_content():
                fd.write(chunk)
        tempDB.add(id=event_dict['source']['userId'], type='image', value=uuid+'.png', timestamp=event_dict['timestamp'])

    # テキストが送られてきた時
    elif event_dict['message']['type'] == 'text':
        tempDB.add(id=event_dict['source']['userId'], type='text', value=event_dict['message']['text'], timestamp=event_dict['timestamp'])
    line_api.reply_message(reply_token=event_dict['replyToken'], text='位置情報を送信してください')


# グループ・ルームに招待参加した時に呼ばれる
def join(event_dict):
    pass

# グループ・ルームから退出した時に呼ばれる
def leave(event_dict):
    pass

# ポストバックイベントが呼ばれた時に呼ばれる
def postback(event_dict):
    data = event_dict['postback']['data'].split()
    if data[0] == 'select':
        if tempDB.get(event_dict['source']['userId']):
            tempDB.add_location(event_dict['source']['userId'], f'{data[1]} {data[2]}')
            line_api.reply_template_confirm(event_dict['replyToken'], '登録しますか?', '登録しますか?', [
                PostbackTemplateAction(label='ええよ', data='yes'),
                PostbackTemplateAction(label='ないわ', data='no'),
            ])
            return
        else:
            data = data[1:]
            result = mainDB.random_select(data[0], data[1])
            if result is None:
                line_api.reply_message(event_dict['replyToken'], 'この駅にはまだ何も登録されていません')
            if result[3] == 'text':
                line_api.push_message(event_dict['source']['userId'], result[4])
            elif result[3] == 'image':
                module.GeneratPreview('contents/user/img/'+result[4])
                line_api.push_image(event_dict['source']['userId'], 'https://trompot.mydns.jp/hack/img/'+result[4], 'https://trompot.mydns.jp/hack/img/preview/'+result[4])
            line_api.reply_template_carousel(event_dict['replyToken'], 'どうでしたか?', 'どうでしたか?', str(result[0])+' '+tempDB.issue_token(result[0]))

    id = 0
    token = ''
    if len(data) == 3:
        id = data[1]
        token = data[2]
        if not tempDB.isvalid_token(id, token):
            line_api.reply_message(event_dict['replyToken'], 'すでに回答されています')
            return
    data = data[0]
    if data == '5':
        mainDB.update(id, 5)
        line_api.reply_message(event_dict['replyToken'], '評価ありがとうございます')
        tempDB.delete_token(id, token)
    elif data == '4':
        mainDB.update(id, 4)
        line_api.reply_message(event_dict['replyToken'], '評価ありがとうございます')
        tempDB.delete_token(id, token)
    elif data == '3':
        mainDB.update(id, 3)
        line_api.reply_message(event_dict['replyToken'], '評価ありがとうございます')
        tempDB.delete_token(id, token)
    elif data == '2':
        mainDB.update(id, 2)
        line_api.reply_message(event_dict['replyToken'], '評価ありがとうございます')
        tempDB.delete_token(id, token)
    elif data == '1':
        mainDB.update(id, 1)
        line_api.reply_message(event_dict['replyToken'], '評価ありがとうございます')
        tempDB.delete_token(id, token)
    elif data == 'yes':
        data = tempDB.get(event_dict['source']['userId'])[0]
        location = tempDB.get_location(event_dict['source']['userId'])[0][1].split()
        mainDB.add((location[0], location[1], data[1], data[2]))
        line_api.reply_message(event_dict['replyToken'], '登録しました')
        tempDB.delete(event_dict['source']['userId'])
        tempDB.delete_location(event_dict['source']['userId'])
    elif data == 'no':
        tempDB.delete(event_dict['source']['userId'])
        tempDB.delete_location(event_dict['source']['userId'])
