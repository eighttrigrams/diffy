(ns diffy.helpers
  (:require [diffy.matrix.matrix :as m]))

(defn rand-initializer
  [[W b]]
  (let [init (fn [_] (- 1 (rand 2)))]
    [(m/emap init W)
     (if (number? b) (init b) (m/emap init b))]))

(defn multiply-dense-layers [scalar layers]
  (mapv
   (fn [[W b]]
     [(m/mul W scalar)
      (m/mul b scalar)])
   layers))
