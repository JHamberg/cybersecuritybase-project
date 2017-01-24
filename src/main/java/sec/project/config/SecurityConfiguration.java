package sec.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // *** A8-Cross-Site Request Forgery ***
        // This allows foreign sites to perform requests as if they
        // we performed by the user, leading to potentially unwanted
        // records and/or data erasure

        // Fix: Remove this line and line 17 in WebConfig.java.
        http.csrf().disable();


        // *** A5 Security misconfiguration ***
        // This is an unnecessary feature page which exposes underlying
        // filesystem structure and potentially lets user insert scripts
        // and queries on the database.

        // Fix: Remove the following line and uncomment the ones below it.
        http.authorizeRequests().anyRequest().permitAll();
        // http.authorizeRequests().antMatchers("/form", "/queue", "/appointment", "/redirect").permitAll();
        // http.authorizeRequests().antMatchers("/h2-console").denyAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
