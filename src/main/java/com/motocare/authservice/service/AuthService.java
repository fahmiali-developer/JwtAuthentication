package com.motocare.authservice.service;
import com.motocare.authservice.client.WorkshopsClient;
import com.motocare.authservice.dto.AuthRequest;
import com.motocare.authservice.dto.Role;
import com.motocare.authservice.entity.UserEntity;
import com.motocare.authservice.exception.BadRequestException;
import com.motocare.authservice.exception.UnauthorizedException;
import com.motocare.authservice.repository.UserRepository;
import com.motocare.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkshopsClient workshopClient;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       WorkshopsClient workshopClient,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.workshopClient = workshopClient;
        this.jwtUtil = jwtUtil;
    }

    public String register(String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        userRepository.save(user);

        return jwtUtil.generateToken(user);
    }

    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return jwtUtil.generateToken(user);
    }

    public String registerAdmin(AuthRequest request) {

        try {
            // 1. validasi email
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            // 2. create workshop (ke service motocare)
            Long workshopId = workshopClient.createWorkshop(
                    request.getWorkshopName(),
                    request.getAddress()
            );

            if (workshopId == null) {
                throw new RuntimeException("Failed to create workshop");
            }

            // 3. create user
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.ADMIN); // 🔥 set di backend
            user.setWorkshopId(workshopId);

            userRepository.save(user);

            // 4. generate JWT
            return jwtUtil.generateToken(user);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public void createAdminByAdmin(String email, String password, Long workshopId) {
        try {
            // ❌ email sudah ada
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already registered");
            }

            // 🔐 validasi workshop exists (optional kalau dari auth-service)
            // workshopClient.getWorkshopById(workshopId);

            // 🧱 create user
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(Role.ADMIN);
            user.setActive(true);
            user.setWorkshopId(workshopId); // 🔥 kunci utamanya

            userRepository.save(user);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}