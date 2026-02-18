FROM python:3.12

WORKDIR /app

COPY src/main/python ./aws-grpc-server
COPY requirements.txt .

RUN pip install -r requirements.txt

ENTRYPOINT ["python", "-u", "aws-grpc-server/request_serializer_service.py", "-p"]
CMD ["50055"]
