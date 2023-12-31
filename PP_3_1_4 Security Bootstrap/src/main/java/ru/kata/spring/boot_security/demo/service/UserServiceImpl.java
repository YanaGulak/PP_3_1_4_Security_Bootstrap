package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }


    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User findUserById(Long id) {
        Optional<User> userFromDB = userRepository.findById(id);
        if (userFromDB.isEmpty()) {
            throw new UsernameNotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        return userFromDB.get();
    }

    @Transactional
    @Override
    public User findByUsername(String username) {
        User userDB = userRepository.findByUsername(username);
        if (userDB == null) {
            throw new UsernameNotFoundException(String.format("Пользователь с логином = %s не найден", username));
        }
        return userDB;
    }

    @Transactional
    @Override
    public boolean createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;//юзер есть в БД
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
        return true;
    }

    @Transactional
    @Override
    public void update(User updateUser, Long id){
        User user = userRepository.findById(id).get();
        user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            userRepository.save(user);
    }


    @Transactional
    @Override
    public boolean deleteUserById(long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
