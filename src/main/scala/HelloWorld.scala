import java.net.HttpCookie

import scalaj.http.HttpResponse

/**
  * Created by jose on 13/02/2016.
  */


object HelloWorld {

  val DefaultCookie = new HttpCookie("default","")

  def main(args: Array[String]): Unit = {
    import scalaj.http._
    println("*****************************1-1********************************")
    val originLocation: String = "https://gosec02.dev.stratio.com:8443/"
    println(originLocation)
    val originPetition = Http(originLocation).option(HttpOptions.allowUnsafeSSL).asString
    val originPetitionCookie = getCookie(originPetition)
    //print(originPetition.body)
    println(originPetition.code)
    //originPetition.headers.foreach(println)

    println("******************************1-2*******************************")
    val callBackLocation = originPetition.headers.get("Location").get.head
    println(callBackLocation)
    println(s"Request Cookie $originPetitionCookie")
    val response2 = Http(callBackLocation).option(HttpOptions.allowUnsafeSSL).cookie(originPetitionCookie).asString
    val callBackCookie: HttpCookie = getCookie(response2)

    //print(response2.body)
    println(response2.code)
    //response2.headers.foreach(println)


    println("*****************************1-3********************************")
    val location2 = response2.headers.get("Location").get.head
    println(location2)
    println(s"No Request Cookie $callBackCookie")
    val response3 = Http(location2).option(HttpOptions.allowUnsafeSSL)./*cookie(callBackCookie).*/asString
    val location2BackCookie: HttpCookie = getCookie(response3)

    println(response3.code)
   //.  print(response3.body)
    //response3.headers.foreach(println)



    val bodyToProcess = response3.body
    val ltLEftMAtch: String = "name=\"lt\" value=\""
    val lt1 = bodyToProcess.indexOf(ltLEftMAtch)
    val ltRightMatch: String = "-gosec05.dev.stratio.com"
    val lt2 = bodyToProcess.indexOf(ltRightMatch)
    val lt = bodyToProcess.substring(lt1+ltLEftMAtch.size,lt2+ltRightMatch.size)
    println(s"LT ${lt}")

    val executionLEftMAtch: String = "name=\"execution\" value=\""
    val execution1 = bodyToProcess.indexOf(executionLEftMAtch)
    val execution = bodyToProcess.substring(execution1+executionLEftMAtch.size).split("\"")(0)
    println(s"execution ${execution}")
    /*
                  <input type="hidden" name="lt" value="LT-16-qiZ64aaTfFgk5UoeVDnSAEfPR5dmEc-docker-sso" />
                     <input type="hidden" name="execution" value="9dc79a30-21d4-46fa-9ad7-eb02b00d6732_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" />
                     <input type="hidden" name="_eventId" value="submit" />
                     */

    println("=================================================================================")
    println("*****************************2-1********************************")

    val username: String = "admin"
    val password: String = "1234"

    val location21 = "https://gosec05.dev.stratio.com:9005/gosec-sso/login?service=https://gosec05.dev.stratio.com:9005/gosec-sso/oauth2.0/callbackAuthorize"
    println(location21)
    println(s"Request Cookie $location2BackCookie")

    val response21 = Http(location21).option(HttpOptions.allowUnsafeSSL).cookie(location2BackCookie).
      postForm(Seq("lt" -> lt,"_eventId" ->"submit" , "execution" ->execution,
        "submit" -> "LOGIN", "username" -> username, "password" -> password)).asString
    val response21BackCookie: HttpCookie = getCookie(response21)

    println(response21.code)
    //print(response21.body)
    //response21.headers.foreach(println)

    println("**************************2-2***********************************")
    val location22 = response21.headers.get("Location").get.head
    println(location22)
    val cookie2: IndexedSeq[HttpCookie] =  response21.headers.get("Set-Cookie").get.map(c=> {
      val split: Array[String] = c.split(";")(0).split("=")
     new HttpCookie(split(0), split(1))
    }) :+ location2BackCookie
    println(cookie2)
    val response22 = Http(location22).option(HttpOptions.allowUnsafeSSL).cookies(cookie2).asString
  //  print(response22.body)
    println(response22.code)
//    response22.headers.foreach(println)

    println("***************************2-3**********************************")
    val location23 = response22.headers.get("Location").get.head
    println(location23)
    println(originPetitionCookie)

    val response23 = Http(location23).option(HttpOptions.allowUnsafeSSL).cookie(location2BackCookie).cookie(originPetitionCookie).asString





  }

  def getCookie(response: HttpResponse[String]): HttpCookie = {
    val originCookie: Option[IndexedSeq[String]] = response.headers.get("Set-Cookie")
    if (originCookie.isDefined) {
      println(s"The Response Cookie is $originCookie")

      val plainLoginCookie = originCookie.get.head.split(";")(0).split("=")
      val loginCookie = new HttpCookie(plainLoginCookie(0), plainLoginCookie(1))
      loginCookie
    }else{
      println("No Cookie")
      DefaultCookie
    }
  }
}
