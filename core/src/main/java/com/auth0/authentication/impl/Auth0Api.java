package com.auth0.authentication.impl;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;

public class Auth0Api extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&audience=API_AUDIENCE&state=STATE";

    private static final String AUTHORIZE_RULE_NO_SCOPE = "?response_type=code&client_id=%s&redirect_uri=%s&audience=API_AUDIENCE&state=STATE";

    private String accessTokenEndpoint;

    private String authorizationUrl;

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    public void setAccessTokenEndpoint(String accessTokenEndpoint) {
        this.accessTokenEndpoint = accessTokenEndpoint;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return this.accessTokenEndpoint;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig oAuthConfig) {
        if (oAuthConfig.hasScope()) {
            return String.format(this.authorizationUrl.concat(AUTHORIZE_URL), oAuthConfig.getApiKey(), oAuthConfig.getCallback(), oAuthConfig.getScope());
        }
        return String.format(this.authorizationUrl.concat(AUTHORIZE_RULE_NO_SCOPE), oAuthConfig.getApiKey(), oAuthConfig.getCallback());
    }
}
