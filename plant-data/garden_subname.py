import requests
import csv
import os
from bs4 import BeautifulSoup
from dotenv import load_dotenv

# load API KEY
load_dotenv()
print(os.environ.get('GARDEN_API_KEY'))

# list file
filename = os.environ.get('OUTPUT_DIR') + 'garden_list.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

filename = os.environ.get('OUTPUT_DIR') + 'garden_subname.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
col = ['id', 'kr_name', 'sub_names']
writer.writerow(col)

# output columns description
col_kr = ['id', '한글명', '유통명']
writer.writerow(col_kr)

# count
cnt = 0

for row in reader:
    data = row
    id = row[0]
    if (id == 'id'):
        continue
    url = 'http://api.nongsaro.go.kr/service/garden/gardenDtl?apiKey=' + \
        os.environ.get('GARDEN_API_KEY') + '&cntntsNo=' + id
    # print(url)
    name = row[1]
    response = requests.get(url)

    if (response.status_code == 200):
        html = response.text
        soup = BeautifulSoup(html, 'html.parser')

        # code
        data = [id, name, soup.find('distbnm').text]
        writer.writerow(data)

        cnt += 1
        print("UPDATED : %s : %d" % (id, cnt))

print("DONE !")
