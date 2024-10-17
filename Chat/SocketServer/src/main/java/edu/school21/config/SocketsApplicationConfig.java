package edu.school21.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.school21.repositories.UsersRepositoryImpl;
import edu.school21.server.Server;
import edu.school21.services.ChatRoomService;
import edu.school21.services.MessageService;
import edu.school21.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@PropertySource("classpath:db.properties")
@ComponentScan(basePackages = "edu.school21")
public class SocketsApplicationConfig {

    @Value("${db.driver}")
    private String driver;

    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        return new HikariDataSource(config);
    }

    @Bean
    public UsersRepositoryImpl usersRepositoryImpl(HikariDataSource dataSource) {
        return new UsersRepositoryImpl(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserServiceImpl userServiceImpl(UsersRepositoryImpl usersRepository, PasswordEncoder passwordEncoder) {
        return new UserServiceImpl(usersRepository, passwordEncoder);
    }

    @Bean
    public Server server(UserServiceImpl userService, MessageService messageService, ChatRoomService chatRoomService) {
        return new Server(userService, messageService, chatRoomService);
    }
}
