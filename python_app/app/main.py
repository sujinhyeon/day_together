# backend/app/main.py

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from firebase_admin import auth as fb_auth
import google.auth.transport.requests
import google.oauth2.id_token

from .firebase_admin_init import default_app  # 초기화만 호출됨
from .schemas import TokenRequest, TokenResponse

app = FastAPI()

# CORS 설정: Android 에뮬레이터/디바이스에서 호출 허용
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],        # 실제 배포 시 도메인만 명시하세요
    allow_methods=["POST"],     # 엔드포인트가 POST 하나뿐이므로
    allow_headers=["*"],
)

@app.post("/google-login", response_model=TokenResponse)
def google_login(body: TokenRequest):
    """
    1) Android 클라이언트가 보낸 Google ID 토큰 검증
    2) Firebase Admin SDK로 해당 uid에 대한 커스텀 토큰 생성
    3) 커스텀 토큰을 JSON으로 반환
    """
    try:
        # --- (1) Google ID 토큰 검증 ---
        # verify_oauth2_token는 issuer, 만료시간, signature를 모두 체크
        request = google.auth.transport.requests.Request()
        id_info = google.oauth2.id_token.verify_oauth2_token(
            body.id_token, request
        )
        uid = id_info.get("sub")  # Google 계정마다 고유한 사용자 ID

        # --- (2) Firebase용 커스텀 토큰 생성 ---
        # create_custom_token: uid 기반으로 Firebase 인증에 사용할 토큰 생성
        custom_token_bytes = fb_auth.create_custom_token(uid)
        custom_token = custom_token_bytes.decode("utf-8")

        # --- (3) 토큰 반환 ---
        return TokenResponse(custom_token=custom_token)

    except ValueError as e:
        # ID 토큰이 유효하지 않을 때
        raise HTTPException(status_code=400, detail=f"Invalid Google ID token: {e}")
    except Exception as e:
        # Firebase Admin SDK 에러 등
        raise HTTPException(status_code=500, detail=f"Server error: {e}")




# # ── Firebase 서비스 계정 키 경로 (절대 경로) ──
# BASE_DIR = os.path.dirname(os.path.abspath(__file__))
# KEY_PATH = os.path.join(BASE_DIR, '..', 'firebase-service-account.json')
# DOTENV_PATH = os.path.join(BASE_DIR, "..", ".env")
# load_dotenv(DOTENV_PATH)

# cred = credentials.Certificate(KEY_PATH)
# firebase_admin.initialize_app(cred)

# # ── FastAPI & 템플릿 설정 ──
# app = FastAPI()
# templates = Jinja2Templates(directory="templates")

# # ── Firebase Web API Key ──
# FIREBASE_WEB_API_KEY = os.getenv("FIREBASE_WEB_API_KEY")

# # ── Naver API Key ──
# NAVER_CLIENT_ID     = os.getenv("NAVER_CLIENT_ID")
# NAVER_CLIENT_SECRET = os.getenv("NAVER_CLIENT_SECRET")

# # FastAPI 엔드포인트와 완전히 일치해야 함
# NAVER_REDIRECT_URI  = os.getenv("NAVER_REDIRECT_URI")

# # 간단한 인메모리 state 저장소 (실서비스에선 Redis/DB 사용)
# _state_store: set[str] = set()

# @app.get("/", response_class=HTMLResponse)
# def login_page(request: Request):
#     return templates.TemplateResponse("login.html", {"request": request, "error": None})

# @app.post("/login", response_class=HTMLResponse)
# def login(request: Request, email: str = Form(...), password: str = Form(...)):
#     url = (
#         f"https://identitytoolkit.googleapis.com/v1/"
#         f"accounts:signInWithPassword?key={FIREBASE_WEB_API_KEY}"
#     )
#     payload = {"email": email, "password": password, "returnSecureToken": True}
#     res = requests.post(url, json=payload)

#     if res.status_code == 200:
#         user = res.json()
#         return HTMLResponse(f"<h2>Welcome, {user['email']}!</h2>")
#     else:
#         return templates.TemplateResponse("login.html", {"request": request, "error": "Invalid email or password."})

# @app.get("/auth/naver/login")
# def naver_login():
#     state = secrets.token_urlsafe(16)
#     _state_store.add(state)
#     authorize_url = (
#         "https://nid.naver.com/oauth2.0/authorize"
#         f"?response_type=code"
#         f"&client_id={NAVER_CLIENT_ID}"
#         f"&redirect_uri={NAVER_REDIRECT_URI}"
#         f"&state={state}"
#     )
#     return RedirectResponse(authorize_url)

# @app.get("/auth/naver/callback", response_class=HTMLResponse)
# def naver_callback(request: Request):
#     code  = request.query_params.get("code")
#     state = request.query_params.get("state")

#     if not code or not state:
#         raise HTTPException(400, "code나 state가 누락되었습니다.")
#     if state not in _state_store:
#         raise HTTPException(400, "Invalid state.")
#     _state_store.remove(state)

#     # 1) 네이버 Access Token 교환
#     token_res = requests.get(
#         "https://nid.naver.com/oauth2.0/token",
#         params={
#             "grant_type":    "authorization_code",
#             "client_id":     NAVER_CLIENT_ID,
#             "client_secret": NAVER_CLIENT_SECRET,
#             "code":          code,
#             "state":         state,
#         },
#     )
#     token_res.raise_for_status()
#     access_token = token_res.json().get("access_token")

#     # 2) 프로필 조회
#     profile_res = requests.get(
#         "https://openapi.naver.com/v1/nid/me",
#         headers={"Authorization": f"Bearer {access_token}"}
#     )
#     profile_res.raise_for_status()
#     profile = profile_res.json().get("response", {})
#     naver_id = profile.get("id")
#     email    = profile.get("email", "")
#     nickname = profile.get("nickname", "").strip()

#     # 3) Firebase 사용자 동기화
#     uid = f"naver:{naver_id}"
#     user_args = {"uid": uid, "email": email}
#     if nickname:
#         user_args["display_name"] = nickname

#     try:
#         firebase_auth.get_user(uid)
#         firebase_auth.update_user(**user_args)
#     except firebase_auth.UserNotFoundError:
#         firebase_auth.create_user(**user_args)

#     # 4) Firebase 커스텀 토큰 생성
#     firebase_token = firebase_auth.create_custom_token(
#         uid, {"provider": "naver", "email": email}
#     ).decode("utf-8")

#     return templates.TemplateResponse("callback.html", {"request": request, "firebase_token": firebase_token})

# # 스케줄러 시작
# start_scheduler()

# # 테스트용 수동 트리거 엔드포인트
# @app.get("/generate")
# async def manual_generate():
#     generate_question()
#     return {"status": "question generated"}


