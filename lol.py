import re
with open('/app/build/reports/coverage/debug/report.xml', 'r') as f:
    s = f.read()
    x = re.sub('<package name="com/sd/a3kleingroup">[\s\S\n]*</package>', 'ree', s)
    print(x)
    with open('/app/build/reports/coverage/debug/report.xml', 'w') as f:
        f.write(x)