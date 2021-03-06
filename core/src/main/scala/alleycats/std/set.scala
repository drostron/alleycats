package alleycats.std

import cats.{Applicative, Fold, Monad, Traverse}

object set extends SetInstances

trait SetInstances {
  implicit val setMonad: Monad[Set] =
    new Monad[Set] {
      def pure[A](a: A): Set[A] = Set(a)
      override def map[A, B](fa: Set[A])(f: A => B): Set[B] = fa.map(f)
      def flatMap[A, B](fa: Set[A])(f: A => Set[B]): Set[B] = fa.flatMap(f)
    }

  implicit val setTraverse: Traverse[Set] =
    new Traverse[Set] {
      def foldLeft[A, B](fa: Set[A], b: B)(f: (B, A) => B): B =
        fa.foldLeft(b)(f)
      def partialFold[A, B](fa: Set[A])(f: A => Fold[B]): Fold[B] =
        Fold.partialIterate(fa)(f)
      def traverse[G[_]: Applicative, A, B](sa: Set[A])(f: A => G[B]): G[Set[B]] = {
        val G = Applicative[G]
        sa.foldLeft(G.pure(Set.empty[B])) { (buf, a) =>
          G.map2(buf, f(a))((sb, b) => sb + b)
        }
      }
    }
}
