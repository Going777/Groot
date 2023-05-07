import requests
import csv
import os
from bs4 import BeautifulSoup
from dotenv import load_dotenv

# load API KEY
load_dotenv()
print(os.environ.get('DRY_API_KEY'))

# output file
filename = os.environ.get('OUTPUT_DIR') + 'dry_list.csv'
f = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(f)

# output column
data = ["id", "kr_name", "img"]
writer.writerow(data)

# URL
url = 'http://api.nongsaro.go.kr/service/dryGarden/dryGardenList?apiKey=' + \
    os.environ.get('DRY_API_KEY') + \
    "&numOfRows=100"
response = requests.get(url)

# RESPONSE
print(response.status_code)
if response.status_code == 200:
    # parse
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')

    # Print & Write result
    for item in soup.findAll('item'):
        it = item.item
        print(item.cntntsno, item.cntntssj)
        print(item.cntntsno.string, item.cntntssj.string)
        print(item.imgurl1.string)
        data = [item.cntntsno.string, item.cntntssj.string, item.imgurl1.string]

        writer.writerow(data)

else:
    print(response.status_code)
