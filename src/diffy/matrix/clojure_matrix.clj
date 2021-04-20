(ns diffy.matrix.clojure-matrix
  (:require [diffy.matrix.matrix :as m]))

(defn- wrap-type [A] (with-meta A {:type :cm}))

(defn matrix 
  [A] 
  (if (number? A)
    A
    (wrap-type A)))

(defn create 
  [n-out n-in]
  (wrap-type (mapv
           (fn [_n-out]
             (mapv (fn [_n-in] 0.0)
                   (range n-in)))
           (range n-out))))

(defn- emap
  [f el & els]
  (wrap-type (if (number? el)
               (apply f (cons el els))
               (apply mapv
                      (fn [& elements]
                        (if (vector? (first elements))
                          (apply emap f elements)
                          (apply f elements)))
                      (cons el els)))))

(defmethod m/to-clj :cm 
  [A] 
  A)

(defmethod m/outer-product [:cm :cm]
  [v w]
  (wrap-type (mapv #(mapv (partial * %1) %2) v (repeat w))))

(defmethod m/transpose :cm 
  [A] 
  (wrap-type (apply map vector A)))

(defmethod m/mmul [:cm :cm] 
  [M v]
  (wrap-type (mapv #(reduce + (map * % v)) M)))

(defmethod m/mul :cm
  [v s]
  (emap #(* % s) v))

(defmethod m/sum :cm
  [v]
  (apply + v))

(defmethod m/add :cm
  [A s]
  (emap (partial + s) A))

(defmethod m/madd :cm
  [A & args]
  (if (number? A)
    (wrap-type (apply + (cons A args)))
    (apply emap + (cons A args))))

(defmethod m/emap :cm
  [& args]
  (apply emap args))

(defmethod m/sub :cm
  [v w]
  (if (number? v)
    (wrap-type (- v w))
    (emap #(- %1 %2) v w)))
