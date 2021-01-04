(ns diffy.dense
  (:require [diffy.layers :as l]
            [diffy.matrix.matrix :refer :all]))

(defn- layer []
  (l/layer
   (fn [[W b] I]
     ((if (scalar? b) add madd) (mmul W I) b))
   (fn [[_W b] E O]
     [(outer-product E O)
      (if (scalar? b) (sum E) E)])
   (fn [[W b] E]
     (mmul (transpose W) E))))

(defn dense
  ([] (dense true))
  ([multiple-bias-values]
   {:layer   (layer)
    :weights (fn [n-in n-out]
               [(create n-in n-out)
                (if multiple-bias-values
                  (matrix (vec (repeat n-out 0.0)))
                  0.0)])}))
