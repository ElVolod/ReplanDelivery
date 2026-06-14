package ru.netology;

import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import lombok.Value;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        Configuration.headless = true;
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // Заполняем город
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        // Очищаем поле даты и вводим первую дату
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        // Заполняем персональные данные
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        // Соглашаемся с условиями и отправляем форму
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();
        // Проверяем успешное создание первой встречи
        $("[data-test-id='success-notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));

        // Меняем дату на вторую (18.06.2026)
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);

        // Повторно отправляем форму
        $(byText("Запланировать")).click();

        // Проверяем появление всплывающего окна перепланирования
        $("[data-test-id='replan-notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        // Подтверждаем перенос встречи
        $("[data-test-id='replan-notification'] .button").click();

        // Проверяем финальный результат переноса даты
        $("[data-test-id='success-notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
