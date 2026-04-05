package com.back.domain.member.controller;

import com.back.domain.member.dto.MemberDto;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
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

    ){}

    public record MemberJoinResBody(
            MemberDto memberDto
    ){}

    @PostMapping
    @Operation(summary = "회원가입")
    public RsData<MemberJoinResBody> join(@RequestBody @Valid MemberJoinReqBody reqBody) {
        Member member = memberService.join(reqBody.username, reqBody.password(), reqBody.nickname());

        return new RsData<>(
                "회원가입이 완료되었습니다. %s님 환영합니다.".formatted(reqBody.nickname),
                "201-1",
                new MemberJoinResBody(
                        new MemberDto(member)
                )
        );
    }

}
