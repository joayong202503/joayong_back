package com.joayong.skillswap.service;

import com.joayong.skillswap.domain.user.dto.request.LoginRequest;
import com.joayong.skillswap.domain.user.dto.request.SignUpRequest;
import com.joayong.skillswap.domain.user.dto.response.DuplicateCheckResponse;
import com.joayong.skillswap.domain.user.dto.response.UserProfileResponse;
import com.joayong.skillswap.domain.user.entity.User;
import com.joayong.skillswap.exception.ErrorCode;
import com.joayong.skillswap.exception.UserException;
import com.joayong.skillswap.jwt.JwtTokenProvider;
import com.joayong.skillswap.repository.UserRepository;
import com.joayong.skillswap.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@Slf4j
@Transactional // 트랜잭션 처리
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final FileUploadUtil fileUploadUtil;

    // 회원가입 중간처리
    public void signUp(SignUpRequest signUpRequest) {

        /*
            Race condition 방지

            사용자가 중복체크 후 회원가입 버튼을 누르기 전까지의 시간동안
            다른 사용자가 같은 값으로 가입할 수 있음
            이를 최종 회원가입에서 한번 더 검사해서 방지
         */
        String email = signUpRequest.getEmail();
        if (email.contains("@")) {
            userRepository.findByEmail(email)
                    .ifPresent(m -> {
                        throw new UserException(ErrorCode.DUPLICATE_EMAIL);
                    });
        } else {
            userRepository.findByName(signUpRequest.getName())
                    .ifPresent(m -> {
                        throw new UserException(ErrorCode.DUPLICATE_USERNAME);
                    });
        }



        // 순수 비밀번호를 꺼내서 암호화
        String rawPassword = signUpRequest.getPassword();
        // 암호화된 패스워드
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 회원정보를 엔터티로 변환
        User newUser = signUpRequest.toEntity();
        // 패스워드를 인코딩패스워드로 교체
        newUser.setPassword(encodedPassword);

        // DB에 전송
        userRepository.save(newUser);
    }


    public DuplicateCheckResponse checkDuplicate(String type,String value) {

        switch (type) {
            case "email":
                log.info("duplicate result : "+userRepository.findByEmail(value));
                // 중복된 경우를 클라이언트에게 알려야 함
                return userRepository.findByEmail(value)
                        .map(m -> DuplicateCheckResponse.unavailable("이미 사용 중인 이메일입니다."))
                        .orElse(DuplicateCheckResponse.available());
            case "name":
                log.info("duplicate result : "+userRepository.findByName(value));
                return userRepository.findByName(value)
                        .map(m -> DuplicateCheckResponse.unavailable("이미 사용 중인 사용자 이름입니다."))
                        .orElse(DuplicateCheckResponse.available());
            default:
                throw new UserException(ErrorCode.INVALID_SIGNUP_DATA);
        }
    }

    // 로그인 처리 (인증 처리)
    /*
        1. 클라이언트가 전달한 계정명(이메일)과
        패스워드를 받아야 함
        2. 계정명을 데이터베이스에 조회해서 존재하는지 유무 확인
        3. 존재한다면 회원정보를 데이터베이스에서 받아와서 비밀번호를 꺼내옴
        4. 패스워드 일치를 검사
     */
    @Transactional(readOnly = true)
    public Map<String, Object> authenticate(LoginRequest loginRequest) {

        // 로그인 시도하는 계정명 (이메일)
        String email = loginRequest.getEmail();

        User foundMember = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 입력한 패스워드와 DB에 저장된 패스워드를 추출
        String inputPassword = loginRequest.getPassword();
        String storedPassword = foundMember.getPassword();

        // 비번이 일치하지 않으면 예외 발생
        // 암호화된 비번을 디코딩해서 비교해야 함
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new UserException(ErrorCode.INVALID_PASSWORD);
        }

        // 로그인이 성공했을 때 JSON 생성 (액세스토큰을 포함)
        String profileUrl = foundMember.getProfileUrl();
        return Map.of(
                "message", "로그인에 성공했습니다.",
                "email", foundMember.getEmail(),
                "name", foundMember.getName(),
                "accessToken", jwtTokenProvider.createAccessToken(foundMember.getEmail()),
                "profile-image", profileUrl == null ? "" : profileUrl
        );
    }

    // 내 정보 불러오기
    public User findMe(String email) {
        User founduser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserException((ErrorCode.USER_NOT_FOUND))
                );
        return founduser;
    }

    // 유저 정보 불러옿기
    public UserProfileResponse findUserProfile(String name) {
        UserProfileResponse result = userRepository.getUserProfile(name);
        log.info("result : "+result);
        return result;
    }

    // 유저 이름 업데이트
    public void updateName(String email, String newName){
        if(newName==null|| newName.isEmpty()){
            throw new RuntimeException("유저 이름은 빈 문자열일 수 없습니다!");
        }
        long updatedRows = userRepository.updateName(email,newName);
        if(updatedRows!=1){
            throw new RuntimeException("유저 이름 변경 실패! email : "+email);
        }
    }

    // 프로필 이미지 업데이트
    public String updateProfileImage(String email, MultipartFile profileImage) {
        String uploadPath = fileUploadUtil.saveFile(profileImage);

        userRepository.updateProfileImage(uploadPath,email);

        return uploadPath;
    }
}