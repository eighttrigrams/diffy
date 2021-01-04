(ns diffy.loss
  (:require [diffy.layers :as l]
            [diffy.matrix.matrix :refer :all]))

(def loss
  (l/loss-layer
   (fn [I Y] (sub I Y))))