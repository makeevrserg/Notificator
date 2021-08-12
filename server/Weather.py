

import urllib.request
import json
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