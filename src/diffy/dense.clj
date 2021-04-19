(ns diffy.dense
  (:require [diffy.layers :as l]
            [diffy.matrix.matrix :as m]))

(defn- layer []
  (l/layer
   (fn [[W b] I]
     ((if (number? b) m/add m/madd) (m/mmul W I) b))
   (fn [[_W b] E O]
     [(m/outer-product E O)
      (if (number? b) (m/sum E) E)])
   (fn [[W _b] E]
     (m/mmul (m/transpose W) E))))

(defn dense
  ([create matrix] (dense create matrix true))
  ([create matrix multiple-bias-values]
   {:layer   (layer)
    :weights (fn [n-in n-out]
               [(create n-in n-out)
                (if multiple-bias-values
                  (matrix (vec (repeat n-out 0.0)))
                  0.0)])}))
