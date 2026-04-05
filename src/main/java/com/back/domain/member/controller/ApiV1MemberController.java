package com.back.domain.member.controller;

import com.back.domain.member.dto.MemberDto;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {

    private final MemberService memberService;

    public record MemberJoinReqBody(
            String username,
            String password,
            String nickname

    ) {
    }

    public record MemberJoinResBody(
            MemberDto memberDto
    ) {
    }

    @PostMapping("/join")
    @Operation(summary = "회원가입")
    public RsData<MemberJoinResBody> join(@RequestBody @Valid MemberJoinReqBody reqBody) {

        memberService.findByUsername(reqBody.username).ifPresent(
                m -> {
                    throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
                }
        );

        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData<>(
                "회원가입이 완료되었습니다. %s님 환영합니다.".formatted(reqBody.nickname),
                "201-1",
                new MemberJoinResBody(
                        new MemberDto(member)
                )
        );
    }

    public record MemberLoginReqBody(
            String username,
            String password,
            String nickname

    ) {
    }

    public record MemberLoginResBody(
            String apiKey
    ) {
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public RsData<MemberLoginResBody> login(@RequestBody @Valid MemberLoginReqBody reqBody) {

        Member actor = memberService.findByUsername(reqBody.username).orElseThrow(
                () -> new ServiceException("401-1", "존재하지 않는 아이디입니다.")
        );

        if (!actor.getPassword().equals(reqBody.password)) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        return new RsData<>(
                "%s님 환영합니다.".formatted(actor.getName()),
                "200-1",
                new MemberLoginResBody(actor.getApiKey())
        );
    }
}
