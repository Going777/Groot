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

filename = os.environ.get('OUTPUT_DIR') + 'garden_details_code.csv'
# filename = os.environ.get('OUTPUT_DIR') + 'garden_details_name.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
col = ['id', 'kr_name', 'bo_name', 'advise', 'height', 'area',
       'smell', 'toxic', 'mgmt_level', 'grw_speed', 'grw_temp', 'humidity',
       'water_cycle', 'water_cycle_winter', 'special_mgmt', 'functionalities',
       'mgmt_demand', 'leaf_pattern', 'leaf_color', 'light_demand',
       'place', 'insects', 'grw_type', 'winter_temp']
writer.writerow(col)

# output columns description
col_kr = ['id', '한글명', '학명', 'ㅁㄹ..', '높이', '넓이',
          '냄새', '독성', '관리 레벨', '성장 속도', '성장 온도', '습도',
          '물 주기', '겨울 물 주기', '관리 특이사항', '기능성',
          '관리 요구', '잎 패턴', '잎 색상', '빛 요구량',
          '배치 위치', '곤충', '생육형태', '겨울철 온도']
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
        data = [id, name, soup.find('plntbnenm').text,
                soup.find('adviseinfo').text, soup.find(
                    'growthhginfo').text, soup.find('growtharainfo').text,
                soup.select_one('smellcode').text, soup.find(
                    'toxctyinfo').text, soup.find('managelevelcodenm').text,
                soup.find('grwtvecodenm').text, soup.find(
                    'grwhtpcodenm').text, soup.select_one('hdcode').text,
                soup.find('watercyclesprngcodenm').text, soup.find(
                    'watercyclewintercodenm').text,
                soup.find('speclmanageinfo').text, soup.find(
                    'fncltyinfo').text,
                soup.find('managedemanddocodenm').text, soup.find(
                    'lefmrkcodenm').text,
                soup.find('lefcolrcodenm').text, soup.find(
                    'lighttdemanddocodenm').text,
                soup.find('postngplacecodenm').text, soup.find(
                    'dlthtscodenm').text, soup.find('grwhstlecodenm').text,
                soup.select_one('winterlwettpcode').text]
        writer.writerow(data)

        # name
        # data = [id, name, soup.find('plntbnenm').text,
        #         soup.find('adviseinfo').text, soup.find(
        #             'growthhginfo').text, soup.find('growtharainfo').text,
        #         soup.find('smellcodenm').text, soup.find(
        #             'toxctyinfo').text, soup.find('managelevelcodenm').text,
        #         soup.find('grwtvecodenm').text, soup.find(
        #             'grwhtpcodenm').text, soup.find('hdcodenm').text,
        #         soup.find('watercyclesprngcodenm').text, soup.find(
        #             'watercyclewintercodenm').text,
        #         soup.find('speclmanageinfo').text, soup.find(
        #             'fncltyinfo').text,
        #         soup.find('managedemanddocodenm').text, soup.find(
        #             'lefmrkcodenm').text,
        #         soup.find('lefcolrcodenm').text, soup.find(
        #             'lighttdemanddocodenm').text,
        #         soup.find('postngplacecodenm').text, soup.find(
        #             'dlthtscodenm').text, soup.find('grwhstlecodenm').text,
        #         soup.find('winterlwettpcodenm').text]
        # writer.writerow(data)
        cnt += 1
        print("UPDATED : %s : %d" % (id, cnt))

print("DONE !")
