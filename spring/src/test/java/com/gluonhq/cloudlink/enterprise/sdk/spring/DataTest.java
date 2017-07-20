/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2017, Gluon Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.cloudlink.enterprise.sdk.spring;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.Assert.*;

public class DataTest {

    private Gson gson = new Gson();

    @Test
    public void getObject() {
        String identifier = UUID.randomUUID().toString();

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                if (request.method() == HttpMethod.GET) {
                    int idx = request.absoluteURI().lastIndexOf("/");
                    if (idx == -1) {
                        request.response().setStatusCode(500).end("Invalid URI: " + request.absoluteURI());
                    } else {
                        String objectIdentifier = request.absoluteURI().substring(idx + 1);
                        if (objectIdentifier.equals(identifier)) {
                            try {
                                StringWriter payload = new StringWriter();
                                gson.newJsonWriter(payload)
                                        .beginObject()
                                        .name("foo").value("bar")
                                        .name("zee").value(1)
                                        .endObject()
                                        .close();
                                StringWriter object = new StringWriter();
                                gson.newJsonWriter(object)
                                        .beginObject()
                                        .name("uid").value(identifier)
                                        .name("payload").value(payload.toString())
                                        .endObject()
                                        .close();
                                request.response().setStatusCode(200)
                                        .end(object.toString());
                            } catch (IOException e) {
                                request.response().setStatusCode(500).end(e.getMessage());
                            }
                        } else {
                            request.response().setStatusCode(500).end("Wrong object identifier, expected: <" + identifier + " but was <" + objectIdentifier + ">");
                        }
                    }
                } else {
                    request.response().setStatusCode(500).end("Expected: <GET> but was <" + request.method() + ">");
                }
            });

            CloudLinkClientConfig config = new CloudLinkClientConfig("http://localhost:45010", "");
            config.setLogLevel(Level.FINE);
            CloudLinkClient client = new CloudLinkClient(config);

            Sample sample = client.getObject(identifier, Sample.class);
            assertNotNull(sample);
            assertEquals("bar", sample.getFoo());
            assertEquals(1, sample.getZee());
        } catch (CloudLinkClientException e) {
            fail(e.getBody());
        } finally {
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    @Test
    public void getNonExistingObject() {
        String identifier = UUID.randomUUID().toString();

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("payload", "{}");
                request.response().setStatusCode(200)
                        .end(jsonObject.toString());
            });

            CloudLinkClientConfig config = new CloudLinkClientConfig("http://localhost:45010", "");
            config.setLogLevel(Level.FINE);
            CloudLinkClient client = new CloudLinkClient(config);

            Sample sample = client.getObject(identifier, Sample.class);
            assertNull(sample);
        } catch (CloudLinkClientException e) {
            fail(e.getBody());
        } finally {
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    @Test
    public void addObject() {
        String identifier = UUID.randomUUID().toString();

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                if (request.method() == HttpMethod.POST) {
                    int idx1 = request.absoluteURI().lastIndexOf("/");
                    int idx2 = request.absoluteURI().lastIndexOf("/", idx1 - 1);
                    if (idx1 == -1 || idx2 == -1) {
                        request.response().setStatusCode(500).end("Invalid URI: " + request.absoluteURI());
                    } else {
                        String contentType = request.getHeader("content-type");
                        if (!contentType.startsWith("application/json")) {
                            request.response().setStatusCode(500).end("Wrong content type, expected: <application/json> but was <" + request.getHeader("content-type") + ">");
                        } else {
                            String objectIdentifier = request.absoluteURI().substring(idx2 + 1, idx1);
                            if (objectIdentifier.equals(identifier)) {
                                request.bodyHandler(buffer -> {
                                    String target = buffer.getString(0, buffer.length());
                                    JsonObject json = new JsonParser().parse(target).getAsJsonObject();
                                    if (!json.has("foo") || !json.get("foo").getAsString().equals("bar")) {
                                        request.response().setStatusCode(500).end("Invalid target: " + target);
                                    } else if (!json.has("zee") || json.get("zee").getAsInt() != 1) {
                                        request.response().setStatusCode(500).end("Invalid target: " + target);
                                    } else {
                                        JsonObject response = new JsonObject();
                                        response.addProperty("uid", identifier);
                                        response.addProperty("payload", target);
                                        request.response().setStatusCode(200).end(response.toString());
                                    }
                                });
                            } else {
                                request.response().setStatusCode(500).end("Wrong object identifier, expected: <" + identifier + " but was <" + objectIdentifier + ">");
                            }
                        }
                    }
                } else {
                    request.response().setStatusCode(500).end("Expected: <POST> but was <" + request.method() + ">");
                }
            });

            CloudLinkClientConfig config = new CloudLinkClientConfig("http://localhost:45010", "");
            config.setLogLevel(Level.FINE);
            CloudLinkClient client = new CloudLinkClient(config);

            Sample sample = new Sample();
            sample.setFoo("bar");
            sample.setZee(1);

            Sample stored = client.addObject(identifier, sample);
            assertNotNull(stored);
            assertEquals(sample.getFoo(), stored.getFoo());
            assertEquals(sample.getZee(), stored.getZee());
        } catch (CloudLinkClientException e) {
            fail(e.getBody());
        } finally {
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    @Test
    public void addStringObject() {
        String identifier = UUID.randomUUID().toString();
        String sample = "sample!";

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                request.bodyHandler(buffer -> {
                    String body = buffer.getString(0, buffer.length());
                    if (!body.equals("{\"v\":\"" + sample + "\"}")) {
                        request.response()
                                .setStatusCode(500)
                                .end("Invalid body, expected: <{\"v\":\"" + sample + "\"} but was: <" + body + ">");
                    } else {
                        try {
                            StringWriter payload = new StringWriter();
                            gson.newJsonWriter(payload).beginObject()
                                    .name("v").value(sample)
                                    .endObject().close();
                            StringWriter object = new StringWriter();
                            gson.newJsonWriter(object).beginObject()
                                    .name("uid").value(identifier).name("payload").value(payload.toString())
                                    .endObject().close();

                            request.response().end(object.toString());
                        } catch (IOException e) {
                            request.response().setStatusCode(500).end(e.getMessage());
                        }
                    }
                });
            });

            CloudLinkClientConfig config = new CloudLinkClientConfig("http://localhost:45010", "");
            config.setLogLevel(Level.FINE);
            CloudLinkClient client = new CloudLinkClient(config);

            String stored = client.addObject(identifier, sample);
            assertNotNull(stored);
            assertEquals(sample, stored);
        } finally {
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    private HttpServer startHttpServer(Handler<HttpServerRequest> requestHandler) {
        HttpServerResponse httpServerResponse = new HttpServerResponse();

        CountDownLatch latch = new CountDownLatch(1);
        HttpServer httpServer = Vertx.vertx().createHttpServer();
        httpServer.requestHandler(requestHandler);
        httpServer.listen(45010, asyncResult -> {
            httpServerResponse.httpServer = asyncResult.result();
            if (asyncResult.failed()) {
                asyncResult.cause().printStackTrace();
            }
            latch.countDown();
        });

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }

        return httpServerResponse.httpServer;
    }

    private static class HttpServerResponse {
        HttpServer httpServer;
    }

    public static class Sample {
        private String foo;
        private int zee;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public int getZee() {
            return zee;
        }

        public void setZee(int zee) {
            this.zee = zee;
        }
    }
}
