(ns diffy.matrix.clojure-core-matrix
  (:require
    [clojure.core.matrix :refer :all]))

(defn use-if [a f g] (if (f a) a (g a)))

(def impl
  {:matrix        matrix
   :scalar?       scalar?
   :create        zero-matrix
   :to-clj        #(-> % (use-if scalar? to-nested-vectors)) ;; currently for M or s
   :outer-product outer-product
   :transpose     transpose
   :mmul          mmul
   :mul           mul
   :sum           esum
   :madd          add
   :add           add
   :sub           sub
   :emap          emap})
