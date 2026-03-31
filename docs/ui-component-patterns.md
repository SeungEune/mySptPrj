# UI Component Patterns

이 문서는 reference-cursor의 컴포넌트 마크업 아이디어를 현재 `mySptPrj` 구조에 맞게 변환한 패턴 문서다.

중요:
- 이 문서는 **현재 클래스 체계를 대체하지 않는다.**
- 현재 프로젝트의 실제 클래스(`primary-button`, `outline-button`, `txt-head-*` 등)를 기준으로 본다.
- reference의 DOM 구조/접근성 아이디어만 선별해 반영한다.

## 1. 기본 원칙

- 클래스명은 현재 프로젝트 기준을 따른다.
- DOM 구조는 가능한 한 단순하고 예측 가능하게 유지한다.
- 텍스트, 버튼, 테이블, 폼, 모달은 역할이 드러나게 마크업한다.
- UI 패턴을 새로 만들기 전에 유사 화면을 먼저 확인한다.

---

## 2. 버튼 패턴

### 기본 버튼

```html
<button type="button" class="primary-button md">
  <span>저장</span>
</button>

<button type="button" class="outline-button gray md">
  <span>취소</span>
</button>
```

### 원칙
- 버튼 텍스트는 가능하면 `<span>`으로 감싼다.
- 주요 액션은 `primary-button`을 우선 고려한다.
- 목록, 취소, 닫기 등은 `outline-button gray`를 우선 고려한다.
- 위험 액션은 기존 프로젝트에 맞는 방식으로 명확히 구분한다.

### 링크형 액션

```html
<a href="/main/mainForm.do" class="primary-button md">
  <span>메인으로 이동</span>
</a>
```

- 실제 이동이면 `a`
- 현재 화면 내 동작이면 `button`

---

## 3. 폼 패턴

### 기본 입력 영역

```html
<div class="conts-wrap">
  <p class="txt-head-sm">기본 정보</p>
  <div class="input-group">
    <input type="text" class="input" placeholder="입력하세요">
  </div>
</div>
```

### 폼 테이블 패턴

```html
<div class="table-box">
  <div class="table-inner">
    <table class="table-type01">
      <colgroup>
        <col width="20%"/>
        <col/>
      </colgroup>
      <tbody>
      <tr>
        <th scope="row">제목</th>
        <td>
          <input type="text" class="input" />
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</div>
```

### 원칙
- 입력 요소는 주변 설명과 가깝게 둔다.
- 필수 여부는 텍스트, th, label 등 현재 화면 체계 안에서 명확히 표시한다.
- 단위/제약사항/도움말은 입력 요소 가까이에 둔다.

---

## 4. 테이블 패턴

### 목록 테이블

```html
<div class="table-box">
  <div class="table-inner">
    <table class="table-type01">
      <caption class="sr-only">공지사항 목록</caption>
      <colgroup>
        <col width="15%"/>
        <col/>
        <col width="20%"/>
      </colgroup>
      <thead>
      <tr>
        <th>구분</th>
        <th>제목</th>
        <th>등록일</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td class="tc" colspan="3">조회 결과가 없습니다.</td>
      </tr>
      </tbody>
    </table>
  </div>
</div>
```

### 원칙
- 가능하면 `caption` 또는 대체 설명을 둔다.
- 빈 상태 문구는 짧고 일관되게 유지한다.
- 테이블은 현재 사용 중인 `table-type01`, `table-box`, `table-inner` 패턴을 우선 따른다.

---

## 5. 안내/에러 상태 패턴

### 기본 안내 카드

```html
<div class="conts-wrap">
  <p class="txt-head-sm">안내</p>
  <p class="txt-body-md">현재 표시할 데이터가 없습니다.</p>
</div>
```

### 에러/빈 상태 문구 원칙
- 현재 상태를 먼저 짧게 설명한다.
- 필요하면 다음 행동을 함께 제시한다.
- 내부 구현 용어를 그대로 노출하지 않는다.

예:
- `요청하신 페이지를 찾을 수 없습니다.`
- `조회 결과가 없습니다.`
- `메인으로 이동하거나 관리자에게 문의해 주세요.`

---

## 6. 탭/모달 패턴

### 적용 원칙
- 현재 레포의 실제 마크업/JS 구조를 우선 존중한다.
- 탭/모달은 DOM 구조보다 JS selector 영향이 더 클 수 있으므로, 마크업 변경 전에 연결된 스크립트를 먼저 확인한다.
- 활성 상태를 나타내는 클래스가 있으면 해당 클래스를 함부로 바꾸지 않는다.

### 권장 체크
- 어떤 class / id를 JS가 찾는가?
- 닫기 버튼, backdrop, active class가 어디에 연결되는가?
- 포커스 이동이나 접근성 속성에 영향이 있는가?

---

## 7. 접근성 패턴

### 기본 원칙
- 보이지 않는 설명이 필요하면 `sr-only`를 사용한다.
- 아이콘만 있는 버튼/링크는 텍스트 또는 접근성 라벨을 고려한다.
- 테이블에는 가능한 한 설명용 caption을 둔다.
- 버튼과 링크는 역할에 맞는 태그를 사용한다.

---

## 8. reference와의 관계

이 문서는 reference-cursor의 아래 아이디어를 현재 구조에 맞게 변환한 결과다.

- 버튼 텍스트를 구조적으로 감싸는 방식
- 테이블 caption / sr-only 사용
- 폼/테이블/상태 메시지의 일관된 DOM 구조
- 행동 중심 문구 작성 방식

하지만 아래는 아직 도입하지 않는다.

- `.btn`, `.form-input`, `.table.row` 클래스 체계 전면 도입
- 현재 클래스 체계를 구버전으로 간주하는 규칙
- reference의 뷰 구조 전면 강제
