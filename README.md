# 🏡 Groot

### 초보 홈 가드너를 위한 서비스

### SSAFY 8기 자율 프로젝트

<br>

<br>

## 👨‍👩‍👧‍👦팀원 소개

---

**[박세희](https://github.com/)** : `팀장`, `Client`

**[김민우](https://github.com/bkkmw)** : `BackEnd`

**[김정원](https://github.com/)** : `Client`

**[서다경](https://github.com/)** : `Client`

**[윤민주](https://github.com/)** : `BackEnd`

**[조승희](https://github.com/)** : `BackEnd`, `UCC`

<br>

## 프로젝트 개요

---

### 기획 배경

- Home Gardening 관심 확대
  - 아파트 단지 등에서 보이는 보라색 조명의 집, 코로나19 이후 "반려식물"에 대한 관심 증가 등 **'홈 가드닝'** 에 대한 관심이 높아지고 있습니다.
- Home Gardening의 어려움
  - 식물 재배를 어려워 하는 이유는 벌레나 해충 등 전문적이고 체계적인 관리 방법 때문이 아닌, 물주기나 햇빛 등 쉽게 넘어가기 쉬운 기본 관리에 어려움을 느끼기 때문입니다.
- Home Gardening의 기대효과
  - Home Gardening은 스트레스와 불안 정도를 낮추며, 정신건강 개선에 많은 도움을 줍니다.
  - 2018년 한국환경과학회지에 따르면, 반려식물을 3개월간 돌본 사람은 그렇지 않은 사람에 비해 우울 증상이 감소한다고 합니다.
- **초보 '홈 가드너'를 위한 서비스**
  - 가드닝 가이드를 제공할 뿐만 아니라, 캐릭터 육성과 커뮤니티 기능을 추가하여 보다 재미있고, 흥미로운 홈 가드닝 서비스를 구현하였습니다.

### 📆 프로젝트 진행 기간

2023.04.10 ~ 2023.05.19

<br>

## 주요 기능

---

### 화분 등록

- 식물 식별 : 사용자가 등록하고자 하는 화분의 사진을 촬영하면 식물의 종을 식별하여 사용자의 화분으로 등록할 수 있도록 합니다.
- 조도 분석 : 화분을 위치하고자 하는 곳의 조도를 Android 센서를 통해 분석하여 해당 식물에게 적합한 위치인지 여부 판단하여, 사용자에게 해당 화분을 놓을 적합한 위치를 찾을 수 있도록 도와줍니다.
- 화분 등록 : 사진을 통한 식물 식별이나, 직접 검색을 통해 화분을 등록할 수 있도록 합니다.
- 육성 가이드 : 물주기, 영양제, 햇빛, 분갈이, 해충 등 개별 식물 별 일정 및 가이드를 제공합니다. 물주기와 같은 잊어버리기 쉬운 일정은 푸쉬 알림을 통한 식물별 리마인더 및 캘린더를 통한 일정을 사용자에게 제공하며, 수행한 일정에 대한 기록을 남길 수 있습니다.

### 캐릭터 육성

- AR : AR 기술을 활용하여 식물과 함께 3D 캐릭터 모델을 활용한 생동감 있는 식물 캐릭터를 만날 수 있습니다.
- 캐릭터 육성 : 물주기 등의 활동을 통해 경험치를 얻어 캐릭터를 육성하여 총 3단계로 변화하는 캐릭터를 만날 수 있습니다.
- 33개의 캐릭터 : 생육 형태에 따라 11 종의 캐릭터로 분류되며, 성장에 따라 변화하는 총 33개의 캐릭터를 AR 기술을 통해 식물과 함께 만날 수 있습니다.

### 다이어리

- 가드닝 활동 체크 : 물주기, 분갈이, 영양제 투여 등의 여부를 체크하여 기록을 남길 수 있습니다.
- 타임라인 : 보유한 모든 화분, 특정 화분에 대한 다이어리를 타임라인을 통해 한눈에 식물의 성장 기록 과정을 확인할 수 있습니다.

### 플래너

- 일정 체크 : 캘린더를 통해 언제 가드닝을 하면 되는지 확인 가능합니다.
- 일정 수행 및 다이어리 연동 : 수행한 미션을 손쉽게 체크할 수 있으며, 다이어리와 연동하여 자동으로 기록을 남길 수 있습니다.

### 식물 정보

- 자동 완성 : 식물 이름 검색을 위한 자동완성 기능을 제공합니다. 또한, 다양한 이름으로 알려진 식물에 대해 식물의 정확한 이름으로 바꾸어 검색하여 사용자에게 편리한 검색 기능을 제공합니다.
- 특성 별 필터링 : 난이도, 필요한 광량, 생육 형태에 대한 필터링 검색 기능을 제공하여, 원하는 식물을 찾을 수 있습니다.
- 이미지 기반 식별 및 검색 : 사진으로 식물을 식별하여 해당 식물에 대한 정보를 제공합니다.
- 추천 기능 : 사용자가 보유한 화분 정보를 통해, 식물 데이터 간 Cosine similarity를 기반으로한 CBF(Content Based Filtering)를 통해 사용자에게 유사한 식물을 추천합니다.
- 식물 정보 : 검색한 식물에 대한 소개, 적정 환경 등을 확인할 수 있으며, 식물을 키우기 위한 TIP을 Youtube 영상을 통해 확인할 수 있습니다.

### 커뮤니티

- 카테고리 별 게시글 검색 : 나눔, 자유, QnA 등 카테고리별 게시판 기능을 제공합니다.
- 실시간 인기 태그 : 카테고리 별 인기 태그 정보를 제공합니다.
- 북마크 기능 : 다시 보고싶은 게시물에 북마크를 등록하여, 마이페이지에서 쉽게 확인할 수 있습니다.
- 채팅 기능 : 사용자 간 채팅을 통해 자유로운 나눔과 대화가 가능하며 푸쉬 알림을 통해 사용자의 device에 알림을 줄 수 있습니다.

<br>

## 주요 기술

---

### AI

- 식물 이미지를 분석하여 식물의 종을 식별합니다.
- 수 많은 종류의 종이 존재하며, 명명 규칙이 잘 지켜지지 않은 경우가 존재하여 상위 N개의 결과 중 동일 속에 대한 가중치를 적용하여 검색 결과를 보정합니다.

### AR

- 식물의 위치를 인식하여, 식물과 식물에 해당하는 캐릭터를 함께 보여줍니다.
- ML Kit의 Object Detection과 Image Tracking을 통해 Object(식물)의 위치를 추적하며, SceneView를 통한 Environment Detection을 활용하여 AR 기술을 구현하였습니다.
- Object Detection에 앞서, Computer Vision 기술과, Deep learning 기술을 활용하여 Object recognition과 classification을 통해 식물을 찾아 Point cloud를 이용하여 dection을 수행합니다.

### Recommendation System

- 사용자의 화분에 대한 데이터 기반으로, 식물 데이터를 통한 Content-Based Filtering을 활용하여 사용자에게 보유한 식물과 유사한 식물을 추천합니다.
- 식물의 생장(생육)형태를 벡터화하여 Cosine Similarity를 계산하여 유사도가 높은 식물을 사용자에게 추천합니다.

### 시스템 아키텍처

![Architecture](/uploads/f7243a321a6c781df8a3935588f981ee/Architecture.png)

### 기술 스택

![tech_stack](/uploads/3fe32ae01339d08dbbcfd5dbc74c8a60/tech_stack.png)
<br>

## 개발 및 협업 환경

---

**Backend**

<img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white">
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white">
<img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=Hibernate&logoColor=white">
<img src="https://img.shields.io/badge/QueryDSL-3388ff?style=for-the-badge&logo=Query&logoColor=white">
<img src="https://img.shields.io/badge/Flask-000000?style=for-the-badge&logo=Flask&logoColor=white">
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white">
<img src="https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=Python&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white">
<img src="https://img.shields.io/badge/Intellij_IDEA-000000?style=for-the-badge&logo=IntellijIDEA&logoColor=white">
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
<img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=Firebase&logoColor=white">

**Android**

<img src="https://img.shields.io/badge/Kotlin-4FC08D?style=for-the-badge&logo=Kotlin&logoColor=white">
<img src="https://img.shields.io/badge/AR_CORE-007ACC?style=for-the-badge&logo=C&logoColor=white">
<img src="https://img.shields.io/badge/ML Kit-007ACC?style=for-the-badge&logo=Android Studio&logoColor=white">
<img src="https://img.shields.io/badge/Retrofit2-2FC774?style=for-the-badge&logo=Square&logoColor=white">
<img src="https://img.shields.io/badge/Glide-00CCBC?style=for-the-badge&logo=Google Play&logoColor=white">
<img src="https://img.shields.io/badge/Android_Studio-007ACC?style=for-the-badge&logo=Android Studio&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white">
<img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=Firebase&logoColor=white">

**Server**

<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white"> <img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white"> <img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"> <img src="https://img.shields.io/badge/S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">

**Cooperation & Communication**

<img src="https://img.shields.io/badge/gitlab-FC6D26?style=for-the-badge&logo=GitLab&logoColor=white"> <img src="https://img.shields.io/badge/jira-0052CC?style=for-the-badge&logo=Jira&logoColor=white"> <img src="https://img.shields.io/badge/MatterMOST-009688?style=for-the-badge&logo=Mattermost&logoColor=white"> <img src="https://img.shields.io/badge/Notion-EF1970?style=for-the-badge&logo=Notion&logoColor=white"> <img src="https://img.shields.io/badge/Discord-FDA061?style=for-the-badge&logo=Discord&logoColor=white">

<br>

## 서비스 화면

---

### 메인 화면

![main_short](/uploads/7c75b917e0da053400085b97476536e6/main_short.mp4)

### 화분 등록

![register_pot](/uploads/2fbf1445e679126e76b1fc06709e1dba/register_pot.mp4)

### AR

![ar_tree](/uploads/0b1bf6e405db440f860dfb39035c828d/ar_tree.mp4)

### 플래너

### 다이어리 및 캐릭터 육성

### 식물 이름 검색 및 이름 자동완성

![search_name](/uploads/3d37e1812297b2970ee1e1cb9b39a727/search_name.mp4)

### 식물 필터링 검색

![filtering_and_recomm](/uploads/46ed927e8b61e6ae6206e2f1d2c8456e/filtering_and_recomm.mp4)

### 식물 식별

![identif_성미인](/uploads/b62ca1016555e4bf10579d01176b74a7/identif_성미인.mp4)

### 커뮤니티

### 리사이클러뷰를 통한 무한 스크롤

<br>

## 프로젝트 산출물

---

### 와이어 프레임

![wireframe](/uploads/1100f4d8637cb082a5d9d599e03d379f/wireframe.png)

### ERD

![ERD](/uploads/94df969a4a4d1698d3063756947eef30/ERD.png)

### API 명세서

![API_specification](/uploads/188b29ec99eac1065d7da31d918a5c89/API_specification.png)

[API 명세서](https://superficial-brush-0a1.notion.site/API-1641b7cde4b34f659bcf60af60eeeb6d)

---

# 🏡 GROOT

![play_store_QR](/uploads/5228af8e7dcebed0d6a5dbc529b1ff98/play_store_QR.png)
