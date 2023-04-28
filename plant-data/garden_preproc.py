import csv
import os
from dotenv import load_dotenv
import re


def reduce_new_lines(str):
    return re.sub('\n+', '\n', str)


def grw_temp_code(code):
    # 10~15, 15~20, 20~25, 25~30
    if code.strip() == "":
        return [20, 25]
    code = int(code) % 82001
    min_temp = 10 + 5 * code
    return [min_temp, min_temp+5]


def winter_temp_code(code):
    # 0-, 5, 7, 10, 13
    if code.strip() == "":
        return 10
    temps = [0, 5, 7, 10, 13]
    return temps[int(code) % 57001]


def humidity_code(code):
    # 40-, 40~70, 70+
    if code.strip() == "":
        return [40, 70]
    lists = [[0, 40], [40, 70], [70, 100]]
    return lists[int(code) % 83001]


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
filename = os.environ.get('OUTPUT_DIR') + 'garden_details_code.csv'
fr = open(filename, 'r', encoding='utf-8-sig')
reader = csv.reader(fr)

# output file
filename = os.environ.get('OUTPUT_DIR') + 'garden_preproc.csv'
fw = open(filename, 'w', encoding='utf-8-sig', newline='')
writer = csv.writer(fw)

# output columns
writer.writerow(col)

# read lines
for row in reader:
    data = [None] * 25
    # skip metadata
    if (row[0] == 'id'):
        continue

    # id, kr name, scientific name
    data[0:2] = row[0:3]

    # grw_type
    data[3] = row[22]

    # grw_speed
    data[4] = row[9]

    # grw_temp : min, max 56
    data[5:6] = grw_temp_code(row[10])

    # winter_temp : min 7
    data[7] = winter_temp_code(row[23])

    # humidity : min, max 8 9
    data[8:9] = humidity_code(row[11])

    # lightdemand 10
    data[10] = row[19]

    # water cycle 11
    data[11] = row[12]

    # mgmt_level 12
    data[12] = row[8]

    # mgmt_demand 13
    data[13] = row[16]

    # place 14
    data[14] = row[20]

    # mgmt_tip 15
    data[15] = reduce_new_lines(row[14])

    # grw_season 16
    data[16] = " "

    # char 17
    data[17] = " "

    # insect 18
    data[18] = row[21]

    # toxic 19
    data[19] = row[7]

    # smell 20
    if row[6].strip() == "":
        data[20] = 4
    else:
        data[20] = int(row[6]) % 79000
    # code

    # height, area 21 22
    data[21:22] = row[4:6]

    # desc 23
    data[23] = reduce_new_lines(row[15])

    writer.writerow(data)


print("DONE !")
