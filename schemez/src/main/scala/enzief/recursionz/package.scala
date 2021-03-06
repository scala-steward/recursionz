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

package enzief

import scalaz.Scalaz.Id

package object recursionz {

  type Fix[F[_]] = Fix.impl.Fix[F]

  type Algebra[F[_], A]        = AlgebraM[F, Id, A]
  type AlgebraM[F[_], M[_], A] = F[A] => M[A]
  type GAlgebra[F[_], W[_], A] = F[W[A]] => A

  type Coalgebra[F[_], A]        = CoalgebraM[F, Id, A]
  type CoalgebraM[F[_], M[_], A] = A => F[M[A]]
}
