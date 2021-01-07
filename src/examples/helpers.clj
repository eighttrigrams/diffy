(ns examples.helpers)

;; adapted from drlia book
(defn discount [rewards & {:keys [gamma] :or {gamma 0.99}}]
  (let [powers  (map #(Math/pow gamma %) (range (count rewards)))
        returns (map * powers rewards)
        maximum (apply max returns)]
    (mapv #(/ % maximum) returns)))

;; this is from drlia ten-armed bandit
(defn rand-choice
  [values fn:rand-proba]
  (let [proba (fn:rand-proba)]
    (reduce-kv
     (fn [[accumulated index] key value]
       (let [new-accumulated (+ accumulated value)]
         (if (> new-accumulated proba)
           (reduced [value key])
           [new-accumulated key])))
     [0. 0]
     values)))

(defn softmax [I]
  (let [e    2.71828182845904523536
        exps (mapv #(Math/pow e %) I)
        sums (apply + exps)]
    (mapv #(/ % sums) exps)))
