(ns diffy.matrix.matrix
  (:require [diffy.matrix.clojure-matrix :refer [impl] :rename {impl clojure-matrix-impl}]))

(def chosen-impl (atom clojure-matrix-impl))

;; A, B  - a matrix, a vector or a scalar, as created by (:matrix @chosen-impl)
;; M     - a matrix, as created by (:matrix @chosen-impl)
;; v,w   - a vector, as created by (:matrix @chosen-impl)
;; s     - a scalar

(defn choose-impl!
  [impl] (reset! chosen-impl impl))

(defn scalar?
  [A] ((:scalar? @chosen-impl) A))

(defn matrix
  [A] ((:matrix @chosen-impl) A))

(defn to-clj
  [A] ((:to-clj @chosen-impl) A))

(defn create
  [n-in n-out] ((:create @chosen-impl) n-out n-in))

(defn outer-product
  [v w] ((:outer-product @chosen-impl) v w))

(defn transpose
  [M] ((:transpose @chosen-impl) M))

(defn mmul
  [M v] ((:mmul @chosen-impl) M v))

(defn mul
  [A s] ((:mul @chosen-impl) A s))

(defn sum
  [v] ((:sum @chosen-impl) v))

(defn add
  "Adds a scalar to each element of a vector or a matrix"
  [v s]
  ((:add @chosen-impl) v s))

(defn madd
  "Adds element-wise. Works for scalars, vectors, matrices.
  Requires the arguments to be of the same shapes."
  [& As] (apply (:madd @chosen-impl) As))

(defn emap
  "Applies f to 1, 2 or 3 As
  f must accept this amount of args.
  "
  [f & As] (apply (:emap @chosen-impl) f As))

(defn sub
  "Subtracts element wise. Works for scalars, vectors, matrices."
  [& As] (apply (:sub @chosen-impl) As))
