import com.google.gson.Gson;
import com.bureauveritas.modelparser.model.OpenModelFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OpenModelFileTest extends MontoyaTest {
    private final OpenModelFile loader = new OpenModelFile();

    private String minifyJson(String jsonString) {
        Gson gson = new Gson();
        return gson.toJson(gson.fromJson(jsonString, Object.class));
    }

    @Test
    void loadAwsJson() {
        try {
            loader.loadModelFromFile(new File(getClass().getResource("sample-awsjson-1.json").toURI()));
            assertEquals("AWSServiceModelFileHandler", loader.getModelFileHandler().toString());
        }
        catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void loadGarbageJson() {
        try {
            loader.loadModelFromFile(new File(getClass().getResource("garbage.json").toURI()));
            assertEquals("ErrorFileHandler", loader.getModelFileHandler().toString());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void loadOpenApi() {
        try {
            loader.loadModelFromFile(new File(getClass().getResource("sample-openapi-1.json").toURI()));
            assertEquals("OpenAPIFileHandler", loader.getModelFileHandler().toString());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void loadOpenApiOperationsSimpleParse() {
        try {
            loader.loadModelFromFile(new File(getClass().getResource("sample-openapi-1.json").toURI()));
            assertEquals("OpenAPIFileHandler", loader.getModelFileHandler().toString());
            List<String> operationList = loader.getModelFileHandler().getOperations();
            assertEquals(
                new HashSet<>(Arrays.asList(
                    "GET / (\"listVersionsv2\")",
                    "GET /v2 (\"getVersionDetailsv2\")"
                )),
                new HashSet<>(operationList)
            );
            for (String operation : operationList) {
                assertEquals(
                    "GET",
                    loader.getModelFileHandler().getOperationMethod(operation)
                );
            }
//            assertTrue(loader.getModelFileHandler().getShapeList().isEmpty());
            String expectedJson = """
            {
               "operationId": "listVersionsv2",
               "summary": "List API versions",
               "responses": {
                 "200": {
                   "description": "200 response",
                   "content": {
                     "application/json": {
                       "examples": {
                         "foo": {
                           "value": {
                             "versions": [
                               {
                                 "status": "CURRENT",
                                 "updated": "2011-01-21T11:33:21Z",
                                 "id": "v2.0",
                                 "links": [
                                   {
                                     "href": "http://127.0.0.1:8774/v2/",
                                     "rel": "self"
                                   }
                                 ]
                               },
                               {
                                 "status": "EXPERIMENTAL",
                                 "updated": "2013-07-23T11:33:21Z",
                                 "id": "v3.0",
                                 "links": [
                                   {
                                     "href": "http://127.0.0.1:8774/v3/",
                                     "rel": "self"
                                   }
                                 ]
                               }
                             ]
                           }
                         }
                       }
                     }
                   }
                 },
                 "300": {
                   "description": "300 response",
                   "content": {
                     "application/json": {
                       "examples": {
                         "foo": {
                           "value": {
                             "versions": [
                               {
                                 "status": "CURRENT",
                                 "updated": "2011-01-21T11:33:21Z",
                                 "id": "v2.0",
                                 "links": [
                                   {
                                     "href": "http://127.0.0.1:8774/v2/",
                                     "rel": "self"
                                   }
                                 ]
                               },
                               {
                                 "status": "EXPERIMENTAL",
                                 "updated": "2013-07-23T11:33:21Z",
                                 "id": "v3.0",
                                 "links": [
                                   {
                                     "href": "http://127.0.0.1:8774/v3/",
                                     "rel": "self"
                                   }
                                 ]
                               }
                             ]
                           }
                         }
                       }
                     }
                   }
                 }
               }
             }""";
            assertEquals(
                minifyJson(expectedJson),
                minifyJson(
                    loader.getModelFileHandler().getOperationDefinition("GET / (\"listVersionsv2\")"))
            );
        } catch (Exception e) {
            fail(e);
        }
    }

    // TODO: fix shape test
//    @Test
//    void loadOpenApiOperationsPetstoreExpandedParse() {
//        try {
//            loader.loadModelFromFile(new File(getClass().getResource(
//                    "sample-openapi-petstore-expanded.json").toURI()));
//            assertEquals("OpenAPIFileHandler", loader.getModelFileHandler().toString());
//            List<String> operationList = loader.getModelFileHandler().getOperationList();
//            assertEquals(
//                new HashSet<>(Arrays.asList(
//                    "GET /pets (\"findPets\")",
//                    "POST /pets (\"addPet\")",
//                    "GET /pets/{id} (\"find pet by id\")",
//                    "DELETE /pets/{id} (\"deletePet\")"
//                )),
//                new HashSet<>(operationList)
//            );
////            assertEquals(
////                new HashSet<>(Arrays.asList(
////                    "Pet",
////                    "NewPet",
////                    "Error"
////                )),
////                new HashSet<>(loader.getModelFileHandler().getShapeList())
////            );
//            String[][] operationMethodsMappings = {
//                {"GET", "GET /pets (\"findPets\")"},
//                {"POST", "POST /pets (\"addPet\")"},
//                {"GET", "GET /pets/{id} (\"find pet by id\")"},
//                {"DELETE", "DELETE /pets/{id} (\"deletePet\")"}
//            };
//            for (String[] testCase : operationMethodsMappings) {
//                assertEquals(
//                    testCase[0],
//                    loader.getModelFileHandler().getOperationMethod(testCase[1])
//                );
//            }
//            String expectedJson = """
//            {
//              "description": "Returns all pets from the system that the user has access to\\nNam sed condimentum est. Maecenas tempor sagittis sapien, nec rhoncus sem sagittis sit amet. Aenean at gravida augue, ac iaculis sem. Curabitur odio lorem, ornare eget elementum nec, cursus id lectus. Duis mi turpis, pulvinar ac eros ac, tincidunt varius justo. In hac habitasse platea dictumst. Integer at adipiscing ante, a sagittis ligula. Aenean pharetra tempor ante molestie imperdiet. Vivamus id aliquam diam. Cras quis velit non tortor eleifend sagittis. Praesent at enim pharetra urna volutpat venenatis eget eget mauris. In eleifend fermentum facilisis. Praesent enim enim, gravida ac sodales sed, placerat id erat. Suspendisse lacus dolor, consectetur non augue vel, vehicula interdum libero. Morbi euismod sagittis libero sed lacinia.\\n\\nSed tempus felis lobortis leo pulvinar rutrum. Nam mattis velit nisl, eu condimentum ligula luctus nec. Phasellus semper velit eget aliquet faucibus. In a mattis elit. Phasellus vel urna viverra, condimentum lorem id, rhoncus nibh. Ut pellentesque posuere elementum. Sed a varius odio. Morbi rhoncus ligula libero, vel eleifend nunc tristique vitae. Fusce et sem dui. Aenean nec scelerisque tortor. Fusce malesuada accumsan magna vel tempus. Quisque mollis felis eu dolor tristique, sit amet auctor felis gravida. Sed libero lorem, molestie sed nisl in, accumsan tempor nisi. Fusce sollicitudin massa ut lacinia mattis. Sed vel eleifend lorem. Pellentesque vitae felis pretium, pulvinar elit eu, euismod sapien.\\n",
//              "operationId": "findPets",
//              "parameters": [
//                {
//                  "name": "tags",
//                  "in": "query",
//                  "description": "tags to filter by",
//                  "required": false,
//                  "style": "form",
//                  "schema": {
//                    "type": "array",
//                    "items": {
//                      "type": "string"
//                    }
//                  }
//                },
//                {
//                  "name": "limit",
//                  "in": "query",
//                  "description": "maximum number of results to return",
//                  "required": false,
//                  "schema": {
//                    "type": "integer",
//                    "format": "int32"
//                  }
//                }
//              ],
//              "responses": {
//                "200": {
//                  "description": "pet response",
//                  "content": {
//                    "application/json": {
//                      "schema": {
//                        "type": "array",
//                        "items": {
//                          "$ref": "#/components/schemas/Pet"
//                        }
//                      }
//                    }
//                  }
//                },
//                "default": {
//                  "description": "unexpected error",
//                  "content": {
//                    "application/json": {
//                      "schema": {
//                        "$ref": "#/components/schemas/Error"
//                      }
//                    }
//                  }
//                }
//              }
//            }""";
//            assertEquals(
//                minifyJson(expectedJson),
//                minifyJson(
//                    loader.getModelFileHandler().getOperationDefinitionByOperationName("GET /pets (\"findPets\")"))
//            );
//            expectedJson = """
//                [
//                    {
//                      "name": "tags",
//                      "in": "query",
//                      "description": "tags to filter by",
//                      "required": false,
//                      "style": "form",
//                      "schema": {
//                        "type": "array",
//                        "items": {
//                          "type": "string"
//                        }
//                      }
//                    },
//                    {
//                      "name": "limit",
//                      "in": "query",
//                      "description": "maximum number of results to return",
//                      "required": false,
//                      "schema": {
//                        "type": "integer",
//                        "format": "int32"
//                      }
//                    }
//                  ]""";
//            assertEquals(
//                minifyJson(expectedJson),
//                minifyJson(loader.getModelFileHandler().getShapeDefinitionByShapeName("GET /pets"))
//            );
//        } catch (Exception e) {
//            fail(e);
//        }
//    }

    // TODO: how can montoya API be used outside of Burp?
//    @Test
//    void serializeOpenAPIExhaustive() {
//        try {
//            loader.loadModelFromFile(new File(getClass().getResource(
//                "sample-openapi-exhaustive.yaml").toURI()));
//            assertEquals("OpenAPIFileHandler", loader.getModelFileHandler().toString());
//            HttpRequest serializedRequest =
//                loader.getModelFileHandler().serializeHttpRequest("GET /health (\"getHealth\")");
//            assertNotNull(serializedRequest);
//        } catch (Exception e) {
//            fail(e);
//        }
//    }
}
