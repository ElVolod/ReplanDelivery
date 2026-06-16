package ru.netology.test;

import com.codeborne.selenide.Selectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var firstMeetingDate = DataGenerator.generateDate(4);
        var secondMeetingDate = DataGenerator.generateDate(7);

        // Заполняем город
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        // Очищаем поле даты и вводим первую дату
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        // Заполняем персональные данные
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        // Соглашаемся с условиями и отправляем форму
        $("[data-test-id='agreement']").click();
        $(Selectors.byText("Запланировать")).click();
        // Проверяем успешное создание первой встречи
        $(Selectors.withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate))
                .shouldBe(visible);

        // Меняем дату на вторую (перепланируем)
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);

        // Повторно отправляем форму
        $(Selectors.byText("Запланировать")).click();

        // Проверяем появление всплывающего окна перепланирования
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                .shouldBe(visible);

        // Подтверждаем перенос встречи
        $("[data-test-id='replan-notification'] button").click();

        // Проверяем финальный результат переноса даты
        $("[data-test-id='success-notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
