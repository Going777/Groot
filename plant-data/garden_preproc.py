import csv
import os
from dotenv import load_dotenv

col = ['id', 'kr_name', 'sci_name', 'grw_type', 'grw_speed'
       'min_temp', 'max_temp', 'winter_min_temp', 'winter_max_temp',
       'min_humidity', 'max_humidity', 'light_demand',
       'water_cycle', 'mgmt_level', 'mgmt_demand', 'place',
       'mgmt_tip', 'grw_season', 'characteristics',
       'insect_info', 'toxic_info', 'smell_deg', 'height', 'area',
       'description'
       ]

load_dotenv()

# load file
filename = os.environ.get('OUTPUT_DIR') + 'garden_details_name.csv'
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
    data = [None] * 26
    # skip metadata
    if (row[0] == 'id'):
        continue

    # id, kr name, scientific name
    data[0:3] = row[0:3]

    # grw_type
    data[3] = row[22]

    # grw_speed
    data[4] = row[9]


print("DONE !")
