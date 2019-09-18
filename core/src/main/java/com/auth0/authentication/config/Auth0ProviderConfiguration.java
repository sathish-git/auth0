package com.auth0.authentication.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Adobe Granite OAuth Auth0 Provider Configuration", description = "Configuration for Auth0 Provider")
public @interface Auth0ProviderConfiguration {

    @AttributeDefinition(name = "Provider ID", defaultValue = "auth0", description = "Assign a unique Provider ID")
    String providerId();

    @AttributeDefinition(name = "Token URL", defaultValue = "https://dev-authenticator.au.auth0.com/oauth/token", description = "Auth0 Token URL")
    String tokenUrl();

    @AttributeDefinition(name = "Authorization URL", defaultValue = "https://dev-authenticator.au.auth0.com/authorize", description = "Auth0 Authorization URL")
    String authorizationUrl();

    @AttributeDefinition(name = "User Info URL", defaultValue = "https://dev-authenticator.au.auth0.com/userinfo", description = "Auth0 User Info URL")
    String detailsURL();

}
