import java.net.HttpCookie

import scalaj.http.HttpResponse
import scalaj.http._

object HelloWorld {

  val DefaultCookie = new HttpCookie("default","")

  def main(args: Array[String]): Unit = {

    println("*****************************1-1********************************")

    val gosec02Url: String = "https://gosec02.dev.stratio.com:8443/api/user"
    println(gosec02Url)

    // HTTP REQUEST
    val response1_1 = createHttpRequest(Http(gosec02Url)).asString
    println("--------------------------------------------------------------------")
    println(response1_1.code)
    println("--------------------------------------------------------------------")

    // COOKIES IN RESPONSE
    val cookiesFromResponse1_1 = getCookie(response1_1)
    println(s"Cookies in response 1-1 $cookiesFromResponse1_1")

    println
    println("******************************1-2*******************************")

    // EXTRACT REDIRECT FROM RESPONSE 1-1
    val callBackLocation = response1_1.headers.get("Location").get.head
    println(s"REDIRECT TO : $callBackLocation")

    // HTTP REQUEST
    val response1_2 =
      createHttpRequest(
        Http(callBackLocation)
          .cookies(cookiesFromResponse1_1)
      ).asString
    println("--------------------------------------------------------------------")
    println(response1_2.code)
    println("--------------------------------------------------------------------")

    // COOKIES IN RESPONSE
    val JSESSIONIDCookie: Seq[HttpCookie] = getCookie(response1_2)
    println(s"Cookies in response 1-2 $JSESSIONIDCookie")

    println
    println("*****************************1-3********************************")

    // EXTRACT REDIRECT FROM RESPONSE 1-2
    val extractJSESSIONIDFROMURL = ";" + JSESSIONIDCookie.head.toString.toLowerCase.replace("\"", "")
    val callBackLocation2 = response1_2.headers.get("Location").get.head.replace(extractJSESSIONIDFROMURL, "")
    println(s"REDIRECT TO : $callBackLocation2")

    // HTTP REQUEST
    val response1_3 =
      createHttpRequest(
        Http(callBackLocation2)
          .cookies(JSESSIONIDCookie)
      ).asString
    println("--------------------------------------------------------------------")
    println(response1_3.code)
    println("--------------------------------------------------------------------")

    // COOKIES IN RESPONSE
    val cookiesFromResponse1_3 = getCookie(response1_3)
    println(s"Cookies in response 1-3 $cookiesFromResponse1_3")

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EXTRACT INFO

    val bodyToProcess = response1_3.body
    val ltLEftMAtch: String = "name=\"lt\" value=\""
    val lt1 = bodyToProcess.indexOf(ltLEftMAtch)
    val ltRightMatch: String = "-gosec05.dev.stratio.com"
    val lt2 = bodyToProcess.indexOf(ltRightMatch)
    val lt = bodyToProcess.substring(lt1+ltLEftMAtch.size,lt2+ltRightMatch.size)
    //println(s"LT ${lt}")

    val executionLEftMAtch: String = "name=\"execution\" value=\""
    val execution1 = bodyToProcess.indexOf(executionLEftMAtch)
    val execution = bodyToProcess.substring(execution1+executionLEftMAtch.size).split("\"")(0)
    //println(s"execution ${execution}")

    /*
      <input type="hidden" name="lt" value="LT-16-qiZ64aaTfFgk5UoeVDnSAEfPR5dmEc-docker-sso" />
         <input type="hidden" name="execution" value="9dc79a30-21d4-46fa-9ad7-eb02b00d6732_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" />
         <input type="hidden" name="_eventId" value="submit" />
    */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    println
    println("=================================================================================")
    println("*****************************2-1********************************")

    val username: String = "admin"
    val password: String = "1234"

    val gosecSSOUri = "https://gosec05.dev.stratio.com:9005/gosec-sso/login?service=https://gosec05.dev.stratio.com:9005/gosec-sso/oauth2.0/callbackAuthorize"
    println("GO TO: " + gosecSSOUri)

    // HTTP REQUEST
    val response2_1 =
      createHttpRequest(
        Http(gosecSSOUri)
          .cookies(JSESSIONIDCookie)
          .postForm(Seq(
            "lt" -> lt,
            "_eventId" ->"submit",
            "execution" ->execution,
            "submit" -> "LOGIN",
            "username" -> username,
            "password" -> password
          ))
      ).asString
    println("--------------------------------------------------------------------")
    println(response2_1.code)
    println("--------------------------------------------------------------------")

    // COOKIES IN RESPONSE
    val CASPRIVACY_AND_TGC: Seq[HttpCookie] = getCookie(response2_1)
    println(s"Cookies in response 2-1 $CASPRIVACY_AND_TGC")

    println
    println("**************************2-2***********************************")

    // EXTRACT REDIRECT FROM RESPONSE 2-2
    val location22 = response2_1.headers.get("Location").get.head
    println(s"REDIRECT TO : $location22")

    // HTTP REQUEST
    val httpRequest2_2 =
      createHttpRequest(
        Http(location22)
          .cookies((CASPRIVACY_AND_TGC union JSESSIONIDCookie).toSeq)
      )

    val response2_2 = httpRequest2_2
      .asString
    println("--------------------------------------------------------------------")
    println(response2_2.code)
    println("--------------------------------------------------------------------")

    // COOKIES IN RESPONSE
    val cookiesFromResponse2_2 = getCookie(response2_2)
    println(s"Cookies in response 2-2 $cookiesFromResponse2_2")

    println
    println("***************************2-3**********************************")

    // EXTRACT REDIRECT FROM RESPONSE 2-2
    val location23 = response2_2.headers.get("Location").get.head
    println(s"REDIRECT TO : $location23")

    // HTTP REQUEST
    val response2_3 =
      createHttpRequest(
        Http(location23).cookies((CASPRIVACY_AND_TGC union JSESSIONIDCookie).toSeq)
      ).asString

    println("--------------------------------------------------------------------")
    println(response2_3.cookies.toSeq)
    println("--------------------------------------------------------------------")

    // COOKIES IN RESPONSE
    val tokenCookie = getCookie(response2_3)
    println(s"Cookies in response 2-3 $tokenCookie")

    println
    println("***************************2-4**********************************")

    // HTTP REQUEST
    val response2_4 =
      createHttpRequest(
        Http(gosec02Url).cookies(tokenCookie)
      ).asString

    println("--------------------------------------------------------------------")
    println(response2_4.body)
    println("--------------------------------------------------------------------")

  }

  def getCookie(response: HttpResponse[String]): Seq[HttpCookie] = {
    val originCookie: Option[IndexedSeq[String]] = response.headers.get("Set-Cookie")

    if (originCookie.isDefined) {
      originCookie.get.map { newCookie =>
        val cookieValue = newCookie.split(";")(0)
        val cookieFields = cookieValue.split("=")
        new HttpCookie(cookieFields(0), cookieFields(1))
      }.distinct
    }else {
      println("No Cookie")
      Seq.empty[HttpCookie]
    }
  }

  def createHttpRequest(request: HttpRequest): HttpRequest =
    request.option(HttpOptions.allowUnsafeSSL)
}
