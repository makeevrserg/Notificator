

import firebase_admin
from pyfcm import FCMNotification
import firebase_admin.messaging as messaging
from firebase_admin import credentials
import Weather
# Инициализация ключа для работы firebase_admin и FCM
API_KEY = 'AAAA0aqNG2o:APA91bE1qLhRRYbJ9CufZEQLvMbkUB8S9Ij1xUcbZ009_zuwnQnz197vX24LWsjmN8J8ni7WquK7yyRK8GFpYfpCxpq4gTjAbUEDagvHsUthrGkHJ-vDqUwTZ1neKLe3qeSNmir4zadE '
cred = credentials.Certificate(
    "key.json")
firebase_admin.initialize_app(cred)


# Отправляет уведомление
# MyFirebaseMessagingService вызывается только когда приложение открыто
def send_weather_notification():
    weather = Weather.get_weather()
    send_notification(
        "Ростов-на-Дону: {}°".format(Weather.getTemp(weather)),
        "Ощущается как {}°. {}.".format(
            Weather.getTempFeel(weather), Weather.getClouds(weather)),
        {
            "color": '#3897e0',
            'title': "Ростов-на-Дону: {}°".format(Weather.getTemp(weather)),
            'body': "Ощущается как {}°. {}.".format(Weather.getTempFeel(weather), Weather.getClouds(weather)),
        },
        'FromNotificationActivity'
    )


# Отправляет уведомление.
# Если не указывать notification, то MyFirebaseMessagingService будет вызываться.
# Если указать notification, то не будет вызван. Отрпвитс сразу в Notification Tray
def send_message(data, token=None, topic=None):
    message = messaging.Message(
        data=data,
        token=token,
        topic=topic,
    )
    messaging.send(message)


def send_notification(topic=None, token=None, message_title=None, message_body=None, data_message=None, click_action=None):
    push_service = FCMNotification(api_key=API_KEY)
    if (token is None):
        push_service.notify_topic_subscribers(
            topic_name=topic,
            message_title=message_title,
            message_body=message_body,
            data_message=data_message,
            click_action=click_action)
    else:
        push_service.notify_single_device(
            registration_id=token,
            message_title=message_title,
            message_body=message_body,
            data_message=data_message,
            click_action=click_action)



def send_weather_message(token=None):
    weather = Weather.get_weather()
    data = {
        "color": '#25bd22',
        'title': "Ростов-на-Дону: {}°".format(Weather.getTemp(weather)),
        'body': "Ощущается как {}°. {}.".format(Weather.getTempFeel(weather), Weather.getClouds(weather)),
        'large_icon': "https://img.poki.com/cdn-cgi/image/quality=78,width=600,height=600,fit=cover,g=0.5x0.5,f=auto/b5bd34054bc849159d949d50021d8926.png"
    }
    if token is None:
        send_message(data=data, topic='weather')
    else:
        send_message(data=data, token=token)
