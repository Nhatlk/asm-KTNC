package e2e;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoginDataDrivenTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {

        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.get("https://www.saucedemo.com/");
    }

    // Đọc dữ liệu từ CSV
    @DataProvider(name = "csvLoginData")
    public Object[][] getCsvLoginData() throws IOException {

        List<Object[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader("src/test/resources/login-data.csv"))) {

            String line;

            // Bỏ qua dòng tiêu đề
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                String[] values = line.split(",", -1);

                if (values.length != 4) {
                    throw new IllegalArgumentException(
                        "CSV phải có 4 cột: " + line
                    );
                }

                data.add(new Object[]{
                    values[0],
                    values[1],
                    values[2],
                    values[3]
                });
            }
        }

        return data.toArray(new Object[0][]);
    }

    @Test(dataProvider = "csvLoginData")
    public void testLogin(
            String username,
            String password,
            String expected,
            String expectedMessage) {

        // ARRANGE
        driver.findElement(By.id("user-name"))
                .sendKeys(username);

        driver.findElement(By.id("password"))
                .sendKeys(password);

        // ACT
        driver.findElement(By.id("login-button"))
                .click();

        // ASSERT
        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        if (expected.equalsIgnoreCase("SUCCESS")) {

            boolean loginSuccess = wait.until(d ->
                    d.getCurrentUrl().contains("/inventory.html")
            );

            Assert.assertTrue(
                loginSuccess,
                "Đăng nhập hợp lệ phải chuyển đến trang Inventory!"
            );

            System.out.println(
                "PASS - Đăng nhập thành công: " + username
            );

        } else {

            String actualError = wait.until(d ->
                d.findElement(
                    By.cssSelector("[data-test='error']")
                ).getText()
            );

            Assert.assertTrue(
                actualError.contains(expectedMessage),
                "Thông báo lỗi không đúng! Actual: " + actualError
            );

            System.out.println(
                "PASS - Đăng nhập bị từ chối đúng kỳ vọng: "
                + username
            );
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}