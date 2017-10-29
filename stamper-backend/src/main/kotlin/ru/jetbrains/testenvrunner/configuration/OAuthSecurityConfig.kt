package ru.jetbrains.testenvrunner.configuration

import org.springframework.beans.factory.annotation.Configurable
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


/**
 * Modifying or overriding the default spring boot security.
 */
@Configurable
@EnableWebSecurity
class OAuthSecurityConfig(val oauth2ClientContext: OAuth2ClientContext,
                          val authorizationCodeResourceDetails: AuthorizationCodeResourceDetails,
                          val resourceServerProperties: ResourceServerProperties) : WebSecurityConfigurerAdapter() {

    /**
     * Method for creating filter for OAuth authentication
     *
     * @return OAuth2ClientAuthenticationProcessingFilter
     */
    private fun filter(): OAuth2ClientAuthenticationProcessingFilter {
        // Creating the filter for "/login" url
        val oAuth2Filter = OAuth2ClientAuthenticationProcessingFilter(
                "/login")


        // Creating the rest template for getting connected with OAuth service.
        // The configuration parameters will inject while creating the bean.
        val oAuth2RestTemplate = OAuth2RestTemplate(authorizationCodeResourceDetails,
                oauth2ClientContext)
        oAuth2Filter.setRestTemplate(oAuth2RestTemplate)


        // setting the token service. It will help for getting the token and
        // user details from the OAuth Service
        oAuth2Filter.setTokenServices(UserInfoTokenServices(resourceServerProperties.userInfoUri,
                resourceServerProperties.clientId))
        return oAuth2Filter
    }


    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/", "/**", "/**.js").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()//
                .logout()//
                .logoutSuccessUrl("/")//
                .permitAll()//
                .and()//
                .addFilterAt(filter(), BasicAuthenticationFilter::class.java)
                .csrf().disable()
    }
}