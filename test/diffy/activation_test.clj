(ns diffy.activation-test
  (:require [clojure.test :refer :all]
            [diffy.activation :refer :all]
            [diffy.matrix.matrix :refer :all]
            [diffy.matrix.clojure-matrix
             :refer  [impl]
             :rename {impl clojure-matrix-impl}]
            [clojure.core.matrix :as ccm]
            [diffy.matrix.clojure-core-matrix
             :refer  [impl]
             :rename {impl clojure-core-matrix-impl}]))

(use-fixtures :once
              (fn [all-tests]
                (ccm/set-current-implementation :vectorz)
                (all-tests)))
(use-fixtures :each
              (fn [test]
                (choose-impl! clojure-matrix-impl)
                (test)
                (choose-impl! clojure-core-matrix-impl)
                (test)))

;; TODO remove duplication
;(def comparison-precision 1.0E-6)
;(defn- compare-predictions [preds1 preds2 precision]
;  (every? #(< (Math/abs %) precision) (mapv #(- %1 %2) (to-clj preds1) preds2)))

(deftest test-relu
  (let [activate      (relu
                       ;; TODO review def usage
                       (def terminate
                         (fn [X]
                           [X
                            (fn [E] (is (= [2.0 0.0] E)))])))
        [O, backward] (activate [1.0 -1.0])
        E             (backward [2.0 2.0])]
    (is (= [1.0 0.0] O))))
