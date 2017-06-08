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
package com.gluonhq.cloudlink.client.enterprise.javaee;

import com.gluonhq.cloudlink.client.enterprise.CloudLinkClient;
import com.gluonhq.cloudlink.client.enterprise.CloudLinkClientException;
import com.gluonhq.cloudlink.client.enterprise.CloudLinkConfig;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotification;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotificationTarget;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.Assert.fail;

public class PushTest {

    @Test
    public void sendPushNotification() {
        String identifier = UUID.randomUUID().toString();

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                request.setExpectMultipart(true);

                if (request.method() == HttpMethod.POST) {
                    request.endHandler(event -> {
                        if (!"Title".equals(request.getFormAttribute("title"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <Title> but was: <" + request.getFormAttribute("title") + ">");
                        } else if (!"Body".equals(request.getFormAttribute("body"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <Body> but was: <" + request.getFormAttribute("body") + ">");
                        } else if (!"0".equals(request.getFormAttribute("deliveryDate"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <0> but was: <" + request.getFormAttribute("deliveryDate") + ">");
                        } else if (!"HIGH".equals(request.getFormAttribute("priority"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <HIGH> but was: <" + request.getFormAttribute("priority") + ">");
                        } else if (!"DAYS".equals(request.getFormAttribute("expirationType"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <DAYS> but was: <" + request.getFormAttribute("expirationType") + ">");
                        } else if (!"5".equals(request.getFormAttribute("expirationAmount"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <5> but was: <" + request.getFormAttribute("expirationAmount") + ">");
                        } else if (!"ALL_DEVICES".equals(request.getFormAttribute("targetType"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <ALL_DEVICES> but was: <" + request.getFormAttribute("targetType") + ">");
                        } else if (!"false".equals(request.getFormAttribute("invisible"))) {
                            request.response().setStatusCode(500)
                                    .end("Expected: <false> but was <" + request.getFormAttribute("invisible") + ">");
                        } else {
                            request.response()
                                    .setStatusCode(200)
                                    .end("{\"identifier\":\"" + identifier + "\"}");
                        }
                    });
                } else {
                    request.response().setStatusCode(500).end("Expected: <POST> but was <" + request.method() + ">");
                }
            });

            CloudLinkConfig config = new CloudLinkConfig("http://localhost:45010", "");
            config.setLogLevel(Level.FINE);
            CloudLinkClient client = new JavaEECloudLinkClient(config);

            PushNotification pushNotification = new PushNotification();
            pushNotification.setTitle("Title");
            pushNotification.setBody("Body");
            pushNotification.setPriority(PushNotification.Priority.HIGH);
            pushNotification.setExpirationType(PushNotification.ExpirationType.DAYS);
            pushNotification.setExpirationAmount(5);
            pushNotification.getTarget().setType(PushNotificationTarget.Type.ALL_DEVICES);

            PushNotification notification = client.sendPushNotification(pushNotification);

            Assert.assertNotNull(notification);
            Assert.assertEquals(identifier, notification.getIdentifier());
        } catch (CloudLinkClientException e) {
            fail(e.getBody());
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
}
