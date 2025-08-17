package workshop;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.Base64;
import org.openqa.selenium.WebElement;

public class ReactiveFormValidation {
    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        // TODO: Enable BiDi bridge (uncomment the next line for the demo)
        // enable BiDi bridge

        WebDriver driver = new ChromeDriver(options);
        LogInspector logs = new LogInspector(driver);
        AtomicInteger formEvents = new AtomicInteger();

        // TODO: Subscribe to console entries to track VALIDATION_* and FORM_* events

        try {
            String page = """
                <html><head><title>Reactive Form</title>
                <style>.err{border:2px solid red}.ok{border:2px solid green}</style></head>
                <body>
                  <form id='f'>
                    <input id='email' placeholder='email'><div id='e1' style='color:red'></div>
                    <input id='pass' type='password' placeholder='password'><div id='e2' style='color:red'></div>
                    <input id='confirm' type='password' placeholder='confirm'><div id='e3' style='color:red'></div>
                    <button id='submit' disabled>Submit</button>
                  </form>
                  <script>
                    const email=document.getElementById('email'),
                          pass=document.getElementById('pass'),
                          confirm=document.getElementById('confirm'),
                          btn=document.getElementById('submit');
                    function okEmail(v){return /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/.test(v)}
                    function okPass(v){return v.length>=8}
                    function update(){
                      const em=okEmail(email.value), pw=okPass(pass.value), cf=pass.value===confirm.value;
                      email.className=em?'ok':(email.value?'err':''); document.getElementById('e1').textContent=em?'':'Invalid email'; console.log(em?'VALIDATION_OK: email':'VALIDATION_ERR: email');
                      pass.className=pw?'ok':(pass.value?'err':''); document.getElementById('e2').textContent=pw?'':'Min 8 chars'; console.log(pw?'VALIDATION_OK: pass':'VALIDATION_ERR: pass');
                      confirm.className=cf&&confirm.value?'ok':(confirm.value?'err':''); document.getElementById('e3').textContent=cf||!confirm.value?'':'Mismatch'; console.log(cf?'VALIDATION_OK: confirm':'VALIDATION_ERR: confirm');
                      btn.disabled=!(em&&pw&&cf); if(!btn.disabled) console.log('FORM_READY');
                    }
                    ['input','change'].forEach(ev=>{
                      email.addEventListener(ev,update); pass.addEventListener(ev,update); confirm.addEventListener(ev,update);
                    });
                    document.getElementById('f').addEventListener('submit',e=>{e.preventDefault(); console.log('FORM_SUBMITTED'); alert('Submitted!')});
                  </script>
                </body></html>
            """;
            // Use base64 data URL to avoid '+'/encoding quirks with URLEncoder
            String base64 = Base64.getEncoder().encodeToString(page.getBytes(StandardCharsets.UTF_8));
            String url = "data:text/html;charset=utf-8;base64," + base64;
            driver.get(url);

            // Drive the form: invalid -> valid
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement email = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
            email.sendKeys("bad");
            sleep(1000);
            email.clear();
            email.sendKeys("user@example.com");

            WebElement pass = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pass")));
            pass.sendKeys("123");
            sleep(1000);
            pass.clear();
            pass.sendKeys("strongpass123");

            WebElement confirm = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("confirm")));
            confirm.sendKeys("nope");
            sleep(1000);
            confirm.clear();
            confirm.sendKeys("strongpass123");
            sleep(1000);

            WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));

            // TODO: Assert form is ready before submit

            submit.click();
            sleep(1000);

            System.out.println("Form-related console events captured: " + formEvents.get());
        } finally {
            try { logs.close(); } catch (Exception ignored) {}
            driver.quit();
        }
    }

    private static void sleep(long ms){ try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
}
