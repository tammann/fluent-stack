package ch.ergonomics

sealed class Stack3<A, B, C> {
  abstract fun drop(): Stack2<A, B>
  abstract fun <D> push(v: D): Stack4<A, B, C, D>
  abstract fun <D> map(m: (C) -> D): Stack3<A, B, D>
  abstract fun <D> map(m: (B, C) -> D): Stack2<A, D>
  abstract fun <D> map(m: (A, B, C) -> D): Stack1<D>
  open fun rethrow() {}
  open fun tos(): C = throw TopOfStackException()

  class Okay<A, B, C>(private val v1: A, private val v2: B, private val v3: C) : Stack3<A, B, C>() {
    override fun drop(): Stack2<A, B> = Stack2.Okay(v1, v2)
    override fun <D> push(v: D): Stack4<A, B, C, D> = Stack4.Okay(v1, v2, v3, v)
    override fun <D> map(m: (C) -> D): Stack3<A, B, D> {
      return try {
        Okay(v1, v2, m.invoke(v3))
      } catch (ex: Exception) {
        Error(ex)
      }
    }

    override fun <D> map(m: (B, C) -> D): Stack2<A, D> {
      return try {
        Stack2.Okay(v1, m.invoke(v2, v3))
      } catch (ex: Exception) {
        Stack2.Error(ex)
      }
    }

    override fun <D> map(m: (A, B, C) -> D): Stack1<D> {
      return try {
        Stack1.Okay(m.invoke(v1, v2, v3))
      } catch (ex: Exception) {
        Stack1.Error(ex)
      }
    }

    override fun tos() = v3
  }

  class Error<A, B, C>(private val ex: Exception) : Stack3<A, B, C>() {
    override fun drop(): Stack2<A, B> = Stack2.Error(ex)
    override fun <D> push(v: D): Stack4<A, B, C, D> = Stack4.Error(ex)
    override fun <D> map(m: (C) -> D): Stack3<A, B, D> = Error(ex)
    override fun <D> map(m: (B, C) -> D): Stack2<A, D> = Stack2.Error(ex)
    override fun <D> map(m: (A, B, C) -> D): Stack1<D> = Stack1.Error(ex)
    override fun rethrow() = throw FluentException(ex)
  }
}