package cn.lifay.test


/**
 * TreeTestVO TODO
 * @author lifay
 * @date 2023/2/20 17:44
 **/
class TreeTreeVO(
    var id: String,
    var parentId: String,
    var name: String,
    var children: ArrayList<TreeTreeVO>?
) {
    override fun toString(): String {
        return name
    }
}