import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ApiClient {

    public static void main(String[] args) {
        // URLs для получения данных пользователей и компаний
        String usersEndpoint = "https://fake-json-api.mock.beeceptor.com/users";
        String companiesEndpoint = "https://dummy-json.mock.beeceptor.com/companies";

        // Извлечение и отображение информации о пользователях
        System.out.println("Fetching user data...");
        fetchDataAndDisplay(usersEndpoint, "User");

        System.out.println();

        // Извлечение и отображение информации о компаниях
        System.out.println("Fetching company data...");
        fetchDataAndDisplay(companiesEndpoint, "Company");
    }

    // Метод для извлечения данных по заданному URL и их отображения
    private static void fetchDataAndDisplay(String endpoint, String type) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Проверка кода ответа
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Не удалось получить данные " + type.toLowerCase() + ". Код ответа: " + responseCode);
                return;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            parseAndDisplay(responseBuilder, type);

        } catch (Exception e) {
            System.out.println("Ошибка при получении данных " + type.toLowerCase() + ": " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                System.out.println("Ошибка при закрытии чтения данных: " + e.getMessage());
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    //Метод для обработки ответа сервера и вывода данных
    private static void parseAndDisplay(StringBuilder response, String type) {
        // Переменная для отслеживания внутри кавычек
        boolean insideQuotes = false;
        for (int i = 0; i < response.length(); i++) {
            char currentChar = response.charAt(i);
            if (currentChar == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }
            if (insideQuotes && currentChar == ' ') {
                response.setCharAt(i, '_');
            }
        }

        String[] tokens = response.toString().split("\\s+");
        ArrayList<String> cleanedData = new ArrayList<>();

        for (String token : tokens) {
            String cleanedToken = token.replaceAll("[\\[\\]\",{}]", "").replace("_", " ");
            if (!cleanedToken.isEmpty()) {
                cleanedData.add(cleanedToken);
            }
        }

        // Логика для форматированного вывода ключ-значение
        String previousKey = "";
        for (int i = 0; i < cleanedData.size(); i += 2) {
            if (i + 1 < cleanedData.size()) {
                String key = cleanedData.get(i);
                String value = cleanedData.get(i + 1);

                if (key.equals("id:") && !previousKey.equals("id:")) {
                    System.out.println();
                }

                System.out.println(key + " " + value);
                previousKey = key;
            }
        }
    }
}
