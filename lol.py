import re
with open('app/build/reports/coverage/debug/report.xml', 'r') as f:
    s = f.read()
    x = re.sub('<package name="com/sd/a3kleingroup">[\s\S\n]*</package>', '', s)
    names_to_ignore = ['Filter', 'Holder', 'RecyclerHolder', 'MyFile']
    for n in names_to_ignore:
        x = re.sub('<class name=".*'+str(n)+'">[\s\S\n]*</class>', '', x)
        x = re.sub('<sourcefile name="'+str(n)+'.java">[\s\S\n]*</sourcefile>', '', x)
    print(x)
    with open('app/build/reports/coverage/debug/report.xml', 'w') as f:
        f.write(x)