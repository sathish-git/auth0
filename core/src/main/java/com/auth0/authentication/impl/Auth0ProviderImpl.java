package com.auth0.authentication.impl;

import com.adobe.cq.social.connect.oauth.ProviderUtils;
import com.adobe.granite.auth.oauth.Provider;
import com.adobe.granite.auth.oauth.ProviderType;
import com.auth0.authentication.config.Auth0ProviderConfiguration;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Provider.class,
        immediate = true,
        name = "Adobe Granite OAuth Auth0 Provider")
@Designate(ocd = Auth0ProviderConfiguration.class)
public class Auth0ProviderImpl implements Provider {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private ResourceResolver resourceResolver;

    private Session session;

    private static final String AUTH0_SUBSERVICE = "auth0";

    private String id;

    private ProviderType type;

    private String detailsURL;

    private Auth0Api auth0Api = new Auth0Api();

    private static final Logger LOG = LoggerFactory.getLogger(Auth0ProviderImpl.class);

    @Activate
    protected void activate(Auth0ProviderConfiguration config) throws Exception {

        LOG.info("**********Setting Auth0 configs**********");
        LOG.info("Provider Id: " + config.providerId());
        LOG.info("Token Url: " + config.tokenUrl());
        LOG.info("Authorization Url: " + config.authorizationUrl());
        LOG.info("Details Url: " + config.detailsURL());
        LOG.info("**********Auth0 configs set**********");


        this.id = config.providerId();
        this.type = ProviderType.OAUTH2;
        this.detailsURL = config.detailsURL();
        auth0Api.setAccessTokenEndpoint(config.tokenUrl());
        auth0Api.setAuthorizationUrl(config.authorizationUrl());

        Map<String, Object> credentialsMap = new HashMap<>();
        credentialsMap.put(ResourceResolverFactory.SUBSERVICE, AUTH0_SUBSERVICE);
        resourceResolver = resourceResolverFactory.getServiceResourceResolver(credentialsMap);
        session = resourceResolver.adaptTo(Session.class);
    }

    @Override
    public ProviderType getType() {
        return this.type;
    }

    @Override
    public Api getApi() {
        return this.auth0Api;
    }

    @Override
    public String getDetailsURL() {
        return this.detailsURL;
    }

    @Override
    public String[] getExtendedDetailsURLs(String s) {
        return new String[0];
    }

    @Override
    public String[] getExtendedDetailsURLs(String s, String s1, Map<String, Object> map) {
        return new String[0];
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String mapUserId(String userId, Map<String, Object> map) {
        return "auth0-" + userId;
    }

    @Override
    public String getUserFolderPath(String userId, String clientId, Map<String, Object> map) {
        LOG.info("userId: " + userId + " ;clientId: " + clientId);
        return "/home/users/auth0/" + userId.substring(0, 4);
    }

    @Override
    public Map<String, Object> mapProperties(String srcUrl, String clientId, Map<String, Object> existing, Map<String, String> newProperties) {
        if (srcUrl.equals(getDetailsURL())) {
            final Map<String, Object> mapped = new HashMap<>(existing);
            for (final Map.Entry<String, String> prop : newProperties.entrySet()) {
                final String mappedKey = prop.getKey();
                final Object mappedValue = prop.getValue();
                if (mappedValue != null) {
                    mapped.put(mappedKey, mappedValue);
                }
            }
            return mapped;
        }
        return existing;
    }

    @Override
    public String getAccessTokenPropertyPath(String clientId) {
        return "oauth/token";
    }

    @Override
    public String getOAuthIdPropertyPath(String clientId) {
        return "oauth/oauthid";
    }

    @Override
    public User getCurrentUser(SlingHttpServletRequest slingHttpServletRequest) {
        try {
            final Authorizable authorizable = slingHttpServletRequest.getResourceResolver().adaptTo(Authorizable.class);
            if (authorizable != null && authorizable.isGroup() && !authorizable.getID().equals("anonymous")) {
                return (User) authorizable;
            }
        } catch (RepositoryException e) {
            LOG.error("failed to identify user", e);
        }
        return null;
    }

    @Override
    public void onUserCreate(User user) {

    }

    @Override
    public void onUserUpdate(User user) {

    }

    @Override
    public OAuthRequest getProtectedDataRequest(String url) {
        return new OAuthRequest(Verb.POST, url);
    }

    @Override
    public Map<String, String> parseProfileDataResponse(Response response) throws IOException {
        return ProviderUtils.parseProfileDataResponse(response);
    }

    @Override
    public String getUserIdProperty() {
        return "email";
    }

    @Override
    public String getValidateTokenUrl(String s, String s1) {
        return null;
    }

    @Override
    public boolean isValidToken(String s, String s1, String s2) {
        return false;
    }

    @Override
    public String getUserIdFromValidateTokenResponseBody(String s) {
        return null;
    }

    @Override
    public String getErrorDescriptionFromValidateTokenResponseBody(String s) {
        return null;
    }

}
