import csv
import os
from dotenv import load_dotenv


def mgmt_level(mgmt_level):
    if ("쉬움" in mgmt_level):
        return 1

    elif ("보통" in mgmt_level):
        return 2

    else:
        return 3


col = ['id', 'kr_name', 'sci_name', 'grw_type', 'grw_speed',
       'min_grw_temp', 'max_grw_temp', 'winter_min_temp',
       'min_humidity', 'max_humidity', 'light_demand',
       'water_cycle', 'mgmt_level', 'mgmt_demand', 'place',
       'mgmt_tip', 'grw_season', 'characteristics',
       'insect_info', 'toxic_info', 'smell_degree', 'height', 'area',
       'description', 'img'
       ]

load_dotenv()

# load urls from list
filename = os.environ.get('OUTPUT_DIR') + 'dry_list.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

urls = []
for row in reader:
    if row[0] == 'id':
        continue

    urls.append(row[2].split("|")[0])

# load file
filename = os.environ.get('OUTPUT_DIR') + 'dry_preproc_v2.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

# output file
filename = os.environ.get('OUTPUT_DIR') + 'dry_preproc_v3.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
writer.writerow(col)

# read line
cnt = 0
for row in reader:
    if row[0] == 'id':
        continue

    cnt += 1
    data = row

    # grw_type
    # data[3] = data[3].split(",")[0]

    if ("반그늘" in data[10]):
        data[10] = 2

    else:
        data[10] = 3

    if ("1 ~2" in data[11] or "두 번" in data[11]):
        data[11] = 53003

    else:
        data[11] = 53004

    data[12] = mgmt_level(data[12])

    data[15] = data[15].replace("\"", "\'")

    data[21] = 25
    data[22] = 30

    data[23] = data[17].replace("\"", "\'")
    data[17] = ""

    data.append(urls[cnt - 1])
    writer.writerow(data)
    print("[%3d] row done : " % (cnt))


print("DONE")
