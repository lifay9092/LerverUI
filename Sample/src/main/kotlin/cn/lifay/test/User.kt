package cn.lifay.test

/**
 * @ClassName User
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:15
 */
class User {
    var id: Int? = null
    var name: String? = null
    var type: SelectTypeEnum? = null

    constructor() {}
    constructor(id: Int?, name: String?) {
        this.id = id
        this.name = name
    }
}