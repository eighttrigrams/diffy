(ns diffy.matrix.neander-matrix
  (:require [uncomplicate.fluokitten.core :refer [fmap]]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(def impl
  {:matrix        (fn [m]
                    (cond
                      (number? m)
                      m
                      (number? (first m))
                      (fv m)
                      (vector? (first m))
                      (trans (fge (count (first m)) (count m) (flatten m)))
                      :else
                      m))
   :create        (fn [n-out n-in] (fge n-out n-in))
   :scalar?       (fn [A] (number? A))
   :to-clj        (fn [A]
                    (cond
                      ((:scalar? impl) A)
                      A
                      (vctr? A)
                      (mapv (fn [i] (entry A i)) (range (dim A)))
                      :else
                      (mapv
                       (fn [m]
                         (mapv
                          (fn [n]
                            (entry A m n))
                          (range (ncols A))))
                       (range (mrows A)))))
   :outer-product (fn [A B] (rk A B))
   :transpose     trans
   :mmul          (fn [A B]
                    (if (vctr? B)
                      (mv A B)
                      (mm A B)))
   :mul           (fn [A s]
                    ((if ((:scalar? impl) A) * ax) s A))
   :sum           sum
   :add           (fn [v s]
                    (if (vctr? v)
                      (alter! v (fn ^double [^long _i ^double e] (+ e s)))
                      (alter! v (fn ^double [^long _i ^long _j ^double e] (+ e s)))))
   :madd          (fn [& args]
                    (if (= 1 (count args))
                      (first args)
                      (apply (if ((:scalar? impl) (first args)) + xpy) args)))
   :emap          (fn [f & args]
                    (cond
                      ((:scalar? impl) (first args))
                      (apply f args)
                      (= 1 (count args))
                      (if (vctr? (first args))
                        (alter! (first args) (fn ^double [^long _i ^double e] (f e)))
                        (alter! (first args) (fn ^double [^long _i ^long _j ^double e] (f e))))
                      (= 2 (count args))
                      (fmap (fn ^double [^double a ^double b] (f a b)) (first args) (second args))
                      (= 3 (count args))
                      (apply fmap (fn ^double [^double a ^double b ^double c] (f a b c)) args)))
   :sub           (fn [A B]
                    (if ((:scalar? impl) A)
                      (- A B)
                      (axpy -1 B A)))})
