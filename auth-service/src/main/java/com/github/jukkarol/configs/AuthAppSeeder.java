package com.github.jukkarol.configs;

import com.github.jukkarol.model.User;
import com.github.jukkarol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthAppSeeder implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthAppSeeder.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(args.getOptionValues("seeder") != null){
            List<String> seeder = Arrays.asList(args.getOptionValues("seeder").get(0).split(","));
            if(seeder.contains("user")) {
                seedUsers();
                log.info("Success run user seeder");
            }
        }else{
            log.info("User seeder skipped");
        }
    }

    private void seedUsers(){
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setName("Admin User");
        user1.setEmail("admin@example.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setRoles(List.of("ROLE_ADMIN"));
        users.add(user1);

        User user2 = new User();
        user2.setName("ATM");
        user2.setEmail("atm@example.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setRoles(List.of("ROLE_ATM"));
        users.add(user2);

        User user3 = new User();
        user3.setName("Regular User");
        user3.setEmail("regularuser@example.com");
        user3.setPassword(passwordEncoder.encode("password123"));
        users.add(user3);

        var index = 0;
        for (var user : users) {
            this.userRepository.save(user);
            log.info("Success run UserSeeder {}", users.get(index).getName());
            index++;
        }
    }
}