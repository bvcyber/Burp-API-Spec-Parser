import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ai.Ai;
import burp.api.montoya.bambda.Bambda;
import burp.api.montoya.burpsuite.BurpSuite;
import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.comparer.Comparer;
import burp.api.montoya.decoder.Decoder;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.http.Http;
import burp.api.montoya.intruder.Intruder;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.project.Project;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.repeater.Repeater;
import burp.api.montoya.scanner.Scanner;
import burp.api.montoya.scope.Scope;
import burp.api.montoya.sitemap.SiteMap;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.utilities.Utilities;
import burp.api.montoya.websocket.WebSockets;
import com.bureauveritas.modelparser.BurpApi;
import org.junit.jupiter.api.BeforeAll;

import java.io.PrintStream;

public class MontoyaTest {
    @BeforeAll
    static void setUp() {
        // Initialize BurpApi so that null exception isn't thrown
        try {
            BurpApi.setApi(new MontoyaApi() {
                @Override
                public Ai ai() {
                    return null;
                }

                @Override
                public Bambda bambda() {
                    return null;
                }

                @Override
                public BurpSuite burpSuite() {
                    return null;
                }

                @Override
                public Collaborator collaborator() {
                    return null;
                }

                @Override
                public Comparer comparer() {
                    return null;
                }

                @Override
                public Decoder decoder() {
                    return null;
                }

                @Override
                public Extension extension() {
                    return null;
                }

                @Override
                public Http http() {
                    return null;
                }

                @Override
                public Intruder intruder() {
                    return null;
                }

                @Override
                public Logging logging() {
                    return new Logging() {
                        @Override
                        public PrintStream output() {
                            return System.out;
                        }

                        @Override
                        public PrintStream error() {
                            return System.err;
                        }

                        @Override
                        public void logToOutput(String s) {
                            System.out.println(s);
                        }

                        @Override
                        public void logToOutput(Object object) {
                            System.out.println(object);
                        }

                        @Override
                        public void logToError(String s) {
                            System.err.println(s);
                        }

                        @Override
                        public void logToError(String s, Throwable throwable) {
                            System.err.println(s);
                            throwable.printStackTrace(System.err);
                        }

                        @Override
                        public void logToError(Throwable throwable) {
                            throwable.printStackTrace(System.err);
                        }

                        @Override
                        public void raiseDebugEvent(String s) {
                            System.err.println(s);
                        }

                        @Override
                        public void raiseInfoEvent(String s) {
                            System.out.println(s);
                        }

                        @Override
                        public void raiseErrorEvent(String s) {
                            System.err.println(s);
                        }

                        @Override
                        public void raiseCriticalEvent(String s) {
                            System.err.println(s);
                        }
                    };
                }

                @Override
                public Organizer organizer() {
                    return null;
                }

                @Override
                public Persistence persistence() {
                    return null;
                }

                @Override
                public Project project() {
                    return null;
                }

                @Override
                public Proxy proxy() {
                    return null;
                }

                @Override
                public Repeater repeater() {
                    return null;
                }

                @Override
                public Scanner scanner() {
                    return null;
                }

                @Override
                public Scope scope() {
                    return null;
                }

                @Override
                public SiteMap siteMap() {
                    return null;
                }

                @Override
                public UserInterface userInterface() {
                    return null;
                }

                @Override
                public Utilities utilities() {
                    return null;
                }

                @Override
                public WebSockets websockets() {
                    return null;
                }
            });
        }
        catch (RuntimeException e) {
            if (!e.getMessage().equals("api already set")) {
                throw e;
            }
        }
    }
}
