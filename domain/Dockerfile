FROM python:3.9.10

COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt
COPY . /app

WORKDIR /app

CMD ["python3", "-m", "flask", "run", "--host=0.0.0.0"]