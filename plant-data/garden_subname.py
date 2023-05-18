import requests
import csv
import os
from bs4 import BeautifulSoup
from dotenv import load_dotenv
import re
from copy import deepcopy

# load API KEY
load_dotenv()
print(os.environ.get('GARDEN_API_KEY'))

# list file
filename = os.environ.get('OUTPUT_DIR') + 'garden_list.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

filename = os.environ.get('OUTPUT_DIR') + 'garden_alt_name.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
col = ['id', 'kr_name', 'alt_names']
writer.writerow(col)

# count
cnt = 0

for row in reader:
    data = []
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

        alt_name = soup.find('distbnm').text

        if (alt_name is None or alt_name == ""):
            continue

        alt_name = re.sub('\(.+\)', '', alt_name)

        alt_names = alt_name.split(",")
        # code
        data = [id, name]

        for t_name in alt_names:
            if (t_name.strip() == ""):
                continue

            temp_data = deepcopy(data)
            temp_data.append(t_name.strip())
            writer.writerow(temp_data)

        cnt += 1
        print("UPDATED : %s : %d" % (id, cnt))

print("DONE !")
