# HobbyMate OpenAI proxy

OpenAI API 키를 Android APK에 포함하지 않기 위한 개발용 프록시입니다.

1. `.env.example`을 `.env`로 복사합니다.
2. `.env`의 `OPENAI_API_KEY`를 설정합니다.
3. `npm start`로 실행합니다.

Android 에뮬레이터는 기본적으로 `http://10.0.2.2:8787/`을 호출합니다.
배포 환경에서는 이 서버를 HTTPS로 배포하고 `local.properties`의
`OPENAI_PROXY_BASE_URL`을 해당 주소로 변경하세요.
