import net.bytebuddy.implementation.bytecode.Throw;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

//
//        Написати тест, який відкриває сторінку https://demoqa.com/webtables,
//        натискає кнопку ADD, заповнює форму додавання, перевіряє,
//        що запис додався, редагує запис через функцію редагування.
public class WebtablesPageTests {
    private WebDriver driver;

    Map<String, String> fieldsData = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "userEmail", "john.doe@example.com",
            "age", "30",
            "salary", "50000",
            "department", "Engineering"
    );

    @BeforeClass // ці тести повністю взаємозалежні
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://demoqa.com/webtables");
    }

    @Test(priority = 1)
    public void checkRegistrationFormTest() {
        WebElement buttonsElement = driver.findElement(By.id("addNewRecordButton"));
        buttonsElement.click();
        Assert.assertTrue(driver.findElement(By.id("registration-form-modal")).isDisplayed(), "Registration Form is not displayed!");
        String[] elementIds = {
                "firstName-wrapper",
                "lastName-wrapper",
                "userEmail-wrapper",
                "age-wrapper",
                "salary-wrapper",
                "department-wrapper",
                "submit"
        };

        for (String id : elementIds) {
            Assert.assertTrue(driver.findElement(By.id(id)).isDisplayed(), "Element with id '" + id + "' is not displayed");
        }
        System.out.println("All elements are present and visible.");
    }

    @Test(dependsOnMethods = "checkRegistrationFormTest")
    public void fillRegistrationFormTest() {
        // Цикл для заповнення полів
        for (Map.Entry<String, String> entry : fieldsData.entrySet()) {
            WebElement inputField = driver.findElement(By.id(entry.getKey()));
            inputField.click();
            inputField.clear(); // Очищаємо поле перед введенням
            inputField.sendKeys(entry.getValue());
        }
        WebElement submit = driver.findElement(By.id("submit"));
        submit.click();
        // Використовуємо assertTrue для перевірки зникнення форми
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("submit"))),
                "Форма реєстрації все ще присутня на сторінці після підтвердження.");

    }

    @Test(dependsOnMethods = "fillRegistrationFormTest")
    public void checkDataTest() {
        // Отримуємо останній рядок
        WebElement lastNonEmptyRow = findLastNonEmptyRow();
        assert lastNonEmptyRow != null;
        // Перевіряємо, що останній рядок містить очікувані дані

        String rowData = lastNonEmptyRow.getText();  // Отримуємо текст з останнього рядка

        for (Map.Entry<String, String> entry : fieldsData.entrySet()) {
            Assert.assertTrue(rowData.contains(entry.getValue()), entry.getValue() + " is not found in the last row.");
        }
    }

    @Test(dependsOnMethods = "checkDataTest")
    public void editDataTest() {
        // Отримуємо останній рядок
        WebElement lastNonEmptyRow = findLastNonEmptyRow();
        assert lastNonEmptyRow != null;
        WebElement editButton = lastNonEmptyRow.findElement(By.xpath(".//span[@title='Edit']"));
        editButton.click();
        Assert.assertTrue(driver.findElement(By.id("registration-form-modal")).isDisplayed(), "Registration Form is not displayed!");
        WebElement inputField = driver.findElement(By.id("age")); // редагуємо вік
        inputField.click();
        inputField.clear(); // Очищаємо поле перед введенням
        inputField.sendKeys("45");
        // Використовуємо assertTrue для перевірки зникнення форми
        WebElement submit = driver.findElement(By.id("submit"));
        submit.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("submit"))),
                "Форма реєстрації все ще присутня на сторінці після підтвердження.");

        // Перевіряємо, що вік змінився в таблиці
        lastNonEmptyRow = findLastNonEmptyRow();
        assert lastNonEmptyRow != null;
        String updatedRowData = lastNonEmptyRow.getText();

         // Перевіряємо, що вік оновився
        Assert.assertTrue(updatedRowData.contains("45"), "Age did not update to 45 in the last row.");

        // Перевіряємо інші поля, ігноруючи вік
        for (Map.Entry<String, String> entry : fieldsData.entrySet()) {
            if (entry.getKey().equals("age")) {
                continue; // Ігноруємо перевірку віку
            }
            Assert.assertTrue(updatedRowData.contains(entry.getValue()), entry.getValue() + " is not found in the last row.");
        }
    }


    private WebElement findLastNonEmptyRow() {
        List<WebElement> rows = driver.findElements(By.xpath("//div[@class='rt-tbody']//div[@role='row']"));
        for (int i = rows.size() - 1; i >= 0; i--) {
            WebElement row = rows.get(i);
            List<WebElement> cells = row.findElements(By.xpath(".//div[@role='gridcell']"));
            boolean isEmptyRow = true;
            for (WebElement cell : cells) {
                if (!cell.getText().trim().isEmpty()) {
                    isEmptyRow = false;
                    break;
                }
            }
            if (!isEmptyRow) {
                return row;
            }
        }
        return null; // Повертаємо null, якщо не знайдено заповнених рядків
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


}
