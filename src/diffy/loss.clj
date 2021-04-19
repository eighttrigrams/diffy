(ns diffy.loss
  (:require [diffy.layers :as l]
            [diffy.matrix.matrix :as m]))

(def loss
  (l/loss-layer
   (fn [I Y] (m/sub I Y))))