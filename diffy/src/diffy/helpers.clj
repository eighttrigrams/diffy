(ns diffy.helpers
  (:require [diffy.matrix.matrix :refer :all]))

(defn rand-initializer
  [[W b]]
  (let [init (fn [_] (- 1 (rand 2)))]
    [(emap init W)
     (emap init b)]))

(defn multiply-dense-layers [scalar layers]
  (mapv
   (fn [[W b]]
     [(mul W scalar)
      (mul b scalar)])
   layers))
