from linebot import LineBotApi
from linebot.models import TextSendMessage, TemplateSendMessage, ButtonsTemplate, CarouselTemplate, CarouselColumn, \
    PostbackTemplateAction, ConfirmTemplate, ImageSendMessage
from linebot.exceptions import LineBotApiError
import setting

REPLY_ENDPOINT = 'https://api.line.me/v2/bot/message/reply'
PUSH_ENDPOINT = 'https://api.line.me/v2/bot/message/push'


# reply messageを送る
def reply_message(reply_token, text):
    line_bot_api = LineBotApi(setting.ACCESS_TOKEN)
    # print(text)
    try:
        if not isinstance(text, (list, tuple)):
            text = [text]
        line_bot_api.reply_message(reply_token, [TextSendMessage(text=t) for t in text])
    except LineBotApiError as e:
        print('error')
        print(e)

def reply_template_carousel(reply_token, text, alt_text, id):
    line_bot_api = LineBotApi(setting.ACCESS_TOKEN)
    line_bot_api.reply_message(reply_token, [TemplateSendMessage(alt_text=alt_text, template=CarouselTemplate([
        CarouselColumn(text='星5', actions=[PostbackTemplateAction(label='星5', data='5 '+id)]),
        CarouselColumn(text='星4', actions=[PostbackTemplateAction(label='星4', data='4 '+id)]),
        CarouselColumn(text='星3', actions=[PostbackTemplateAction(label='星3', data='3 '+id)]),
        CarouselColumn(text='星2', actions=[PostbackTemplateAction(label='星2', data='2 '+id)]),
        CarouselColumn(text='星1', actions=[PostbackTemplateAction(label='星1', data='1 '+id)]),
    ]))])

def reply_template_confirm(reply_token, text, alt_text, actions):
    line_bot_api = LineBotApi(setting.ACCESS_TOKEN)
    line_bot_api.reply_message(reply_token, [TemplateSendMessage(alt_text=alt_text, template=ConfirmTemplate(
        actions=actions
    ))])

def reply_template_button(reply_token, text, alt_text, choices):
    line_bot_api = LineBotApi(setting.ACCESS_TOKEN)
    line_bot_api.reply_message(reply_token, [TemplateSendMessage(alt_text, ButtonsTemplate(text, actions=
    [{
        "type":'{0} {1}'.format(e),
        "label":"ええよ",
        "data":"yes",
        "text":'{0} {1}'.format(e)
        } for e in choices
    ]))])




# push messageを送る
def push_message(id, text):
    line_bot_api = LineBotApi(setting.ACCESS_TOKEN)
    # print(text)
    try:
        if not isinstance(text, (list, tuple)):
            text = [text]
        line_bot_api.push_message(id, [TextSendMessage(text=t) for t in text])
    except LineBotApiError as e:
        print('error')
        print(e)

def push_image(id, image_url, image_url_preview):
    line_bot_api = LineBotApi(setting.ACCESS_TOKEN)
    line_bot_api.push_message([ImageSendMessage(original_content_url=image_url, preview_image_url=image_url_preview)])

