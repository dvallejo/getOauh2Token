import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by jose on 13/02/2016.
  */
@RunWith(classOf[JUnitRunner])
class Test extends FlatSpec with Matchers {

  "A test" should "test something" in {
    true should be(true)
  }
}
