from yaml import load, dump
from yaml import CLoader as Loader, CDumper as Dumper
import os

#Все конфиги из config.yml
global config

#Сохранение файла
def saveConfig():
    global config
    with open('config.yml', 'w') as file:
        dump(config,file)
#Создание файла если его нет
def create_file():
    with open('config.yml','a+'):
        return
#Загрузка файла
def loadConfig():
    global config
    create_file()
    with open('config.yml', 'r') as file:
        config = load(file.read(), Loader=Loader)
        if config is None:
            config={'init': True}
            saveConfig()
        return config

#Создание пользователя
def createUser(name):
    if 'users' not in config:
        config['users'] = [name]
        saveConfig()
        loadConfig()
    else:
        user_list = config['users']
        if name in user_list:
            return {'result':'User already exist!'}
        user_list.append(name)
        config['users'] = user_list
        saveConfig()
        loadConfig()
    
    return {'result':'User created!'}

#Загрузка настроек
loadConfig()
