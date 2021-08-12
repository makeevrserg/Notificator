import os
from firebase_admin.messaging import AndroidFCMOptions
from flask import Flask, Response
from flask import request
import flask
import Firebase as Firebase
import data
app = Flask(__name__)

#Подобие api для тестовых уведомлений

#Отправляет пользователю уведомление о погоде
@app.route('/getWeather', methods=['POST'])
def getWeather():
    args = request.args
    if 'token' not in args or args['token'] is None:
        return {'result': 'Token is null'}
    Firebase.send_weather_message(args['token'])
    return {'result': 'ok'}



#Отправляет пользователю уведомление с его именем
@app.route('/getName', methods=['POST', 'GET'])
def getName():
    args = request.args
    if 'token' not in args or 'name' not in args:
        return {'result': 'Not enough arguments'}
    Firebase.send_message(data={
        "color": '#25bd22',
        'title': "Ваше имя: {}°".format(args['name']),
        'large_icon': "https://img.poki.com/cdn-cgi/image/quality=78,width=600,height=600,fit=cover,g=0.5x0.5,f=auto/b5bd34054bc849159d949d50021d8926.png"
    }, token=args['token'])
    return {'result': 'ok'}


#Фукнция которая возвращает None если объекта ключа нет
def get_key(key, map):
    if key not in map:
        return None
    return map[key]


#Отправляет уведомление с ошибкой
@app.route('/error', methods=['POST', 'GET'])
def send_error():
    args = request.args
    print(args)
    Firebase.send_notification(
        token=get_key('token', args),
        message_title=get_key('title', args),
        message_body=get_key('body', args),
        data_message=get_key('data', args)
    )
    return {'result': 'ok'}


#Регистрация нового пользователя.
#Запис данных происходит в config.yml data.py
@ app.route('/register', methods=['POST'])
def register():
    args = request.args
    if 'name' not in args:
        return {'result': 'Wrong parameters! name is required!'}
    result = data.createUser(args['name'])
    Firebase.send_message(data={
        "color": '#18ad59',
        'title': result['result'],
        'body': "Имя: {}.".format(args['name']),
        'large_icon': "https://img.poki.com/cdn-cgi/image/quality=78,width=600,height=600,fit=cover,g=0.5x0.5,f=auto/b5bd34054bc849159d949d50021d8926.png"
    }, token=get_key('token', args))
    return {'result': 'ok'}


if __name__ == '__main__':
    app.run(debug=True, host="192.168.1.142", port=5000)
