from flask import Flask, request, jsonify, current_app
from sqlalchemy import create_engine, text
import pymysql
from config import mysql
import pandas as pd
import numpy as np
from numpy import dot
from numpy.linalg import norm
from itertools import islice

app = Flask(__name__)

grw_type_list = ['군생형', '다육형', '로제트형', 
                 '포도송이형', '불규칙형', '탑형', 
                 '관목형', '직립형', '포복형', '덩굴성', 
                 '풀모양']

# 식물 id랑 cos 유사도 값 담을 딕셔너리
dict_plant = {}

@app.route('/recommendations', methods=['GET'])
def plant_recommend():
    db_connection = pymysql.connect(
    user    = mysql['user'],
    passwd  = mysql['passwd'],
    host    = mysql['host'],
    db      = mysql['database'],
    charset = 'utf8',
    cursorclass=pymysql.cursors.DictCursor
    )
    try:
        # db connect
        conn = db_connection
        # cursor = conn.cursor()

        # 유저 존재 여부 조회
        userPK = request.args.get('id')

        with conn.cursor() as cursor:
            conn = db_connection
            user_sql = "select * from users where id=%s"
            cursor.execute(user_sql, userPK)
            result = cursor.fetchall()

        if (len(result) == 0):
            return jsonify({'msg':'존재하지 않는 사용자'}), 400

        with conn.cursor() as cursor:
        # 유저 pot 조회
            pot_sql = "select plants.grw_type from plants join pots on pots.plant_id = plants.id where pots.user_id = %s"
            cursor.execute(pot_sql, userPK)
            result = cursor.fetchall()

        # 등록된 화분이 없을 때 처리
        if (len(result) == 0):
            return jsonify({'msg':'화분이 존재하지 않습니다.'}), 204

        user_dataFrame = pd.DataFrame(result)

        # plant 정보 조회
        plant_sql = "select id, grw_type from plants"
        cursor = conn.cursor()
        cursor.execute(plant_sql)
        result = cursor.fetchall()
        plant_dataFrame = pd.DataFrame(result)
            

        # 유저 정보 벡터화 후 합치기
        user_v = iternalDataFrame(user_dataFrame)

        # 식물 정보 벡터화 후 코사인 유사도 검사
        dict_result = plantDataToVector(plant_dataFrame, user_v)

        # 10개만 자르기
        res = dict_result[0:20]

        # json 형태로 바꾸기
        datas = listToJson(res)
        print(datas)
        
        return jsonify({'msg':'식물 목록 조회에 성공했습니다.','plants':datas}), 200
    finally:
        conn.close()

    

# response 객체로 만들기
def listToJson(list):
    db_connection = pymysql.connect(
    user    = mysql['user'],
    passwd  = mysql['passwd'],
    host    = mysql['host'],
    db      = mysql['database'],
    charset = 'utf8',
    cursorclass=pymysql.cursors.DictCursor
    )

    conn = db_connection
    response = []
    for item in list:
        cursor = conn.cursor()
        try:
            print('item: ',item[0])
            sql = "select id, grw_type, kr_name, img from plants where id=%s"
            cursor.execute(sql, item[0])
            result = cursor.fetchall()

            data = {
                'plantId':result[0]['id'],
                'krName':result[0]['kr_name'],
                'img':result[0]['img']
            }

            response.append(data)
        finally:
            cursor.close()
        
    return response



# 코사인 유사도 구하는 함수
def cos_sim(A, B):
    return dot(A,B)/(norm(A)*norm(B))


#유저 데이터 dataframe을 돌면서 벡터화 하는 함수
def iternalDataFrame(dataFrame):
    final_np = np.zeros(len(grw_type_list))
    for data in dataFrame['grw_type']:
        result = make_matrix(grw_type_list, data)
        final_np += np.array(result)
    
    return final_np

#식물 정보를 벡터화, cos 유사도 검사, 정렬 후 dict 반환 함수
def plantDataToVector(dataFrame, user_data):
    for id, type in zip(dataFrame['id'], dataFrame['grw_type']):
        result = make_matrix(grw_type_list, type)
        np_result = np.array(result)
        # 유저 정보랑 cos 유사도 돌리기
        cos_result = cos_sim(np_result, user_data)
        dict_plant[id] = cos_result

    # dict 정렬
    sorted_dict = sorted(dict_plant.items(), key=lambda item:item[1], reverse=True)
    print(sorted_dict)
    return sorted_dict

# 벡터화 (일치하는 항목 0/1으로 바꾸기)
def make_matrix(types, pot_data):
    split_list = pot_data.split(',')
    freq_list = []
    for type in types:
        freq = 0
        for word in split_list:
            if type == word:
                freq += 1
        freq_list.append(freq)
    return freq_list

if __name__ == '__main__':
    app.run()

