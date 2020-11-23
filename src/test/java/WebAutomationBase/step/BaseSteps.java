package WebAutomationBase.step;

import WebAutomationBase.base.BaseTest;
import WebAutomationBase.helper.ElementHelper;
import WebAutomationBase.helper.StoreHelper;
import WebAutomationBase.model.ElementInfo;
import com.thoughtworks.gauge.Step;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.swing.text.Document;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertTrue;

public class BaseSteps extends BaseTest {

  public static int DEFAULT_MAX_ITERATION_COUNT = 150;
  public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

  private static Log4jLoggerAdapter logger = (Log4jLoggerAdapter) LoggerFactory
          .getLogger(BaseSteps.class);

  private static String SAVED_ATTRIBUTE;

  private Actions actions = new Actions(driver);
  private String compareText;


  private ApiTestingPost apiTestingpost = new ApiTestingPost();

  public BaseSteps() {

    PropertyConfigurator
            .configure(BaseSteps.class.getClassLoader().getResource("log4j.properties"));
  }
  @Step({"Go to <url> address",
          "<url> adresine git"})
  public void goToUrl(String url) {
    driver.get(url);
    logger.info(url + " adresine gidiliyor.");
  }

  @Step({"Go to <url> addres"})
  public void goToUrl2(String url) {
    driver.get(url);
    logger.info(url + " adresine gidiliyor.");
  }
public void Control(boolean statement, String onTrue, String onFalse){

    if (statement==true){
      logger.info(onTrue);
    }
    else{
      logger.info(onFalse);
      Assert.assertTrue(false);
    }
}
  @Step({"Kullanici adi ve sifre ile giris yapildi"})
  public void girisYap() throws InterruptedException, IOException {
    String username;
    String password;
    Thread.sleep(1000);
    driver.findElement(By.cssSelector("[class='fancybox-item fancybox-close']")).click();
    Control(driver.findElement(By.id("browsing-gw-homepage")).isDisplayed(),"Ana Sayfa Kontrolü Basarili","Ana sayfa kontrolu Basarisiz");
    driver.findElement(By.id("accountBtn")).click();

    FileReader reader= new FileReader(System.getProperty("user.dir") + "/username.txt");
    @SuppressWarnings("resource")
    BufferedReader br = new BufferedReader(reader);
    while ((username = br.readLine()) != null) {

      System.out.println("username= " +username);
      Thread.sleep(1000);
      driver.findElement(By.id("login-email")).sendKeys(username);
    }

    FileReader readerPass= new FileReader(System.getProperty("user.dir") + "/password.txt");
    @SuppressWarnings("resource")
    BufferedReader brr = new BufferedReader(readerPass);
    while ((password = brr.readLine()) != null) {

      System.out.println("password= " +password);
      driver.findElement(By.id("login-password-input")).sendKeys(password);
    }
      driver.findElement(By.cssSelector("#login-register > div.lr-container > div.q-layout.login > form > button")).click();
      Thread.sleep(3000);
      driver.findElement(By.cssSelector("#modal-root > div > div > div.modal-close")).click();
  }

  @Step({"Urun arama ekraninda bilgisayar kelimesi aranır"})
  public void UrunArama() throws InterruptedException {
    Thread.sleep(2000);
    driver.findElement(By.cssSelector("#auto-complete-app > div > div.search-box-container > input")).sendKeys("bilgisayar");
    Thread.sleep(2000);
    driver.findElement(By.cssSelector("#auto-complete-app > div > div > i")).click();
  }
  @Step({"Arama Sonucu sepete ürün eklemesi yapma"})
  public void UrunEkle() throws IOException, InterruptedException {
    String urunAdi= driver.findElement(By.cssSelector("#search-app > div > div.srch-rslt-cntnt > div.srch-prdcts-cntnr > div:nth-child(2) > div > div:nth-child(1) > div.p-card-chldrn-cntnr > a > div.prdct-desc-cntnr-wrppr > div.prdct-desc-cntnr > div > span.prdct-desc-cntnr-name.hasRatings")).getText();
    FileWriter writerUrunAdi= new FileWriter(System.getProperty("user.dir") + "/UrunAdi.txt");
    PrintWriter brr = new PrintWriter(writerUrunAdi);
    brr.println(urunAdi);
    brr.close();

    driver.findElement(By.cssSelector("#search-app > div > div.srch-rslt-cntnt > div.srch-prdcts-cntnr > div:nth-child(2) > div > div:nth-child(1) > div.p-card-chldrn-cntnr > a > div.prdct-desc-cntnr-wrppr > div.prdct-desc-cntnr > div > span.prdct-desc-cntnr-name.hasRatings")).click();
    Thread.sleep(2000);
    String urunFiyati= driver.findElement(By.className("prc-slg")).getText();

    FileWriter  writerUrunFiyati= new FileWriter(System.getProperty("user.dir") + "/UrunFiyati.txt");
    PrintWriter brru = new PrintWriter(writerUrunFiyati);
    brru.println(urunFiyati);
    brru.close();

    driver.findElement(By.cssSelector("#product-detail-app > div > div.pr-cn > div.pr-cn-in > div.pr-in-at > div:nth-child(6) > button.pr-in-btn.add-to-bs > div.add-to-bs-tx")).click();
    Thread.sleep(2000);
    driver.findElement(By.id("myBasketListItem")).click();
  }

