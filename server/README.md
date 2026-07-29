# HobbyMate OpenAI 중계 서버

OpenAI API 키가 APK 안에 포함되지 않도록 주간 로드맵과 영상 체크리스트
생성 요청을 중계합니다.

1. `.env.example`을 `.env`로 복사합니다.
2. `.env`의 `OPENAI_API_KEY`를 설정합니다.
3. Node.js 20.6 이상에서 `npm start`를 실행합니다.

Android 에뮬레이터는 기본적으로 `http://10.0.2.2:8787/`에 접속합니다. 배포 서버를 사용할 경우 프로젝트의 `local.properties`에서 `OPENAI_PROXY_BASE_URL`을 HTTPS 주소로 변경하세요.

서버가 연결되지 않으면 Android 앱은 생성 오류와 재시도 UI를 표시합니다.
임의 로드맵이나 체크리스트로 대체하지 않습니다.
