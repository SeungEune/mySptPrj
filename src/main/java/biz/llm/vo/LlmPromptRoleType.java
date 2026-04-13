package biz.llm.vo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LLM 프롬프트 역할 타입
 */
public enum LlmPromptRoleType {

    DEV("DEV", "개발 도우미", "코드 구현, 디버깅, 구조 분석 중심으로 응답합니다."),
    DOC("DOC", "문서 도우미", "보고서, 공문, 산출물 초안 작성 중심으로 응답합니다."),
    REQ("REQ", "요구사항 정리", "요구사항을 기능, 조건, 예외사항, 검토포인트 기준으로 정리합니다."),
    TEST("TEST", "테스트 케이스", "정상, 예외, 경계값 테스트 케이스 중심으로 응답합니다."),
    DBA("DBA", "DB 설계", "테이블 설계, SQL 작성, 인덱스, 성능, 정합성 중심으로 응답합니다."),
    ARCH("ARCH", "시스템 설계", "시스템 구조, 모듈 분리, 인터페이스, 데이터 흐름 중심으로 응답합니다.");

    private final String roleCd;
    private final String roleNm;
    private final String roleDesc;

    LlmPromptRoleType(String roleCd, String roleNm, String roleDesc) {
        this.roleCd = roleCd;
        this.roleNm = roleNm;
        this.roleDesc = roleDesc;
    }

    public String getRoleCd() {
        return roleCd;
    }

    public String getRoleNm() {
        return roleNm;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public static List<LlmPromptRoleVO> toRoleVOList() {
        return Arrays.stream(values())
                .map(type -> new LlmPromptRoleVO(type.getRoleCd(), type.getRoleNm(), type.getRoleDesc()))
                .collect(Collectors.toList());
    }

    public static LlmPromptRoleType fromCode(String roleCd) {
        return Arrays.stream(values())
                .filter(type -> type.getRoleCd().equals(roleCd))
                .findFirst()
                .orElse(DEV);
    }
}
