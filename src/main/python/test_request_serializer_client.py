import grpc
import request_serializer_pb2
import request_serializer_pb2_grpc
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("file", help="path to model file")
parser.add_argument("operation", help="operation in the model to test")
args = parser.parse_args()

# MODEL_FILEPATH = r'C:\Users\philip\Downloads\HermodEmailManagementServiceModel-1.0.normal.json'
# OPERATION_NAME = "NotifyCustomer"


def test():
    with grpc.insecure_channel("localhost:50055") as channel:
        stub = request_serializer_pb2_grpc.RequestSerializerStub(channel)
        response = stub.SerializeRequest(
            request_serializer_pb2.SerializeRequestRequest(
                model_str=open(args.file, encoding='UTF-8').read(),
                operation_name=args.operation
            )
        )
        print(response)


if __name__ == "__main__":
    test()
