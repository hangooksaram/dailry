package com.daily.daily.member.controller;

import com.daily.daily.common.dto.SuccessResponseDTO;
import com.daily.daily.member.dto.DuplicateResultDTO;
import com.daily.daily.member.dto.EmailDTO;
import com.daily.daily.member.dto.EmailVerifyDTO;
import com.daily.daily.member.dto.JoinDTO;
import com.daily.daily.member.dto.MemberInfoDTO;
import com.daily.daily.member.dto.NicknameDTO;
import com.daily.daily.member.dto.PasswordRecoverDTO;
import com.daily.daily.member.dto.PasswordTokenDTO;
import com.daily.daily.member.dto.PasswordUpdateDTO;
import com.daily.daily.member.service.MemberEmailService;
import com.daily.daily.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberEmailService memberEmailService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberInfoDTO join(@RequestBody @Valid JoinDTO joinDTO) {
        return memberService.join(joinDTO);
    }

    @Secured(value = "ROLE_MEMBER")
    @GetMapping
    public MemberInfoDTO getMemberByAccessToken(@AuthenticationPrincipal Long id) {
        return memberService.findById(id);
    }

    @GetMapping("/check-username")
    public DuplicateResultDTO checkDuplicatedUsername(@RequestParam("username") String username) {
        System.out.println(username);
        if (memberService.existsByUsername(username)) {
            return new DuplicateResultDTO(true);
        }

        return new DuplicateResultDTO(false);
    }

    @GetMapping("/check-nickname")
    public DuplicateResultDTO checkDuplicatedNickname(@RequestParam("nickname") String nickname) {
        if (memberService.existsByNickname(nickname)) {
            return new DuplicateResultDTO(true);
        }

        return new DuplicateResultDTO(false);
    }

    @Secured(value = "ROLE_MEMBER")
    @PatchMapping("/nickname")
    public MemberInfoDTO updateNickname(
            @RequestBody @Valid NicknameDTO nicknameDTO,
            @AuthenticationPrincipal Long id
    ) {
        return memberService.updateNickname(id, nicknameDTO.getNickname());
    }

    @Secured(value = "ROLE_MEMBER")
    @PatchMapping("/password")
    public SuccessResponseDTO updatePassword(@RequestBody @Valid PasswordUpdateDTO passwordUpdateDTO, @AuthenticationPrincipal Long id) {
        memberService.updatePassword(passwordUpdateDTO, id);
        return new SuccessResponseDTO();
    }

    @Secured(value = "ROLE_MEMBER")
    @PostMapping("/email-verification/request")
    public SuccessResponseDTO sendCertificationNumber(@RequestBody @Valid EmailDTO emailDTO) {
        memberEmailService.sendCertificationNumber(emailDTO.getEmail());
        return new SuccessResponseDTO();
    }

    @Secured(value = "ROLE_MEMBER")
    @PostMapping("/email-verification/confirm")
    public SuccessResponseDTO verifyEmailAndRegister(
            @RequestBody @Valid EmailVerifyDTO emailVerifyDTO,
            @AuthenticationPrincipal Long id
    ) {
        memberEmailService.verifyEmailAndRegister(id, emailVerifyDTO);
        return new SuccessResponseDTO();
    }

    @PostMapping("/recover-username")
    public SuccessResponseDTO recoverUsername(@RequestBody @Valid EmailDTO emailDTO) {
        memberEmailService.sendUsername(emailDTO.getEmail());
        return new SuccessResponseDTO();
    }

    /**
     * 비밀번호 찾기를 위한 메일 전송
     * request : username, email
     * username 과 email이 일치하면 비밀번호 재설정 토큰을 생성후,
     * 해당 토큰을 URL에 포함한 재설정 페이지를 이메일로 전송.
     */

    @PostMapping("/recover-password")
    public SuccessResponseDTO recoverPassword(@RequestBody @Valid PasswordRecoverDTO passwordRecoverDTO) {
        memberEmailService.recoverPassword(passwordRecoverDTO.getUsername(), passwordRecoverDTO.getEmail());
        return new SuccessResponseDTO();
    }

    /**
     * 비밀번호 찾기 페이지에서 비밀번호를 재설정을 할때, 호출되는 API
     * request : passwordResetToken, password
     *
     * 비밀번호 재설정 토큰이 유효하면 비밀번호를 재설정한다.
     */

    @PatchMapping("/recover-password")
    public SuccessResponseDTO updatePasswordByResetToken(@RequestBody @Valid PasswordTokenDTO passwordTokenDTO) {
        memberService.updatePasswordByResetToken(passwordTokenDTO.getPasswordResetToken(), passwordTokenDTO.getPassword());
        return new SuccessResponseDTO();
    }

    @PostMapping("/withdrawal")
    public SuccessResponseDTO withdrawalMember(@AuthenticationPrincipal Long memberId) {
        memberService.withdrawalMember(memberId);
        return new SuccessResponseDTO();
    }
}
