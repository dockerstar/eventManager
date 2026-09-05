package dev.sorokin.eventmanager.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpUserRequest(
        @NotBlank(message = "Логин не должен быть пустым")
        String login,
        @NotBlank(message = "Пароль не должен быть пустым")
        String password,
        @NotNull
        @Min(value = 18, message = "Возраст должен быть не меньше 18")
        Integer age
) {
}
