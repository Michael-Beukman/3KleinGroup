"""
import re
with open('app/build/reports/coverage/debug/report.xml', 'r') as f:
    s = f.read()
    x = re.sub('<package name="com/sd/a3kleingroup">[\s\S\n]*</package>', '', s)
    names_to_ignore = ['Filter', 'Holder', 'RecyclerHolder', 'MyFile']
    # for n in names_to_ignore:
        # x = re.sub('<class name=".*'+str(n)+'">[\s\S\n]*</class>', '', x)
        # x = re.sub('<sourcefile name="'+str(n)+'.java">[\s\S\n]*</sourcefile>', '', x)
        # break

    print(x)
    with open('app/build/reports/coverage/debug/report.xml', 'w') as f:
        f.write(x)
"""
from xml.etree.ElementTree import ElementTree
import re
# New
def change(filename='app/build/reports/coverage/debug/report.xml'):
    tree = ElementTree()
    tree.parse(filename)
    regexes=['.*/.*Activity.*', '.*/messaging/.*', '.*Holder.*', '.*/UI/.*', '.*File.*', '.*ViewFriendPublicFiles.*', '.*DialogFragment.*', '.*Callback.*']
    to_ignore=['.*MySentFiles.*', '.*/db/.*', '.*FileModel.*']

    for p in tree.findall('package'):
             name = p.attrib['name']; print (name)
             for c in p.findall('class'):
                #   print(c.attrib)
                  #p.remove(c)
                  if any([re.match(ii, c.attrib['name']) for ii in to_ignore]): continue
                  for r in regexes:
                      if re.match(r, c.attrib['name']):
                          try:
                            p.remove(c)
                          except Exception as e:
                               print ("ERROR at line 34" , e)
                          print("removing ", c.attrib['name'])
                          continue
             for s in p.findall('sourcefile'):
                # print(s.attrib['name'])
                #if re.match(to_ignore, s.attrib['name']): continue
                if any([re.match(ii, p.attrib['name'] + '/' + s.attrib['name']) for ii in to_ignore]): continue
                for r in regexes:
                    if re.match(r, p.attrib['name'] + '/' + s.attrib['name']):
                        try:
                            p.remove(s);
                        except Exception as e:
                               print ("ERROR at line 45" , e)
                        print("removing ", p.attrib['name'] + '/' + s.attrib['name'], r)
                        continue
    # write again
    tree.write(filename)

change() #'app/build/reports/coverage/tmp/report.xml'
