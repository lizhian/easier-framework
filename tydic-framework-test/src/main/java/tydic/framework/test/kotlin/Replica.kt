package tydic.framework.test.kotlin

import cn.hutool.core.util.StrUtil
import cn.hutool.db.sql.SqlUtil
import tydic.framework.core.kt.or
import tydic.framework.core.kt.println
import tydic.framework.core.kt.smartSplit
import tydic.framework.core.kt.toUnderlineCase
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import kotlin.reflect.full.instanceParameter

class Replica {}
typealias P1 = Person
typealias P2 = Person

fun main() {
    println(Book())
    val param = listOf("1", "2", "3")
    val sql = newQuery {
        select {
            P1::class.all()
            P2::class.all()
            P1::getBookName.column()
            Book::getBootName asTo Person::getBookName
            Book::getBootName `as` Person::getBookName
        }
        //selectAllFrom(Person::class)
        from(Person::class)
        leftJoin(Book::class) on {
            Person::getId eq Book::getPersonId
            Person::getId eq Book::getPersonId
        }
        where {
            Person::getId eq "11"
            Person::getId ge "111"
            Person::getId ge 1
            Person::getId.isNull()
            Person::getId.isNotNull()
            if (param.isNotEmpty()) {
                Person::getId `in` param
                Person::getId inValues param
            }
            or { Person::getId eq "value" }
            and {
                Person::getId eq "11"
                Person::getId eq "11"
                or {
                    Person::getId eq "11"
                    Person::getId eq "11"
                    Person::getId eq "11"
                }
            }
        }
        orderBy {
            Person::getAge.asc()
            Book::getBootName.desc()
        }
        limit { 10 to 20 }
    }.render()
    var formatSql = SqlUtil.formatSql(sql)
    formatSql.println()
}

fun <T, R> KFunction1<T, R>.toColumnName(): String {
    return StrUtil.toUnderlineCase(this.name).removePrefix("get_")
}

fun <T : Any> KClass<T>.toTableName(): String {
    return StrUtil.toUnderlineCase(this.simpleName)
}

class Query : SQLNode {
    private val children = mutableListOf<SQLNode>()
    override fun render(): String {
        return children.joinToString(separator = " ") { it.render() }
    }

    fun select(block: Select.() -> Unit) {
        val select = Select()
        block(select)
        children += select
    }

    fun from(table: KClass<out Any>) {
        children += From(table)
    }

    fun selectAllFrom(table: KClass<out Any>) {
        val select = Select()
        select.children += Select.SelectTable(table)
        children += select
    }

    fun join(type: JoinType = JoinType.none, outer: Boolean = false, table: KClass<out Any>) {
        children += Join(type, outer, table)
    }

    fun leftJoin(table: KClass<out Any>): Join {
        val join = Join(type = JoinType.left, outer = false, table)
        children += join
        return join
    }

    fun where(function: Where.() -> Unit) {
        var where = Where()
        function(where)
        children += where
    }

    fun orderBy(orderByBlock: OrderBy.() -> Unit) {
        //TODO("Not yet implemented")
    }

    fun limit(limitBlock: () -> Pair<Int, Int>) {
        //TODO("Not yet implemented")
    }


}

class Select() : SQLNode {
    val children = mutableListOf<SQLNode>()
    override fun render(): String {
        return "select ${children.joinToString { it.render() }}"
    }

    fun <T : Any> KClass<T>.all() {
        children.add(SelectTable(this))
    }

    fun <T, R> KFunction1<T, R>.column() {
        children.add(SelectColumn(this))
    }


    infix fun <T1, T2, R> KFunction1<T1, R>.asTo(alias: KFunction1<T2, R>) {
        children.add(SelectColumnAs(this, alias))
    }

    infix fun <T1, T2, R> KFunction1<T1, R>.`as`(alias: KFunction1<T2, R>) {
        children.add(SelectColumnAs(this, alias))
    }


    data class SelectTable<T : Any>(val table: KClass<T>) : SQLNode {
        override fun render(): String {
            return "${table.toTableName()}.*"
        }
    }

    data class SelectColumn<T, R>(val column: KFunction1<T, R>) : SQLNode {
        override fun render(): String {
            return column.toColumnName()
        }
    }

