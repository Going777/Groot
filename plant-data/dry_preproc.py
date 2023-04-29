import csv
import os
from dotenv import load_dotenv
import re


def grw_temp(val):
    if val.strip() == '':
        return [20, 25]
    else:
        val = val.replace('°C', '').replace('℃', '')
        val = re.split('~| ', val)
        temps = []
        if isinstance(val, str):
            temps.append(val)
            temps.append(25)
        else:
            if val[1] == "이상":
                val[1] = 30
            temps = val
        return temps


def winter_temp(val):
    if val.strip() == '':
        return 10
    else:
        temp = val.replace('°C', '').split('~')
        return temp[0]


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
filename = os.environ.get('OUTPUT_DIR') + 'dry_details.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

# output file
filename = os.environ.get('OUTPUT_DIR') + 'dry_preproc.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
writer.writerow(col)

cnt = 0

for row in reader:
    if row[0] == 'id':
        continue

    data = [None] * 24

    # id, kr name
    data[0:1] = row[0:2]

    # scientific name
    data[2] = row[2].replace('<i>', '').replace('</i>', '').split(',')[0]

    # grw_type
    data[3] = row[3]

    # grw_speed
    data[4] = row[4]

    # grw_temp : min, max 56
    data[5:6] = grw_temp(row[5])

    # winter_temp : min 7
    data[7] = winter_temp(row[6])

    # humidity : min, max 8 9
    data[8:9] = [20, 25]

    # lightdemand 10
    data[10] = row[8].replace('<br />', '')

    # water cycle 11
    data[11] = row[9].replace('<br />', '')

    # mgmt_level 12
    data[12] = row[12]

    # mgmt_demand 13
    data[13] = row[13]

    # place 14
    data[14] = row[14].replace('<br />', '')

    # mgmt_tip 15
    data[15] = row[15].replace('<br />', '')

    # grw_season 16
    data[16] = " "

    # char 17
    data[17] = row[7].replace('<br />', '')

    # insect 18
    data[18] = row[11].replace('<br />', '')

    # toxic 19
    data[19] = ""

    # smell 20

    data[20] = 4
    # code

    # height, area 21 22
    data[21] = ""
    data[22] = ""

    # desc 23
    data[23] = ""

    writer.writerow(data[0:24])

    cnt += 1
    print("[%3d] row done : %2d colums" % (cnt, len(data)))

print("DONE")
