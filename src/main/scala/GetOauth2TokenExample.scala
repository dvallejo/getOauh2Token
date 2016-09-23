import java.net.HttpCookie

import scalaj.http.HttpResponse
import scalaj.http._

object GetOauth2TokenExample extends App {

  //*****************************1-1********************************
  val gosec02Url = "https://gosec02.dev.stratio.com:8443/api/scope"
  println("1. GO TO " + gosec02Url)

  // HTTP REQUEST
  val response1_1 = allowUnsafeSSL(Http(gosec02Url)).asString

  //******************************1-2*******************************

  // EXTRACT REDIRECT FROM RESPONSE 1-1
  val callBackLocation = response1_1.headers.get("Location").get.head
  println(s"2. REDIRECT TO : $callBackLocation")

  // HTTP REQUEST
  val response1_2 =
    allowUnsafeSSL(Http(callBackLocation)).asString

  // COOKIES IN RESPONSE
  val JSESSIONIDCookie = getCookie(response1_2)

  //*****************************1-3********************************

  // EXTRACT REDIRECT FROM RESPONSE 1-2
  val callBackLocation2 = response1_2.headers.get("Location").get.head
  println(s"3. REDIRECT TO : $callBackLocation2 with JSESSIONID")

  // HTTP REQUEST
  val response1_3 = allowUnsafeSSL(Http(callBackLocation2).cookies(JSESSIONIDCookie)).asString

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // EXTRACT INFO FROM REQUEST 1.3

  val bodyToProcess = response1_3.body
  val ltLEftMAtch = "name=\"lt\" value=\""
  val lt1 = bodyToProcess.indexOf(ltLEftMAtch)
  val ltRightMatch = "-gosec05.dev.stratio.com"
  val lt2 = bodyToProcess.indexOf(ltRightMatch)
  val lt = bodyToProcess.substring(lt1 + ltLEftMAtch.length, lt2 + ltRightMatch.length)

  val executionLEftMAtch = "name=\"execution\" value=\""
  val execution1 = bodyToProcess.indexOf(executionLEftMAtch)
  val execution = bodyToProcess.substring(execution1 + executionLEftMAtch.length).split("\"")(0)


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  //*****************************2-1********************************

  val username = "admin"
  val password = "1234"

  val gosecSSOUri = "https://gosec05.dev.stratio.com:9005/gosec-sso/login?service=https://gosec05.dev.stratio.com:9005/gosec-sso/oauth2.0/callbackAuthorize"
  println(s"4. GO TO: $gosecSSOUri with JSESSIONID: ${JSESSIONIDCookie.head.toString}")

  // HTTP REQUEST
  val response2_1 =
    allowUnsafeSSL(
      Http(gosecSSOUri)
        .cookies(JSESSIONIDCookie)
        .postForm(Seq(
        "lt" -> lt,
        "_eventId" -> "submit",
        "execution" -> execution,
        "submit" -> "LOGIN",
        "username" -> username,
        "password" -> password
      ))
    ).asString

  // COOKIES IN RESPONSE
  val CASPRIVACY_AND_TGC_COOKIES = getCookie(response2_1)

  //**************************2-2***********************************

  // EXTRACT REDIRECT FROM RESPONSE 2-2
  val location22 = response2_1.headers.get("Location").get.head
  println(s"5. REDIRECT TO : $location22 WITH JSESSIONID, CASPRIVACY AND TGC COOKIES")

  // HTTP REQUEST
  val response2_2 =
    allowUnsafeSSL(
      Http(location22)
        .cookies((CASPRIVACY_AND_TGC_COOKIES union JSESSIONIDCookie).toSeq)
    ).asString

  //***************************2-3**********************************

  // EXTRACT REDIRECT FROM RESPONSE 2-2
  val location23 = response2_2.headers.get("Location").get.head
  println(s"6. REDIRECT TO : $location23 WITH JSESSIONID, CASPRIVACY AND TGC COOKIES")

  // HTTP REQUEST
  val response2_3 =
    allowUnsafeSSL(
      Http(location23).cookies((CASPRIVACY_AND_TGC_COOKIES union JSESSIONIDCookie).toSeq)
    ).asString

  // COOKIES IN RESPONSE
  val tokenCookie = getCookie(response2_3)
  println(s"\nOauth Token obtained: ${tokenCookie.head}")

  //***************************2-4**********************************

  // HTTP REQUEST
  println(s"7. GO TO: $gosec02Url with Oauth2 Token COOKIE")
  val response2_4 =
    allowUnsafeSSL(
      Http(gosec02Url).cookies(tokenCookie)
    ).asString

  println("\n" + response2_4.body)

  def getCookie(response: HttpResponse[String]): Seq[HttpCookie] = {
    response.headers.get("Set-Cookie") match {
      case Some(cookies) =>
        cookies.map { cookie =>
          val cookieFields = cookie.split(";")(0).split("=")
          new HttpCookie(cookieFields(0), cookieFields(1))
        }.distinct
      case None => Seq.empty[HttpCookie]
    }
  }

  def allowUnsafeSSL(request: HttpRequest): HttpRequest =
    request.option(HttpOptions.allowUnsafeSSL)
}
