package cn.lifay.test

/**
 * Person TODO
 *
 * @author lifay
 * @date 2023/3/2 20:37
 */
class Person(var id: Int, var name: String, var child: Boolean) {
    override fun toString(): String {
        return name
    }
}