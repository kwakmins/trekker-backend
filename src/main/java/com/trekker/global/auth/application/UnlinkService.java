package com.trekker.global.auth.application;

import com.trekker.domain.member.entity.Member;
import com.trekker.domain.member.entity.SocialProvider;
import com.trekker.global.config.redis.dao.RedisRepository;
import com.trekker.global.exception.custom.BusinessException;
import com.trekker.global.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnlinkService {

    private final RestTemplate restTemplate;
    private final RedisRepository redisRepository;

    @Value("${kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;
    @Value("${kakao.unlink-url}")
    private String KAKAO_UNLINK_URL;
    @Value("${google.unlink-url}")
    private String GOOGLE_UNLINK_URL;

    /**
     * 소셜 계정 연결 해제
     *
     * @param member 연결 해제할 회원
     */
    public void unlink(Member member) {
        SocialProvider socialProvider = member.getSocialProvider();

        try {
            switch (socialProvider.getProvider().toLowerCase()) {
                case "kakao":
                    unlinkKakao(socialProvider.getProviderId());
                    break;
                case "google":
                    unlinkGoogle(member.getEmail());
                    break;
                default:
                    throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
            }
        } catch (BusinessException e) {
            log.error("소셜 연결 해제 실패: 사용자 ID = {}, Error = {}", socialProvider.getProviderId(),
                    e.getMessage());
            throw e;
        }
    }

    /**
     * 카카오 연결 해제
     *
     * @param providerId 소셜 제공자의 고유 ID
     */
    private void unlinkKakao(String providerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", providerId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(KAKAO_UNLINK_URL, request, String.class);
            log.info("카카오 연결 끊기 성공: 사용자 ID = {}", providerId);
        } catch (Exception e) {
            log.error("카카오 연결 끊기 실패: 사용자 ID = {}", providerId, e);
            throw new BusinessException(providerId, "providerId", ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }

    /**
     * 구글 연결 해제
     *
     * @param email 사용자 이메일
     */
    private void unlinkGoogle(String email) {
        String refreshToken = redisRepository.fetchAndDeleteSocialRefreshToken(email);

        if (refreshToken == null) {
            log.error("구글 연결 끊기 실패: Refresh Token이 없습니다.");
            throw new BusinessException(email, "refreshToken", ErrorCode.SOCIAL_UNLINK_FAILED);
        }

        String url = GOOGLE_UNLINK_URL + "?token=" + refreshToken;

        try {
            restTemplate.postForEntity(url, null, String.class);
            log.info("구글 연결 끊기 성공: 사용자 이메일 = {}", email);
        } catch (Exception e) {
            log.error("구글 연결 끊기 실패: Refresh Token = {}", refreshToken, e);
            throw new BusinessException(refreshToken, "refreshToken",
                    ErrorCode.SOCIAL_UNLINK_FAILED);
        }
    }
}