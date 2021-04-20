(ns diffy.matrix.clojure-core-matrix
  (:require
    [diffy.matrix.matrix :as m]
    [clojure.core.matrix :as ccm]))

(defn- use-if
  [a f g] (if (f a) a (g a)))

(defn- wrap-type
  [A] (with-meta A {:type :ccm}))

(defn matrix
  [A] (if (number? A) A (wrap-type A)))

(defn create
  [n-out n-in] (matrix (ccm/zero-matrix n-in n-out)))

(defmethod m/to-clj :ccm
  [A] (matrix (-> A (use-if number? ccm/to-nested-vectors))))

(defmethod m/outer-product :ccm
  [v w] (matrix (ccm/outer-product v w)))

(defmethod m/transpose :ccm
  [A] (matrix (ccm/transpose A)))

(defmethod m/mmul :ccm
  [M v] (matrix (ccm/mmul M v)))

(defmethod m/mul :ccm
  [v s] (matrix (ccm/mul v s)))

(defmethod m/sum :ccm
  [v] (ccm/esum v))

(defmethod m/add :ccm
  [A s] (matrix (ccm/add A s)))

(defmethod m/madd :ccm
  [& As] (matrix (apply ccm/add As)))

(defmethod m/sub :ccm
  [v w] (matrix (ccm/sub v w)))

(defmethod m/emap :ccm
  [& args] (matrix (apply ccm/emap args)))
