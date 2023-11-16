package com.example.shop.Controllers;

import com.example.shop.models.Role;
import com.example.shop.models.User;
import com.example.shop.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }




    @GetMapping("/reg")
    public String reg(@RequestParam(name = "error", defaultValue = "", required = false) String error, Model model) {
        if(error.equals("username")) {
            model.addAttribute("error", "Такой логин пользователя уже занят");
        }
        return "reg";
    }

    @GetMapping("/user")
    public String user(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());

        return "user";
    }

    @PostMapping("/user/update")
    public String updateUser(Principal principal, User userForm) {
        // Находим пользвоателя по имени авторизованого пользователя
        User user = userRepository.findByUsername(principal.getName());
        // Устанавливаем для этого пользователя новые данные
        user.setUsername(userForm.getUsername());
        // Делаем хэш пароля
        if(userForm.getPassword() == ""){
            user.setPassword(userForm.getPassword());
        }
        String pass = passwordEncoder.encode(userForm.getPassword());
        user.setPassword(pass);
        user.setRoles(userForm.getRoles());


        // Сохраняем (обновляем) данные про пользователя
        userRepository.save(user);
        // Выполняем редирект
        return "redirect:/user";
    }

    @PostMapping("/reg")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password) {
        if(userRepository.findByUsername(username) != null) {
            return "redirect:/reg?error=username";
        }

        password = passwordEncoder.encode(password);
        User user = new User(username, password, email, true, Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }

}