  @Step({"Sepetteki Urun ile arama sonucu çıkan ürün karşılaştırması"})
  public void UrunKarsilastir() throws IOException, InterruptedException {
    Thread.sleep(2000);
    String sepettekiUrunFiyati=driver.findElement(By.className("total-price")).getText();
    String urunFiyati;
    FileReader reader= new FileReader(System.getProperty("user.dir") + "/UrunFiyati.txt");
    BufferedReader br = new BufferedReader(reader);
    while ((urunFiyati = br.readLine()) != null) {

      if (urunFiyati.contains(sepettekiUrunFiyati)) {

        logger.info("Urunun fiyati ile sepetteki fiyat aynidir.");
      }

      else {
        logger.info("Urunun fiyati ile sepetteki fiyat ayni degil.");
      }
    }

    br.close();
  }
  @Step({"Urun Adedi Arttırma"})
  public void UrunArtir() throws InterruptedException {
    Thread.sleep(2000);
    driver.findElement(By.cssSelector("[class='ty-numeric-counter-button']")).click();
    Thread.sleep(2000);

    System.out.println("Value of the button is:- "+ driver.findElement(By.className("counter-content")).getAttribute("value"));
    Thread.sleep(2000);
    String adet= driver.findElement(By.className("counter-content")).getAttribute("value");

    if (adet.contains("2")) {

      logger.info("Urun Adet Kontrolu Basarili");
    }

    else {
      logger.info("Urun Adet Kontrolu Basarisiz");

    }
    Thread.sleep(2000);
    driver.findElement(By.className("i-trash")).click();
    Thread.sleep(2000);
    driver.findElement(By.cssSelector("#ngdialog1 > div.ngdialog-content > form > div > div.footer > div > div.left > button.btn-item.btn-remove")).click();
  }
  @Step({"deneme"})
  public void deneme() {
    System.out.println("deneme basarili");
  }

  /* ---------------------- Api Testleri ----------------------------- */

  protected okhttp3.Response response=null;
  protected OkHttpClient.Builder client =client();
  protected String url= "https://petstore.swagger.io";
  protected String requestBody= null;

  private OkHttpClient.Builder client(){
    OkHttpClient.Builder client = new OkHttpClient.Builder();
    client.connectTimeout(30, TimeUnit.SECONDS);
    client.readTimeout(45, TimeUnit.SECONDS);
    client.writeTimeout(30, TimeUnit.SECONDS);
    client.hostnameVerifier(new HostnameVerifier() {

      @Override
      public boolean verify(String hostname, SSLSession session) {

        return true;
      }
    });
    return client;
  }



  protected Request getService (String url) {
    return new Request.Builder().url(url).get().addHeader("content-type", "application/json")
            //.addHeader("cookie", getSessionId(response))
            .build();
  }

  protected String getServices(String url, String service, String serviceName) {
    String body="";
    try {
      Request request = getService(url+service);
      response= client.build().newCall(request).execute();
      body=response.body().string();
      Control(response.isSuccessful(), serviceName + "Basarili " + "<br>" + response.message() + "<br>" + body +"",serviceName + "Basarisiz " + "<br>" + response.message() + "<br>" + body +"");
      logger.info(String.valueOf(response.code()));
    }
    catch (Exception e) {
      logger.error(e.toString());
    }
    return body;
  }

