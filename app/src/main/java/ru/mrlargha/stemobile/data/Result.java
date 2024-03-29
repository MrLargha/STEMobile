package ru.mrlargha.stemobile.data;

import org.jetbrains.annotations.NotNull;

/**
 * Результат выполнения запроса
 *
 * @param <T> тип ответа сервера
 */
public class Result<T> {
    @NotNull
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getErrorString() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends Result {
        private final T data;

        Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private final String errorString;

        public Error(String errorString) {
            this.errorString = errorString;
        }

        public String getErrorString() {
            return this.errorString;
        }
    }
}
