package code.support.demo.bean

import java.io.Serializable

/**
 * Created by Design on 2016/5/10.
 */
class PagerItem : Serializable {

    private var position: Int = 0
    var name: String? = null
    var imageUrl: String? = null
    var desc: String? = null

    fun setPosition(position: Int) {
        this.position = position
    }

}