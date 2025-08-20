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
                           <style>.err{border:2px solid red}.ok{border:2px solid green}</style>
                           <meta charset="UTF-8">
                       </head>
                       <body>
                       <form id='f' novalidate>
                           <input id='email' placeholder='email'><div id='e1' style='color:red'></div>
                           <input id='pass' type='password' placeholder='password'><div id='e2' style='color:red'></div>
                           <input id='confirm' type='password' placeholder='confirm'><div id='e3' style='color:red'></div>
                           <button id='submit' disabled>Submit</button>
                       </form>
                       <script>
                           const email = document.getElementById('email'),
                               pass = document.getElementById('pass'),
                               confirm = document.getElementById('confirm'),
                               btn = document.getElementById('submit');
                           // --- validators -> { ok, code, msg } ---
                           function validateEmail(v){
                               if(!v) return { ok:false, code:'REQUIRED', msg:'Email is required' };
                               if(!/^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/.test(v))
                                   return { ok:false, code:'FORMAT', msg:'Enter a valid email like name@domain.com' };
                               return { ok:true };
                           }
                           function validatePass(v){
                               if(!v) return { ok:false, code:'REQUIRED', msg:'Password is required' };
                               if(v.length < 8) return { ok:false, code:'MIN_LENGTH', msg:'Must be at least 8 characters' };
                               return { ok:true };
                           }
                           function validateConfirm(pw, cf){
                               if(!cf) return { ok:false, code:'REQUIRED', msg:'Please confirm your password' };
                               if(pw !== cf) return { ok:false, code:'MISMATCH', msg:'Passwords do not match' };
                               return { ok:true };
                           }
                           // --- UI helper (unchanged visuals) ---
                           function setStatus(input, errorDiv, res, {showEmptyMsg=false}={}){
                               if(res.ok){
                                   input.className = 'ok';
                                   errorDiv.textContent = '';
                               } else {
                                   input.className = input.value ? 'err' : '';
                                   // keep original behavior: only show mismatch text for confirm; otherwise hide when empty
                                   if(showEmptyMsg || input.value){
                                       errorDiv.textContent = res.msg;
                                   } else {
                                       errorDiv.textContent = '';
                                   }
                               }
                           }
                           // --- logging: separate, human-readable only ---
                           function log(name, res){
                               if(res.ok){
                                   console.log(`VALIDATION_OK: ${name}`);
                               } else {
                                   console.warn(`VALIDATION_ERR: ${name} — ${res.code} (${res.msg})`);
                               }
                           }
                           function update(){
                               const emRes = validateEmail(email.value.trim());
                               const pwRes = validatePass(pass.value);
                               const cfRes = validateConfirm(pass.value, confirm.value);
                       
                               setStatus(email, document.getElementById('e1'), emRes, {showEmptyMsg:false});
                               log('email', emRes);
                       
                               setStatus(pass, document.getElementById('e2'), pwRes, {showEmptyMsg:false});
                               log('pass', pwRes);
                       
                               // Visual: only show "Mismatch" when user typed confirm; (keeps old look)
                               const showConfirmText = confirm.value.length > 0 && !cfRes.ok && cfRes.code === 'MISMATCH';
                               confirm.className = (cfRes.ok ? 'ok' : (confirm.value ? 'err' : ''));
                               document.getElementById('e3').textContent = showConfirmText ? cfRes.msg : '';
                               // Logs: always tell the precise reason (even when blank)
                               // Also, only consider confirm OK if it's non-empty and equal:
                               const confirmIsOk = !!confirm.value && pass.value === confirm.value;
                               log('confirm', confirmIsOk ? {ok:true} : cfRes);
                       
                               const formReady = emRes.ok && pwRes.ok && confirmIsOk;
                               btn.disabled = !formReady;
                               if(formReady) console.info('FORM_READY');
                           }
                       
                           ['input','change','blur'].forEach(ev=>{
                               email.addEventListener(ev, update);
                               pass.addEventListener(ev, update);
                               confirm.addEventListener(ev, update);
                           });
                       
                           document.getElementById('f').addEventListener('submit', e=>{
                               e.preventDefault();
                               update();
                               if(btn.disabled){
                                   console.warn('FORM_BLOCKED: submission blocked by validation errors');
                                   return;
                               }
                               console.log('FORM_SUBMITTED');
                               alert('Submitted!');
                           });
                       
                           // initial run (handles autofill)
                           update();
                       </script>
                       </body>
                       </html>
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