    data class SelectColumnAs<T1, T2, R>(val column: KFunction1<T1, R>, val alias: KFunction1<T2, R>) : SQLNode {
        override fun render(): String {
            return "${column.toColumnName()} as ${alias.toColumnName()}"
        }
    }
}

data class From<T : Any>(val table: KClass<T>) : SQLNode {
    override fun render(): String {
        return "from ${table.toTableName()}"
    }
}

enum class JoinType : SQLNode {
    none, inner, left, right, full, cross, nature, self;

    override fun render(): String {
        return when (this) {
            none -> String()
            else -> this.name
        }
    }
}

data class Join(val type: JoinType, val outer: Boolean, val table: KClass<out Any>) : SQLNode {
    private val on: On = On()
    infix fun on(block: On.() -> Unit) {
        block(on)
    }

    override fun render(): String {
        return "${type.render()} ${outer to "outer" or ""} join ${table.toTableName()} ${on.render()}"
    }
}

class On(val link: String = " and ") : SQLNode {
    private val children = mutableListOf<SQLNode>()
    override fun render(): String {
        return "on ${children.joinToString(transform = SQLNode::render, separator = link)}"
    }

    fun or(block: On.() -> Unit): On {
        val or = On()
        block(or)
        children.add(or)
        return or
    }

    fun and(block: On.() -> Unit): On {
        val and = On()
        block(and)
        children.add(and)
        return and
    }

    infix fun <T1, T2, R> KFunction1<T1, R>.eq(column: KFunction1<T2, R>): Unit {
        children.add(OnCondition(this, "=", column))
    }

    data class OnCondition<T1, T2, R>(
        val key: KFunction1<T1, R>,
        val type: String,
        val value: KFunction1<T2, R>
    ) : SQLNode {
        override fun render(): String {
            val keyType = key.instanceParameter?.type ?: return String()
            val valueType = value.instanceParameter?.type ?: return String()
            val keyTable = keyType.toString().smartSplit().last().toUnderlineCase()
            val valueTable = valueType.toString().smartSplit().last().toUnderlineCase()
            return "$keyTable.${key.toColumnName()} $type $valueTable.${value.toColumnName()}"
        }
    }


}


fun newQuery(block: Query.() -> Unit): Query {
    val query = Query()
    block(query)
    return query
}

interface SQLNode {
    fun render(): String
}


class Where(
    val isRoot: Boolean = true,
    val surround: String = "",
) : SQLNode {
    private val children = mutableListOf<SQLNode>()

    override fun render(): String {
        val collectionString = children.joinToString(transform = SQLNode::render, separator = "\nand ")
        var str = if (isRoot) {
            "where $collectionString"
        } else {
            "$surround ( $collectionString )"
        }
        str = str.replace("and or (", "or (")
        str = str.replace("and and (", "and (")
        return str
    }

    fun or(conditionBlock: Where.() -> Unit) {
        val or = Where(isRoot = false, surround = "or")
        conditionBlock(or)
        children.add(or)
    }

    fun and(conditionBlock: Where.() -> Unit) {
        val and = Where(isRoot = false, surround = "and")
        conditionBlock(and)
        children += and
    }

    infix fun <T, R> KFunction1<T, R>.eq(value: R?) {
        children += WhereCondition(this, "=", value)
    }

    infix fun <T, R> KFunction1<T, R>.`=`(value: R?): Unit {
        children += WhereCondition(this, "=", value)
    }

    infix fun <T, R> KFunction1<T, R>.ge(value: R?): Unit {
        children += WhereCondition(this, ">=", value)
    }

    fun <T, R> KFunction1<T, R>.isNull(): Unit {
        children += WhereCondition(this, "is", null)
    }

    fun <T, R> KFunction1<T, R>.isNotNull(): Unit {
        children += WhereCondition(this, "is not", null)
    }

    infix fun <T, R> KFunction1<T, R>.`≧`(value: R?): Unit {
        children += WhereCondition(this, ">=", null)
    }

    infix fun <T, R> KFunction1<T, R>.`in`(value: Collection<R>): Unit {

    }

    infix fun <T, R> KFunction1<T, R>.inValues(value: Collection<R>): Unit {

    }

    data class WhereCondition<T1, R>(
        val key: KFunction1<T1, R>,
        val type: String,
        val value: Any?
    ) : SQLNode {
        override fun render(): String {
            val keyType = key.instanceParameter?.type ?: return String()
            val keyTable = keyType.toString().smartSplit().last().toUnderlineCase()
            return "$keyTable.${key.toColumnName()} $type $value"
        }
    }


}

