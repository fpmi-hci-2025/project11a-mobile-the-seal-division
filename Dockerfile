FROM raatiniemi/android-environment:jdk-17-android-33-f15be6124a5ede4dac48ab787f9dcc37f055efd7

WORKDIR /app

COPY . .

RUN chmod +x gradlew

CMD ["./gradlew", "assembleDebug"]