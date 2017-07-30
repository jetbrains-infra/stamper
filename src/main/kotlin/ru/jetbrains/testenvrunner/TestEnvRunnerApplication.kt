package ru.jetbrains.testenvrunner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import javax.inject.Inject

@EnableOAuth2Sso
@SpringBootApplication
class TestEnvRunnerApplication : WebSecurityConfigurerAdapter() {
    @Inject
    lateinit var tokenRepository: PersistentTokenRepository

    override fun configure(http: HttpSecurity) {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/login**", "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and().logout().logoutSuccessUrl("/").permitAll()
                .and().csrf().disable()
                .rememberMe()
                .tokenRepository(tokenRepository)
                .alwaysRemember(true)
                .rememberMeCookieName("rememberme")
                .tokenValiditySeconds(60 * 60 * 24)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(TestEnvRunnerApplication::class.java, *args)

}
