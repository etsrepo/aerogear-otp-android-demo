/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aerogear.otp.android.demo;

import android.app.Application;
import android.support.v4.app.FragmentActivity;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.authentication.AuthenticationConfig;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.Pipe;

import java.net.URI;
import java.net.URL;
import java.util.List;

public class OTPApplication extends Application {

    private String baseURL = "http://jaxrs-sblanc.rhcloud.com/rest";
    private Authenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();

        authenticator = new Authenticator(baseURL);
        AuthenticationConfig config = new AuthenticationConfig();
        config.setLoginEndpoint("/auth/login");
        authenticator.auth("login", config);
    }

    public void login(FragmentActivity activity, String username, String password, Callback<HeaderAndBody> callback) {
        AuthenticationModule authModule = authenticator.get("login", activity);
        authModule.login(username, password, callback);
    }

    public void retrieveOTPPath(FragmentActivity activity, Callback<List<OTPUser>> callback) throws Exception {
        AuthenticationModule authModule = authenticator.get("login", activity);

        URL url = new URL(baseURL);

        Pipeline pipeline = new Pipeline(url);

        PipeConfig pipeConfig = new PipeConfig(url, OTPUser.class);
        pipeConfig.setEndpoint("/auth/otp/secret");
        pipeConfig.setAuthModule(authModule);

        Pipe<OTPUser> pipe = pipeline.pipe(OTPUser.class, pipeConfig);

        pipe.read(callback);
    }

}
