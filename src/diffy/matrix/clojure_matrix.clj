(ns diffy.matrix.clojure-matrix
  (:require [diffy.matrix.matrix :as m]))

(defn matrix [A] 
  (if (number? A)
    A
    (with-meta (if (vector? (first A))
                 (mapv #(with-meta % {:type :cm}) A)
                 A) {:type :cm})))

(defn create [n-out n-in]
  (matrix (mapv
           (fn [_n-out]
             (mapv (fn [_n-in] 0.0)
                   (range n-in)))
           (range n-out))))

(defmethod m/to-clj :cm 
  [A] A)

(defmethod m/outer-product [:cm :cm]
  [v w]
  (matrix (mapv #(mapv (partial * %1) %2) v (repeat w))))

(defmethod m/transpose :cm 
  [A] (matrix (apply (comp #(with-meta % {:type :cm}) map) vector A)))

(defmethod m/mmul [:cm :cm] 
  [M v]
  (matrix (mapv #(reduce + (map * % v)) M)))

(defmethod m/mul :cm
  [v s]
  (matrix (m/emap #(* % s) v)))

(defmethod m/sum :cm
  [v]
  (apply + v))

(defmethod m/add :cm
  [A s]
  (m/emap (partial + s) A))

(defmethod m/madd :cm
  [A & args]
  (if (number? A)
    (matrix (apply + (cons A args)))
    (apply m/emap + (cons A args))))

(defmethod m/emap :cm 
  [f el & els]
  (let [wat (matrix (if (number? el)
                      (apply f (cons el els))
                      (apply mapv
                             (fn [& elements]
                               (if (vector? (first elements))
                                 (apply m/emap f elements)
                                 (apply f elements)))
                             (cons el els))))]
    wat))

(defmethod m/sub :cm
  [v w]
  (matrix (if (number? v)
            (- v w)
            (m/emap #(- %1 %2) v w))))
