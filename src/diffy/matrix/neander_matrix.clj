(ns diffy.matrix.neander-matrix
  (:require 
   [diffy.matrix.matrix :as m]
   [uncomplicate.fluokitten.core :refer [fmap]]
   [uncomplicate.neanderthal.core :as nc]
   [uncomplicate.neanderthal.native :as nn]))


(defn matrix [m] (cond
                   (number? m)
                   m
                   (number? (first m))
                   (nn/fv m)
                   (vector? (first m))
                   (nc/trans (nn/fge (count (first m)) (count m) (flatten m)))
                   :else
                   m))

(defn create [n-out n-in] (nn/fge n-out n-in))

(defmethod m/mmul uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [A B]
  (if (nc/vctr? B)
    (nc/mv A B)
    (nc/mm A B)))

(defmethod m/add uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector
  [v s]
  (if (nc/vctr? v)
    (nc/alter! v (fn ^double [^long _i ^double e] (+ e s)))
    (nc/alter! v (fn ^double [^long _i ^long _j ^double e] (+ e s)))))

(defmethod m/emap uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector
  [f & args]
  (cond
    (number? (first args))
    (apply f args)
    (= 1 (count args))
    (if (nc/vctr? (first args))
      (nc/alter! (first args) (fn ^double [^long _i ^double e] (f e)))
      (nc/alter! (first args) (fn ^double [^long _i ^long _j ^double e] (f e))))
    (= 2 (count args))
    (fmap (fn ^double [^double a ^double b] (f a b)) (first args) (second args))
    (= 3 (count args))
    (apply fmap (fn ^double [^double a ^double b ^double c] (f a b c)) args)))

(defmethod m/mul uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [A s]
  (if (number?  A) * nc/ax) s A)

(defn- sub 
  [A B]
  (if (number? A)
    (- A B)
    (nc/axpy -1 B A)))

(defmethod m/sub uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector
  [A B]
  (sub A B))

(defmethod m/sub uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [A B]
  (sub A B))

(defmethod m/sum uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [A] (nc/sum A))

(defmethod m/sum uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector
  [v] (nc/sum v))

(defmethod m/transpose uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [A] (nc/trans A))

(defmethod m/outer-product uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector
  [A B] (nc/rk A B))

(defmethod m/madd uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [& args]
  (if (= 1 (count args))
    (first args)
    (apply (if (number? (first args)) + nc/xpy) args)))

(defn- to-clj
  [A]
  (cond
    (number? A)
    A
    (nc/vctr? A)
    (mapv (fn [i] (nc/entry A i)) (range (nc/dim A)))
    :else
    (mapv
     (fn [m]
       (mapv
        (fn [n]
          (nc/entry A m n))
        (range (nc/ncols A))))
     (range (nc/mrows A)))))

(defmethod m/to-clj uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector 
  [A] (to-clj A))

(defmethod m/to-clj uncomplicate.neanderthal.internal.host.buffer_block.RealGEMatrix
  [A] (to-clj A))
