# 웹 애플리케이션 서버
## 진행 방법
* 웹 애플리케이션 서버 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## TODO

### 1. GET /index.html 응답하기
- [x] 요청 헤더 읽기(출력하기)
- [x] 요청 헤더에서 경로 파싱하기
- [x] 경로에 맞는 파일(index.html) 읽기(file I/O)
- [x] 응답 메시지 본문에 파일 내용 작성하기
- [x] 응답 메시지 전송하기
- [ ] 코드 리팩토링

### 2. CSS 지원하기
- [x] path에서 확장자 파싱하기
- [x] css파일 읽기
- [x] response header content-type 설정하기
- [x] 응답 메시지 본문에 파일 내용 작성하기
- [x] 응답 메시지 전송하기
- [ ] 코드 리팩토링

### 3. Query String 파싱
- [x] path에서 쿼리 스트링 파싱하기
- [x] User객체 생성하기
- [x] DataBase에 user객체 저장하기
- [ ] 코드 리팩토링

### 4. POST 방식으로 회원가입
- [x] 회원가입 메소드 post로 변경하기
- [x] body 읽고 request에 추가하기
- [x] body의 데이터 파싱하기
- [x] 파싱한 데이터로 user 저장하기
- [ ] 코드 리팩토링

### 5. Redirect
- [x] Location 헤더 추가하기
- [x] 응답코드 변경하기
- [ ] 코드 리팩토링