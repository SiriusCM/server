import pandas as pd
from jinja2 import Template
from sqlalchemy import create_engine

with open('excelObject.ftl', 'r') as file:
    template_str = file.read()
template1 = Template(template_str)

with open('excelConfig.ftl', 'r') as file:
    template_str = file.read()
template2 = Template(template_str)

nameList = []
engine = create_engine("mysql+pymysql://gaoliandi:gaoliandi@172.23.246.114:3306/excel")
tables = pd.read_sql_query('show tables', engine).to_dict(orient='list')['Tables_in_excel']
for table in tables:
    className = 'Excel' + table.title()
    entryList = pd.read_sql_query('desc ' + table, engine).to_dict(orient='records')
    for entry in entryList:
        if entry['Type'] == 'bigint':
            entry['Type'] = 'long'
        elif entry['Type'] == 'text':
            entry['Type'] = 'String'
    code = template1.render(packageName='com.sirius.server.conf', className=className, entryList=entryList)
    with open(className + '.java', 'w', encoding='utf-8') as file:
        file.write(code)
    nameList.append({'className': className, 'table': table})

code = template2.render(packageName='com.sirius.server.conf', nameList=nameList)
with open('ExcelConfig.java', 'w', encoding='utf-8') as file:
    file.write(code)

print('')