  protected Request deleteService (String url) {
    return new Request.Builder().url(url).delete().addHeader("content-type", "application/json")
            //.addHeader("cookie", getSessionId(response))
            .build();
  }

  protected String deleteServices(String url, String service, String serviceName) {
    String body="";
    try {
      Request request = deleteService(url+service);
      response= client.build().newCall(request).execute();
      body=response.body().string();
      Control(response.isSuccessful(), serviceName + "Basarili " + "<br>" + response.message() + "<br>" + body +"",serviceName + "Basarisiz " + "<br>" + response.message() + "<br>" + body +"");
    }
    catch (Exception e) {
      logger.error(e.toString());
    }
    return body;
  }

  public Request postService(String url, String body) {
    requestBody= body;
    return new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json\""), body))
            .addHeader("content-type", "application/json")
            .build();
  }

  protected String postServices(String url, String service,String body, String serviceName) {
    String responsebody="";
    try {
      Request request = postService(url+service,body);
      response= client.build().newCall(request).execute();
      responsebody=response.body().string();
      Control(response.isSuccessful(), serviceName + "Basarili " + "<br><br>" + "Request :" + "<br>" + body +"",serviceName + "Basarisiz " + "<br>" + response.message() + "<br>" + body +"");
      logger.info(String.valueOf(response.code()));
    }
    catch (Exception e) {
      logger.error(e.toString());
    }
    return responsebody;
  }

  public Request putService(String url, String body) {
    requestBody= body;
    return new Request.Builder().url(url).put(RequestBody.create(MediaType.parse("application/json\""), body))
            .addHeader("content-type", "application/json")
            .build();
  }

  protected String putServices(String url, String service,String body, String serviceName) {
    String responsebody="";
    try {
      Request request = putService(url+service,body);
      response= client.build().newCall(request).execute();
      responsebody=response.body().string();
      Control(response.isSuccessful(), serviceName + "Basarili " + "<br><br>" + "Request :" + "<br>" + body +"",serviceName + "Basarisiz " + "<br>" + response.message() + "<br>" + body +"");

    }
    catch (Exception e) {
      logger.error(e.toString());
    }
    return responsebody;
  }
  @Step({"getUserByUserName Api Testi"})
  public void getUserByUserName() {
    getServices(url, "/v2/user/burak", "getUserByUserName");
  }
  @Step({"getOrderId Api Testi"})
  public void getOrderId() {
    getServices(url, "/v2/store/order/1", "getOrderId");
  }
  @Step({"getInventory Api Testi"})
  public void getInventory() {
    getServices(url, "/v2/store/inventory", "getInventory");
  }
  @Step({"getStatus Api Testi"})
  public void getStatus() {
    getServices(url, "/v2/pet/findByStatus?status=available", "getStatus");
  }
  @Step({"deleteUpdateAnExistinPet Api Testi"})
  public void deleteUpdateAnExistinPet() {
    deleteServices(url, "/v2/store/order/1", "deleteUpdateAnExistinPet");
  }
  @Step({"postCreateWhiteList Api Test"})
  public void postCreateWhiteList () {
    String body= "[{\"id\": 0,\"username\": \"burak\",\"firstName\":\"string\",\"lastName\":\"string\",\"email\":\"string\",\"password\": \"burak\",\"phone\": \"string\",\"userStatus\": 0}]";
    postServices(url, "/v2/user/createWithList", body, "postCreateWhiteList");
  }
  @Step({"postPlaceAnOrderForAPet Api Testi"})
  public void postPlaceAnOrderForAPet () {
    String body= "{\"id\": 1,\"petId\": 0,\"quantity\": 0,\"shipDate\": \"2020-08-16T21:18:52.569Z\",\"status\": \"placed\",\"complete\": true}";
    postServices(url, "/v2/store/order", body, "postPlaceAnOrderForAPet");
  }
  @Step({"putUpdateAnExistinPet Api Testi"})
  public void putUpdateAnExistinPet () {
    String body= "{\"id\": 0,\"category\": {\"id\": 0,\"name\": \"string\"},\"name\": \"doggie\",\"photoUrls\": [\"string\"],\"tags\": [{\"id\": 0,\"name\": \"string\"}],\"status\": \"available\"}";
    putServices(url, "/v2/pet", body, "putUpdateAnExistinPet");
  }

}









