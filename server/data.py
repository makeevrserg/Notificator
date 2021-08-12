from yaml import load, dump
from yaml import CLoader as Loader, CDumper as Dumper
import os

global config

def saveConfig():
    global config
    with open('config.yml', 'w') as file:
        dump(config,file)


def loadConfig():
    global config
    with open('config.yml', 'r') as file:
        config = load(file.read(), Loader=Loader)
        return config

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


loadConfig()
