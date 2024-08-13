import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
//Написати тест, який відкриває сторінку https://demoqa.com/elements,
//// натискає на Buttons, клікає кнопку Click Me, рахує і виводить в консоль текст повідомлення.


public class ElementsPageTests {
    private WebDriver driver;

    @BeforeMethod // ці тести будуть виконуватись "з чистого листа"
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://demoqa.com/elements");
    }

    @Test(priority = 1)
    public void clickOnButtonsTest()  {
        String expectedUrl = "https://demoqa.com/buttons";
        WebElement buttonsElement = driver.findElement(By.id("item-4"));
        buttonsElement.click();
        String actualUrl = driver.getCurrentUrl();
        Assert.assertEquals(actualUrl, expectedUrl, "URL did not change as expected.");
    }

    @Test(dependsOnMethods = "clickOnButtonsTest") // Знаю, що тести повинні бути незалежними, але хотілося поекспериментувати
    public void clickMeTest() throws InterruptedException {
        WebElement buttonsElement = driver.findElement(By.id("item-4"));
        buttonsElement.click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        WebElement clickMeElement = driver.findElement(By.xpath("//button[text()='Click Me']"));
        Assert.assertTrue(clickMeElement.isDisplayed(), "Click Me button is not displayed!");
        Assert.assertTrue(clickMeElement.isEnabled(), "Click Me button is not enabled!");
        clickMeElement.click();
        WebElement text = driver.findElement(By.id("dynamicClickMessage"));
        Assert.assertEquals(text.getText(), "You have done a dynamic click", "Text is not the same!");
        System.out.println(text.getText());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
