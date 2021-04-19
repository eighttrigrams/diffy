(ns diffy.activation-test
  (:require [clojure.test :refer [use-fixtures deftest is]]
            [diffy.activation :refer [relu]]
            [diffy.matrix.clojure-matrix :as cm]
            [diffy.matrix.clojure-core-matrix :as ccm]))

(defonce matrix-impl (atom nil))

(use-fixtures :each
  (fn [test]
    (reset! matrix-impl cm/matrix)
    (test)
    (reset! matrix-impl ccm/matrix)
    (test)))

(deftest test-relu
  (let [activate      (relu
                       (fn [X]
                         [X
                          (fn [E] (is (= [2.0 0.0] E)))]))
        [O, backward] (activate (@matrix-impl [1.0 -1.0]))
        _E             (backward [2.0 2.0])]
    (is (= [1.0 0.0] O))))
