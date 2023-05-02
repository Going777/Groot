import requests
import csv
import os
from bs4 import BeautifulSoup
from dotenv import load_dotenv

# load API KEY
load_dotenv()
print(os.environ.get('DRY_API_KEY'))

# output file
filename = os.environ.get('OUTPUT_DIR') + 'garden_list.csv'
f = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(f)

# output column
data = ["id", "kr_name"]
writer.writerow(data)

# request
url = 'http://api.nongsaro.go.kr/service/garden/gardenList?apiKey=' + \
    os.environ.get('GARDEN_API_KEY') + \
    '&numOfRows=300'
response = requests.get(url)

# response
print(response.status_code)
if response.status_code == 200:
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')

    for item in soup.findAll('item'):
        it = item.item
        print(item.cntntsno, item.cntntssj)
        print(item.cntntsno.string, item.cntntssj.string)
        data = [item.cntntsno.string, item.cntntssj.string]
        writer.writerow(data)

else:
    print(response.status_code)
