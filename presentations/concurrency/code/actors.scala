
import scala.actors.Actor
import scala.actors.Actor._

case class Add(val data:String)
case class GetFirst()
case class First(val first: String)


class StateManager extends Actor {
    var state: List = List()
  
    def act() {
        while (true) {
            receive {
                case Add(data) =>
                  state = state :: data
                case GetFirst =>
                  sender ! First(state.head)
            }
        }
    }
}

sm = new StateManager
sm.start
sm ! Add("one")
sm ! Add("two")

first = sm !! GetFirst()
