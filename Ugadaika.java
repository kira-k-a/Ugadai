import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

public class Ugadaika {

    private static int secretNumber;
    private static int attemptsLeft = 3;
    private static boolean gameActive = false;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/start", new StartHandler());
        server.createContext("/game", new GameHandler());
        server.createContext("/guess", new GuessHandler());
        server.createContext("/repeat", new RepeatHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = """
                <html>
                <head>
                    <title>Угадай число</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            text-align: center;
                            margin-top: 50px;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                        }
                        .game-message {
                            margin: 20px 0;
                        }
                        .guess-form {
                            margin: 20px 0;
                        }
                        .guess-form input {
                            padding: 10px;
                            width: 100px;
                        }
                        .guess-form button {
                            padding: 10px 20px;
                            background-color: #007BFF;
                            color: white;
                            border: none;
                            cursor: pointer;
                        }
                        .guess-form button:hover {
                            background-color: #0056b3;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Игра "Угадай число"</h1>
                        <button onclick="location.href='/start'">Начать игру</button>
                    </div>
                </body>
                </html>
                """;
            sendResponse(exchange, response, 200);
        }
    }

    static class StartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (gameActive) {
                String response = "Игра уже началась. Завершите текущую игру.";
                sendResponse(exchange, response, 400);
                return;
            }

            secretNumber = new Random().nextInt(50) + 1;
            attemptsLeft = 3;
            gameActive = true;
            String response = """
                <html>
                <head>
                    <title>Угадай число</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            text-align: center;
                            margin-top: 50px;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                        }
                        .game-message {
                            margin: 20px 0;
                        }
                        .guess-form {
                            margin: 20px 0;
                        }
                        .guess-form input {
                            padding: 10px;
                            width: 100px;
                        }
                        .guess-form button {
                            padding: 10px 20px;
                            background-color: #007BFF;
                            color: white;
                            border: none;
                            cursor: pointer;
                        }
                        .guess-form button:hover {
                            background-color: #0056b3;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Игра началась!</h1>
                        <p class="game-message">У вас есть 3 попытки, чтобы угадать число от 0 до 50.</p>
                        <form class="guess-form" id="guessForm">
                            <input type="number" id="guess" placeholder="Введите число от 0 до 50">
                            <button type="button" onclick="guessNumber()">Угадать</button>
                        </form>
                        <p id="resultMessage"></p>
                    </div>
                    <script>
                        function guessNumber() {
                            const guess = document.getElementById('guess').value;
                            fetch('/guess?guess=' + guess, { method: 'POST' })
                                .then(response => response.text())
                                .then(data => {
                                    document.getElementById('resultMessage').innerText = data;
                                    if (data.includes('Поздравляем!') || data.includes('исчерпали все попытки')) {
                                        document.getElementById('guessForm').style.display = 'none';
                                        document.getElementById('resultMessage').insertAdjacentHTML('afterend', '<button onclick="location.href=\\'/repeat\\'">Повторить</button>');
                                    }
                                });
                        }
                    </script>
                </body>
                </html>
                """;
            sendResponse(exchange, response, 200);
        }
    }

    static class GameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!gameActive) {
                String response = "Игра не началась. Начните игру с помощью /start.";
                sendResponse(exchange, response, 400);
                return;
            }

            String response = """
                <html>
                <head>
                    <title>Угадай число</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            text-align: center;
                            margin-top: 50px;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                        }
                        .game-message {
                            margin: 20px 0;
                        }
                        .guess-form {
                            margin: 20px 0;
                        }
                        .guess-form input {
                            padding: 10px;
                            width: 100px;
                        }
                        .guess-form button {
                            padding: 10px 20px;
                            background-color: #007BFF;
                            color: white;
                            border: none;
                            cursor: pointer;
                        }
                        .guess-form button:hover {
                            background-color: #0056b3;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Игра началась!</h1>
                        <p class="game-message">У вас есть 3 попытки, чтобы угадать число от 1 до 50.</p>
                        <form class="guess-form" id="guessForm">
                            <input type="number" id="guess" placeholder="Введите число от 1 до 50">
                            <button type="button" onclick="guessNumber()">Угадать</button>
                        </form>
                        <p id="resultMessage"></p>
                    </div>
                    <script>
                        function guessNumber() {
                            const guess = document.getElementById('guess').value;
                            fetch('/guess?guess=' + guess, { method: 'POST' })
                                .then(response => response.text())
                                .then(data => {
                                    document.getElementById('resultMessage').innerText = data;
                                    if (data.includes('Поздравляем!') || data.includes('исчерпали все попытки')) {
                                        document.getElementById('guessForm').style.display = 'none';
                                        document.getElementById('resultMessage').insertAdjacentHTML('afterend', '<button onclick="location.href=\\'/repeat\\'">Повторить</button>');
                                    }
                                });
                        }
                    </script>
                </body>
                </html>
                """;
            sendResponse(exchange, response, 200);
        }
    }

    static class GuessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!gameActive) {
                String response = "Игра не началась. Начните игру с помощью /start.";
                sendResponse(exchange, response, 400);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.startsWith("guess=")) {
                String response = "Неверный формат запроса. Используйте ?guess=number.";
                sendResponse(exchange, response, 400);
                return;
            }

            int guess = Integer.parseInt(query.substring(6));
            attemptsLeft--;

            if (guess == secretNumber) {
                gameActive = false;
                String response = "Поздравляем! Вы угадали число.";
                sendResponse(exchange, response, 200);
            } else if (attemptsLeft == 0) {
                gameActive = false;
                String response = "К сожалению, вы исчерпали все попытки. Загаданное число было: " + secretNumber;
                sendResponse(exchange, response, 200);
            } else if (guess < secretNumber) {
                String response = "Загаданное число больше. Осталось попыток: " + attemptsLeft;
                sendResponse(exchange, response, 200);
            } else {
                String response = "Загаданное число меньше. Осталось попыток: " + attemptsLeft;
                sendResponse(exchange, response, 200);
            }
        }
    }

    static class RepeatHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Сбросить состояние игры и перенаправить на старт
            gameActive = false;
            attemptsLeft = 3;
            String response = """
                <html>
                <head>
                    <meta http-equiv='refresh' content='0; URL=/start'>
                </head>
                </html>
                """;
            sendResponse(exchange, response, 200);
        }
    }

    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}