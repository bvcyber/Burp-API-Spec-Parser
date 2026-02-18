"""
Use botocore to serialize C2J operation to HTTP request
"""

from botocore.model import ServiceModel
from botocore.serialize import create_serializer
from botocore.utils import ArgumentGenerator
from botocore.awsrequest import AWSRequest
from botocore.signers import RequestSigner
from botocore.hooks import BaseEventHooks
from request_serializer_pb2 import SerializeRequestResponse
from concurrent import futures
from collections import namedtuple, OrderedDict
import request_serializer_pb2_grpc
import json
import grpc
import traceback
import argparse


class CredentialsPlaceholder:
    def __init__(self):
        self.ReadOnlyCredentials = namedtuple(
            'ReadOnlyCredentials',
            ['access_key', 'secret_key', 'token']
        )
        self.placeholder_credentials = self.ReadOnlyCredentials(
            'AAAAAAAAAAAAAAAAAAAA',
            '00000000000+0000000000000000000000000000',
            '',
        )

    def get_frozen_credentials(self):
        return self.placeholder_credentials


class RequestSerializerServicer(request_serializer_pb2_grpc.RequestSerializerServicer):
    def __init__(self):
        super().__init__()
        self.argument_generator = ArgumentGenerator()
        self.event_emitter = BaseEventHooks()
        self.placeholder_credentials = CredentialsPlaceholder()

    def _update_none_to_empty_string(self, data):
        if isinstance(data, OrderedDict) or isinstance(data, dict):
            for key, value in data.items():
                if value is None:
                    data[key] = ''
                else:
                    self._update_none_to_empty_string(value)
        elif isinstance(data, list):
            for i in range(len(data)):
                if data[i] is None:
                    data[i] = ''
                else:
                    self._update_none_to_empty_string(data[i])

    def SerializeRequest(self, request, context):
        try:
            model = ServiceModel(json.loads(request.model_str))
            operation_model = model.operation_model(request.operation_name)
            serializer = create_serializer(model.metadata['protocol'], False)
            auth_instance = RequestSigner(
                model.service_id,
                'us-east-1',
                model.signing_name,
                model.signature_version,
                self.placeholder_credentials,
                self.event_emitter,
            ).get_auth_instance(
                model.signing_name,
                'us-east-1',
                signature_version=model.signature_version,
                request_credentials=self.placeholder_credentials,
            )

            skeleton = self.argument_generator.generate_skeleton(operation_model.input_shape)
            # TODO: generated skeleton outputs None for blob types which causes an exception when serializing;
            # TODO: workaround is to change all None types to empty string. Does this interfere with non-blob types?
            self._update_none_to_empty_string(skeleton)

            result = serializer.serialize_to_request(
                skeleton,
                operation_model)
            if 'Host' not in result['headers']:
                # Set the Host header, or else botocore will throw an exception
                result['headers']['Host'] = ''
            aws_request = AWSRequest(
                method=result['method'],
                headers=result['headers'],
                url=result['url_path'],
                data=result['body'],
            )
            auth_instance.add_auth(aws_request)
            aws_request.prepare()

            try:
                # Convert all query_string values to str to match proto definition
                result['query_string'] = {k: str(v) for k, v in result['query_string'].items()}
            except Exception as e:
                # Sometimes result doesn't return a dict for query_string
                # TODO: probably a better way of handling this rather than catching an Exception
                print(f'WARNING: query_string - received a {type(result["query_string"])} instead of dict')
                print(result['query_string'])
                # traceback.print_exc()
            result['headers'] = aws_request.headers

            if type(result['body']) is dict:
                result['body'] = json.dumps(result['body']).encode('utf-8')

            print(result['method'])
            print(result['url_path'])
            print(result['query_string'])
            print(result['headers'])
            print(result['body'])

            return SerializeRequestResponse(
                url_path=result['url_path'],
                query_string=result['query_string'],
                method=result['method'],
                headers=result['headers'],
                body=result['body'],
            )
        except Exception as e:
            traceback.print_exc()
            return SerializeRequestResponse(
                url_path='/',
                query_string={},
                method='ERROR',
                headers={},
                body=bytes(f'An exception occurred: {str(e)}', 'utf-8'),
            )


def serve(port=50055):
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    request_serializer_pb2_grpc.add_RequestSerializerServicer_to_server(
        RequestSerializerServicer(), server)
    server.add_insecure_port(f'[::]:{port}')
    server.start()
    print(f'Listening on {port}')
    server.wait_for_termination()


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', '-p', type=int, default=50055)
    args = parser.parse_args()
    serve(port=args.port)
