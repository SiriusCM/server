import pymysql
from jinja2 import Template

db = pymysql.connect(host='127.0.0.1', user='gaoliandi', password='gaoliandi', database='conf')
cursor = db.cursor()
sql = "SELECT * FROM EMPLOYEE"
cursor.execute(sql)
results = cursor.fetchall()
fieldList = [{'type': 'String', 'name': 'name'}]

with open('conf.ftl', 'r') as file:
    template_str = file.read()

template = Template(template_str)
code = template.render(packageName='com.sirius.server', className='ConfTest', dbName='conftest', fieldList=fieldList)

with open('ConfTest.java', 'w', encoding='utf-8') as file:
    file.write(code)
