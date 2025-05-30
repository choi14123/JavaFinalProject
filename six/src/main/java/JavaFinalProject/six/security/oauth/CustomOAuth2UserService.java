package JavaFinalProject.six.security.oauth;

import JavaFinalProject.six.user.dto.response.LoginResponse;
import JavaFinalProject.six.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService( @Lazy UserService userService) {
        this.userService = userService;
    }
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // JWT 발급
        String jwt = userService.handleOAuth2Login(oAuth2User);
        System.out.println("JWT : "+ jwt);
        // 리디렉션용 URL에 JWT 포함 (프론트에 전달)
        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:8080/oauth/callback.html")
                .queryParam("token", jwt)
                .build()
                .toUriString();

        try {
            // Spring Security 흐름 우회 -> Redirect 직접 수행
            HttpServletResponse response =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            System.out.println("Redirecting to "+redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new OAuth2AuthenticationException("리디렉션 실패");
        }

        // 의미 없는 유저 리턴 (실제 사용 X)
        return oAuth2User;
    }

}
