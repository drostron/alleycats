package alleycats

import cats.{Applicative, CoflatMap, Comonad}
import simulacrum.typeclass

@typeclass trait Extract[F[_]] {
  def extract[A](fa: F[A]): A
}

object Extract {
  implicit def comonadIsExtract[F[_]](implicit ev: Comonad[F]): Extract[F] =
    new Extract[F] {
      def extract[A](fa: F[A]): A = ev.extract(fa)
    }

  implicit def extractCoflatMapIsComonad[F[_]](implicit e: Extract[F], cf: CoflatMap[F]): Comonad[F] =
    new Comonad[F] {
      def extract[A](fa: F[A]): A = e.extract(fa)
      override def map[A, B](fa: F[A])(f: A => B): F[B] = cf.map(fa)(f)
      def coflatMap[A, B](fa: F[A])(f: F[A] => B): F[B] = cf.coflatMap(fa)(f)
    }
}
