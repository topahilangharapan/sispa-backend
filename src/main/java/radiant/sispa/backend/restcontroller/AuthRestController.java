package radiant.sispa.backend.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import radiant.sispa.backend.restdto.request.LoginJwtRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.LoginJwtResponseDTO;
import radiant.sispa.backend.restservice.UserRestService;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.Date;


@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    @Autowired
    UserRestService userService;


    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginJwtRequestDTO loginJwtRequestDTO) {
        var baseResponseDTO = new BaseResponseDTO<LoginJwtResponseDTO>();
        try {
            UserDetails userDetails = userDetailService.loadUserByUsername(loginJwtRequestDTO.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginJwtRequestDTO.getUsername(), loginJwtRequestDTO.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginJwtResponseDTO loginJwtResponseDTO = new LoginJwtResponseDTO();
            loginJwtResponseDTO.setToken(jwtUtils.generateJwtToken(userDetails.getUsername()));

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(loginJwtResponseDTO);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Login Successful!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Wrong username or Password!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.UNAUTHORIZED);
        }
    }
}
