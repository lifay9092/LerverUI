package cn.lifay.test

data class UserData(var id: Int, var name: String, var type: SelectTypeEnum, var child: Boolean = false) {
    constructor(id: Int, name: String) : this(id, name, SelectTypeEnum.A) {
    }
}
