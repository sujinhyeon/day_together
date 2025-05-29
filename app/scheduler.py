import os
from datetime import datetime, timedelta
import openai
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from firebase_admin import firestore

# OpenAI API 키 세팅 (환경변수 또는 .env 사용)
openai.api_key = os.getenv("OPENAI_API_KEY")

def generate_question():
    # 지난 24시간 메시지 조회
    since = datetime.utcnow() - timedelta(days=1)
    msgs_iter = (
        firestore.client()
        .collection('messages')
        .where('timestamp', '>=', since)
        .order_by('timestamp')
        .stream()
    )
    msgs = [m.to_dict() for m in msgs_iter]
    if not msgs:
        return

    # GPT 호출 메시지 포맷
    chat = [{"role": "system", "content": "가족 대화 기반으로 흥미로운 질문 하나 생성"}]
    chat += [{"role":"user","content": m['content']} for m in msgs]

    # GPT API 호출
    resp = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=chat,
        max_tokens=60
    )
    question_text = resp.choices[0].message.content.strip()

    # Firestore에 저장
    firestore.client().collection('questions').add({
        'content': question_text,
        'generatedAt': firestore.SERVER_TIMESTAMP,
        'context': msgs[-10:]
    })

# 스케줄러 시작 함수

def start_scheduler():
    scheduler = AsyncIOScheduler(timezone="Asia/Seoul")
    scheduler.add_job(generate_question, 'cron', hour=9, minute=0)
    scheduler.start()
    return scheduler
