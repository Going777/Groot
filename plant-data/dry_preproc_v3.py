import csv
import os
from dotenv import load_dotenv


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
    data[3] = data[3].split(",")[0]

    # temp. soon will be modified
    data[11] = 53003

    data[15] = data[15].replace("\"", "\'")

    data[17] = data[17].replace("\"", "\'")

    data.append(urls[cnt - 1])
    writer.writerow(data)
    print("[%3d] row done : " % (cnt))


print("DONE")
