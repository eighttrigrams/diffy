(ns diffy.matrix.clojure-matrix)

(def impl
  {:matrix            identity
   :create            (fn [n-out n-in]
                        (mapv
                         (fn [n-out-]
                           (mapv (fn [n-in-] 0.0)
                                 (range n-in)))
                         (range n-out)))
   :diagonal-matrix   (fn [values]
                        (vec
                         (map-indexed
                          (fn [i _]
                            (vec
                             (map-indexed
                              (fn [j v]
                                (if (= i j) v 0.0))
                              values)))
                          values)))
   :repetition-matrix (fn [values]
                        (mapv #(vec (repeat (count values) %)) values))
   :to-clj            identity
   :scalar?           (fn [A]
                        (number? A))
   :outer-product     (fn
                        [v w]
                        (mapv #(mapv (partial * %1) %2) v (repeat w)))
   :transpose         (partial apply map vector)
   :mmul              (fn mmul [M v]
                        (mapv #(reduce + (map * % v)) M))
   :mul               (fn [v s]
                        (if ((:scalar? impl) v)
                          (* v s)
                          ((:emap impl) #(* % s) v)))
   :sum               (partial apply +)
   :add               (fn [A s]
                        ((:emap impl) (partial + s) A))
   :madd              (fn [A & args]
                        (if (= (count args) 0)
                          A
                          (if (number? A)
                            (apply + (cons A args))
                            (apply (:emap impl) + (cons A args)))))
   :emap              (fn [f el & els]
                        (if ((:scalar? impl) el)
                          (apply f (cons el els))
                          (apply mapv
                                 (fn [& elements]
                                   (if (vector? (first elements))
                                     (apply (:emap impl) f elements)
                                     (apply f elements)))
                                 (cons el els))))
   :sub               (fn [v w]
                        (if ((:scalar? impl) v)
                          (- v w)
                          ((:emap impl) #(- %1 %2) v w)))})
