package cn.lifay.test

data class UserData(var id: Int?, var name: String?,
                    var type: SelectTypeEnum?, var child: Boolean? = false,
                    var sex : String?) {
    constructor(id: Int, name: String) : this(id, name, SelectTypeEnum.A, sex = "男") {
    }
}
