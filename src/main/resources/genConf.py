from jinja2 import Template

with open('conf.ftl', 'r') as file:
    template_str = file.read()

template = Template(template_str)
fieldList = [{'type': 'String', 'name': 'name'}]
code = template.render(packageName='com.sirius.server', className='ConfTest', dbName='conftest', fieldList=fieldList)

with open('ConfTest.java', 'w', encoding='utf-8') as file:
    file.write(code)
