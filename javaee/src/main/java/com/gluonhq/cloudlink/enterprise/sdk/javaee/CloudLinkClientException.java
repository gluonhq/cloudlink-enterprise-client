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
package com.gluonhq.cloudlink.enterprise.sdk.javaee;

/**
 * Exception for handling invalid status codes of HTTP calls.
 */
public class CloudLinkClientException extends RuntimeException {

    private int status;
    private String body;

    /**
     * Creates a new CloudLinkClientException with the specified status and message.
     *
     * @param status the http status code of the response
     * @param message the http reason phrase of the response
     */
    public CloudLinkClientException(int status, String message) {
        super(message);

        this.status = status;
        this.body = null;
    }

    /**
     * Creates a new CloudLinkClientException with the specified status, message and body.
     *
     * @param status the http status code of the response
     * @param message the http reason phrase of the response
     * @param body the http body of the response
     */
    public CloudLinkClientException(int status, String message, String body) {
        super(message);

        this.status = status;
        this.body = body;
    }

    /**
     * Returns the http status code that was defined in the http response.
     *
     * @return the http status code of the response
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the http body that was returned in the http response.
     *
     * @return the http body of the response
     */
    public String getBody() {
        return body;
    }
}
