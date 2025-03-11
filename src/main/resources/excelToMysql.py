import pandas as pd
from sqlalchemy import create_engine

df = pd.read_excel('test.xlsx')
df.index.name = "id"

engine = create_engine("mysql+mysqlconnector://gaoliandi:gaoliandi@127.0.0.1:3306/conf", echo=False)
df.to_sql(name='student', con=engine, if_exists="replace")
