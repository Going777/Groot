import requests
import csv
import os
from bs4 import BeautifulSoup
from dotenv import load_dotenv

# load API KEY
load_dotenv()
print(os.environ.get('DRY_API_KEY'))

# list file
filename = os.environ.get('OUTPUT_DIR') + 'dry_list.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

# output file
filename = os.environ.get('OUTPUT_DIR') + 'dry_details.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
col = ['id', 'kr_name', 'bo_name',
       'grw_type', 'grw_speed', 'grw_temp',
       'winter_temp', 'characteristic', 'light_demand',
       'water_cycle', 'high_temp_hum', 'insect',
       'mgmt_level', 'mgmt_demand', 'place',
       'tips'
       ]
writer.writerow(col)

# output columns description
col_kr = ['id', '한글명', '학명',
          '생육 형태', '성장 속도', '성장 온도',
          '겨울철 온도', '특징', '빛 요구량',
          '물 주기', '고온다습', '곤충',
          '관리 레벨', '관리 요구', '배치 위치',
          '팁',]
writer.writerow(col_kr)

# count
cnt = 0

# read response
for row in reader:
    data = row
    id = row[0]
    # ignore first row
    if (id == 'id'):
        continue
    url = 'http://api.nongsaro.go.kr/service/dryGarden/dryGardenDtl?apiKey=' + \
        os.environ.get('DRY_API_KEY') + '&cntntsNo=' + id
    name = row[1]
    response = requests.get(url)

    # parse
    html = response.text
    soup = BeautifulSoup(html, 'html.parser')

    data = [id, name, soup.find('scnm').text,
            soup.find('stlesenm').text, soup.find(
                'grwtsevenm').text, soup.find('grwhtpinfo').text,
            soup.find('pswntrtpinfo').text, soup.find(
                'chartrinfo').text, soup.find('lighttinfo').text,
            soup.find('watercycleinfo').text, soup.find(
                'hgtmmhmrinfo').text, soup.find('dlthtsinfo').text,
            soup.find('managelevelnm').text, soup.find(
                'managedemandnm').text, soup.find('batchplaceinfo').text,
            soup.find('tipinfo').text]

    writer.writerow(data)
    cnt += 1
    print("UPDATED : %s : %d" % (id, cnt))

print("DONE !")
