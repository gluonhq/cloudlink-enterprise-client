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

import java.util.logging.Level;

/**
 * A configuration class that is used for defining meta-data on a {@link CloudLinkClient} instance.
 */
public class CloudLinkClientConfig {

    private String hostname;
    private String serverKey;
    private Level logLevel = Level.OFF;

    /**
     * Create a new configuration using the default host name, cloud.gluonhq.com, to connect to Gluon CloudLink.
     *
     * @param serverKey the server key of your Gluon CloudLink Application
     */
    public CloudLinkClientConfig(String serverKey) {
        this.hostname = "cloud.gluonhq.com";
        this.serverKey = serverKey;
    }

    /**
     * Create a new configuration using the specified host name to connect to Gluon CloudLink.
     *
     * @param hostname the host name to use when connecting to Gluon CloudLink
     * @param serverKey the server key of your Gluon CloudLink Application
     */
    public CloudLinkClientConfig(String hostname, String serverKey) {
        this.hostname = hostname;
        this.serverKey = serverKey;
    }

    /**
     * Returns the host name that is used when connecting to Gluon CloudLink.
     *
     * @return the host name to use when connecting to Gluon CloudLink.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the host name to use when connecting to Gluon CloudLink.
     *
     * @param hostname the host name to use when connecting to Gluon CloudLink
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Returns the server key that will be used to authenticate your Gluon CloudLink Application when connecting to
     * Gluon CloudLink. The server key can be obtained from the <a href="https://gluon.io">Gluon Dashboard</a> in
     * the <code>Server</code> tab of the <code>Credentials</code> page.
     *
     * @return the server key that is used for authenticating your Gluon CloudLink Application
     */
    public String getServerKey() {
        return serverKey;
    }

    /**
     * Sets the server key to use for authentication your Gluon CloudLink Application.
     *
     * @param serverKey the server key that is used for authentication your Gluon CloudLink Application
     */
    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }

    /**
     * Returns the log level of the CloudLinkClient.
     *
     * @return the log level for the CloudLinkClient
     */
    public Level getLogLevel() {
        return logLevel;
    }

    /**
     * Sets the log level of the CloudLinkClient. The log level is used to determine what information about the
     * requests to Gluon CloudLink will be logged.
     *
     * @param logLevel the log level for the CloudLinkClient
     */
    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }
}
