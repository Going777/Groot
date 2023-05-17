FROM python:3.9.10

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt
RUN pip3 install uwsgi

COPY . /app

WORKDIR /app

CMD ["uwsgi", "uwsgi.ini"]