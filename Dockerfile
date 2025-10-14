FROM budtmo/docker-android:openjdk-17

WORKDIR /app

COPY . .

RUN chmod +x gradlew

CMD ["./gradlew", "assembleDebug"]