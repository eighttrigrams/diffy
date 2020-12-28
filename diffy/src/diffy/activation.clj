(ns diffy.activation
  (:require [diffy.layers :as l]
            [diffy.matrix.matrix :refer :all]))

(def e 2.71828182845904523536)

(def sigmoid
  (l/activation-layer
   (partial emap #(/ 1.0 (+ 1.0 (/ 1.0 (Math/pow e %)))))
   (partial emap (fn [_i o e] (* e (* o (- 1.0 o)))))))

(def relu
  (l/activation-layer
   (partial emap #(if (> % 0.0) % 0.0))
   (partial emap (fn [i _o e] (if (> i 0.0) e 0.0)))))