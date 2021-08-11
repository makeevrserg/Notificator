

import firebase_admin
from pyfcm import FCMNotification
import urllib.request
import json
import firebase_admin.messaging as messaging
from firebase_admin import credentials
API_KEY = 'AAAA0aqNG2o:APA91bE1qLhRRYbJ9CufZEQLvMbkUB8S9Ij1xUcbZ009_zuwnQnz197vX24LWsjmN8J8ni7WquK7yyRK8GFpYfpCxpq4gTjAbUEDagvHsUthrGkHJ-vDqUwTZ1neKLe3qeSNmir4zadE '
import datetime
# Send to single device.





#Получение актуальной температуры города
def get_weather() -> dict:
    with urllib.request.urlopen("https://api.openweathermap.org/data/2.5/weather?q=Rostov-on-Don,%20Rostov%20Oblast,%20Russia&appid=3e3eddc996fca6b3281a545d47b5463c&units=metric&lang=ru") as url:
        data = json.loads(url.read().decode())
        return data


def getClouds(weather):
    return weather['weather'][0]['description'].capitalize()


def getTemp(weather):
    return weather['main']['temp']


def getTempFeel(weather):
    return weather['main']['feels_like']


def getWind(weather):
    return weather['wind']['speed']

def send_notification(message_title, message_body, data_message, click_action):
    push_service = FCMNotification(api_key=API_KEY)

    result = push_service.notify_topic_subscribers(
        topic_name="weather",
        message_title=message_title,
        message_body=message_body,
        data_message=data_message,
        click_action=click_action)

#Отправляет уведомление
#MyFirebaseMessagingService вызывается только когда приложение открыто        
def sendWeatherNotificaton():
    weather = get_weather()
    send_notification(
        "Ростов-на-Дону: {}°".format(getTemp(weather)),
        "Ощущается как {}°. {}.".format(getTempFeel(weather), getClouds(weather)),
        {
            "color": '#3897e0',
            'title':"Ростов-на-Дону: {}°".format(getTemp(weather)),
            'body':"Ощущается как {}°. {}.".format(getTempFeel(weather), getClouds(weather)),
        },
        'FromNotificationActivity'
    )


#Отправляет уведомление.
#Если не указывать notification, то MyFirebaseMessagingService будет вызываться.
#Если указать notification, то не будет вызван. Отрпвитс сразу в Notification Tray
def sendMessage():
    cred = credentials.Certificate(
        "key.json")
    firebase_admin.initialize_app(cred)
    topic = 'weather'
    weather = get_weather()
    # See documentation on defining a message payload.
    message = messaging.Message(
        data={
            "color": '#25bd22',
            'title':"Ростов-на-Дону: {}°".format(getTemp(weather)),
            'body':"Ощущается как {}°. {}.".format(getTempFeel(weather), getClouds(weather)),
            'large_icon':"https://img.poki.com/cdn-cgi/image/quality=78,width=600,height=600,fit=cover,g=0.5x0.5,f=auto/b5bd34054bc849159d949d50021d8926.png"
        },
    
        # notification=messaging.Notification(
        #     title="Ростов-на-Дону: {}°".format(getTemp(weather)),
        #     body="Ощущается как {}°. {}.".format(getTempFeel(weather), getClouds(weather)),
        # ),


        topic=topic,
    )
    messaging.send(message)


def main():
    #sendWeatherNotificaton()
    sendMessage()


if __name__ == '__main__':
    main()