class OrderBy() {

    fun <T, R> KFunction1<T, R>.asc(): Unit {

    }

    fun <T, R> KFunction1<T, R>.desc(): Unit {

    }

}


interface EntityManager {
    fun createQuery(query: String, cls: Class<out BaseEntity>): Any {
        TODO("Not yet implemented")
    }

}

interface BaseEntity {

}

data class Test(var x: String = "", var y: Int = 1)

fun load(
    em: EntityManager, cls: Class<out BaseEntity>, conditionBlock: Condition.() -> Condition
): MutableList<out BaseEntity> {
    //val mTools = AppBeans.get(MetadataTools::class.java)
    val condition =
        Condition(Triple(Condition.NULL_PLACE_HOLDER, Condition.NULL_PLACE_HOLDER, Condition.NULL_PLACE_HOLDER))
    val invoke = conditionBlock.invoke(condition)

    var conditionQl = invoke.metaExpr.third
    var query = "select e0 from ${cls.simpleName} e0 "

    // add Where Clause
    query += if (conditionQl != "") " where $conditionQl" else ""

    val emQuery = em.createQuery(query, cls)
    //params.forEachIndexed { idx, p -> emQuery.setParameter(idx, p) }

    return mutableListOf();
}


class Condition(var metaExpr: Triple<String, Any, String>) {

    val subConditions: MutableList<Condition> by lazy { mutableListOf() }
    val on: MutableList<Any> by lazy { mutableListOf() }

    companion object {
        val NULL_PLACE_HOLDER: String = ""
    }

    val Any?.unit get() = Unit

    infix fun String.eq(v: Any): Unit = _eq(this, v).unit

    infix fun String.le(v: Any): Unit = _le(this, v).unit

    infix fun String.ge(v: Any): Unit = _ge(this, v).unit

    infix fun String.ends(v: Any): Unit = _endsWith(this, v).unit

    private fun _eq(field: String, value: Any) = subConditions.add(
        Condition(
            Triple(
                field, value, "${if (field.contains(".")) field else "e0.$field"} = :param"
            )
        )
    )

    private fun _le(field: String, value: Any) = subConditions.add(
        Condition(
            Triple(
                field, value, "${if (field.contains(".")) field else "e0.$field"} <= :param"
            )
        )
    )

    private fun _ge(field: String, value: Any) = subConditions.add(
        Condition(
            Triple(
                field, value, "${if (field.contains(".")) field else "e0.$field"} >= :param"
            )
        )
    )

    private fun _endsWith(field: String, value: Any) = subConditions.add(
        Condition(
            Triple(
                field, value, "${if (field.contains(".")) field else "e0.$field"} like CONCAT ('%', :param)"
            )
        )
    )

    fun or(conditionBlock: Condition.() -> Unit): Condition {
        val c = Condition(Triple(NULL_PLACE_HOLDER, NULL_PLACE_HOLDER, NULL_PLACE_HOLDER))
        conditionBlock.invoke(c)
        val s = c.subConditions.joinToString(" or ") { "(${it.metaExpr.third})" }
        c.metaExpr = Triple(NULL_PLACE_HOLDER, NULL_PLACE_HOLDER, s)
        subConditions.add(c)
        return c
    }

    fun and(conditionBlock: Condition.() -> Unit): Condition {
        val c = Condition(Triple(NULL_PLACE_HOLDER, NULL_PLACE_HOLDER, NULL_PLACE_HOLDER))

        conditionBlock.invoke(c)
        val s = c.subConditions.joinToString(" and ") { "(${it.metaExpr.third})" }
        c.metaExpr = Triple(NULL_PLACE_HOLDER, NULL_PLACE_HOLDER, s)
        subConditions.add(c)
        return c
    }

    fun not(conditionBlock: Condition.() -> Unit): Condition {
        val c = and(conditionBlock)
        val s = "not " + c.metaExpr.third
        c.metaExpr = Triple(NULL_PLACE_HOLDER, NULL_PLACE_HOLDER, s)
        return c
    }

    fun getParams(): List<Any> {

        if (subConditions.size == 0) {
            return mutableListOf(metaExpr.second)
        }
        val params = mutableListOf<Any>()
        subConditions.forEach {
            params.addAll(it.getParams())
        }
        return params
    }
}