import os

import pandas as pd
from sqlalchemy import create_engine
from sqlalchemy_utils import database_exists, create_database

engine = create_engine("mysql+pymysql://gaoliandi:gaoliandi@172.23.246.114:3306/excel")
if not database_exists(engine.url):
    create_database(engine.url)

current_directory = os.getcwd()
for fileName in os.listdir(current_directory):
    if 'xlsx' not in fileName:
        continue
    df = pd.read_excel(fileName)
    df.to_sql(name=fileName.split('.')[0], con=engine, if_exists="replace", index=False)
