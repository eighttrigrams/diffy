(ns diffy.matrix.matrix)

;; A, B  - a matrix, a vector or a scalar, as created by (:matrix @chosen-impl)
;; M     - a matrix, as created by (:matrix @chosen-impl)
;; v,w   - a vector, as created by (:matrix @chosen-impl)
;; s     - a scalar

(defn type-for-dispatch [item]
  (if (or
       (instance? clojure.lang.PersistentVector item)
       (instance? clojure.lang.LazySeq item)) ;; TODO review
    (:type (meta item))
    (class item)))

(defmulti to-clj 
  (fn [A]
    (if (number? A)
      :default
      (type-for-dispatch A))))

(defmulti outer-product 
  (fn [v _w]
    (type-for-dispatch v)))

(defmulti transpose
  (fn [M]
    (type-for-dispatch M)))

(defmulti mmul
  (fn [M _v]
    (type-for-dispatch M)))

(defmulti mul
  (fn [v _s]
    (if (number? v)
      :default
      (type-for-dispatch v))))

(defmulti sum
  (fn [v]
    (type-for-dispatch v)))

(defmulti add
  "Adds a scalar to each element of a vector or a matrix"
  (fn [v _s]
    (type-for-dispatch v)))

(defmulti madd
  "Adds element-wise. Works for scalars, vectors, matrices.
  Requires the arguments to be of the same shapes."
  (fn [& As]
    (if (= (count As) 1)
      :default-1
      (if (number? (first As))
        :default-2
        (type-for-dispatch (first As))))))

(defmulti emap 
  "Applies f to 1, 2 or 3 As
  f must accept this amount of args."
  (fn [_f & As]
    (type-for-dispatch (first As))))

(defmulti sub
  "Subtracts element wise. Works for scalars, vectors, matrices."
  (fn [& As] 
    (if (= (count As) 1)
      :default
      (type-for-dispatch (first As)))))

(defmethod to-clj :default
  [s] s)

(defmethod mul :default
  [v s] 
  (* v s))

(defmethod madd :default-1
  [A] A)

(defmethod madd :default-2
  [& As] (apply + As))

(defmethod sub :default
  [v w] 
  (- v w))
