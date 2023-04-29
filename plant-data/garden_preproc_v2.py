import csv
import os
from dotenv import load_dotenv
import openai
from time import sleep


def get_gpt_response(message):
    completion = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "user", "content": message}
        ]
    )
    print(completion.choices[0].message.content)
    return completion.choices[0].message.content


col = ['id', 'kr_name', 'sci_name', 'grw_type', 'grw_speed',
       'min_temp', 'max_temp', 'winter_min_temp',
       'min_humidity', 'max_humidity', 'light_demand',
       'water_cycle', 'mgmt_level', 'mgmt_demand', 'place',
       'mgmt_tip', 'grw_season', 'characteristics',
       'insect_info', 'toxic_info', 'smell_deg', 'height', 'area',
       'description'
       ]

load_dotenv()

# load file
filename = os.environ.get('OUTPUT_DIR') + 'garden_preproc.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

# output file
filename = os.environ.get('OUTPUT_DIR') + 'garden_preproc_v2.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
writer.writerow(col)

# base URL
openai.api_key = os.environ.get('CHATGPT_API_KEY')

# read line
cnt = 0
for row in reader:
    if row[0] == 'id':
        continue

    cnt += 1
    if cnt % 10 != 0:
        continue

    data = row
    message = "아래 내용 말투 예쁘게 바꿔서 한국어로 출력해줘.\n"
    if data[15].strip() != '':
        tip_message = message + row[15]
        data[15] = get_gpt_response(tip_message)

    if data[23].strip() != '':
        desc_message = message + row[23]
        data[23] = get_gpt_response(desc_message)

    writer.writerow(data)
    print("[%3d] row done : wait 1 minute" % (cnt))
    # if cnt == 10:
    #     break
    sleep(50)


print("DONE")
