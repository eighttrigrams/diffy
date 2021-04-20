(ns diffy.matrix.matrix)

;; A, B  - a matrix, a vector or a scalar, as created by (:matrix @chosen-impl)
;; M     - a matrix, as created by (:matrix @chosen-impl)
;; v,w   - a vector, as created by (:matrix @chosen-impl)
;; s     - a scalar

(defmulti to-clj 
  (fn [A]
    (if (number? A)
      :default
      (:type (meta A)))))

(defmulti outer-product 
  (fn [v _w]
    (:type (meta v))))

(defmulti transpose
  (fn [M]
    (:type (meta M))))

(defmulti mmul
  (fn [M _v]
    (:type (meta M))))

(defmulti mul
  (fn [v _s]
    (if (number? v)
      :default
      (:type (meta v)))))

(defmulti sum
  (fn [v]
    (:type (meta v))))

(defmulti add
  "Adds a scalar to each element of a vector or a matrix"
  (fn [v _s]
    (:type (meta v))))

(defmulti madd
  "Adds element-wise. Works for scalars, vectors, matrices.
  Requires the arguments to be of the same shapes."
  (fn [& As]
    (if (= (count As) 1)
      :default-1
      (if (number? (first As))
        :default-2
        (:type (meta (first As)))))))

(defmulti emap 
  "Applies f to 1, 2 or 3 As
  f must accept this amount of args."
  (fn [_f & As] 
    (:type (meta (first As)))))

(defmulti sub
  "Subtracts element wise. Works for scalars, vectors, matrices."
  (fn [& As] 
    (if (= (count As) 1)
      :default
      (:type (meta (first As))))))

(defmethod to-clj :default
  [s] s)

(defmethod mul :default
  [v s] (* v s))

(defmethod madd :default-1
  [A] A)

(defmethod madd :default-2
  [& As] (apply + As))

(defmethod sub :default
  [v w] (- v w))
