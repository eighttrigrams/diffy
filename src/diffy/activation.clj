(ns diffy.activation
  (:require [diffy.layers :refer :all]
            [diffy.matrix.matrix :refer :all]))

(def e 2.71828182845904523536)

(def sigmoid
  (activation-layer
   (partial emap #(/ 1.0 (+ 1.0 (/ 1.0 (Math/pow e %)))))
   (partial emap (fn [_i o e] (* e (* o (- 1.0 o)))))))

(def relu
  (activation-layer
   (partial emap #(if (> % 0.0) % 0.0))
   (partial emap (fn [i _o e] (if (> i 0.0) e 0.0)))))


;; TODO under construction
;; TODO use diagonal-matrix function for clojure.core.matrix; see what to use with neanderthal
;; TODO make em ul for element-wise multiplication
(def softmax
  (activation-layer
   (fn [I]
     (let [exps (emap #(Math/pow e %) I)
           sums (sum exps)]
       (emap #(/ % sums) exps)))
   (fn [_I O E]
     (let [vals (emap #(* % (- 1.0 %)) O)
           diag (diagonal-matrix vals)
           vert (repetition-matrix vals)
           hori (vec (transpose vert))
           mult (emap * vert hori)
           subs (sub diag mult)
           sums (mapv sum subs)]
       (emap * sums E)))))
