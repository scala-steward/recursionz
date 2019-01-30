// Copyright (c) 2019 Yui Pham.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// "distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package enzief.recursionz

import scalaz.Scalaz._
import scalaz.tc._
import scalaz.tc.syntax._

class Recursionz[F[_]](implicit val F: Functor[F]) {

  /** Hylomorphism - fundamental operation of recursion schemes. It recursively
    * applies `cof` to unfold `a` into layers of `F[A]`, then recursively folds
    * then into a `B` using `f`.
    * It can also be seen as the (fused) composition of an anamorphism and a
    * catamorphism that avoids building the intermediate recursive data structure.
    */
  def hylo[A, B](a: A)(f: Algebra[F, B], cof: Coalgebra[F, A]): B =
    f(cof(a).map(hylo(_)(f, cof)))

  def compose[M[_]: Monad]: Recursionz[λ[α => F[M[α]]]] =
    new Recursionz[λ[α => F[M[α]]]]()(instanceOf(new CompositionFunctor[F, M]))
}

object Recursionz {
  def apply[F[_]](implicit F: Recursionz[F]): Recursionz[F] = F

  implicit def fromM[F[_]](implicit F: RecursionzM[F]): Recursionz[F] = F.recursionz

  implicit def fromFunctor[F[_]: Functor]: Recursionz[F] = new Recursionz[F]
}

class RecursionzM[F[_]](implicit val F: Traversable[F]) {

  /** `Traversable[F]` implies `Functor[F]` so that `this` implies `Recursionz[F]`.
    */
  val recursionz: Recursionz[F] = new Recursionz[F]

  /** A Kleisli hylomorphism.
    */
  def hyloM[M[_]: Monad, A, B](a: A)(f: AlgebraM[F, M, B], cof: CoalgebraM[F, M, A]): M[B] =
    recursionz
      .compose[M]
      .hylo[A, M[B]](a)(
        _.sequence >>= { _.sequence >>= f },
        cof
      )
}

object RecursionzM {
  def apply[F[_]](implicit F: RecursionzM[F]): RecursionzM[F] = F

  implicit def fromTraversable[F[_]: Traversable]: RecursionzM[F] = new RecursionzM[F]
}