package com.emintolgahanpolat.api.configuration.security.controller;

import com.emintolgahanpolat.api.configuration.captcha.CaptchaService;
import com.emintolgahanpolat.api.configuration.security.AuthenticationRequest;
import com.emintolgahanpolat.api.configuration.security.AuthenticationResponse;
import com.emintolgahanpolat.api.configuration.security.JWTUtil;
import com.emintolgahanpolat.api.configuration.security.JwtUser;
import com.emintolgahanpolat.api.configuration.security.user.*;
import com.emintolgahanpolat.api.exceptions.ProcessException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE})
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CaptchaService captchaService;

    @GetMapping(value = "")
    public String home() {
        return "Hoş Geldiniz.";
    }


    //  var image = new Image();
    //  image.src = 'data:image/png;base64,'+ JSON.parse(document.getElementsByTagName("pre")[0].innerHTML).token;
    //  document.body.appendChild(image);
    @ApiOperation(value = "Base64 Captcha Getir")
    @GetMapping(value = "captcha", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getCaptcha() {
        HashMap<String, String> map = new HashMap<>();
        map.put("token", captchaService.generateCaptchaBase64());
        return ResponseEntity.ok(map);
    }

    @ApiOperation(value = "Test için Captcha Cevabı Getir")
    @GetMapping(value = "captchaAnswer")
    public ResponseEntity<?> getCaptchaAnswer() {
        HashMap<String, String> map = new HashMap<>();
        map.put("answer", captchaService.generateCaptcha().getAnswer());
        return ResponseEntity.ok(map);
    }

    @ApiOperation(value = "Oturum Aç")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Token")
    }
    )

    @PostMapping(value = "signin")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, @RequestHeader String captcha) {
        if (!captchaService.validateCaptcha(captcha)) {
            throw new ProcessException("Invalid Captcha");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @ApiOperation(value = "Token Yenile")
    @GetMapping(value = "refresh")
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtUtil.refreshToken(token);
            return ResponseEntity.ok(new AuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @ApiOperation(value = "Kayıt Ol")
    @PostMapping(value = "signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User signUpRequest, @RequestHeader String captcha) throws ProcessException {

        if (!captchaService.validateCaptcha(captcha)) {
            throw new ProcessException("Invalid Captcha");
        }
        String password = signUpRequest.getPassword();
        if (userRepository.existsUsersByUsername(signUpRequest.getUsername())) {
            throw new ProcessException("Bu kullanıcı adı daha önce kullanılıyor.");
        }

        if (userRepository.existsUsersByEmail(signUpRequest.getEmail())) {
            throw new ProcessException("Bu mail adresi kayıtlı.");
        }


        signUpRequest.setEnabled(true);
        signUpRequest.setPassword(encoder.encode(signUpRequest.getPassword()));
        signUpRequest.setLastPasswordResetDate(new Date());
        List<Role> strRoles = signUpRequest.getRoles();
        List<Role> roles = new ArrayList<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.getName().toString()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN);
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(RoleName.ROLE_MODERATOR);
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER);
                        roles.add(userRole);
                }
            });
        }

        signUpRequest.setRoles(roles);
        userRepository.save(signUpRequest);


        return createAuthenticationToken(new AuthenticationRequest(signUpRequest.getUsername(), password), captchaService.generateCaptcha().getAnswer());
    }

    @ApiOperation(value = "Kullanıcı Bilgisi Getir")
    @GetMapping(value = "user")
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        return (JwtUser) userDetailsService.loadUserByUsername(username);
    }
}
